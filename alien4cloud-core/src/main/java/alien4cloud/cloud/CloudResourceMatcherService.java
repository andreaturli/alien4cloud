package alien4cloud.cloud;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import alien4cloud.model.cloud.ActivableComputeTemplate;
import alien4cloud.model.cloud.AvailabilityZone;
import alien4cloud.model.cloud.Cloud;
import alien4cloud.model.cloud.CloudImage;
import alien4cloud.model.cloud.CloudImageFlavor;
import alien4cloud.model.cloud.CloudResourceMatcherConfig;
import alien4cloud.model.cloud.CloudResourceType;
import alien4cloud.model.cloud.ComputeTemplate;
import alien4cloud.model.cloud.NetworkTemplate;
import alien4cloud.model.cloud.StorageTemplate;
import alien4cloud.model.components.AbstractPropertyValue;
import alien4cloud.model.components.IndexedNodeType;
import alien4cloud.model.components.ScalarPropertyValue;
import alien4cloud.model.topology.NodeGroup;
import alien4cloud.model.topology.NodeTemplate;
import alien4cloud.model.topology.Topology;
import alien4cloud.paas.IPaaSProvider;
import alien4cloud.paas.function.FunctionEvaluator;
import alien4cloud.tosca.ToscaUtils;
import alien4cloud.tosca.normative.NormativeBlockStorageConstants;
import alien4cloud.tosca.normative.NormativeComputeConstants;
import alien4cloud.tosca.normative.NormativeNetworkConstants;
import alien4cloud.tosca.normative.Size;
import alien4cloud.tosca.normative.SizeUnit;
import alien4cloud.tosca.normative.ToscaType;
import alien4cloud.utils.MappingUtil;
import alien4cloud.utils.version.Version;
import alien4cloud.utils.version.VersionUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CloudResourceMatcherService {

    @Resource
    private CloudImageService cloudImageService;

    @Resource
    private CloudService cloudService;

    @Getter
    @Setter
    public static class MatchableTemplates {

        private Map<String, NodeTemplate> computeTemplates = Maps.newHashMap();

        private Map<String, NodeTemplate> networkTemplates = Maps.newHashMap();

        private Map<String, NodeTemplate> storageTemplates = Maps.newHashMap();
    }

    /**
     * This method browse topology's node templates and return those that need to be matched to cloud's resources
     *
     * @param topology the topology to check
     * @return all node template that must be matched
     */
    public MatchableTemplates getMatchableTemplates(Topology topology, Map<String, IndexedNodeType> types) {
        Map<String, NodeTemplate> allNodeTemplates = topology.getNodeTemplates();
        MatchableTemplates matchableNodeTemplates = new MatchableTemplates();
        if (allNodeTemplates == null) {
            return matchableNodeTemplates;
        }
        for (Map.Entry<String, NodeTemplate> nodeTemplateEntry : allNodeTemplates.entrySet()) {
            if (ToscaUtils.isFromType(NormativeComputeConstants.COMPUTE_TYPE, types.get(nodeTemplateEntry.getKey()))) {
                matchableNodeTemplates.computeTemplates.put(nodeTemplateEntry.getKey(), nodeTemplateEntry.getValue());
            }
            if (ToscaUtils.isFromType(NormativeNetworkConstants.NETWORK_TYPE, types.get(nodeTemplateEntry.getKey()))) {
                matchableNodeTemplates.networkTemplates.put(nodeTemplateEntry.getKey(), nodeTemplateEntry.getValue());
            }
            if (ToscaUtils.isFromType(NormativeBlockStorageConstants.BLOCKSTORAGE_TYPE, types.get(nodeTemplateEntry.getKey()))) {
                matchableNodeTemplates.storageTemplates.put(nodeTemplateEntry.getKey(), nodeTemplateEntry.getValue());
            }
        }
        return matchableNodeTemplates;
    }

    /**
     * Match a topology to cloud resources and return cloud's matched resources each matchable resource of the topology
     *
     * @param topology the topology to check
     * @param cloud the cloud
     * @param paaSProvider the paaS provider
     * @param cloudResourceMatcherConfig the matcher configuration
     * @param types the tosca types of all node of the topology
     * @return match result which contains the images that can be used, the flavors that can be used and their possible association
     */
    public CloudResourceTopologyMatchResult matchTopology(Topology topology, Cloud cloud, IPaaSProvider paaSProvider,
            CloudResourceMatcherConfig cloudResourceMatcherConfig, Map<String, IndexedNodeType> types) {
        MatchableTemplates matchableNodes = getMatchableTemplates(topology, types);
        Map<String, Collection<ComputeTemplate>> templateMatchResult = Maps.newHashMap();
        Map<String, Collection<NetworkTemplate>> networkMatchResult = Maps.newHashMap();
        Map<String, Collection<StorageTemplate>> storageMatchResult = Maps.newHashMap();
        Set<String> imageIds = Sets.newHashSet();
        Map<String, CloudImageFlavor> flavorMap = Maps.newHashMap();
        for (Map.Entry<String, NodeTemplate> computeTemplateEntry : matchableNodes.computeTemplates.entrySet()) {
            List<CloudImage> eligibleImages = matchImages(cloud, cloudResourceMatcherConfig, computeTemplateEntry.getValue());
            List<ComputeTemplate> eligibleTemplates = Lists.newArrayList();
            for (CloudImage eligibleImage : eligibleImages) {
                List<CloudImageFlavor> flavors = matchFlavors(cloud, cloudResourceMatcherConfig, paaSProvider, computeTemplateEntry.getValue(), eligibleImage);
                for (CloudImageFlavor flavor : flavors) {
                    imageIds.add(eligibleImage.getId());
                    flavorMap.put(flavor.getId(), flavor);
                    List<ActivableComputeTemplate> computeTemplates = cloud.getComputeTemplates();
                    for (ActivableComputeTemplate computeTemplate : computeTemplates) {
                        if (computeTemplate.isEnabled() && computeTemplate.getCloudImageId().equals(eligibleImage.getId())
                                && computeTemplate.getCloudImageFlavorId().equals(flavor.getId())) {
                            eligibleTemplates.add(new ComputeTemplate(computeTemplate.getCloudImageId(), computeTemplate.getCloudImageFlavorId(),
                                    computeTemplate.getDescription()));
                        }
                    }
                }
            }
            templateMatchResult.put(computeTemplateEntry.getKey(), eligibleTemplates);
        }
        for (Map.Entry<String, NodeTemplate> networkTemplateEntry : matchableNodes.networkTemplates.entrySet()) {
            networkMatchResult.put(networkTemplateEntry.getKey(), matchNetworks(cloud, networkTemplateEntry.getValue()));
        }
        for (Map.Entry<String, NodeTemplate> storageTemplateEntry : matchableNodes.storageTemplates.entrySet()) {
            storageMatchResult.put(storageTemplateEntry.getKey(), matchStorages(cloud, storageTemplateEntry.getValue()));
        }
        Set<AvailabilityZone> availabilityZones = Sets.newHashSet();
        for (AvailabilityZone zone : cloud.getAvailabilityZones()) {
            if (cloud.getAvailabilityZoneMapping() != null && cloud.getAvailabilityZoneMapping().containsKey(zone.getId())) {
                availabilityZones.add(zone);
            }
        }
        Map<String, Collection<AvailabilityZone>> availabilityZoneMatchResult = Maps.newHashMap();
        Map<String, NodeGroup> nodeGroups = topology.getGroups();
        if (nodeGroups != null) {
            for (Map.Entry<String, NodeGroup> nodeGroupEntry : nodeGroups.entrySet()) {
                availabilityZoneMatchResult.put(nodeGroupEntry.getKey(), availabilityZones);
            }
        }
        return new CloudResourceTopologyMatchResult(cloudImageService.getMultiple(imageIds), flavorMap, templateMatchResult, storageMatchResult,
                networkMatchResult, availabilityZoneMatchResult);
    }

    public List<NetworkTemplate> matchNetworks(Cloud cloud, NodeTemplate nodeTemplate) {
        Map<String, AbstractPropertyValue> networkProperties = nodeTemplate.getProperties();
        Set<NetworkTemplate> existingNetworks = cloud.getNetworks();
        List<NetworkTemplate> eligibleNetworks = Lists.newArrayList();
        for (NetworkTemplate network : existingNetworks) {
            if (!match(networkProperties, NormativeNetworkConstants.CIDR, network.getCidr(), new TextValueParser(), new EqualMatcher<String>())) {
                continue;
            }
            if (!match(networkProperties, NormativeNetworkConstants.IP_VERSION, network.getIpVersion(), new IntegerValueParser(), new EqualMatcher<Integer>())) {
                continue;
            }
            if (!match(networkProperties, NormativeNetworkConstants.GATEWAY_IP, network.getGatewayIp(), new TextValueParser(), new EqualMatcher<String>())) {
                continue;
            }
            if (!match(networkProperties, NormativeNetworkConstants.NETWORK_NAME, network.getId(), new TextValueParser(), new EqualMatcher<String>())) {
                continue;
            }
            eligibleNetworks.add(network);
        }
        return eligibleNetworks;
    }

    public List<StorageTemplate> matchStorages(Cloud cloud, NodeTemplate nodeTemplate) {
        Map<String, AbstractPropertyValue> storageProperties = nodeTemplate.getProperties();
        Set<StorageTemplate> existingStorages = cloud.getStorages();
        List<StorageTemplate> eligibleStorages = Lists.newArrayList();
        for (StorageTemplate storage : existingStorages) {
            if (!match(storageProperties, NormativeBlockStorageConstants.DEVICE, storage.getDevice(), new TextValueParser(), new EqualMatcher<String>())) {
                continue;
            }
            if (!match(storageProperties, NormativeBlockStorageConstants.SIZE, new Size(storage.getSize(), SizeUnit.B), new SizeValueParser(),
                    new GreaterOrEqualValueMatcher<Size>())) {
                continue;
            }
            eligibleStorages.add(storage);
        }
        return eligibleStorages;
    }

    /**
     * Get all available image for a compute by filtering with its properties on OS information
     *
     * @param cloud the cloud
     * @param nodeTemplate the compute to search for images
     * @return the available images on the cloud
     */
    public List<CloudImage> matchImages(Cloud cloud, CloudResourceMatcherConfig cloudResourceMatcherConfig, NodeTemplate nodeTemplate) {
        Map<String, AbstractPropertyValue> computeTemplateProperties = nodeTemplate.getProperties();
        Map<String, CloudImage> availableImages = cloudImageService.getMultiple(cloud.getImages());
        List<CloudImage> matchedImages = Lists.newArrayList();
        for (CloudImage cloudImage : availableImages.values()) {
            if (!cloudResourceMatcherConfig.getImageMapping().containsKey(cloudImage)) {
                // Only consider matched resources
                continue;
            }
            if (!match(computeTemplateProperties, NormativeComputeConstants.OS_ARCH, cloudImage.getOsArch(), new TextValueParser(),
                    new StringIgnoreCaseEqualMatcher())) {
                continue;
            }
            if (!match(computeTemplateProperties, NormativeComputeConstants.OS_DISTRIBUTION, cloudImage.getOsDistribution(), new TextValueParser(),
                    new StringIgnoreCaseEqualMatcher())) {
                continue;
            }
            if (!match(computeTemplateProperties, NormativeComputeConstants.OS_TYPE, cloudImage.getOsType(), new TextValueParser(),
                    new StringIgnoreCaseEqualMatcher())) {
                continue;
            }
            if (!match(computeTemplateProperties, NormativeComputeConstants.OS_VERSION, VersionUtil.parseVersion(cloudImage.getOsVersion()),
                    new VersionValueParser(), new GreaterOrEqualValueMatcher<Version>())) {
                continue;
            }
            List<ActivableComputeTemplate> computeTemplates = cloudService.getComputeTemplates(cloud, cloudImage.getId(), null, false);
            if (!computeTemplates.isEmpty()) {
                for (ActivableComputeTemplate computeTemplate : computeTemplates) {
                    if (computeTemplate.isEnabled()) {
                        matchedImages.add(cloudImage);
                        break;
                    }
                }
            }
        }

        Collections.sort(matchedImages, new Comparator<CloudImage>() {
            @Override
            public int compare(CloudImage left, CloudImage right) {
                return VersionUtil.compare(left.getOsVersion(), right.getOsVersion());
            }
        });

        return matchedImages;
    }

    /**
     * Get all available flavors for the given node and the given image on the given cloud
     *
     * @param cloud the cloud
     * @param cloudResourceMatcherConfig the resource matcher configuration for the cloud
     * @param paaSProvider the paaS provider
     * @param nodeTemplate the compute to search for flavors
     * @param cloudImage the image
     * @return the available flavors for the compute and the image on the given cloud
     */
    public List<CloudImageFlavor> matchFlavors(Cloud cloud, CloudResourceMatcherConfig cloudResourceMatcherConfig, IPaaSProvider paaSProvider,
            NodeTemplate nodeTemplate, CloudImage cloudImage) {
        String paaSImageId = cloudResourceMatcherConfig.getImageMapping().get(cloudImage);
        if (paaSImageId == null) {
            // The image is not matched
            return Lists.newArrayList();
        }
        // From the image, try to get all flavors that are matched and compatible with the image
        String[] paaSFlavorIds = paaSProvider.getAvailableResourceIds(CloudResourceType.FLAVOR, paaSImageId);
        Set<CloudImageFlavor> eligibleFlavors = Sets.newHashSet();
        if (paaSFlavorIds != null) {
            Map<String, CloudImageFlavor> flavorReverseMapping = MappingUtil.getReverseMapping(cloudResourceMatcherConfig.getFlavorMapping());
            for (String paaSFlavorId : paaSFlavorIds) {
                CloudImageFlavor eligibleFlavor = flavorReverseMapping.get(paaSFlavorId);
                if (eligibleFlavor != null) {
                    eligibleFlavors.add(eligibleFlavor);
                }
            }
        } else {
            List<ActivableComputeTemplate> allTemplates = cloud.getComputeTemplates();
            Map<String, CloudImageFlavor> allFlavors = Maps.newHashMap();
            for (CloudImageFlavor flavor : cloud.getFlavors()) {
                allFlavors.put(flavor.getId(), flavor);
            }
            for (ActivableComputeTemplate template : allTemplates) {
                if (template.isEnabled() && template.getCloudImageId().equals(cloudImage.getId())) {
                    // get flavors that correspond to the given cloud image from all active compute template
                    eligibleFlavors.add(allFlavors.get(template.getCloudImageFlavorId()));
                }
            }
        }
        List<CloudImageFlavor> matchedFlavors = Lists.newArrayList();
        Map<String, AbstractPropertyValue> computeTemplateProperties = nodeTemplate.getProperties();
        for (CloudImageFlavor flavor : eligibleFlavors) {
            if (!match(computeTemplateProperties, NormativeComputeConstants.NUM_CPUS, flavor.getNumCPUs(), new IntegerValueParser(),
                    new GreaterOrEqualValueMatcher<Integer>())) {
                continue;
            }
            if (!match(computeTemplateProperties, NormativeComputeConstants.DISK_SIZE, new Size(flavor.getDiskSize(), SizeUnit.B), new SizeValueParser(),
                    new GreaterOrEqualValueMatcher<Size>())) {
                continue;
            }
            if (!match(computeTemplateProperties, NormativeComputeConstants.MEM_SIZE, new Size(flavor.getMemSize(), SizeUnit.B), new SizeValueParser(),
                    new GreaterOrEqualValueMatcher<Size>())) {
                continue;
            }
            List<ActivableComputeTemplate> computeTemplates = cloudService.getComputeTemplates(cloud, cloudImage.getId(), flavor.getId(), false);
            if (!computeTemplates.isEmpty()) {
                for (ActivableComputeTemplate computeTemplate : computeTemplates) {
                    if (computeTemplate.isEnabled()) {
                        matchedFlavors.add(flavor);
                        break;
                    }
                }
            }
        }
        Collections.sort(matchedFlavors);
        return matchedFlavors;
    }

    private interface ValueParser<T> {

        T parseValue(String textValue);

    }

    private static class IntegerValueParser implements ValueParser<Integer> {
        @Override
        public Integer parseValue(String textValue) {
            return Integer.parseInt(textValue);
        }
    }

    private static class SizeValueParser implements ValueParser<Size> {
        @Override
        @SneakyThrows
        public Size parseValue(String textValue) {
            return ToscaType.SIZE_TYPE.parse(textValue);
        }
    }

    private static class TextValueParser implements ValueParser<String> {
        @Override
        public String parseValue(String textValue) {
            return textValue;
        }
    }

    private static class VersionValueParser implements ValueParser<Version> {
        @Override
        public Version parseValue(String textValue) {
            return VersionUtil.parseVersion(textValue);
        }
    }

    private interface ValueMatcher<T> {

        boolean matchValue(T expected, T actual);

    }

    private static class GreaterOrEqualValueMatcher<T extends Comparable<? super T>> implements ValueMatcher<T> {
        @Override
        public boolean matchValue(T expected, T actual) {
            // Actual must be greater or equal than expected
            return actual.compareTo(expected) >= 0;
        }
    }

    private static class EqualMatcher<T> implements ValueMatcher<T> {
        @Override
        public boolean matchValue(T expected, T actual) {
            // Actual must be equal strictly as expected
            return expected.equals(actual);
        }
    }

    private static class StringIgnoreCaseEqualMatcher implements ValueMatcher<String> {
        @Override
        public boolean matchValue(String expected, String actual) {
            return expected.equalsIgnoreCase(actual);
        }
    }

    private <T> boolean match(Map<String, AbstractPropertyValue> properties, String keyToCheck, T actualValue, ValueParser<T> valueParser,
            ValueMatcher<T> valueMatcher) {
        try {
            if (properties == null) {
                return true;
            }
            AbstractPropertyValue propertyValue = properties.get(keyToCheck);
            String expectedStrValue = null;
            if (propertyValue instanceof ScalarPropertyValue) {
                expectedStrValue = FunctionEvaluator.getScalarValue(propertyValue);
            }
            return expectedStrValue == null || expectedStrValue.isEmpty() || valueMatcher.matchValue(valueParser.parseValue(expectedStrValue), actualValue);
        } catch (Exception e) {
            log.warn("Error happened while matching key [" + keyToCheck + "], actual value [" + actualValue + "], properties [" + properties + "]", e);
            return false;
        }
    }
}
