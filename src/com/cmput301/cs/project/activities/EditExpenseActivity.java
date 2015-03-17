package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.*;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.TextWatcherAdapter;
import com.cmput301.cs.project.model.Expense;
import com.cmput301.cs.project.model.Receipt;
import com.cmput301.cs.project.utils.ReceiptLoading;
import com.cmput301.cs.project.utils.Utils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.io.File;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;


/**
 * An activity that is called when a new expense is created or when a expense is being edited from 
 * within {@link com.cmput301.cs.project.activities.ExpenseListActivity ExpenseListActivity}.</br>
 * Allows the user to add a Description, Category, Date, Money (with type), Receipt and select completeness which is stored
 * within that particular {@link com.cmput301.cs.project.model.Expense Expense}.
 *
 * The editted expense is passed back using setResult
 *
 *
 * @author rozsa
 *
 */

public class EditExpenseActivity extends Activity {
    private static final int REQ_CODE_PICK_DATE = 1;
    private static final int REQ_CODE_RECEIPT = 2;

    private Expense.Builder mBuilder;

    private EditText mDescription;
    private EditText mAmount;

    private Spinner mCategory;
    private Spinner mCurrency;

    private Switch mCompleted;

    private Button mDate;
    private DateFormat mDateFormat;

    private ImageButton mReceipt;

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
                setResult(RESULT_OK, new Intent().putExtra(App.KEY_EXPENSE, mBuilder.build()));
                finish();
            }
        });
    }

    private void createBuilder() {
        Expense expense = getIntent().getParcelableExtra(App.KEY_EXPENSE);
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
        mCategory.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new ArrayList<String>(Expense.CATEGORIES)));
        mCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final ArrayAdapter<String> adapter = (ArrayAdapter<String>) parent.getAdapter();
                mBuilder.category(adapter.getItem(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        mCurrency = (Spinner) findViewById(R.id.currency);
        mCurrency.setAdapter(new ArrayAdapter<CurrencyUnit>(this, android.R.layout.simple_spinner_item,new ArrayList<CurrencyUnit>(Expense.CURRENCIES)));
        mCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final ArrayAdapter<CurrencyUnit> adapter = (ArrayAdapter<CurrencyUnit>) parent.getAdapter();
                mBuilder.currencyUnit(adapter.getItem(position));
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

        mReceipt = (ImageButton) findViewById(R.id.receiptImage);
        mReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                final Uri receiptFileUri = ReceiptLoading.getReceiptUri(mBuilder.getId());

                intent.putExtra(MediaStore.EXTRA_OUTPUT, receiptFileUri);
                startActivityForResult(intent, REQ_CODE_RECEIPT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        switch (requestCode) {
            case REQ_CODE_PICK_DATE:
                mBuilder.time(data.getLongExtra(CalendarActivity.KEY_DATE, -1));
                updateUI();
                break;

            case REQ_CODE_RECEIPT:
                final Uri receiptFileUri = ReceiptLoading.getReceiptUri(mBuilder.getId());
                mBuilder.receipt(new Receipt(receiptFileUri.toString()));
                updateUI();
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateUI() {
        mDescription.setText(mBuilder.getDescription());

        final Money money = mBuilder.getMoney();
        mAmount.setText(money.getAmount().toString());

        int spinnerPosition = ((ArrayAdapter<CurrencyUnit>) mCurrency.getAdapter()).getPosition(money.getCurrencyUnit());
        mCurrency.setSelection(spinnerPosition);

        if (mBuilder.isTimeSet()) {
            mDate.setText(mDateFormat.format(mBuilder.getTime()));
        }

        if (mBuilder.isCategorySet()) {
            int categoryPosition = getIndex(mCategory, mBuilder.getCategory());
            mCategory.setSelection(categoryPosition);
        }


        mCompleted.setChecked(mBuilder.isCompleted());

        if (mBuilder.hasReceipt()) {
            final Uri receiptFileUri = ReceiptLoading.getReceiptUri(mBuilder.getId());
            final BitmapDrawable drawable = new BitmapDrawable(getResources(), receiptFileUri.toString());
            mReceipt.setImageDrawable(drawable);
        } else {
            mReceipt.setImageDrawable(null);
        }
    }

    // This is from http://stackoverflow.com/questions/2390102/how-to-set-selected-item-of-spinner-by-value-not-by-position
    // on March 15, 2015
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
