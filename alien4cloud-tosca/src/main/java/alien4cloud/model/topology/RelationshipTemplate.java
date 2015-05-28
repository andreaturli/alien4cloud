package alien4cloud.model.topology;

/**
 * <p>
 * Specify a relationship between the components of the cloud application.
 * </p>
 * <p>
 * For each specified Relationship Template the source element and target element MUST be specified in the Topology Template.
 * </p>
 *
 * @author luc boutier
 */
public class RelationshipTemplate extends AbstractTemplate {
    /**
     * <p>
     * This element specifies the target of the relationship represented by the current Relationship Template
     * </p>
     * <p>
     * This attribute references by ID a Node Template or a Capability of a Node Template within the same Service Template document that is the target of the
     * Relationship Template.
     * </p>
     * <p>
     * If the Relationship Type referenced by the type attribute defines a constraint on the valid source of the relationship by means of its ValidTarget
     * element, the ref attribute of TargetElement MUST reference an object the type of which complies with the valid source constraint of the respective
     * Relationship Type.
     * </p>
     * <p>
     * In case a Node Type is defined as valid target in the Relationship Type definition, the ref attribute MUST reference a Node Template of the corresponding
     * Node Type (or of a sub-type).
     * </p>
     * <p>
     * In case a Capability Type is defined a valid target in the Relationship Type definition, the ref attribute MUST reference a Capability of the
     * corresponding Capability Type within a Node Template.
     * </p>
     */
    private String target;

    private String requirementName;
    private String requirementType;

    private String targetedCapabilityName;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getRequirementName() {
        return requirementName;
    }

    public void setRequirementName(String requirementName) {
        this.requirementName = requirementName;
    }

    public String getRequirementType() {
        return requirementType;
    }

    public void setRequirementType(String requirementType) {
        this.requirementType = requirementType;
    }

    public String getTargetedCapabilityName() {
        return targetedCapabilityName;
    }

    public void setTargetedCapabilityName(String targetedCapabilityName) {
        this.targetedCapabilityName = targetedCapabilityName;
    }
}
