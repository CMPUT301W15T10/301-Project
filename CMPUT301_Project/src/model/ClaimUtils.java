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

package model;

public final class ClaimUtils {
    private ClaimUtils() {
    }

    public static void nonNullOrThrow(Object object, String name) {
        if (object == null) {
            throw new IllegalArgumentException(name + " must not be null.");
        }
    }

    public static void nonNullnonEmptyOrThrow(String string, String name) {
        if (string == null || string.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be null or empty.");
        }
    }

    public static void nonNegativeOrThrow(long number, String name) {
        if (number < 0) {
            throw new IllegalArgumentException(name + " must not be negative.");
        }
    }
}
