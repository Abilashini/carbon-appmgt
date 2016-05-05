package org.wso2.carbon.appmgt.rest.api.publisher;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.wso2.carbon.appmgt.rest.api.publisher.dto.AppDTO;
import org.wso2.carbon.appmgt.rest.api.publisher.dto.PolicyPartialIdListDTO;
import org.wso2.carbon.appmgt.rest.api.publisher.dto.TagListDTO;

import javax.ws.rs.core.Response;
import java.io.InputStream;

public abstract class AppsApiService {
    public abstract Response appsMobileBinariesPost(InputStream fileInputStream,Attachment fileDetail,String ifMatch,String ifUnmodifiedSince);
    public abstract Response appsMobileBinariesFileNameGet(String fileName,String ifMatch,String ifUnmodifiedSince);
    public abstract Response appsStaticContentsPost(InputStream fileInputStream,Attachment fileDetail,String ifMatch,String ifUnmodifiedSince);
    public abstract Response appsStaticContentsFileNameGet(String fileName,String ifMatch,String ifUnmodifiedSince);
    public abstract Response appsAppTypeGet(String appType,String query,String fieldFilter,Integer limit,Integer offset,String accept,String ifNoneMatch);
    public abstract Response appsAppTypePost(String appType,AppDTO body,String contentType,String ifModifiedSince);
    public abstract Response appsAppTypeChangeLifecyclePost(String appType,String action,String appId,String ifMatch,String ifUnmodifiedSince);
    public abstract Response appsAppTypeIdAppIdGet(String appType,String appId,String accept,String ifNoneMatch,String ifModifiedSince);
    public abstract Response appsAppTypeIdAppIdPut(String appType,String appId,AppDTO body,String contentType,String ifMatch,String ifUnmodifiedSince);
    public abstract Response appsAppTypeIdAppIdDelete(String appType,String appId,String ifMatch,String ifUnmodifiedSince);
    public abstract Response appsAppTypeIdAppIdCreateNewVersionPost(String appType,String appId,String contentType,String ifModifiedSince);
    public abstract Response appsAppTypeIdAppIdLifecycleGet(String appType,String appId,String accept,String ifNoneMatch);
    public abstract Response appsAppTypeIdAppIdLifecycleHistoryGet(String appType,String appId,String accept,String ifNoneMatch);
    public abstract Response appsAppTypeIdAppIdStorageFileNameGet(String appType,String appId,String fileName,String ifMatch,String ifUnmodifiedSince);
    public abstract Response appsAppTypeIdAppIdSubscriptionsGet(String appType,String appId,String accept,String ifNoneMatch,String ifModifiedSince);
    public abstract Response appsAppTypeIdAppIdTagsGet(String appType,String appId,String accept,String ifNoneMatch);
    public abstract Response appsAppTypeIdAppIdTagsPut(String appType,String appId,TagListDTO body,String contentType,String ifMatch,String ifUnmodifiedSince);
    public abstract Response appsAppTypeIdAppIdTagsDelete(String appType,String appId,TagListDTO body,String ifMatch,String ifUnmodifiedSince);
    public abstract Response appsAppTypeIdAppIdThrottlingtiersGet(String appType,String appId,String accept,String ifNoneMatch);
    public abstract Response appsAppTypeIdAppIdXacmlpoliciesGet(String appType,String appId,String accept,String ifNoneMatch);
    public abstract Response appsAppTypeIdAppIdXacmlpoliciesPost(String appType,String appId,PolicyPartialIdListDTO body,String contentType,String ifModifiedSince);
    public abstract Response appsAppTypeIdAppIdXacmlpoliciesPolicyPartialIdDelete(String appType,String appId,Integer policyPartialId,String ifMatch,String ifUnmodifiedSince);
    public abstract Response appsAppTypeTagsGet(String appType,String accept,String ifNoneMatch);
    public abstract Response appsAppTypeValidateContextPost(String appType,String appContext,String contentType,String ifModifiedSince);
}

