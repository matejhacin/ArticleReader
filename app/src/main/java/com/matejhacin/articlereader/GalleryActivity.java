package com.matejhacin.articlereader;

import android.database.Cursor;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;

import com.matejhacin.adapters.ImagePagerAdapter;
import com.matejhacin.classes.Constants;
import com.matejhacin.database.DatabaseHandler;

import java.util.ArrayList;


public class GalleryActivity extends AppCompatActivity {

    /*
    Variables
     */

    // Data
    int articleId;
    ArrayList<String> imageTitleArrayList;
    ArrayList<String> imageDescriptionArrayList;
    ArrayList<String> imageFileNameArrayList;
    DatabaseHandler dbHandler;

    // ViewPager
    ViewPager viewPager;
    ImagePagerAdapter imagePagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        // Get extras
        articleId = getIntent().getExtras().getInt(Constants.EXTRA_ARTICLE);
        getSupportActionBar().setTitle(Html.fromHtml(getIntent().getExtras().getString(Constants.EXTRA_ARTICLE_TITLE)));

        // Variables
        imageTitleArrayList = new ArrayList<>();
        imageDescriptionArrayList = new ArrayList<>();
        imageFileNameArrayList = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.pager);
        dbHandler = new DatabaseHandler(getApplicationContext());

        // Get article images
        Cursor imageCursor = dbHandler.getPictures(articleId);
        imageCursor.moveToFirst();
        do {
            imageTitleArrayList.add(imageCursor.getString(imageCursor.getColumnIndex("p_title")));
            imageDescriptionArrayList.add(imageCursor.getString(imageCursor.getColumnIndex("p_content")));
            imageFileNameArrayList.add(imageCursor.getString(imageCursor.getColumnIndex("p_filename")));
        } while (imageCursor.moveToNext());

        // Set up ViewPager
        imagePagerAdapter = new ImagePagerAdapter(getSupportFragmentManager(), imageTitleArrayList.size());
        imagePagerAdapter.setImageData(imageTitleArrayList, imageDescriptionArrayList, imageFileNameArrayList);
        viewPager.setAdapter(imagePagerAdapter);
        viewPager.setCurrentItem(0);
    }
}
