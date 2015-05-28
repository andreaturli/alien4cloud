package alien4cloud.tosca.parser;

import org.yaml.snakeyaml.nodes.Node;

public class YamlSimpleParser<T> extends YamlParser<T> {
    private INodeParser<T> nodeParser;

    public YamlSimpleParser(INodeParser<T> nodeParser) {
        this.nodeParser = nodeParser;
    }

    @Override
    protected INodeParser<T> getParser(Node rootNode, ParsingContextExecution context) throws ParsingException {
        return nodeParser;
    }
}
