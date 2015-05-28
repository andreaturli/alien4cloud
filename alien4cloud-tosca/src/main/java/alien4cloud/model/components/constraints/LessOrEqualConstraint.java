package alien4cloud.model.components.constraints;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import alien4cloud.json.deserializer.TextDeserializer;
import alien4cloud.tosca.normative.IPropertyType;
import alien4cloud.tosca.properties.constraints.exception.ConstraintValueDoNotMatchPropertyTypeException;
import alien4cloud.tosca.properties.constraints.exception.ConstraintViolationException;

public class LessOrEqualConstraint extends AbstractComparablePropertyConstraint {

    @JsonDeserialize(using = TextDeserializer.class)
    @NotNull
    private String lessOrEqual;

    @Override
    public void initialize(IPropertyType<?> propertyType) throws ConstraintValueDoNotMatchPropertyTypeException {
        initialize(lessOrEqual, propertyType);
    }

    @Override
    protected void doValidate(Object propertyValue) throws ConstraintViolationException {
        if (getComparable().compareTo(propertyValue) < 0) {
            throw new ConstraintViolationException(propertyValue + " >= " + lessOrEqual);
        }
    }

    public String getLessOrEqual() {
        return lessOrEqual;
    }

    public void setLessOrEqual(String lessOrEqual) {
        this.lessOrEqual = lessOrEqual;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LessOrEqualConstraint)) return false;

        LessOrEqualConstraint that = (LessOrEqualConstraint) o;

        if (lessOrEqual != null ? !lessOrEqual.equals(that.lessOrEqual) : that.lessOrEqual != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return lessOrEqual != null ? lessOrEqual.hashCode() : 0;
    }
}
