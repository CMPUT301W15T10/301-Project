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

package com.cmput301.cs.project;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Stub class that contains empty implementations of {@link TextWatcher}.
 * All methods are safe to override without calling super.
 * <p/>
 * <pre>
 * final TextView textView = …;
 * textView.addTextChangedListener(new TextWatcherAdapter() {
 *    {@code @Override}
 *     public void afterTextChanged(Editable s) {
 *         final String string = s.toString();
 *         …
 *     }
 * });
 * </pre>
 * instead of
 * <pre>
 * final TextView textView = …;
 * textView.addTextChangedListener(new TextWatcher() {
 *    {@code @Override}
 *     public void beforeTextChanged(CharSequence s, int start, int count, int after) {
 *         // do nothing
 *     }
 *
 *    {@code @Override}
 *     public void onTextChanged(CharSequence s, int start, int before, int count) {
 *         // do nothing
 *     }
 *
 *    {@code @Override}
 *     public void afterTextChanged(Editable s) {
 *         final String string = s.toString();
 *         …
 *     }
 * });
 * </pre>
 */
public abstract class TextWatcherAdapter implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        // do nothing
    }
}
