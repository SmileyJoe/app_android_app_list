package io.smileyjoe.applist.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.badge.BadgeDrawable;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.adapter.PagerAdapterMain;
import io.smileyjoe.applist.databinding.ActivityMainBinding;
import io.smileyjoe.applist.enums.Page;
import io.smileyjoe.applist.util.Notify;

public class MainActivity extends BaseActivity {

    private static final int ACTIVITY_SAVE_APP = 1;

    private ActivityMainBinding mView;

    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mView.getRoot());

        PagerAdapterMain pagerAdapterMain = new PagerAdapterMain(this, this::onFragmentLoadComplete);

        mView.pagerApps.setAdapter(pagerAdapterMain);
        mView.pagerApps.registerOnPageChangeCallback(new OnPageChangeListener());
        mView.textTitle.setText(Page.fromId(0).getTitle(getBaseContext()));
        mView.bottomNavigation.setOnItemSelectedListener(item -> {
            mView.pagerApps.setCurrentItem(Page.fromId(item.getItemId()).getPosition());
            return true;
        });
    }

    private class OnPageChangeListener extends ViewPager2.OnPageChangeCallback {
        @Override
        public void onPageSelected(int position) {
            Page nav = Page.fromPosition(position);
            mView.textTitle.setText(nav.getTitle(getBaseContext()));
            mView.bottomNavigation.setSelectedItemId(nav.getId());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_SAVE_APP:
                if (resultCode == RESULT_OK) {
                    Notify.success(mView.layoutMain, R.string.success_app_saved);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void onFragmentLoadComplete(Page page, int position, int appCount) {
        BadgeDrawable badge = mView.bottomNavigation.getOrCreateBadge(page.getId());
        badge.setVisible(true);
        badge.setNumber(appCount);
    }
}
