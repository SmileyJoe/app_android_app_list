package io.smileyjoe.applist.comparator;

import java.util.Comparator;

import io.smileyjoe.applist.object.AppDetail;

/**
 * Created by cody on 2018/07/05.
 */

public class AppDetailComparator implements Comparator<AppDetail> {

    @Override
    public int compare(AppDetail appDetailLeft, AppDetail appDetailRight) {
        return appDetailLeft.getName().compareTo(appDetailRight.getName());
    }
}