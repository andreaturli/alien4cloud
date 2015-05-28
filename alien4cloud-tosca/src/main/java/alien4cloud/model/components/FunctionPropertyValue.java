package alien4cloud.model.components;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import alien4cloud.ui.form.annotation.FormProperties;

/**
 * A TOSCA function to be used as the value for a property (or operation parameter).
 */
@FormProperties({ "function", "parameters" })
public class FunctionPropertyValue extends AbstractPropertyValue {
    private String function;

    private List<String> parameters;

    public FunctionPropertyValue() {}

    public FunctionPropertyValue(String function, List<String> parameters) {
        this.function = function;
        this.parameters = parameters;
    }

    /** Get the modelable entity's (node or relationship template) name related to the function, represented by the first parameter. */
    @JsonIgnore
    public String getTemplateName() {
        return parameters.get(0);
    }

    /** get the name of the property or attribute we want to retrieve, represented by the last parameter in the list */
    @JsonIgnore
    public String getPropertyOrAttributeName() {
        return parameters.get(parameters.size() - 1);
    }

    /** Get, if provided, the capability/requirement name within the template which contains the prop or attr to retrieve */
    @JsonIgnore
    public String getCapabilityOrRequirementName() {
        return parameters.size() > 2 ? parameters.get(1) : null;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

}
