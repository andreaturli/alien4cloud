package alien4cloud.tosca.normative;

/**
 * @author Minh Khang VU
 */
public enum TimeUnit implements Unit {

    D(60 * 60 * 24), H(60 * 60), M(60), S(1), MS(Math.pow(10, -3)), US(Math.pow(10, -6)), NS(Math.pow(10, -9));

    /**
     * Multiplier if convert to base unit which is second (s)
     */
    private double multiplier;

    TimeUnit(double multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public double getMultiplier() {
        return multiplier;
    }
}
