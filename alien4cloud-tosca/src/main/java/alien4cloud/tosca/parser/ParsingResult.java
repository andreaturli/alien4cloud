package alien4cloud.tosca.parser;

public class ParsingResult<T> {
    private T result;
    private ParsingContext context;

    public ParsingResult() {
    }

    public ParsingResult(T result, ParsingContext context) {
        this.result = result;
        this.context = context;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public ParsingContext getContext() {
        return context;
    }

    public void setContext(ParsingContext context) {
        this.context = context;
    }
}
