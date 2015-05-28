package alien4cloud.model.components;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import alien4cloud.json.deserializer.OperationParameterDeserializer;
import alien4cloud.ui.form.annotation.FormProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@FormProperties({ "function_concat", "parameters" })
public class ConcatPropertyValue extends AbstractPropertyValue {
    private String function_concat;
    @JsonDeserialize(contentUsing = OperationParameterDeserializer.class)
    private List<IValue> parameters;

    public String getFunction_concat() {
        return function_concat;
    }

    public void setFunction_concat(String function_concat) {
        this.function_concat = function_concat;
    }

    public List<IValue> getParameters() {
        return parameters;
    }

    public void setParameters(List<IValue> parameters) {
        this.parameters = parameters;
    }
}
