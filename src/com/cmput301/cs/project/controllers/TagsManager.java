package com.cmput301.cs.project.controllers;

import android.content.Context;
import com.cmput301.cs.project.listeners.TagsChangedListener;
import com.cmput301.cs.project.models.ClaimUtils;
import com.cmput301.cs.project.models.Tag;
import com.cmput301.cs.project.serialization.LocalClaimSaver;

import java.util.*;

/**
 * Controls the tags that are shown in {@link com.cmput301.cs.project.activities.TagManagerActivity TagManagerActivity}. <p>
 * Loads any locally saved claims from the {@link com.cmput301.cs.project.serialization.LocalClaimSaver LocalClaimSaver} <p>
 * <p/>
 * Use {@link TagsManager#get(Context)} to obtain the singleton.
 * There are getter methods, as well as the search methods, for using either the {@link #findTagByName(String) name}
 * or the {@link #findTagById(String) id} to find the tag.
 * You can also delete the tag by either its {@link #deleteTagByName(String) name} or its {@link #deleteTagById(String) id}.
 */
public class TagsManager {

    private static TagsManager sInstance;

    /**
     * Obtains the singleton for {@code TagsManager}.
     *
     * @param context non-null instance of {@link Context}
     * @return a non-null instance of {@code TagsManager}
     */
    public static TagsManager get(Context context) {
        if (sInstance == null) {
            sInstance = new TagsManager(context);
        }
        return sInstance;
    }

    /**
     * TEST ONLY. Uses the supplied {@link LocalClaimSaver} for all operations.
     *
     * @param claimSaves non-null instance of {@code LocalClaimSaver}
     * @return a non-null instance of {@code TagsManager}
     */
    public static TagsManager ofClaimSaves(LocalClaimSaver claimSaves) {
        return new TagsManager(claimSaves);
    }

    private final LocalClaimSaver mClaimSaves;
    private final SortedSet<Tag> mTags = new TreeSet<Tag>();
    private final List<TagsChangedListener> mListeners = new ArrayList<TagsChangedListener>();

    private TagsManager(Context context) {
        this(LocalClaimSaver.ofAndroid(context));
    }

    private TagsManager(LocalClaimSaver claimSaves) {
        mClaimSaves = claimSaves;
        mTags.addAll(claimSaves.readAllTags());
    }

    /**
     * Adds a {@link TagsChangedListener} for listening to changes of {@link Tag Tags}. Refer to specific methods for
     * info of callbacks.
     *
     * @param listener non-null instance of {@code TagsChangedListener}
     * @see #removeTagChangedListener(TagsChangedListener)
     * @see #getTagByName(String)
     * @see #renameTag(Tag, String)
     * @see #deleteTagById(String)
     * @see #deleteTagByName(String)
     */
    public void addTagChangedListener(TagsChangedListener listener) {
        ClaimUtils.nonNullOrThrow(listener, "listener");
        mListeners.add(listener);
    }

    /**
     * Removes the listener by reference checking ({@code==} operator).
     *
     * @param removing an instance of {@link TagsChangedListener}
     */
    public void removeTagChangedListener(TagsChangedListener removing) {
        for (Iterator<TagsChangedListener> iterator = mListeners.iterator(); iterator.hasNext(); ) {
            final TagsChangedListener listener = iterator.next();
            if (listener == removing) {  // ref. check
                iterator.remove();
                break;
            }
        }
    }

    /**
     * Finds or <em>creates</em> a {@link Tag} with the supplied {@code name}. If a new {@code Tag} is created,
     * {@link TagsChangedListener#onTagCreated(Tag)} is called after the creation.
     *
     * @param name non-null {@code String} name
     * @return a non-null instance of {@code Tag}
     */
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

    /**
     * Finds an <em>existing</em> {@link Tag} with the supplied {@code name}. Returns null if none is found.
     *
     * @param name the {@code String} name; usually obtained by {@link Tag#getName()}
     * @return an instance of {@code Tag} with the same name; null if not found
     */
    public Tag findTagByName(String name) {
        name = name.trim();
        for (Tag tag : mTags) {
            if (tag.getName().equals(name)) {
                return tag;
            }
        }
        return null;
    }

    /**
     * Finds an <em>existing</em> {@link Tag} with the supplied {@code id}. Returns null if none is found.
     *
     * @param id the {@code String} id; usually obtained by {@link Tag#getId()}
     * @return an instance of {@code Tag} with the same id; null if not found
     */
    public Tag findTagById(String id) {
        for (Tag tag : mTags) {
            if (tag.getId().equals(id)) {
                return tag;
            }
        }
        return null;
    }

    /**
     * Renames a {@link Tag}. A new instance of {code Tag} will be created with the same {@link Tag#getId() id}.
     * The old instance should be discarded. {@link TagsChangedListener#onTagRenamed(Tag, Tag)} is called after a
     * successful removal.
     *
     * @param oldTag  the {@code Tag} to be renamed
     * @param newName non-null {@code String} name
     * @return a non-null instance of {@code Tag} with the same id
     */
    public Tag renameTag(Tag oldTag, String newName) {
        final Tag newTag = new Tag(newName, this, oldTag.getId());
        mTags.remove(oldTag);
        mTags.add(newTag);
        tagRenamedInternal(newTag, oldTag);
        return newTag;
    }

    /**
     * Finds and deletes an existing {@link Tag} by its {@code id}. No-op if the tag is not found.
     * {@link TagsChangedListener#onTagDeleted(Tag)} is called after a successful removal.
     *
     * @param id id of the {@code Tag}; usually obtained by {@link Tag#getId()}
     */
    public void deleteTagById(String id) {
        final Tag tag = findTagById(id);
        if (tag != null) {
            mTags.remove(tag);
            tagDeletedInternal(tag);
        }
    }

    /**
     * Finds and deletes an existing {@link Tag} by its {@code name}. No-op if the tag is not found.
     * {@link TagsChangedListener#onTagDeleted(Tag)} is called after a successful removal.
     *
     * @param name id of the {@code Tag}; usually obtained by {@link Tag#getName()}
     */
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

    private void tagRenamedInternal(Tag tag, Tag oldTag) {
        mClaimSaves.saveAllTags(peekTags());
        notifyListenersRenamed(tag, oldTag);
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


    private void notifyListenersRenamed(Tag tag, Tag oldTag) {
        for (TagsChangedListener listener : mListeners) {
            listener.onTagRenamed(tag, oldTag);
        }
    }

    private void notifyListenersDeleted(Tag tag) {
        for (TagsChangedListener listener : mListeners) {
            listener.onTagDeleted(tag);
        }
    }

    /**
     * Peeks at all the existing {@link Tag Tags}. Sorted by the natural order of {@code Tag}.
     *
     * @return unmodifiable {@link SortedSet} of {@code Tags}; sorted by {@link Tag#compareTo(Tag)}
     */
    public SortedSet<Tag> peekTags() {
        return Collections.unmodifiableSortedSet(mTags);
    }
}
