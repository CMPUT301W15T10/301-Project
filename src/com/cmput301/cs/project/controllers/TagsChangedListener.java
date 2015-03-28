package com.cmput301.cs.project.controllers;

import com.cmput301.cs.project.models.Tag;

/**
 * Interface for listeners that {@link com.cmput301.cs.project.controllers.TagsManager TagsManager} uses.
 * 
 * @author rozsa
 *
 */

public interface TagsChangedListener {
    void onTagRenamed(Tag tag, Tag oldName);

    void onTagDeleted(Tag tag);

    void onTagCreated(Tag tag);
}