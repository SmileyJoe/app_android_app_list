package io.smileyjoe.applist.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import io.smileyjoe.applist.object.AppDetail;

/**
 * Created by cody on 2018/07/03.
 */

public class PackageUtil {

    private PackageUtil() {
    }

    public static List<AppDetail> getInstalledApplications(PackageManager packageManager) {
        List<AppDetail> appDetails = new ArrayList<>();
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);

        for (PackageInfo packageInfo : packages) {
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                AppDetail app = new AppDetail();
                app.setPackage(packageInfo.packageName);
                app.setName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                app.setLaunchActivity(packageManager.getLaunchIntentForPackage(packageInfo.packageName));
                try {
                    app.setIcon(packageManager.getApplicationIcon(app.getPackage()));
                } catch (PackageManager.NameNotFoundException e) {
                    // do nothing, just don't add an icon //
                }

                appDetails.add(app);
            }
        }

        return appDetails;
    }

    public static List<AppDetail> getInstalledApplications(PackageManager packageManager, List<AppDetail> savedApps) {
        List<AppDetail> installedApps = getInstalledApplications(packageManager);

        for (AppDetail installedApp : installedApps) {
            installedApp.onSavedUpdated(savedApps);
        }

        return installedApps;
    }
}
