package alien4cloud.model.components.constraints;

import javax.validation.constraints.NotNull;

import alien4cloud.tosca.properties.constraints.exception.ConstraintViolationException;

public class LengthConstraint extends AbstractStringPropertyConstraint {
    @NotNull
    private Integer length;

    @Override
    protected void doValidate(String propertyValue) throws ConstraintViolationException {
        if (propertyValue.length() != length) {
            throw new ConstraintViolationException("The length of the value is not equals to [" + length + "]");
        }
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LengthConstraint)) return false;

        LengthConstraint that = (LengthConstraint) o;

        if (length != null ? !length.equals(that.length) : that.length != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return length != null ? length.hashCode() : 0;
    }
}
