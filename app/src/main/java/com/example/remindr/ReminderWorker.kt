package com.example.remindr

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.remindr.database.Reminder
import com.example.remindr.database.ReminderDatabase
import com.example.remindr.database.ReminderViewModel


class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {




    override fun doWork(): Result {

        val reminderId = inputData.getString("generatedId")?.toInt()
        val title = inputData.getString("title").toString()
        val date = inputData.getString("date").toString()
        val time = inputData.getString("time").toString()

        val datetime = "$date, $time"

        println(datetime)
        println(reminderId)

        FullReminderFragment.sendNot(applicationContext, title, datetime)
        for (x in 0..100) {
            println(x)
        }

        if (reminderId != null) {
            ReminderDatabase.getDatabase(applicationContext).reminderDao().updateReminderById(reminderId, true)
        }

        return Result.success()

    }
}