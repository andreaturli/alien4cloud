package alien4cloud.model.components.constraints;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import alien4cloud.tosca.normative.IPropertyType;
import alien4cloud.tosca.properties.constraints.ConstraintUtil;
import alien4cloud.tosca.properties.constraints.exception.ConstraintValueDoNotMatchPropertyTypeException;
import alien4cloud.tosca.properties.constraints.exception.ConstraintViolationException;

public class EqualConstraint extends AbstractPropertyConstraint {
    @NotNull
    private String equal;

    @JsonIgnore
    private Object typed;

    @Override
    public void initialize(IPropertyType<?> propertyType) throws ConstraintValueDoNotMatchPropertyTypeException {
        typed = ConstraintUtil.convert(propertyType, equal);
    }

    @Override
    public void validate(Object propertyValue) throws ConstraintViolationException {
        if (propertyValue == null) {
            if (typed != null) {
                fail(propertyValue);
            }
        } else if (typed == null) {
            fail(propertyValue);
        } else if (!typed.equals(propertyValue)) {
            fail(propertyValue);
        }
    }

    private void fail(Object propertyValue) throws ConstraintViolationException {
        throw new ConstraintViolationException("Equal constraint violation, the reference is <" + equal + "> but the value to compare is <" + propertyValue
                + ">");
    }

    public String getEqual() {
        return equal;
    }

    public void setEqual(String equal) {
        this.equal = equal;
    }

    public Object getTyped() {
        return typed;
    }

    public void setTyped(Object typed) {
        this.typed = typed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EqualConstraint)) return false;

        EqualConstraint that = (EqualConstraint) o;

        if (equal != null ? !equal.equals(that.equal) : that.equal != null) return false;
        if (typed != null ? !typed.equals(that.typed) : that.typed != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = equal != null ? equal.hashCode() : 0;
        result = 31 * result + (typed != null ? typed.hashCode() : 0);
        return result;
    }
}
