package com.cmput301_project;

/**
 * Created by Blaine on 02/03/2015.
 */

import org.junit.Test;
import com.cmput301_project.model.Tag;
import static org.junit.Assert.*;

public class TagTest {
    @Test
    public void testGetTag() {
        Tag tag = Tag.getTag("tag");
        assertEquals("tag", tag.getName());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetNullTag() {
        Tag tag = Tag.getTag(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testGetEmptyTag() {
        Tag tag = Tag.getTag("");
    }

    public void testRemoveTag() {

    }
}


