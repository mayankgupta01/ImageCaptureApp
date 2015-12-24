package com.flipkart.imagecaptureapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

/**
 * Created by mayank.gupta on 18/12/15.
 */
public class ImageHelper {
    LruCache<String, Bitmap> cache;
    Context context;
    private static final String TAG="ImageHelper";

    public ImageHelper(Context context) {
        this.context = context;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 8;

        cache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    public void displayImage(ImageView imageView, String imageFileName) {
        Bitmap image = cache.get(imageFileName);

        if(image != null) {
//            image found in cache, display it
            Log.i(TAG, "Found image in cache : " + imageFileName);
            imageView.setImageBitmap(image);
        }else {
//            Create the async taxk to lazy load the image in a background thread
            Log.i(TAG, "Cache miss!!!!! Filename : " + imageFileName);
            new ImageDiskLoader(imageView).execute(imageFileName);

//            Day 5 : Pass 1 code: In pass 2, this will be shifter to Async Task
//            Log.i(TAG, "Cache miss!!!!! Filename : " + imageFileName);
//            FileInputStream inStr = null;
//            try {
//                inStr = context.openFileInput(imageFileName);
//                image = BitmapFactory.decodeStream(inStr);
//
////                add the image to the cache
//                cache.put(imageFileName, image);
//
////        display the image on the imageView
//                imageView.setImageBitmap(image);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
        }

    }

//    Day 5 : Pass 2 : Offload the reading from file part to Async Task on background thread
    private class ImageDiskLoader extends AsyncTask<String, Void, Bitmap> {
//    Doing a weakreference here, this will not update the reference counter on the object. So if GC wants to collect the object, it wont
//    consider this reference as a blocker.
        WeakReference<ImageView>  imageView;

        public ImageDiskLoader(ImageView imageView) {
            this.imageView = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            FileInputStream inStr = null;
            Bitmap image = null;
            try {
//                Day 5 : Pass 1 : context openFileinput reads from the apps files directory in data/data.
//                To read from external storage, we need to implement our own.
//                inStr = context.openFileInput(params[0]);

//                Day 5 : Pass 2 : reading from external storage
                File externalDirPath = Environment.getExternalStorageDirectory();
                File imageDirPath = new File(externalDirPath, "imageCapture");
                File imageFileName = new File(imageDirPath, params[0]);
                inStr = new FileInputStream(imageFileName);

                image = BitmapFactory.decodeStream(inStr);

//                add the image to the cache
                cache.put(params[0], image);

                return image;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                this.imageView.get().setImageBitmap(bitmap);
            }
        }
    }
}
