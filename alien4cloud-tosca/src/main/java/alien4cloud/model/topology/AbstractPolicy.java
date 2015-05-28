package alien4cloud.model.topology;

public abstract class AbstractPolicy {

    private String name;

    public AbstractPolicy() {}

    public abstract String getType();

    public void setType(String type) {
        // here only for JSON deserialization
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
