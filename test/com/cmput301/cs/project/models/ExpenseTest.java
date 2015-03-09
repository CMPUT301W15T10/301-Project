package com.cmput301.cs.project.models;

import com.cmput301.cs.project.project.model.Expense;
import com.cmput301.cs.project.project.model.Receipt;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.*;

public class ExpenseTest {

    @Test
    public void independentId() {
        final long time = System.currentTimeMillis();
        final Money amount = Money.ofMajor(CurrencyUnit.USD, 20);

        final Expense first = new Expense.Builder().money(amount)
                .description("Pizza").category("Food").time(time).build();

        final Expense carbonCopy = new Expense.Builder().money(amount)
                .description("Pizza").category("Food").time(time).build();
        
        //assertNotEquals(first.getId(), carbonCopy.getId());
    }

    @Test
         public void equality() {
        final long time = System.currentTimeMillis();
        final Money amount = Money.ofMajor(CurrencyUnit.USD, 20);

        final Expense first = new Expense.Builder().money(amount).id("rofl")
                .description("Pizza").category("Food").time(time).build();

        final Expense carbonCopy = new Expense.Builder().money(amount).id("rofl")
                .description("Pizza").category("Food").time(time).build();

        assertEquals(first, carbonCopy);
        assertEquals(first.hashCode(), carbonCopy.hashCode());
    }

    @Test
    public void inequality() {
        final long time = System.currentTimeMillis();
        final Money amount = Money.ofMajor(CurrencyUnit.USD, 20);

        final Expense first = new Expense.Builder().money(amount).id("rofl")
                .description("Hot Dog").category("Food").time(time).build();

        final Expense carbonCopy = new Expense.Builder().money(amount).id("rofl")
                .description("Pizza").category("Food").time(time).build();

        assertNotEquals(first, carbonCopy);
        assertNotEquals(first.hashCode(), carbonCopy.hashCode());
    }

    @Test
    public void compare() {
        final long time = System.currentTimeMillis();
        final long laterTime = time + 50;
        final Money amount = Money.ofMajor(CurrencyUnit.USD, 20);

        final Expense first = new Expense.Builder().money(amount).id("rofl")
                .description("Pizza").category("Food").time(time).build();

        final Expense carbonCopy = new Expense.Builder().money(amount).id("rofl")
                .description("Pizza").category("Food").time(time).build();

        final Expense later = new Expense.Builder().money(amount).id("rofl")
                .description("Pizza").category("Food").time(laterTime).build();

        assertTrue(first.compareTo(later) < 0);
        assertTrue(later.compareTo(first) > 0);

        assertTrue(first.compareTo(carbonCopy) == 0);
        assertTrue(carbonCopy.compareTo(first) == 0);
    }

    @Test
    public void defaultTitle() {
        final Expense expense = new Expense.Builder().build();
        assertEquals(Expense.DESCRIPTION_UNSPECIFIED, expense.getDescription());
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
        final Expense expense = new Expense.Builder().description(null).build();
        assertEquals(Expense.DESCRIPTION_UNSPECIFIED, expense.getDescription());
    }

    @Test
    public void builderTitleEmpty() {
        final Expense expense = new Expense.Builder().description(" ").build();
        assertEquals(Expense.DESCRIPTION_UNSPECIFIED, expense.getDescription());
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

    @Test
    public void testBuilderSanity(){
        final long time = System.currentTimeMillis();
        final String category = "Food";
        final Receipt receipt = new Receipt("/path");
        final String title = "My title";
        final UUID id = UUID.randomUUID();
        final Expense.Builder builder = new Expense.Builder()
                .receipt(receipt)
                .amountInBigDecimal(BigDecimal.TEN)
                .category(category)
                .completed(true)
                .currencyUnit(CurrencyUnit.CAD)
                .time(time)
                .description(title)
                .id(id.toString());
        assertTrue(builder.isCategorySet());
        assertTrue(builder.isTimeSet());
        assertTrue(builder.isDescriptionSet());

        assertEquals(receipt, builder.getReceipt());
        assertEquals(0, BigDecimal.TEN.compareTo(builder.getMoney().getAmount()));
        assertEquals(category, builder.getCategory());
        assertTrue(builder.isCompleted());
        assertEquals(CurrencyUnit.CAD, builder.getMoney().getCurrencyUnit());
        assertEquals(time, builder.getTime());
        assertEquals(title, builder.getDescription());
        assertEquals(id.toString(), builder.getId());

        Expense expense = builder.build();

        assertEquals(receipt, expense.getReceipt());
        assertEquals(0, BigDecimal.TEN.compareTo(expense.getAmount().getAmount()));
        assertEquals(category, expense.getCategory());
        assertTrue(expense.isCompleted());
        assertEquals(CurrencyUnit.CAD, expense.getAmount().getCurrencyUnit());
        assertEquals(time, expense.getTime());
        assertEquals(title, expense.getDescription());
        assertEquals(id.toString(), expense.getId());

    }

}