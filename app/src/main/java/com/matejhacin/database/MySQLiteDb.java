package com.matejhacin.database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteDb extends SQLiteOpenHelper {

    // Static table "numbers"
    public static String ARTICLE_NUMBER = "0";
    public static String ARTICLE_KEYWORD_NUMBER = "1";
    public static String ARTICLE_PICTURE_NUMBER = "2";
    public static String CATEGORY_NUMBER = "3";
    public static String KEYWORD_NUMBER = "4";
    public static String LINKED_ARTICLES_NUMBER = "5";
    public static String PICTURE_NUMBER = "6";
    public static String SUBCATEGORY_NUMBER = "7";

    // Variables
    public static String DB_NAME = "waldorf_articles.db";
    public static int DB_VERSION = 1;

    /*
    Category
     */
    String qCategory = "CREATE TABLE Category (" +
            "c_id INTEGER NOT NULL PRIMARY KEY, " +
            "c_name TEXT NOT NULL);";

    /*
    Subcategory
     */
    String qSubcategory = "CREATE TABLE Subcategory (" +
            "s_id INTEGER NOT NULL PRIMARY KEY, " +
            "s_name TEXT NOT NULL, " +
            "c_id INTEGER NOT NULL);";

    /*
    Article
     */
    String qArticle = "CREATE TABLE Article (" +
            "a_id INTEGER NOT NULL PRIMARY KEY, " +
            "a_title TEXT NOT NULL, " +
            "a_content TEXT NOT NULL, " +
            "s_id INTEGER NOT NULL);";

    /*
    Keyword
     */
    String qKeyword = "CREATE TABLE Keyword (" +
            "k_id INTEGER NOT NULL PRIMARY KEY, " +
            "k_name TEXT NOT NULL);";

    /*
    Article_Keyword
     */
    String qArticleKeyword = "CREATE TABLE Article_Keyword (" +
            "ak_id INTEGER NOT NULL PRIMARY KEY, " +
            "k_id INTEGER NOT NULL, " +
            "a_id INTEGER NOT NULL);";

    /*
    Picture
     */
    String qPicture = "CREATE TABLE Picture (" +
            "p_id INTEGER NOT NULL PRIMARY KEY, " +
            "p_title TEXT NOT NULL, " +
            "p_content TEXT NOT NULL, " +
            "p_filename TEXT NOT NULL);";

    /*
    Article_Picture
     */
    String qArticlePicture = "CREATE TABLE Article_Picture (" +
            "ap_id INTEGER NOT NULL PRIMARY KEY, " +
            "a_id INTEGER NOT NULL, " +
            "p_id INTEGER NOT NULL);";

    /*
    Linked_Articles
     */
    String qLinkedArticles = "CREATE TABLE Linked_Articles (" +
            "aa_id INTEGER NOT NULL PRIMARY KEY, " +
            "article_a_id INTEGER NOT NULL, " +
            "article_b_id INTEGER NOT NULL);";

    public MySQLiteDb(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase db) {
        db.execSQL(qCategory);
        db.execSQL(qSubcategory);
        db.execSQL(qArticle);
        db.execSQL(qKeyword);
        db.execSQL(qArticleKeyword);
        db.execSQL(qPicture);
        db.execSQL(qArticlePicture);
        db.execSQL(qLinkedArticles);
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Category");
        db.execSQL("DROP TABLE IF EXISTS Subcategory");
        db.execSQL("DROP TABLE IF EXISTS Article");
        db.execSQL("DROP TABLE IF EXISTS Keyword");
        db.execSQL("DROP TABLE IF EXISTS Article_Keyword");
        db.execSQL("DROP TABLE IF EXISTS Picture");
        db.execSQL("DROP TABLE IF EXISTS Article_Picture");
        db.execSQL("DROP TABLE IF EXISTS Linked_Articles");
        onCreate(db);
    }
}
