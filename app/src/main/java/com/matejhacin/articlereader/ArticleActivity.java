package com.matejhacin.articlereader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.matejhacin.adapters.ArticleListAdapter;
import com.matejhacin.classes.Constants;
import com.matejhacin.database.DatabaseHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class ArticleActivity extends AppCompatActivity implements View.OnClickListener {

    /*
    Variables
     */

    // Data
    private int articleId;
    private String articleTitle;
    private DatabaseHandler databaseHandler;
    private ArrayList<String> relatedArticleArrayList;
    private ArrayList<Integer> relatedArticleIdArrayList;

    // Views
    private TextView contentTextView;
    private RelativeLayout galleryButtonLayout;
    private RelativeLayout relatedArticlesButtonLayout;

    /*
    Lifecycle
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        // Variables
        articleId = getIntent().getExtras().getInt(Constants.EXTRA_ARTICLE);
        databaseHandler = new DatabaseHandler(getApplicationContext());
        contentTextView = (TextView) findViewById(R.id.contentTextView);
        galleryButtonLayout = (RelativeLayout) findViewById(R.id.galleryButtonLayout);
        relatedArticlesButtonLayout = (RelativeLayout) findViewById(R.id.relatedArticlesButtonLayout);
        relatedArticleArrayList = new ArrayList<>();
        relatedArticleIdArrayList = new ArrayList<>();

        // Click listeners
        galleryButtonLayout.setOnClickListener(this);
        relatedArticlesButtonLayout.setOnClickListener(this);

        // Get article from SQLite
        Cursor articleCursor = databaseHandler.getSingleArticle(articleId);
        if (articleCursor.moveToFirst()) {

            // Populate activity with article info
            articleTitle = articleCursor.getString(articleCursor.getColumnIndex("a_title"));
            getSupportActionBar().setTitle(Html.fromHtml(articleTitle));
            contentTextView.setText(Html.fromHtml(articleCursor.getString(articleCursor.getColumnIndex("a_content"))));

            // Put article in LastArticles ArrayList
            updateLastArticlesArrayList(articleId);

            // Get related articles
            Cursor relatedArticleCursor = databaseHandler.getRelatedArticles(articleId);
            if (relatedArticleCursor.moveToFirst()) {
                do {
                    relatedArticleArrayList.add(
                            relatedArticleCursor.getString(relatedArticleCursor.getColumnIndex("a_title"))
                    );
                    relatedArticleIdArrayList.add(
                            relatedArticleCursor.getInt(relatedArticleCursor.getColumnIndex("a_id"))
                    );
                } while (relatedArticleCursor.moveToNext());
            }
        }

    }

    /*
    Callbacks
     */

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.galleryButtonLayout:
                // Create intent with extra article id and start activity
                if (hasImages()) {
                    Intent intent = new Intent(ArticleActivity.this, GalleryActivity.class);
                    intent.putExtra(Constants.EXTRA_ARTICLE, articleId);
                    intent.putExtra(Constants.EXTRA_ARTICLE_TITLE, articleTitle);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.article_no_images, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.relatedArticlesButtonLayout:
                if (relatedArticleArrayList.size() > 0) {
                    // Create dialog with listview and display it
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
                            ArticleActivity.this);
                    dialogBuilder.setTitle("Related articles");
                    ArticleListAdapter articleListAdapter = new ArticleListAdapter(
                            getApplicationContext(), relatedArticleArrayList);
                    dialogBuilder.setAdapter(articleListAdapter,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Open this activity again with the corresponding article
                                    Intent intent = new Intent(ArticleActivity.this, ArticleActivity.class);
                                    intent.putExtra(Constants.EXTRA_ARTICLE, relatedArticleIdArrayList.get(which));
                                    finish();
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            });
                    dialogBuilder.show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.article_no_related_articles, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /*
    Methods
     */

    private void updateLastArticlesArrayList(int articleId) {

        //Variables
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SP_KEY, MODE_PRIVATE);
        Gson gson = new Gson();
        ArrayList<Integer> articleIdArrayList = null;
        String jsonString = sharedPreferences.getString(Constants.SP_LAST_ARTICLES, null);

        // See if ArrayList json already exists
        if (jsonString == null) {
            articleIdArrayList = new ArrayList<>();
            articleIdArrayList.add(articleId);
        } else {
            Type type = new TypeToken<ArrayList<Integer>>(){}.getType();
            articleIdArrayList = gson.fromJson(jsonString, type);
            // Only add it if it doesn't contain it yet
            if (!articleIdArrayList.contains(articleId)) articleIdArrayList.add(0, articleId);
            // ArrayList should always only containt 3 elements and no more!
            if (articleIdArrayList.size() == 4) articleIdArrayList.remove(3);
        }

        // Convert to json format and put it in SP
        jsonString = gson.toJson(articleIdArrayList);
        sharedPreferences.edit().putString(Constants.SP_LAST_ARTICLES, jsonString).apply();
    }

    private boolean hasImages() {
        Cursor imageCursor = new DatabaseHandler(getApplicationContext()).getPictures(articleId);
        return imageCursor.moveToFirst();
    }
}
