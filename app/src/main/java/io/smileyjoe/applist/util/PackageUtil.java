package io.smileyjoe.applist.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.List;
import java.util.stream.Collectors;

import io.smileyjoe.applist.object.AppDetail;

public class PackageUtil {

    private PackageUtil() {
    }

    public static List<AppDetail> getInstalledApplications(PackageManager packageManager) {
        return packageManager.getInstalledPackages(0)
                .stream()
                .filter(packageInfo -> (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                .map(packageInfo -> {
                    AppDetail app = new AppDetail();
                    app.setPackage(packageInfo.packageName);
                    app.setName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                    app.setInstalled(true);
                    try {
                        app.setIcon(packageManager.getApplicationIcon(app.getPackage()));
                    } catch (PackageManager.NameNotFoundException e) {
                        // do nothing, just don't add an icon //
                    }
                    return app;
                })
                .collect(Collectors.toList());
    }

    public static List<AppDetail> getInstalledApplications(PackageManager packageManager, List<AppDetail> savedApps) {
        List<AppDetail> installedApps = getInstalledApplications(packageManager);

        installedApps.forEach(installedApp -> installedApp.onSavedUpdated(savedApps));

        return installedApps;
    }

    public static List<AppDetail> checkInstalled(PackageManager packageManager, List<AppDetail> savedApps) {
        List<AppDetail> installedApps = getInstalledApplications(packageManager);

        savedApps.forEach(savedApp -> {
            savedApp.onInstalledUpdated(installedApps);
            try {
                savedApp.setIcon(packageManager.getApplicationIcon(savedApp.getPackage()));
            } catch (PackageManager.NameNotFoundException e) {
                // do nothing, just don't add an icon //
            }
        });

        return savedApps;
    }
}
