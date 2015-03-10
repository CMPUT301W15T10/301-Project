package com.cmput301.cs.project.models;/*
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

import com.cmput301.cs.project.project.model.*;
import com.cmput301.cs.project.project.utils.ClaimSaves;
import com.google.gson.Gson;
import junit.framework.TestCase;
import org.joda.money.CurrencyUnit;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
public class ClaimTest extends TestCase {

    public static final long FIVE_DAYS = 432000000L;






    /**
     * Use Case 6 (US 02.01.01, US 02.02.01)
     */
    public void testSortClaims() {
        final Set<Claim> claims = new TreeSet<Claim>();
        final Claim.Builder builder = new Claim.Builder().claimaint(new User("name"));

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
     * Use Case 9 (US 04.01.01-04.04.01, US 04.08.01)
     * Use Case 15 (US 4.01.01-4.04.01)
     */
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
        Claim.Builder builder = new Claim.Builder()
                .endTime(endTime)
                .startTime(startTime)
                .claimaint(user)
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

    @Test
    public void testEquality() {
        final long time = System.currentTimeMillis();
        final User user = new User("name");
        final String title = "my title";
        final String dest = "mydest";
        final String reason = "the reason";
        final Expense expense = new Expense.Builder().build();
        final UUID id = UUID.randomUUID();

        //Remember ids are important
        Claim.Builder builder = new Claim.Builder()
                .endTime(time + 10)
                .startTime(time)
                .claimaint(user)
                .putDestinationAndReason(dest, reason)
                .putExpense(expense)
                .id(id.toString())
                .title(title);

        Claim carbonCopy = builder.build();
        Claim claim = builder.build();

        assertEquals(carbonCopy, claim);
        assertEquals(carbonCopy.hashCode(), claim.hashCode());
    }


    @Test
    public void testInequality() {
        final long time = System.currentTimeMillis();
        final User user = new User("name");
        final String title = "my title";
        final String dest = "mydest";
        final String reason = "the reason";
        final Expense expense = new Expense.Builder().build();
        final UUID id = UUID.randomUUID();

        //Remember ids are important
        Claim.Builder builder = new Claim.Builder()
                .endTime(time + 10)
                .startTime(time)
                .claimaint(user)
                .putDestinationAndReason(dest, reason)
                .putExpense(expense)
                .id(id.toString())
                .title(title);
        Claim claim = builder.build();
        Claim almostCopy = builder.title("different title").build();

        assertTrue(almostCopy.equals(claim));
        assertTrue(almostCopy.hashCode() == claim.hashCode());

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

        final Gson gson = ClaimSaves.getGson();

        final String serialized = gson.toJson(expense);
        final Expense read = gson.fromJson(serialized, Expense.class);  // step 1, 2

        final Expense expense1 = Expense.Builder.copyFrom(read).description("no more food").build();  // step 3, 4
        final String serialized1 = gson.toJson(expense1);  // step 5
        final Expense read1 = gson.fromJson(serialized1, Expense.class);

        assertEquals("no more food", read1.getDescription());
        assertTrue(!read1.equals(read));
    }

}