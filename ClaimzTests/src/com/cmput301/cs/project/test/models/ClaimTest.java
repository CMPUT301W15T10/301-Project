package com.cmput301.cs.project.test.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.joda.money.CurrencyUnit;


import com.cmput301.cs.project.activities.ClaimListActivity;
import com.cmput301.cs.project.model.Claim;
import com.cmput301.cs.project.model.Expense;
import com.cmput301.cs.project.model.User;
import com.cmput301.cs.project.utils.LocalClaimSaver;
import com.google.gson.Gson;


import android.test.ActivityInstrumentationTestCase2;

public class ClaimTest extends ActivityInstrumentationTestCase2<ClaimListActivity> {

	public ClaimTest() {
		super(ClaimListActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public static final long FIVE_DAYS = 432000000L;
	
	public void testSortClaims() {
        final Set<Claim> claims = new TreeSet<Claim>();
        final Claim.Builder builder = new Claim.Builder(new User("name"));

        claims.add(builder.startTime(10).build());
        claims.add(builder.startTime(2).build());
        claims.add(builder.startTime(30).build());

        // step 1, 2, 3
        final List<Claim> sorted = new ArrayList<Claim>(claims);
        for (int i = 0, sortedSize = sorted.size(); i < sortedSize; i++) {
            final Claim claim = sorted.get(i);
            switch (i) {
                case 0:
                    assertEquals(2, claim.getStartTime());
                    break;
                case 1:
                    assertEquals(10, claim.getStartTime());
                    break;
                case 2:
                    assertEquals(30, claim.getStartTime());
                    break;
                default:
                    throw new AssertionError("unexpected index: " + i);
            }
        }
    }

	public void testCreateClaimSanity() {

        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + 10;
        final User user = new User("name");
        final String title = "my title";
        final String dest = "mydest";
        final String reason = "the reason";
        final Expense expense = new Expense.Builder().build();
        final UUID id = UUID.randomUUID();

        //Remember ids are important
        Claim.Builder builder = new Claim.Builder(user)
                .startTime(startTime)
                .endTime(endTime)
                .putDestinationAndReason(dest, reason)
                .putExpense(expense)
                .id(id.toString())
                .title(title);
        final Claim claim = builder.build();

        assertTrue(builder.isTitleSet());
        assertTrue(builder.isEndTimeSet());
        assertTrue(builder.isStartTimeSet());


        assertEquals(endTime, builder.getEndTime());
        assertEquals(startTime, builder.getStartTime());
        assertEquals(user, builder.getClaimant());
        assertTrue(builder.peekExpenses().contains(expense));
        assertEquals(id.toString(), builder.getId());
        assertEquals(title, builder.getTitle());


        assertEquals(endTime, claim.getEndTime());
        assertEquals(startTime, claim.getStartTime());
        assertEquals(user, claim.getClaimant());
        assertTrue(claim.peekExpenses().contains(expense));
        assertEquals(id.toString(), claim.getId());
        assertEquals(title, claim.getTitle());


    }
	
	
    public void testEquality() {
        final long time = System.currentTimeMillis();
        final User user = new User("name");
        final String title = "my title";
        final String dest = "mydest";
        final String reason = "the reason";
        final Expense expense = new Expense.Builder().build();
        final UUID id = UUID.randomUUID();

        //Remember ids are important
        Claim.Builder builder = new Claim.Builder(user)
                .endTime(time + 10)
                .startTime(time)
                .putDestinationAndReason(dest, reason)
                .putExpense(expense)
                .id(id.toString())
                .title(title);

        Claim carbonCopy = builder.build();
        Claim claim = builder.build();

        assertEquals(carbonCopy, claim);
        assertEquals(carbonCopy.hashCode(), claim.hashCode());
    }


   
    public void testInequality() {
        final long time = System.currentTimeMillis();
        final User user = new User("name");
        final String title = "my title";
        final String dest = "mydest";
        final String reason = "the reason";
        final Expense expense = new Expense.Builder().build();
        final UUID id = UUID.randomUUID();

        //Remember ids are important
        Claim.Builder builder = new Claim.Builder(user)
                .endTime(time + 10)
                .startTime(time)
                .putDestinationAndReason(dest, reason)
                .putExpense(expense)
                .id(id.toString())
                .title(title);
        Claim claim = builder.build();
        Claim almostCopy = builder.title("different title").build();

        assertTrue(!almostCopy.equals(claim));
        assertTrue(!(almostCopy.hashCode() == claim.hashCode()));

    }


    /**
     * Use Case 17 (US 4.06.01)
     */
    public void testEditExpense() {
        final Expense expense = new Expense.Builder()
                .time(System.currentTimeMillis())
                .category("Meal")
                .amountInBigDecimal(BigDecimal.TEN)
                .currencyUnit(CurrencyUnit.CAD)
                .description("Taxi food")
                .build();

        final Gson gson = LocalClaimSaver.getGson();

        final String serialized = gson.toJson(expense);
        final Expense read = gson.fromJson(serialized, Expense.class);  // step 1, 2

        final Expense expense1 = Expense.Builder.copyFrom(read).description("no more food").build();  // step 3, 4
        final String serialized1 = gson.toJson(expense1);  // step 5
        final Expense read1 = gson.fromJson(serialized1, Expense.class);

        assertEquals("no more food", read1.getDescription());
        assertTrue(!read1.equals(read));
    }


    /**
     * Use Case 19 (US 05.01.01, 08.04.01)
     * Ensuring that the expenses will be in the correct order, even when one is updated
     */
    public void testUpdateExpense() {
        final User user = new User("name");
        final Expense unchangedExpense = new Expense.Builder().build();
        Expense changedExpense = new Expense.Builder().build();

        int expectedPositionForChangedExpense = 0, expectedPositionForUnchangedExpense = 1;

        // The order should be changedExpense, unchangedExpense because that is the order they were added in
        Claim.Builder builder = new Claim.Builder(user)
                .putExpense(changedExpense)
                .putExpense(unchangedExpense);

        changedExpense = Expense.Builder.copyFrom(changedExpense).build();

        // It should now updated changedExpense in place
        builder.putExpense(changedExpense);

        final Claim claim = builder.build();

        assertEquals(expectedPositionForChangedExpense, claim.peekExpenses().indexOf(changedExpense));
        assertEquals(expectedPositionForUnchangedExpense, claim.peekExpenses().indexOf(unchangedExpense));
    }
	
}
