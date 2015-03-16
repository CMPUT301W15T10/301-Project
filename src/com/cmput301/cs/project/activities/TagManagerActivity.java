package com.cmput301.cs.project.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.adapters.TagsAdapter;
import com.cmput301.cs.project.controllers.TagsManager;

/**
 * An activity that shows a list of {@link com.cmput301.cs.project.models.Tag Tags} that can be clicked to be edited.
 * Also has a menu button for the addition of new tags. Both of these call 
 * {@link com.cmput301.cs.project.activities.TagEditActivity TagEditActivity}. 
 * 
 * @author rozsa
 *
 */

public class TagManagerActivity extends ListActivity {

    private TagsAdapter mTagsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTagsAdapter = new TagsAdapter(this);
        TagsManager.get(this).addTagChangedListener(mTagsAdapter);
        setListAdapter(mTagsAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TagsManager.get(this).removeTagChangedListener(mTagsAdapter);
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
