package com.cmput301.cs.project.project.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.project.adapters.TagsAdapter;

public class TagManagerActivity extends ListActivity {

    private TagsAdapter mTagsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTagsAdapter = new TagsAdapter(this);
        setListAdapter(mTagsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tag_manager_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                startActivity(new Intent(this, TagEditActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        startActivity(TagEditActivity.intentWithTag(this, mTagsAdapter.getItem(position)));
    }
}
