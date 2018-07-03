package io.smileyjoe.applist;

import android.app.Application;

import za.co.smileyjoedev.lib.debug.Debug;

/**
 * Created by cody on 2018/07/03.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(getApplicationContext());
    }
}
