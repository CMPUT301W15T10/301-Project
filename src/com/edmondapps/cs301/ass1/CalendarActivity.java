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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import com.edmondapps.cs301.ass1.utils.Utils;

public class CalendarActivity extends Activity {
    public static final String KEY_DATE = "date";
    public static final String KEY_MIN_DATE = "min_date";
    public static final String KEY_MAX_DATE = "max_date";

    public static final class Builder {
        private Intent mIntent;

        public Builder(Context context) {
            mIntent = new Intent(context, CalendarActivity.class);
        }

        public Builder selectedDate(long date) {
            mIntent.putExtra(KEY_DATE, date);
            return this;
        }

        public Builder minDate(long minDate) {
            mIntent.putExtra(KEY_MIN_DATE, minDate);
            return this;
        }

        public Builder maxDate(long maxDate) {
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
