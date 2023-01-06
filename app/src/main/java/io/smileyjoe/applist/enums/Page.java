package io.smileyjoe.applist.enums;

import android.content.Context;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.util.PackageUtil;

public enum Page {
    INSTALLED(0, R.id.nav_installed, R.string.fragment_title_installed_apps),
    SAVED(1, R.id.nav_saved, R.string.fragment_title_saved_apps),
    FAVOURITE(2, R.id.nav_favourite, R.string.fragment_title_favourite_apps);

    private int mPosition;
    @IdRes
    private int mId;
    @StringRes
    private int mTitle;

    Page(int position, int id, int title) {
        mPosition = position;
        mId = id;
        mTitle = title;
    }

    public int getPosition() {
        return mPosition;
    }

    public int getId() {
        return mId;
    }

    public String getTitle(Context context) {
        return context.getString(mTitle);
    }

    public List<AppDetail> getApps(Context context, DataSnapshot dataSnapshot){
        ArrayList<AppDetail> apps = new ArrayList<>();

        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
            apps.add(new AppDetail(itemSnapshot));
        }

        switch (this){
            case INSTALLED:
                return PackageUtil.getInstalledApplications(context.getPackageManager(), apps);
            case FAVOURITE:
                return PackageUtil.checkInstalled(context.getPackageManager(),
                        apps.stream()
                                .filter(app -> app.isFavourite())
                                .collect(Collectors.toList()));
            case SAVED:
                return PackageUtil.checkInstalled(context.getPackageManager(), apps);
            default:
                return apps;
        }
    }

    public static Page fromId(@IdRes int id) {
        return Arrays.stream(values())
                .filter(nav -> nav.getId() == id)
                .findFirst()
                .orElse(Page.INSTALLED);
    }

    public static Page fromPosition(int position) {
        return Arrays.stream(values())
                .filter(nav -> nav.getPosition() == position)
                .findFirst()
                .orElse(Page.INSTALLED);
    }
}
