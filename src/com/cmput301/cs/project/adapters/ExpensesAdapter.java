/*
 * Copyright 2015 Edmond Chui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cmput301.cs.project.adapters;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.controllers.SettingsController;
import com.cmput301.cs.project.models.Destination;
import com.cmput301.cs.project.models.Expense;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Basic adapter that adapts an {@link com.cmput301.cs.project.models.Expense Expense} to be viewable in a ListView.
 * WARNING: This creates a COPY of the list. This is because it will change the list which doesn't work with unmodifiable lists
 *
 * @author rozsa
 */

public final class ExpensesAdapter extends ArrayAdapter<Expense> {

    private static final float DISTANCE_CITY = 200f * 1000;  // 200km: Edmonton <-> Calgary
    private static final float DISTANCE_GLOBE = 800f * 1000;  // 800km: Edmonton <-> Vancouver

    private static final class ViewHolder {
        private final TextView date;
        private final TextView category;
        private final TextView description;
        private final TextView amount;
        private final TextView receipt;
        private final TextView complete;
        private final TextView geolocation;
        private final View colour;

        private ViewHolder(View parent) {
            date = (TextView) parent.findViewById(R.id.date);
            geolocation = (TextView) parent.findViewById(R.id.geolocation);
            complete = (TextView) parent.findViewById(R.id.complete);
            receipt = (TextView) parent.findViewById(R.id.receipt);
            amount = (TextView) parent.findViewById(R.id.amount);
            description = (TextView) parent.findViewById(R.id.description);
            category = (TextView) parent.findViewById(R.id.category);
            colour = parent.findViewById(R.id.distance_colour);
        }
    }

    private final LayoutInflater mInflater;
    private final DateFormat mFormatter;
    private LatLng mHome;

    /**
     * WARNING: This creates a COPY of the list. This is because it will change the list when sorting, which doesn't work with unmodifiable lists
     */
    public ExpensesAdapter(Context context, List<Expense> expenses) {
        super(context, R.layout.expense_list_item, new ArrayList<Expense>(expenses));
        mInflater = LayoutInflater.from(context);
        mFormatter = android.text.format.DateFormat.getMediumDateFormat(context);  // with respect to user settings
        final Destination destination = SettingsController.get(context).loadHomeAsDestination();
        if (destination != null) {
            mHome = destination.getLocation();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.expense_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Expense expense = getItem(position);

        holder.date.setText(mFormatter.format(new Date(expense.getTime())));
        holder.category.setText(expense.getCategory());
        holder.description.setText(expense.getDescription());
        holder.amount.setText(expense.getAmount().toString());

        holder.receipt.setText(expense.hasReceipt() ? "Receipt attached." : "");
        holder.complete.setText(expense.isCompleted() ? "" : "Incomplete");

        final Destination destination = expense.getDestination();
        if (destination != null) {
            holder.colour.setBackgroundColor(colourForLatLng(destination.getLocation()));
            holder.geolocation.setText(destination.getName());
        }

        return convertView;
    }

    private int colourForLatLng(LatLng latLng) {
        if (mHome == null || latLng == null) return Color.TRANSPARENT;

        final float distance = distanceFromHome(latLng);
        if (distance > DISTANCE_GLOBE) {
            return Color.RED;
        } else if (distance > DISTANCE_CITY) {
            return Color.YELLOW;
        } else {
            return Color.GREEN;
        }
    }

    private float distanceFromHome(LatLng latLng) {
        final double homeLat = mHome.latitude;
        final double homeLong = mHome.longitude;
        final double targetLat = latLng.latitude;
        final double targetLong = latLng.longitude;
        final float[] result = new float[1];
        Location.distanceBetween(homeLat, homeLong, targetLat, targetLong, result);
        return result[0];
    }
}