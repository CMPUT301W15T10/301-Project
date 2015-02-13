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

/**
 * Class that contains a set of {@link com.edmondapps.cs301.ass1.model.Expense Expenses}, and details of a trip. <br/>
 * This is an immutable class. <br/>
 * Use {@link com.edmondapps.cs301.ass1.model.Claim.Builder Claim.Builder} to obtain an instance.
 */
// Effective Java Item 15, 17
public final class Claim {

    /**
     * The unspecified title.
     */
    public static final String TITLE_UNNAMED = "(UNNAMED)";

    public enum Status {
        IN_PROGRESS(true), SUBMITTED(false), RETURNED(true), APPROVED(false);

        private final boolean mAllowEdits;

        Status(boolean allowEdits) {
            mAllowEdits = allowEdits;
        }

        /**
         * @return if the {@code Status} allows editing
         */
        public boolean getAllowEdits() {
            return mAllowEdits;
        }
    }

    /**
     * Use this class to obtain instances of {@link com.edmondapps.cs301.ass1.model.Claim Claim}.
     */
    // Effective Java Item 2
    public static final class Builder {

        /**
         * Creates a {@code Builder} instance with the given {@code Claim}.
         *
         * @param claim non-null instance of {@code Claim}
         * @return an instance of {@code Builder}
         */
        // Effective Java Item 1
        public static Builder copyFrom(Claim claim) {
            return new Builder(claim);
        }

        // default values
        private Set<Expense> mExpenses = new TreeSet<Expense>();
        private Map<String, String> mDestinations = new HashMap<String, String>();  // Destination -> Reason
        private String mTitle = TITLE_UNNAMED;
        private long mStartTime = -1;
        private long mEndTime = -1;
        private String mId = UUID.randomUUID().toString();
        private Status mStatus = Status.IN_PROGRESS;

        /**
         * Creates an instance of {@code Builder} with the default values.
         */
        public Builder() {
        }

        /**
         * Creates an instance of {@code Builder} with the given {@code Claim}.
         *
         * @param claim non-null instance of {@code Claim}
         */
        private Builder(Claim claim) {
            mExpenses.addAll(claim.mExpenses);
            mTitle = claim.mTitle;
            mStartTime = claim.mStartTime;
            mEndTime = claim.mEndTime;
            mId = claim.mId;
            mStatus = claim.mStatus;
        }

