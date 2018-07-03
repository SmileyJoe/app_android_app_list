package io.smileyjoe.applist.object;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by cody on 2018/07/03.
 */

public class AppDetail {

    private String mName;
    private String mPackage;
    private Intent mLaunchActivity;
    private Drawable mIcon;

    public void setName(String name) {
        mName = name;
    }

    public void setPackage(String aPackage) {
        mPackage = aPackage;
    }

    public void setLaunchActivity(Intent launchActivity) {
        mLaunchActivity = launchActivity;
    }

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public String getName() {
        return mName;
    }

    public String getPackage() {
        return mPackage;
    }

    public Intent getLaunchActivity() {
        return mLaunchActivity;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    @Override
    public String toString() {
        return "AppDetail{" +
                "name='" + getName() + '\'' +
                ", package='" + getPackage() + '\'' +
                ", launchActivity=" + (getLaunchActivity() != null) +
                ", icon=" + (getIcon() != null) +
                '}';
    }
}
