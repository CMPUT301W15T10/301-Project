package com.cmput301.cs.project.test.general;


import com.cmput301.cs.project.activities.ClaimListActivity;
import com.cmput301.cs.project.controllers.TagsChangedListener;
import com.cmput301.cs.project.controllers.TagsManager;
import com.cmput301.cs.project.model.Tag;
import com.cmput301.cs.project.utils.MockClaimSaves;

import android.test.ActivityInstrumentationTestCase2;

public class TagsManagerTest extends ActivityInstrumentationTestCase2<ClaimListActivity> {

	public TagsManagerTest() {
		super(ClaimListActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	private TagsManager mManager;
    private TagsChangedListener mListener;
    private Tag mDeletedTag;
    private Tag mCreatedTag;

    public void testsetup() {
        mDeletedTag = null;
        mCreatedTag = null;
        mListener = new TagsChangedListener() {
            @Override
            public void onTagRenamed(Tag tag, String oldName) {
                assertEquals("ok", oldName);
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

    public void testSingleTag() {
        final Tag okTag = mManager.getTagByName("ok");
        final Tag aOkTag = mManager.getTagByName("ok");
        assertTrue(okTag == aOkTag);  // identity check
        assertEquals(okTag.getId(), aOkTag.getId());
        assertEquals(okTag.getName(), aOkTag.getName());
    }

    public void testDeleteTag() {
        mManager.getTagByName("ok");
        assertNotNull(mManager.findTagByName("ok"));
        mManager.deleteTagByName("ok");
        assertNull(mManager.findTagByName("ok"));
    }

    public void testDeleteListener() {
        final Tag okTag = mManager.getTagByName("ok");
        mDeletedTag = okTag;
        mManager.addTagChangedListener(mListener);
        mManager.deleteTagById(okTag.getId());
    }

    public void testCreateListener() {
        mManager.addTagChangedListener(mListener);
        final Tag tag = mManager.getTagByName("ok");
        assertTrue(mCreatedTag == tag);  // identity check
    }

    public void testRenameListener() {
        final Tag ok = mManager.getTagByName("ok");
        mManager.addTagChangedListener(mListener);
        mManager.renameTag(ok, "wut now");
    }
}
