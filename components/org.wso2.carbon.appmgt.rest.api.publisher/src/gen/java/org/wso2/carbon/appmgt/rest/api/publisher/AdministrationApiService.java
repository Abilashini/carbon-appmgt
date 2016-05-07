package org.wso2.carbon.appmgt.rest.api.publisher;

import org.wso2.carbon.appmgt.rest.api.publisher.*;
import org.wso2.carbon.appmgt.rest.api.publisher.dto.*;

import org.wso2.carbon.appmgt.rest.api.publisher.dto.ErrorDTO;
import org.wso2.carbon.appmgt.rest.api.publisher.dto.BusinessOwnerListDTO;
import org.wso2.carbon.appmgt.rest.api.publisher.dto.BusinessOwnerDTO;
import org.wso2.carbon.appmgt.rest.api.publisher.dto.AppListDTO;
import org.wso2.carbon.appmgt.rest.api.publisher.dto.PolicyPartialDTO;

import java.util.List;

import java.io.InputStream;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import javax.ws.rs.core.Response;

public abstract class AdministrationApiService {
    public abstract Response administrationBusinessownerGet(String accept,String ifNoneMatch);
    public abstract Response administrationBusinessownerPost(BusinessOwnerDTO body,String contentType,String ifModifiedSince);
    public abstract Response administrationBusinessownerBusinessOwnerIdGet(String businessOwnerId,String accept,String ifNoneMatch);
    public abstract Response administrationBusinessownerBusinessOwnerIdPut(String businessOwnerId,BusinessOwnerDTO body,String contentType,String ifMatch,String ifUnmodifiedSince);
    public abstract Response administrationBusinessownerBusinessOwnerIdDelete(String businessOwnerId,String ifMatch,String ifUnmodifiedSince);
    public abstract Response administrationPolicygroupsPolicyGroupIdAppsGet(Integer policyGroupId,String accept,String ifNoneMatch,String ifModifiedSince);
    public abstract Response administrationXacmlpoliciesPost(PolicyPartialDTO body,String contentType,String ifModifiedSince);
    public abstract Response administrationXacmlpoliciesValidatePost(PolicyPartialDTO body,String contentType,String ifModifiedSince);
    public abstract Response administrationXacmlpoliciesPolicyPartialIdGet(Integer policyPartialId,String accept,String ifNoneMatch,String ifModifiedSince);
    public abstract Response administrationXacmlpoliciesPolicyPartialIdPut(Integer policyPartialId,PolicyPartialDTO body,String contentType,String ifMatch,String ifUnmodifiedSince);
    public abstract Response administrationXacmlpoliciesPolicyPartialIdDelete(Integer policyPartialId,String ifMatch,String ifUnmodifiedSince);
}

