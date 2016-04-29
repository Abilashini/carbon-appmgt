package org.wso2.carbon.appmgt.rest.api.publisher;

import org.wso2.carbon.appmgt.rest.api.publisher.dto.*;
import org.wso2.carbon.appmgt.rest.api.publisher.AppsApiService;
import org.wso2.carbon.appmgt.rest.api.publisher.factories.AppsApiServiceFactory;

import io.swagger.annotations.ApiParam;

import org.wso2.carbon.appmgt.rest.api.publisher.dto.ErrorDTO;
import java.io.File;
import org.wso2.carbon.appmgt.rest.api.publisher.dto.BinaryDTO;
import org.wso2.carbon.appmgt.rest.api.publisher.dto.StaticContentDTO;
import org.wso2.carbon.appmgt.rest.api.publisher.dto.AppListDTO;
import org.wso2.carbon.appmgt.rest.api.publisher.dto.AppDTO;
import org.wso2.carbon.appmgt.rest.api.publisher.dto.ResponseMessageDTO;
import org.wso2.carbon.appmgt.rest.api.publisher.dto.AppInfoDTO;

import java.util.List;

import java.io.InputStream;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import javax.ws.rs.core.Response;
import javax.ws.rs.*;

@Path("/apps")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(value = "/apps", description = "the apps API")
public class AppsApi  {

   private final AppsApiService delegate = AppsApiServiceFactory.getAppsApi();

