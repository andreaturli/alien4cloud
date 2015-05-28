package alien4cloud.model.components.constraints;

import javax.validation.constraints.NotNull;

import alien4cloud.tosca.normative.IPropertyType;
import alien4cloud.tosca.properties.constraints.exception.ConstraintValueDoNotMatchPropertyTypeException;
import alien4cloud.tosca.properties.constraints.exception.ConstraintViolationException;

public class LessThanConstraint extends AbstractComparablePropertyConstraint {
    @NotNull
    private String lessThan;

    @Override
    public void initialize(IPropertyType<?> propertyType) throws ConstraintValueDoNotMatchPropertyTypeException {
        initialize(lessThan, propertyType);
    }

    @Override
    protected void doValidate(Object propertyValue) throws ConstraintViolationException {
        if (getComparable().compareTo(propertyValue) <= 0) {
            throw new ConstraintViolationException(propertyValue + " > " + lessThan);
        }
    }

    public String getLessThan() {
        return lessThan;
    }

    public void setLessThan(String lessThan) {
        this.lessThan = lessThan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LessThanConstraint)) return false;

        LessThanConstraint that = (LessThanConstraint) o;

        if (lessThan != null ? !lessThan.equals(that.lessThan) : that.lessThan != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return lessThan != null ? lessThan.hashCode() : 0;
    }
}
