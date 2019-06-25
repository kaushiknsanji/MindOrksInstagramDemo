package com.mindorks.kaushiknsanji.instagram.demo.data.local.db

import androidx.room.TypeConverter
import java.util.*

/**
 * [TypeConverter] class to aid Room with complex data type conversions.
 *
 * @author Kaushik N Sanji
 */
class Converter {

    /**
     * For reading [Date] from the database.
     */
    @TypeConverter
    fun toDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }

    /**
     * For writing [Date] to the database.
     */
    @TypeConverter
    fun toTimestamp(date: Date?): Long? = date?.time

    /**
     * For reading [List] of table's primary ids from a table column of the database.
     */
    @TypeConverter
    fun toListOfIds(ids: String): List<Long> =
        ids.split(",").map { idStr: String -> idStr.trim().toLong() }

    /**
     * For writing [List] of table's primary ids to a table column of the database.
     */
    @TypeConverter
    fun toStringOfIds(idList: List<Long>?): String? =
        idList?.joinToString(separator = ",")

}