    @POST
    @Path("/mobile/binaries")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Uploading binary files", notes = "Uploading .apk/.IPA binary files.", response = BinaryDTO.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. \nBinary file uploaded successfully."),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. \nInvalid request or validation error"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found. \nRequested App does not exist."),
        
        @io.swagger.annotations.ApiResponse(code = 412, message = "Precondition Failed. \nThe request has not been performed because one of the preconditions is not met.") })

    public Response appsMobileBinariesPost(@ApiParam(value = "Document to upload") @Multipart(value = "file", required = false) InputStream fileInputStream,
    @ApiParam(value = "Document to upload : details") @Multipart(value = "file" , required = false) Attachment fileDetail,
    @ApiParam(value = "Validator for conditional requests; based on ETag."  )@HeaderParam("If-Match") String ifMatch,
    @ApiParam(value = "Validator for conditional requests; based on Last Modified header."  )@HeaderParam("If-Unmodified-Since") String ifUnmodifiedSince)
    {
    return delegate.appsMobileBinariesPost(fileInputStream,fileDetail,ifMatch,ifUnmodifiedSince);
    }
    @POST
    @Path("/static-contents")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Uploading images, pdf, documents files", notes = "Uploading images for banners, screenshots etc.", response = StaticContentDTO.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. \nStatic content uploaded successfully."),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. \nInvalid request or validation error"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found. \nRequested entity does not exist."),
        
        @io.swagger.annotations.ApiResponse(code = 412, message = "Precondition Failed. \nThe request has not been performed because one of the preconditions is not met.") })

    public Response appsStaticContentsPost(@ApiParam(value = "Document to upload") @Multipart(value = "file", required = false) InputStream fileInputStream,
    @ApiParam(value = "Document to upload : details") @Multipart(value = "file" , required = false) Attachment fileDetail,
    @ApiParam(value = "Validator for conditional requests; based on ETag."  )@HeaderParam("If-Match") String ifMatch,
    @ApiParam(value = "Validator for conditional requests; based on Last Modified header."  )@HeaderParam("If-Unmodified-Since") String ifUnmodifiedSince)
    {
    return delegate.appsStaticContentsPost(fileInputStream,fileDetail,ifMatch,ifUnmodifiedSince);
    }
    @GET
    @Path("/{appType}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get all Apps", notes = "Get a list of available Apps qualifying under a given search condition.", response = AppListDTO.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. \nList of qualifying Apps is returned."),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. \nInvalid request or validation error."),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden. \nThe request must be conditional but no condition has been specified."),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found. \nThe resource to be updated does not exist.") })

    public Response appsAppTypeGet(@ApiParam(value = "App Type. Either webapp or mobileapp",required=true ) @PathParam("appType") String appType,
    @ApiParam(value = "**Search condition**.\n\n\n\n\nYou can search in attributes by using an **\"attribute:\"** modifier.\n\n\n\n\nEg. \"provider:wso2\" will match an App if the provider of the App contains \"wso2\".\n\n\n\n\nSupported attribute modifiers are [*provider, app_name, app_version, app_id**]\n\n\n\n\nIf no advanced attribute modifier has been specified, search will match the\ngiven query string against App Name.") @QueryParam("query") String query,
    @ApiParam(value = "Extent of the fields of an app to return", defaultValue="basic") @QueryParam("field-filter") String fieldFilter,
    @ApiParam(value = "Maximum size of resource array to return.", defaultValue="25") @QueryParam("limit") Integer limit,
    @ApiParam(value = "Starting point within the complete list of items qualified.", defaultValue="0") @QueryParam("offset") Integer offset,
    @ApiParam(value = "Media types acceptable for the response. Default is JSON."  , defaultValue="JSON")@HeaderParam("Accept") String accept,
    @ApiParam(value = "Validator for conditional requests; based on the ETag of the formerly retrieved\nvariant of the resourec."  )@HeaderParam("If-None-Match") String ifNoneMatch)
    {
    return delegate.appsAppTypeGet(appType,query, fieldFilter, limit,offset,accept,ifNoneMatch);
    }
    @POST
    @Path("/{appType}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Create a new App", notes = "Create a new App", response = AppDTO.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 201, message = "Created. \nSuccessful response with the newly created object as entity in the body. \nLocation header contains URL of newly created entity."),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. \nInvalid request or validation error."),
        
        @io.swagger.annotations.ApiResponse(code = 415, message = "Unsupported Media Type. \nThe entity of the request was in a not supported format.") })

    public Response appsAppTypePost(@ApiParam(value = "App Type. Either webapp or mobileapp",required=true ) @PathParam("appType") String appType,
    @ApiParam(value = "App object that needs to be added" ,required=true ) AppDTO body,
    @ApiParam(value = "Media type of the entity in the body. Default is JSON." ,required=true , defaultValue="JSON")@HeaderParam("Content-Type") String contentType,
    @ApiParam(value = "Validator for conditional requests; based on Last Modified header of the \nformerly retrieved variant of the resource."  )@HeaderParam("If-Modified-Since") String ifModifiedSince)
    {
    return delegate.appsAppTypePost(appType,body,contentType,ifModifiedSince);
    }
    @POST
    @Path("/{appType}/change-lifecycle")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Change App Status", notes = "Change the lifecycle of an App", response = ResponseMessageDTO.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 202, message = "OK. \nLifecycle changed successfully."),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. \nInvalid request or validation error"),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found. \nRequested App does not exist."),
        
        @io.swagger.annotations.ApiResponse(code = 412, message = "Precondition Failed. \nThe request has not been performed because one of the preconditions is not met.") })

    public Response appsAppTypeChangeLifecyclePost(@ApiParam(value = "App Type. Either webapp or mobileapp",required=true ) @PathParam("appType") String appType,
    @ApiParam(value = "The action to demote or promote the state of the App.\n\n\n\n\nSupported actions are [ **Publish,Approve,Reject,Unpublish,Deprecate,Retire,Recycle,Re-Publish,Submit#for#Review **]",required=true, allowableValues="{values=[Publish, Approve, Reject, Unpublish, Deprecate, Retire, Recycle, Re-Publish, Submit#for#Review]}") @QueryParam("action") String action,
    @ApiParam(value = "**appId ID** consisting of the **UUID** of the App. \nThe combination of the provider of the appId, name of the appId and the version is also accepted as a valid App ID.\nShould be formatted as **provider-name-version**.",required=true) @QueryParam("appId") String appId,
    @ApiParam(value = "Validator for conditional requests; based on ETag."  )@HeaderParam("If-Match") String ifMatch,
    @ApiParam(value = "Validator for conditional requests; based on Last Modified header."  )@HeaderParam("If-Unmodified-Since") String ifUnmodifiedSince)
    {
    return delegate.appsAppTypeChangeLifecyclePost(appType,action,appId,ifMatch,ifUnmodifiedSince);
    }
    @GET
    @Path("/{appType}/id/{appId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Get app details", notes = "Get details of an app.", response = AppInfoDTO.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. \nQualifying App is returned."),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. \nInvalid request or validation error."),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden. \nThe request must be conditional but no condition has been specified."),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found. \nThe resource to be updated does not exist.") })

    public Response appsAppTypeIdAppIdGet(@ApiParam(value = "App Type. Either webapp or mobileapp",required=true ) @PathParam("appType") String appType,
    @ApiParam(value = "**APP ID** consisting of the **UUID** of the App. \nThe combination of the provider of the app, name of the appId and the version is also accepted as a valid App ID.\nShould be formatted as **provider-name-version**.",required=true ) @PathParam("appId") String appId,
    @ApiParam(value = "Media types acceptable for the response. Default is JSON."  , defaultValue="JSON")@HeaderParam("Accept") String accept,
    @ApiParam(value = "Validator for conditional requests; based on the ETag of the formerly retrieved\nvariant of the resourec."  )@HeaderParam("If-None-Match") String ifNoneMatch,
    @ApiParam(value = "Validator for conditional requests; based on Last Modified header of the \nformerly retrieved variant of the resource."  )@HeaderParam("If-Modified-Since") String ifModifiedSince)
    {
    return delegate.appsAppTypeIdAppIdGet(appType,appId,accept,ifNoneMatch,ifModifiedSince);
    }
    @PUT
    @Path("/{appType}/id/{appId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Update an existing App", notes = "Update an existing App", response = AppDTO.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. \nSuccessful response with updated App object"),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request. \nInvalid request or validation error"),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden. \nThe request must be conditional but no condition has been specified."),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found. \nThe resource to be updated does not exist."),
        
        @io.swagger.annotations.ApiResponse(code = 412, message = "Precondition Failed. \nThe request has not been performed because one of the preconditions is not met.") })

    public Response appsAppTypeIdAppIdPut(@ApiParam(value = "App Type. Either webapp or mobileapp",required=true ) @PathParam("appType") String appType,
    @ApiParam(value = "**APP ID** consisting of the **UUID** of the App. \nThe combination of the provider of the app, name of the appId and the version is also accepted as a valid App ID.\nShould be formatted as **provider-name-version**.",required=true ) @PathParam("appId") String appId,
    @ApiParam(value = "App object that needs to be added" ,required=true ) AppDTO body,
    @ApiParam(value = "Media type of the entity in the body. Default is JSON." ,required=true , defaultValue="JSON")@HeaderParam("Content-Type") String contentType,
    @ApiParam(value = "Validator for conditional requests; based on ETag."  )@HeaderParam("If-Match") String ifMatch,
    @ApiParam(value = "Validator for conditional requests; based on Last Modified header."  )@HeaderParam("If-Unmodified-Since") String ifUnmodifiedSince)
    {
    return delegate.appsAppTypeIdAppIdPut(appType,appId,body,contentType,ifMatch,ifUnmodifiedSince);
    }
    @DELETE
    @Path("/{appType}/id/{appId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Delete App", notes = "Delete an existing App", response = Void.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. \nResource successfully deleted."),
        
        @io.swagger.annotations.ApiResponse(code = 403, message = "Forbidden. \nThe request must be conditional but no condition has been specified."),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found. \nResource to be deleted does not exist."),
        
        @io.swagger.annotations.ApiResponse(code = 412, message = "Precondition Failed. \nThe request has not been performed because one of the preconditions is not met.") })

    public Response appsAppTypeIdAppIdDelete(@ApiParam(value = "App Type. Either webapp or mobileapp",required=true ) @PathParam("appType") String appType,
    @ApiParam(value = "**APP ID** consisting of the **UUID** of the App. \nThe combination of the provider of the app, name of the appId and the version is also accepted as a valid App ID.\nShould be formatted as **provider-name-version**.",required=true ) @PathParam("appId") String appId,
    @ApiParam(value = "Validator for conditional requests; based on ETag."  )@HeaderParam("If-Match") String ifMatch,
    @ApiParam(value = "Validator for conditional requests; based on Last Modified header."  )@HeaderParam("If-Unmodified-Since") String ifUnmodifiedSince)
    {
    return delegate.appsAppTypeIdAppIdDelete(appType,appId,ifMatch,ifUnmodifiedSince);
    }
}

