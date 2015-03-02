package model;

import java.util.*;

/**
 * Created by Blaine on 02/03/2015.
 */

/*
    Tags ARE mutable.

 */


public class Tag extends Observable {

    private static List<Tag> tags = new ArrayList<Tag>();

    private String name = "";
    private boolean deleted = false;

    private Tag(String name) {
        this.name = name;
        Tag.tags.add(this);
    }

    public void rename(String name) {
        this.name = name;
        notifyObservers();
    }

    public boolean isDeleted() {
        return deleted;
    }

    /*
     * Sets this tag as deleted, invalidating it. Notifies observers so they can delete it
     */
    public void delete() {
        this.deleted = true;
        Tag.tags.remove(this);
        notifyObservers();
    }

    public String getName() {
        return this.name;
    }


    /*
        Returns a tag with the given name

        Uses a static builder so we have no copies of tags
     */
    public static Tag getTag(String name) {

        for (Tag tag : Tag.tags){
            if(tag.getName().equals(name)){
                return tag;
            }
        }

        Tag tag = new Tag(name);
        return tag;
    }

}
