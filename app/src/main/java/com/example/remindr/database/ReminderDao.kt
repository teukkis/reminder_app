package com.example.remindr.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminder(reminder: Reminder)

    @Query("SELECT * FROM reminders_table ORDER BY id ASC")
    fun allReminders(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminders_table WHERE id=:id")
    fun reminderById(id: String): LiveData<Reminder>

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Delete
    suspend fun deleteReminder(reminder: Reminder)
}