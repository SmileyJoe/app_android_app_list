package io.smileyjoe.applist.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.adapter.PagerAdapterMain;
import io.smileyjoe.applist.fragment.AppListFragment;

public class MainActivity extends BaseActivity {

    private PagerAdapterMain mPagerAdapterMain;
    private ViewPager mViewPager;

    public static Intent getIntent(Context context){
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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fabCreate = (FloatingActionButton) findViewById(R.id.fab_create);
        fabCreate.setOnClickListener(new OnFabCreateClick());
    }

    private class OnFabCreateClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            startActivity(SaveAppActivity.getIntent(view.getContext()));
        }
    }
}
