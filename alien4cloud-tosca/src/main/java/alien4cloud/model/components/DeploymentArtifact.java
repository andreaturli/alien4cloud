package alien4cloud.model.components;

public class DeploymentArtifact implements IArtifact {
    /** This attribute specifies the type of this artifact. */
    private String artifactType;
    /** Specifies the reference of the artifact. */
    private String artifactRef;
    /** Specifies the display name of the artifact. */
    private String artifactName;

    /**
     * Non TOSCA compliant property, the artifactRepository indicate where the artifact is stored. It might be in the archive it-self (in this case this
     * property is null), in alien's internal artifact repository (alien) or nexus, git, svn ...
     */
    private String artifactRepository;

    /**
     * The name of the archive in which the original artifact lies.
     */
    private String archiveName;

    /**
     * The version of the archive in which the original artifact lies.
     */
    private String archiveVersion;

    public String getArtifactName() {
        return artifactName != null ? artifactName : artifactRef;
    }

    @Override
    public String getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(String artifactType) {
        this.artifactType = artifactType;
    }

    @Override
    public String getArtifactRef() {
        return artifactRef;
    }

    public void setArtifactRef(String artifactRef) {
        this.artifactRef = artifactRef;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    @Override
    public String getArtifactRepository() {
        return artifactRepository;
    }

    public void setArtifactRepository(String artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    @Override
    public String getArchiveName() {
        return archiveName;
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    public String getArchiveVersion() {
        return archiveVersion;
    }

    public void setArchiveVersion(String archiveVersion) {
        this.archiveVersion = archiveVersion;
    }
}
