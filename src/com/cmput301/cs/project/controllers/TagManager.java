package com.cmput301.cs.project.controllers;

import android.content.Context;
import com.cmput301.cs.project.model.Tag;
import com.cmput301.cs.project.utils.ClaimSaves;

import java.util.*;

public class TagManager {
    public static final String FILE_NAME = "tags.json";

    private static TagManager sInstance;

    public static TagManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TagManager(context);
        }
        return sInstance;
    }

    public static TagManager ofClaimSaves(ClaimSaves claimSaves) {
        return new TagManager(claimSaves);
    }

    public interface TagChangedListener {
        public void onTagRenamed(Tag tag, String oldName);

        public void onTagDeleted(Tag tag);
    }

    private final ClaimSaves mClaimSaves;
    private final SortedSet<Tag> mTags = new TreeSet<Tag>();
    private final List<TagChangedListener> mListeners = new ArrayList<TagChangedListener>();

    private TagManager(Context context) {
        this(ClaimSaves.ofAndroid(context));
    }

    private TagManager(ClaimSaves claimSaves) {
        mClaimSaves = claimSaves;
        mTags.addAll(claimSaves.readAllTags());
        mListeners.add(new TagChangedListener() {
            @Override
            public void onTagRenamed(Tag tag, String oldName) {
                mClaimSaves.saveAllTags(peekTags());
            }

            @Override
            public void onTagDeleted(Tag tag) {
                mClaimSaves.saveAllTags(peekTags());
            }
        });
    }

    public void addTagChangedListener(TagChangedListener listener) {
        mListeners.add(listener);
    }

    public void removeTagChangedListener(TagChangedListener listener) {
        mListeners.remove(listener);
    }

    public Tag getTagByName(String name) {
        final Tag tag = findTagByName(name);
        final Tag out;
        if (tag == null) {
            out = new Tag(name, this);
            mTags.add(out);
        } else {
            out = tag;
        }
        return out;
    }

    public Tag findTagByName(String name) {
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
        for (TagChangedListener listener : mListeners) {
            listener.onTagRenamed(newTag, oldName);
        }
        return newTag;
    }

    public void deleteTagById(String id) {
        final Tag tag = findTagById(id);
        if (tag != null) {
            mTags.remove(tag);
            notifyListenersDeleted(tag);
        }
    }

    public void deleteTagByName(String name) {
        final Tag tag = findTagByName(name);
        if (tag != null) {
            mTags.remove(tag);
            notifyListenersDeleted(tag);
        }
    }

    private void notifyListenersDeleted(Tag tag) {
        for (TagChangedListener listener : mListeners) {
            listener.onTagDeleted(tag);
        }
    }

    public SortedSet<Tag> peekTags() {
        return Collections.unmodifiableSortedSet(mTags);
    }
}
