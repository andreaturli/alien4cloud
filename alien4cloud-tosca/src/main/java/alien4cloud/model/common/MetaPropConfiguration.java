package alien4cloud.model.common;

import javax.validation.constraints.NotNull;

import org.elasticsearch.annotation.ESObject;
import org.elasticsearch.annotation.Id;
import org.elasticsearch.annotation.StringField;
import org.elasticsearch.annotation.query.TermFilter;
import org.elasticsearch.annotation.query.TermsFacet;
import org.elasticsearch.mapping.IndexType;

import com.fasterxml.jackson.annotation.JsonInclude;

import alien4cloud.model.components.PropertyDefinition;
import alien4cloud.ui.form.annotation.FormLabel;
import alien4cloud.ui.form.annotation.FormProperties;
import alien4cloud.ui.form.annotation.FormValidValues;

/**
 * Predefined configuration for tag edit
 */
@ESObject
@SuppressWarnings("PMD.UnusedPrivateField")
@JsonInclude(JsonInclude.Include.NON_NULL)
@FormProperties({ "name", "description", "required", "target", "type", "password", "default", "constraints" })
public class MetaPropConfiguration extends PropertyDefinition {
    /**
     * Auto generated id
     */
    @Id
    private String id;

    /**
     * The name of the tag
     */
    @TermFilter
    @StringField(includeInAll = true, indexType = IndexType.not_analyzed)
    @NotNull
    @FormLabel("TAG_CONFIG.NAME")
    private String name;

    /**
     * Target of the tag configuration (application or component or cloud)
     */
    @StringField(includeInAll = true, indexType = IndexType.not_analyzed)
    @FormValidValues({ "application", "component", "cloud" })
    @NotNull
    @TermsFacet
    @FormLabel("TAG_CONFIG.TARGET")
    private String target;

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

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
