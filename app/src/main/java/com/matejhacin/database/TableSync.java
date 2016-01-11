package com.matejhacin.database;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.util.FileUtility;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TableSync {

    private Context context;

    //URLS
    public static String DB_GET_FULL_TABLE_URL = "http://matejhacin.com/other_projects/waldorfska_sola/db_get_full_table.php";
    public static String DB_PICTURES_URL = "http://matejhacin.com/other_projects/waldorfska_sola/images/";

    // Constructor
    public TableSync(Context context)
    {
        this.context = context;
    }

    /*
    Get JSON from URL
     */
    public String getJsonResponse(String url, String tableNumber) {
        OkHttpClient okHttp = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(new FormEncodingBuilder()
                        .add("id", tableNumber).build())
                .build();
        try{
            Response response = okHttp.newCall(request).execute();
            String json = response.body().string();
            return json.substring(23);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    Get list of table ids
     */
    public int[] getTableIdsFromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, int[].class);
    }

    /*
    Download all pictures
     */
    public void downloadPictures(ArrayList<String> pictureNames) {

        // Variables
        String path = Environment.getExternalStorageDirectory() + "/WaldorfPictures/";
        File folder = new File(path);
        boolean createdSuccessful = false;

        // Delete folder and it's components first
        FileUtility.deleteDirectory(folder);

        // Create the empty folder again
        if (!folder.exists()) {
            createdSuccessful = folder.mkdir();
        }
        if (createdSuccessful) {
            Log.i("Status", "Picture folder created");
        } else {
            Log.i("Status", "Picture folder couldn't be created!");
            return;
        }

        for (String name : pictureNames)
        {
            Ion.with(context)
                    .load(DB_PICTURES_URL + name)
                    .write(new File(path + name));
        }
    }
}
