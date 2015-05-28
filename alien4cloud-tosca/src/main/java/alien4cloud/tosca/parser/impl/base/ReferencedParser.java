package alien4cloud.tosca.parser.impl.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.Node;

import alien4cloud.tosca.parser.INodeParser;
import alien4cloud.tosca.parser.ParsingContextExecution;
import alien4cloud.tosca.parser.ParsingError;
import alien4cloud.tosca.parser.impl.ErrorCode;

/**
 * Parser implementation that delegates parsing to a parser referenced in the parser registry based on the type key.
 */
public class ReferencedParser<T> implements INodeParser<T> {

    private static final Logger log = LoggerFactory.getLogger(ReferencedParser.class);

    private String typeName;

    public ReferencedParser(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public T parse(Node node, ParsingContextExecution context) {
        INodeParser<?> delegate = context.getRegistry().get(typeName);
        if (delegate == null) {
            log.error("No parser found for yaml type {}", typeName);
            context.getParsingErrors().add(
                    new ParsingError(ErrorCode.ALIEN_MAPPING_ERROR, "No parser found for yaml type", node.getStartMark(), "", node.getEndMark(), typeName));
            return null;
        }
        return (T) delegate.parse(node, context);
    }

    @Override
    public boolean isDeferred(ParsingContextExecution context) {
        INodeParser<?> delegate = context.getRegistry().get(typeName);
        return delegate.isDeferred(context);
    }

    @Override
    public int getDefferedOrder(ParsingContextExecution context) {
        INodeParser<?> delegate = context.getRegistry().get(typeName);
        return delegate.getDefferedOrder(context);
    }

}
