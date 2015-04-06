package com.cmput301.cs.project.listeners;

import com.cmput301.cs.project.models.Tag;

import java.util.ArrayList;

/*
 *   If this listeners is used by TagSelectorDialogFragment, must be implemented by the Activity
 */
public interface TagSelectorListener {
    public void wantedTagsChanged(ArrayList<Tag> newWantedTags);
}
