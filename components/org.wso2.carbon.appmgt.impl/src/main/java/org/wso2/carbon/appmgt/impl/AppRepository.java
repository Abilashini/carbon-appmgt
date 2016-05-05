package org.wso2.carbon.appmgt.impl;

import org.wso2.carbon.appmgt.api.model.App;

/**
 * The interface for app repository implementations.
 */
public interface AppRepository {


    /**
     *
     * Persists the given app in the repository.
     *
     * @param app
     * @return
     */
    long saveApp(App app);


}
