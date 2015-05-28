package alien4cloud.model.components;

import java.util.Map;

import com.google.common.collect.Maps;

import alien4cloud.ui.form.annotation.FormProperties;

/**
 * Definition of the operations that can be performed on (instances of) a Node Type.
 *
 * @author luc boutier
 */
@FormProperties({ "operations" })
public class Interface {
    private String description;
    /** Defines an operation available to manage particular aspects of the Node Type. */
    private Map<String, Operation> operations = Maps.newHashMap();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Operation> getOperations() {
        return operations;
    }

    public void setOperations(Map<String, Operation> operations) {
        this.operations = operations;
    }
}
