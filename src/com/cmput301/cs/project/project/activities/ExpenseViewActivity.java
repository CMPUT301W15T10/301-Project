package com.cmput301.cs.project.project.activities;

import java.text.DateFormat;

import android.widget.ImageView;
import android.widget.TextView;
import com.cmput301.cs.project.R;
import org.joda.money.Money;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.cmput301.cs.project.project.model.Expense;

public class ExpenseViewActivity extends Activity {
    private Expense mExpense;
    private DateFormat mDateFormat;

    private TextView mDescription;
    private TextView mMoney;
    private TextView mCurrency;
    private TextView mDate;
    private TextView mCategory;

    private ImageView mReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_view_activity);

        mExpense = new Expense.Builder().build();

        mDescription = (TextView) findViewById(R.id.description);
        mMoney = (TextView) findViewById(R.id.money);
        mCurrency = (TextView) findViewById(R.id.currency);
        mDate = (TextView) findViewById(R.id.date);
        mCategory = (TextView) findViewById(R.id.category);

        mReceipt = (ImageView) findViewById(R.id.receiptImage);

        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);

        updateUi();
    }

    private void updateUi() {
        final Money amount = mExpense.getAmount();
        mDescription.setText(mExpense.getDescription());
        mMoney.setText(amount.getAmount().toPlainString());
        mCurrency.setText(amount.getCurrencyUnit().toString());
        mDate.setText(mDateFormat.format(mExpense.getTime()));
        mCategory.setText(mExpense.getCategory());

        // Still need to set the receipt image
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
