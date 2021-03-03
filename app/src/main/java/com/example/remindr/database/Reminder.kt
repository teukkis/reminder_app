package com.example.remindr.database

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Parcelize
@Entity(tableName = "reminders_table")
data class Reminder (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "location_x") val location_x: String?,
    @ColumnInfo(name = "location_y") val location_y: String?,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "time") val time: String,
    @ColumnInfo(name = "creator_id") val creator_id: String?,
    @ColumnInfo(name = "reminder_seen") val reminder_seen: String?,
    @ColumnInfo(name = "reminder_image") val reminder_image: Bitmap
): Parcelable