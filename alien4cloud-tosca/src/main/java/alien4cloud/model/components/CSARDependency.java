package alien4cloud.model.components;

import static com.google.common.base.Preconditions.checkNotNull;

import org.elasticsearch.annotation.StringField;
import org.elasticsearch.mapping.IndexType;

/**
 * Defines a dependency on a CloudServiceArchive.
 */
public class CSARDependency {

    @StringField(indexType = IndexType.not_analyzed)
    private String name;

    @StringField(indexType = IndexType.not_analyzed)
    private String version;

    public CSARDependency() {}

    public CSARDependency(String version, String name) {
        this.version = version;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = checkNotNull(name, "name");
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = checkNotNull(version, "version");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CSARDependency)) return false;

        CSARDependency that = (CSARDependency) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CSARDependency{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
