package com.cmput301.cs.project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.cmput301.cs.project.controllers.TagsChangedListener;
import com.cmput301.cs.project.controllers.TagsManager;
import com.cmput301.cs.project.model.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TagsAdapter extends BaseAdapter implements TagsChangedListener {

    private static class ViewHolder {
        private final TextView mTextView;

        private ViewHolder(View parent) {
            mTextView = (TextView) parent.findViewById(android.R.id.text1);
        }
    }

    private final LayoutInflater mInflater;
    private final List<Tag> mTags = new ArrayList<Tag>();

    public TagsAdapter(Context context) {
        this(context, TagsManager.get(context).peekTags());
    }

    public TagsAdapter(Context context, Collection<? extends Tag> tags) {
        mInflater = LayoutInflater.from(context);
        mTags.addAll(tags);
        TagsManager.get(context).addTagChangedListener(this);
    }

    @Override
    public int getCount() {
        return mTags.size();
    }

    @Override
    public Tag getItem(int position) {
        return mTags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_activated_1, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Tag tag = getItem(position);

        holder.mTextView.setText(tag.getName());

        return convertView;
    }

    @Override
    public void onTagRenamed(Tag tag, String oldName) {
        final String id = tag.getId();
        for (int i = 0, tagsSize = mTags.size(); i < tagsSize; i++) {
            final Tag t = mTags.get(i);
            if (t.getId().equals(id)) {
                mTags.remove(i);
                mTags.add(i, tag);
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onTagDeleted(Tag tag) {
        final String id = tag.getId();
        for (Iterator<Tag> iterator = mTags.iterator(); iterator.hasNext(); ) {
            final Tag t = iterator.next();
            if (t.getId().equals(id)) {
                iterator.remove();
                notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    public void onTagCreated(Tag tag) {
        mTags.add(tag);
        notifyDataSetChanged();
    }
}
