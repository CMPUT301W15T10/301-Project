package com.cmput301.cs.project;/*
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

import com.cmput301.cs.project.model.Expense;
import com.cmput301.cs.project.model.Receipt;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExpenseTest {

    @Test
    public void independentId() {
        final long time = System.currentTimeMillis();
        final Money amount = Money.ofMajor(CurrencyUnit.USD, 20);

        final Expense first = new Expense.Builder().money(amount)
                .title("Pizza").category("Food").time(time).build();

        final Expense carbonCopy = new Expense.Builder().money(amount)
                .title("Pizza").category("Food").time(time).build();

        assertNotEquals(first.getId(), carbonCopy.getId());
    }

    @Test
    public void equality() {
        final long time = System.currentTimeMillis();
        final Money amount = Money.ofMajor(CurrencyUnit.USD, 20);

        final Expense first = new Expense.Builder().money(amount).id("rofl")
                .title("Pizza").category("Food").time(time).build();

        final Expense carbonCopy = new Expense.Builder().money(amount).id("rofl")
                .title("Pizza").category("Food").time(time).build();

        assertEquals(first, carbonCopy);
    }

    @Test
    public void compare() {
        final long time = System.currentTimeMillis();
        final long laterTime = time + 50;
        final Money amount = Money.ofMajor(CurrencyUnit.USD, 20);

        final Expense first = new Expense.Builder().money(amount).id("rofl")
                .title("Pizza").category("Food").time(time).build();

        final Expense carbonCopy = new Expense.Builder().money(amount).id("rofl")
                .title("Pizza").category("Food").time(time).build();

        final Expense later = new Expense.Builder().money(amount).id("rofl")
                .title("Pizza").category("Food").time(laterTime).build();

        assertTrue(first.compareTo(later) < 0);
        assertTrue(later.compareTo(first) > 0);

        assertTrue(first.compareTo(carbonCopy) == 0);
        assertTrue(carbonCopy.compareTo(first) == 0);
    }

    @Test
    public void defaultTitle() {
        final Expense expense = new Expense.Builder().build();
        assertEquals(Expense.TITLE_UNSPECIFIED, expense.getTitle());
    }

    @Test
    public void defaultCategory() {
        final Expense expense = new Expense.Builder().build();
        assertEquals(Expense.CATEGORY_UNCATEGORIZED, expense.getCategory());
    }

    @Test
    public void builderAmountNegative() {  // expect builder don't throw
        new Expense.Builder().money(Money.ofMajor(CurrencyUnit.USD, -20));
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderAmountNull() {
        new Expense.Builder().money(null);
    }

    @Test
    public void builderTitleNull() {
        final Expense expense = new Expense.Builder().title(null).build();
        assertEquals(Expense.TITLE_UNSPECIFIED, expense.getTitle());
    }

    @Test
    public void builderTitleEmpty() {
        final Expense expense = new Expense.Builder().title(" ").build();
        assertEquals(Expense.TITLE_UNSPECIFIED, expense.getTitle());
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderNegativeTime() {
        new Expense.Builder().time(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderIdNull() {
        new Expense.Builder().id(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void builderIdEmpty() {
        new Expense.Builder().id(" ");
    }

    @Test
    public void builderCategoryNull() {
        final Expense expense = new Expense.Builder().category(null).build();
        assertEquals(Expense.CATEGORY_UNCATEGORIZED, expense.getCategory());
    }

    @Test
    public void builderCategoryEmpty() {
        final Expense expense = new Expense.Builder().category(" ").build();
        assertEquals(Expense.CATEGORY_UNCATEGORIZED, expense.getCategory());
    }

    @Test
    public void builderReceiptNull() {
        final Expense expense = new Expense.Builder().receipt(null).build();
        assertNull(expense.getReceipt());

    }

    @Test
    public void builderReceipt() {
        final Receipt receipt = new Receipt("/path/to/receipt");
        final Expense expense = new Expense.Builder().receipt(receipt).build();
        assertEquals(receipt, expense.getReceipt());

    }

}