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
import com.cmput301.cs.project.project.model.Claim;
import com.cmput301.cs.project.project.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    public ClaimsAdapter(Context context, Collection<? extends Claim> claims) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mClaims = new ArrayList<Claim>(claims);
    }

    public List<Claim> peekAllClaims() {
        return Collections.unmodifiableList(mClaims);
    }

    public void removeClaimById(String id) {
        for (Claim claim : mClaims) {
            if (claim.getId().equals(id)) {
                mClaims.remove(claim);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void putClaim(Claim claim) {
        for (int i = 0, mClaimsSize = mClaims.size(); i < mClaimsSize; ++i) {
            final Claim o = mClaims.get(i);

            if (claim.getId().equals(o.getId())) {
                mClaims.remove(i);
                mClaims.add(i, claim);
                notifyDataSetChanged();
                return;
            }
        }
        addClaim(claim);
    }

    public void addClaim(Claim claim) {
        mClaims.add(claim);
        notifyDataSetChanged();
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