package alien4cloud.model.components.constraints;

import javax.validation.constraints.NotNull;

import alien4cloud.tosca.properties.constraints.exception.ConstraintViolationException;

public class MaxLengthConstraint extends AbstractStringPropertyConstraint {
    @NotNull
    private Integer maxLength;

    public MaxLengthConstraint(Integer maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    protected void doValidate(String propertyValue) throws ConstraintViolationException {
        if (propertyValue.length() > maxLength) {
            throw new ConstraintViolationException("The length of the value is greater than [" + maxLength + "]");
        }
    }

    public MaxLengthConstraint() {
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaxLengthConstraint)) return false;

        MaxLengthConstraint that = (MaxLengthConstraint) o;

        if (maxLength != null ? !maxLength.equals(that.maxLength) : that.maxLength != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return maxLength != null ? maxLength.hashCode() : 0;
    }
}
