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

import com.cmput301.cs.project.utils.Utils;
import com.google.gson.InstanceCreator;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Class that contains a set of {@link com.cmput301.cs.project.models.Expense Expenses}, and details of a trip. <p>
 * This is an immutable class. <p>
 * See {@link com.cmput301.cs.project.models.Claim.Builder Claim.Builder} on how to obtain an instance.<p>
 * Claims are the main component of this app and therefore there are references to it in most other areas of the app. The most common uses of the Claim class are:
 * <ul>
 * <li>Creating a new Claim (uses the builder)- 
 * <li>
 * </ul>
 */
// Effective Java Item 15, 17
public final class Claim implements Comparable<Claim>, Saveable {

    /**
     * The unspecified title.
     */

    public static final Comparator<? super Claim> START_DESCENDING = new Comparator<Claim>() {
        @Override
        public int compare(Claim lhs, Claim rhs) {
            return ((Long) lhs.getStartTime()).compareTo(rhs.getStartTime());
        }
    };

    public static final Comparator<? super Claim> START_ASCENDING = new Comparator<Claim>() {
        @Override
        public int compare(Claim lhs, Claim rhs) {
            return ((Long) rhs.getStartTime()).compareTo(lhs.getStartTime());
        }
    };

    private final User mClaimant;
    private final long mModified;
    private boolean mDeleted;

    public boolean isDeleted() {
        return mDeleted;
    }

    public User getClaimant() {
        return mClaimant;
    }

    public boolean canApprove(User user) {
        return !(mClaimant.equals(user) || mStatus != Status.SUBMITTED);

    }

    public boolean isEditable() {
        return mStatus.getAllowEdits();
    }

    //http://stackoverflow.com/a/669165/1036813 March 17 2015 blaine1

    public String getTagsAsString() {
        final StringBuilder sb = new StringBuilder();

        String delimiter = "";
        for (Tag tag : mTags) {
            sb.append(delimiter).append(tag.getName());
            delimiter = ", ";
        }

        return sb.toString();
    }

    //http://stackoverflow.com/a/669165/1036813 March 17 2015 blaine1

    public String getTotalsAsString() {
        Map<CurrencyUnit, Money> totals = new HashMap<CurrencyUnit, Money>();

        for (Expense expense : mExpenses) {
            Money amount = expense.getAmount();
            if (totals.containsKey(amount.getCurrencyUnit())) {
                Money newAmount = totals.get(amount.getCurrencyUnit()).plus(amount.getAmount());
                totals.put(amount.getCurrencyUnit(), newAmount);
            } else {
                totals.put(amount.getCurrencyUnit(), amount);
            }

        }

        StringBuilder sb = new StringBuilder();
        String separator = "";

        for (Map.Entry<CurrencyUnit, Money> amount : totals.entrySet()) {
            sb.append(separator).append(amount.getValue().toString());
            separator = ", ";
        }

        return sb.toString();

    }

    public String getDestinationsAsString() {
        StringBuilder sb = new StringBuilder();
        String separator = "";

        for (Destination dest : mDestinations) {
            sb.append(separator).append(dest.getName());

            separator = " ";
        }

        return sb.toString();
    }

    public long getModified() {
        return mModified;
    }

    public Expense getExpense(String expenseId) {
        for (Expense expense : mExpenses) {
            if (expense.getId().equals(expenseId)) {
                return expense;
            }
        }

        return null;
    }

    public String getAllApprovers() {
        final StringBuilder sb = new StringBuilder();

        Set<User> approvers = new HashSet<User>();

        String delimiter = "";
        for (Comment comment : peekComments()) {
            approvers.add(comment.getApprover());

        }

        for(User approver : approvers) {
            sb.append(delimiter).append(approver.getUserName());
            delimiter = ", ";
        }

        return sb.toString();
    }


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
     * Use this class to obtain instances of {@link com.cmput301.cs.project.models.Claim Claim}.
     * <p/>
     * Creating a Claim:
     * <pre>
     * Claim claim = new Claim.Builder(App.get(this).getUser())
     *                   .startTime(…)
     *                   .endTime(…)
     *                   …
     *                   .build();
     * </pre>
     * <p/>
     * Editing a Claim:
     * <pre>
     * Claim oldClaim = …;
     * Claim claim = oldClaim.edit()
     *                       .startTime(…)
     *                       .endTime(…)
     *                       …
     *                       .build();
     * </pre>
     */
    // Effective Java Item 2
    public static final class Builder {


