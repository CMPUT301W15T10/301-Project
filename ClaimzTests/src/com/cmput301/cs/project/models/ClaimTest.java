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

import com.cmput301.cs.project.serialization.LocalSaver;
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
public class ClaimTest extends TestCase {

    public static final long FIVE_DAYS = 432000000L;


    /**
     * Use Case 6 (US 02.01.01, US 02.02.01)
     */
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


    /**
     * Use Case 9 (US 04.01.01-04.04.01, US 04.08.01)
     * Use Case 15 (US 4.01.01-4.04.01)
     */
    public void testCreateClaimSanity() {

        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + 10;
        final User user = new User("name");
        final String dest = "mydest";
        final String reason = "the reason";
        final Expense expense = new Expense.Builder().build();
        final UUID id = UUID.randomUUID();

        //Remember ids are important
        Claim.Builder builder = new Claim.Builder(user)
                .startTime(startTime)
                .endTime(endTime)
                .putDestination(new Destination.Builder(dest, reason).build())
                .putExpense(expense)
                .id(id.toString());
        final Claim claim = builder.build();

        assertTrue(builder.isEndTimeSet());
        assertTrue(builder.isStartTimeSet());


        assertEquals(endTime, builder.getEndTime());
        assertEquals(startTime, builder.getStartTime());
        assertEquals(user, builder.getClaimant());
        assertTrue(builder.peekExpenses().contains(expense));
        assertEquals(id.toString(), builder.getId());


        assertEquals(endTime, claim.getEndTime());
        assertEquals(startTime, claim.getStartTime());
        assertEquals(user, claim.getClaimant());
        assertTrue(claim.peekExpenses().contains(expense));
        assertEquals(id.toString(), claim.getId());


    }

    public void testEquality() {
        final long time = System.currentTimeMillis();
        final User user = new User("name");
        final String dest = "mydest";
        final String reason = "the reason";
        final Expense expense = new Expense.Builder().build();
        final UUID id = UUID.randomUUID();

        //Remember ids are important
        Claim.Builder builder = new Claim.Builder(user)
                .endTime(time + 10)
                .startTime(time)
                .putDestination(new Destination.Builder(dest, reason).build())
                .putExpense(expense)
                .id(id.toString());

        Claim carbonCopy = builder.build();
        Claim claim = builder.build();

        assertEquals(carbonCopy, claim);
        assertEquals(carbonCopy.hashCode(), claim.hashCode());
    }

    public void testInequality() {
        final long time = System.currentTimeMillis();
        final User user = new User("name");
        final String dest = "mydest";
        final String reason = "the reason";
        final Expense expense = new Expense.Builder().build();
        final UUID id = UUID.randomUUID();
        final UUID otherId = UUID.randomUUID();

        //Remember ids are important
        Claim.Builder builder = new Claim.Builder(user)
                .endTime(time + 10)
                .startTime(time)
                .putDestination(new Destination.Builder(dest, reason).build())
                .putExpense(expense)
                .id(id.toString());
        Claim claim = builder.build();

        Claim otherClaim = builder.id(otherId.toString()).build();

        assertTrue(!otherClaim.equals(claim));
        assertTrue(otherClaim.hashCode() != claim.hashCode());
    }


    /**
     * Use Case 17 (US 4.06.01)
     */
    public void testEditExpense() {
        final Expense expense = new Expense.Builder()
                .time(System.currentTimeMillis())
                .category("Meal")
                .amount(BigDecimal.TEN)
                .currencyUnit(CurrencyUnit.CAD)
                .description("Taxi food")
                .build();

        final Gson gson = LocalSaver.getGson();

        final String serialized = gson.toJson(expense);
        final Expense read = gson.fromJson(serialized, Expense.class);  // step 1, 2

        final Expense expense1 = read.edit().description("no more food").build();  // step 3, 4
        final String serialized1 = gson.toJson(expense1);  // step 5
        final Expense read1 = gson.fromJson(serialized1, Expense.class);

        assertEquals("no more food", read1.getDescription());
        assertTrue(!read1.equals(read));
    }
}