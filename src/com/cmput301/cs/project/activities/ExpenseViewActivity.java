package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.model.Expense;
import org.joda.money.Money;

import java.io.File;
import java.text.DateFormat;

public class ExpenseViewActivity extends Activity {
    public static final String KEY_EXPENSE = "key_expense";

    private Expense mExpense;
    private DateFormat mDateFormat;

    private TextView mDescription;
    private TextView mMoney;
    private TextView mDate;
    private TextView mCategory;
    private TextView mCompleted;

    private ImageView mReceipt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_view_activity);

        mDescription = (TextView) findViewById(R.id.description);
        mMoney = (TextView) findViewById(R.id.money);
        mDate = (TextView) findViewById(R.id.date);
        mCategory = (TextView) findViewById(R.id.category);
        mCompleted = (TextView) findViewById(R.id.completed);

        mReceipt = (ImageView) findViewById(R.id.receiptImage);

        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);

        initExpense();

        updateUi();
    }

    private void initExpense() {
        mExpense = getIntent().getParcelableExtra(KEY_EXPENSE);
        if (mExpense == null) {
            throw new IllegalStateException("Expected an Expense");
        }
    }

    private void updateUi() {
        final Money amount = mExpense.getAmount();

        mDescription.setText(mExpense.getDescription());
        mMoney.setText(amount.toString());
        mDate.setText(mDateFormat.format(mExpense.getTime()));
        mCategory.setText(mExpense.getCategory());

        mCompleted.setText(mExpense.isCompleted() ? "Yes" : "No");

        if (mExpense.hasReceipt()) {
            final File receiptFile = mExpense.getReceipt().getFile();
            mReceipt.setImageDrawable(new BitmapDrawable(getResources(), receiptFile.getPath()));

        } else {
            mReceipt.setImageDrawable(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.expense_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.delete) {
            // Handle delete here
            return true;
        } else if (id == R.id.edit) {
            // Handle edit here
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}