package alien4cloud.tosca.properties.constraints.exception;

import alien4cloud.tosca.properties.constraints.ConstraintUtil;

/**
 * Exception happened while user violated a predefined constraint
 *
 * @author mkv
 *
 */
public class ConstraintViolationException extends ConstraintFunctionalException {

    private static final long serialVersionUID = 1L;

    public ConstraintViolationException(String message) {
        super(message);
    }

    public ConstraintViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstraintViolationException(String message, Throwable cause, ConstraintUtil.ConstraintInformation constraintInformation) {
        super(message, cause);
        this.constraintInformation = constraintInformation;
    }

}
