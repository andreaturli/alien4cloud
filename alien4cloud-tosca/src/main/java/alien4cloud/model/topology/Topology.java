package alien4cloud.model.topology;

import java.util.Map;
import java.util.Set;

import org.elasticsearch.annotation.ESObject;
import org.elasticsearch.annotation.Id;
import org.elasticsearch.annotation.NestedObject;
import org.elasticsearch.annotation.query.TermFilter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Sets;

import alien4cloud.model.components.CSARDependency;
import alien4cloud.model.components.PropertyDefinition;
import alien4cloud.security.IManagedSecuredResource;
import alien4cloud.utils.jackson.ConditionalAttributes;
import alien4cloud.utils.jackson.ConditionalOnAttribute;
import alien4cloud.utils.jackson.JSonMapEntryArrayDeSerializer;
import alien4cloud.utils.jackson.JSonMapEntryArraySerializer;

@ESObject
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("PMD.UnusedPrivateField")
public class Topology implements IManagedSecuredResource {
    @Id
    private String id;

    /** Id of the application or topology template. */
    private String delegateId;
    /** Type of the delegate (application or topology template) */
    private String delegateType;

    /** The list of dependencies of this topology. */
    @TermFilter(paths = { "name", "version" })
    @NestedObject(nestedClass = CSARDependency.class)
    private Set<CSARDependency> dependencies = Sets.newHashSet();

    @TermFilter(paths = "value.type")
    @ConditionalOnAttribute(ConditionalAttributes.ES)
    @JsonDeserialize(using = JSonMapEntryArrayDeSerializer.class)
    @JsonSerialize(using = JSonMapEntryArraySerializer.class)
    private Map<String, NodeTemplate> nodeTemplates;

    /**
     * Scaling policies are defined by node (key is the node name).
     */
    private Map<String, ScalingPolicy> scalingPolicies;
    private Map<String, PropertyDefinition> inputs;

    /**
     * Outputs coming from node properties:
     * <ul>
     * <li>key is the node template name.
     * <li>value is a list of node template property names.
     * </ul>
     */
    private Map<String, Set<String>> outputProperties;

    /**
     * Outputs coming from node template capability properties:
     * <ul>
     * <li>key is the node template name.
     * <li>key is the capability name.
     * <li>value is a list of output property names.
     * </ul>
     */
    private Map<String, Map<String, Set<String>>> outputCapabilityProperties;

    /**
     * Outputs coming from node attributes:
     * <ul>
     * <li>key is the node template name.
     * <li>value is a list of node template attribute names.
     * </ul>
     */
    private Map<String, Set<String>> outputAttributes;
    private Map<String, Set<String>> inputArtifacts;

    private Map<String, NodeGroup> groups;

    public Topology() {
    }

    public Topology(String id, String delegateId, String delegateType, Set<CSARDependency> dependencies, Map<String, NodeTemplate> nodeTemplates, Map<String, ScalingPolicy> scalingPolicies, Map<String, PropertyDefinition> inputs, Map<String, Set<String>> outputProperties, Map<String, Map<String, Set<String>>> outputCapabilityProperties, Map<String, Set<String>> outputAttributes, Map<String, Set<String>> inputArtifacts, Map<String, NodeGroup> groups) {
        this.id = id;
        this.delegateId = delegateId;
        this.delegateType = delegateType;
        this.dependencies = dependencies;
        this.nodeTemplates = nodeTemplates;
        this.scalingPolicies = scalingPolicies;
        this.inputs = inputs;
        this.outputProperties = outputProperties;
        this.outputCapabilityProperties = outputCapabilityProperties;
        this.outputAttributes = outputAttributes;
        this.inputArtifacts = inputArtifacts;
        this.groups = groups;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getDelegateId() {
        return delegateId;
    }

    public void setDelegateId(String delegateId) {
        this.delegateId = delegateId;
    }

    @Override
    public String getDelegateType() {
        return delegateType;
    }

    public void setDelegateType(String delegateType) {
        this.delegateType = delegateType;
    }

    public Set<CSARDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<CSARDependency> dependencies) {
        this.dependencies = dependencies;
    }

    public Map<String, NodeTemplate> getNodeTemplates() {
        return nodeTemplates;
    }

    public void setNodeTemplates(Map<String, NodeTemplate> nodeTemplates) {
        this.nodeTemplates = nodeTemplates;
    }

    public Map<String, ScalingPolicy> getScalingPolicies() {
        return scalingPolicies;
    }

    public void setScalingPolicies(Map<String, ScalingPolicy> scalingPolicies) {
        this.scalingPolicies = scalingPolicies;
    }

    public Map<String, PropertyDefinition> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, PropertyDefinition> inputs) {
        this.inputs = inputs;
    }

    public Map<String, Set<String>> getOutputProperties() {
        return outputProperties;
    }

    public void setOutputProperties(Map<String, Set<String>> outputProperties) {
        this.outputProperties = outputProperties;
    }

    public Map<String, Map<String, Set<String>>> getOutputCapabilityProperties() {
        return outputCapabilityProperties;
    }

    public void setOutputCapabilityProperties(Map<String, Map<String, Set<String>>> outputCapabilityProperties) {
        this.outputCapabilityProperties = outputCapabilityProperties;
    }

    public Map<String, Set<String>> getOutputAttributes() {
        return outputAttributes;
    }

    public void setOutputAttributes(Map<String, Set<String>> outputAttributes) {
        this.outputAttributes = outputAttributes;
    }

    public Map<String, Set<String>> getInputArtifacts() {
        return inputArtifacts;
    }

    public void setInputArtifacts(Map<String, Set<String>> inputArtifacts) {
        this.inputArtifacts = inputArtifacts;
    }

    public Map<String, NodeGroup> getGroups() {
        return groups;
    }

    public void setGroups(Map<String, NodeGroup> groups) {
        this.groups = groups;
    }
}
