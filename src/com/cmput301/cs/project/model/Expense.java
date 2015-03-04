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

package com.cmput301.cs.project.model;

import com.google.gson.InstanceCreator;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Class that contains the details of an expense. <br/>
 * This is an immutable class. <br/>
 * Use {@link com.cmput301.cs.project.model.Expense.Builder Expense.Builder} to obtain an instance.
 */
// Effective Java Item 15, 17
public final class Expense implements Comparable<Expense> {
    /**
     * The unspecified category
     */
    public static final String CATEGORY_UNCATEGORIZED = "(UNCATEGORIZED)";

    /**
     * The unspecified title
     */
    public static final String TITLE_UNSPECIFIED = "(UNSPECIFIED)";

    /**
     * The default {@link Money}, with the amount of zero in USD.
     */
    public static final Money DEFAULT_MONEY = Money.zero(CurrencyUnit.USD);

    /**
     * Use this class to obtain instances of {@link com.cmput301.cs.project.model.Expense Expense}.
     */
    // Effective Java Item 2
    public static final class Builder {
        private Receipt receipt;

        /**
         * Creates a {@code Builder} instance with the given {@code Expense}.
         *
         * @param expense non-null instance of {@code Expense}
         * @return an instance of {@code Builder}
         */
        public static Builder copyFrom(Expense expense) {
            return new Builder(expense);
        }

        // default values
        private String mTitle = TITLE_UNSPECIFIED;
        private Money mMoney = DEFAULT_MONEY;
        private String mCategory = CATEGORY_UNCATEGORIZED;
        private long mTime = -1;
        private String mId = UUID.randomUUID().toString();
        private boolean mCompleted = false;
        private Receipt mReceipt;

        /**
         * Creates an instance of {@code Builder} with the default values.
         */
        public Builder() {
        }

        /**
         * Creates a {@code Builder} instance with the given {@code Expense}.
         *
         * @param expense non-null instance of {@code Expense}
         */
        private Builder(Expense expense) {
            mTitle = expense.getTitle();
            mMoney = expense.getAmount();
            mCategory = expense.getCategory();
            mTime = expense.getTime();
            mId = expense.getId();
            mCompleted = expense.isCompleted();
            mReceipt = expense.getReceipt();
        }

        /**
         * Specifies the money of the {@code Expense}.
         *
         * @param money non-null instance of {@link org.joda.money.Money Money}
         * @return this instance of {@code Builder}
         * @see #getMoney()
         */
        public Builder money(Money money) {
            ClaimUtils.nonNullOrThrow(money, "money");
            mMoney = money;
            return this;
        }

        /**
         * Specifies the amount, keeping the currency.
         *
         * @param amount non-null instance of {@link java.math.BigDecimal BigDecimal}
         * @return this instance of {@code Builder}
         * @see Expense#DEFAULT_MONEY
         * @see #currencyUnit(org.joda.money.CurrencyUnit)
         */
        public Builder amountInBigDecimal(BigDecimal amount) {
            ClaimUtils.nonNullOrThrow(amount, "amount");
            mMoney = mMoney.withAmount(amount, RoundingMode.UP);
            return this;
        }

        /**
         * Specifies the unit, keeping the amount.
         *
         * @param unit non-null instance of {@link org.joda.money.CurrencyUnit CurrencyUnit}
         * @return this instance of {@code Builder}
         * @see Expense#DEFAULT_MONEY
         * @see #amountInBigDecimal(java.math.BigDecimal)
         */
        public Builder currencyUnit(CurrencyUnit unit) {
            ClaimUtils.nonNullOrThrow(unit, "unit");
            mMoney = mMoney.withCurrencyUnit(unit, RoundingMode.UP);
            return this;
        }

