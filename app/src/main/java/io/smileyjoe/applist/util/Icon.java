package io.smileyjoe.applist.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import io.smileyjoe.applist.BuildConfig;
import io.smileyjoe.applist.R;
import io.smileyjoe.applist.object.User;

public class Icon {

    private static final String STORAGE_KEY_APP_ICON = BuildConfig.DEBUG ? "icon-debug" : "icon";

    private Icon(){}

    private static StorageReference getReference(){
        return FirebaseStorage.getInstance().getReference().child(STORAGE_KEY_APP_ICON);
    }

    public static StorageReference getIconReference(String packageName){
        StorageReference reference = getReference();

        if(reference != null){
            return reference.child(packageName + ".png");
        } else {
            return null;
        }
    }

    public static void upload(String packageName, Drawable icon){
        StorageReference storageReference = getIconReference(packageName);

        // only upload if there is no icon //
        storageReference.getDownloadUrl().addOnFailureListener(e -> {
            Bitmap iconBitmap = getBitmapFromDrawable(icon);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            byte[] data = output.toByteArray();

            UploadTask uploadTask = storageReference.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });
        });

    }

    public static void load(ImageView imageView, String packageName){
        StorageReference storageReference = getIconReference(packageName);

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(imageView.getContext())
                    .load(storageReference)
                    .into(imageView);

            imageView.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
            imageView.setVisibility(View.GONE);
        });
    }

    /**
     * Taken from https://stackoverflow.com/a/52453231
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
