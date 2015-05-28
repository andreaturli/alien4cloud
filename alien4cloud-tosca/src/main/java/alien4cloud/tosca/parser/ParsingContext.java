package alien4cloud.tosca.parser;

import java.util.List;

import com.google.common.collect.Lists;

public class ParsingContext {
    private String fileName;
    private List<ParsingError> parsingErrors = Lists.newArrayList();
    /** If parsing triggers parsing of other related yaml files. */
    private List<ParsingResult<?>> subResults = Lists.newArrayList();

    public ParsingContext() {
    }

    public ParsingContext(String fileName) {
        this.fileName = fileName;
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

    public void setParsingErrors(List<ParsingError> parsingErrors) {
        this.parsingErrors = parsingErrors;
    }

    public List<ParsingResult<?>> getSubResults() {
        return subResults;
    }

    public void setSubResults(List<ParsingResult<?>> subResults) {
        this.subResults = subResults;
    }
}
