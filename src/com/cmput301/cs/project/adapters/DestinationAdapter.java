package com.cmput301.cs.project.adapters;

/**
 * Simple adapter for displaying a destination in a simple_list_item_activated_2
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.cmput301.cs.project.models.Destination;

import java.util.Collection;

/**
 * Basic adapter for displaying {@link Destination Destinations}.
 */
public class DestinationAdapter extends ArrayAdapter<Destination> {

    private static final class ViewHolder {
        private final TextView destination;
        private final TextView reason;

        private ViewHolder(View parent) {
            destination = (TextView) parent.findViewById(android.R.id.text1);
            reason = (TextView) parent.findViewById(android.R.id.text2);
        }
    }

    private final LayoutInflater mInflater;


    public DestinationAdapter(Context context, Collection<Destination> destinations) {
        super(context, android.R.layout.simple_list_item_activated_2);

        mInflater = LayoutInflater.from(context);
        addAll(destinations);
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

        final Destination destination = getItem(position);
        holder.destination.setText(destination.getName());
        holder.reason.setText(destination.getReason());

        return convertView;
    }
}
