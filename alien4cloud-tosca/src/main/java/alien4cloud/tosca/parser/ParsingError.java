package alien4cloud.tosca.parser;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.MarkedYAMLException;

import alien4cloud.tosca.parser.impl.ErrorCode;

/**
 * Contains error informations relative to Yaml parsing.
 */
public class ParsingError {
    private ParsingErrorLevel errorLevel;
    private ErrorCode errorCode;
    private String context;
    private SimpleMark startMark;
    private String problem;
    private SimpleMark endMark;
    private String note;

    public ParsingError() {
    }

    public ParsingError(ParsingErrorLevel errorLevel, ErrorCode errorCode, String context, Mark startMark, String problem, Mark endMark, String note) {
        this.errorLevel = errorLevel;
        this.errorCode = errorCode;
        this.context = context;
        this.startMark = startMark == null ? null : new SimpleMark(startMark);
        this.problem = problem;
        this.endMark = endMark == null ? null : new SimpleMark(endMark);
        this.note = note;
    }

    public ParsingError(ErrorCode errorCode, String context, Mark startMark, String problem, Mark endMark, String note) {
        this.errorLevel = ParsingErrorLevel.ERROR;
        this.errorCode = errorCode;
        this.context = context;
        this.startMark = startMark == null ? null : new SimpleMark(startMark);
        this.problem = problem;
        this.endMark = endMark == null ? null : new SimpleMark(endMark);
        this.note = note;
    }

    public ParsingError(ErrorCode errorCode, MarkedYAMLException cause) {
        this.errorLevel = ParsingErrorLevel.ERROR;
        this.errorCode = errorCode;
        this.context = cause.getContext();
        this.startMark = cause.getContextMark() == null ? null : new SimpleMark(cause.getContextMark());
        this.problem = cause.getProblem();
        this.endMark = cause.getContextMark() == null ? null : new SimpleMark(cause.getProblemMark());
        this.note = null;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public ParsingErrorLevel getErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(ParsingErrorLevel errorLevel) {
        this.errorLevel = errorLevel;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public SimpleMark getStartMark() {
        return startMark;
    }

    public void setStartMark(SimpleMark startMark) {
        this.startMark = startMark;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public SimpleMark getEndMark() {
        return endMark;
    }

    public void setEndMark(SimpleMark endMark) {
        this.endMark = endMark;
    }

    @Override
    public String toString() {
        return "Context: " + context + "\nProblem: " + problem + "\nNote: " + note + "\nStart: " + startMark + "\nEnd  : " + endMark;
    }
}
