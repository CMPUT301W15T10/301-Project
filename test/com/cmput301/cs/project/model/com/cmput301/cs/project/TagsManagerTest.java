package com.cmput301.cs.project;

import com.cmput301.cs.project.MockClaimSaves;
import com.cmput301.cs.project.controllers.TagsManager;
import com.cmput301.cs.project.model.Tag;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TagsManagerTest {

    private TagsManager mManager;

    @Before
    public void setup() {
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
}
