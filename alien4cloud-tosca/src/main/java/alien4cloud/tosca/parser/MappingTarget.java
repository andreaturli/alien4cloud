package alien4cloud.tosca.parser;

public class MappingTarget {
    private boolean isRootPath;
    private String path;
    private INodeParser<?> parser;

    public MappingTarget(String path, INodeParser<?> parser) {
        this.isRootPath = path.startsWith("/");
        if (isRootPath) {
            this.path = path.substring(1);
        } else {
            this.path = path;
        }
        this.parser = parser;
    }

    public boolean isRootPath() {
        return isRootPath;
    }

    public String getPath() {
        return path;
    }

    public INodeParser<?> getParser() {
        return parser;
    }
}
