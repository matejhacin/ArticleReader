package com.matejhacin.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.matejhacin.articlereader.ImageFragment;

import java.util.ArrayList;

public class ImagePagerAdapter extends FragmentStatePagerAdapter {

    /*
    Variables
     */

    // Data
    ArrayList<String> imageTitleArrayList;
    ArrayList<String> imageDescriptionArrayList;
    ArrayList<String> imageFileNameArrayList;

    int size;

    /*
    Constructor
     */

    public ImagePagerAdapter(FragmentManager fm, int size) {
        super(fm);
        this.size = size;
    }

    /*
    Callbacks
     */

    @Override
    public Fragment getItem(int position) {
        return ImageFragment.newInstance(imageTitleArrayList.get(position), imageDescriptionArrayList.get(position),
                imageFileNameArrayList.get(position));
    }

    @Override
    public int getCount() {
        return size;
    }

    /*
    Methods
     */

    public void setImageData(ArrayList<String> imageTitleArrayList, ArrayList<String> imageDescriptionArrayList,
                             ArrayList<String> imageFileNameArrayList) {
        this.imageTitleArrayList = imageTitleArrayList;
        this.imageDescriptionArrayList = imageDescriptionArrayList;
        this.imageFileNameArrayList = imageFileNameArrayList;
    }
}
