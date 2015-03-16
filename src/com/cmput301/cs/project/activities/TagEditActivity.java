package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.controllers.TagsManager;
import com.cmput301.cs.project.model.Tag;
import com.cmput301.cs.project.utils.Utils;

/**
 * An activity that is called when a new tag is created or a tag is clicked on 
 * {@link com.cmput301.cs.project.activites.TagManagerActivity TagManagerActivity}.</br>
 * Allows the tag to be deleted from the app. </br>
 * Contains a String and generates a unique ID which is stored in a {@link com.cmput301.cs.project.model.Tag Tag}.
 * 
 * @author rozsa
 *
  */

public class TagEditActivity extends Activity {
    public static final String KEY_TAG = "key_tag";
    public static final String KEY_TAG_ID = "key_tag_id";

    public static Intent intentWithTag(Context context, Tag tag) {
        return new Intent(context, TagEditActivity.class).putExtra(KEY_TAG_ID, tag.getId());
    }

    private Tag mTag;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag = tryFindingTag();

        Utils.setupDiscardDoneBar(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mEditText.getText().toString();
                if (!name.trim().isEmpty()) {
                    final TagsManager manager = TagsManager.get(TagEditActivity.this);
                    if (mTag != null) {
                        mTag = manager.renameTag(mTag, name);
                    } else {
                        manager.getTagByName(name);  // create if needed
                    }
                    finish();
                }
            }
        });
        setContentView(R.layout.tag_edit_activity);

        mEditText = (EditText) findViewById(R.id.tag);
        if (mTag != null) {
            mEditText.setText(mTag.getName());
        }

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTag != null) {
                    TagsManager.get(TagEditActivity.this).deleteTagById(mTag.getId());
                }
                finish();
            }
        });
    }

    private Tag tryFindingTag() {
        final String id = getIntent().getStringExtra(KEY_TAG_ID);
        return TagsManager.get(this).findTagById(id);
    }
}
