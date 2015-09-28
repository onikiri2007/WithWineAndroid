package com.bluechilli.withwine.configs;


import com.bluechilli.withwine.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by monishi on 13/03/15.
 */
public final class FontConfigurator {


    public static void ConfigFonts() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Signika-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }
}
