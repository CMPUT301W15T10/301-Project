package com.cmput301.cs.project.adapters;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.models.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter to create all tags with a checkbox
 *
 * Gives a listener for each on check
 *
 * @author Morgan
 */

public class TagsCheckboxesAdapter extends ArrayAdapter<Tag> {

    private static class ViewHolder {
        private final TextView mText;
        private final CheckBox mCheckBox;

        private ViewHolder(View parent) {
            mText = (TextView) parent.findViewById(R.id.text);
            mCheckBox = (CheckBox) parent.findViewById(R.id.checkBox);
        }
    }

    private final List<Tag> mWantedTags;
    private final LayoutInflater mInflater;
    private final CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

    public TagsCheckboxesAdapter(Context context, CompoundButton.OnCheckedChangeListener onCheckedChangeListener, List<Tag> tags, ArrayList<Tag> wantedTags) {
        super(context, R.layout.tag_selector_item, tags);

        mInflater = LayoutInflater.from(context);
        mOnCheckedChangeListener = onCheckedChangeListener;
        mWantedTags = wantedTags;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.tag_selector_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Tag tag = getItem(position);
        holder.mText.setText(tag.getName());
        holder.mCheckBox.setTag(tag);

        holder.mCheckBox.setOnCheckedChangeListener(null);

        if (mWantedTags.contains(tag)) {
            holder.mCheckBox.setChecked(true);
        } else {
            holder.mCheckBox.setChecked(false);
        }

        holder.mCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);

        return convertView;
    }
}
