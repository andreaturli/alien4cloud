package alien4cloud.audit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AuditedMethod extends Method {

    private boolean enabled;

    public AuditedMethod(String path, String method, String category, String action, boolean enabled) {
        super(path, method, category, action);
        this.enabled = enabled;
    }
}
