package alien4cloud.tosca.parser;

import java.util.List;

import com.google.common.collect.Lists;

import alien4cloud.exception.FunctionalException;

public class ParsingException extends FunctionalException {
    private static final long serialVersionUID = 1L;

    private String fileName;
    private final List<ParsingError> parsingErrors;

    public ParsingException(String fileName, ParsingError toscaParsingError) {
        super(toscaParsingError.toString());
        this.fileName = fileName;
        parsingErrors = Lists.newArrayList(toscaParsingError);
    }

    public ParsingException(String fileName, List<ParsingError> toscaParsingErrors) {
        super(toscaParsingErrors.toString());
        this.fileName = fileName;
        parsingErrors = toscaParsingErrors;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<ParsingError> getParsingErrors() {
        return parsingErrors;
    }
}
