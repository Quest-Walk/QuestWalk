package com.hapataka.questwalk.core.local

import androidx.room.TypeConverter

class TypeConverter {
    @TypeConverter
    fun List<String>.toDataFromStringList() = this.joinToString(",")

    @TypeConverter
    fun String.toStringList() = if (this.isEmpty()) emptyList() else this.split(",")

    @TypeConverter
    fun List<Int>.toDataFromIntList() = this.joinToString(",")

    @TypeConverter
    fun String.toIntList() = if (this.isEmpty()) emptyList() else this.split(",").map { it.toInt() }
}