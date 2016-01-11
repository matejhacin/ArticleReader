package com.matejhacin.articlereader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.matejhacin.adapters.ArticleListAdapter;
import com.matejhacin.classes.Constants;
import com.matejhacin.classes.TableObject;
import com.matejhacin.database.DatabaseHandler;
import com.matejhacin.database.TableSync;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /*
    Variables
     */

    // Views
    private TextView aboutText;
    private RelativeLayout searchButtonLayout;
    private ListView listView;
    private TextView listTitle;
    private View listTopDivider;

    // Data
    private ArrayList<Integer> articleIdArrayList;
    private ArrayList<String> articleArrayList;
    private DatabaseHandler databaseHandler;
    private boolean dataDownloaded;

    // Other
    private SharedPreferences sp;

    /*
    Lifecycle
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        aboutText = (TextView) findViewById(R.id.aboutText);
        searchButtonLayout = (RelativeLayout) findViewById(R.id.searchButtonLayout);
        searchButtonLayout.setOnClickListener(this);
        listView = (ListView) findViewById(R.id.listView);
        listTitle = (TextView) findViewById(R.id.listTitle);
        listTopDivider = (View) findViewById(R.id.listTopDivider);

        // Shared Preferences
        sp = getSharedPreferences(Constants.SP_KEY, MODE_PRIVATE);
        dataDownloaded = sp.getBoolean(Constants.SP_DATA_DOWNLOADED, false);

        /*
        Check if data isn't downloaded
            Check if you have internet connection
                Download data if you have
                Display alert if you don't
         */
        if (!dataDownloaded) {
            if (isConnected()) {
                new DownloadDatabase().execute();
            } else {
                showConnectionAlert(R.string.main_alert_message_no_data);
                disableSearchButton();
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        // Data
        articleIdArrayList = new ArrayList<>();
        articleArrayList = new ArrayList<>();
        databaseHandler = new DatabaseHandler(getApplicationContext());

        // ListView (showing last 3 articles)
        articleIdArrayList = getLastArticles();
        if (articleIdArrayList == null)
        {
            // Hide all ListView components
            listTitle.setVisibility(View.GONE);
            listTopDivider.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
        }
        else
        {
            //Show all ListView components
            listTitle.setVisibility(View.VISIBLE);
            listTopDivider.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);

            // Get article name for every id and add it to articleArrayList
            for (int articleId : articleIdArrayList) {
                Cursor articleCursor = databaseHandler.getSingleArticle(articleId);
                if (articleCursor.moveToFirst()) {
                    articleArrayList.add(articleCursor.getString(articleCursor.getColumnIndex("a_title")));
                }
            }

            // Setup ListView
            ArticleListAdapter articleListAdapter = new ArticleListAdapter(getApplicationContext(), articleArrayList);
            listView.setAdapter(articleListAdapter);
            articleListAdapter.notifyDataSetChanged();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, ArticleActivity.class);
                    intent.putExtra(Constants.EXTRA_ARTICLE, articleIdArrayList.get(position));
                    startActivity(intent);
                }
            });
        }
    }

    /*
    Callbacks
     */

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.searchButtonLayout:
                startActivity(new Intent(MainActivity.this, BrowseActivity.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        }
        else if (id == R.id.action_download_data) {
            if (isConnected()) {
                new DownloadDatabase().execute();
            } else {
                showConnectionAlert(R.string.main_alert_message);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Methods
     */

    private ArrayList<Integer> getLastArticles()
    {
        Gson gson = new Gson();
        String json = sp.getString(Constants.SP_LAST_ARTICLES, null);
        Type type = new TypeToken<ArrayList<Integer>>(){}.getType();
        return gson.fromJson(json, type);
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showConnectionAlert(int messageStringResource) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setCancelable(false);
        alertBuilder.setTitle(getResources().getString(R.string.main_alert_title));
        alertBuilder.setMessage(getResources().getString(messageStringResource));
        alertBuilder.setPositiveButton(getResources().getString(R.string.main_alert_ok), null);
        alertBuilder.show();
    }

    private void disableSearchButton() {
        searchButtonLayout.setEnabled(false);
        searchButtonLayout.setBackgroundColor(getResources().getColor(R.color.primaryTransparent));
    }

    private void enableSearchButton() {
        searchButtonLayout.setEnabled(true);
        searchButtonLayout.setBackgroundColor(getResources().getColor(R.color.primary));
    }

    /*
    AsyncTask class for downloading data
     */

    private class DownloadDatabase extends AsyncTask<Void, Void, Void> {

        /*
        Variables
         */

        private TableSync tableSync;
        private DatabaseHandler databaseHandler;
        private Gson gson;
        private String jsonResponse;
        private TableObject[] tableObjects;
        private ArrayList<String> pictureNameArrayList;
        private ProgressDialog progressDialog;

        /*
        Lifecycle
         */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Set SP accordingly
            sp.edit().putBoolean(Constants.SP_DATA_DOWNLOADED, false).apply();
            sp.edit().putString(Constants.SP_LAST_ARTICLES, null).apply();

            // Initialize variables
            tableSync = new TableSync(getApplicationContext());
            databaseHandler = new DatabaseHandler(getApplicationContext());
            gson = new Gson();
            pictureNameArrayList = new ArrayList<>();

            // Set up and show ProgressDialog
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle(getResources().getString(R.string.dialog_downloading));
            progressDialog.setMessage(getResources().getString(R.string.dialog_article_data));
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Disable search button
            disableSearchButton();

            // Clear table before filling it up
            databaseHandler.clearTables(new String[]{"Category", "Subcategory", "Article",
                    "Keyword", "Article_Keyword", "Picture", "Article_Picture", "Linked_Articles"});
        }

        @Override
        protected Void doInBackground(Void... params) {

            /*
            Get all rows for every table and insert them in to SQLite
             */

            // Category
            jsonResponse = tableSync.getJsonResponse(TableSync.DB_GET_FULL_TABLE_URL, "3");
            tableObjects = gson.fromJson(jsonResponse, TableObject[].class);
            for (TableObject object : tableObjects) {
                databaseHandler.insertCategory(object.getCategoryId(), object.getCategoryName());
            }

            // Subcategory
            jsonResponse = tableSync.getJsonResponse(TableSync.DB_GET_FULL_TABLE_URL, "7");
            tableObjects = gson.fromJson(jsonResponse, TableObject[].class);
            for (TableObject object : tableObjects) {
                databaseHandler.insertSubcategory(object.getSubcategoryId(), object.getSubcategoryName(),
                        object.getCategoryId());
            }

            // Article
            jsonResponse = tableSync.getJsonResponse(TableSync.DB_GET_FULL_TABLE_URL, "0");
            tableObjects = gson.fromJson(jsonResponse, TableObject[].class);
            for (TableObject object : tableObjects) {
                databaseHandler.insertArticle(object.getArticleId(), object.getArticleTitle(),
                        object.getArticleContent(), object.getSubcategoryId());
            }

            // Keyword
            jsonResponse = tableSync.getJsonResponse(TableSync.DB_GET_FULL_TABLE_URL, "4");
            tableObjects = gson.fromJson(jsonResponse, TableObject[].class);
            for (TableObject object : tableObjects) {
                databaseHandler.insertKeyword(object.getKeywordId(), object.getKeywordName());
            }

            // ArticleKeyword
            jsonResponse = tableSync.getJsonResponse(TableSync.DB_GET_FULL_TABLE_URL, "1");
            tableObjects = gson.fromJson(jsonResponse, TableObject[].class);
            for (TableObject object : tableObjects) {
                databaseHandler.insertArticleKeyword(object.getArticleKeywordId(), object.getKeywordId(),
                        object.getArticleId());
            }

            // Picture
            jsonResponse = tableSync.getJsonResponse(TableSync.DB_GET_FULL_TABLE_URL, "6");
            tableObjects = gson.fromJson(jsonResponse, TableObject[].class);
            for (TableObject object : tableObjects) {
                databaseHandler.insertPicture(object.getPictureId(), object.getPictureTitle(),
                        object.getPictureContent(), object.getPictureFileName());
                pictureNameArrayList.add(object.getPictureFileName());
            }

            // ArticlePicture
            jsonResponse = tableSync.getJsonResponse(TableSync.DB_GET_FULL_TABLE_URL, "2");
            tableObjects = gson.fromJson(jsonResponse, TableObject[].class);
            for (TableObject object : tableObjects) {
                databaseHandler.insertArticlePicture(object.getArticlePictureId(), object.getArticleId(),
                        object.getPictureId());
            }

            // LinkedArticles
            jsonResponse = tableSync.getJsonResponse(TableSync.DB_GET_FULL_TABLE_URL, "5");
            tableObjects = gson.fromJson(jsonResponse, TableObject[].class);
            for (TableObject object : tableObjects) {
                databaseHandler.insertLinkedArticles(object.getArticleArticleId(), object.getArticleAId(),
                        object.getArticleBId());
            }

            // Download pictures
            tableSync.downloadPictures(pictureNameArrayList);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            sp.edit().putBoolean(Constants.SP_DATA_DOWNLOADED, true).apply();
            progressDialog.dismiss();
            enableSearchButton();
            // Call onResume again to make sure listview no longer shows last articles
            onResume();
        }
    }
}
