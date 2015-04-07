package com.cmput301.cs.project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.cmput301.cs.project.models.Comment;

import java.util.List;

/**
 * Adapts a comment into a simple_list_item_activated_2.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {

    private static final class ViewHolder {
        private final TextView comment;
        private final TextView approver;

        private ViewHolder(View parent) {
            approver = (TextView) parent.findViewById(android.R.id.text1);
            comment = (TextView) parent.findViewById(android.R.id.text2);
        }
    }

    private final LayoutInflater mInflater;


    public CommentAdapter(Context context, List<Comment> objects) {
        super(context, android.R.layout.simple_list_item_activated_2, objects);

        mInflater = LayoutInflater.from(context);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_activated_2, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Comment comment = getItem(position);
        holder.approver.setText(comment.getApprover().getUserName());
        holder.comment.setText(comment.getText());

        return convertView;
    }
}
