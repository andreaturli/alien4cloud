package alien4cloud.model.topology;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import alien4cloud.json.deserializer.PolicyDeserializer;

/**
 * A node group is a group of nodes in a topology. All members share the same policies.
 */
public class NodeGroup {

    private String name;

    private Set<String> members;

    /**
     * The group index for a given topology.
     */
    private int index;

    @JsonDeserialize(contentUsing = PolicyDeserializer.class)
    private List<AbstractPolicy> policies;

    public NodeGroup() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getMembers() {
        return members;
    }

    public void setMembers(Set<String> members) {
        this.members = members;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<AbstractPolicy> getPolicies() {
        return policies;
    }

    public void setPolicies(List<AbstractPolicy> policies) {
        this.policies = policies;
    }
}
