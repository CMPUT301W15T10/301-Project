package com.cmput301.cs.project.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.cmput301.cs.project.R;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.adapters.ExpensesAdapter;
import com.cmput301.cs.project.model.Claim;
import com.cmput301.cs.project.model.Expense;

public class ExpenseListActivity extends ListActivity {
    public static final String KEY_CLAIM = "key_claim";

    private Claim mClaim;

    private ExpensesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_list_activity);

        Log.w("Hi there", "hey");

        mClaim = getIntent().getParcelableExtra(KEY_CLAIM);
        if (mClaim == null) {
            Expense expense = new Expense.Builder().build();
            Expense expense1 = new Expense.Builder().description("Hi bob").build();
            mClaim = new Claim.Builder().putExpense(expense).claimaint(App.get(this).getUser()).putExpense(expense1).build();

            //throw new IllegalStateException("Must have claim passed in using KEY_CLAIM");
        }

        mAdapter = new ExpensesAdapter(this, mClaim.peekExpenses());
        setListAdapter(mAdapter);
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
            // Need to handle creating a new Expense
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
