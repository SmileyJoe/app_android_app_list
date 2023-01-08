package io.smileyjoe.applist.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

import io.smileyjoe.applist.BuildConfig;
import io.smileyjoe.applist.object.AppDetail;

public class Icon {

    private static final String STORAGE_KEY_APP_ICON = BuildConfig.DEBUG ? "icon-debug" : "icon";

    private Icon() {
    }

    private static StorageReference getReference() {
        return FirebaseStorage.getInstance().getReference().child(STORAGE_KEY_APP_ICON);
    }

    public static StorageReference getIconReference(String packageName) {
        StorageReference reference = getReference();

        if (reference != null) {
            return reference.child(packageName + ".png");
        } else {
            return null;
        }
    }

    public static void upload(String packageName, Drawable icon) {
        StorageReference storageReference = getIconReference(packageName);

        // only upload if there is no icon //
        storageReference.getDownloadUrl().addOnFailureListener(e -> {
            Bitmap iconBitmap = getBitmapFromDrawable(icon);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            byte[] data = output.toByteArray();

            storageReference.putBytes(data);
        });

    }

    public static void load(ImageView imageView, AppDetail appDetail) {
        if (appDetail.getIcon() != null) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageDrawable(appDetail.getIcon());
        } else {
            StorageReference storageReference = getIconReference(appDetail.getPackage());

            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(imageView.getContext())
                        .load(storageReference)
                        .into(imageView);

                imageView.setVisibility(View.VISIBLE);
            }).addOnFailureListener(e -> imageView.setVisibility(View.GONE));
        }
    }

    /**
     * Taken from https://stackoverflow.com/a/52453231
     *
     * @param drawable
     * @return
     */
    private static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

}
