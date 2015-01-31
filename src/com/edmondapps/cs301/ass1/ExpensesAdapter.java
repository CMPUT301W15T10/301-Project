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

package com.edmondapps.cs301.ass1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.edmondapps.cs301.ass1.model.Expense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ExpensesAdapter extends BaseAdapter {

    private static final class ViewHolder {
        private final TextView title;
        private final TextView subTitle;

        private ViewHolder(View parent) {
            title = (TextView) parent.findViewById(android.R.id.text1);
            subTitle = (TextView) parent.findViewById(android.R.id.text2);
        }
    }

    private final LayoutInflater mInflater;
    private final List<Expense> mExpenses;
    private final Context mContext;

    public ExpensesAdapter(Context context, Collection<? extends Expense> expenses) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mExpenses = new ArrayList<Expense>(expenses);
    }

    public List<Expense> peekAllExpenses() {
        return Collections.unmodifiableList(mExpenses);
    }

    public void putAllExpenses(Iterable<? extends Expense> expenses) {
        for (Expense expense : expenses) {
            putExpense(expense);
        }
    }

    public void removeExpense(Expense expense) {
        mExpenses.remove(expense);
        notifyDataSetChanged();
    }

    public void putExpense(Expense expense) {
        for (int i = 0, mClaimsSize = mExpenses.size(); i < mClaimsSize; ++i) {
            final Expense o = mExpenses.get(i);

            if (expense.getId().equals(o.getId())) {
                mExpenses.remove(i);
                mExpenses.add(i, expense);
                notifyDataSetChanged();
                return;
            }
        }
        addExpense(expense);
    }

    public void addExpense(Expense expense) {
        mExpenses.add(expense);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mExpenses.size();
    }

    @Override
    public Expense getItem(int position) {
        return mExpenses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        final Expense expense = getItem(position);
        holder.title.setText(expense.getTitle());
        holder.subTitle.setText(expense.getAmount().toString());

        return convertView;
    }
}