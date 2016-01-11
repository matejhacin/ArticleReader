package com.matejhacin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DatabaseHandler {

    private MySQLiteDb myDb;
    private SQLiteDatabase db;

    public DatabaseHandler(Context context)
    {
        myDb = new MySQLiteDb(context);
    }

    void open()
    {
        db = myDb.getWritableDatabase();
    }

    void close()
    {
        if (db != null) db.close();
    }

    /*
    Get tableName id
     */
    private String getTableIdName(String tableName)
    {
        String idName = null;
        switch(tableName)
        {
            case "Category":
                idName = "c_id";
                break;
            case "Subcategory":
                idName = "s_id";
                break;
            case "Article":
                idName = "a_id";
                break;
            case "Keyword":
                idName = "k_id";
                break;
            case "Article_Keyword":
                idName = "ak_id";
                break;
            case "Picture":
                idName = "p_id";
                break;
            case "Article_Picture":
                idName = "ap_id";
                break;
            case "Linked_Articles":
                idName = "aa_id";
                break;
        }
        return idName;
    }

    /*
    Category
     */
    public int insertCategory(int c_id, String c_name)
    {
        ContentValues cv = new ContentValues();
        cv.put("c_id", c_id);
        cv.put("c_name", c_name);

        open();
        return (int)db.insert("Category", null, cv);
    }

    /*
    Subcategory
     */
    public int insertSubcategory(int s_id, String s_name, int c_id)
    {
        ContentValues cv = new ContentValues();
        cv.put("s_id", s_id);
        cv.put("s_name", s_name);
        cv.put("c_id", c_id);

        open();
        return (int)db.insert("Subcategory", null, cv);
    }

    /*
    Article
     */
    public int insertArticle(int a_id, String a_title ,String a_content, int s_id)
    {
        ContentValues cv = new ContentValues();
        cv.put("a_id", a_id);
        cv.put("a_title", a_title);
        cv.put("a_content", a_content);
        cv.put("s_id", s_id);

        open();
        return (int)db.insert("Article", null, cv);
    }

    /*
    Keyword
     */
    public int insertKeyword(int k_id, String k_name)
    {
        ContentValues cv = new ContentValues();
        cv.put("k_id", k_id);
        cv.put("k_name", k_name);

        open();
        return (int)db.insert("Keyword", null, cv);
    }

    /*
    Article_Keyword
     */
    public int insertArticleKeyword(int ak_id, int k_id, int a_id)
    {
        ContentValues cv = new ContentValues();
        cv.put("ak_id", ak_id);
        cv.put("k_id", k_id);
        cv.put("a_id", a_id);

        open();
        return (int)db.insert("Article_Keyword", null, cv);
    }

    /*
    Picture
     */
    public int insertPicture(int p_id, String p_title, String p_content, String p_filename)
    {
        ContentValues cv = new ContentValues();
        cv.put("p_id", p_id);
        cv.put("p_title", p_title);
        cv.put("p_content", p_content);
        cv.put("p_filename", p_filename);

        open();
        return (int)db.insert("Picture", null, cv);
    }

    /*
    Article_Picture
     */
    public int insertArticlePicture(int ap_id, int a_id, int p_id)
    {
        ContentValues cv = new ContentValues();
        cv.put("ap_id", ap_id);
        cv.put("a_id", a_id);
        cv.put("p_id", p_id);

        open();
        return (int)db.insert("Article_Picture", null, cv);
    }

    /*
    Linked_Articles
     */
    public int insertLinkedArticles(int aa_id, int article_a_id, int article_b_id)
    {
        ContentValues cv = new ContentValues();
        cv.put("aa_id", aa_id);
        cv.put("article_a_id", article_a_id);
        cv.put("article_b_id", article_b_id);

        open();
        return (int)db.insert("Linked_Articles", null, cv);
    }

    /*
    Delete row
     */
    public int deleteRow(int row_id, String tableName)
    {
        String idName = getTableIdName(tableName);
        open();
        return db.delete(tableName, idName+"="+row_id, null);
    }

    /*
    Query for ids from some table
     */
    public Cursor getTableIds(String tableName)
    {
        String idName = getTableIdName(tableName);
        String sql = "SELECT "+idName+" " +
                "FROM "+tableName;
        open();
        return db.rawQuery(sql, null);
    }

    /*
    Query for categories
     */
    public Cursor getCategories()
    {
        open();
        return db.query(
                "Category",
                null, null, null, null, null, null);
    }

    /*
    Query for subcategory that belong to a c_id
     */
    public Cursor getSubcategories(int c_id)
    {
        String sql = "SELECT s_id, s_name " +
                "FROM Subcategory s " +
                "INNER JOIN Category c ON s.c_id=c.c_id " +
                "WHERE s.c_id="+c_id;
        open();
        return db.rawQuery(sql, null);
    }

    /*
    Query for articles that belong to s_id
     */
    public Cursor getArticles(int s_id)
    {
        String sql = "SELECT a_id, a_title " +
                "FROM Article a " +
                "INNER JOIN Subcategory s ON a.s_id=s.s_id " +
                "WHERE a.s_id="+s_id;
        open();
        return db.rawQuery(sql, null);
    }

    /*
    Query for article title, content
     */
    public Cursor getSingleArticle(int a_id)
    {
        String sql = "SELECT a_title, a_content " +
                "FROM Article " +
                "WHERE a_id="+a_id;
        open();
        return db.rawQuery(sql, null);
    }

    /*
    Query for all keywords and their ids
     */

    public Cursor getKeywords()
    {
        String sql = "SELECT k_id, k_name " +
                "FROM Keyword";
        open();
        return db.rawQuery(sql, null);
    }

    /*
    Query for articles that belong to a certain keyword or a searchString
     */

    public Cursor getArticlesRelatedToKeywords(String[] keywordIds)
    {
        // Create a string with the correct number of question marks
        // so that we can use IN
        String questionMarks = "";
        for (int i = 0; i < keywordIds.length; i++) {
            if (i == 0) questionMarks += "?";
            else questionMarks += ", ?";
        }
        // Create query
        String sql = "SELECT a.a_id, a.a_title " +
                "FROM Article a " +
                "INNER JOIN Article_Keyword ak ON a.a_id=ak.a_id " +
                "WHERE ak.k_id IN (" + questionMarks + ")";
        open();
        return db.rawQuery(sql, keywordIds);
    }

    /*
    Query for picture titles, descriptions and filenames related to the article a_id
     */
    public Cursor getPictures(int a_id)
    {
        String sql = "SELECT p_title, p_content, p_filename " +
                "FROM Picture p " +
                "INNER JOIN Article_Picture ap ON p.p_id=ap.p_id " +
                "INNER JOIN Article a ON ap.a_id=a.a_id " +
                "WHERE ap.a_id="+a_id;
        open();
        return db.rawQuery(sql, null);
    }

    /*
    Query for articles that are linked to a certain article
     */
    public Cursor getRelatedArticles(int a_id)
    {
        String sql = "SELECT a_id, a_title " +
                "FROM Article " +
                "WHERE a_id " +
                "IN (SELECT article_b_id FROM Linked_Articles WHERE article_a_id="+a_id+")";
        open();
        return db.rawQuery(sql, null);
    }

    /*
    Query for all articles
     */
    public Cursor getAllArticles()
    {
        String sql = "SELECT a_id, a_title " +
                "FROM Article";
        open();
        return db.rawQuery(sql, null);
    }

    /*
    Completely wipe a certain table or tables
     */
    public void clearTables(String[] tableNames)
    {
        for (String tableName : tableNames)
        {
            String sql = "DELETE FROM "+tableName+";";
            open();
            db.execSQL(sql);
        }
    }
}
