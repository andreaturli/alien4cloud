package alien4cloud.model.components;

import static alien4cloud.dao.model.FetchContext.TAG_SUGGESTION;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.elasticsearch.annotation.BooleanField;
import org.elasticsearch.annotation.DateField;
import org.elasticsearch.annotation.Id;
import org.elasticsearch.annotation.StringField;
import org.elasticsearch.annotation.query.FetchContext;
import org.elasticsearch.annotation.query.TermFilter;
import org.elasticsearch.mapping.IndexType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import alien4cloud.exception.IndexingServiceException;
import alien4cloud.model.common.Tag;

@JsonInclude(Include.NON_NULL)
public abstract class IndexedToscaElement {
    @FetchContext(contexts = { TAG_SUGGESTION }, include = { false })
    @StringField(indexType = IndexType.analyzed)
    @TermFilter
    private String elementId;

    @FetchContext(contexts = { TAG_SUGGESTION }, include = { false })
    @StringField(indexType = IndexType.not_analyzed)
    @TermFilter
    private String archiveName;

    @FetchContext(contexts = { TAG_SUGGESTION }, include = { false })
    @StringField(indexType = IndexType.not_analyzed)
    @TermFilter
    private String archiveVersion;

    @BooleanField
    @TermFilter
    private boolean isHighestVersion;

    private Set<String> olderVersions;

    @FetchContext(contexts = { TAG_SUGGESTION }, include = { false })
    @DateField(includeInAll = false, index = IndexType.no)
    private Date creationDate;

    @FetchContext(contexts = { TAG_SUGGESTION }, include = { false })
    @DateField(includeInAll = false, index = IndexType.no)
    private Date lastUpdateDate;

    /* Normative element */
    @FetchContext(contexts = { TAG_SUGGESTION }, include = { false })
    private String description;

    /* DSL extension */
    private List<Tag> tags;

    @Id
    public String getId() {
        if (elementId == null) {
            throw new IndexingServiceException("Element id is mandatory");
        }
        if (archiveVersion == null) {
            throw new IndexingServiceException("Archive version is mandatory");
        }
        return elementId + ":" + archiveVersion;
    }

    public void setId(String id) {
        // Not authorized to set id as it's auto-generated
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

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

    public boolean isHighestVersion() {
        return isHighestVersion;
    }

    public void setHighestVersion(boolean isHighestVersion) {
        this.isHighestVersion = isHighestVersion;
    }

    public Set<String> getOlderVersions() {
        return olderVersions;
    }

    public void setOlderVersions(Set<String> olderVersions) {
        this.olderVersions = olderVersions;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexedToscaElement)) return false;

        IndexedToscaElement that = (IndexedToscaElement) o;

        if (isHighestVersion != that.isHighestVersion) return false;
        if (archiveName != null ? !archiveName.equals(that.archiveName) : that.archiveName != null) return false;
        if (archiveVersion != null ? !archiveVersion.equals(that.archiveVersion) : that.archiveVersion != null)
            return false;
        if (creationDate != null ? !creationDate.equals(that.creationDate) : that.creationDate != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (elementId != null ? !elementId.equals(that.elementId) : that.elementId != null) return false;
        if (lastUpdateDate != null ? !lastUpdateDate.equals(that.lastUpdateDate) : that.lastUpdateDate != null)
            return false;
        if (olderVersions != null ? !olderVersions.equals(that.olderVersions) : that.olderVersions != null)
            return false;
        if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = elementId != null ? elementId.hashCode() : 0;
        result = 31 * result + (archiveName != null ? archiveName.hashCode() : 0);
        result = 31 * result + (archiveVersion != null ? archiveVersion.hashCode() : 0);
        result = 31 * result + (isHighestVersion ? 1 : 0);
        result = 31 * result + (olderVersions != null ? olderVersions.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (lastUpdateDate != null ? lastUpdateDate.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        return result;
    }
}
