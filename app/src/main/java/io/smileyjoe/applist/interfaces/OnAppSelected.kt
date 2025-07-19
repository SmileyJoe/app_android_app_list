package io.smileyjoe.applist.interfaces

import io.smileyjoe.applist.objects.AppDetail

fun interface OnAppSelected {
    fun onSelected(app: AppDetail)
}