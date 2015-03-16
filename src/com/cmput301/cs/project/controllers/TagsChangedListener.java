package com.cmput301.cs.project.controllers;

import com.cmput301.cs.project.model.Tag;

/**
 * Interface for listeners that {@link com.cmput301.cs.project.controllers.TagsManager TagsManager} uses.
 * 
 * @author rozsa
 *
 */

public interface TagsChangedListener {
    public void onTagRenamed(Tag tag, String oldName);

    public void onTagDeleted(Tag tag);

    public void onTagCreated(Tag tag);
}