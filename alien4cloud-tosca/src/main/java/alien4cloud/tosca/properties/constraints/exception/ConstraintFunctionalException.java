package alien4cloud.tosca.properties.constraints.exception;

import alien4cloud.exception.FunctionalException;
import alien4cloud.tosca.properties.constraints.ConstraintUtil;
import lombok.Getter;

/**
 * All functional error related to constraint processing must go here
 *
 * @author mkv
 *
 */
public class ConstraintFunctionalException extends FunctionalException {

    private static final long serialVersionUID = 1L;

    @Getter
    protected ConstraintUtil.ConstraintInformation constraintInformation;

    public ConstraintFunctionalException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstraintFunctionalException(String message) {
        super(message);
    }

    public ConstraintFunctionalException(String message, Throwable cause, ConstraintUtil.ConstraintInformation constraintInformation) {
        super(message, cause);
        this.constraintInformation = constraintInformation;
    }

}
