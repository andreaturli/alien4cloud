package alien4cloud.model.components;

import static alien4cloud.dao.model.FetchContext.COMPONENT_SUMMARY;
import static alien4cloud.dao.model.FetchContext.QUICK_SEARCH;
import static alien4cloud.dao.model.FetchContext.TAG_SUGGESTION;

import java.util.List;
import java.util.Map;

import org.elasticsearch.annotation.query.FetchContext;
import org.elasticsearch.annotation.query.TermsFacet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import alien4cloud.utils.jackson.ConditionalAttributes;
import alien4cloud.utils.jackson.ConditionalOnAttribute;
import alien4cloud.utils.jackson.JSonMapEntryArrayDeSerializer;
import alien4cloud.utils.jackson.JSonMapEntryArraySerializer;

public class IndexedInheritableToscaElement extends IndexedToscaElement {

    @TermsFacet
    private boolean isAbstract;

    @FetchContext(contexts = { QUICK_SEARCH, TAG_SUGGESTION }, include = { false, false })
    @TermsFacet
    private List<String> derivedFrom;

    @ConditionalOnAttribute(ConditionalAttributes.REST)
    @JsonDeserialize(using = JSonMapEntryArrayDeSerializer.class)
    @JsonSerialize(using = JSonMapEntryArraySerializer.class)
    @FetchContext(contexts = { COMPONENT_SUMMARY, QUICK_SEARCH, TAG_SUGGESTION }, include = { false, false, false })
    private Map<String, PropertyDefinition> properties;

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public List<String> getDerivedFrom() {
        return derivedFrom;
    }

    public void setDerivedFrom(List<String> derivedFrom) {
        this.derivedFrom = derivedFrom;
    }

    public Map<String, PropertyDefinition> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, PropertyDefinition> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexedInheritableToscaElement)) return false;

        IndexedInheritableToscaElement that = (IndexedInheritableToscaElement) o;

        if (isAbstract != that.isAbstract) return false;
        if (derivedFrom != null ? !derivedFrom.equals(that.derivedFrom) : that.derivedFrom != null) return false;
        if (properties != null ? !properties.equals(that.properties) : that.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (isAbstract ? 1 : 0);
        result = 31 * result + (derivedFrom != null ? derivedFrom.hashCode() : 0);
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }
}
