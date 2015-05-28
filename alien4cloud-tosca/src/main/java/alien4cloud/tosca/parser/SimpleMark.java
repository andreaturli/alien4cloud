package alien4cloud.tosca.parser;

import org.yaml.snakeyaml.error.Mark;

public class SimpleMark {
    private int line;
    private int column;

    /** No args constructor for de-serialization. */
    public SimpleMark() {
    }

    public SimpleMark(Mark mark) {
        this.line = mark.getLine() + 1;
        this.column = mark.getColumn() + 1;
    }

    public SimpleMark(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return "SimpleMark{" +
                "line=" + line +
                ", column=" + column +
                '}';
    }
}
