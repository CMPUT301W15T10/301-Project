package com.cmput301.cs.project.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
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
 * The claim which the expenses belong MUST be passed as an intent as App.KEY_CLAIM_ID
 *
 * @author rozsa
 */

public class ExpenseListActivity extends ListActivity {
    private static final int VIEW_EXPENSE = 0;

    private Claim mClaim;

    private ExpensesAdapter mAdapter;

    private MenuItem mAddExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_list_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final ClaimsList claimsList = ClaimsList.getInstance(this);

        String claimId = getIntent().getStringExtra(App.KEY_CLAIM_ID);
        if (claimId == null) {
            throw new IllegalStateException("Must have claim id passed in using KEY_CLAIM_ID");
        }

        mClaim = claimsList.getClaimById(claimId);

        updateAddExpenseMenuItem();
        invalidateOptionsMenu();

        updateList();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Expense expenseSelected = mAdapter.getItem(position);

        Intent intent = new Intent(this, ExpenseViewActivity.class);
        intent.putExtra(App.KEY_CLAIM_ID, mClaim.getId());
        intent.putExtra(App.KEY_EXPENSE_ID, expenseSelected.getId());
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
        if (id == R.id.add_expense) {
            if (mClaim.isEditable()) {
                Intent intent = new Intent(ExpenseListActivity.this, EditExpenseActivity.class);
                intent.putExtra(App.KEY_CLAIM_ID, mClaim.getId());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Claim cannot be edited", Toast.LENGTH_LONG).show();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == App.RESULT_DELETE && requestCode == VIEW_EXPENSE) {
            deleteExpense(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void deleteExpense(Intent data) {
        final ClaimsList claimsList = ClaimsList.getInstance(this);
        final String expenseId = data.getStringExtra(App.KEY_EXPENSE_ID);
        final Expense removedExpense = mClaim.getExpense(expenseId);
        final Claim newClaim = mClaim.edit().removeExpense(removedExpense).build();

        claimsList.editClaim(newClaim);
        mClaim = newClaim;

        updateList();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mAddExpense = menu.findItem(R.id.add_expense);
        updateAddExpenseMenuItem();
        return true;
    }

    /*
     * Must call invalidateOptionsMenu() outside of onPrepareOptionsMenu(Menu)
     */
    private void updateAddExpenseMenuItem() {
        if (mAddExpense == null)
            return;

        final boolean editable = mClaim.isEditable();
        mAddExpense.setEnabled(editable);
        mAddExpense.getIcon().setAlpha(editable ? 255 : 255 / 2);
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
