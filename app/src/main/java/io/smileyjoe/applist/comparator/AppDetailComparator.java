package io.smileyjoe.applist.comparator;

import java.util.Comparator;

import io.smileyjoe.applist.object.AppDetail;

public class AppDetailComparator implements Comparator<AppDetail> {

    @Override
    public int compare(AppDetail appDetailLeft, AppDetail appDetailRight) {
        return appDetailLeft.getName().toLowerCase().compareTo(appDetailRight.getName().toLowerCase());
    }
}
