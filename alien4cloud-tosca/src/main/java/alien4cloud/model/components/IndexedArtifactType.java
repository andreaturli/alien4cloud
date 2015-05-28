package alien4cloud.model.components;

import java.util.List;

import org.elasticsearch.annotation.ESObject;
import org.elasticsearch.annotation.StringField;
import org.elasticsearch.annotation.query.TermFilter;
import org.elasticsearch.mapping.IndexType;

@ESObject
@SuppressWarnings("PMD.UnusedPrivateField")
public class IndexedArtifactType extends IndexedInheritableToscaElement {
    private String mimeType;
    @TermFilter
    @StringField(indexType = IndexType.not_analyzed)
    private List<String> fileExt;

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public List<String> getFileExt() {
        return fileExt;
    }

    public void setFileExt(List<String> fileExt) {
        this.fileExt = fileExt;
    }
}
