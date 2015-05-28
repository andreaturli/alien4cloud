package alien4cloud.model.components;

import org.elasticsearch.annotation.ESObject;
import org.elasticsearch.annotation.query.TermsFacet;

@ESObject
public class IndexedRelationshipType extends IndexedArtifactToscaElement {
    @TermsFacet
    private String[] validSources;

    @TermsFacet
    private String[] validTargets;

    public String[] getValidSources() {
        return validSources;
    }

    public void setValidSources(String[] validSources) {
        this.validSources = validSources;
    }

    public String[] getValidTargets() {
        return validTargets;
    }

    public void setValidTargets(String[] validTargets) {
        this.validTargets = validTargets;
    }
}
