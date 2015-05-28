package alien4cloud.tosca.container.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alien4cloud.model.components.PropertyConstraint;
import alien4cloud.model.components.PropertyDefinition;
import alien4cloud.tosca.normative.IPropertyType;
import alien4cloud.tosca.normative.ToscaType;
import alien4cloud.tosca.properties.constraints.exception.ConstraintValueDoNotMatchPropertyTypeException;

public class ToscaPropertyConstraintValidator implements ConstraintValidator<ToscaPropertyConstraint, PropertyDefinition> {

    private static final Logger log = LoggerFactory.getLogger(ToscaPropertyConstraintValidator.class);

    @Override
    public void initialize(ToscaPropertyConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(PropertyDefinition value, ConstraintValidatorContext context) {
        if (value.getConstraints() == null) {
            return true;
        }
        IPropertyType<?> toscaType = ToscaType.fromYamlTypeName(value.getType());
        if (toscaType == null) {
            return false;
        }
        boolean isValid = true;
        for (int i = 0; i < value.getConstraints().size(); i++) {
            PropertyConstraint constraint = value.getConstraints().get(i);
            try {
                constraint.initialize(toscaType);
            } catch (ConstraintValueDoNotMatchPropertyTypeException e) {
                log.info("Constraint definition error", e);
                context.buildConstraintViolationWithTemplate("CONSTRAINTS.VALIDATION.TYPE").addPropertyNode("constraints").addBeanNode().inIterable()
                        .atIndex(i).addConstraintViolation();
                isValid = false;
            }
        }
        return isValid;
    }
}
