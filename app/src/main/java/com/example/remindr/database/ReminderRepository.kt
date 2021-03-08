package com.example.remindr.database

import androidx.lifecycle.LiveData

class ReminderRepository(private val reminderDao: ReminderDao) {

    val allReminders: LiveData<List<Reminder>> = reminderDao.allReminders()

    suspend fun addReminder(reminder: Reminder) {
        reminderDao.addReminder(reminder)
    }

    suspend fun reminderById(id: String) {
        reminderDao.reminderById(id)
    }

    suspend fun  updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
    }

    suspend fun  updateReminderById(id: Int, reminder_seen: Boolean) {
        reminderDao.updateReminderById(id, reminder_seen)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }
}