        // default values
        private final List<Expense> mExpenses = new ArrayList<Expense>();


        private final List<Destination> mDestinations = new ArrayList<Destination>();
        private final SortedSet<Tag> mTags = new TreeSet<Tag>();
        private final boolean mGsonToFill;
        private final List<Comment> mComments = new ArrayList<Comment>();
        private final User mClaimant;


        private long mStartTime = -1;
        private long mEndTime = -1;
        private String mId = UUID.randomUUID().toString();
        private Status mStatus = Status.IN_PROGRESS;
        private boolean mDeleted = false;

        /**
         * For Gson only. Use {@link #Builder(com.cmput301.cs.project.models.User)} instead.
         */
        private Builder() {
            mGsonToFill = true;
            mClaimant = null;
        }

        /**
         * Creates an instance of {@code Builder} with the default values.
         */
        public Builder(User claimaint) {
            Utils.nonNullOrThrow(claimaint, "claimaint");
            mGsonToFill = false;
            mClaimant = claimaint;
        }

        /**
         * Creates an instance of {@code Builder} with the given {@code Claim}.
         *
         * @param claim non-null instance of {@code Claim}
         */
        private Builder(Claim claim) {
            mExpenses.addAll(claim.peekExpenses());
            mDestinations.addAll(claim.peekDestinations());

            mTags.addAll(claim.peekTags());
            mStartTime = claim.getStartTime();
            mEndTime = claim.getEndTime();
            mId = claim.getId();
            mStatus = claim.getStatus();
            mComments.addAll(claim.peekComments());
            mClaimant = claim.getClaimant();
            mGsonToFill = false;
        }

        public Builder delete() {
            mDeleted = true;
            return this;
        }

        public List<Destination> getDestinations() {
            return Collections.unmodifiableList(mDestinations);
        }

        public List<Expense> getExpenses() {
            return Collections.unmodifiableList(mExpenses);
        }

