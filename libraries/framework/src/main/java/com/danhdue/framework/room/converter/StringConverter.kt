/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.room.converter

import androidx.room.TypeConverter
import com.danhdue.framework.extension.fromJson
import com.danhdue.framework.extension.toJson

class StringConverter {
    @TypeConverter
    fun toListOfStrings(stringValue: String): List<String>? = stringValue.fromJson()

    @TypeConverter
    fun fromListOfStrings(listOfString: List<String>?): String = listOfString.toJson()
}
