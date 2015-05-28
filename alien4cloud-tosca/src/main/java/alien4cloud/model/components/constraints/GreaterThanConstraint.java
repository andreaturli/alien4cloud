package alien4cloud.model.components.constraints;

import javax.validation.constraints.NotNull;

import alien4cloud.tosca.normative.IPropertyType;
import alien4cloud.tosca.properties.constraints.exception.ConstraintValueDoNotMatchPropertyTypeException;
import alien4cloud.tosca.properties.constraints.exception.ConstraintViolationException;

public class GreaterThanConstraint extends AbstractComparablePropertyConstraint {
    @NotNull
    private String greaterThan;

    @Override
    public void initialize(IPropertyType<?> propertyType) throws ConstraintValueDoNotMatchPropertyTypeException {
        initialize(greaterThan, propertyType);
    }

    @Override
    protected void doValidate(Object propertyValue) throws ConstraintViolationException {
        if (getComparable().compareTo(propertyValue) >= 0) {
            throw new ConstraintViolationException(propertyValue + " < " + greaterThan);
        }
    }

    public String getGreaterThan() {
        return greaterThan;
    }

    public void setGreaterThan(String greaterThan) {
        this.greaterThan = greaterThan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GreaterThanConstraint)) return false;

        GreaterThanConstraint that = (GreaterThanConstraint) o;

        if (greaterThan != null ? !greaterThan.equals(that.greaterThan) : that.greaterThan != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return greaterThan != null ? greaterThan.hashCode() : 0;
    }
}
