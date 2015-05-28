package alien4cloud.model.components;

import static alien4cloud.dao.model.FetchContext.QUICK_SEARCH;
import static alien4cloud.dao.model.FetchContext.TAG_SUGGESTION;

import java.util.List;

import org.elasticsearch.annotation.ESObject;
import org.elasticsearch.annotation.NumberField;
import org.elasticsearch.annotation.query.FetchContext;
import org.elasticsearch.annotation.query.TermsFacet;
import org.elasticsearch.mapping.IndexType;

@ESObject
@SuppressWarnings("PMD.UnusedPrivateField")
public class IndexedNodeType extends IndexedArtifactToscaElement {

    @FetchContext(contexts = { QUICK_SEARCH, TAG_SUGGESTION }, include = { false, false })
    @TermsFacet(paths = "type")
    private List<CapabilityDefinition> capabilities;

    @FetchContext(contexts = { QUICK_SEARCH, TAG_SUGGESTION }, include = { false, false })
    @TermsFacet(paths = "type")
    private List<RequirementDefinition> requirements;

    @FetchContext(contexts = { QUICK_SEARCH, TAG_SUGGESTION }, include = { false, false })
    @TermsFacet
    private List<String> defaultCapabilities;

    @NumberField(index = IndexType.not_analyzed, includeInAll = false)
    private long alienScore;

    public List<CapabilityDefinition> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<CapabilityDefinition> capabilities) {
        this.capabilities = capabilities;
    }

    public List<RequirementDefinition> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<RequirementDefinition> requirements) {
        this.requirements = requirements;
    }

    public List<String> getDefaultCapabilities() {
        return defaultCapabilities;
    }

    public void setDefaultCapabilities(List<String> defaultCapabilities) {
        this.defaultCapabilities = defaultCapabilities;
    }

    public long getAlienScore() {
        return alienScore;
    }

    public void setAlienScore(long alienScore) {
        this.alienScore = alienScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexedNodeType)) return false;
        if (!super.equals(o)) return false;

        IndexedNodeType that = (IndexedNodeType) o;

        if (alienScore != that.alienScore) return false;
        if (capabilities != null ? !capabilities.equals(that.capabilities) : that.capabilities != null) return false;
        if (defaultCapabilities != null ? !defaultCapabilities.equals(that.defaultCapabilities) : that.defaultCapabilities != null)
            return false;
        if (requirements != null ? !requirements.equals(that.requirements) : that.requirements != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (capabilities != null ? capabilities.hashCode() : 0);
        result = 31 * result + (requirements != null ? requirements.hashCode() : 0);
        result = 31 * result + (defaultCapabilities != null ? defaultCapabilities.hashCode() : 0);
        result = 31 * result + (int) (alienScore ^ (alienScore >>> 32));
        return result;
    }
}