        /**
         * Updates the {@link com.cmput301.cs.project.models.Expense Expense} if there is already an {@code Expense}
         * with the same {@link com.cmput301.cs.project.models.Expense#getId() id}.
         * <p/>
         * Adds the {@code Expense} otherwise.
         *
         * @param expense non-null instance of {@code Expense}
         * @return this instance of {@code Builder}
         * @see #addExpense(Expense)
         */
        public Builder putExpense(Expense expense) {
            Utils.nonNullOrThrow(expense, "expense");
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
         * Adds an {@link com.cmput301.cs.project.models.Expense Expense} to the underlying {@code Set}. Equality
         * is determined by {@link Expense#compareTo(Expense)}.
         *
         * @param expense non-null instance of {@code Expense}
         * @return this instance of {@code Builder}
         * @see #putExpense(Expense)
         */
        private Builder addExpense(Expense expense) {
            Utils.nonNullOrThrow(expense, "expense");
            mExpenses.add(expense);
            return this;
        }

        /**
         * Removes an {@link com.cmput301.cs.project.models.Expense Expense} with the same id.
         *
         * @param expense an instance of {@code Expense}; must not be null
         * @return this instance of {@code Builder}
         */
        public Builder removeExpense(Expense expense) {
            Utils.nonNullOrThrow(expense, "expense");
            final String id = expense.getId();
            for (Iterator<Expense> iterator = mExpenses.iterator(); iterator.hasNext(); ) {
                final Expense e = iterator.next();
                if (e.getId().equals(id)) {
                    iterator.remove();
                    return this;
                }
            }
            return this;
        }

        public Builder addTag(Tag tag) {
            Utils.nonNullOrThrow(tag, "tag");
            mTags.add(tag);
            return this;
        }

        /**
         * Puts the destination and the associated reason to the underlying {@code Map}.
         *
         * @param destination the {@link Destination}
         * @return this instance of {@code Builder}
         */
        public Builder putDestination(Destination destination) {
            Utils.nonNullOrThrow(destination, "destination");
            mDestinations.add(destination);
            return this;
        }


        /**
         * Specifies the start time of the {@code Claim}.
         * <p/>
         * If {@link #isEndTimeSet()}, then the start time must be larger or equals to the end time. No-op if violated.
         *
         * @param startTime non-negative time; and {@code >=} end time if {@link #isEndTimeSet()}; no-op if violated
         * @return this instance of {@code Builder}
         * @see #getStartTime()
         */
        public Builder startTime(long startTime) {
            if (startTime < 0 || (isEndTimeSet() && mEndTime < startTime)) return this;
            mStartTime = startTime;
            return this;
        }

        /**
         * Specifies the end time of the {@code Claim}.
         * <p/>
         * If {@link #isStartTimeSet()}, then the end time must be smaller or equals to the end time. No-op if violated.
         *
         * @param endTime non-negative time; and {@code <=} start time if {@link #isStartTimeSet()}; no-op if violated
         * @return this instance of {@code Builder}
         * @see #getEndTime()
         */
        public Builder endTime(long endTime) {
            if (endTime < 0 || (isStartTimeSet() && mStartTime > endTime)) return this;
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
            Utils.nonNullNonEmptyOrThrow(id, "id");
            mId = id;
            return this;
        }

        public Builder removeTag(Tag tag) {
            mTags.remove(tag);
            return this;
        }

        public Builder submitClaim() {
            mStatus = Status.SUBMITTED;
            return this;
        }

        public Builder returnClaim(User approver, Comment comment) {
            changeStatus(Status.RETURNED, approver, comment);
            return this;
        }

        public Builder approveClaim(User approver, Comment comment) {
            changeStatus(Status.APPROVED, approver, comment);
            return this;
        }

        private void changeStatus(Status status, User approver, Comment comment) {
            if (approver == mClaimant) {
                throw new IllegalArgumentException("Approver cannot be claimaint");
            }

            mComments.add(comment);

            changeStatus(status);
        }

        private void changeStatus(Status status) {
            if ((mStatus == Status.IN_PROGRESS || mStatus == Status.RETURNED) && status != Status.SUBMITTED) {
                throw new IllegalStateException("Returned and in progress claims can only be submitted");
            }
            if ((mStatus == Status.SUBMITTED) && !(status == Status.APPROVED || status == Status.RETURNED)) {
                throw new IllegalStateException("submitted claims can only returned or approved");
            }
            if (mStatus == Status.APPROVED) {
                throw new IllegalStateException("approved claims cannot be changed");
            }


            mStatus = status;
        }

        /**
         * Peeks at the list of {@link com.cmput301.cs.project.models.Tag Tags}.
         *
         * @return an unmodifiable set of {@code Tag}
         */
        public SortedSet<Tag> peekTags() {
            return Collections.unmodifiableSortedSet(mTags);
        }

        /**
         * Peeks at the list of {@link com.cmput301.cs.project.models.Expense Expenses}.
         *
         * @return an unmodifiable list of {@code Expenses}
         */
        public List<Expense> peekExpenses() {
            return Collections.unmodifiableList(mExpenses);
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
         * @return the status specified by {@link com.cmput301.cs.project.models.Claim.Status};
         * otherwise, {@link Status#IN_PROGRESS}; never null
         */
        public Status getStatus() {
            return mStatus;
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
            if (!mGsonToFill && mClaimant == null) {
                throw new IllegalArgumentException("Claimaint cannot be null");
            }

            return new Claim(this);
        }

        public User getClaimant() {
            return mClaimant;
        }

        public void removeDestination(Destination destination) {
            mDestinations.remove(destination);
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
            return new Builder().build();
        }
    };

    private final List<Expense> mExpenses;
    private final List<Destination> mDestinations;
    private final SortedSet<Tag> mTags;
    private final long mStartTime;
    private final long mEndTime;
    private final String mId;
    private final Status mStatus;
    private final List<Comment> mComments;

    // Effective Java Item 2
    private Claim(Builder b) {
        mExpenses = b.mExpenses;
        mDestinations = b.mDestinations;
        mTags = b.mTags;

        mStartTime = b.mStartTime;
        mEndTime = b.mEndTime;
        mId = b.mId;
        mStatus = b.mStatus;
        mComments = b.mComments;
        mClaimant = b.mClaimant;
        mModified = System.currentTimeMillis();
        mDeleted = b.mDeleted;
    }

    /**
     * Creates a {@code Builder} instance with the given {@code Claim}.
     *
     * @return an instance of {@code Builder}
     */
    public Claim.Builder edit() {
        return new Claim.Builder(this);
    }

    public boolean isCompleted() {
        for (Expense expense : mExpenses) {
            if (!expense.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    public List<Destination> getDestinations() {
        return Collections.unmodifiableList(mDestinations);
    }

    /**
     * Peeks at the list of {@link com.cmput301.cs.project.models.Expense Expenses}.
     *
     * @return an unmodifiable list of {@code Expenses}
     */
    public List<Expense> peekExpenses() {
        return Collections.unmodifiableList(mExpenses);
    }

    /**
     * Peeks at the set of {@code String} tags, ordered by {@link String#CASE_INSENSITIVE_ORDER}.
     *
     * @return an unmodifiable sorted set of {@code String} tags
     */
    public SortedSet<Tag> peekTags() {
        return Collections.unmodifiableSortedSet(mTags);
    }

    public List<Comment> peekComments() {
        return Collections.unmodifiableList(mComments);
    }

    /**
     * Peeks at the map of {@code destination -> reason}.
     *
     * @return an unmodifiable map of {@code destination -> reason}
     */
    public List<Destination> peekDestinations() {
        return Collections.unmodifiableList(mDestinations);
    }


    /**
     * @return the start time; always {@code >=} {@link #getEndTime()} and positive
     * @see #getEndTime()
     */
    public long getStartTime() {
        return mStartTime;
    }

    /**
     * @return the end time; always {@code <=} {@link #getStartTime()} and positive
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

    /**
     * Compares this and another instance of {@code Expense} in the following order:
     * <ol>
     * <li>{@link #getStartTime() start time}</li>
     * <li>{@link #getEndTime() end time}</li>
     * <li>{@link #getStatus() status} (by enum ordinal)</li>
     * </ol>
     * Unsorted items:
     * <ul>
     * <li>{@link #peekExpenses() expenses}</li>
     * <li>{@link #peekDestinations() destinations}</li>
     * <li>{@link #peekTags() tags}</li>
     * <li>{@link #getId() id}</li>
     * </ul>
     * This method is <em>inconsistent</em> with {@link #equals(Object)}: if this method returns {@code 0},
     * {@code equals(Object)} may not return {@code true}, as defined in <i>Effective Java</i> Item 12.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Claim o) {
        if (mStartTime < o.mStartTime) return -1;
        if (mStartTime > o.mStartTime) return 1;

        if (mEndTime < o.mEndTime) return -1;
        if (mEndTime > o.mEndTime) return 1;

        final int statusDiff = mStatus.compareTo(o.mStatus);
        if (statusDiff != 0) return statusDiff;

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Claim)) return false;

        final Claim claim = (Claim) o;

        if (mEndTime != claim.mEndTime) return false;
        if (mStartTime != claim.mStartTime) return false;
        if (!mDestinations.equals(claim.mDestinations)) return false;
        if (!mExpenses.equals(claim.mExpenses)) return false;
        if (!mId.equals(claim.mId)) return false;
        if (mStatus != claim.mStatus) return false;
        if (!mTags.equals(claim.mTags)) return false;
        if (!mClaimant.equals(claim.mClaimant)) return false;
        if (!mComments.equals(claim.mComments)) return false;

        return true;
    }

    @Override
    public int hashCode() {

        int result = mExpenses.hashCode();
        result = 31 * result + mDestinations.hashCode();
        result = 31 * result + mTags.hashCode();
        result = 31 * result + (int) (mStartTime ^ (mStartTime >>> 32));
        result = 31 * result + (int) (mEndTime ^ (mEndTime >>> 32));
        result = 31 * result + mId.hashCode();
        result = 31 * result + mStatus.hashCode();
        result = 31 * result + mComments.hashCode();
        result = 31 * result + mClaimant.hashCode();
        return result;
    }

}