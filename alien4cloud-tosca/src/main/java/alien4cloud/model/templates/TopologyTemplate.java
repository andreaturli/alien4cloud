package alien4cloud.model.templates;

import org.elasticsearch.annotation.ESObject;
import org.elasticsearch.annotation.Id;
import org.elasticsearch.annotation.StringField;
import org.elasticsearch.mapping.IndexType;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author mourouvi
 *
 */
@Getter
@Setter
@SuppressWarnings("PMD.UnusedPrivateField")
@ESObject
public class TopologyTemplate {

    @Id
    private String id;
    @StringField(includeInAll = true, indexType = IndexType.not_analyzed)
    private String name;
    private String description;
    private String topologyId;

}
