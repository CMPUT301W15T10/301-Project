package com.cmput301.cs.project;

import com.cmput301.cs.project.controllers.TagsChangedListener;
import com.cmput301.cs.project.controllers.TagsManager;
import com.cmput301.cs.project.model.Tag;
import com.cmput301.cs.project.utils.MockClaimSaves;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TagsManagerTest {

    private TagsManager mManager;
    private TagsChangedListener mListener;
    private Tag mDeletedTag;
    private Tag mCreatedTag;

    @Before
    public void setup() {
        mDeletedTag = null;
        mCreatedTag = null;
        mListener = new TagsChangedListener() {
            @Override
            public void onTagRenamed(Tag tag, Tag oldTag) {
                assertEquals("ok", oldTag.getName());
            }

            @Override
            public void onTagDeleted(Tag tag) {
                assertTrue(mDeletedTag == tag);  // identity check
            }

            @Override
            public void onTagCreated(Tag tag) {
                mCreatedTag = tag;
            }
        };
        mManager = TagsManager.ofClaimSaves(new MockClaimSaves());
    }

    @Test
    public void singleTag() {
        final Tag okTag = mManager.getTagByName("ok");
        final Tag aOkTag = mManager.getTagByName("ok");
        assertTrue(okTag == aOkTag);  // identity check
        assertEquals(okTag.getId(), aOkTag.getId());
        assertEquals(okTag.getName(), aOkTag.getName());
    }

    @Test
    public void deleteTag() {
        mManager.getTagByName("ok");
        assertNotNull(mManager.findTagByName("ok"));
        mManager.deleteTagByName("ok");
        assertNull(mManager.findTagByName("ok"));
    }

    @Test
    public void deleteListener() {
        final Tag okTag = mManager.getTagByName("ok");
        mDeletedTag = okTag;
        mManager.addTagChangedListener(mListener);
        mManager.deleteTagById(okTag.getId());
    }

    @Test
    public void createListener() {
        mManager.addTagChangedListener(mListener);
        final Tag tag = mManager.getTagByName("ok");
        assertTrue(mCreatedTag == tag);  // identity check
    }

    @Test
    public void renameListener() {
        final Tag ok = mManager.getTagByName("ok");
        mManager.addTagChangedListener(mListener);
        mManager.renameTag(ok, "wut now");
    }
}
