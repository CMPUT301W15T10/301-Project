package com.cmput301.cs.project.project.model;

import com.cmput301.cs.project.project.controllers.TagsManager;

import java.util.UUID;

/**
 * Created by Blaine on 02/03/2015.
 */

public class Tag implements Comparable<Tag> {

    private final String mId;
    private final String mName;

    private Tag() {
        mId = "Gson only";
        mName = "Gson only";
    }

    public Tag(String name, TagsManager manager) {
        this(name, manager, UUID.randomUUID().toString());
    }

    public Tag(String name, TagsManager manager, String id) {
        ClaimUtils.nonNullOrThrow(manager, "manager");
        mName = name.trim();
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return this.mName;
    }

    @Override
    public int compareTo(Tag o) {
        return mName.compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;

        final Tag tag = (Tag) o;

        if (!mId.equals(tag.mId)) return false;
        if (!mName.equals(tag.mName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mId.hashCode();
        result = 31 * result + mName.hashCode();
        return result;
    }
}