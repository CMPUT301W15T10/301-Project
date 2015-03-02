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

package com.cmput301.cs.project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.cmput301.cs.project.model.Expense;
import org.joda.money.Money;

import java.text.DateFormat;

public class ExpenseActivity extends Activity {
    public static final String KEY_EXPENSE = "expense_key";
    public static final String KEY_EXPENSE_ID = "expense_id_key";
    public static final String KEY_START_TIME = "start_day_time";

    public static final String ACTION_DELETE = "action_del";

    public static Intent createIntentWithExpense(Context context, Expense expense) {
        final String id = expense.getId();
        App.get(context).putObjectTransfer(id, expense);

        return new Intent(context, ExpenseActivity.class).putExtra(KEY_EXPENSE, id);
    }

    private Expense mExpense;
    private DateFormat mDateFormat;

    private TextView mMoney;
    private TextView mCurrency;
    private TextView mTitle;
    private TextView mDate;
    private TextView mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_activity);

        mMoney = (TextView) findViewById(R.id.money);
        mCurrency = (TextView) findViewById(R.id.currency);
        mTitle = (TextView) findViewById(R.id.title);
        mDate = (TextView) findViewById(R.id.date);
        mCategory = (TextView) findViewById(R.id.category);

        mExpense = findExpenseOrThrow();
        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);

        updateUi();
    }

    private void updateUi() {
        final Money amount = mExpense.getAmount();
        mMoney.setText(amount.getAmount().toPlainString());
        mCurrency.setText(amount.getCurrencyUnit().toString());
        mTitle.setText(mExpense.getTitle());
        mDate.setText(mDateFormat.format(mExpense.getTime()));
        mCategory.setText(mExpense.getCategory());
    }


    private Expense findExpenseOrThrow() {
        final String claimKey = getIntent().getStringExtra(KEY_EXPENSE);
        if (claimKey != null) {
            final Expense expense = App.get(this).getObjectTransfer(claimKey);
            if (expense != null) {
                return expense;
            }
        }
        throw new IllegalStateException("you use must pass in KEY_EXPENSE");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.expense_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                final Intent intent = ExpenseBuilderActivity.createIntentWithExpense(this, mExpense);
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
                finish();
                return true;
            case R.id.delete:
                setResult(RESULT_OK, new Intent(ACTION_DELETE).putExtra(KEY_EXPENSE_ID, mExpense.getId()));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
