package alien4cloud.model.components.constraints;

import javax.validation.constraints.NotNull;

import alien4cloud.tosca.properties.constraints.exception.ConstraintViolationException;

public class MinLengthConstraint extends AbstractStringPropertyConstraint {
    @NotNull
    private Integer minLength;

    public MinLengthConstraint(Integer minLength) {
        this.minLength = minLength;
    }

    @Override
    protected void doValidate(String propertyValue) throws ConstraintViolationException {
        if (propertyValue.length() < minLength) {
            throw new ConstraintViolationException("The length of the value is less than [" + minLength + "]");
        }
    }

    public MinLengthConstraint() {
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MinLengthConstraint)) return false;

        MinLengthConstraint that = (MinLengthConstraint) o;

        if (minLength != null ? !minLength.equals(that.minLength) : that.minLength != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return minLength != null ? minLength.hashCode() : 0;
    }
}
