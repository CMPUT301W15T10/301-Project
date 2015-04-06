package com.cmput301.cs.project.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.adapters.ExpensesAdapter;
import com.cmput301.cs.project.models.Claim;
import com.cmput301.cs.project.models.ClaimsList;
import com.cmput301.cs.project.models.Expense;

/**
 * An activity that shows a list of all {@link com.cmput301.cs.project.models.Expense Expenses} associated with a {@link com.cmput301.cs.project.models.Claim Claim}. <p>
 * When an {@link com.cmput301.cs.project.models.Expense Expense} is clicked, {@link com.cmput301.cs.project.activities.ExpenseViewActivity ExpenseViewActivity} is called. <p>
 * When the menu item is clicked, {@link com.cmput301.cs.project.activities.EditExpenseActivity EditExpenseActivity} is called to
 * generate a new expense.
 * <p/>
 * The claim which the expenses belong MUST be passed as an intent as App.KEY_CLAIM
 *
 * @author rozsa
 */

public class ExpenseListActivity extends ListActivity {
    private static final int VIEW_EXPENSE = 0;
    private static final int NEW_EXPENSE = 1;

    private Claim mClaim;

    private ExpensesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_list_activity);

        mClaim = getIntent().getParcelableExtra(App.KEY_CLAIM);
        if (mClaim == null) {
            throw new IllegalStateException("Must have claim passed in using KEY_CLAIM");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        final ClaimsList claimsList = ClaimsList.getInstance(this);
        mClaim = claimsList.getClaimById(mClaim.getId());

        updateList();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Expense expenseSelected = mAdapter.getItem(position);

        Intent intent = new Intent(this, ExpenseViewActivity.class);
        intent.putExtra(App.KEY_CLAIM, mClaim);
        intent.putExtra(App.KEY_EXPENSE, expenseSelected);
        startActivityForResult(intent, VIEW_EXPENSE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.expense_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.add_expense) {
            Intent intent = new Intent(ExpenseListActivity.this, EditExpenseActivity.class);
            startActivityForResult(intent, NEW_EXPENSE);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == App.RESULT_DELETE && requestCode == VIEW_EXPENSE) {
            deleteExpense(data);
        } else if (resultCode == RESULT_OK && requestCode == NEW_EXPENSE) {
            updateExpense(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateExpense(Intent data) {
        final ClaimsList claimsList = ClaimsList.getInstance(this);
        final Expense newExpense = data.getParcelableExtra(App.KEY_EXPENSE);
        final Claim newClaim = mClaim.edit().putExpense(newExpense).build();

        claimsList.editClaim(newClaim);
        mClaim = newClaim;

        updateList();
    }

    private void deleteExpense(Intent data) {
        final ClaimsList claimsList = ClaimsList.getInstance(this);
        final Expense newExpense = data.getParcelableExtra(App.KEY_EXPENSE);
        final Claim newClaim = mClaim.edit().removeExpense(newExpense).build();

        claimsList.editClaim(newClaim);
        mClaim = newClaim;

        updateList();
    }

    /*
     * This is basically a hack..... Will create a new adapter everytime it
     * needs to update the list
     */
    private void updateList() {
        mAdapter = new ExpensesAdapter(this, mClaim.peekExpenses());
        setListAdapter(mAdapter);

        mAdapter.sort(Expense.OCCURRED_DESCENDING);
    }
}
