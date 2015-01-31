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

import java.lang.reflect.Type;
import java.util.*;

public final class Claim {

    public enum Status {
        IN_PROGRESS(true), SUBMITTED(false), RETURNED(true), APPROVED(false);

        private final boolean mAllowEdits;

        Status(boolean allowEdits) {
            mAllowEdits = allowEdits;
        }

        public boolean doesAllowEdits() {
            return mAllowEdits;
        }
    }

    public static final class Builder {

        public static Builder copyFrom(Claim claim) {
            return new Builder(claim);
        }

        private Set<Expense> mExpenses = new TreeSet<Expense>();
        private String mTitle = TITLE_UNNAMED;
        private long mStartTime = -1;
        private long mEndTime = -1;
        private String mId = UUID.randomUUID().toString();
        private Status mStatus = Status.IN_PROGRESS;

        public Builder() {
        }

        private Builder(Claim claim) {
            mExpenses.addAll(claim.mExpenses);
            mTitle = claim.mTitle;
            mStartTime = claim.mStartTime;
            mEndTime = claim.mEndTime;
            mId = claim.mId;
            mStatus = claim.mStatus;
        }

        public Builder putExpense(Expense expense) {
            for (Iterator<Expense> iterator = mExpenses.iterator(); iterator.hasNext(); ) {
                final Expense e = iterator.next();
                if (e.getId().equals(expense.getId())) {
                    iterator.remove();
                    mExpenses.add(expense);
                    return this;
                }
            }
            addExpense(expense);
            return this;
        }

        public Builder addExpense(Expense expense) {
            mExpenses.add(expense);
            return this;
        }

        public Builder removeExpense(Expense expense) {
            mExpenses.remove(expense);
            return this;
        }

        public Builder title(String title) {
            mTitle = title == null || title.trim().isEmpty() ? TITLE_UNNAMED : title;
            return this;
        }

        public Builder startTime(long startTime) {
            ClaimUtils.nonNegativeOrThrow(startTime, "startName");
            if (isEndTimeSet() && mEndTime < startTime) {
                throw new IllegalArgumentException(
                        "end time cannot be earlier than start time; start: " + startTime + " end: " + mEndTime);
            }
            mStartTime = startTime;
            return this;
        }

        public Builder endTime(long endTime) {
            ClaimUtils.nonNegativeOrThrow(endTime, "endName");
            if (isStartTimeSet() && mStartTime > endTime) {
                throw new IllegalArgumentException(
                        "start time cannot be later than end time; start: " + mStartTime + " end: " + endTime);
            }
            mEndTime = endTime;
            return this;
        }

        public Builder id(String id) {
            ClaimUtils.nonNullnonEmptyOrThrow(id, "id");
            mId = id;
            return this;
        }

        public Builder status(Status status) {
            ClaimUtils.nonNullOrThrow(status, "status");
            mStatus = status;
            return this;
        }

        public Set<Expense> peekExpenses() {
            return Collections.unmodifiableSet(mExpenses);
        }

        public String getTitle() {
            return mTitle;
        }

        public long getStartTime() {
            return mStartTime;
        }

        public long getEndTime() {
            return mEndTime;
        }

        public Status getStatus() {
            return mStatus;
        }

        public boolean isTitleSet() {
            return !mTitle.equals(TITLE_UNNAMED);
        }

        public boolean isStartTimeSet() {
            return mStartTime != -1;
        }

        public boolean isEndTimeSet() {
            return mEndTime != -1;
        }

        public Claim build() {
            return new Claim(this);
        }
    }

    public static final String TITLE_UNNAMED = "(UNNAMED)";

    public static InstanceCreator<Claim> getInstanceCreator() {
        return INSTANCE_CREATOR;
    }

    private static final InstanceCreator<Claim> INSTANCE_CREATOR = new InstanceCreator<Claim>() {
        @Override
        public Claim createInstance(Type type) {
            return new Claim.Builder().build();
        }
    };

    private final Set<Expense> mExpenses;
    private final String mTitle;
    private final long mStartTime;
    private final long mEndTime;
    private final String mId;
    private final Status mStatus;

    public Claim(Builder b) {
        mExpenses = b.mExpenses;
        mTitle = b.mTitle;
        mStartTime = b.mStartTime;
        mEndTime = b.mEndTime;
        mId = b.mId;
        mStatus = b.mStatus;
    }

    public Set<Expense> peekExpenses() {
        return Collections.unmodifiableSet(mExpenses);
    }

    public String getTitle() {
        return mTitle;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public String getId() {
        return mId;
    }

    public Status getStatus() {
        return mStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Claim)) return false;

        final Claim claim = (Claim) o;

        if (mEndTime != claim.mEndTime) return false;
        if (mStartTime != claim.mStartTime) return false;
        if (mExpenses != null ? !mExpenses.equals(claim.mExpenses) : claim.mExpenses != null) return false;
        if (mId != null ? !mId.equals(claim.mId) : claim.mId != null) return false;
        if (mStatus != claim.mStatus) return false;
        if (mTitle != null ? !mTitle.equals(claim.mTitle) : claim.mTitle != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mExpenses != null ? mExpenses.hashCode() : 0;
        result = 31 * result + (mTitle != null ? mTitle.hashCode() : 0);
        result = 31 * result + (int) (mStartTime ^ (mStartTime >>> 32));
        result = 31 * result + (int) (mEndTime ^ (mEndTime >>> 32));
        result = 31 * result + (mId != null ? mId.hashCode() : 0);
        result = 31 * result + (mStatus != null ? mStatus.hashCode() : 0);
        return result;
    }
}
