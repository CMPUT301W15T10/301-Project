package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.TextWatcherAdapter;
import com.cmput301.cs.project.models.Destination;
import com.cmput301.cs.project.models.Expense;
import com.cmput301.cs.project.models.Receipt;
import com.cmput301.cs.project.utils.Utils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;


/**
 * An activity that is called when a new expense is created or when a expense is being edited from
 * within {@link com.cmput301.cs.project.activities.ExpenseListActivity ExpenseListActivity}.<p>
 * Allows the user to add a Description, Category, Date, Money (with type), Receipt and select completeness which is stored
 * within that particular {@link com.cmput301.cs.project.models.Expense Expense}.
 * <p/>
 * The editted expense is passed back using setResult
 *
 * @author rozsa
 */

public class EditExpenseActivity extends Activity {
    private static final int REQ_CODE_PICK_DATE = 1;
    private static final int REQ_CODE_RECEIPT = 2;
    private static final int REQ_CODE_LOCATION = 3;

    private Expense.Builder mBuilder;

    private EditText mDescription;
    private EditText mAmount;

    private Spinner mCategory;
    private Spinner mCurrency;

    private Switch mCompleted;

    private Button mDate;
    private DateFormat mDateFormat;

    private ImageButton mReceipt;
    private Button mDeleteReceipt;

    private Button mLocationButton;

    @Override
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
        else {
            mBuilder = expense.edit();
        }
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
                    mBuilder.amount(new BigDecimal(s.toString()));
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
                @SuppressWarnings("unchecked")  // we know it's ArrayAdapter<String>
                final ArrayAdapter<String> adapter = (ArrayAdapter<String>) parent.getAdapter();
                mBuilder.category(adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        mCurrency = (Spinner) findViewById(R.id.currency);
        mCurrency.setAdapter(new ArrayAdapter<CurrencyUnit>(this, android.R.layout.simple_spinner_item, new ArrayList<CurrencyUnit>(Expense.CURRENCIES)));
        mCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                @SuppressWarnings("unchecked")  // we know it's ArrayAdapter<CurrencyUnit>
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

                final Uri receiptFileUri = getReceiptUri();

                intent.putExtra(MediaStore.EXTRA_OUTPUT, receiptFileUri);
                startActivityForResult(intent, REQ_CODE_RECEIPT);
            }
        });

        mDeleteReceipt = (Button) findViewById(R.id.deleteReceipt);
        mDeleteReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReceipt();
            }
        });

        mLocationButton = (Button) findViewById(R.id.location_btn);
        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(EditExpenseActivity.this, MapActivity.class)
                        .putExtra(MapActivity.KEY_DESTINATION,
                                mBuilder.getDestination()), REQ_CODE_LOCATION);
            }
        });
    }

    private Uri getReceiptUri() {
        File file = new File(getStorageFolder(), mBuilder.getId() + ".jpg");
        return Uri.fromFile(file);
    }

    // This is from http://developer.android.com/training/basics/data-storage/files.html
    // March 15, 2015
    private File getStorageFolder() {
        File file = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        if (!file.mkdirs()) {
            Log.e("EditExpenseActivity", "Was unable to make the directory " + file.toString());
        }

        return file;
    }

    private void deleteReceipt() {
        File receiptFile = mBuilder.getReceipt().getFile();
        if (!receiptFile.delete()) {
            Log.w("EditExpenseActivity", "Unable to delete file " + receiptFile.toString());
        }

        mBuilder.receipt(null);

        updateUI();
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
                final Uri receiptFileUri = getReceiptUri();
                mBuilder.receipt(new Receipt(receiptFileUri.getPath()));
                updateUI();
                break;

            case REQ_CODE_LOCATION:
                final Destination destination = data.getParcelableExtra(MapActivity.KEY_DESTINATION);
                mBuilder.destination(destination);
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

        @SuppressWarnings("unchecked")  // we know it's ArrayAdapter<CurrencyUnit>
        final ArrayAdapter<CurrencyUnit> adapter = (ArrayAdapter<CurrencyUnit>) mCurrency.getAdapter();
        int spinnerPosition = adapter.getPosition(money.getCurrencyUnit());
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
            final Uri receiptFileUri = mBuilder.getReceipt().getUri();

            final BitmapDrawable drawable = new BitmapDrawable(getResources(), receiptFileUri.getPath());
            mReceipt.setImageDrawable(drawable);

            mDeleteReceipt.setEnabled(true);
        } else {
            mReceipt.setImageDrawable(getResources().getDrawable(android.R.drawable.gallery_thumb));

            mDeleteReceipt.setEnabled(false);
        }

        final Destination destination = mBuilder.getDestination();
        if (destination != null) {
            mLocationButton.setText(destination.getName());
        }
    }

    // This is from
    // http://stackoverflow.com/questions/2390102/how-to-set-selected-item-of-spinner-by-value-not-by-position
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
