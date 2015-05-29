package alien4cloud.rest.application;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import alien4cloud.application.ApplicationService;
import alien4cloud.application.ApplicationVersionService;
import alien4cloud.audit.annotation.Audit;
import alien4cloud.dao.IGenericSearchDAO;
import alien4cloud.dao.model.FacetedSearchResult;
import alien4cloud.dao.model.GetMultipleDataResult;
import alien4cloud.exception.AlreadyExistException;
import alien4cloud.exception.DeleteLastApplicationVersionException;
import alien4cloud.exception.DeleteReferencedObjectException;
import alien4cloud.model.application.Application;
import alien4cloud.model.application.ApplicationVersion;
import alien4cloud.rest.component.SearchRequest;
import alien4cloud.rest.model.RestResponse;
import alien4cloud.rest.model.RestResponseBuilder;
import alien4cloud.security.model.ApplicationRole;
import alien4cloud.security.AuthorizationUtil;
import alien4cloud.security.model.Role;
import alien4cloud.utils.MapUtil;
import alien4cloud.utils.ReflectionUtil;
import alien4cloud.utils.VersionUtil;
import alien4cloud.utils.version.InvalidVersionException;
import alien4cloud.utils.version.UpdateApplicationVersionException;

import com.google.common.collect.Lists;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/rest/applications/{applicationId:.+}/versions")
@Api(value = "", description = "Manages application's versions")
public class ApplicationVersionController {

    @Resource(name = "alien-es-dao")
    private IGenericSearchDAO alienDAO;
    @Resource
    private ApplicationVersionService appVersionService;
    @Resource
    private ApplicationService applicationService;

    /**
     * Get most recent snapshot application version for an application
     *
     * @param applicationId The application id.
     */
    @ApiOperation(value = "Get the first snapshot application version for an application.", notes = "Return the first snapshot application version for an application. Application role required [ APPLICATION_MANAGER | APPLICATION_USER | APPLICATION_DEVOPS | DEPLOYMENT_MANAGER ]")
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RestResponse<ApplicationVersion> get(@PathVariable String applicationId) {
        Application application = alienDAO.findById(Application.class, applicationId);
        AuthorizationUtil.checkAuthorizationForApplication(application, ApplicationRole.values());
        ApplicationVersion[] versions = appVersionService.getByApplicationId(applicationId);
        ApplicationVersion firstSnapshot = versions[0];
        for (ApplicationVersion current : versions) {
            if (VersionUtil.compare(firstSnapshot.getVersion(), current.getVersion()) > 0) {
                return RestResponseBuilder.<ApplicationVersion> builder().data(current).build();
            }
        }
        return RestResponseBuilder.<ApplicationVersion> builder().data(firstSnapshot).build();
    }

    /**
     * Sort the ApplicationVersion from newest to oldest
     *
     * @param data
     * @return a sorted array of ApplicationVersion
     */
    private ApplicationVersion[] sortArrayOfApplicationVersion(ApplicationVersion[] data) {
        if (data == null || data.length <= 1) {
            return data;
        }
        List<ApplicationVersion> sortedData = Lists.newArrayList(data);
        Collections.sort(sortedData, new Comparator<ApplicationVersion>() {
            @Override
            public int compare(ApplicationVersion left, ApplicationVersion right) {
                return VersionUtil.compare(right.getVersion(), left.getVersion());
            }
        });
        return sortedData.toArray(new ApplicationVersion[data.length]);
    }

    /**
     * Search application versions for a given application id
     *
     * @param applicationId the targeted application id
     * @param searchRequest
     * @return A rest response that contains a {@link FacetedSearchResult} containing application versions for an application id sorted by version
     */
    @ApiOperation(value = "Search application versions", notes = "Returns a search result with that contains application versions matching the request. A application version is returned only if the connected user has at least one application role in [ APPLICATION_MANAGER | APPLICATION_USER | APPLICATION_DEVOPS | DEPLOYMENT_MANAGER ]")
    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public RestResponse<GetMultipleDataResult<ApplicationVersion>> search(@PathVariable String applicationId, @RequestBody SearchRequest searchRequest) {
        Application application = applicationService.getOrFail(applicationId);
        AuthorizationUtil.checkAuthorizationForApplication(application, ApplicationRole.values());
        GetMultipleDataResult<ApplicationVersion> searchResult = alienDAO.search(ApplicationVersion.class, null,
                getApplicationVersionsFilters(applicationId, searchRequest.getQuery()), searchRequest.getFrom(), searchRequest.getSize());
        searchResult.setData(sortArrayOfApplicationVersion(searchResult.getData()));
        return RestResponseBuilder.<GetMultipleDataResult<ApplicationVersion>> builder().data(searchResult).build();
    }

