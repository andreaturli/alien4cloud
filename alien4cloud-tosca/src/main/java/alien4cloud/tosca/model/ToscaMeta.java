package alien4cloud.tosca.model;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import alien4cloud.model.components.CSARDependency;

/**
 * Contains meta-data on the archive.
 */
public class ToscaMeta {
    @NotNull
    private String name;
    @NotNull
    private String version;
    private String license;
    private String createdBy;
    private String entryDefinitions;
    private List<String> definitions;
    private Set<CSARDependency> dependencies;

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

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getEntryDefinitions() {
        return entryDefinitions;
    }

    public void setEntryDefinitions(String entryDefinitions) {
        this.entryDefinitions = entryDefinitions;
    }

    public List<String> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<String> definitions) {
        this.definitions = definitions;
    }

    public Set<CSARDependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<CSARDependency> dependencies) {
        this.dependencies = dependencies;
    }
}
