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

package com.cmput301.cs.project.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.InstanceCreator;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Class that contains the details of an expense. <p>
 * This is an immutable class. <p>
 * See {@link com.cmput301.cs.project.models.Expense.Builder Expense.Builder} on how to obtain an instance.
 */
// Effective Java Item 15, 17
public final class Expense implements Comparable<Expense>, Parcelable {

    public static final Comparator<? super Expense> OCCURRED_DESCENDING = new Comparator<Expense>() {
        @Override
        public int compare(Expense lhs, Expense rhs) {
            return ((Long) lhs.getTimeOccurred()).compareTo(rhs.getTimeOccurred());
        }
    };


    public static final Set<String> CATEGORIES = Collections.unmodifiableSet(new TreeSet<String>() {{
        add("Accomodation");
        add("Air Fare");
        add("Ground Transport");
        add("Vehicle Rental");
        add("Fuel");
        add("Parking");
        add("Registration");
        add("Meal");
        add("Others");
    }});

    public static final Set<CurrencyUnit> CURRENCIES = Collections.unmodifiableSet(new TreeSet<CurrencyUnit>() {{
        add(CurrencyUnit.GBP);
        add(CurrencyUnit.CAD);
        add(CurrencyUnit.EUR);
        add(CurrencyUnit.USD);
        add(CurrencyUnit.CHF);
        add(CurrencyUnit.JPY);
        add(CurrencyUnit.getInstance("CNY"));

    }});


    /**
     * The default {@link Money}, with the amount of zero in USD.
     */
    private static final Money DEFAULT_MONEY = Money.zero(CurrencyUnit.USD);
    private long mTimeOccurred;

    public long getTimeOccurred() {
        return mTimeOccurred;
    }

