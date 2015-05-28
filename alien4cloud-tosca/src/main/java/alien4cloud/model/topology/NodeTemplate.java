package alien4cloud.model.topology;

import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import alien4cloud.model.components.AbstractPropertyValue;
import alien4cloud.model.components.DeploymentArtifact;
import alien4cloud.utils.jackson.ConditionalAttributes;
import alien4cloud.utils.jackson.ConditionalOnAttribute;
import alien4cloud.utils.jackson.JSonMapEntryArrayDeSerializer;
import alien4cloud.utils.jackson.JSonMapEntryArraySerializer;

/**
 * Specifies a kind of a component making up the cloud application.
 *
 * @author luc boutier
 */
public class NodeTemplate extends AbstractTemplate {
    /**
     * Id in the map is name.replaceAll(" ", "").toLowerCase();
     */
    private String name;

    /**
     * Attributes of the node template
     */
    @ConditionalOnAttribute(ConditionalAttributes.REST)
    @JsonDeserialize(using = JSonMapEntryArrayDeSerializer.class)
    @JsonSerialize(using = JSonMapEntryArraySerializer.class)
    private Map<String, String> attributes;

    /**
     * The requirement that this node template defines
     */
    @ConditionalOnAttribute(ConditionalAttributes.REST)
    @JsonDeserialize(using = JSonMapEntryArrayDeSerializer.class)
    @JsonSerialize(using = JSonMapEntryArraySerializer.class)
    private Map<String, Requirement> requirements;

    /**
     * Relationships between node templates
     */
    @ConditionalOnAttribute(ConditionalAttributes.REST)
    @JsonDeserialize(using = JSonMapEntryArrayDeSerializer.class)
    @JsonSerialize(using = JSonMapEntryArraySerializer.class)
    private Map<String, RelationshipTemplate> relationships;

    /**
     * The capabilities that this node template defines
     */
    @ConditionalOnAttribute(ConditionalAttributes.REST)
    @JsonDeserialize(using = JSonMapEntryArrayDeSerializer.class)
    @JsonSerialize(using = JSonMapEntryArraySerializer.class)
    private Map<String, Capability> capabilities;

    /**
     * The {@link NodeGroup}s this template is member of.
     */
    private Set<String> groups;

    public NodeTemplate(String type, Map<String, AbstractPropertyValue> properties, Map<String, String> attributes,
            Map<String, RelationshipTemplate> relationships, Map<String, Requirement> requirements, Map<String, Capability> capabilities,
            Map<String, DeploymentArtifact> artifacts) {
        this.setType(type);
        this.setProperties(properties);
        this.setArtifacts(artifacts);
        this.attributes = attributes;
        this.relationships = relationships;
        this.requirements = requirements;
        this.capabilities = capabilities;
    }

    public static final boolean isValidNodeTemplateName(String nodeTemplateName) {
        String pattern = "^[A-Za-z0-9\\\\-]*$";
        if (nodeTemplateName != null && !nodeTemplateName.matches(pattern)) {
            return false;
        }
        return true;
    }

    public NodeTemplate() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(Map<String, Requirement> requirements) {
        this.requirements = requirements;
    }

    public Map<String, RelationshipTemplate> getRelationships() {
        return relationships;
    }

    public void setRelationships(Map<String, RelationshipTemplate> relationships) {
        this.relationships = relationships;
    }

    public Map<String, Capability> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Map<String, Capability> capabilities) {
        this.capabilities = capabilities;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }
}
