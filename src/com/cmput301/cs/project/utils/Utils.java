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

package com.cmput301.cs.project.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.model.Claim;

public final class Utils {
    private Utils() {
        throw new UnsupportedOperationException("utils class");
    }

    public static void setupDiscardDoneBar(Activity activity, View.OnClickListener discardListener, View.OnClickListener doneListener) {
        final ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
                    | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

            final LayoutInflater inflater = LayoutInflater.from(actionBar.getThemedContext());
            final View customView = inflater.inflate(R.layout.ab_done_discard, null);
            actionBar.setCustomView(customView, new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            customView.findViewById(R.id.discard).setOnClickListener(discardListener);
            final View done = customView.findViewById(R.id.done);
            done.setOnClickListener(doneListener);
            done.setEnabled(false);
        }
    }

    public static int stringIdForClaimStatus(Claim.Status status) {
        switch (status) {
            case IN_PROGRESS:
                return R.string.in_progress;
            case SUBMITTED:
                return R.string.submitted;
            case RETURNED:
                return R.string.returned;
            case APPROVED:
                return R.string.approved;
            default:
                throw new AssertionError("unexpected status: " + status);
        }
    }

    public static Intent intentWithEmailString(String subject, String text) {
        // ref: http://stackoverflow.com/questions/8701634/send-email-intent
        return new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null))
                .putExtra(Intent.EXTRA_SUBJECT, subject)
                .putExtra(Intent.EXTRA_TEXT, text);

    }
}
