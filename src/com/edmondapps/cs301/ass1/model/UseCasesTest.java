/*
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

package com.edmondapps.cs301.ass1.model;

import com.edmondapps.cs301.ass1.utils.ClaimSaves;
import com.google.gson.Gson;
import junit.framework.TestCase;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;

public class UseCasesTest extends TestCase {

    public static final long FIVE_DAYS = 432000000L;

    /**
     * Use Case 3 (US 01.01.01, US 01.02.01, US 01.06.01)
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

        final Gson gson = ClaimSaves.getGson();  // step 3
        final String serialized = gson.toJson(perfect);
        assertEquals(perfect, gson.fromJson(serialized, Claim.class));
    }

    /**
     * Use Case 5 (US 01.04.01, US 01.06.01)
     * <br/>
     * relies on {@link #testCreateClaim()}
     */
    public void testEditClaim() {
        final long now = System.currentTimeMillis();
        final long fiveDaysLater = now + FIVE_DAYS;

        final String dest = "Canada";
        final String reason = "Go home";

        // step 1
        final Claim perfect = new Claim.Builder()
                .startTime(now)
                .endTime(fiveDaysLater)
                .putDestinationAndReason(dest, reason)
                .build();

        final String dest2 = "USA";
        final String reason2 = "???";

        final Claim claim = Claim.Builder.copyFrom(perfect)
                .startTime(now + 1)
                .endTime(fiveDaysLater + 1)
                .putDestinationAndReason(dest2, reason2)
                .build();  // step 2

        final Gson gson = ClaimSaves.getGson();
        final String serialized = gson.toJson(claim);  // step 3
        assertEquals(claim, gson.fromJson(serialized, Claim.class));
    }

    /**
     * Use Case 9 (US 04.01.01-04.04.01, US 04.08.01)
     */
    public void testCreateExpense() {
        final long now = System.currentTimeMillis();
        final String category = "Meal";
        final String title = "Taxi food";

        // step 1
        final Expense.Builder builder = new Expense.Builder()
                .time(now)
                .category(category)
                .amountInBigDecimal(BigDecimal.TEN)
                .currencyUnit(CurrencyUnit.CAD)
                .title(title)
                .completed(false);

        final Expense expense = builder.build();  // step 2

        final Gson gson = ClaimSaves.getGson();
        final String serialized = gson.toJson(expense);  // step 3
        assertEquals(expense, gson.fromJson(serialized, Expense.class));
    }
}