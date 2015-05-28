package alien4cloud.model.components;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import alien4cloud.json.deserializer.OperationParameterDeserializer;
import alien4cloud.ui.form.annotation.FormProperties;

/**
 * Defines an operation available to manage particular aspects of the Node Type.
 *
 * @author luc boutier
 */
@FormProperties({ "description" })
public class Operation {
    /** Implementation artifact for the interface. */
    private ImplementationArtifact implementationArtifact;
    /** Description of the operation. */
    private String description;
    /** This OPTIONAL property contains a list of one or more input parameter definitions. */
    @JsonDeserialize(contentUsing = OperationParameterDeserializer.class)
    private Map<String, IValue> inputParameters;

    public Operation() {}

    /**
     * <p>
     * Jackson DeSerialization workaround constructor to create an operation with no arguments.
     * </p>
     *
     * @param emptyString The empty string provided by jackson.
     */
    @SuppressWarnings("PMD.UnusedFormalParameterRule")
    public Operation(String emptyString) {
    }

    public ImplementationArtifact getImplementationArtifact() {
        return implementationArtifact;
    }

    public void setImplementationArtifact(ImplementationArtifact implementationArtifact) {
        this.implementationArtifact = implementationArtifact;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, IValue> getInputParameters() {
        return inputParameters;
    }

    public void setInputParameters(Map<String, IValue> inputParameters) {
        this.inputParameters = inputParameters;
    }
}
