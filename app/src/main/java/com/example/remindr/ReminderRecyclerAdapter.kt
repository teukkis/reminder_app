package com.example.remindr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.ListFragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.remindr.database.Reminder
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
        this.items = reminderList
        notifyDataSetChanged()
    }

    class ReminderViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView) {
        val reminderMessage = itemView.reminder_title
        val reminderDate = itemView.card_layout_date
        val reminderTime = itemView.card_layout_time



        fun bind(reminder: Reminder) {
            reminderMessage.text = reminder.message
            reminderDate.text = reminder.date
            reminderTime.text = reminder.time

            itemView.rowLayout.setOnClickListener {
                val bundle = bundleOf("reminder" to reminder)
                itemView.findNavController().navigate(R.id.action_homeFragment_to_editReminderFragment, bundle)
            }
        }

    }
}