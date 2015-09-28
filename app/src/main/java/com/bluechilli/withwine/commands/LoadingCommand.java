package com.bluechilli.withwine.commands;

import com.bluechilli.withwine.R;
import com.bluechilli.withwine.WithWineAppApplication;

/**
 * Created by monishi on 6/02/15.
 */
public class LoadingCommand {

    private final String loadingMessage;
    private final int backgroundColorResourceId;
    private final boolean show;

    public LoadingCommand(boolean show) {
        this(R.color._loading_overlay, show);
    }

    public LoadingCommand(int backgroundColorResourceId, boolean show) {
        this(WithWineAppApplication.getInstance().getApplicationContext().getResources().getString(R.string.loading_message), backgroundColorResourceId, show);
    }

    public LoadingCommand(String loadingMessage, int backgroundColorResourceId, boolean show) {
        this.loadingMessage = loadingMessage;
        this.backgroundColorResourceId = backgroundColorResourceId;
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }

    public String getLoadingMessage() {
        return loadingMessage;
    }

    public int getBackgroundColorResourceId() {
        return backgroundColorResourceId;
    }
}
