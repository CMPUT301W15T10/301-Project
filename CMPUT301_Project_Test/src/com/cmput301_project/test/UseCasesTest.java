package com.cmput301_project.test;/*
 * Copyright 2015 Edmond Chui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import model.Claim;
import model.Expense;
import utils.ClaimSaves;
import com.google.gson.Gson;
import junit.framework.TestCase;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.util.*;

/**
 * As per "Requirement Specifications (Use Cases)" on GitHub wiki, Revision 6018854.
 * Includes:
 * <ol>
 * <li>Use Case 2 (US 01.01.01, US 01.02.01, US 01.06.01)</li>
 * <li>Use Case 3 (US 01.03.01)</li>
 * <li>Use Case 4 (US 01.04.01, US 01.06.01)</li>
 * <li>Use Case 5 (US 01.05.01, US 01.06.01)</li>
 * <li>Use Case 6 (US 02.01.01, US 02.02.01)</li>
 * <li>Use Case 8 (US 03.01.01)</li>
 * <li>Use Case 9 (US 04.01.01-04.04.01, US 04.08.01)</li>
 * <li>Use Case 11 (US 03.02.01)</li>
 * <li>Use Case 12 (US 06.01.01, US 06.04.01)</li>
 * <li>Use Case 15 (US 4.01.01-4.04.01)</li>
 * <li>Use Case 17 (US 4.06.01)</li>
 * <li>Use Case 18 (US 4.07.01)</li>
 * <li>Use Case 19 (US 05.01.01, 08.04.01)</li>
 * <li>Use Case 29 (US 03.02.01)</li>
 * </ol>
 * Does not include (reason: involves UI testing, unavailable models):
 * <ul>
 * <li>Use Case 1</li>
 * <li>Use Case 7</li>
 * <li>Use Case 10 (US 03.03.01)</li>
 * <li>Use Case 13 (US 06.03.01)</li>
 * <li>Use Case 14 (US 09.01.01)</li>
 * <li>Use Case 16 (US 04.05.01, US 06.02.01, 08.05.01)</li>
 * <li>Use Case 20 (US 07.01.01, US 07.02.01)</li>
 * <li>Use Case 21 (US 07.03.01)</li>
 * <li>Use Case 22 (US 07.04.01)</li>
 * <li>Use Case 23 (US 07.05.01)</li>
 * <li>Use Case 24 (US 08.01.01-08.03.01)</li>
 * <li>Use Case 25 (US 08.06.01)</li>
 * <li>Use Case 26 (US 08.07.01)</li>
 * <li>Use Case 27 (US 08.08.01)</li>
 * <li>Use Case 28 (US 03.02.01)</li>
 * </ul>
 */
public class UseCasesTest extends TestCase {

    public static final long FIVE_DAYS = 432000000L;

