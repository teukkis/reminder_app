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

    @Query("UPDATE reminders_table SET reminder_seen=:reminder_seen WHERE id = :id")
    fun updateReminderById(id: Int, reminder_seen: Boolean): Int

    @Delete
    suspend fun deleteReminder(reminder: Reminder)
}