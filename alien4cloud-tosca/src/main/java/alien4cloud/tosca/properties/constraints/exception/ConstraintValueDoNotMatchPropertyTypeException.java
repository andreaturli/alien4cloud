package alien4cloud.tosca.properties.constraints.exception;

import alien4cloud.tosca.properties.constraints.ConstraintUtil;

/**
 * Exception to be thrown when a constraint definition is invalid because the specified value doesn't match the property type.
 *
 * @author luc boutier
 */
public class ConstraintValueDoNotMatchPropertyTypeException extends ConstraintFunctionalException {

    private static final long serialVersionUID = 4342613849660957651L;

    public ConstraintValueDoNotMatchPropertyTypeException(String message) {
        super(message);
    }

    public ConstraintValueDoNotMatchPropertyTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstraintValueDoNotMatchPropertyTypeException(String message, Throwable cause, ConstraintUtil.ConstraintInformation constraintInformation) {
        super(message, cause);
        this.constraintInformation = constraintInformation;
    }
}
