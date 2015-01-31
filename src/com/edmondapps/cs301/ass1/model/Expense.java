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

import com.google.gson.InstanceCreator;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public final class Expense implements Comparable<Expense> {
    public static final String CATEGORY_UNCATEGORIZED = "(UNCATEGORIZED)";
    public static final String TITLE_UNSPECIFIED = "(UNSPECIFIED)";

    public static final class Builder {
        public static Builder copyFrom(Expense expense) {
            return new Builder(expense);
        }

        private String mTitle = TITLE_UNSPECIFIED;
        private Money mMoney = Money.zero(CurrencyUnit.USD);
        private String mCategory = CATEGORY_UNCATEGORIZED;
        private long mTime = -1;
        private String mId = UUID.randomUUID().toString();

        public Builder() {
        }

        private Builder(Expense expense) {
            mTitle = expense.mTitle;
            mMoney = expense.mAmount;
            mCategory = expense.mCategory;
            mTime = expense.mTime;
            mId = expense.mId;
        }

        public Builder money(Money money) {
            ClaimUtils.nonNullOrThrow(money, "money");
            mMoney = money;
            return this;
        }

        public Builder amountInBigDecimal(BigDecimal amount) {
            ClaimUtils.nonNullOrThrow(amount, "amount");
            mMoney = mMoney.withAmount(amount, RoundingMode.UP);
            return this;
        }

        public Builder currencyUnit(CurrencyUnit unit) {
            ClaimUtils.nonNullOrThrow(unit, "unit");
            mMoney = mMoney.withCurrencyUnit(unit, RoundingMode.UP);
            return this;
        }

        public Builder title(String title) {
            mTitle = title == null || title.trim().isEmpty() ? TITLE_UNSPECIFIED : title;
            return this;
        }

        public Builder time(long time) {
            ClaimUtils.nonNegativeOrThrow(time, "time");
            mTime = time;
            return this;
        }

        public Builder category(String category) {
            mCategory = category == null || category.trim().isEmpty() ? CATEGORY_UNCATEGORIZED : category;
            return this;
        }

        public Builder id(String id) {
            ClaimUtils.nonNullnonEmptyOrThrow(id, "id");
            mId = id;
            return this;
        }

        public boolean isTitleSet() {
            return !TITLE_UNSPECIFIED.equals(mTitle);
        }

        public boolean isCategorySet() {
            return !CATEGORY_UNCATEGORIZED.equals(mCategory);
        }

        public boolean isTimeSet() {
            return mTime != -1;
        }

        public String getTitle() {
            return mTitle;
        }

        public Money getMoney() {
            return mMoney;
        }

        public String getCategory() {
            return mCategory;
        }

        public long getTime() {
            return mTime;
        }

        public String getId() {
            return mId;
        }

        public Expense build() {
            if (mTime == -1) {
                mTime = System.currentTimeMillis();
            }
            return new Expense(this);
        }
    }

    private static final InstanceCreator<Expense> INSTANCE_CREATOR = new InstanceCreator<Expense>() {
        @Override
        public Expense createInstance(Type type) {
            return new Expense.Builder().money(Money.zero(CurrencyUnit.USD)).build();
        }
    };

    public static InstanceCreator<Expense> getInstanceCreator() {
        return INSTANCE_CREATOR;
    }

    private final String mTitle;
    private final Money mAmount;
    private final String mCategory;
    private final long mTime;
    private final String mId;

    private Expense(Builder b) {
        mTitle = b.mTitle.trim();
        mAmount = b.mMoney;
        mCategory = b.mCategory.trim();
        mTime = b.mTime;
        mId = b.mId.trim();
    }

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

    public Money getAmount() {
        return mAmount;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getCategory() {
        return mCategory;
    }

    public long getTime() {
        return mTime;
    }

    public String getId() {
        return mId;
    }
}
