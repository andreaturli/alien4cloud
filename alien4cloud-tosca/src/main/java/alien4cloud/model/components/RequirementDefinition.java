package alien4cloud.model.components;

import java.util.Map;

import alien4cloud.json.deserializer.BoundDeserializer;
import alien4cloud.json.serializer.BoundSerializer;
import alien4cloud.ui.form.annotation.FormProperties;
import alien4cloud.ui.form.annotation.FormSuggestion;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Specifies the requirements that the Node Type exposes.
 */
@FormProperties({ "type", "lowerBound", "upperBound" })
public class RequirementDefinition {
    private String id;
    /**
     * <p>
     * Identifies the type of the requirement.
     * </p>
     * <p>
     * This must be a qualified name: Either namespace:type, either type only if the {@link capability type} is defined in the same namespace as the
     * {@link RequirementDefinition definition}.
     * </p>
     */
    @FormSuggestion(fromClass = IndexedCapabilityType.class, path = "elementId")
    private String type;
    /** Specifies the default relationship type to be used for the relationship. This can be overriden by user but should be used as default. */
    private String relationshipType;
    /** Can specify the optional target capability name on which to bind the relationship. */
    private String capabilityName;
    /** Constraints to specify on the target capability or node's properties. */
    private Map<String, PropertyConstraint> constraints;
    /**
     * Specifies the lower boundary by which a requirement MUST be matched for Node Templates according to the current Node Type, or for instances created for
     * those Node Templates. The default value for this attribute is one. A value of zero would indicate that matching of the requirement is optional.
     */
    private int lowerBound = 1;
    /**
     * Specifies the upper boundary by which a requirement MUST be matched for Node Templates according to the current Node Type, or for instances created for
     * those Node Templates. The default value for this attribute is one. A value of “unbounded�? indicates that there is no upper boundary.
     */
    @JsonDeserialize(using = BoundDeserializer.class)
    @JsonSerialize(using = BoundSerializer.class)
    private int upperBound = 1;

    /**
     * Quick constructor to create a requirement definition from id and type.
     *
     * @param id The requirement id.
     * @param type The requirement type.
     */
    public RequirementDefinition(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public RequirementDefinition() {}

    public RequirementDefinition(String id, String type, String relationshipType, String capabilityName, Map<String, PropertyConstraint> constraints, int lowerBound, int upperBound) {
        this.id = id;
        this.type = type;
        this.relationshipType = relationshipType;
        this.capabilityName = capabilityName;
        this.constraints = constraints;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getCapabilityName() {
        return capabilityName;
    }

    public void setCapabilityName(String capabilityName) {
        this.capabilityName = capabilityName;
    }

    public Map<String, PropertyConstraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(Map<String, PropertyConstraint> constraints) {
        this.constraints = constraints;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequirementDefinition)) return false;

        RequirementDefinition that = (RequirementDefinition) o;

        if (lowerBound != that.lowerBound) return false;
        if (upperBound != that.upperBound) return false;
        if (capabilityName != null ? !capabilityName.equals(that.capabilityName) : that.capabilityName != null)
            return false;
        if (constraints != null ? !constraints.equals(that.constraints) : that.constraints != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (relationshipType != null ? !relationshipType.equals(that.relationshipType) : that.relationshipType != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (relationshipType != null ? relationshipType.hashCode() : 0);
        result = 31 * result + (capabilityName != null ? capabilityName.hashCode() : 0);
        result = 31 * result + (constraints != null ? constraints.hashCode() : 0);
        result = 31 * result + lowerBound;
        result = 31 * result + upperBound;
        return result;
    }
}
