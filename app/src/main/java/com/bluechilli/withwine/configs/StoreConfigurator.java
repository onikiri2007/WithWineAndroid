package com.bluechilli.withwine.configs;

import com.bluechilli.withwine.interfaces.IContext;
import com.bluechilli.withwine.managers.NetworkManager;
import com.bluechilli.withwine.stores.UserStore;

/**
 * Created by monishi on 4/03/15.
 */
public final class StoreConfigurator {


    public static void startStores(IContext context) {
        UserStore.getInstance().start(context);
        NetworkManager.getInstance().registerEvents();
    }

    public static void stopStores() {
        UserStore.getInstance().stop();
        NetworkManager.getInstance().unregisterEvents();
    }
}