        /**
         * Specifies the title of the {@code Claim}.
         * <br/>
         * Defaults to {@link Expense#TITLE_UNSPECIFIED} if the title is null or (trimmed) empty
         *
         * @param title nullable {@code String} title
         * @return this instance of {@code Builder}
         * @see #getTitle()
         * @see #isTitleSet()
         */
        public Builder title(String title) {
            mTitle = title == null || title.trim().isEmpty() ? TITLE_UNSPECIFIED : title;
            return this;
        }

        /**
         * Specifies the time of the {@code Expense}.
         *
         * @param time non-negative time
         * @return this instance of {@code Builder}
         * @see #getTime()
         * @see #isTimeSet()
         */
        public Builder time(long time) {
            ClaimUtils.nonNegativeOrThrow(time, "time");
            mTime = time;
            return this;
        }

        /**
         * Specifies the category of the {@code Expense}.
         * <br/>
         * Defaults to {@link Expense#CATEGORY_UNCATEGORIZED} if the category is null or (trimmed) empty
         *
         * @param category nullable {@code String} category
         * @return this instance of {@code Builder}
         * @see #getCategory()
         * @see #isCategorySet()
         */
        public Builder category(String category) {
            mCategory = category == null || category.trim().isEmpty() ? CATEGORY_UNCATEGORIZED : category;
            return this;
        }

        /**
         * Specifies the id of the {@code Expense}.
         *
         * @param id non-null {@code String} id
         * @return this instance of {@code Builder}
         * @see #getId()
         */
        public Builder id(String id) {
            ClaimUtils.nonNullnonEmptyOrThrow(id, "id");
            mId = id;
            return this;
        }

        /**
         * Specifies if the {@code Expense} is completed.
         *
         * @param completed the new completed status
         * @return this instance of {@code Builder}
         */
        public Builder completed(boolean completed) {
            mCompleted = completed;
            return this;
        }

        public Builder receipt(Receipt receipt) {
            mReceipt = receipt;
            return this;
        }

        /**
         * @return if the title is {@link Expense#TITLE_UNSPECIFIED}
         * @see #getTitle()
         */
        public boolean isTitleSet() {
            return !TITLE_UNSPECIFIED.equals(mTitle);
        }

        /**
         * @return if the category is {@link Expense#CATEGORY_UNCATEGORIZED}
         * @see #getCategory()
         */
        public boolean isCategorySet() {
            return !CATEGORY_UNCATEGORIZED.equals(mCategory);
        }

        /**
         * @return if the time set by {@link #time(long)}
         * @see #getTime()
         */ 
        public boolean isTimeSet() {
            return mTime != -1;
        }

        /**
         * @return the title specified by {@link #title(String)} if {@link #isTitleSet()};
         * otherwise, {@link Expense#TITLE_UNSPECIFIED}; never null
         * @see #isTitleSet()
         */
        public String getTitle() {
            return mTitle;
        }

        /**
         * @return the money specified by the {@link #money(org.joda.money.Money)};
         * otherwise, {@link Expense#DEFAULT_MONEY}; never null
         */
        public Money getMoney() {
            return mMoney;
        }

        /**
         * @return the category specified by {@link #category(String)} if {@link #isCategorySet()};
         * otherwise, {@link Expense#CATEGORY_UNCATEGORIZED}; never null
         * @see #isCategorySet()
         */
        public String getCategory() {
            return mCategory;
        }

        /**
         * @return the time specified by {@link #time(long)} if {@link #isTimeSet()}; otherwise, undefined
         */
        public long getTime() {
            return mTime;
        }

        /**
         * @return the id specified by {@link #id(String)},
         * or an id generated by {@link java.util.UUID UUID} at the instantiation of this instance
         */
        public String getId() {
            return mId;
        }

        /**
         * @return if the {@code Expense} is completed
         */
        public boolean isCompleted() {
            return mCompleted;
        }

        /**
         * Creates an instance of {@code Expense}.
         * <br/>
         * If {@link #isTimeSet()} is false, time will be set to {@link System#currentTimeMillis()}.
         *
         * @return an instance of {@coded Expense}; never null
         */
        public Expense build() {
            if (!isTimeSet()) {
                time(System.currentTimeMillis());
            }
            return new Expense(this);
        }

