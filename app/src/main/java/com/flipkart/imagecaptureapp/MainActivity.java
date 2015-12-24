package com.flipkart.imagecaptureapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.ListView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private ListView imageListView;
    private String[] files;
    private ImageAdapter imageAdapter;
    private static final String TAG = "Main Activity";
    public static final int IMAGE_CAPTURE_REQUEST_CODE = 101;

    //    Day 5 : Pass 2: Moving the images from files folder to external Storage. Because it is better to use SD card memory than phone memory.
    boolean externalStorageAvailable;
    boolean imageCaptureDirAvailable;
    SharedPreferences preferences;

    private boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();
        return  state.equalsIgnoreCase(Environment.MEDIA_MOUNTED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Day 5 : Pass 3 : Adding to shared preference, User may prefer to use external storage or phone memory
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("EXTERNAL", true);
        editor.commit();

        imageListView = (ListView) findViewById(R.id.listView);
//        Day 5 : Pass 2 : Storing to external Storage
        externalStorageAvailable = isExternalStorageAvailable();
        Log.i(TAG, "The external storage is available? : " + externalStorageAvailable);

        if(externalStorageAvailable) {
//            get a path to external storage
            File externalDirPath = Environment.getExternalStorageDirectory();
            File imageDirPath = new File (externalDirPath, "imageCapture");
            Log.i(TAG, "Directory Path: " + imageDirPath.toString());

            if(!imageDirPath.exists()) {
                boolean success = imageDirPath.mkdirs();
                if(success) {
                    Log.i(TAG, "Creating imageCapture directory");
                    imageCaptureDirAvailable = true;
                }else {
                    Log.i(TAG, "Failed to create imageCapture directory");
                }
            }
        }
//        Day 5 : Pass 1
//        files = fileList();

//        Day 5 : Pass 2
        files = getFileFromExternalFolder();
        imageAdapter = new ImageAdapter(this, files);

//        attach the adapter to the list view
        imageListView.setAdapter(imageAdapter);

    }

    public void getImage(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,IMAGE_CAPTURE_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();

//       Day 5 : Pass 1: get list of files in the file folder
//        files = fileList();

//        Day 5 : Pass 2 :
        files = getFileFromExternalFolder();
        imageAdapter.setImageNames(files);

//        refresh the list view so that all images are shown
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(IMAGE_CAPTURE_REQUEST_CODE ==requestCode && resultCode == RESULT_OK) {
            Bitmap image = data.getParcelableExtra("data");

//            create a random image
            UUID uuid = UUID.randomUUID();

//            write the file to the files folder on the file system
            try {
//                Day 5 : Pass 1:
//                FileOutputStream fout =openFileOutput(uuid.toString(), MODE_PRIVATE);
//
//                image.compress(Bitmap.CompressFormat.PNG, 90, fout);

//                Day 5 : Pass 2 :
                File externalDirPath = Environment.getExternalStorageDirectory();
                File imageDirPath = new File(externalDirPath, "imageCapture");
                File imageFileName = new File(imageDirPath, uuid.toString());
                FileOutputStream fout = new FileOutputStream(imageFileName);

                image.compress(Bitmap.CompressFormat.PNG, 90, fout);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
//    Day 5 : Pass 2
    private String[] getFileFromExternalFolder() {
        File externalDirPath = Environment.getExternalStorageDirectory();
        File imageDirPath = new File(externalDirPath, "imageCapture");
        return imageDirPath.list();
    }
}
