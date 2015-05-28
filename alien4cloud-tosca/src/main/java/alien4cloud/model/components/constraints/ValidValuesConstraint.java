package alien4cloud.model.components.constraints;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;

import alien4cloud.tosca.normative.IPropertyType;
import alien4cloud.tosca.properties.constraints.ConstraintUtil;
import alien4cloud.tosca.properties.constraints.exception.ConstraintValueDoNotMatchPropertyTypeException;
import alien4cloud.tosca.properties.constraints.exception.ConstraintViolationException;
import alien4cloud.ui.form.annotation.FormProperties;

@FormProperties("validValues")
public class ValidValuesConstraint extends AbstractPropertyConstraint {
    @NotNull
    private List<String> validValues;
    @JsonIgnore
    private Set<Object> validValuesTyped;

    @Override
    public void initialize(IPropertyType<?> propertyType) throws ConstraintValueDoNotMatchPropertyTypeException {
        validValuesTyped = Sets.newHashSet();
        if (validValues == null) {
            throw new ConstraintValueDoNotMatchPropertyTypeException("validValues constraint has invalid value <> property type is <" + propertyType.toString()
                    + ">");
        }
        for (String value : validValues) {
            validValuesTyped.add(ConstraintUtil.convert(propertyType, value));
        }
    }

    @Override
    public void validate(Object propertyValue) throws ConstraintViolationException {
        if (propertyValue == null) {
            throw new ConstraintViolationException("Value to validate is null");
        }
        if (!validValuesTyped.contains(propertyValue)) {
            throw new ConstraintViolationException("The value is not in the list of valid values");
        }
    }

    public List<String> getValidValues() {
        return validValues;
    }

    public void setValidValues(List<String> validValues) {
        this.validValues = validValues;
    }

    public Set<Object> getValidValuesTyped() {
        return validValuesTyped;
    }

    public void setValidValuesTyped(Set<Object> validValuesTyped) {
        this.validValuesTyped = validValuesTyped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidValuesConstraint)) return false;

        ValidValuesConstraint that = (ValidValuesConstraint) o;

        if (validValues != null ? !validValues.equals(that.validValues) : that.validValues != null) return false;
        if (validValuesTyped != null ? !validValuesTyped.equals(that.validValuesTyped) : that.validValuesTyped != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = validValues != null ? validValues.hashCode() : 0;
        result = 31 * result + (validValuesTyped != null ? validValuesTyped.hashCode() : 0);
        return result;
    }
}
