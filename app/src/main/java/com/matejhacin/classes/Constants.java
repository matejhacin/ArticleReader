package com.matejhacin.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;

import java.util.ArrayList;

public class Constants {

    /*
    Variables
     */

    // Static SharedPreferences Strings
    public static final String SP_KEY = "waldorfSharedPreferences";
    public static final String SP_DATA_DOWNLOADED = "dataDownloaded";
    public static final String SP_LAST_ARTICLES ="lastArticles";

    // Static Extra Strings
    public static final String EXTRA_SUBCATEGORY = "subcategory";
    public static final String EXTRA_ARTICLE = "article";
    public static final String EXTRA_CATEGORY_NAME = "categoryName";
    public static final String EXTRA_SUBCATEGORY_NAME = "subcategoryName";
    public static final String EXTRA_ARTICLE_TITLE = "articleTitle";
    public static final String EXTRA_SEARCH = "search";
    public static final String EXTRA_SEARCH_PHRASE ="searchPhrase";
}
