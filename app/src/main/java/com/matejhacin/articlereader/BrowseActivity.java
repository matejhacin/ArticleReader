package com.matejhacin.articlereader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.matejhacin.classes.Constants;
import com.matejhacin.database.DatabaseHandler;

import java.util.ArrayList;


public class BrowseActivity extends AppCompatActivity {

    // Views
    ExpandableListView eListView;

    // Data
    DatabaseHandler dbHandler;
    ArrayList<String> parents;
    ArrayList<ArrayList<String>> children;
    ArrayList<int[]> subcategoryIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_browse);

        // Views
        eListView = (ExpandableListView) findViewById(R.id.expandableListView);

        // Data
        dbHandler = new DatabaseHandler(getApplicationContext());
        parents = new ArrayList<>();
        children = new ArrayList<>();
        subcategoryIds = new ArrayList<>();

        // Get categories and Subcategories
        Cursor categoryCursor = dbHandler.getCategories();
        if (categoryCursor.moveToFirst()) {
            do {
                // Add category do parents
                parents.add(categoryCursor.getString(categoryCursor.getColumnIndex("c_name")));

                // Get subcategories for this category and add it to children
                ArrayList<String> child = new ArrayList<>();
                // Create table of ids of categories because we need it in ArticleListActivity
                int[] ids = new int[999];
                int indeks = 0;
                Cursor subcategoryCursor = dbHandler.getSubcategories(
                        categoryCursor.getInt(categoryCursor.getColumnIndex("c_id"))
                );
                if (subcategoryCursor.moveToFirst()) {
                    do {
                        child.add(subcategoryCursor.getString(subcategoryCursor.getColumnIndex("s_name")));
                        ids[indeks++] = subcategoryCursor.getInt(subcategoryCursor.getColumnIndex("s_id"));
                    } while (subcategoryCursor.moveToNext());
                }
                children.add(child);
                subcategoryIds.add(ids);

            } while (categoryCursor.moveToNext());
        }

        // Setting up ExpandableListView
        ExpandableListAdapter adapter = new ExpandableListAdapter() {

            @Override
            public void registerDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver observer) {

            }

            @Override
            public int getGroupCount() {
                return parents.size();
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                ArrayList<String> currentChildren = (ArrayList<String>) children.get(groupPosition);
                return currentChildren.size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return parents.get(groupPosition);
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                ArrayList<String> currentChildren = (ArrayList<String>) children.get(groupPosition);
                return currentChildren.get(childPosition);
            }

            @Override
            public long getGroupId(int groupPosition) {
                return 0;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                if (convertView == null)
                {
                    convertView = getLayoutInflater().inflate(R.layout.explist_group, null);
                }
                TextView tw = (TextView) convertView.findViewById(R.id.twGroup);
                tw.setText(Html.fromHtml(parents.get(groupPosition)));
                return convertView;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                ArrayList<String> currentChildren = (ArrayList<String>) children.get(groupPosition);
                if (convertView == null)
                {
                    convertView = getLayoutInflater().inflate(R.layout.explist_child, null);
                }
                TextView tw = (TextView) convertView.findViewById(R.id.twChild);
                tw.setText(Html.fromHtml(currentChildren.get(childPosition)));
                return convertView;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }

            @Override
            public boolean areAllItemsEnabled() {
                return true;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public void onGroupExpanded(int groupPosition) {

            }

            @Override
            public void onGroupCollapsed(int groupPosition) {

            }

            @Override
            public long getCombinedChildId(long groupId, long childId) {
                return 0;
            }

            @Override
            public long getCombinedGroupId(long groupId) {
                return 0;
            }
        };

        eListView.setAdapter(adapter);
        eListView.setGroupIndicator(null);

        // ExpandableListView click listener
        eListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // Create intent and add category, subcategory extra and start activity
                Intent intent = new Intent(BrowseActivity.this, ArticleListActivity.class);
                intent.putExtra(Constants.EXTRA_SUBCATEGORY, subcategoryIds.get(groupPosition)[childPosition]);
                intent.putExtra(Constants.EXTRA_CATEGORY_NAME, parents.get(groupPosition));
                intent.putExtra(Constants.EXTRA_SUBCATEGORY_NAME, children.get(groupPosition).get(childPosition));
                startActivity(intent);
                return false;
            }
        });
    }

    /*
    Callbacks
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_browse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.searchButton:
                showSearchDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Methods
     */

    private void showSearchDialog() {

        // Variables
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BrowseActivity.this);
        final EditText input = new EditText(BrowseActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        input.setLayoutParams(layoutParams);
        input.setSingleLine();

        // Create the dialog
        alertDialogBuilder.setTitle(getResources().getString(R.string.browse_dialog_title));
        alertDialogBuilder.setView(input);
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.browse_dialog_search), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.browse_dialog_empty), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(BrowseActivity.this, ArticleListActivity.class);
                    intent.putExtra(Constants.EXTRA_SEARCH, true);
                    intent.putExtra(Constants.EXTRA_SEARCH_PHRASE, input.getText().toString());
                    startActivity(intent);
                }
            }
        });
        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.browse_dialog_cancel), null);
        alertDialogBuilder.show();
    }
}
