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

package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.utils.Utils;

/**
 * Activity for picking a date. This activity is used whenever a date needs to be picked in the app.<p>
 * If an activity relies on both a start date and an end date the calendar will restrict choices to the user so
 *  that an end date can't come before a start date and vice versa.
 *
 *  Returns the date as a result in an intent as KEY_DATE <p>
 *  
 *  this activity is linked to in:
 *  <ul>
 *  <li>{@link com.cmput301.cs.project.activities.EditClaimActivity EditClaimActivity}
 *  <li>{@link com.cmput301.cs.project.activities.EditExpenseActivity EditExpenseActivity}
 *  </ul>
 *
 *  @author rozsa
 *  @author jbenson
 */
public class CalendarActivity extends Activity {
    public static final String KEY_DATE = "date";
    public static final String KEY_MIN_DATE = "min_date";
    public static final String KEY_MAX_DATE = "max_date";

    public static final class Builder {
        private final Intent mIntent;

        public Builder(Context context) {
            mIntent = new Intent(context, CalendarActivity.class);
        }

        public Builder selectedDate(long date) {
            if (date < 0) return this;
            mIntent.putExtra(KEY_DATE, date);
            return this;
        }

        public Builder minDate(long minDate) {
            if (minDate < 0) return this;
            mIntent.putExtra(KEY_MIN_DATE, minDate);
            return this;
        }

        public Builder maxDate(long maxDate) {
            if (maxDate < 0) return this;
            mIntent.putExtra(KEY_MAX_DATE, maxDate);
            return this;
        }

        public Intent build() {
            return mIntent;
        }
    }
    private static final long FIVE_DAYS = 432000000L;

    private CalendarView mCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        Utils.setupDiscardDoneBar(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putDateInResult();
                finish();
            }
        });

        setContentView(R.layout.calendar_activity);

        mCalendarView = (CalendarView) findViewById(R.id.calendar);

        tryFindingExtras();
    }

    private void tryFindingExtras() {
        final Intent intent = getIntent();
        final long selectedDate = intent.getLongExtra(KEY_DATE, -1);

        final long minDate = intent.getLongExtra(KEY_MIN_DATE, -1);
        if (minDate != -1) {
            mCalendarView.setMinDate(minDate);
        }

        final long maxDate = intent.getLongExtra(KEY_MAX_DATE, -1);
        if (maxDate != -1) {
            mCalendarView.setMaxDate(maxDate);
        }

        if (selectedDate != -1) {
            mCalendarView.setDate(selectedDate, false, true);
        } else {
            if (minDate != -1) {
                // general use case: pick start date (minDate is set), then end date (selectedDate is not set)
                // suggest the selected date to be five days after the minDate to the user
                // five is an arbitrary number of days
                mCalendarView.setDate(minDate + FIVE_DAYS, false, true);
            }
        }
    }

    private void putDateInResult() {
        final long date = mCalendarView.getDate();
        setResult(RESULT_OK, new Intent().putExtra(KEY_DATE, date));
    }
}