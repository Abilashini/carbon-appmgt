package org.wso2.carbon.appmgt.rest.api.publisher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;



@ApiModel(description = "")
public class AppDTO  {
  
  
  
  private String id = null;
  
  @NotNull
  private String name = null;
  
  
  private String type = null;
  
  
  private String isSite = null;
  
  
  private String description = null;
  
  
  private String context = null;
  
  @NotNull
  private String version = null;
  
  
  private String provider = null;
  
  @NotNull
  private String appDefinition = null;
  
  
  private Boolean isDefaultVersion = null;
  
  @NotNull
  private List<String> transport = new ArrayList<String>();
  
  
  private List<String> tags = new ArrayList<String>();
  
  
  private String thumbnailUrl = null;
  
  
  private List<String> visibleRoles = new ArrayList<String>();
  
  
  private String path = null;
  
  
  private String resourceId = null;
  
  
  private String lifecycle = null;
  
  
  private String lifecycleState = null;
  
  
  private String appUrL = null;
  
  
  private String bundleversion = null;
  
  
  private String packagename = null;
  
  
  private String category = null;
  
  
  private String displayName = null;
  
  
  private String screenshots = null;
  
  
  private String banner = null;
  
  
  private String createdtime = null;
  
  
  private String platform = null;
  
  
  private String lifecycleAvailableActions = null;

  
  /**
   * UUID of the app registry artifact
   **/
  @ApiModelProperty(value = "UUID of the app registry artifact")
  @JsonProperty("id")
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }

  
  /**
   **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("name")
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  
  /**
   * App type (either Webapp/Mobile app
   **/
  @ApiModelProperty(value = "App type (either Webapp/Mobile app")
  @JsonProperty("type")
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }

  
  /**
   * Either a webapp or site
   **/
  @ApiModelProperty(value = "Either a webapp or site")
  @JsonProperty("isSite")
  public String getIsSite() {
    return isSite;
  }
  public void setIsSite(String isSite) {
    this.isSite = isSite;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("context")
  public String getContext() {
    return context;
  }
  public void setContext(String context) {
    this.context = context;
  }

  
  /**
   **/
  @ApiModelProperty(required = true, value = "")
  @JsonProperty("version")
  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
  }

  
  /**
   * If the provider value is not given user invoking the api will be used as the provider.
   **/
  @ApiModelProperty(value = "If the provider value is not given user invoking the api will be used as the provider.")
  @JsonProperty("provider")
  public String getProvider() {
    return provider;
  }
  public void setProvider(String provider) {
    this.provider = provider;
  }

  
  /**
   * Swagger definition of the App which contains details about URI templates and scopes
   **/
  @ApiModelProperty(required = true, value = "Swagger definition of the App which contains details about URI templates and scopes")
  @JsonProperty("appDefinition")
  public String getAppDefinition() {
    return appDefinition;
  }
  public void setAppDefinition(String appDefinition) {
    this.appDefinition = appDefinition;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("isDefaultVersion")
  public Boolean getIsDefaultVersion() {
    return isDefaultVersion;
  }
  public void setIsDefaultVersion(Boolean isDefaultVersion) {
    this.isDefaultVersion = isDefaultVersion;
  }

  
  /**
   * Supported transports for the App (http and/or https).
   **/
  @ApiModelProperty(required = true, value = "Supported transports for the App (http and/or https).")
  @JsonProperty("transport")
  public List<String> getTransport() {
    return transport;
  }
  public void setTransport(List<String> transport) {
    this.transport = transport;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("tags")
  public List<String> getTags() {
    return tags;
  }
  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("thumbnailUrl")
  public String getThumbnailUrl() {
    return thumbnailUrl;
  }
  public void setThumbnailUrl(String thumbnailUrl) {
    this.thumbnailUrl = thumbnailUrl;
  }

  
  /**
   **/
  @ApiModelProperty(value = "")
  @JsonProperty("visibleRoles")
  public List<String> getVisibleRoles() {
    return visibleRoles;
  }
  public void setVisibleRoles(List<String> visibleRoles) {
    this.visibleRoles = visibleRoles;
  }

  
  /**
   * Registry path of the asset
   **/
  @ApiModelProperty(value = "Registry path of the asset")
  @JsonProperty("path")
  public String getPath() {
    return path;
  }
  public void setPath(String path) {
    this.path = path;
  }

  
  /**
   * Resource Id path of the asset
   **/
  @ApiModelProperty(value = "Resource Id path of the asset")
  @JsonProperty("resourceId")
  public String getResourceId() {
    return resourceId;
  }
  public void setResourceId(String resourceId) {
    this.resourceId = resourceId;
  }

  
  /**
   * lifecycle type of the asset
   **/
  @ApiModelProperty(value = "lifecycle type of the asset")
  @JsonProperty("lifecycle")
  public String getLifecycle() {
    return lifecycle;
  }
  public void setLifecycle(String lifecycle) {
    this.lifecycle = lifecycle;
  }

  
  /**
   * lifecycle state of the asset
   **/
  @ApiModelProperty(value = "lifecycle state of the asset")
  @JsonProperty("lifecycleState")
  public String getLifecycleState() {
    return lifecycleState;
  }
  public void setLifecycleState(String lifecycleState) {
    this.lifecycleState = lifecycleState;
  }

  
  /**
   * URL of the asset
   **/
  @ApiModelProperty(value = "URL of the asset")
  @JsonProperty("appUrL")
  public String getAppUrL() {
    return appUrL;
  }
  public void setAppUrL(String appUrL) {
    this.appUrL = appUrL;
  }

  
  /**
   * Bundleversion of the asset
   **/
  @ApiModelProperty(value = "Bundleversion of the asset")
  @JsonProperty("bundleversion")
  public String getBundleversion() {
    return bundleversion;
  }
  public void setBundleversion(String bundleversion) {
    this.bundleversion = bundleversion;
  }

  
  /**
   * packagename of the asset
   **/
  @ApiModelProperty(value = "packagename of the asset")
  @JsonProperty("packagename")
  public String getPackagename() {
    return packagename;
  }
  public void setPackagename(String packagename) {
    this.packagename = packagename;
  }

  
  /**
   * category of the asset
   **/
  @ApiModelProperty(value = "category of the asset")
  @JsonProperty("category")
  public String getCategory() {
    return category;
  }
  public void setCategory(String category) {
    this.category = category;
  }

  
  /**
   * displayName of the asset
   **/
  @ApiModelProperty(value = "displayName of the asset")
  @JsonProperty("displayName")
  public String getDisplayName() {
    return displayName;
  }
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  
  /**
   * packagename of the asset
   **/
  @ApiModelProperty(value = "packagename of the asset")
  @JsonProperty("screenshots")
  public String getScreenshots() {
    return screenshots;
  }
  public void setScreenshots(String screenshots) {
    this.screenshots = screenshots;
  }

  
  /**
   * /publisher/api/mobileapp/getfile/uWnObGDXigTO7pl.jpg
   **/
  @ApiModelProperty(value = "/publisher/api/mobileapp/getfile/uWnObGDXigTO7pl.jpg")
  @JsonProperty("banner")
  public String getBanner() {
    return banner;
  }
  public void setBanner(String banner) {
    this.banner = banner;
  }

  
  /**
   * createdtime of the asset
   **/
  @ApiModelProperty(value = "createdtime of the asset")
  @JsonProperty("createdtime")
  public String getCreatedtime() {
    return createdtime;
  }
  public void setCreatedtime(String createdtime) {
    this.createdtime = createdtime;
  }

  
  /**
   * platform of the asset
   **/
  @ApiModelProperty(value = "platform of the asset")
  @JsonProperty("platform")
  public String getPlatform() {
    return platform;
  }
  public void setPlatform(String platform) {
    this.platform = platform;
  }

  
  /**
   * platform of the asset
   **/
  @ApiModelProperty(value = "platform of the asset")
  @JsonProperty("lifecycleAvailableActions")
  public String getLifecycleAvailableActions() {
    return lifecycleAvailableActions;
  }
  public void setLifecycleAvailableActions(String lifecycleAvailableActions) {
    this.lifecycleAvailableActions = lifecycleAvailableActions;
  }

  

  @Override
  public String toString()  {
    StringBuilder sb = new StringBuilder();
    sb.append("class AppDTO {\n");
    
    sb.append("  id: ").append(id).append("\n");
    sb.append("  name: ").append(name).append("\n");
    sb.append("  type: ").append(type).append("\n");
    sb.append("  isSite: ").append(isSite).append("\n");
    sb.append("  description: ").append(description).append("\n");
    sb.append("  context: ").append(context).append("\n");
    sb.append("  version: ").append(version).append("\n");
    sb.append("  provider: ").append(provider).append("\n");
    sb.append("  appDefinition: ").append(appDefinition).append("\n");
    sb.append("  isDefaultVersion: ").append(isDefaultVersion).append("\n");
    sb.append("  transport: ").append(transport).append("\n");
    sb.append("  tags: ").append(tags).append("\n");
    sb.append("  thumbnailUrl: ").append(thumbnailUrl).append("\n");
    sb.append("  visibleRoles: ").append(visibleRoles).append("\n");
    sb.append("  path: ").append(path).append("\n");
    sb.append("  resourceId: ").append(resourceId).append("\n");
    sb.append("  lifecycle: ").append(lifecycle).append("\n");
    sb.append("  lifecycleState: ").append(lifecycleState).append("\n");
    sb.append("  appUrL: ").append(appUrL).append("\n");
    sb.append("  bundleversion: ").append(bundleversion).append("\n");
    sb.append("  packagename: ").append(packagename).append("\n");
    sb.append("  category: ").append(category).append("\n");
    sb.append("  displayName: ").append(displayName).append("\n");
    sb.append("  screenshots: ").append(screenshots).append("\n");
    sb.append("  banner: ").append(banner).append("\n");
    sb.append("  createdtime: ").append(createdtime).append("\n");
    sb.append("  platform: ").append(platform).append("\n");
    sb.append("  lifecycleAvailableActions: ").append(lifecycleAvailableActions).append("\n");
    sb.append("}\n");
    return sb.toString();
  }
}
