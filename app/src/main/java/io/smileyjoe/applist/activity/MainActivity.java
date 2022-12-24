package io.smileyjoe.applist.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.adapter.PagerAdapterMain;
import io.smileyjoe.applist.fragment.AppListFragment;
import io.smileyjoe.applist.util.Notify;

public class MainActivity extends BaseActivity {

    private static final int ACTIVITY_SAVE_APP = 1;

    private PagerAdapterMain mPagerAdapterMain;
    private ViewPager mViewPager;
    private CoordinatorLayout mCoordinatorMain;
    private TabLayout mTabLayout;

    public static Intent getIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPagerAdapterMain = new PagerAdapterMain(getBaseContext(), getSupportFragmentManager(), new OnFragmentLoadComplete());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapterMain);

        mCoordinatorMain = (CoordinatorLayout) findViewById(R.id.coordinator_main);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

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

    private class OnFragmentLoadComplete implements PagerAdapterMain.Listener{

        @Override
        public void onLoadComplete(AppListFragment.Type type, int position, int appCount) {
            if(mTabLayout != null){
                mTabLayout.getTabAt(position).setText(type.getFragmentTitle(getBaseContext()) + " (" + appCount + ")");
            }
        }
    }
}
