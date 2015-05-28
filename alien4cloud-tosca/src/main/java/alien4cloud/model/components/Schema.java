package alien4cloud.model.components;

import java.util.List;
import java.util.Map;

/**
 * Schema allows to create new types that can be used along TOSCA definitions.
 */
public class Schema {
    private String derivedFrom;
    private List<PropertyConstraint> constraints;
    private Map<String, PropertyDefinition> properties;

    public String getDerivedFrom() {
        return derivedFrom;
    }

    public void setDerivedFrom(String derivedFrom) {
        this.derivedFrom = derivedFrom;
    }

    public List<PropertyConstraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<PropertyConstraint> constraints) {
        this.constraints = constraints;
    }

    public Map<String, PropertyDefinition> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, PropertyDefinition> properties) {
        this.properties = properties;
    }
}
