package alien4cloud.tosca.model;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import alien4cloud.model.components.Csar;
import alien4cloud.model.components.IndexedArtifactType;
import alien4cloud.model.components.IndexedCapabilityType;
import alien4cloud.model.components.IndexedNodeType;
import alien4cloud.model.components.IndexedRelationshipType;
import alien4cloud.model.topology.Topology;

/** Root object to be de-serialized. */
public class ArchiveRoot {
    /** Contains meta-data related to the actual archive. */
    private Csar archive = new Csar();

    /** An archive can embed topology template (TOSCA meaning). */
    private Topology topology;
    /** The description of the topology template is not a property of the topology. */
    private String topologyTemplateDescription;

    private List<ArchiveRoot> localImports;

    private Map<String, IndexedNodeType> nodeTypes = Maps.newHashMap();
    private Map<String, IndexedRelationshipType> relationshipTypes = Maps.newHashMap();
    private Map<String, IndexedCapabilityType> capabilityTypes = Maps.newHashMap();
    private Map<String, IndexedArtifactType> artifactTypes = Maps.newHashMap();

    /**
     * Indicates if this archive contains tosca types (node types, relationships, capabilities, artifacts).
     */
    public boolean hasToscaTypes() {
        return !nodeTypes.isEmpty() || !relationshipTypes.isEmpty() || !capabilityTypes.isEmpty() || !artifactTypes.isEmpty();
    }

    /**
     * Indicates if this archive contains a topology template.
     */
    public boolean hasToscaTopologyTemplate() {
        return topology != null;
    }

    public Csar getArchive() {
        return archive;
    }

    public void setArchive(Csar archive) {
        this.archive = archive;
    }

    public Topology getTopology() {
        return topology;
    }

    public void setTopology(Topology topology) {
        this.topology = topology;
    }

    public String getTopologyTemplateDescription() {
        return topologyTemplateDescription;
    }

    public void setTopologyTemplateDescription(String topologyTemplateDescription) {
        this.topologyTemplateDescription = topologyTemplateDescription;
    }

    public List<ArchiveRoot> getLocalImports() {
        return localImports;
    }

    public void setLocalImports(List<ArchiveRoot> localImports) {
        this.localImports = localImports;
    }

    public Map<String, IndexedNodeType> getNodeTypes() {
        return nodeTypes;
    }

    public void setNodeTypes(Map<String, IndexedNodeType> nodeTypes) {
        this.nodeTypes = nodeTypes;
    }

    public Map<String, IndexedRelationshipType> getRelationshipTypes() {
        return relationshipTypes;
    }

    public void setRelationshipTypes(Map<String, IndexedRelationshipType> relationshipTypes) {
        this.relationshipTypes = relationshipTypes;
    }

    public Map<String, IndexedCapabilityType> getCapabilityTypes() {
        return capabilityTypes;
    }

    public void setCapabilityTypes(Map<String, IndexedCapabilityType> capabilityTypes) {
        this.capabilityTypes = capabilityTypes;
    }

    public Map<String, IndexedArtifactType> getArtifactTypes() {
        return artifactTypes;
    }

    public void setArtifactTypes(Map<String, IndexedArtifactType> artifactTypes) {
        this.artifactTypes = artifactTypes;
    }
}
