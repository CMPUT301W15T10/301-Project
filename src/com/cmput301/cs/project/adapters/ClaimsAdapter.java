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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.models.Claim;
import com.cmput301.cs.project.models.Tag;
import com.cmput301.cs.project.utils.Utils;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Basic adapter that adapts a {@link com.cmput301.cs.project.models.Claim Claim} to be viewable in a ListView. Not entirely correct yet.
 *
 * @author rozsa
 */
// The code for filtering was from
// From http://stackoverflow.com/questions/5780289/filtering-listview-with-custom-object-adapter April 5, 2015
// and http://www.survivingwithandroid.com/2012/10/android-listview-custom-filter-and.html April 5, 2015
// (Used some ideas from both)
public final class ClaimsAdapter extends ArrayAdapter<Claim> implements Filterable {


    private static final class ViewHolder {
        private final TextView status;
        private final TextView startDate;
        private final TextView tags;
        private final TextView totals;
        private final TextView destinations;

        private ViewHolder(View parent) {
            startDate = (TextView) parent.findViewById(R.id.start_date);
            tags = (TextView) parent.findViewById(R.id.tags);
            totals = (TextView) parent.findViewById(R.id.totals);
            status = (TextView) parent.findViewById(R.id.status);
            destinations = (TextView) parent.findViewById(R.id.destinations);
        }
    }

    private final ClaimsFilter mFilter;

    private final LayoutInflater mInflater;

    private final DateFormat mDateFormat;

    private final List<Claim> mUnfilteredClaims;
    private List<Claim> mFilteredClaims;

    public ClaimsAdapter(Context context, List<Claim> claims) {
        super(context, R.layout.claim_list_item, claims);

        mInflater = LayoutInflater.from(context);
        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(context);  // with respec to user settings

        mFilter = new ClaimsFilter();

        mUnfilteredClaims = claims;
        mFilteredClaims = claims;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.claim_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Claim claim = getItem(position);

        holder.startDate.setText(mDateFormat.format(new Date(claim.getStartTime())));
        holder.status.setText(Utils.stringIdForClaimStatus(claim.getStatus()));
        holder.tags.setText(claim.getTagsAsString());
        holder.totals.setText(claim.getTotalsAsString());
        holder.destinations.setText(claim.getDestinationsAsString());

        return convertView;
    }

    @Override
    public int getCount() {
        return mFilteredClaims.size();
    }

    @Override
    public Claim getItem(int position) {
        return mFilteredClaims.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mFilteredClaims.get(position).hashCode();
    }

    /*
     * Will automatically refilter the adapter
     */
    public void updateFilter(List<Tag> allWantedTags) {
        mFilter.setWantedTags(allWantedTags);
        getFilter().filter("");
    }

    public Filter getFilter() {
        return mFilter;
    }


    private class ClaimsFilter extends Filter {
        private List<Tag> mWantedTags;

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (mWantedTags.size() == 0) {
                // In this case, keep everything
                results.values = mUnfilteredClaims;

            } else {
                final ArrayList<Claim> newFilteredList = new ArrayList<Claim>();

                for (Claim claim : mUnfilteredClaims) {
                    boolean oneTagWasInList = false;

                    for (Tag tag : claim.peekTags()) {
                        if (mWantedTags.contains(tag)) {
                            oneTagWasInList = true;
                        }
                    }

                    if (oneTagWasInList) {
                        newFilteredList.add(claim);
                    }
                }

                results.values = newFilteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredClaims = (List<Claim>) results.values;
            ClaimsAdapter.this.notifyDataSetChanged();
        }

        public void setWantedTags(List<Tag> wantedTags) {
            this.mWantedTags = wantedTags;
        }
    }
}