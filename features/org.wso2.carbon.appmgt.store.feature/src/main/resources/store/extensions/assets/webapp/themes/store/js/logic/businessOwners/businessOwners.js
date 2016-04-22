/*
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
function merge(businessOwner){
var log = new Log();
    var owner = new Array();
    
    owner.push({
                      name : "Name",
                      value : businessOwner.businessOwnerName
                  });
    owner.push({
                      name : "Email",
                      value : businessOwner.businessOwnerEmail
                  });
    owner.push({
                      name : "Website",
                      value : businessOwner.businessOwnerSite
                  });
    owner.push({
                      name : "Description",
                      value : businessOwner.businessOwnerDescription
                  });
    var details = businessOwner.businessOwnerDeatails;
    details = JSON.parse(details);
    for(var key in details){
        owner.push({
                       name : key,
                       value : details[key]
                   });
    }

    return owner;
}