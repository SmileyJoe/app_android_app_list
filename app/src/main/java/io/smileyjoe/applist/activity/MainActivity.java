package io.smileyjoe.applist.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;

import java.util.Arrays;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.adapter.PagerAdapterMain;
import io.smileyjoe.applist.databinding.ActivityMainBinding;
import io.smileyjoe.applist.fragment.AppListFragment;
import io.smileyjoe.applist.util.Notify;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

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

        PagerAdapterMain pagerAdapterMain = new PagerAdapterMain(getBaseContext(), getSupportFragmentManager(), this::onFragmentLoadComplete);

        mView.pagerApps.setAdapter(pagerAdapterMain);
        mView.pagerApps.addOnPageChangeListener(this);
        mView.textTitle.setText(Nav.fromId(0).getTitle(getBaseContext()));
        mView.bottomNavigation.setOnItemSelectedListener(item -> {
            mView.pagerApps.setCurrentItem(Nav.fromId(item.getItemId()).getPosition());
            return true;
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Nav nav = Nav.fromPosition(position);
        mView.textTitle.setText(nav.getTitle(getBaseContext()));
        mView.bottomNavigation.setSelectedItemId(nav.getId());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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

    public void onFragmentLoadComplete(AppListFragment.Type type, int position, int appCount) {
        BadgeDrawable badge = mView.bottomNavigation.getOrCreateBadge(Nav.fromPosition(position).getId());
        badge.setVisible(true);
        badge.setNumber(appCount);
    }

    private enum Nav {
        INSTALLED(0, R.id.nav_installed, R.string.fragment_title_installed_apps),
        SAVED(1, R.id.nav_saved, R.string.fragment_title_saved_apps);

        private int mPosition;
        @IdRes
        private int mId;
        @StringRes
        private int mTitle;

        Nav(int position, int id, int title) {
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

        public static Nav fromId(@IdRes int id) {
            return Arrays.stream(values())
                    .filter(nav -> nav.getId() == id)
                    .findFirst()
                    .orElse(Nav.INSTALLED);
        }

        public static Nav fromPosition(int position) {
            return Arrays.stream(values())
                    .filter(nav -> nav.getPosition() == position)
                    .findFirst()
                    .orElse(Nav.INSTALLED);
        }
    }
}
