package alien4cloud.model.components;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import alien4cloud.json.deserializer.BoundDeserializer;
import alien4cloud.json.serializer.BoundSerializer;
import alien4cloud.ui.form.annotation.FormProperties;
import alien4cloud.ui.form.annotation.FormSuggestion;

/**
 * Specifies the capabilities that the Node Type exposes.
 */
@FormProperties({ "type", "lowerBound", "upperBound" })
public class CapabilityDefinition {
    private String id;
    private String description;
    /** Identifies the type of the capability. */
    @FormSuggestion(fromClass = IndexedCapabilityType.class, path = "elementId")
    private String type;

    /**
     * Specifies the upper boundary of client requirements the defined capability can serve. The default value for this attribute is one. A value of
     * 'unbounded' indicates that there is no upper boundary.
     */
    @JsonDeserialize(using = BoundDeserializer.class)
    @JsonSerialize(using = BoundSerializer.class)
    private int upperBound = Integer.MAX_VALUE;

    /** Map of properties value(s) to define the capability. */
    private Map<String, List<String>> properties;

    public CapabilityDefinition() {}

    public CapabilityDefinition(String id, String type, int upperBound) {
        this.id = id;
        this.type = type;
        this.upperBound = upperBound;
    }

    public CapabilityDefinition(String id, String description, String type, int upperBound, Map<String, List<String>> properties) {
        this.id = id;
        this.description = description;
        this.type = type;
        this.upperBound = upperBound;
        this.properties = properties;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    public Map<String, List<String>> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, List<String>> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CapabilityDefinition)) return false;

        CapabilityDefinition that = (CapabilityDefinition) o;

        if (upperBound != that.upperBound) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (properties != null ? !properties.equals(that.properties) : that.properties != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + upperBound;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }
}
