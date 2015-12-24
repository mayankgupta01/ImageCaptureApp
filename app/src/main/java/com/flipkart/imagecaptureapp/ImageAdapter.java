package com.flipkart.imagecaptureapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by mayank.gupta on 18/12/15.
 */
public class ImageAdapter extends BaseAdapter {

    Context context;
    ImageHelper imageHelper;

    public void setImageNames(String[] imageNames) {
        this.imageNames = imageNames;
    }

    String[] imageNames;

    public ImageAdapter(Context context, String[] imageNames) {
        this.context = context;
        this.imageNames = imageNames;
        imageHelper = new ImageHelper(context);
    }

    @Override
    public int getCount() {
        return imageNames.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mainView = null;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);

//        inflate row.xml
          mainView = inflater.inflate(R.layout.row, null);
        }else {
            mainView = convertView;
        }

//        display the image in the image view
//        get the imageView reference from row.xml view
        ImageView imageView = (ImageView) mainView.findViewById(R.id.imageView);
        imageHelper.displayImage(imageView,imageNames[position]);
//        Day 5 : Pass 2: Moving this code to ImageHelper
//        FileInputStream inStr = null;
//        try {
//            inStr = context.openFileInput(imageNames[position]);
//            Bitmap image = BitmapFactory.decodeStream(inStr);
//
////        display the image on the imageView
//            imageView.setImageBitmap(image);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

        return mainView;
    }
}
