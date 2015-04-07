package com.cmput301.cs.project.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import com.cmput301.cs.project.models.Comment;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment> {

    public CommentAdapter(Context context, int resource, List<Comment> objects) {
        super(context, resource, objects);
    }
}
