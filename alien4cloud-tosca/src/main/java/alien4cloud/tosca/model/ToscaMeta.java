package alien4cloud.tosca.model;

import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import alien4cloud.model.components.CSARDependency;

/**
 * Contains meta-data on the archive.
 */
@Getter
@Setter
@SuppressWarnings("PMD.UnusedPrivateField")
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
}