    /**
     * Use this class to obtain instances of {@link com.cmput301.cs.project.models.Expense Expense}.
     * <p>
     * Creating an Expense:
     * <pre>
     * Expense expense = new Expense.Builder()
     *                       .amount(BigDecimal.TEN)
     *                       .currencyUnit(CurrencyUnit.CAD)
     *                       …
     *                       .build();
     * </pre>
     * <p>
     * Editing an Expense:
     * <pre>
     * Expense oldExpense = …;
     * Expense expense = oldExpense.edit()
     *                             .amount(BigDecimal.TEN)
     *                             .currencyUnit(CurrencyUnit.CAD)
     *                             …
     *                             .build();
     * </pre>
     */
    // Effective Java Item 2
    public static final class Builder {
        // default values
        private String mDescription = null;
        private Money mMoney = DEFAULT_MONEY;
        private String mCategory = null;
        private long mTime = -1;
        private String mId = UUID.randomUUID().toString();
        private boolean mCompleted = false;
        private Receipt mReceipt;
        private long mTimeOccurred = System.currentTimeMillis();

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
            mDescription = expense.getDescription();
            mMoney = expense.getAmount();
            mCategory = expense.getCategory();
            mTime = expense.getTime();
            mId = expense.getId();
            mCompleted = expense.isCompleted();
            mReceipt = expense.getReceipt();
            mTimeOccurred = expense.getTimeOccurred();
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
        public Builder amount(BigDecimal amount) {
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
         * @see #amount(java.math.BigDecimal)
         */
        public Builder currencyUnit(CurrencyUnit unit) {
            ClaimUtils.nonNullOrThrow(unit, "unit");

            if (!CURRENCIES.contains(unit)) {
                throw new IllegalArgumentException(unit + "is not a valid currency");
            }

            mMoney = mMoney.withCurrencyUnit(unit, RoundingMode.UP);
            return this;
        }

        /**
         * Specifies the description of the {@code Claim}.
         * <p>
         *
         * @param description nullable {@code String} description
         * @return this instance of {@code Builder}
         * @see #getDescription()
         * @see #isDescriptionSet()
         */
        public Builder description(String description) {
            mDescription = description;
            return this;
        }

        /**
         * Specifies the time of the {@code Expense}. Negative time will be dropped.
         *
         * @param time non-negative time; otherwise no-op
         * @return this instance of {@code Builder}
         * @see #getTime()
         * @see #isTimeSet()
         */
        public Builder time(long time) {
            if (time < 0) return this;
            mTime = time;
            return this;
        }

        /**
         * Specifies the category of the {@code Expense}.
         * <p>
         *
         * @param category nullable {@code String} category
         * @return this instance of {@code Builder}
         * @see #getCategory()
         * @see #isCategorySet()
         */

        public Builder category(String category) {
            ClaimUtils.nonNullnonEmptyOrThrow(category, "category");

            if (!CATEGORIES.contains(category)) {
                throw new IllegalArgumentException(category + "is not a valid category");
            }

            mCategory = category;
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
         * @return if the description is not null
         * @see #getDescription()
         */
        public boolean isDescriptionSet() {
            return mDescription != null;
        }

        /**
         * @return if the category is not null
         * @see #getCategory()
         */
        public boolean isCategorySet() {
            return mCategory != null;
        }

        /**
         * @return if the time set by {@link #time(long)}
         * @see #getTime()
         */
        public boolean isTimeSet() {
            return mTime != -1;
        }

        /**
         * @return the description specified by {@link #description(String)}
         * @see #isDescriptionSet()
         */
        public String getDescription() {
            return mDescription;
        }

        /**
         * @return the money specified by the {@link #money(org.joda.money.Money)};
         * otherwise, {@link Expense#DEFAULT_MONEY}; never null
         */
        public Money getMoney() {
            return mMoney;
        }

        /**
         * @return the category specified by {@link #category(String)}
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
         * <p>
         * If {@link #isTimeSet()} is false, time will be set to {@link System#currentTimeMillis()}.
         *
         * @return an instance of {@code Expense}; never null
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

        /**
         * @return if the {@code Receipt} exists
         */
        public boolean hasReceipt() {
            return mReceipt != null;
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

    private final String mDescription;
    private final Money mAmount;
    private final String mCategory;
    private final long mTime;
    private final String mId;
    private final boolean mCompleted;
    private final Receipt mReceipt;

    // Effective Java Item 2
    private Expense(Builder b) {
        mDescription = b.mDescription;
        mAmount = b.mMoney;
        mCategory = b.mCategory;
        mTime = b.mTime;
        mId = b.mId.trim();
        mCompleted = b.mCompleted;
        mReceipt = b.mReceipt;
        mTimeOccurred = b.mTimeOccurred;
    }

    /**
     * Creates a {@code Builder} instance with the given {@code Expense}.
     *
     * @return an instance of {@code Builder}
     */
    public Expense.Builder edit() {
        return new Builder(this);
    }

    /**
     * Compares this and another instance of {@code Expense} in the following order:
     * <ol>
     * <li>{@link #getTimeOccurred() timeOccured}</li>
     * <li>{@link #getTime() time}</li>
     * <li>{@link #getDescription() description}</li>
     * <li>{@link #getAmount() money}</li>
     * <li>{@link #getCategory() category}</li>
     * <li>{@link #getId() id}</li>
     * </ol>
     * This method is consistent with {@link #equals(Object)}: if this method returns {@code 0},
     * {@code equals(Object)} returns {@code true}, as defined in <i>Effective Java</i> Item 12.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Expense o) {
        if (mTimeOccurred < o.mTimeOccurred) return -1;
        if (mTimeOccurred > o.mTimeOccurred) return 1;
        
        if (mTime < o.mTime) return -1;
        if (mTime > o.mTime) return 1;

        final int descriptionDiff = mDescription.compareToIgnoreCase(o.mDescription);
        if (descriptionDiff != 0) return descriptionDiff;

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

        if (mTimeOccurred != expense.mTimeOccurred) return false;
        if (mTime != expense.mTime) return false;
        if (mCompleted != expense.mCompleted) return false;


        // Made more difficult by them possibly being null
        // If null, check if other is not null, otherwise check if equal
        if (mDescription == null ? 
                expense.mDescription != null : 
                !mDescription.equals(expense.mDescription))
            return false;
        if (mAmount == null ? 
                expense.mAmount != null : 
                !mAmount.equals(expense.mAmount))
            return false;
        if (mCategory == null ? 
                expense.mCategory != null : 
                !mCategory.equals(expense.mCategory))
            return false;
        if (mId == null ? 
                expense.mId != null : 
                !mId.equals(expense.mId))
            return false;

        // Want the others receipt to be in the same state
        return mReceipt == null ? 
                expense.mReceipt == null : 
                mReceipt.equals(expense.mReceipt);
    }

    @Override
    public int hashCode() {
        int result = (int) (mTimeOccurred ^ (mTimeOccurred >>> 32));
        result = 31 * result + ((mDescription != null) ? mDescription.hashCode() : 0);
        result = 31 * result + ((mAmount != null) ? mAmount.hashCode() : 0);
        result = 31 * result + ((mCategory != null) ? mCategory.hashCode() : 0);
        result = 31 * result + (int) (mTime ^ (mTime >>> 32));
        result = 31 * result + ((mId != null) ? mId.hashCode() : 0);
        result = 31 * result + (mCompleted ? 1 : 0);
        result = 31 * result + ((mReceipt != null) ? mReceipt.hashCode() : 0);
        return result;
    }

    /**
     * @return the amount; never null
     */
    public Money getAmount() {
        return mAmount;
    }

    /**
     * @return the description; never null
     */
    public String getDescription() {
        return mDescription;
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

    /**
     * @return if it contains a valid {@code Receipt}
     */
    public boolean hasReceipt() {
        return mReceipt != null;
    }

    /**
     * @return the {@code Receipt}; may be null
     */
    public Receipt getReceipt() {
        return mReceipt;
    }

    // generated by http://www.parcelabler.com/
    protected Expense(Parcel in) {
        mDescription = in.readString();
        mAmount = (Money) in.readValue(Money.class.getClassLoader());
        mCategory = in.readString();
        mTime = in.readLong();
        mId = in.readString();
        mCompleted = in.readByte() != 0x00;
        mReceipt = (Receipt) in.readValue(Receipt.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // generated by http://www.parcelabler.com/
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDescription);
        dest.writeValue(mAmount);
        dest.writeString(mCategory);
        dest.writeLong(mTime);
        dest.writeString(mId);
        dest.writeByte((byte) (mCompleted ? 0x01 : 0x00));
        dest.writeValue(mReceipt);
    }

    // generated by http://www.parcelabler.com/
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Expense> CREATOR = new Parcelable.Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };
}
