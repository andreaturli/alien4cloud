package alien4cloud.utils.services;

import java.beans.IntrospectionException;

import org.springframework.stereotype.Service;

import alien4cloud.model.components.PropertyConstraint;
import alien4cloud.model.components.PropertyDefinition;
import alien4cloud.tosca.normative.IPropertyType;
import alien4cloud.tosca.normative.ToscaType;
import alien4cloud.tosca.properties.constraints.ConstraintUtil;
import alien4cloud.tosca.properties.constraints.ConstraintUtil.ConstraintInformation;
import alien4cloud.tosca.properties.constraints.exception.ConstraintTechnicalException;
import alien4cloud.tosca.properties.constraints.exception.ConstraintValueDoNotMatchPropertyTypeException;
import alien4cloud.tosca.properties.constraints.exception.ConstraintViolationException;
import alien4cloud.utils.version.InvalidVersionException;
import alien4cloud.utils.VersionUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Common property constraint utils
 *
 * @author mourouvi
 *
 */
// FIXME: why is this a service ? It looks like a util (with static methods) rather than a service !
@Slf4j
@Service
public class ConstraintPropertyService {

    /**
     * Check constraints defined on a property for a specified value
     *
     * @param propertyName Property name (mainly used to create a comprehensive error message)
     * @param propertyValue Tested property value
     * @param propertyDefinition Full property definition with type, constraints, default value,...
     * @throws ConstraintViolationException
     * @throws ConstraintValueDoNotMatchPropertyTypeException
     */
    public void checkPropertyConstraint(final String propertyName, final String propertyValue, final PropertyDefinition propertyDefinition)
            throws ConstraintViolationException, ConstraintValueDoNotMatchPropertyTypeException {

        // get property definition
        if (propertyDefinition != null) {
            ConstraintInformation consInformation = null;
            if (propertyDefinition.getConstraints() != null && !propertyDefinition.getConstraints().isEmpty()) {
                for (PropertyConstraint constraint : propertyDefinition.getConstraints()) {
                    IPropertyType<?> toscaType = ToscaType.fromYamlTypeName(propertyDefinition.getType());
                    try {
                        consInformation = ConstraintUtil.getConstraintInformation(constraint);
                        constraint.initialize(toscaType);
                        constraint.validate(toscaType, propertyValue);
                    } catch (ConstraintViolationException e) {
                        throw new ConstraintViolationException(e.getMessage(), e, consInformation);
                    } catch (IntrospectionException | ConstraintValueDoNotMatchPropertyTypeException e) {
                        // ConstraintValueDoNotMatchPropertyTypeException is not supposed to be raised here (only in constraint definition validation)
                        log.error("Constraint violation introspection error for property <" + propertyName + "> with value <" + propertyValue + ">", e);
                        throw new ConstraintTechnicalException("Unable to get constraint informations for property <" + propertyName + ">");
                    }
                }
            } else {
                // check any property definition without constraints (type/value)
                try {
                    checkBasicType(propertyDefinition, propertyName, propertyValue);
                } catch (NumberFormatException | InvalidVersionException e) {
                    log.error("Basic type check failed", e);
                    consInformation = new ConstraintInformation(propertyName, null, propertyValue, propertyDefinition.getType());
                    throw new ConstraintValueDoNotMatchPropertyTypeException(e.getMessage(), e, consInformation);
                }
            }

        }
    }

    /**
     * Test type/value regardless constraints
     *
     * @param propertyDefinition
     * @param propertyValue
     * @throws Exception
     */
    private void checkBasicType(final PropertyDefinition propertyDefinition, final String propertyName, final String propertyValue) {

        // check basic type value : "boolean" (not handled, no exception thrown)
        // "string" (basic case, no exception), "float", "integer", "version"
        String type = propertyDefinition.getType();
        try {
            switch (type) {
            case "integer":
                Integer.parseInt(propertyValue);
                break;
            case "float":
                Float.parseFloat(propertyValue);
                break;
            case "version":
                VersionUtil.parseVersion(propertyValue);
                break;
            default:
                // last type "string" can have any format
                log.info("Type {} not checked yet", type);
                break;
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Float or Integer type invalid check for property [ " + propertyName + " ] and value [ " + propertyValue + " ]");
        } catch (InvalidVersionException e) {
            throw new InvalidVersionException("Version type invalid check for property [ " + propertyName + " ] and value [ " + propertyValue + " ]");
        }
    }

}
