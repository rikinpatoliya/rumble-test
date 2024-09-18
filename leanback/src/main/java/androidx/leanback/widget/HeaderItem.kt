/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package androidx.leanback.widget

/**
 * A header item describes the metadata of a [Row], such as a category
 * of media items.  May be subclassed to add more information.
 */
open class HeaderItem
/**
 * Create a header item.  All fields are optional.
 */(
    /**
     * Returns a unique identifier for this item.
     */
    val id: Long,
    /**
     * Returns the name of this header item.
     */
    val name: String
) {

    /**
     * Returns the description for the current row.
     */
    /**
     * Sets the description for the current header item. This will be visible when
     * the row receives focus.
     */
    var description: CharSequence? = null
    /**
     * Returns optional content description for the HeaderItem.  When it is null, [.getName]
     * should be used for the content description.
     * @return Content description for the HeaderItem.
     */
    /**
     * Sets optional content description for the HeaderItem.
     * @param contentDescription Content description sets on the HeaderItem.
     */
    var contentDescription: CharSequence? = null

    /**
     * Create a header item.
     */
    constructor(name: String) : this(ObjectAdapter.NO_ID.toLong(), name)
}
