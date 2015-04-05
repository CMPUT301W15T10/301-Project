package com.cmput301.cs.project.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public class ExpenseTest extends TestCase {
    private static final String VALID_CATEGORY = Expense.CATEGORIES.iterator().next();

    public void testIndependentId() {
        final long time = System.currentTimeMillis();
        final Money amount = Money.ofMajor(CurrencyUnit.USD, 20);

        final Expense first = new Expense.Builder().money(amount).description("Pizza").category(VALID_CATEGORY).time(time)
                .build();

        final Expense carbonCopy = new Expense.Builder().money(amount).description("Pizza").category(VALID_CATEGORY).time(time)
                .build();

        assertTrue(!(first.getId(). equals(carbonCopy.getId())));
    }

    public void testEquality() {
        final long time = System.currentTimeMillis();
        final Money amount = Money.ofMajor(CurrencyUnit.USD, 20);

        final Expense first = new Expense.Builder().money(amount).id("rofl").description("Pizza").category(VALID_CATEGORY)
                .time(time).build();

        final Expense carbonCopy = new Expense.Builder().money(amount).id("rofl").description("Pizza").category(VALID_CATEGORY)
                .time(time).build();

        assertEquals(first, carbonCopy);
        assertEquals(first.hashCode(), carbonCopy.hashCode());
    }

    public void TestInequality() {
        final long time = System.currentTimeMillis();
        final Money amount = Money.ofMajor(CurrencyUnit.USD, 20);

        final Expense first = new Expense.Builder().money(amount).id("rofl").description("Hot Dog").category(VALID_CATEGORY)
                .time(time).build();

        final Expense carbonCopy = new Expense.Builder().money(amount).id("rofl").description("Pizza").category(VALID_CATEGORY)
                .time(time).build();

        assertTrue(!first.equals(carbonCopy));
        assertTrue(!(first.hashCode() == carbonCopy.hashCode()));
    }

    public void testCompare() {
        final long time = System.currentTimeMillis();
        final long laterTime = time + 50;
        final Money amount = Money.ofMajor(CurrencyUnit.USD, 20);

        final Expense first = new Expense.Builder().money(amount).id("rofl").description("Pizza").category(VALID_CATEGORY)
                .time(time).build();

        final Expense carbonCopy = new Expense.Builder().money(amount).id("rofl").description("Pizza").category(VALID_CATEGORY)
                .time(time).build();

        final Expense later = new Expense.Builder().money(amount).id("rofl").description("Pizza").category(VALID_CATEGORY)
                .time(laterTime).build();

        assertTrue(first.compareTo(later) < 0);
        assertTrue(later.compareTo(first) > 0);

        assertTrue(first.compareTo(carbonCopy) == 0);
        assertTrue(carbonCopy.compareTo(first) == 0);
    }
    
    /**
     * Use Case 19 (US 05.01.01, 08.04.01)
     * Ensuring that the expenses will be in the correct order, even when one is updated using OCCURRED_DESCENDING
     */
    public void testSort() {
        List<Expense> expenses = new ArrayList<Expense>();

        final Expense expenseOne = new Expense.Builder().build();
        
        // This will ensure that there is an actual difference between their time occured/created
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
        }

        final Expense expenseTwo = new Expense.Builder().build();
        
        // Not the correct order once sorted
        expenses.add(expenseTwo);
        expenses.add(expenseOne);
        
        Collections.sort(expenses, Expense.OCCURRED_DESCENDING);

        assertEquals(0, expenses.indexOf(expenseOne));
        assertEquals(1, expenses.indexOf(expenseTwo));
    }

    public void testDefaultTitle() {
        final Expense expense = new Expense.Builder().build();
        assertEquals(null, expense.getDescription());
    }

    public void testDefaultCategory() {
        final Expense expense = new Expense.Builder().build();
        assertEquals(null, expense.getCategory());
    }

    public void testBuilderAmountNegative() { // expect builder don't throw
        new Expense.Builder().money(Money.ofMajor(CurrencyUnit.USD, -20));
    }

    public void testBuilderAmountNull() {
        try {
            new Expense.Builder().money(null);
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    public void testBuilderTitleNull() {
        final Expense expense = new Expense.Builder().description(null).build();
        assertEquals(null, expense.getDescription());
    }

    public void testBuilderTitleEmpty() {
        final Expense expense = new Expense.Builder().description("").build();
        assertEquals("", expense.getDescription());
    }

    public void testBuilderNegativeTime() {
        new Expense.Builder().time(-1); // negative time gets dropped and
                                        // doesn't crash
    }

    public void testBuilderIdNull() {
        try {
            new Expense.Builder().id(null);
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    public void testBuilderIdEmpty() {
        try {
            new Expense.Builder().id(" ");
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    public void testBuilderCategoryNull() {
        try {
            new Expense.Builder().category(null);
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    public void testBuilderCategoryEmpty() {
        try {
            new Expense.Builder().category(" ");
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    public void testBuilderReceiptNull() {
        final Expense expense = new Expense.Builder().receipt(null).build();
        assertNull(expense.getReceipt());

    }

    public void testBuilderReceipt() {
        final Receipt receipt = new Receipt("/path/to/receipt");
        final Expense expense = new Expense.Builder().receipt(receipt).build();
        assertEquals(receipt, expense.getReceipt());

    }

    public void testBuilderSanity() {
        final long time = System.currentTimeMillis();
        final Receipt receipt = new Receipt("/path");
        final String title = "My title";
        final UUID id = UUID.randomUUID();
        final Expense.Builder builder = new Expense.Builder().receipt(receipt).amount(BigDecimal.TEN)
                .category(VALID_CATEGORY).completed(true).currencyUnit(CurrencyUnit.CAD).time(time).description(title)
                .id(id.toString());
        assertTrue(builder.isCategorySet());
        assertTrue(builder.isTimeSet());
        assertTrue(builder.isDescriptionSet());

        assertEquals(receipt, builder.getReceipt());
        assertEquals(0, BigDecimal.TEN.compareTo(builder.getMoney().getAmount()));
        assertEquals(VALID_CATEGORY, builder.getCategory());
        assertTrue(builder.isCompleted());
        assertEquals(CurrencyUnit.CAD, builder.getMoney().getCurrencyUnit());
        assertEquals(time, builder.getTime());
        assertEquals(title, builder.getDescription());
        assertEquals(id.toString(), builder.getId());

        Expense expense = builder.build();

        assertEquals(receipt, expense.getReceipt());
        assertEquals(0, BigDecimal.TEN.compareTo(expense.getAmount().getAmount()));
        assertEquals(VALID_CATEGORY, expense.getCategory());
        assertTrue(expense.isCompleted());
        assertEquals(CurrencyUnit.CAD, expense.getAmount().getCurrencyUnit());
        assertEquals(time, expense.getTime());
        assertEquals(title, expense.getDescription());
        assertEquals(id.toString(), expense.getId());
    }
}