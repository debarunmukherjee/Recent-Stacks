package com.example.recentstacks.db

import androidx.room.TypeConverter
import com.example.recentstacks.models.Owner
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromOwnerToString(source: Owner): String {
        val gson = Gson()
        return gson.toJson(source)
    }

    @TypeConverter
    fun fromStringToOwner(source: String): Owner {
        val gson = Gson()
        return gson.fromJson(source, Owner::class.java)
    }

    @TypeConverter
    fun fromListToString(source: List<String>): String {
        val gson = Gson()
        return gson.toJson(source)
    }

    @TypeConverter
    fun fromStringToList(source: String): List<String> {
        val gson = Gson()
        return gson.fromJson(source, Array<String>::class.java).toList()
    }
}