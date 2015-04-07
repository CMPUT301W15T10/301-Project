package com.cmput301.cs.project.activities;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.utils.Utils;

/**
 * Comment activity returns a result with a comment in the intent as {@literal CommentActivity.COMMENT_TEXT}
 *
 * Used as an approver to add comments when returning and approving
 *
 * @author Blaine
 */

public class CommentActivity extends Activity {
    public static final String COMMENT_TEXT = "COMMENT";
    private EditText mComment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_activity);

        mComment = (EditText) findViewById(R.id.comment);

        setUpActionBar();

    }
    public void finishComment() {
        if(mComment.getText().toString().isEmpty()) {
            mComment.setError("Comment cannot be empty");
        } else {
            Intent data = new Intent();
            data.putExtra(COMMENT_TEXT, mComment.getText().toString());

            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void setUpActionBar() {
        Utils.setupDiscardDoneBar(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishComment();
            }
        });
    }

}