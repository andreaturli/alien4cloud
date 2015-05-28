package alien4cloud.paas.plan;

/**
 * Constants and helper functions to manage TOSCA Node life-cycle.
 */
public final class ToscaNodeLifecycleConstants {
    // lifecycle interfaces
    public static final String STANDARD_SHORT = "Standard";
    public static final String STANDARD = "tosca.interfaces.node.lifecycle.Standard";

    // lifecycle operations
    public static final String CREATE = "create";
    public static final String CONFIGURE = "configure";
    public static final String START = "start";
    public static final String POST_START = "post_start";
    public static final String STOP = "stop";
    public static final String DELETE = "delete";

    // node states
    public static final String INITIAL = "initial";
    public static final String CREATED = "created";
    public static final String CONFIGURED = "configured";
    public static final String STARTED = "started";
    public static final String AVAILABLE = "available";
    public static final String STOPPED = "stopped";
    public static final String DELETED = "deleted";

    private ToscaNodeLifecycleConstants() {
    }
}