package com.cmput301_project.test;

/**
 * Created by Blaine on 02/03/2015.
 */

import junit.framework.TestCase;
import model.Tag;

public class TagTest extends TestCase {

    public void testGetTag() {

        Tag tag = Tag.getTag("tag");
        assertEquals("tag", tag.getName());
    }

    public void testRemoveTag() {

    }
}


