package alien4cloud.model.topology;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import alien4cloud.json.deserializer.PropertyValueDeserializer;
import alien4cloud.model.components.AbstractPropertyValue;

/**
 * Define the requirement for the node. Must match the Node type's definitions.
 *
 * @author luc boutier
 */
public class Requirement {
    /**
     * The QName value of this attribute refers to the Requirement Type definition of the Requirement. This Requirement Type denotes the semantics and well as
     * potential properties of the Requirement.
     */
    private String type;
    /**
     * This element specifies initial values for one or more of the Requirement Properties according to the Requirement Type providing the property definitions.
     * Properties are provided in the form of an XML fragment. The same rules as outlined for the Properties element of the Node Template apply.
     */
    @JsonDeserialize(contentUsing = PropertyValueDeserializer.class)
    private Map<String, AbstractPropertyValue> properties;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, AbstractPropertyValue> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, AbstractPropertyValue> properties) {
        this.properties = properties;
    }
}
