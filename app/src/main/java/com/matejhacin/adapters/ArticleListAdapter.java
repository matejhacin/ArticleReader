package com.matejhacin.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.matejhacin.articlereader.R;

import java.util.ArrayList;

public class ArticleListAdapter extends ArrayAdapter<String> {

    /*
    Variables
     */

    private Context context;
    private ArrayList<String> articleArrayList;

    /*
    Constructor
     */

    public ArticleListAdapter(Context context, ArrayList<String> articleArrayList) {
        super(context, R.layout.explist_child);
        this.context = context;
        this.articleArrayList = articleArrayList;
    }

    /*
    Callbacks
     */

    @Override
    public int getCount() {
        return articleArrayList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate layout item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.explist_child, parent, false);

        // Get textview
        TextView itemTextView = (TextView) view.findViewById(R.id.twChild);

        // Set TextView value
        itemTextView.setText(Html.fromHtml(articleArrayList.get(position)));

        return view;
    }
}