    private ClaimSaves mClaimSaves;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mClaimSaves = ClaimSaves.ofTest();
    }

    /**
     * Use Case 2 (US 01.01.01, US 01.02.01, US 01.06.01)
     * Use Case 12 (US 06.01.01, US 06.04.01)
     */
    public void testCreateClaim() {
        final long now = System.currentTimeMillis();
        final long fiveDaysLater = now + FIVE_DAYS;

        final String dest = "Canada";
        final String reason = "Go home";

        // step 1
        final Claim.Builder builder = new Claim.Builder()
                .startTime(now)
                .endTime(fiveDaysLater)
                .putDestinationAndReason(dest, reason);  // step 2

        try {
            builder.startTime(builder.getEndTime() + 1);  // step 2.b
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {  // success
        }

        try {
            builder.endTime(builder.getStartTime() - 1);  // step 2.b
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {  // success
        }

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
    public void testReadingClaims() {
        final long now = System.currentTimeMillis();
        final long fiveDaysLater = now + FIVE_DAYS;

        final Claim claim = new Claim.Builder()
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
    public void testEditClaim() {
        final long now = System.currentTimeMillis();
        final long fiveDaysLater = now + FIVE_DAYS;

        // step 1
        final Claim perfect = new Claim.Builder()
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
     * Use Case 5 (US 01.05.01, US 01.06.01)
     */
    public void testDeleteClaim() {
        final List<Claim> changing = new ArrayList<Claim>();

        final long now = System.currentTimeMillis();
        final long fiveDaysLater = now + FIVE_DAYS;

        final Claim claim = new Claim.Builder()
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
     * Use Case 6 (US 02.01.01, US 02.02.01)
     */
    public void testSortClaims() {
        final Set<Claim> claims = new TreeSet<Claim>();
        final Claim.Builder builder = new Claim.Builder();

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

    /**
     * Use Case 8 (US 03.01.01)
     * Use Case 11 (US 03.02.01) (new tags are created implicitly)
     */
    public void testAddTag() {
        final Claim claim = new Claim.Builder().addTags("hello", "ok").build();  // step 1, 2
        mClaimSaves.saveAllClaims(Collections.singleton(claim));  // step 3

        final List<Claim> claims = mClaimSaves.readAllClaims();
        assertEquals(1, claims.size());
        assertEquals(claim, claims.get(0));
        assertEquals(2, claims.get(0).peekTags().size());
        assertTrue(claims.get(0).peekTags().contains("ok"));
        assertTrue(claims.get(0).peekTags().contains("hello"));
    }

    /**
     * Use Case 9 (US 04.01.01-04.04.01, US 04.08.01)
     * Use Case 15 (US 4.01.01-4.04.01)
     */
    public void testCreateExpense() {
        // step 1
        final Expense.Builder builder = new Expense.Builder()
                .time(System.currentTimeMillis())
                .category("Meal")
                .amountInBigDecimal(BigDecimal.TEN)
                .currencyUnit(CurrencyUnit.CAD)
                .title("Taxi food");

        final Expense expense = builder.build();  // step 2

        final Gson gson = ClaimSaves.getGson();
        final String serialized = gson.toJson(expense);  // step 3
        assertEquals(expense, gson.fromJson(serialized, Expense.class));
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
                .title("Taxi food")
                .build();

        final Gson gson = ClaimSaves.getGson();

        final String serialized = gson.toJson(expense);
        final Expense read = gson.fromJson(serialized, Expense.class);  // step 1, 2

        final Expense expense1 = Expense.Builder.copyFrom(read).title("no more food").build();  // step 3, 4
        final String serialized1 = gson.toJson(expense1);  // step 5
        final Expense read1 = gson.fromJson(serialized1, Expense.class);

        assertEquals("no more food", read1.getTitle());
        assertTrue(!read1.equals(read));
    }

    /**
     * Use Case 18 (US 4.07.01)
     */
    public void testDeleteExpense() {

        // step 1
        final Expense expense = new Expense.Builder()
                .time(System.currentTimeMillis())
                .category("Meal")
                .amountInBigDecimal(BigDecimal.TEN)
                .currencyUnit(CurrencyUnit.CAD)
                .title("Taxi food")
                .build();

        final Claim claim = new Claim.Builder()
                .startTime(System.currentTimeMillis())
                .endTime(System.currentTimeMillis() + FIVE_DAYS)
                .putDestinationAndReason("Canada", "Go home")
                .addExpense(expense)
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
    public void testReadExpenses() {
        // asserts the expenses are sorted by the order of entry
        final Expense first = new Expense.Builder()
                .time(System.currentTimeMillis())
                .category("Meal")
                .amountInBigDecimal(BigDecimal.TEN)
                .currencyUnit(CurrencyUnit.CAD)
                .title("Taxi food")
                .build();

        final Expense second = new Expense.Builder()
                .time(System.currentTimeMillis())
                .category("Meal Round 2")
                .amountInBigDecimal(BigDecimal.TEN)
                .currencyUnit(CurrencyUnit.CAD)
                .title("Taxi food")
                .build();

        final Expense third = new Expense.Builder()
                .time(System.currentTimeMillis())
                .category("Meal Round 3")
                .amountInBigDecimal(BigDecimal.TEN)
                .currencyUnit(CurrencyUnit.CAD)
                .title("Taxi food")
                .build();

        final Claim claim = new Claim.Builder()
                .startTime(System.currentTimeMillis())
                .endTime(System.currentTimeMillis() + FIVE_DAYS)
                .putDestinationAndReason("Canada", "Go home")
                .addExpense(first)
                .addExpense(second)
                .addExpense(third)
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
    public void testDeleteTag() {
        final Claim claim = new Claim.Builder().addTags("hello", "ok").build();  // step 1, 2
        mClaimSaves.saveAllClaims(Collections.singleton(claim));  // step 3

        final List<Claim> claims = mClaimSaves.readAllClaims();
        final Claim claim1 = Claim.Builder.copyFrom(claims.get(0)).removeTag("ok").build();
        mClaimSaves.saveAllClaims(Collections.singleton(claim1));

        final List<Claim> claims1 = mClaimSaves.readAllClaims();
        assertEquals(claim1, claims1.get(0));
        assertEquals(1, claims1.get(0).peekTags().size());
    }
}