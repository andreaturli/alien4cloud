package alien4cloud.tosca.normative;

import java.math.BigDecimal;

/**
 * @author Minh Khang VU
 */
public abstract class ScalarUnit<T extends Unit> implements Comparable<ScalarUnit<T>> {

    private long value;

    private T unit;

    public ScalarUnit(long value, T unit) {
        this.value = value;
        this.unit = unit;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public T getUnit() {
        return unit;
    }

    public void setUnit(T unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScalarUnit)) return false;

        ScalarUnit that = (ScalarUnit) o;

        if (value != that.value) return false;
        if (unit != null ? !unit.equals(that.unit) : that.unit != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (value ^ (value >>> 32));
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(ScalarUnit<T> o) {
        BigDecimal thisValue = new BigDecimal(value).multiply(new BigDecimal(unit.getMultiplier()));
        BigDecimal otherValue = new BigDecimal(o.value).multiply(new BigDecimal(o.unit.getMultiplier()));
        return thisValue.compareTo(otherValue);
    }
}
