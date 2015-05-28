package alien4cloud.topology;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.mapping.FilterValuesStrategy;
import org.springframework.stereotype.Service;

import alien4cloud.application.ApplicationService;
import alien4cloud.application.ApplicationVersionService;
import alien4cloud.component.CSARRepositorySearchService;
import alien4cloud.csar.services.CsarService;
import alien4cloud.dao.IGenericSearchDAO;
import alien4cloud.dao.model.GetMultipleDataResult;
import alien4cloud.exception.NotFoundException;
import alien4cloud.exception.VersionConflictException;
import alien4cloud.model.application.Application;
import alien4cloud.model.application.ApplicationVersion;
import alien4cloud.model.components.CSARDependency;
import alien4cloud.model.components.CapabilityDefinition;
import alien4cloud.model.components.IndexedCapabilityType;
import alien4cloud.model.components.IndexedNodeType;
import alien4cloud.model.components.IndexedRelationshipType;
import alien4cloud.model.components.IndexedToscaElement;
import alien4cloud.model.templates.TopologyTemplate;
import alien4cloud.model.topology.NodeTemplate;
import alien4cloud.model.topology.RelationshipTemplate;
import alien4cloud.model.topology.Topology;
import alien4cloud.rest.utils.JsonUtil;
import alien4cloud.security.model.ApplicationRole;
import alien4cloud.security.AuthorizationUtil;
import alien4cloud.security.model.Role;
import alien4cloud.security.model.User;
import alien4cloud.topology.exception.UpdateTopologyException;
import alien4cloud.topology.task.SuggestionsTask;
import alien4cloud.topology.task.TaskCode;
import alien4cloud.tosca.container.ToscaTypeLoader;
import alien4cloud.tosca.serializer.VelocityUtil;
import alien4cloud.utils.MapUtil;
import alien4cloud.utils.version.VersionUtil;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@Slf4j
@Service
public class TopologyService {

    @Resource
    private CSARRepositorySearchService csarRepoSearchService;

    @Resource(name = "alien-es-dao")
    private IGenericSearchDAO alienDAO;

    @Resource
    private ApplicationService appService;

    @Resource
    private CsarService csarService;

    @Resource
    private TopologyServiceCore topologyServiceCore;

    @Resource
    private ApplicationVersionService applicationVersionService;

    private ToscaTypeLoader initializeTypeLoader(Topology topology) {
        ToscaTypeLoader loader = new ToscaTypeLoader(csarService);
        Map<String, IndexedNodeType> nodeTypes = topologyServiceCore.getIndexedNodeTypesFromTopology(topology, false, false);
        Map<String, IndexedRelationshipType> relationshipTypes = topologyServiceCore.getIndexedRelationshipTypesFromTopology(topology);
        if (topology.getNodeTemplates() != null) {
            for (NodeTemplate nodeTemplate : topology.getNodeTemplates().values()) {
                IndexedNodeType nodeType = nodeTypes.get(nodeTemplate.getType());
                loader.loadType(nodeTemplate.getType(), new CSARDependency(nodeType.getArchiveName(), nodeType.getArchiveVersion()));
                if (nodeTemplate.getRelationships() != null) {
                    for (RelationshipTemplate relationshipTemplate : nodeTemplate.getRelationships().values()) {
                        IndexedRelationshipType relationshipType = relationshipTypes.get(relationshipTemplate.getType());
                        loader.loadType(relationshipTemplate.getType(),
                                new CSARDependency(relationshipType.getArchiveName(), relationshipType.getArchiveVersion()));
                    }
                }
            }
        }
        return loader;
    }

    /**
     * Get a map of all capability types defined in the given node types.
     *
     * @param nodeTypes The collection of node types for which to get capabilities.
     * @param dependencies The dependencies in which to look for capabilities.
     * @return A map of capability types defined in the given node types.
     */
    public Map<String, IndexedCapabilityType> getIndexedCapabilityTypes(Collection<IndexedNodeType> nodeTypes, Collection<CSARDependency> dependencies) {
        Map<String, IndexedCapabilityType> capabilityTypes = Maps.newHashMap();
        for (IndexedNodeType nodeType : nodeTypes) {
            if (nodeType.getCapabilities() != null) {
                for (CapabilityDefinition capabilityDefinition : nodeType.getCapabilities()) {
                    IndexedCapabilityType capabilityType = csarRepoSearchService.getRequiredElementInDependencies(IndexedCapabilityType.class,
                            capabilityDefinition.getType(), dependencies);
                    capabilityTypes.put(capabilityDefinition.getType(), capabilityType);
                }
            }
        }
        return capabilityTypes;
    }

