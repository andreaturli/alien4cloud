package alien4cloud.model.components;

import static alien4cloud.dao.model.FetchContext.DEPLOYMENT;

import java.util.Set;

import org.elasticsearch.annotation.ESObject;
import org.elasticsearch.annotation.Id;
import org.elasticsearch.annotation.NestedObject;
import org.elasticsearch.annotation.StringField;
import org.elasticsearch.annotation.query.FetchContext;
import org.elasticsearch.annotation.query.TermFilter;
import org.elasticsearch.mapping.IndexType;

import alien4cloud.exception.IndexingServiceException;
import alien4cloud.model.deployment.IDeploymentSource;

@ESObject
public class Csar implements IDeploymentSource {
	@FetchContext(contexts = { DEPLOYMENT }, include = { true })
	private String name;

	@TermFilter
	private String version;

	private String toscaDefinitionsVersion;

	private String toscaDefaultNamespace;

	private String templateAuthor;

	private String description;

    @TermFilter(paths = { "name", "version" })
    @NestedObject(nestedClass = CSARDependency.class)
    private Set<CSARDependency> dependencies;

	private String topologyId;
	private String cloudId;

	private String license;

	/** Default constructor */
	public Csar() {
	}

	/** Argument constructor */
	public Csar(String name, String version) {
		this.name = name;
		this.version = version;
	}

	@Id
	@StringField(indexType = IndexType.not_analyzed, includeInAll = false)
	@FetchContext(contexts = { DEPLOYMENT }, include = { true })
	public String getId() {
		if (name == null) {
			throw new IndexingServiceException("Csar name is mandatory");
		}
		if (version == null) {
			throw new IndexingServiceException("Csar version is mandatory");
		}
		return name + ":" + version;
	}

	public void setId(String id) {
		// Not authorized to set id as it's auto-generated from name and version
	}

	public void setDependencies(Set<CSARDependency> dependencies) {
		this.dependencies = dependencies;
	}

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getToscaDefinitionsVersion() {
        return toscaDefinitionsVersion;
    }

    public void setToscaDefinitionsVersion(String toscaDefinitionsVersion) {
        this.toscaDefinitionsVersion = toscaDefinitionsVersion;
    }

    public String getToscaDefaultNamespace() {
        return toscaDefaultNamespace;
    }

    public void setToscaDefaultNamespace(String toscaDefaultNamespace) {
        this.toscaDefaultNamespace = toscaDefaultNamespace;
    }

    public String getTemplateAuthor() {
        return templateAuthor;
    }

    public void setTemplateAuthor(String templateAuthor) {
        this.templateAuthor = templateAuthor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<CSARDependency> getDependencies() {
        return dependencies;
    }

    public String getTopologyId() {
        return topologyId;
    }

    public void setTopologyId(String topologyId) {
        this.topologyId = topologyId;
    }

    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Csar)) return false;

        Csar csar = (Csar) o;

        if (cloudId != null ? !cloudId.equals(csar.cloudId) : csar.cloudId != null) return false;
        if (dependencies != null ? !dependencies.equals(csar.dependencies) : csar.dependencies != null) return false;
        if (description != null ? !description.equals(csar.description) : csar.description != null) return false;
        if (license != null ? !license.equals(csar.license) : csar.license != null) return false;
        if (name != null ? !name.equals(csar.name) : csar.name != null) return false;
        if (templateAuthor != null ? !templateAuthor.equals(csar.templateAuthor) : csar.templateAuthor != null)
            return false;
        if (topologyId != null ? !topologyId.equals(csar.topologyId) : csar.topologyId != null) return false;
        if (toscaDefaultNamespace != null ? !toscaDefaultNamespace.equals(csar.toscaDefaultNamespace) : csar.toscaDefaultNamespace != null)
            return false;
        if (toscaDefinitionsVersion != null ? !toscaDefinitionsVersion.equals(csar.toscaDefinitionsVersion) : csar.toscaDefinitionsVersion != null)
            return false;
        if (version != null ? !version.equals(csar.version) : csar.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (toscaDefinitionsVersion != null ? toscaDefinitionsVersion.hashCode() : 0);
        result = 31 * result + (toscaDefaultNamespace != null ? toscaDefaultNamespace.hashCode() : 0);
        result = 31 * result + (templateAuthor != null ? templateAuthor.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (dependencies != null ? dependencies.hashCode() : 0);
        result = 31 * result + (topologyId != null ? topologyId.hashCode() : 0);
        result = 31 * result + (cloudId != null ? cloudId.hashCode() : 0);
        result = 31 * result + (license != null ? license.hashCode() : 0);
        return result;
    }
}