        public Receipt getReceipt() {
            return mReceipt;
        }
    }

    /**
     * You must use this to generate an instance of {@code Expense} for {@code Gson}.
     *
     * @return an {@code InstanceCreator} for {@code Gson}
     */
    public static InstanceCreator<Expense> getInstanceCreator() {
        return INSTANCE_CREATOR;
    }

    // final fields will be set by Gson via reflection
    private static final InstanceCreator<Expense> INSTANCE_CREATOR = new InstanceCreator<Expense>() {
        @Override
        public Expense createInstance(Type type) {
            return new Expense.Builder().build();
        }
    };

    private final String mTitle;
    private final Money mAmount;
    private final String mCategory;
    private final long mTime;
    private final String mId;
    private final boolean mCompleted;
    private final Receipt mReceipt;

    // Effective Java Item 2
    private Expense(Builder b) {
        mTitle = b.mTitle.trim();
        mAmount = b.mMoney;
        mCategory = b.mCategory.trim();
        mTime = b.mTime;
        mId = b.mId.trim();
        mCompleted = b.mCompleted;
        mReceipt = b.mReceipt;
    }

    /**
     * Compares this and another instance of {@code Expense} in the following order:
     * <ol>
     * <li>{@link #getTime() time}</li>
     * <li>{@link #getTitle() title}</li>
     * <li>{@link #getAmount() money}</li>
     * <li>{@link #getCategory() category}</li>
     * <li>{@link #getId() id}</li>
     * </ol>
     * This method is consistent with {@link #equals(Object)}: if this method returns {@code 0},
     * {@code equals(Object)} returns {@code true}, as defined in <i>Effective Java</i> Item 12.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Expense o) {
        if (mTime < o.mTime) return -1;
        if (mTime > o.mTime) return 1;

        final int titleDiff = mTitle.compareToIgnoreCase(o.mTitle);
        if (titleDiff != 0) return titleDiff;

        final int amountDiff = mAmount.compareTo(o.mAmount);
        if (amountDiff != 0) return amountDiff;

        final int categoryDiff = mCategory.compareToIgnoreCase(o.mCategory);
        if (categoryDiff != 0) return categoryDiff;

        final int idDiff = mId.compareToIgnoreCase(o.mId);
        if (idDiff != 0) return idDiff;

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expense)) return false;

        final Expense expense = (Expense) o;

        if (mTime != expense.mTime) return false;
        if (mAmount != null ? !mAmount.equals(expense.mAmount) : expense.mAmount != null) return false;
        if (mCategory != null ? !mCategory.equals(expense.mCategory) : expense.mCategory != null) return false;
        if (mId != null ? !mId.equals(expense.mId) : expense.mId != null) return false;
        if (mTitle != null ? !mTitle.equals(expense.mTitle) : expense.mTitle != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mTitle != null ? mTitle.hashCode() : 0;
        result = 31 * result + (mAmount != null ? mAmount.hashCode() : 0);
        result = 31 * result + (mCategory != null ? mCategory.hashCode() : 0);
        result = 31 * result + (int) (mTime ^ (mTime >>> 32));
        result = 31 * result + (mId != null ? mId.hashCode() : 0);
        return result;
    }

    /**
     * @return the amount; never null
     */
    public Money getAmount() {
        return mAmount;
    }

    /**
     * @return the title; never null
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return the category; never null
     */
    public String getCategory() {
        return mCategory;
    }

    /**
     * @return the time; always positive
     */
    public long getTime() {
        return mTime;
    }

    /**
     * @return the {@code String} id; never null
     */
    public String getId() {
        return mId;
    }

    /**
     * @return if the {@code Expense} is marked as completed
     */
    public boolean isCompleted() {
        return mCompleted;
    }

    public Receipt getReceipt() {
        return mReceipt;
    }
}
