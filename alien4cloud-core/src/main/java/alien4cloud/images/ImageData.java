package alien4cloud.images;

import org.elasticsearch.annotation.ESObject;
import org.elasticsearch.annotation.Id;

import lombok.Getter;
import lombok.Setter;

@ESObject
@Getter
@Setter
@SuppressWarnings("PMD.UnusedPrivateField")
public class ImageData {
    @Id
    private String id;
    private byte[] data;
    private String mime;
}
