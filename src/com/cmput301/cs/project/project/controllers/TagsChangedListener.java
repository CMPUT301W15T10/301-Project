package com.cmput301.cs.project.project.controllers;

import com.cmput301.cs.project.project.model.Tag;

public interface TagsChangedListener {
    public void onTagRenamed(Tag tag, String oldName);

    public void onTagDeleted(Tag tag);

    public void onTagCreated(Tag tag);
}