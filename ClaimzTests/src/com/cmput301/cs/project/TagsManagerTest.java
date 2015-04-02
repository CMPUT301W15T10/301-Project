package com.cmput301.cs.project;

import junit.framework.TestCase;

import com.cmput301.cs.project.controllers.TagsManager;
import com.cmput301.cs.project.models.Tag;
import com.cmput301.cs.project.utils.MockClaimSaves;

public class TagsManagerTest extends TestCase {

    private TagsManager mManager;

    @Override
    protected void setUp() {
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
}
