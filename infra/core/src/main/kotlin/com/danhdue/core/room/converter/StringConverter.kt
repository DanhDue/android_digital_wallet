/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.core.room.converter

import androidx.room.TypeConverter
import com.danhdue.core.extension.fromJson
import com.danhdue.core.extension.toJson

class StringConverter {
    @TypeConverter
    fun toListOfStrings(stringValue: String): List<String>? = stringValue.fromJson()

    @TypeConverter
    fun fromListOfStrings(listOfString: List<String>?): String = listOfString.toJson()
}
