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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Basic adapter that adapts a {@link com.cmput301.cs.project.model.Claim Claim} to be viewable in a ListView. Not entirely correct yet.
 * 
 * @author rozsa
 *
 */

public final class ClaimsAdapter extends BaseAdapter {

    private static final class ViewHolder {
        private final TextView title;
        private final TextView subTitle;

        private ViewHolder(View parent) {
            title = (TextView) parent.findViewById(android.R.id.text1);
            subTitle = (TextView) parent.findViewById(android.R.id.text2);
        }
    }

    private final LayoutInflater mInflater;
    private final List<Claim> mClaims;
    private final Context mContext;

    public ClaimsAdapter(Context context, List<Claim> claims) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mClaims = claims;
    }

    @Override
    public int getCount() {
        return mClaims.size();
    }

    @Override
    public Claim getItem(int position) {
        return mClaims.get(position);
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

        final Claim claim = getItem(position);
        holder.title.setText(claim.getTitle());
        holder.subTitle.setText(mContext.getString(Utils.stringIdForClaimStatus(claim.getStatus())));

        return convertView;
    }
}