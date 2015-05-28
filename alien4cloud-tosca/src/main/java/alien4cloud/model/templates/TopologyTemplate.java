package alien4cloud.model.templates;

import org.elasticsearch.annotation.ESObject;
import org.elasticsearch.annotation.Id;
import org.elasticsearch.annotation.StringField;
import org.elasticsearch.mapping.IndexType;

/**
 *
 * @author mourouvi
 *
 */
@ESObject
public class TopologyTemplate {

    @Id
    private String id;
    @StringField(includeInAll = true, indexType = IndexType.not_analyzed)
    private String name;
    private String description;
    private String topologyId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTopologyId() {
        return topologyId;
    }

    public void setTopologyId(String topologyId) {
        this.topologyId = topologyId;
    }
}