    /**
     * Get application version from it's id
     *
     * @param applicationId The application id
     */
    @ApiOperation(value = "Get an application version based from its id.", notes = "Returns the application version details. Application role required [ APPLICATION_MANAGER | APPLICATION_DEVOPS ]")
    @RequestMapping(value = "/{applicationVersionId:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public RestResponse<ApplicationVersion> getApplicationEnvironment(@PathVariable String applicationId, @PathVariable String applicationVersionId) {
        Application application = applicationService.getOrFail(applicationId);
        AuthorizationUtil.checkAuthorizationForApplication(application, ApplicationRole.values());
        ApplicationVersion applicationVersion = appVersionService.getOrFail(applicationVersionId);
        return RestResponseBuilder.<ApplicationVersion> builder().data(applicationVersion).build();
    }

    /**
     * Create a new application version for an application
     *
     * @param request data to create an application environment
     * @return application environment id
     */
    @ApiOperation(value = "Create a new application version.", notes = "If successfull returns a rest response with the id of the created application version in data. If not successful a rest response with an error content is returned. Role required [ APPLICATIONS_MANAGER ]. "
            + "By default the application version creator will have application roles [APPLICATION_MANAGER]")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    @Audit
    public RestResponse<String> create(@PathVariable String applicationId, @RequestBody ApplicationVersionRequest request) {
        AuthorizationUtil.checkHasOneRoleIn(Role.APPLICATIONS_MANAGER);
        Application application = applicationService.getOrFail(applicationId);
        AuthorizationUtil.hasAuthorizationForApplication(application, ApplicationRole.APPLICATION_MANAGER);
        ApplicationVersion appVersion = appVersionService.createApplicationVersion(applicationId, request.getTopologyId(), request.getVersion(),
                request.getDescription());
        return RestResponseBuilder.<String> builder().data(appVersion.getId()).build();
    }

    /**
     * Update application version
     *
     * @param applicationVersionId
     * @param request
     * @return
     */
    @ApiOperation(value = "Updates by merging the given request into the given application version", notes = "Updates by merging the given request into the given application version. The logged-in user must have the application manager role for this application. Application role required [ APPLICATION_MANAGER ]")
    @RequestMapping(value = "/{applicationVersionId:.+}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Audit
    public RestResponse<Void> update(@PathVariable String applicationVersionId, @RequestBody ApplicationVersionRequest request) {
        ApplicationVersion appVersion = appVersionService.getOrFail(applicationVersionId);

        if (appVersion.isReleased()) {
            throw new UpdateApplicationVersionException("The application version " + appVersion.getId() + " is released and cannot be update.");
        } else if (request.getVersion() != null && !VersionUtil.isValid(request.getVersion())) {
            throw new InvalidVersionException(request.getVersion() + "is not a valid version name");
        } else if (request.getVersion() != null && !appVersion.getVersion().equals(request.getVersion())
                && appVersionService.isApplicationVersionNameExist(appVersion.getApplicationId(), request.getVersion())) {
            throw new AlreadyExistException("An application version already exist for this application with the version :" + applicationVersionId);
        }

        if (request.getVersion() != null) {
            appVersion.setSnapshot(VersionUtil.isSnapshot(request.getVersion()));
            appVersion.setReleased(!appVersion.isSnapshot());
        }

        ReflectionUtil.mergeObject(request, appVersion);
        alienDAO.save(appVersion);
        return RestResponseBuilder.<Void> builder().build();
    }

    /**
     * Delete an application environment based on it's id. Should not be able to delete a deployed version.
     *
     * @param applicationId
     * @param applicationVersionId
     * @return boolean is delete
     */
    @ApiOperation(value = "Delete an application version from its id", notes = "The logged-in user must have the application manager role for this application. Application role required [ APPLICATION_MANAGER ]")
    @RequestMapping(value = "/{applicationVersionId:.+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Audit
    public RestResponse<Boolean> delete(@PathVariable String applicationId, @PathVariable String applicationVersionId) {
        Application application = applicationService.getOrFail(applicationId);
        AuthorizationUtil.checkAuthorizationForApplication(application, ApplicationRole.APPLICATION_MANAGER);
        appVersionService.getOrFail(applicationVersionId);
        if (appVersionService.isApplicationVersionDeployed(applicationVersionId)) {
            throw new DeleteReferencedObjectException("Application version with id <" + applicationVersionId + "> could not be deleted beacause it's used");
        } else if (appVersionService.getByApplicationId(applicationId).length == 1) {
            throw new DeleteLastApplicationVersionException("Application version with id <" + applicationVersionId
                    + "> can't be be deleted beacause it's the last application version.");
        }
        appVersionService.delete(applicationVersionId);
        return RestResponseBuilder.<Boolean> builder().data(true).build();
    }

    /**
     * Filter to search app versions only for an application id
     *
     * @param applicationId
     * @return a filter for application versions
     */
    private Map<String, String[]> getApplicationVersionsFilters(String applicationId, String version) {
        List<String> filterKeys = Lists.newArrayList();
        List<String[]> filterValues = Lists.newArrayList();
        if (applicationId != null) {
            filterKeys.add("applicationId");
            filterValues.add(new String[] { applicationId });
        }
        if (version != null && !version.equals("")) {
            filterKeys.add("version");
            filterValues.add(new String[] { version });
        }
        return MapUtil.newHashMap(filterKeys.toArray(new String[filterKeys.size()]), filterValues.toArray(new String[filterValues.size()][]));
    }

}