        /**
         * Updates the {@link com.edmondapps.cs301.ass1.model.Expense Expense} if there is already an {@code Expense}
         * with the same {@link com.edmondapps.cs301.ass1.model.Expense#getId() id}.
         * <br/>
         * Adds the {@code Expense} otherwise.
         *
         * @param expense non-null instance of {@code Expense}
         * @return this instance of {@code Builder}
         * @see #addExpense(Expense)
         */
        public Builder putExpense(Expense expense) {
            ClaimUtils.nonNullOrThrow(expense, "expense");
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

        /**
         * Adds an {@link com.edmondapps.cs301.ass1.model.Expense Expense} to the underlying {@code Set}. Equality
         * is determined by {@link Expense#compareTo(Expense)}.
         *
         * @param expense non-null instance of {@code Expense}
         * @return this instance of {@code Builder}
         * @see #putExpense(Expense)
         */
        public Builder addExpense(Expense expense) {
            ClaimUtils.nonNullOrThrow(expense, "expense");
            mExpenses.add(expense);
            return this;
        }

        /**
         * Puts the destination and the associated reason to the underlying {@code Map}.
         *
         * @param destination the {@code String} destination
         * @param reason      the {@code String} associated reason
         * @return this instance of {@code Builder}
         */
        public Builder putDestinationAndReason(String destination, String reason) {
            ClaimUtils.nonNullnonEmptyOrThrow(destination, "destination");
            ClaimUtils.nonNullnonEmptyOrThrow(reason, "reason");
            mDestinations.put(destination, reason);
            return this;
        }

        /**
         * Specifies the title of the {@code Claim}.
         * <br/>
         * Defaults to {@link Claim#TITLE_UNNAMED} if the title is null or (trimmed) empty
         *
         * @param title nullable {@code String} title
         * @return this instance of {@code Builder}
         * @see #getTitle()
         */
        public Builder title(String title) {
            mTitle = title == null || title.trim().isEmpty() ? TITLE_UNNAMED : title;
            return this;
        }

        /**
         * Specifies the start time of the {@code Claim}.
         * <br/>
         * If {@link #isEndTimeSet()}, then the start time must be larger or equals to the end time.
         *
         * @param startTime non-negative time; and >= end time if {@link #isEndTimeSet()}
         * @return this instance of {@code Builder}
         * @see #getStartTime()
         */
        public Builder startTime(long startTime) {
            ClaimUtils.nonNegativeOrThrow(startTime, "startName");
            if (isEndTimeSet() && mEndTime < startTime) {
                throw new IllegalArgumentException(
                        "end time cannot be earlier than start time; start: " + startTime + " end: " + mEndTime);
            }
            mStartTime = startTime;
            return this;
        }

        /**
         * Specifies the end time of the {@code Claim}.
         * <br/>
         * If {@link #isStartTimeSet()}, then the end time must be smaller or equals to the end time.
         *
         * @param endTime non-negative time; and <= start time if {@link #isStartTimeSet()}
         * @return this instance of {@code Builder}
         * @see #getEndTime()
         */
        public Builder endTime(long endTime) {
            ClaimUtils.nonNegativeOrThrow(endTime, "endName");
            if (isStartTimeSet() && mStartTime > endTime) {
                throw new IllegalArgumentException(
                        "start time cannot be later than end time; start: " + mStartTime + " end: " + endTime);
            }
            mEndTime = endTime;
            return this;
        }

        /**
         * Specifies the id of the {@code Claim}.
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
         * Specifies the status of the {@code Claim}.
         *
         * @param status non-null instance of {@code Status}
         * @return this instance of {@code Builder}
         * @see #getStatus()
         */
        public Builder status(Status status) {
            ClaimUtils.nonNullOrThrow(status, "status");
            mStatus = status;
            return this;
        }

        /**
         * Peeks at the set of {@link com.edmondapps.cs301.ass1.model.Expense Expenses}.
         *
         * @return an unmodifiable set of {@code Expenses}
         */
        public Set<Expense> peekExpenses() {
            return Collections.unmodifiableSet(mExpenses);
        }

        /**
         * @return the title specified by {@link #title(String)} if {@link #isStartTimeSet()};
         * otherwise, {@link Claim#TITLE_UNNAMED}; never null
         * @see #isTitleSet()
         */
        public String getTitle() {
            return mTitle;
        }

        /**
         * @return the id specified by {@link #id(String)},
         * or an id generated by {@link java.util.UUID UUID} at the instantiation of this instance
         */
        public String getId() {
            return mId;
        }

        /**
         * @return the start time specified by {@link #startTime(long)} if {@link #isStartTimeSet()}; otherwise, undefined
         * @see #isStartTimeSet()
         */
        public long getStartTime() {
            return mStartTime;
        }


        /**
         * @return the start time specified by {@link #endTime(long)} if {@link #isEndTimeSet()}; otherwise, undefined
         * @see #isEndTimeSet()
         */
        public long getEndTime() {
            return mEndTime;
        }

        /**
         * @return the status specified by {@link #status(com.edmondapps.cs301.ass1.model.Claim.Status)};
         * otherwise, {@link Status#IN_PROGRESS}; never null
         */
        public Status getStatus() {
            return mStatus;
        }

        /**
         * @return if the title is {@link Claim#TITLE_UNNAMED}
         * @see #getTitle()
         */
        public boolean isTitleSet() {
            return !TITLE_UNNAMED.equals(mTitle);
        }

        /**
         * @return if the start time is not specified by {@link #startTime(long)}
         * @see #getStartTime()
         */
        public boolean isStartTimeSet() {
            return mStartTime != -1;
        }

        /**
         * @return if the end time is not specified by {@link #endTime(long)}
         * @see #getEndTime()
         */
        public boolean isEndTimeSet() {
            return mEndTime != -1;
        }

        /**
         * Creates an instance of {@code Claim}.
         *
         * @return an instance of {@code Claim}; never null
         */
        public Claim build() {
            return new Claim(this);
        }
    }

    /**
     * You must use this to generate an instance of {@code Claim} for {@code Gson}.
     *
     * @return an {@code InstanceCreator} for {@code Gson}
     */
    public static InstanceCreator<Claim> getInstanceCreator() {
        return INSTANCE_CREATOR;
    }

    // final fields will be set by Gson via reflection
    private static final InstanceCreator<Claim> INSTANCE_CREATOR = new InstanceCreator<Claim>() {
        @Override
        public Claim createInstance(Type type) {
            return new Claim.Builder().build();
        }
    };

    private final Set<Expense> mExpenses;
    private final Map<String, String> mDestinations = new HashMap<String, String>();  // destination -> reason
    private final String mTitle;
    private final long mStartTime;
    private final long mEndTime;
    private final String mId;
    private final Status mStatus;

    // Effective Java Item 2
    private Claim(Builder b) {
        mExpenses = b.mExpenses;
        mDestinations.putAll(b.mDestinations);
        mTitle = b.mTitle;
        mStartTime = b.mStartTime;
        mEndTime = b.mEndTime;
        mId = b.mId;
        mStatus = b.mStatus;
    }

    /**
     * Peeks at the set of {@link com.edmondapps.cs301.ass1.model.Expense Expenses}.
     *
     * @return an unmodifiable set of {@code Expenses}
     */
    public Set<Expense> peekExpenses() {
        return Collections.unmodifiableSet(mExpenses);
    }

    /**
     * Peeks at the map of {@code destination -> reason}.
     *
     * @return an unmodifiable map of {@code destination -> reason}
     */
    public Map<String, String> peekDestinations() {
        return Collections.unmodifiableMap(mDestinations);
    }

    /**
     * @return the title; never null
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * @return the start time; always >= {@link #getEndTime()} and positive
     * @see #getEndTime()
     */
    public long getStartTime() {
        return mStartTime;
    }

    /**
     * @return the end time; always <= {@link #getStartTime()} and positive
     */
    public long getEndTime() {
        return mEndTime;
    }

    /**
     * @return the {@code String} id; never null
     */
    public String getId() {
        return mId;
    }

    /**
     * @return the status; never null
     */
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
