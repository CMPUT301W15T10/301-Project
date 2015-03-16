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

import android.os.Parcel;
import android.os.Parcelable;
import com.cmput301.cs.project.controllers.TagsChangedListener;
import com.cmput301.cs.project.controllers.TagsManager;
import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Class that contains a set of implements Parcelable {@link com.cmput301.cs.project.model.Expense Expenses}, and details of a trip. <br/>
 * This is an immutable class. <br/>
 * Use {@link com.cmput301.cs.project.model.Claim.Builder Claim.Builder} to obtain an instance.
 * <p/>
 * If you want this to update itself as tags changes, add this with {@link TagsManager#addTagChangedListener(TagsChangedListener)}
 */
// Effective Java Item 15, 17
public final class Claim implements Comparable<Claim>, TagsChangedListener, Parcelable {

    /**
     * The unspecified title.
     */
    public static final String TITLE_UNNAMED = "";
    private final User mClaimant;

    public User getClaimant() {
        return mClaimant;
    }

    public boolean canApprove(User user) {
        if(mClaimant.equals(user)) {
            return false;
        }

        if(mComments.size() == 0){
            return true;
        }

        return mComments.get(0).getApprover().equals(user);
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
     * Use this class to obtain instances of {@link com.cmput301.cs.project.model.Claim Claim}.
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
        private final List<Expense> mExpenses = new ArrayList<Expense>();



        private final Map<String, String> mDestinations = new HashMap<String, String>();  // Destination -> Reason
        private final SortedSet<Tag> mTags = new TreeSet<Tag>();
        private final boolean mGsonToFill;
        private final List<Comment> mComments = new ArrayList<Comment>();

        private String mTitle = TITLE_UNNAMED;
        private long mStartTime = -1;
        private long mEndTime = -1;
        private String mId = UUID.randomUUID().toString();
        private Status mStatus = Status.IN_PROGRESS;
        private User mClaimant;

        /**
         * For Gson only. Use {@link #Builder(com.cmput301.cs.project.model.User)} instead.
         */
        private Builder() {
            mGsonToFill = true;
        }

        /**
         * Creates an instance of {@code Builder} with the default values.
         */
        public Builder(User claimaint) {
            ClaimUtils.nonNullOrThrow(claimaint, "claimaint");
            mClaimant = claimaint;
            mGsonToFill = false;
        }

        /**
         * Creates an instance of {@code Builder} with the given {@code Claim}.
         *
         * @param claim non-null instance of {@code Claim}
         */
        private Builder(Claim claim) {
            mExpenses.addAll(claim.peekExpenses());
            mDestinations.putAll(claim.peekDestinations());
            mTitle = claim.getTitle();
            mTags.addAll(claim.peekTags());
            mStartTime = claim.getStartTime();
            mEndTime = claim.getEndTime();
            mId = claim.getId();
            mStatus = claim.getStatus();
            mComments.addAll(claim.peekComments());
            mClaimant = claim.getClaimant();
            mGsonToFill = false;
        }

        public Map<String, String> getDestinations(){
            return Collections.unmodifiableMap(mDestinations);
        }

        public List<Expense> getExpenses(){
            return Collections.unmodifiableList(mExpenses);
        }

        /**
         * Updates the {@link com.cmput301.cs.project.model.Expense Expense} if there is already an {@code Expense}
         * with the same {@link com.cmput301.cs.project.model.Expense#getId() id}.
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
         * Adds an {@link com.cmput301.cs.project.model.Expense Expense} to the underlying {@code Set}. Equality
         * is determined by {@link Expense#compareTo(Expense)}.
         *
         * @param expense non-null instance of {@code Expense}
         * @return this instance of {@code Builder}
         * @see #putExpense(Expense)
         */
        private Builder addExpense(Expense expense) {
            ClaimUtils.nonNullOrThrow(expense, "expense");
            mExpenses.add(expense);
            return this;
        }

        /**
         * Removes an {@link com.cmput301.cs.project.model.Expense Expense} with the same id.
         *
         * @param expense an instance of {@code Expense}; must not be null
         * @return this instance of {@code Builder}
         */
        public Builder removeExpenseById(Expense expense) {
            ClaimUtils.nonNullOrThrow(expense, "expense");
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
            mTags.add(tag);
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
            mDestinations.put(destination, reason);
            return this;
        }

        /**
         * Specifies the title of the {@code Claim}.
         *
         * @param title {@code String} title; must no be null
         * @return this instance of {@code Builder}
         * @see #getTitle()
         */
        public Builder title(String title) {
            ClaimUtils.nonNullnonEmptyOrThrow(title, "title");
            mTitle = title;
            return this;
        }

        public Builder claimaint(User claimaint) {
            ClaimUtils.nonNullOrThrow(claimaint, "claimant");
            mClaimant = claimaint;
            return this;
        }

        /**
         * Specifies the start time of the {@code Claim}.
         * <br/>
         * If {@link #isEndTimeSet()}, then the start time must be larger or equals to the end time. No-op if violated.
         *
         * @param startTime non-negative time; and >= end time if {@link #isEndTimeSet()}; no-op if violated
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
         * <br/>
         * If {@link #isStartTimeSet()}, then the end time must be smaller or equals to the end time. No-op if violated.
         *
         * @param endTime non-negative time; and <= start time if {@link #isStartTimeSet()}; no-op if violated
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
            ClaimUtils.nonNullnonEmptyOrThrow(id, "id");
            mId = id;
            return this;
        }

        public Builder removeTag(Tag tag) {
            mTags.remove(tag);
            return this;
        }

        /**
         * Peeks at the list of {@link com.cmput301.cs.project.model.Expense Expenses}.
         *
         * @return an unmodifiable list of {@code Expenses}
         */
        public List<Expense> peekExpenses() {
            return Collections.unmodifiableList(mExpenses);
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
         * @return the status specified by {@link ( com.cmput301.cs.project.model.Claim.Status)};
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
            if (!mGsonToFill && mClaimant == null) {
                throw new IllegalArgumentException("Claimaint cannot be null");
            }

            return new Claim(this);
        }

        public User getClaimant() {
            return mClaimant;
        }

        public void removeDestination(String destination) {
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
    private final Map<String, String> mDestinations;  // destination -> reason
    private final SortedSet<Tag> mTags;
    private final String mTitle;
    private final long mStartTime;
    private final long mEndTime;
    private final String mId;
    private Status mStatus;
    private final List<Comment> mComments;

    // Effective Java Item 2
    private Claim(Builder b) {
        mExpenses = b.mExpenses;
        mDestinations = b.mDestinations;
        mTags = b.mTags;
        mTitle = b.mTitle;
        mStartTime = b.mStartTime;
        mEndTime = b.mEndTime;
        mId = b.mId;
        mStatus = b.mStatus;
        mComments = b.mComments;
        mClaimant = b.mClaimant;
    }

    public Map<String, String> getDestinations() {
        return Collections.unmodifiableMap(mDestinations);
    }

    // generated by http://www.parcelabler.com/
    protected Claim(Parcel in) {
        mClaimant = (User) in.readValue(User.class.getClassLoader());
        if (in.readByte() == 0x01) {
            mExpenses = new ArrayList<Expense>();
            in.readList(mExpenses, Expense.class.getClassLoader());
        } else {
            mExpenses = null;
        }
        mTags = (SortedSet) in.readValue(SortedSet.class.getClassLoader());
        mTitle = in.readString();
        mStartTime = in.readLong();
        mEndTime = in.readLong();
        mId = in.readString();
        mStatus = (Status) in.readValue(Status.class.getClassLoader());
        if (in.readByte() == 0x01) {
            mComments = new ArrayList<Comment>();
            in.readList(mComments, Comment.class.getClassLoader());
        } else {
            mComments = null;
        }
        final int size = in.readInt();
        mDestinations = new HashMap<String, String>(size);
        for (int i = 0; i < size; i++) {
            mDestinations.put(in.readString(), in.readString());
        }
    }

    /**
     * Peeks at the list of {@link com.cmput301.cs.project.model.Expense Expenses}.
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
    public void onTagRenamed(Tag tag, String oldName) {
        final String id = tag.getId();
        for (Iterator<Tag> iterator = mTags.iterator(); iterator.hasNext(); ) {
            final Tag t = iterator.next();
            if (t.getId().equals(id)) {
                iterator.remove();
                mTags.add(tag);
                break;
            }
        }
    }

    @Override
    public void onTagDeleted(Tag tag) {
        mTags.remove(tag);
    }

    @Override
    public void onTagCreated(Tag tag) {
        // do nothing
    }

    /**
     * Compares this and another instance of {@code Expense} in the following order:
     * <ol>
     * <li>{@link #getStartTime() start time}</li>
     * <li>{@link #getEndTime() end time}</li>
     * <li>{@link #getTitle() title}</li>
     * <li>{@link #getStatus() status} (by enum ordinal)</li>
     * <li>{@link #getId() id}</li>
     * </ol>
     * Unsorted items:
     * <ul>
     * <li>{@link #peekExpenses() expenses}</li>
     * <li>{@link #peekDestinations() destinations}</li>
     * <li>{@link #peekTags() tags}</li>
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

        final int titleDiff = mTitle.compareToIgnoreCase(o.mTitle);
        if (titleDiff != 0) return titleDiff;

        final int statusDiff = mStatus.compareTo(o.mStatus);
        if (statusDiff != 0) return statusDiff;

        final int idDiff = mId.compareToIgnoreCase(o.mId);
        if (idDiff != 0) return idDiff;

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
        if (!mTitle.equals(claim.mTitle)) return false;
        if (!mClaimant.equals(claim.mClaimant)) return false;
        if (!mComments.equals(claim.mComments)) return false;

        return true;
    }

    @Override
    public int hashCode() {

        int result = mExpenses.hashCode();
        result = 31 * result + mDestinations.hashCode();
        result = 31 * result + mTags.hashCode();
        result = 31 * result + mTitle.hashCode();
        result = 31 * result + (int) (mStartTime ^ (mStartTime >>> 32));
        result = 31 * result + (int) (mEndTime ^ (mEndTime >>> 32));
        result = 31 * result + mId.hashCode();
        result = 31 * result + mStatus.hashCode();
        result = 31 * result + mComments.hashCode();
        result = 31 * result + mClaimant.hashCode();
        return result;
    }

    public void submitClaim() {
        mStatus = Status.SUBMITTED;
    }

    public void returnClaim(User approver, Comment comment) {
        changeStatus(Status.RETURNED, approver, comment);
    }

    public void approveClaim(User approver, Comment comment) {
        changeStatus(Status.APPROVED, approver, comment);
    }

    private void changeStatus(Status status, User approver, Comment comment) {
        if (approver == mClaimant) {
            throw new IllegalArgumentException("Approver cannot be claimaint");
        }

        changeStatus(status);
    }

    private void changeStatus(Status status) {
        if ((mStatus == Status.IN_PROGRESS || mStatus == Status.RETURNED) && status != Status.SUBMITTED) {
            throw new IllegalStateException("Statuses cannot be changed in such a way");
        }
        if (mStatus == Status.SUBMITTED && status != Status.SUBMITTED || status != Status.APPROVED) {
            throw new IllegalStateException("Statuses cannot be changed in such a way");
        }
        if (mStatus == Status.APPROVED) {
            throw new IllegalStateException("Statuses cannot be changed in such a way");
        }

        mStatus = status;
    }


    private void addComment(Comment comment) {
        this.mComments.add(comment);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    // generated by http://www.parcelabler.com/
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mClaimant);
        if (mExpenses == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mExpenses);
        }
        dest.writeValue(mTags);
        dest.writeString(mTitle);
        dest.writeLong(mStartTime);
        dest.writeLong(mEndTime);
        dest.writeString(mId);
        dest.writeValue(mStatus);
        if (mComments == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mComments);
        }
        dest.writeInt(mDestinations.size());
        for (Map.Entry<String, String> entry : mDestinations.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    // generated by http://www.parcelabler.com/
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Claim> CREATOR = new Parcelable.Creator<Claim>() {
        @Override
        public Claim createFromParcel(Parcel in) {
            return new Claim(in);
        }

        @Override
        public Claim[] newArray(int size) {
            return new Claim[size];
        }
    };
}