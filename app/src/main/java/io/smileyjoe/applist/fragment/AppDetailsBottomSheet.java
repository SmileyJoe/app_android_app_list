package io.smileyjoe.applist.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Optional;

import io.smileyjoe.applist.R;
import io.smileyjoe.applist.databinding.FragmentBottomSheetDetailsBinding;
import io.smileyjoe.applist.object.AppDetail;
import io.smileyjoe.applist.util.Icon;

public class AppDetailsBottomSheet extends BottomSheetDialogFragment {

    private enum Action{
        EDIT(R.string.action_edit, R.drawable.ic_edit),
        SHARE(R.string.action_share, R.drawable.ic_share),
        PLAY_STORE(R.string.action_play_store, R.drawable.ic_play_store),
        FAVOURITE(R.string.action_favourite, R.drawable.ic_favourite),
        UNFAVOURITE(R.string.action_unfavourite, R.drawable.ic_favourite_outline),
        SAVE(R.string.action_save, R.drawable.ic_save),
        DELETE(R.string.action_delete, R.drawable.ic_delete);

        @StringRes
        private int mTitle;
        @DrawableRes
        private int mIcon;

        Action(int title, int icon) {
            mTitle = title;
            mIcon = icon;
        }

        public int getIcon() {
            return mIcon;
        }

        public int getTitle() {
            return mTitle;
        }

        public String getTag(){
            return "action_" + name();
        }

        public boolean shouldShow(AppDetail appDetail){
            switch (this){
                case FAVOURITE:
                    return !appDetail.isFavourite() && appDetail.isSaved();
                case UNFAVOURITE:
                    return appDetail.isFavourite();
                case SAVE:
                    return !appDetail.isSaved();
                case DELETE:
                    return appDetail.isSaved();
                case EDIT:
                    // todo: implement edit //
                    return false;
                default:
                    return true;
            }
        }
    }

    private FragmentBottomSheetDetailsBinding mView;
    private AppDetail mAppDetail;

    public AppDetailsBottomSheet(AppDetail appDetail) {
        mAppDetail = appDetail;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = FragmentBottomSheetDetailsBinding.inflate(inflater, container, false);
        mView.textTitle.setText(mAppDetail.getName());
        mView.textPackage.setText(mAppDetail.getPackage());
        mView.textInstalled.setVisibility(mAppDetail.isInstalled() ? View.VISIBLE : View.GONE);
        Icon.load(mView.imageIcon, mAppDetail);
        addActions();
        return mView.getRoot();
    }

    private void addActions(){
        for(Action action:Action.values()){
            TextView textAction = new TextView(getContext(), null, 0, R.style.Text_ListAction);
            textAction.setText(action.getTitle());
            textAction.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getContext(), action.getIcon()), null, null, null);
            textAction.setVisibility(action.shouldShow(mAppDetail) ? View.VISIBLE : View.GONE);
            textAction.setOnClickListener(v -> onActionClick(action));
            textAction.setTag(action.getTag());
            mView.layoutActions.addView(textAction);
        }
    }

    private void onActionClick(Action action){
        switch (action){
            case PLAY_STORE:
                openUrl();
                break;
            case SHARE:
                share();
                break;
            case FAVOURITE:
                mAppDetail.isFavourite(true);
                mAppDetail.save(getActivity(), Optional.of((error, ref) -> {
                    hide(Action.FAVOURITE);
                    show(Action.UNFAVOURITE);
                }));
                break;
            case UNFAVOURITE:
                mAppDetail.isFavourite(false);
                mAppDetail.save(getActivity(), Optional.of((error, ref) -> {
                    show(Action.FAVOURITE);
                    hide(Action.UNFAVOURITE);
                }));
                break;
            case SAVE:
                mAppDetail.setSaved(true);
                mAppDetail.save(getActivity(), Optional.of((error, ref) -> {
                    if(error == null){
                        hide(Action.SAVE);
                        show(Action.DELETE, Action.FAVOURITE);
                    }
                }));
                break;
            case DELETE:
                mAppDetail.delete(getActivity(), Optional.of((error, ref) -> {
                    if(error == null){
                        mAppDetail.isFavourite(false);
                        hide(Action.DELETE, Action.FAVOURITE, Action.UNFAVOURITE);
                        show(Action.SAVE);
                    }
                }));
                break;
        }
    }

    private void hide(Action... actions){
        for(Action action:actions) {
            mView.layoutActions.findViewWithTag(action.getTag()).setVisibility(View.GONE);
        }
    }

    private void show(Action... actions){
        for(Action action:actions) {
            mView.layoutActions.findViewWithTag(action.getTag()).setVisibility(View.VISIBLE);
        }
    }

    private void openUrl() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mAppDetail.getPlayStoreLink()));
        startActivity(browserIntent);
    }

    private void share(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mAppDetail.getPlayStoreLink());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}
