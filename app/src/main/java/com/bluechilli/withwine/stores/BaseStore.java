package com.bluechilli.withwine.stores;

import com.bluechilli.withwine.interfaces.IContext;
import com.path.android.jobqueue.JobManager;

/**
 * Created by monishi on 4/03/15.
 */
public abstract class BaseStore {

    private IContext context;

    public void start(IContext context) {
        this.context = context;
    }

    public void stop() {
        context = null;
    }

    public IContext getContext() {
        return context;
    }

    public JobManager getJobManager() {
        return context.jobManager();
    }
}
