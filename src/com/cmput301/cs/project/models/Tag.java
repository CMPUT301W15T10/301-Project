package com.cmput301.cs.project.models;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import com.cmput301.cs.project.controllers.TagsManager;
import com.cmput301.cs.project.utils.Utils;

import java.util.UUID;

/**
 * Class that contains a name and a unique ID that a user can associate with a {@link com.cmput301.cs.project.models.Claim Claim}. A {@link com.cmput301.cs.project.models.Claim Claim} can hold any number of tags that are associated with the claimant ({@link com.cmput301.cs.project.models.User User}) <p>
 * Specific tags are bound to a {@link com.cmput301.cs.project.models.User User} (device).
 * <p/>
 * <b>Creating a new Tag:</b>
 * <pre>
 *     final TagsManager manager = …;
 *     final String name = …;
 *     final Tag tag = manager.getTagByName(name);
 * </pre>
 * <p/>
 * <b>Editing an Existing Tag:</b>
 * <pre>
 *     final Tag oldTag = …;
 *     final String newName = …;
 *     final TagsManager manager = …;
 *     final newTag = manager.renameTag(oldTag, newName);
 *     // oldTag should be discarded
 * </pre>
 * The Parcelable implementation allows the tags to be passed into and pulled out of intents by using
 * {@link Intent#putExtra(String, Parcelable)} and {@link Intent#getParcelableExtra(String)} respectively.
 * <p/>
 * Each tag is given a random unique id upon construction.
 *
 * @author rozsa
 * @author jbenson
 */

public class Tag implements Comparable<Tag>, Parcelable {

    private final String mId;
    private final String mName;

    /**
     * generates a random ID for the tag using the randomUUID() method.
     *
     * @param name
     * @param manager
     */
    public Tag(String name, TagsManager manager) {
        this(name, manager, UUID.randomUUID().toString());
    }

    public Tag(String name, TagsManager manager, String id) {
        Utils.nonNullOrThrow(manager, "manager");
        mName = name.trim();
        mId = Utils.nonNullOrThrow(id, "id");
    }

    /**
     * @return non-null instance of {@code String}
     */
    public String getId() {
        return mId;
    }

    /**
     * @return non-null instance of {@code String}
     */
    public String getName() {
        return this.mName;
    }

    /**
     * Compares the {@code Tag} by {@link #getName() name}.
     * {@inheritDoc}
     *
     * @param o non-null instance of {@code Tag}
     * @return specified by {@link String#compareTo(String)}
     */
    @Override
    public int compareTo(Tag o) {
        return mName.compareTo(o.getName());
    }

    // Generated by IntelliJ
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;

        final Tag tag = (Tag) o;

        if (!mId.equals(tag.mId)) return false;
        return mName.equals(tag.mName);

    }

    // Generated by IntelliJ
    @Override
    public int hashCode() {
        int result = mId.hashCode();
        result = 31 * result + mName.hashCode();
        return result;
    }

    /**
     * this code is merely implementing Android.Parcelable<p>
     * you can read more on Parcels here: <a href="http://developer.android.com/reference/android/os/Parcelable.html"> http://developer.android.com</a>
     */
    // generated by http://www.parcelabler.com/
    protected Tag(Parcel in) {
        mId = in.readString();
        mName = in.readString();
    }

    /**
     * this code is merely implementing Android.Parcelable<p>
     * you can read more on Parcels here: <a href="http://developer.android.com/reference/android/os/Parcelable.html"> http://developer.android.com</a>
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * this code is merely implementing Android.Parcelable<p>
     * you can read more on Parcels here: <a href="http://developer.android.com/reference/android/os/Parcelable.html"> http://developer.android.com</a>
     */
    // generated by http://www.parcelabler.com/
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mName);
    }

    /**
     * this code is merely implementing Android.Parcelable<p>
     * you can read more on Parcels here: <a href="http://developer.android.com/reference/android/os/Parcelable.html"> http://developer.android.com</a>
     */
    // generated by http://www.parcelabler.com/
    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };
}