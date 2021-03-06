package alien4cloud.component;

import java.util.Date;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import alien4cloud.Constants;
import alien4cloud.dao.IGenericSearchDAO;
import alien4cloud.dao.model.GetMultipleDataResult;
import alien4cloud.model.components.IndexedNodeType;
import alien4cloud.model.topology.Topology;
import alien4cloud.utils.MapUtil;
import alien4cloud.utils.version.Version;

import com.google.common.collect.Maps;

/**
 * Updates the scoring of node types based on their usage, version and default capabilities.
 */
@Slf4j
@Component
public class NodeTypeScoreService implements Runnable {
    /** Every hours. TODO This should be a configuration param. */
    private static final long SCORE_RATE_MILLISECONDS = 1000 * 60 * 60;

    @Resource(name = "alien-es-dao")
    private IGenericSearchDAO alienESDAO;
    @Resource(name = "node-type-score-scheduler")
    private TaskScheduler scheduler;

    @Value("${components.search.boost.usage}")
    private long usageBoost;
    @Value("${components.search.boost.version}")
    private long versionBoost;
    @Value("${components.search.boost.default}")
    private long defaultBoost;

    /** Refresh boost for all indexed node types in the system. */
    @PostConstruct
    public void refreshBoostCompute() {
        Date date = new Date(System.currentTimeMillis() + SCORE_RATE_MILLISECONDS);
        scheduler.scheduleAtFixedRate(this, date, SCORE_RATE_MILLISECONDS);
    }

    @Override
    public void run() {
        log.info("Updating node type scores.");
        // Go over all indexed node types.
        GetMultipleDataResult<IndexedNodeType> getMultipleDataResult = alienESDAO.find(IndexedNodeType.class, null, 0, Constants.DEFAULT_ES_SEARCH_SIZE);
        int from = getMultipleDataResult.getTo() + 1;
        processNodeTypes(getMultipleDataResult.getData());
        while (getMultipleDataResult.getData().length > 0 && from < getMultipleDataResult.getTotalResults()) {
            getMultipleDataResult = alienESDAO.find(IndexedNodeType.class, null, from, from + Constants.DEFAULT_ES_SEARCH_SIZE);
            from = getMultipleDataResult.getTo() + 1;
            processNodeTypes(getMultipleDataResult.getData());
        }
    }

    private void processNodeTypes(IndexedNodeType[] indexedNodeTypes) {
        for (IndexedNodeType nodeType : indexedNodeTypes) {
            Map<String, String[]> usedNodeFiler = Maps.newHashMap();
            usedNodeFiler.put("nodeTemplates.value.type", new String[] { nodeType.getElementId() });
            // count the applications that uses the node-type
            long usageFactor = usageBoost * alienESDAO.count(Topology.class, null, usedNodeFiler);
            // get the version factor (latest version of a node is better than previous version, snapshot versions do not get boost)
            long versionFactor = isLatestVersion(nodeType) ? versionBoost : 0;
            // default boost (boost node types that have a default capability)
            long defaultFactor = nodeType.getDefaultCapabilities() == null || nodeType.getDefaultCapabilities().isEmpty() ? 0 : defaultBoost;
            // update the score for the node type.
            nodeType.setAlienScore(usageFactor + defaultFactor + versionFactor);
            alienESDAO.save(nodeType);
        }
    }

    private boolean isLatestVersion(IndexedNodeType nodeType) {
        Map<String, String[]> filters = MapUtil.newHashMap(new String[] { "elementId" }, new String[][] { new String[] { nodeType.getElementId() } });
        // TODO get a single element and order by version.
        Version nodeVersion = new Version(((IndexedNodeType) nodeType).getArchiveVersion());

        for (Object otherVersionNodeType : alienESDAO.find(IndexedNodeType.class, filters, Constants.DEFAULT_ES_SEARCH_SIZE).getData()) {
            Version otherVersion = new Version(((IndexedNodeType) otherVersionNodeType).getArchiveVersion());
            if (nodeVersion.compareTo(otherVersion) < 0) {
                return false;
            }
        }
        return true;
    }
}
