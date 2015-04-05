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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.models.Expense;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Basic adapter that adapts an {@link com.cmput301.cs.project.models.Expense Expense} to be viewable in a ListView.
 * WARNING: This creates a COPY of the list. This is because it will change the list which doesn't work with unmodifiable lists
 * 
 * @author rozsa
 *
 */

public final class ExpensesAdapter extends ArrayAdapter<Expense> {

    private static final class ViewHolder {
        private final TextView date;
        private final TextView category;
        private final TextView description;
        private final TextView amount;
        private final TextView receipt;
        private final TextView complete;
        private final TextView geolocation;

        private ViewHolder(View parent) {
            date = (TextView) parent.findViewById(R.id.date);
            geolocation = (TextView) parent.findViewById(R.id.geolocation);
            complete = (TextView) parent.findViewById(R.id.complete);
            receipt = (TextView) parent.findViewById(R.id.receipt);
            amount = (TextView) parent.findViewById(R.id.amount);
            description = (TextView) parent.findViewById(R.id.description);
            category = (TextView) parent.findViewById(R.id.category);
        }
    }

    private final LayoutInflater mInflater;

    private final DateFormat mFormatter;

    /**
     * WARNING: This creates a COPY of the list. This is because it will change the list when sorting, which doesn't work with unmodifiable lists
     */
    public ExpensesAdapter(Context context, List<Expense> expenses) {
        super(context, R.layout.expense_list_item, new ArrayList<Expense>(expenses));
        mInflater = LayoutInflater.from(context);
        mFormatter = android.text.format.DateFormat.getMediumDateFormat(context);  // with respect to user settings
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

        //Log.e("my tag", "" + expense.getTime());
        //Log.e("my tag", "" + (holder == null));
        //Log.e("my tag", "" + (holder.date == null));

        holder.date.setText(mFormatter.format(new Date(expense.getTime())));
        holder.category.setText(expense.getCategory());
        holder.description.setText(expense.getDescription());
        holder.amount.setText(expense.getAmount().toString());

        holder.receipt.setText(expense.hasReceipt() ? "Receipt attached." : "");
        holder.complete.setText(expense.isCompleted() ? "" : "Incomplete");

        //TODO: add geolocation here
        //holder.date.setText(expense.isGeoTagged());

        return convertView;
    }
}