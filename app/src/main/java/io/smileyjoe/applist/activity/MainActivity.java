package io.smileyjoe.applist.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.adapter.PagerAdapterMain;
import io.smileyjoe.applist.util.Notify;

public class MainActivity extends BaseActivity {

    private static final int ACTIVITY_SAVE_APP = 1;

    private PagerAdapterMain mPagerAdapterMain;
    private ViewPager mViewPager;
    private CoordinatorLayout mCoordinatorMain;

    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPagerAdapterMain = new PagerAdapterMain(getBaseContext(), getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapterMain);

        mCoordinatorMain = (CoordinatorLayout) findViewById(R.id.coordinator_main);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fabCreate = (FloatingActionButton) findViewById(R.id.fab_create);
        fabCreate.setOnClickListener(new OnFabCreateClick());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVITY_SAVE_APP:
                if (resultCode == RESULT_OK) {
                    Notify.success(mCoordinatorMain, R.string.success_app_saved);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private class OnFabCreateClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivityForResult(SaveAppActivity.getIntent(view.getContext()), ACTIVITY_SAVE_APP);
        }
    }
}
