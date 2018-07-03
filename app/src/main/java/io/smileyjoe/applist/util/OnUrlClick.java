package io.smileyjoe.applist.util;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

/**
 * Created by cody on 2018/07/03.
 */

public class OnUrlClick implements View.OnClickListener {

    private String mUrl;

    public OnUrlClick(String url) {
        mUrl = url;
    }

    @Override
    public void onClick(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl));
        view.getContext().startActivity(browserIntent);
    }
}