    /**
     * Find replacements nodes for a node template
     *
     * @param nodeTemplateName the node to search for replacements
     * @param topology the topology
     * @return all possible replacement types for this node
     */
    @SneakyThrows(IOException.class)
    public IndexedNodeType[] findReplacementForNode(String nodeTemplateName, Topology topology) {
        NodeTemplate nodeTemplate = topology.getNodeTemplates().get(nodeTemplateName);
        Map<String, Map<String, Set<String>>> nodeTemplatesToFilters = Maps.newHashMap();
        Entry<String, NodeTemplate> nodeTempEntry = Maps.immutableEntry(nodeTemplateName, nodeTemplate);
        IndexedNodeType indexedNodeType = csarRepoSearchService.getRequiredElementInDependencies(IndexedNodeType.class, nodeTemplate.getType(),
                topology.getDependencies());
        processNodeTemplate(topology, nodeTempEntry, nodeTemplatesToFilters);
        List<SuggestionsTask> topoTasks = searchForNodeTypes(nodeTemplatesToFilters,
                MapUtil.newHashMap(new String[] { nodeTemplateName }, new IndexedNodeType[] { indexedNodeType }));

        if (CollectionUtils.isEmpty(topoTasks)) {
            return null;
        }
        return topoTasks.get(0).getSuggestedNodeTypes();
    }

    private void addFilters(String nodeTempName, String filterKey, String filterValueToAdd, Map<String, Map<String, Set<String>>> nodeTemplatesToFilters) {
        Map<String, Set<String>> filters = nodeTemplatesToFilters.get(nodeTempName);
        if (filters == null) {
            filters = Maps.newHashMap();
        }
        Set<String> filterValues = filters.get(filterKey);
        if (filterValues == null) {
            filterValues = Sets.newHashSet();
        }

        filterValues.add(filterValueToAdd);
        filters.put(filterKey, filterValues);
        nodeTemplatesToFilters.put(nodeTempName, filters);
    }

    /**
     * Process a node template to retrieve filters for node replacements search
     */
    void processNodeTemplate(final Topology topology, final Entry<String, NodeTemplate> nodeTempEntry,
            Map<String, Map<String, Set<String>>> nodeTemplatesToFilters) {
        String capabilityFilterKey = "capabilities.type";
        String requirementFilterKey = "requirements.type";
        NodeTemplate template = nodeTempEntry.getValue();
        Map<String, RelationshipTemplate> relTemplates = template.getRelationships();

        nodeTemplatesToFilters.put(nodeTempEntry.getKey(), null);

        // process the node template source of relationships
        if (relTemplates != null && !relTemplates.isEmpty()) {
            for (RelationshipTemplate relationshipTemplate : relTemplates.values()) {
                addFilters(nodeTempEntry.getKey(), requirementFilterKey, relationshipTemplate.getRequirementType(), nodeTemplatesToFilters);
            }
        }

        // process the node template target of relationships
        List<RelationshipTemplate> relTemplatesTargetRelated = topologyServiceCore.getTargetRelatedRelatonshipsTemplate(nodeTempEntry.getKey(),
                topology.getNodeTemplates());
        for (RelationshipTemplate relationshipTemplate : relTemplatesTargetRelated) {
            addFilters(nodeTempEntry.getKey(), capabilityFilterKey, relationshipTemplate.getRequirementType(), nodeTemplatesToFilters);
        }

    }

    private IndexedNodeType[] getIndexedNodeTypesFromSearchResponse(final GetMultipleDataResult searchResult, final IndexedNodeType toExcludeIndexedNodeType)
            throws IOException {
        IndexedNodeType[] toReturnArray = null;
        for (int j = 0; j < searchResult.getData().length; j++) {
            IndexedNodeType nodeType = JsonUtil.readObject(JsonUtil.toString(searchResult.getData()[j]), IndexedNodeType.class);
            if (toExcludeIndexedNodeType == null || !nodeType.getId().equals(toExcludeIndexedNodeType.getId())) {
                toReturnArray = ArrayUtils.add(toReturnArray, nodeType);
            }
        }
        return toReturnArray;
    }

