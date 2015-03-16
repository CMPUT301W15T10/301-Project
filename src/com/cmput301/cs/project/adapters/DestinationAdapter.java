package com.cmput301.cs.project.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.cmput301.cs.project.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DestinationAdapter extends ArrayAdapter<Pair<String, String>> {

    private static final class ViewHolder {
        private final TextView destination;
        private final TextView reason;

        private ViewHolder(View parent) {
            destination = (TextView) parent.findViewById(android.R.id.text1);
            reason = (TextView) parent.findViewById(android.R.id.text2);
        }
    }

    private final LayoutInflater mInflater;
    private final Context mContext;


    public DestinationAdapter(Context context, Map<String, String> destinations) {
        super(context, android.R.layout.simple_list_item_activated_2);



        mContext = context;
        mInflater = LayoutInflater.from(context);

        for(Map.Entry<String, String> destination : destinations.entrySet()){
            add(new Pair<String, String>(destination.getKey(), destination.getValue()));
        }


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

        final Pair<String, String> destination = getItem(position);
        holder.destination.setText(destination.first);
        holder.reason.setText(destination.second);

        return convertView;
    }
}
