package alien4cloud.model.topology;

public class ScalingPolicy {

    private int minInstances;

    private int maxInstances;

    private int initialInstances;

    public ScalingPolicy() {
    }

    public ScalingPolicy(int minInstances, int maxInstances, int initialInstances) {
        this.minInstances = minInstances;
        this.maxInstances = maxInstances;
        this.initialInstances = initialInstances;
    }

    public int getMinInstances() {
        return minInstances;
    }

    public void setMinInstances(int minInstances) {
        this.minInstances = minInstances;
    }

    public int getMaxInstances() {
        return maxInstances;
    }

    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
    }

    public int getInitialInstances() {
        return initialInstances;
    }

    public void setInitialInstances(int initialInstances) {
        this.initialInstances = initialInstances;
    }
}