    /**
     * Search for nodeTypes given some filters. Apply AND filter strategy when multiple values for a filter key.
     */
    List<SuggestionsTask> searchForNodeTypes(Map<String, Map<String, Set<String>>> nodeTemplatesToFilters,
            Map<String, IndexedNodeType> toExcludeIndexedNodeTypes) throws IOException {
        if (nodeTemplatesToFilters == null || nodeTemplatesToFilters.isEmpty()) {
            return null;
        }
        List<SuggestionsTask> toReturnTasks = Lists.newArrayList();
        for (Map.Entry<String, Map<String, Set<String>>> nodeTemplatesToFiltersEntry : nodeTemplatesToFilters.entrySet()) {
            Map<String, String[]> formattedFilters = Maps.newHashMap();
            Map<String, FilterValuesStrategy> filterValueStrategy = Maps.newHashMap();
            IndexedNodeType[] data = null;
            if (nodeTemplatesToFiltersEntry.getValue() != null) {
                for (Map.Entry<String, Set<String>> filterEntry : nodeTemplatesToFiltersEntry.getValue().entrySet()) {
                    formattedFilters.put(filterEntry.getKey(), filterEntry.getValue().toArray(new String[filterEntry.getValue().size()]));
                    // AND strategy if multiple values
                    filterValueStrategy.put(filterEntry.getKey(), FilterValuesStrategy.AND);
                }

                // retrieve only non abstract components
                formattedFilters.put("abstract", ArrayUtils.toArray("false"));

                GetMultipleDataResult searchResult = alienDAO.search(IndexedNodeType.class, null, formattedFilters, filterValueStrategy, 20);
                data = getIndexedNodeTypesFromSearchResponse(searchResult, toExcludeIndexedNodeTypes.get(nodeTemplatesToFiltersEntry.getKey()));
            }
            TaskCode taskCode = data == null || data.length < 1 ? TaskCode.IMPLEMENT : TaskCode.REPLACE;
            SuggestionsTask task = new SuggestionsTask();
            task.setNodeTemplateName(nodeTemplatesToFiltersEntry.getKey());
            task.setComponent(toExcludeIndexedNodeTypes.get(nodeTemplatesToFiltersEntry.getKey()));
            task.setCode(taskCode);
            task.setSuggestedNodeTypes(data);
            toReturnTasks.add(task);
        }

        return toReturnTasks;
    }

    /**
     * Check that the user has enough rights for a given topology.
     *
     * @param topology The topology for which to check roles.
     * @param applicationRoles The roles required to edit the topology for an application.
     */
    public void checkAuthorizations(Topology topology, ApplicationRole... applicationRoles) {
        if (topology.getDelegateType().equals(Application.class.getSimpleName().toLowerCase())) {
            String applicationId = topology.getDelegateId();
            Application application = appService.getOrFail(applicationId);
            AuthorizationUtil.checkAuthorizationForApplication(application, applicationRoles);
        } else {
            AuthorizationUtil.checkHasOneRoleIn(Role.ARCHITECT);
        }
    }

    /**
     * Check that the current user can update the given topology.
     *
     * @param topology The topology that is subject to being updated.
     */
    public void checkEditionAuthorizations(Topology topology) {
        checkAuthorizations(topology, ApplicationRole.APPLICATION_MANAGER, ApplicationRole.APPLICATION_DEVOPS);
    }

    private String toLowerCase(String text) {
        return text.substring(0, 1).toLowerCase() + text.substring(1);
    }

