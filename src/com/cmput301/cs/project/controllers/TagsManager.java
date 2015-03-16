package com.cmput301.cs.project.controllers;

import android.content.Context;
import com.cmput301.cs.project.model.ClaimUtils;
import com.cmput301.cs.project.model.Tag;
import com.cmput301.cs.project.utils.ClaimSaves;

import java.util.*;

/**
 * Controls the tags that are shown in {@link com.cmput301.cs.project.activities.TagManagerActivity TagManagerActivity}. </br>
 * Implements {@link com.cmput301.cs.project.controllers.TagsChangedListener TagsChangedListener}.
 * 
 * @author rozsa
 *
 */

public class TagsManager {

    private static TagsManager sInstance;

    public static TagsManager get(Context context) {
        if (sInstance == null) {
            sInstance = new TagsManager(context);
        }
        return sInstance;
    }

    public static TagsManager ofClaimSaves(ClaimSaves claimSaves) {
        return new TagsManager(claimSaves);
    }

    private final ClaimSaves mClaimSaves;
    private final SortedSet<Tag> mTags = new TreeSet<Tag>();
    private final List<TagsChangedListener> mListeners = new ArrayList<TagsChangedListener>();

    private TagsManager(Context context) {
        this(ClaimSaves.ofAndroid(context));
    }

    private TagsManager(ClaimSaves claimSaves) {
        mClaimSaves = claimSaves;
        mTags.addAll(claimSaves.readAllTags());
    }

    public void addTagChangedListener(TagsChangedListener listener) {
        ClaimUtils.nonNullOrThrow(listener, "listener");
        mListeners.add(listener);
    }

    public void removeTagChangedListener(TagsChangedListener removing) {
        for (Iterator<TagsChangedListener> iterator = mListeners.iterator(); iterator.hasNext(); ) {
            final TagsChangedListener listener = iterator.next();
            if (listener == removing) {
                iterator.remove();
                break;
            }
        }
    }

    public Tag getTagByName(String name) {
        name = name.trim();
        final Tag tag = findTagByName(name);
        final Tag out;
        if (tag == null) {
            out = new Tag(name, this);
            mTags.add(out);
            tagCreatedInternal(out);
        } else {
            out = tag;
        }
        return out;
    }

    public Tag findTagByName(String name) {
        name = name.trim();
        for (Tag tag : mTags) {
            if (tag.getName().equals(name)) {
                return tag;
            }
        }
        return null;
    }

    public Tag findTagById(String id) {
        for (Tag tag : mTags) {
            if (tag.getId().equals(id)) {
                return tag;
            }
        }
        return null;
    }

    public Tag renameTag(Tag tag, String newName) {
        final String oldName = tag.getName();
        final Tag newTag = new Tag(newName, this, tag.getId());
        tagRenamedInternal(newTag, oldName);
        return newTag;
    }

    public void deleteTagById(String id) {
        final Tag tag = findTagById(id);
        if (tag != null) {
            mTags.remove(tag);
            tagDeletedInternal(tag);
        }
    }

    public void deleteTagByName(String name) {
        final Tag tag = findTagByName(name);
        if (tag != null) {
            mTags.remove(tag);
            tagDeletedInternal(tag);
        }
    }

    private void tagCreatedInternal(Tag tag) {
        mClaimSaves.saveAllTags(peekTags());
        notifyListenersCreated(tag);
    }

    private void tagRenamedInternal(Tag tag, String oldName) {
        mClaimSaves.saveAllTags(peekTags());
        notifyListenersRenamed(tag, oldName);
    }

    private void tagDeletedInternal(Tag tag) {
        mClaimSaves.saveAllTags(peekTags());
        notifyListenersDeleted(tag);
    }

    private void notifyListenersCreated(Tag tag) {
        for (TagsChangedListener listener : mListeners) {
            listener.onTagCreated(tag);
        }
    }


    private void notifyListenersRenamed(Tag tag, String oldName) {
        for (TagsChangedListener listener : mListeners) {
            listener.onTagRenamed(tag, oldName);
        }
    }

    private void notifyListenersDeleted(Tag tag) {
        for (TagsChangedListener listener : mListeners) {
            listener.onTagDeleted(tag);
        }
    }

    public SortedSet<Tag> peekTags() {
        return Collections.unmodifiableSortedSet(mTags);
    }
}
