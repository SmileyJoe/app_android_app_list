package io.smileyjoe.applist.comparator

import io.smileyjoe.applist.objects.AppDetail

/**
 * Sort a list of [AppDetail] alphabetically
 */
class AppDetailComparator : Comparator<AppDetail> {

    override fun compare(left: AppDetail?, right: AppDetail?): Int {
        return left?.name?.lowercase()?.compareTo(right?.name?.lowercase()!!)!!
    }
}