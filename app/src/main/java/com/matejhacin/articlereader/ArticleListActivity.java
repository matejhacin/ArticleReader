package com.matejhacin.articlereader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.matejhacin.adapters.ArticleListAdapter;
import com.matejhacin.classes.Constants;
import com.matejhacin.database.DatabaseHandler;

import java.util.ArrayList;


public class ArticleListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    /*
    Variables
     */

    // Browse extra
    private int subcategoryId;
    private String categoryName;
    private String subcategoryName;

    // Search extra
    private String searchString;

    // Data
    private DatabaseHandler dbHandler;
    private ArrayList<String> articleArrayList;
    private ArrayList<Integer> articleIdArrayList;
    private ArrayList<String> keywordIdArrayList;

    // Views
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        // Variables
        listView = (ListView) findViewById(R.id.articleListView);
        dbHandler = new DatabaseHandler(getApplicationContext());
        articleArrayList = new ArrayList<>();
        articleIdArrayList = new ArrayList<>();
        keywordIdArrayList = new ArrayList<>();

        if (getIntent().getBooleanExtra(Constants.EXTRA_SEARCH, false))
        {
            // Get Intent extras
            searchString = getIntent().getStringExtra(Constants.EXTRA_SEARCH_PHRASE);

            // Set ActionBar title
            getSupportActionBar().setTitle(getResources().getString(R.string.action_search) + " - " + searchString);

            // Get all keywords that match the search query
            Cursor keywordCursor = dbHandler.getKeywords();
            if (keywordCursor.moveToFirst()) {
                do {
                    String keyword = keywordCursor.getString(keywordCursor.getColumnIndex("k_name"));
                    String keywordId = keywordCursor.getString(keywordCursor.getColumnIndex("k_id"));
                    // Check if searchString is a substring of Keyword string (not case sensitive)
                    if (keyword.toLowerCase().contains(searchString.toLowerCase())) {
                        keywordIdArrayList.add(keywordId);
                    }
                } while (keywordCursor.moveToNext());
            }


            // Convert ArrayList to Array
            String[] keywordIds = keywordIdArrayList.toArray(new String[keywordIdArrayList.size()]);

            // Get Articles that match with those keywords
            Cursor articleCursor = dbHandler.getArticlesRelatedToKeywords(keywordIds);
            if (articleCursor.moveToFirst()) {
                do {
                    articleArrayList.add(articleCursor.getString(articleCursor.getColumnIndex("a_title")));
                    articleIdArrayList.add(articleCursor.getInt(articleCursor.getColumnIndex("a_id")));
                } while (articleCursor.moveToNext());
            }

            // Get all Articles which names' match with the query
            Cursor allArticleCursor = dbHandler.getAllArticles();
            if (allArticleCursor.moveToFirst()) {
                do {
                    String articleTitle = allArticleCursor.getString(allArticleCursor.getColumnIndex("a_title"));
                    int articleId = allArticleCursor.getInt(allArticleCursor.getColumnIndex("a_id"));
                    if (articleTitle.toLowerCase().contains(searchString.toLowerCase())
                            && !articleIdArrayList.contains(articleId)) {
                        articleArrayList.add(articleTitle);
                        articleIdArrayList.add(articleId);
                    }
                } while (allArticleCursor.moveToNext());
            }

            // Check if we have any results, if not, show alert
            if (articleArrayList.isEmpty()) {
                showEmptySearchResultAlert();
            }
        }
        else
        {
            // Get Intent extras
            subcategoryId = getIntent().getExtras().getInt(Constants.EXTRA_SUBCATEGORY);
            categoryName = getIntent().getExtras().getString(Constants.EXTRA_CATEGORY_NAME);
            subcategoryName = getIntent().getExtras().getString(Constants.EXTRA_SUBCATEGORY_NAME);

            // Set ActionBar title
            getSupportActionBar().setTitle(Html.fromHtml(categoryName + " - " + subcategoryName));

            // Get articles from SQLite
            Cursor articleCursor = dbHandler.getArticles(subcategoryId);
            if (articleCursor.moveToFirst()) {
                do {
                    articleArrayList.add(articleCursor.getString(articleCursor.getColumnIndex("a_title")));
                    articleIdArrayList.add(articleCursor.getInt(articleCursor.getColumnIndex("a_id")));
                } while (articleCursor.moveToNext());
            }
        }

        // Set up ListView
        ArticleListAdapter adapter = new ArticleListAdapter(getApplicationContext(), articleArrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    /*
    Callbacks
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Create intent with extra article and start ArticleActivity
        Intent intent = new Intent(ArticleListActivity.this, ArticleActivity.class);
        intent.putExtra(Constants.EXTRA_ARTICLE, articleIdArrayList.get(position));
        startActivity(intent);
    }

    /*
    Methods
     */

    private void showEmptySearchResultAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ArticleListActivity.this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.list_dialog_title));
        alertDialogBuilder.setMessage(
                getResources().getString(R.string.list_dialog_message1) + searchString + getResources().getString(R.string.list_dialog_message2));
        alertDialogBuilder.setNeutralButton(getResources().getString(R.string.list_dialog_back),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alertDialogBuilder.show();
    }
}