    private String toUpperCase(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    /**
     * Construct a relationship name from target and relationship type.
     *
     * @param type type of the relationship
     * @param targetName name of the target
     * @return the default constructed name
     */
    public String getRelationShipName(String type, String targetName) {
        String[] tokens = type.split("\\.");
        if (tokens.length > 1) {
            return toLowerCase(tokens[tokens.length - 1]) + toUpperCase(targetName);
        } else {
            return toLowerCase(type) + toUpperCase(targetName);
        }
    }

    /**
     * Create a {@link TopologyDTO} from a topology by fetching node types, relationship types and capability types used in the topology.
     *
     * @param topology The topology for which to create a DTO.
     * @return The {@link TopologyDTO} that contains the given topology
     */
    public TopologyDTO buildTopologyDTO(Topology topology) {
        Map<String, IndexedNodeType> nodeTypes = topologyServiceCore.getIndexedNodeTypesFromTopology(topology, false, false);
        Map<String, IndexedRelationshipType> relationshipTypes = topologyServiceCore.getIndexedRelationshipTypesFromTopology(topology);
        Map<String, IndexedCapabilityType> capabilityTypes = getIndexedCapabilityTypes(nodeTypes.values(), topology.getDependencies());
        String yaml = getYaml(topology);
        Map<String, Map<String, Set<String>>> outputCapabilityProperties = topology.getOutputCapabilityProperties();
        return new TopologyDTO(topology, nodeTypes, relationshipTypes, capabilityTypes, outputCapabilityProperties, yaml);
    }

    /**
     * Build a node template
     *
     * @param dependencies the dependencies on which new node will be constructed
     * @param indexedNodeType the type of the node
     * @param templateToMerge the template that can be used to merge into the new node template
     * @return new constructed node template
     */
    public NodeTemplate buildNodeTemplate(Set<CSARDependency> dependencies, IndexedNodeType indexedNodeType, NodeTemplate templateToMerge) {
        return topologyServiceCore.buildNodeTemplate(dependencies, indexedNodeType, templateToMerge);
    }

    private CSARDependency getDependencyWithName(Topology topology, String archiveName) {
        for (CSARDependency dependency : topology.getDependencies()) {
            if (dependency.getName().equals(archiveName)) {
                return dependency;
            }
        }
        return null;
    }

    /**
     * Load a type into the topology (add dependency for this new type, upgrade if necessary ...)
     *
     * @param topology the topology
     * @param element the element to load
     * @param <T> tosca element type
     * @return The real loaded element if element given in argument is from older archive than topology's dependencies
     */
    @SuppressWarnings("unchecked")
    public <T extends IndexedToscaElement> T loadType(Topology topology, T element) {
        String type = element.getElementId();
        String archiveName = element.getArchiveName();
        String archiveVersion = element.getArchiveVersion();
        CSARDependency newDependency = new CSARDependency(archiveName, archiveVersion);
        CSARDependency topologyDependency = getDependencyWithName(topology, archiveName);
        if (topologyDependency != null) {
            int comparisonResult = VersionUtil.compare(newDependency.getVersion(), topologyDependency.getVersion());
            if (comparisonResult > 0) {
                // Dependency of the type is more recent, try to upgrade the topology
                topology.getDependencies().add(newDependency);
                topology.getDependencies().remove(topologyDependency);
                Map<String, IndexedNodeType> nodeTypes;
                try {
                    nodeTypes = topologyServiceCore.getIndexedNodeTypesFromTopology(topology, false, false);
                    topologyServiceCore.getIndexedRelationshipTypesFromTopology(topology);
                } catch (NotFoundException e) {
                    throw new VersionConflictException("Version conflict, cannot add archive [" + archiveName + ":" + archiveVersion
                            + "], upgrade of the topology to this archive from version [" + topologyDependency.getVersion() + "] failed", e);
                }
                // Try to upgrade existing nodes
                Map<String, NodeTemplate> newNodeTemplates = Maps.newHashMap();
                Map<String, NodeTemplate> existingNodeTemplates = topology.getNodeTemplates();
                if (existingNodeTemplates != null) {
                    for (Entry<String, NodeTemplate> nodeTemplateEntry : existingNodeTemplates.entrySet()) {
                        NodeTemplate newNodeTemplate = buildNodeTemplate(topology.getDependencies(), nodeTypes.get(nodeTemplateEntry.getValue().getType()),
                                nodeTemplateEntry.getValue());
                        newNodeTemplates.put(nodeTemplateEntry.getKey(), newNodeTemplate);
                    }
                    topology.setNodeTemplates(newNodeTemplates);
                }
            } else if (comparisonResult < 0) {
                // Dependency of the topology is more recent, try to upgrade the dependency of the type
                element = csarRepoSearchService.getElementInDependencies((Class<T>) element.getClass(), element.getElementId(), topology.getDependencies());
            }
        }
        ToscaTypeLoader typeLoader = initializeTypeLoader(topology);
        typeLoader.loadType(type, new CSARDependency(element.getArchiveName(), element.getArchiveVersion()));
        topology.setDependencies(typeLoader.getLoadedDependencies());
        return element;
    }

    public void unloadType(Topology topology, String... types) {
        ToscaTypeLoader typeLoader = initializeTypeLoader(topology);
        for (String type : types) {
            typeLoader.unloadType(type);
        }
        topology.setDependencies(typeLoader.getLoadedDependencies());
    }

    /**
     * Throw an UpdateTopologyException if the topology is released
     *
     * @param topology topology to be checked
     */
    public void throwsErrorIfReleased(Topology topology) {
        if (isReleased(topology)) {
            throw new UpdateTopologyException("The topology " + topology.getId() + " cannot be updated because it's released");
        }
    }

    /**
     * True when an topology is released
     *
     * @param topology topology to be checked
     * @return true if the environment is currently deployed
     */
    public boolean isReleased(Topology topology) {
        ApplicationVersion appVersion = getApplicationVersion(topology);
        return !(appVersion == null || !appVersion.isReleased());
    }

    /**
     * Get the released application version of a topology
     *
     * @param topology the topology
     * @return The application version associated with the environment.
     */
    private ApplicationVersion getApplicationVersion(Topology topology) {
        GetMultipleDataResult<ApplicationVersion> dataResult = alienDAO.search(
                ApplicationVersion.class,
                null,
                MapUtil.newHashMap(new String[] { "applicationId", "topologyId" }, new String[][] { new String[] { topology.getDelegateId() },
                        new String[] { topology.getId() } }), 1);
        if (dataResult.getData() != null && dataResult.getData().length > 0) {
            return dataResult.getData()[0];
        }
        return null;
    }

    /**
     * Retrieve the topology template from its id
     *
     * @param topologyTemplateId the topology template's id
     * @return the required topology template
     */
    public TopologyTemplate getOrFailTopologyTemplate(String topologyTemplateId) {
        TopologyTemplate topologyTemplate = alienDAO.findById(TopologyTemplate.class, topologyTemplateId);
        if (topologyTemplate == null) {
            log.debug("Failed to recover the topology template <{}>", topologyTemplateId);
            throw new NotFoundException("Topology template with id [" + topologyTemplateId + "] cannot be found");
        }
        return topologyTemplate;
    }

    public String getYaml(Topology topology) {
        Map<String, Object> velocityCtx = new HashMap<>();
        velocityCtx.put("topology", topology);
        velocityCtx.put("template_name", "template-id");
        velocityCtx.put("template_version", "1.0.0-SNAPSHOT");
        User loggedUser = AuthorizationUtil.getCurrentUser();
        velocityCtx.put("template_author", loggedUser != null ? loggedUser.getUsername() : null);
        if (topology.getDelegateType().equals(Application.class.getSimpleName().toLowerCase())) {
            String applicationId = topology.getDelegateId();
            Application application = appService.getOrFail(applicationId);
            velocityCtx.put("template_name", application.getName());
            velocityCtx.put("application_description", application.getDescription());
            ApplicationVersion version = applicationVersionService.getByTopologyId(topology.getId());
            if (version != null) {
                velocityCtx.put("template_version", version.getVersion());
            }
        } else if (topology.getDelegateType().equals(TopologyTemplate.class.getSimpleName().toLowerCase())) {
            String topologyTemplateId = topology.getDelegateId();
            TopologyTemplate template = getOrFailTopologyTemplate(topologyTemplateId);
            velocityCtx.put("template_name", template.getName());
            velocityCtx.put("application_description", template.getDescription());
        }

        try {
            StringWriter writer = new StringWriter();
            VelocityUtil.generate("templates/topology-1_0_0_wd03.yml.vm", writer, velocityCtx);
            return writer.toString();
        } catch (Exception e) {
            log.error("Exception while templating YAML for topology " + topology.getId(), e);
            return ExceptionUtils.getFullStackTrace(e);
        }

    }

}
