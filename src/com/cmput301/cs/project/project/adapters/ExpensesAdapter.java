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

package com.cmput301.cs.project.project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.cmput301.cs.project.project.model.Expense;

import java.util.*;

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

    public ExpensesAdapter(Context context, Collection<? extends Expense> expenses) {
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

    public void removeExpenseById(Expense expense) {
        final String id = expense.getId();
        for (Iterator<Expense> iterator = mExpenses.iterator(); iterator.hasNext(); ) {
            final Expense e = iterator.next();
            if (e.getId().equals(id)) {
                iterator.remove();
                break;
            }
        }
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
        holder.title.setText(expense.getDescription());
        holder.subTitle.setText(expense.getAmount().toString());

        return convertView;
    }
}