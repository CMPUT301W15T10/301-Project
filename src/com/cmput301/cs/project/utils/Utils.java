/*
 * Copyright 2015 Edmond Chui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cmput301.cs.project.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.models.Claim;

/**
 * Utility class that has various methods that are used in the app. <p>

 * @author rozsa
 *
 */

public final class Utils {
    private Utils() {
        throw new UnsupportedOperationException("utils class");
    }

    /**
     * Creates a Discard/Done bar in an activity and allows OnClickListeners to be attached.<p>
     * @param activity non-null
     * @param discardListener listener for the discard button; can be null
     * @param doneListener listener for the done button; can be null
     */
    
    public static void setupDiscardDoneBar(Activity activity, View.OnClickListener discardListener, View.OnClickListener doneListener) {
        final ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM, ActionBar.DISPLAY_SHOW_CUSTOM
                    | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

            final LayoutInflater inflater = LayoutInflater.from(actionBar.getThemedContext());
            final View customView = inflater.inflate(R.layout.ab_done_discard, null);
            actionBar.setCustomView(customView, new ActionBar.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            customView.findViewById(R.id.discard).setOnClickListener(discardListener);
            customView.findViewById(R.id.done).setOnClickListener(doneListener);
        }
    }

    /**
     * Returns value for Status that can be used to display the status. <p>
     *
     * @param status non-null
     * @return Android resource string id
     */
    
    public static int stringIdForClaimStatus(Claim.Status status) {
        switch (status) {
            case IN_PROGRESS:
                return R.string.in_progress;
            case SUBMITTED:
                return R.string.submitted;
            case RETURNED:
                return R.string.returned;
            case APPROVED:
                return R.string.approved;
            default:
                throw new AssertionError("unexpected status: " + status);
        }
    }

    /**
     * Ensures the object is not null, then returns the same reference. Otherwise, an {@code IllegalArgumentException} is thrown with the name given.
     * <p>
     * Example:
     * <pre>
     * final String stringThatMaybeNull = …;
     * final String stringThatCantBeNull = ClaimUtils.nonNullOrThrow(stringThatMaybeNull, "s");
     * stringThatCantBeNull.trim();  // this line will never crash from NullPointerException
     * </pre>
     *
     * @param object the object to check for nullity
     * @param name   the name of the object (for Exception message)
     * @param <T>    any object type, it will be the return type
     * @return the original object, if not null
     */
    public static <T> T nonNullOrThrow(T object, String name) {
        if (object == null) {
            throw new IllegalArgumentException(name + " must not be null.");
        }
        return object;
    }

    /**
     * Ensures the {@code String} is not null or empty, then returns the same {@code String}.
     * Otherwise, an {@code IllegalArgumentException} is thrown with the name given.
     * <p>
     * Example:
     * <pre>
     * final String stringThatMaybeNullOrEmpty = …;
     * final String stringThatCantBeNullOrEmpty = ClaimUtils.nonNullOrThrow(stringThatMaybeNullOrEmpty, "s");
     * stringThatCantBeNull.isEmpty();  // always false (and won't crash)
     * </pre>
     *
     * @param string the {@code String} to check for nullity
     * @param name   the name of the object (for Exception message)
     * @return the original {@code String}, if not null or empty
     */
    public static String nonNullNonEmptyOrThrow(String string, String name) {
        if (string == null || string.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be null or empty.");
        }
        return string;
    }
}
