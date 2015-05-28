package alien4cloud.tosca.parser;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;

public class ParsingContextExecution {

    private static final Logger log = LoggerFactory.getLogger(ParsingContextExecution.class);

    private BeanWrapper root;

    private final Queue<DefferedParsingValueExecutor> deferredParsers = new PriorityQueue<DefferedParsingValueExecutor>();

    private final ParsingContext parsingContext;

    /** Map of parsers by type */
    private Map<String, INodeParser> registry;

    /** Eventually, the current node parent object. */
    private Object parent;

    public ParsingContextExecution(String fileName) {
        parsingContext = new ParsingContext(fileName);
    }

    public String getFileName() {
        return parsingContext.getFileName();
    }

    public List<ParsingError> getParsingErrors() {
        return parsingContext.getParsingErrors();
    }

    public void addDeferredParser(DefferedParsingValueExecutor runnable) {
        if (log.isDebugEnabled()) {
            log.debug("Adding deferred parser for key : " + runnable.getKey());
        }
        // if the order is not defined, we use the queue size to keep FIFO behavior
        if (runnable.getDeferredOrder() == 0) {
            runnable.setDeferredOrder(deferredParsers.size());
        }
        deferredParsers.offer(runnable);
    }

    public void runDefferedParsers() {
        while (!deferredParsers.isEmpty()) {
            DefferedParsingValueExecutor runnable = deferredParsers.poll();
            if (log.isDebugEnabled()) {
                log.debug("Finally executing deferred parser for key : " + runnable.getKey());
            }
            runnable.run();
        }
    }

    public static Logger getLog() {
        return log;
    }

    public BeanWrapper getRoot() {
        return root;
    }

    public void setRoot(BeanWrapper root) {
        this.root = root;
    }

    public ParsingContext getParsingContext() {
        return parsingContext;
    }

    public Map<String, INodeParser> getRegistry() {
        return registry;
    }

    public void setRegistry(Map<String, INodeParser> registry) {
        this.registry = registry;
    }

    public Object getParent() {
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }
}
