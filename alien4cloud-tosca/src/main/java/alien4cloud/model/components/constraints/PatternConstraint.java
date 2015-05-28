package alien4cloud.model.components.constraints;

import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import alien4cloud.tosca.properties.constraints.exception.ConstraintViolationException;

public class PatternConstraint extends AbstractStringPropertyConstraint {
    @NotNull
    private String pattern;
    @JsonIgnore
    private Pattern compiledPattern;

    public void setPattern(String pattern) {
        this.pattern = pattern;
        this.compiledPattern = Pattern.compile(this.pattern);
    }

    @Override
    protected void doValidate(String propertyValue) throws ConstraintViolationException {
        if (!compiledPattern.matcher(propertyValue).matches()) {
            throw new ConstraintViolationException("The value do not match pattern " + pattern);
        }
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatternConstraint)) return false;

        PatternConstraint that = (PatternConstraint) o;

        if (compiledPattern != null ? !compiledPattern.equals(that.compiledPattern) : that.compiledPattern != null)
            return false;
        if (pattern != null ? !pattern.equals(that.pattern) : that.pattern != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pattern != null ? pattern.hashCode() : 0;
        result = 31 * result + (compiledPattern != null ? compiledPattern.hashCode() : 0);
        return result;
    }
}
