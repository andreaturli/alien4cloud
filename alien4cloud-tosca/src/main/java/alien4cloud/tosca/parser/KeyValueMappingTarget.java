package alien4cloud.tosca.parser;

public class KeyValueMappingTarget extends MappingTarget {
    private String keyPath;

    public KeyValueMappingTarget(String keyPath, String path, INodeParser<?> parser) {
        super(path, parser);
        this.keyPath = keyPath;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }
}
