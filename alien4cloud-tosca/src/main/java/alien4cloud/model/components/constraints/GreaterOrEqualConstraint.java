package alien4cloud.model.components.constraints;

import javax.validation.constraints.NotNull;

import alien4cloud.tosca.normative.IPropertyType;
import alien4cloud.tosca.properties.constraints.exception.ConstraintValueDoNotMatchPropertyTypeException;
import alien4cloud.tosca.properties.constraints.exception.ConstraintViolationException;

public class GreaterOrEqualConstraint extends AbstractComparablePropertyConstraint {
    @NotNull
    private String greaterOrEqual;

    @Override
    public void initialize(IPropertyType<?> propertyType) throws ConstraintValueDoNotMatchPropertyTypeException {
        initialize(greaterOrEqual, propertyType);
    }

    @Override
    protected void doValidate(Object propertyValue) throws ConstraintViolationException {
        if (getComparable().compareTo(propertyValue) > 0) {
            throw new ConstraintViolationException(propertyValue + " <= " + greaterOrEqual);
        }
    }

    public String getGreaterOrEqual() {
        return greaterOrEqual;
    }

    public void setGreaterOrEqual(String greaterOrEqual) {
        this.greaterOrEqual = greaterOrEqual;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GreaterOrEqualConstraint)) return false;

        GreaterOrEqualConstraint that = (GreaterOrEqualConstraint) o;

        if (greaterOrEqual != null ? !greaterOrEqual.equals(that.greaterOrEqual) : that.greaterOrEqual != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return greaterOrEqual != null ? greaterOrEqual.hashCode() : 0;
    }
}
