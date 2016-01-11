package com.matejhacin.classes;

public class TableObject {

    /*
    Variables
     */

    private String c_name; // Category name
    private String s_name; // Subcategory name
    private int c_id; // Category ID
    private String a_title; // Article title
    private String a_content; // Article content
    private int s_id; // Subcategory id
    private String k_name; // Keyword name
    private int ak_id; // ArticleKeyword id
    private int k_id; // Keyword id
    private String p_title; // Picture title
    private String p_content; // Picture content
    private String p_filename; // Picture filename
    private int ap_id; // ArticlePicture id
    private int a_id; // Article id
    private int p_id; // Picture id
    private int aa_id; // ArticleArticle id
    private int article_a_id; // Article A id
    private int article_b_id; // Article B id

    /*
    Getters
     */

    public String getCategoryName() {
        return c_name;
    }

    public String getSubcategoryName() {
        return s_name;
    }

    public int getCategoryId() {
        return c_id;
    }

    public String getArticleTitle() {
        return a_title;
    }

    public String getArticleContent() {
        return a_content;
    }

    public int getSubcategoryId() {
        return s_id;
    }

    public String getKeywordName() {
        return k_name;
    }

    public int getArticleKeywordId() {
        return ak_id;
    }

    public int getKeywordId() {
        return k_id;
    }

    public String getPictureTitle() {
        return p_title;
    }

    public String getPictureContent() {
        return p_content;
    }

    public String getPictureFileName() {
        return p_filename;
    }

    public int getArticlePictureId() {
        return ap_id;
    }

    public int getArticleId() {
        return a_id;
    }

    public int getPictureId() {
        return p_id;
    }

    public int getArticleArticleId() {
        return aa_id;
    }

    public int getArticleAId() {
        return article_a_id;
    }

    public int getArticleBId() {
        return article_b_id;
    }
}
