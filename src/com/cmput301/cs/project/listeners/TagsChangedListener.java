package com.cmput301.cs.project.listeners;

import com.cmput301.cs.project.controllers.TagsManager;
import com.cmput301.cs.project.models.Tag;

/**
 * Interface for listeners that {@link TagsManager TagsManager} uses.
 *
 * @author rozsa
 */
public interface TagsChangedListener {
    /**
     * Called after a {@link Tag} is renamed.
     *
     * @param newTag the new tag with the new name; never null
     * @param oldTag the old tag with the old name; never null
     * @see TagsManager#renameTag(Tag, String)
     */
    void onTagRenamed(Tag newTag, Tag oldTag);

    /**
     * Called after a {@link Tag} is deleted.
     *
     * @param tag the deleted tag; never null
     * @see TagsManager#deleteTagById(String)
     * @see TagsManager#deleteTagByName(String)
     */
    void onTagDeleted(Tag tag);

    /**
     * Called after a {@link Tag} is created.
     *
     * @param tag the new tag; never null
     */
    void onTagCreated(Tag tag);
}