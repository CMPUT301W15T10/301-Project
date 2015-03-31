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

package com.cmput301.cs.project.models;

/**
 * A class that can be called to throw exceptions that can be raised by improper input.
 *
 * @author rozsa
 */

public final class ClaimUtils {
    private ClaimUtils() {
        throw new AssertionError("util class");
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
     * stringThatCantBeNull.isEmpty();  // always false
     * </pre>
     *
     * @param string the {@code String} to check for nullity
     * @param name   the name of the object (for Exception message)
     * @return the original {@code String}, if not null or empty
     */
    public static String nonNullnonEmptyOrThrow(String string, String name) {
        if (string == null || string.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be null or empty.");
        }
        return string;
    }
}
