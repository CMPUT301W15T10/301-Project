package com.cmput301.cs.project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.models.Claim;
import com.cmput301.cs.project.utils.Utils;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ClaimsApproverAdapter extends ArrayAdapter<Claim> {

    private static final class ViewHolder {
        private final TextView name;
        private final TextView startDate;
        private final TextView status;
        private final TextView totals;
        private final TextView approverName;

        private ViewHolder(View parent) {
            name = (TextView) parent.findViewById(R.id.name);
            startDate = (TextView) parent.findViewById(R.id.start_date);
            totals = (TextView) parent.findViewById(R.id.totals);
            status = (TextView) parent.findViewById(R.id.status);
            approverName = (TextView) parent.findViewById(R.id.approverName);
        }
    }

    private final LayoutInflater mInflater;

    private final DateFormat mDateFormat;

    public ClaimsApproverAdapter(Context context, List<Claim> claims) {
        super(context, R.layout.claim_list_approver_item, claims);

        mInflater = LayoutInflater.from(context);
        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.claim_list_approver_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Claim claim = getItem(position);

        holder.name.setText(claim.getClaimant().getUserName());
        holder.startDate.setText(mDateFormat.format(new Date(claim.getStartTime())));
        holder.status.setText(Utils.stringIdForClaimStatus(claim.getStatus()));
        holder.totals.setText(claim.getTotalsAsString());
        holder.approverName.setText(claim.getAllApprovers());

        return convertView;
    }
}
