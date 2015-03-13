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
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.cmput301.cs.project.model.Claim;
import com.cmput301.cs.project.utils.Utils;

public final class ClaimsStatusAdapter extends BaseAdapter {

    private static final class ViewHolder {
        private final TextView title;

        private ViewHolder(View parent) {
            title = (TextView) parent.findViewById(android.R.id.text1);
        }
    }

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final Claim.Status[] mStatuses;

    public ClaimsStatusAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mStatuses = Claim.Status.values();
    }

    @Override
    public int getCount() {
        return mStatuses.length;
    }

    @Override
    public Claim.Status getItem(int position) {
        return mStatuses[position];
    }

    private String stringForStatus(Claim.Status status) {
        return mContext.getString(Utils.stringIdForClaimStatus(status));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewWithId(android.R.layout.simple_spinner_item, position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getViewWithId(android.R.layout.simple_spinner_dropdown_item, position, convertView, parent);
    }

    private View getViewWithId(int layoutId, int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(layoutId, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(stringForStatus(getItem(position)));

        return convertView;
    }
}
