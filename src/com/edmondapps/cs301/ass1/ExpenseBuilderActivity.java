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
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.*;
import com.edmondapps.cs301.ass1.model.Expense;
import com.edmondapps.cs301.ass1.utils.Utils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.List;

public class ExpenseBuilderActivity extends Activity implements AdapterView.OnItemSelectedListener {
    public static final String KEY_EXPENSE = "expense_key";
    public static final String KEY_START_TIME = "start_day_time";
    public static final String ACTION_PUT = "action_put";

    public static Intent createIntentWithExpense(Context context, Expense expense) {
        final String id = expense.getId();
        App.get(context).putObjectTransfer(id, expense);

        return new Intent(context, ExpenseBuilderActivity.class).putExtra(KEY_EXPENSE, id);
    }

    private static final int REQ_CODE_TIME = 1;

    private List<String> mCurrencies;
    private List<String> mCategories;

    private Expense.Builder mBuilder;
    private DateFormat mDateFormat;

    private Spinner mCurrencySpinner;
    private Spinner mCategorySpinner;
    private EditText mMoneyAmount;
    private EditText mTitle;
    private Button mTime;

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
                putExpenseToResult();
                finish();
            }
        });

        setContentView(R.layout.expense_builder_activity);

        initViews();

        final Expense expense = tryFindingExpense();
        if (expense == null) {
            mBuilder = new Expense.Builder();
        } else {
            mBuilder = Expense.Builder.copyFrom(expense);
        }

        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);

        final Resources resources = getResources();
        mCurrencies = Arrays.asList(resources.getStringArray(R.array.currencies));
        mCategories = Arrays.asList(resources.getStringArray(R.array.expense_categories));

        updateUI();
    }

    private void initViews() {
        mCurrencySpinner = (Spinner) findViewById(R.id.currency_spinner);
        mCurrencySpinner.setAdapter(createArrayAdapter(R.array.currencies));
        mCurrencySpinner.setOnItemSelectedListener(this);

        mCategorySpinner = (Spinner) findViewById(R.id.category_spinner);
        mCategorySpinner.setAdapter(createArrayAdapter(R.array.expense_categories));
        mCategorySpinner.setOnItemSelectedListener(this);

        mMoneyAmount = (EditText) findViewById(R.id.money);
        mMoneyAmount.addTextChangedListener(new TextWaterAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    mBuilder.amountInBigDecimal(new BigDecimal(s.toString()));
                    mMoneyAmount.setError(null);
                } catch (NumberFormatException e) {
                    mMoneyAmount.setError(getText(R.string.money_amount_error));
                }
            }
        });

        mTitle = (EditText) findViewById(R.id.title);
        mTitle.addTextChangedListener(new TextWaterAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                mBuilder.title(s.toString());
            }
        });

        mTime = (Button) findViewById(R.id.time);
        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(launchCalendarWithTime(), REQ_CODE_TIME);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_TIME:
                    final long date = data.getLongExtra(CalendarActivity.KEY_DATE, -1);
                    if (date != -1) {
                        mBuilder.time(date);
                        updateUI();
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Intent launchCalendarWithTime() {
        final CalendarActivity.Builder builder = new CalendarActivity.Builder(this);
        if (mBuilder.isTimeSet()) {
            builder.selectedDate(mBuilder.getTime());
        } else {
            final long startTime = getIntent().getLongExtra(KEY_START_TIME, -1);
            if (startTime != -1) {
                builder.selectedDate(startTime);
            }
        }
        return builder.build();
    }

    private ArrayAdapter<CharSequence> createArrayAdapter(int arrayResId) {
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private Intent intentWithExpense() {
        final Expense expense = mBuilder.build();
        final String expenseId = expense.getId();
        App.get(this).putObjectTransfer(expenseId, expense);
        return new Intent().putExtra(KEY_EXPENSE, expenseId);
    }

    private void putExpenseToResult() {
        setResult(RESULT_OK, intentWithExpense().setAction(ACTION_PUT));
    }

    private Expense tryFindingExpense() {
        final String claimKey = getIntent().getStringExtra(KEY_EXPENSE);
        if (claimKey != null) {
            return App.get(this).getObjectTransfer(claimKey);
        }
        return null;
    }

    private void updateUI() {
        final Money money = mBuilder.getMoney();
        mCurrencySpinner.setSelection(mCurrencies.indexOf(money.getCurrencyUnit().getCurrencyCode()), true);
        mCategorySpinner.setSelection(mCategories.indexOf(mBuilder.getCategory()), true);
        if (!mBuilder.getMoney().isZero()) {
            mMoneyAmount.setText(money.getAmount().toPlainString());
        }
        if (mBuilder.isTitleSet()) {
            mTitle.setText(mBuilder.getTitle());
        }
        if (mBuilder.isTimeSet()) {
            mTime.setText(mDateFormat.format(mBuilder.getTime()));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        @SuppressWarnings("unchecked")
        final ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) parent.getAdapter();
        switch (parent.getId()) {
            case R.id.currency_spinner:
                mBuilder.currencyUnit(CurrencyUnit.of(adapter.getItem(position).toString()));
                break;
            case R.id.category_spinner:
                mBuilder.category(adapter.getItem(position).toString());
                break;
            default:
                throw new AssertionError("unknown spinner");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }
}
