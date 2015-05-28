package alien4cloud.model.components;

import alien4cloud.ui.form.annotation.FormProperties;

/**
 * Represents a simple scalar property value.
 */
@FormProperties({ "value" })
public class ScalarPropertyValue extends AbstractPropertyValue {
    private String value;


    public ScalarPropertyValue() {}

    public ScalarPropertyValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScalarPropertyValue)) return false;

        ScalarPropertyValue that = (ScalarPropertyValue) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
