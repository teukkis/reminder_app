package com.example.remindr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.remindr.models.Reminder
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.layout_reminder_list_item.view.*

class ReminderRecyclerAdapter  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<Reminder> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReminderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_reminder_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ReminderViewHolder -> {
                holder.bind(items[position])
            }
        }
    }

    fun submitList(reminderList: List<Reminder>){
        items = reminderList
    }

    class ReminderViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        val reminderTitle = itemView.reminder_title
        val reminderTime = itemView.reminder_time

        fun bind(reminder: Reminder) {
            reminderTitle.setText(reminder.title)
            reminderTime.setText(reminder.time)
        }
    }
}