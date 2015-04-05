package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.models.Expense;
import com.cmput301.cs.project.utils.ReceiptLoading;
import org.joda.money.Money;

import java.text.DateFormat;


// This activities formatting and general flow was influenced by
// https://github.com/chuihinwai/echui-notes/blob/master/src/com/edmondapps/cs301/ass1/ExpenseActivity.java
// on March 12, 2015

/**
 * An activity that shows the details of an {@link com.cmput301.cs.project.models.Expense Expense}. <p>
 * Has a menu button that calls {@link com.cmput301.cs.project.activities.EditExpenseActivity EditExpenseActivity} for editing
 * on that expense.
 *
 * @author rozsa
 */

public class ExpenseViewActivity extends Activity {
    public static final String KEY_EXPENSE = "key_expense";
    private static final int EDIT_EXPENSE = 0;

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

        initExpenseController();

        updateUi();
    }

    private void initExpenseController() {
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

        mCompleted.setText(mExpense.isCompleted() ? "Completed" : "In Progress");

        if (mExpense.hasReceipt()) {
            final Uri receiptFileUri = ReceiptLoading.getReceiptUri(mExpense.getId());
            final BitmapDrawable drawable = new BitmapDrawable(getResources(), receiptFileUri.getPath());
            mReceipt.setImageDrawable(drawable);
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
            setResult(App.RESULT_DELETE, new Intent().putExtra(App.KEY_EXPENSE, mExpense));
            finish();
            return true;
        } else if (id == R.id.edit) {
            Intent intent = new Intent(this, EditExpenseActivity.class);
            intent.putExtra(App.KEY_EXPENSE, mExpense);
            startActivityForResult(intent, EDIT_EXPENSE);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_EXPENSE) {
            mExpense = data.getParcelableExtra(App.KEY_EXPENSE);
            setResult(RESULT_OK, new Intent().putExtra(App.KEY_EXPENSE, mExpense));

            updateUi();
        }
    }
}
