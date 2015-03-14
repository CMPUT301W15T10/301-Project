package com.cmput301.cs.project.utils;

import com.cmput301.cs.project.controllers.TagsManager;
import com.cmput301.cs.project.model.Claim;
import com.cmput301.cs.project.model.Expense;
import com.cmput301.cs.project.model.Tag;
import com.cmput301.cs.project.model.User;
import junit.framework.TestCase;
import org.joda.money.CurrencyUnit;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClaimSavesTest extends TestCase {
    public static final long FIVE_DAYS = 432000000L;

    private ClaimSaves mClaimSaves;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mClaimSaves = new MockClaimSaves();
    }

    /**
     * Use Case 5 (US 01.05.01, US 01.06.01)
     */
    @Test
    public void testDeleteClaim() {
        final List<Claim> changing = new ArrayList<Claim>();

        final long now = System.currentTimeMillis();
        final long fiveDaysLater = now + FIVE_DAYS;

        final Claim claim = new Claim.Builder(new User("name"))
                .startTime(now)
                .endTime(fiveDaysLater)
                .putDestinationAndReason("Canada", "Go home")
                .build();
        changing.add(claim);

        mClaimSaves.saveAllClaims(changing);  // step 1

        final List<Claim> claims = mClaimSaves.readAllClaims();
        assertEquals(1, claims.size());
        assertEquals(claim, claims.get(0));

        changing.remove(claim);  // step 2, 3

        mClaimSaves.saveAllClaims(changing);

        final List<Claim> read = mClaimSaves.readAllClaims();
        assertTrue(!read.contains(claim));
        assertEquals(0, read.size());
    }


    /**
     * Use Case 2 (US 01.01.01, US 01.02.01, US 01.06.01)
     * Use Case 12 (US 06.01.01, US 06.04.01)
     */
    @Test
    public void testCreateClaim() {
        final long now = System.currentTimeMillis();
        final long fiveDaysLater = now + FIVE_DAYS;

        final String dest = "Canada";
        final String reason = "Go home";

        // step 1
        final Claim.Builder builder = new Claim.Builder(new User("name"))
                .startTime(now)
                .endTime(fiveDaysLater)
                .putDestinationAndReason(dest, reason);  // step 2

        builder.startTime(builder.getEndTime() + 1); // step 2.b
        assertEquals(now, builder.getStartTime());

        builder.endTime(builder.getStartTime() - 1); // step 2.b
        assertEquals(fiveDaysLater, builder.getEndTime());

        try {
            builder.putDestinationAndReason(" ", " ");  // step 2.b
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {  // success
        }

        try {
            builder.putDestinationAndReason(dest, " ");  // step 2.b
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {  // success
        }

        try {
            builder.putDestinationAndReason(" ", reason);  // step 2.b
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {  // success
        }

        final Claim perfect = builder.build();
        assertEquals(now, perfect.getStartTime());
        assertEquals(fiveDaysLater, perfect.getEndTime());
        assertTrue(perfect.peekDestinations().containsKey(dest));
        assertTrue(perfect.peekDestinations().containsValue(reason));

        mClaimSaves.saveAllClaims(Collections.singleton(perfect));  // step 3
        final List<Claim> claims = mClaimSaves.readAllClaims();

        assertEquals(1, claims.size());
        assertEquals(perfect, claims.get(0));
    }

    /**
     * Use Case 3 (US 01.03.01)
     */
    @Test
    public void testReadingClaims() {
        final long now = System.currentTimeMillis();
        final long fiveDaysLater = now + FIVE_DAYS;

        final Claim claim = new Claim.Builder(new User("name"))
                .startTime(now)
                .endTime(fiveDaysLater)
                .putDestinationAndReason("Canada", "Go home")
                .build();

        mClaimSaves.saveAllClaims(Collections.singleton(claim));  // step 1

        // asserts multiple calls to read the claims would not corrupt the data
        final List<Claim> claims = mClaimSaves.readAllClaims();
        final List<Claim> claims2 = mClaimSaves.readAllClaims();
        assertEquals(claims, claims2);
    }


    /**
     * Use Case 4 (US 01.04.01, US 01.06.01)
     * <br/>
     * relies on {@link #testCreateClaim()}
     */
    @Test
    public void testEditClaim() {
        final long now = System.currentTimeMillis();
        final long fiveDaysLater = now + FIVE_DAYS;

        // step 1
        final Claim perfect = new Claim.Builder(new User("name"))
                .startTime(now)
                .endTime(fiveDaysLater)
                .putDestinationAndReason("Canada", "Go home")
                .build();

        final Claim claim = Claim.Builder.copyFrom(perfect)
                .startTime(now + 1)
                .endTime(fiveDaysLater + 1)
                .putDestinationAndReason("USA", "???")
                .build();  // step 2

        assertTrue(!claim.equals(perfect));

        mClaimSaves.saveAllClaims(Collections.singleton(claim));  // step 3
        final List<Claim> claims = mClaimSaves.readAllClaims();

        assertEquals(1, claims.size());
        assertEquals(claim, claims.get(0));
    }


    /**
     * Use Case 8 (US 03.01.01)
     * Use Case 11 (US 03.02.01) (new tags are created implicitly)
     */
    @Test
    public void testAddTag() {
        final Tag tag = TagsManager.ofClaimSaves(mClaimSaves).getTagByName("myTag");
        final Claim claim = new Claim.Builder(new User("name")).addTag(tag).build();  // step 1, 2
        mClaimSaves.saveAllClaims(Collections.singleton(claim));  // step 3

        final List<Claim> claims = mClaimSaves.readAllClaims();
        assertEquals(1, claims.size());
        assertEquals(claim, claims.get(0));
        assertEquals(1, claims.get(0).peekTags().size());
        assertTrue(claims.get(0).peekTags().contains(tag));
    }

    /**
     * Use Case 18 (US 4.07.01)
     */
    @Test
    public void testDeleteExpense() {

        // step 1
        final Expense expense = new Expense.Builder()
                .time(System.currentTimeMillis())
                .category("Meal")
                .amountInBigDecimal(BigDecimal.TEN)
                .currencyUnit(CurrencyUnit.CAD)
                .description("Taxi food")
                .build();

        final Claim claim = new Claim.Builder(new User("name"))
                .startTime(System.currentTimeMillis())
                .endTime(System.currentTimeMillis() + FIVE_DAYS)
                .putDestinationAndReason("Canada", "Go home")
                .putExpense(expense)
                .build();

        mClaimSaves.saveAllClaims(Collections.singleton(claim));

        final List<Claim> read = mClaimSaves.readAllClaims();
        assertEquals(1, read.size());
        assertEquals(1, read.get(0).peekExpenses().size());
        assertEquals(claim, read.get(0));

        final Claim removed = Claim.Builder.copyFrom(read.get(0)).removeExpenseById(expense).build();
        mClaimSaves.saveAllClaims(Collections.singleton(removed));

        final List<Claim> read1 = mClaimSaves.readAllClaims();
        assertEquals(1, read1.size());
        assertEquals(0, read1.get(0).peekExpenses().size());
    }

    /**
     * Use Case 19 (US 05.01.01, 08.04.01)
     */
    @Test
    public void testReadExpenses() {
        // asserts the expenses are sorted by the order of entry
        final Expense first = new Expense.Builder()
                .time(System.currentTimeMillis())
                .category("Meal")
                .amountInBigDecimal(BigDecimal.TEN)
                .currencyUnit(CurrencyUnit.CAD)
                .description("Taxi food")
                .build();

        final Expense second = new Expense.Builder()
                .time(System.currentTimeMillis())
                .category("Meal Round 2")
                .amountInBigDecimal(BigDecimal.TEN)
                .currencyUnit(CurrencyUnit.CAD)
                .description("Taxi food")
                .build();

        final Expense third = new Expense.Builder()
                .time(System.currentTimeMillis())

                .category("Meal Round 3")
                .amountInBigDecimal(BigDecimal.TEN)
                .currencyUnit(CurrencyUnit.CAD)
                .description("Taxi food")
                .build();

        final Claim claim = new Claim.Builder(new User("name"))
                .startTime(System.currentTimeMillis())
                .endTime(System.currentTimeMillis() + FIVE_DAYS)
                .putDestinationAndReason("Canada", "Go home")
                .putExpense(first)
                .putExpense(second)
                .putExpense(third)
                .build();

        mClaimSaves.saveAllClaims(Collections.singleton(claim));

        final List<Claim> claims = mClaimSaves.readAllClaims();
        assertEquals(1, claims.size());
        assertEquals(claim, claims.get(0));

        final List<Expense> expenses = claims.get(0).peekExpenses();
        assertEquals(first, expenses.get(0));
        assertEquals(second, expenses.get(1));
        assertEquals(third, expenses.get(2));
    }

    /**
     * Use Case 29 (US 03.02.01)
     */
    @Test
    public void testDeleteTag() {
        final Tag tag = TagsManager.ofClaimSaves(mClaimSaves).getTagByName("MyTag");
        final Claim claim = new Claim.Builder(new User("name")).addTag(tag).build();  // step 1, 2
        mClaimSaves.saveAllClaims(Collections.singleton(claim));  // step 3

        final List<Claim> claims = mClaimSaves.readAllClaims();
        final Claim claim1 = Claim.Builder.copyFrom(claims.get(0)).removeTag(tag).build();
        mClaimSaves.saveAllClaims(Collections.singleton(claim1));

        final List<Claim> claims1 = mClaimSaves.readAllClaims();
        assertEquals(claim1, claims1.get(0));
        assertEquals(0, claims1.get(0).peekTags().size());
    }

}