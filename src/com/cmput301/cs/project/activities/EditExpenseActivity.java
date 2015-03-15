package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.*;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.TextWatcherAdapter;
import com.cmput301.cs.project.model.Expense;
import com.cmput301.cs.project.utils.Utils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.text.DateFormat;

public class EditExpenseActivity extends Activity {
    public static final String KEY_EXPENSE = "key_expense";
    private static final int REQ_CODE_PICK_DATE = 1;

    private Expense.Builder mBuilder;

    private EditText mDescription;
    private EditText mAmount;

    private Spinner mCategory;
    private Spinner mCurrency;

    private Switch mCompleted;

    private Button mDate;
    private DateFormat mDateFormat;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_edit_activity);

        createDiscardDoneBar();

        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);

        createBuilder();

        initEditing();
        updateUI();
    }

    private void createDiscardDoneBar() {
        Utils.setupDiscardDoneBar(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private void createBuilder() {
        Expense expense = getIntent().getParcelableExtra(KEY_EXPENSE);
        if (expense == null)
            mBuilder = new Expense.Builder();
        else
            mBuilder = Expense.Builder.copyFrom(expense);
    }

    /*
     * Method that initializes all of the editing related members, and sets up their responses to input.
     */
    private void initEditing() {
        mDescription = (EditText) findViewById(R.id.description);
        mDescription.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                mBuilder.description(s.toString());
            }
        });

        // The idea of using setError is from
        // https://github.com/chuihinwai/echui-notes/blob/master/src/com/edmondapps/cs301/ass1/ExpenseBuilderActivity.java
        // March 14, 2015
        mAmount = (EditText) findViewById(R.id.amount);
        mAmount.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    mBuilder.amountInBigDecimal(new BigDecimal(s.toString()));
                    mAmount.setError(null);
                } catch (NumberFormatException e) {
                    mAmount.setError(getText(R.string.money_amount_error));
                }
            }
        });

        mCategory = (Spinner) findViewById(R.id.category);
        mCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) parent.getAdapter();
                mBuilder.category(adapter.getItem(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        mCurrency = (Spinner) findViewById(R.id.currency);
        mCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) parent.getAdapter();
                final CurrencyUnit unit = CurrencyUnit.getInstance(adapter.getItem(position).toString());
                mBuilder.currencyUnit(unit);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mCompleted = (Switch) findViewById(R.id.completed);
        mCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mBuilder.completed(isChecked);
            }
        });

        mDate = (Button) findViewById(R.id.date);
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new CalendarActivity.Builder(EditExpenseActivity.this)
                        .selectedDate(mBuilder.getTime()).build(), REQ_CODE_PICK_DATE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_PICK_DATE:
                if (resultCode == RESULT_OK) {
                    mBuilder.time(data.getLongExtra(CalendarActivity.KEY_DATE, -1));
                    updateUI();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateUI() {
        mDescription.setText(mBuilder.getDescription());

        final Money money = mBuilder.getMoney();
        mAmount.setText(money.getAmount().toString());

        if (mBuilder.isTimeSet()) {
            mDate.setText(mDateFormat.format(mBuilder.getTime()));
        }

        if (mBuilder.isCategorySet()) {
            int spinnerPosition = getIndex(mCategory, mBuilder.getCategory());
            mCategory.setSelection(spinnerPosition);
        }

        int spinnerPosition = getIndex(mCurrency, money.getCurrencyUnit().toString());
        mCurrency.setSelection(spinnerPosition);

        mCompleted.setChecked(mBuilder.isCompleted());
    }

    //private method of your class
    private int getIndex(Spinner spinner, String wantedString) {
        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(wantedString)) {
                index = i;
                break;
            }
        }
        return index;
    }
}
