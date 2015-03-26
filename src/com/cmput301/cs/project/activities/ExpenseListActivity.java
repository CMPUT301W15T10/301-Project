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
import com.cmput301.cs.project.model.Claim;
import com.cmput301.cs.project.model.ClaimsList;
import com.cmput301.cs.project.model.Expense;

/**
 * An activity that shows a list of all {@link com.cmput301.cs.project.model.Expense Expenses} associated with a {@link com.cmput301.cs.project.model.Claim Claim}. </br>
 * When an {@link com.cmput301.cs.project.model.Expense Expense} is clicked, {@link com.cmput301.cs.project.activities.ExpenseViewActivity ExpenseViewActivity} is called. </br>
 * When the menu item is clicked, {@link com.cmput301.cs.project.activities.EditExpenseActivity EditExpenseActivity} is called to
 * generate a new expense.
 *
 * The claim which the expenses belong MUST be passed as an intent as App.KEY_CLAIM
 *
 * @author rozsa
 */

public class ExpenseListActivity extends ListActivity {
    private static final int EDIT_EXPENSE = 0;

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

        mAdapter = new ExpensesAdapter(this, mClaim.peekExpenses());
        setListAdapter(mAdapter);
        mAdapter.sort(Expense.OCCURRED_DESCENDING);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Expense expenseSelected = mAdapter.getItem(position);

        Intent intent = new Intent(this, ExpenseViewActivity.class);
        intent.putExtra(ExpenseViewActivity.KEY_EXPENSE, expenseSelected);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.expense_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.add_expense) {
            Intent intent = new Intent(ExpenseListActivity.this, EditExpenseActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_EXPENSE && resultCode == RESULT_OK) {
            final ClaimsList claimsList = ClaimsList.getInstance(this);
            final Expense newExpense = data.getParcelableExtra(App.KEY_EXPENSE);
            final Claim newClaim = mClaim.edit().putExpense(newExpense).build();
            claimsList.editClaim(mClaim, newClaim);
        }

    }

}
