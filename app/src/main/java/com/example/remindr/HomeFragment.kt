package com.example.remindr

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.remindr.database.ReminderViewModel
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import kotlinx.android.synthetic.main.fragment_home.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var reminderAdapter: ReminderRecyclerAdapter
    private lateinit var navController: NavController
    private lateinit var reminderViewModel: ReminderViewModel

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_open) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(context, R.anim.rotate_close) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        view.findViewById<BottomNavigationItemView>(R.id.logout).setOnClickListener(this)
        view.findViewById<BottomNavigationItemView>(R.id.profile).setOnClickListener(this)
        view.findViewById<FloatingActionButton>(R.id.addNewReminderFloatingButton).setOnClickListener(this)

        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            reminderAdapter = ReminderRecyclerAdapter()
            val topSpacingDecoration = TopSpacingItemDecoration(30)
            addItemDecoration(topSpacingDecoration)
            adapter = reminderAdapter
        }

        reminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)
        reminderViewModel.allReminders.observe(viewLifecycleOwner, Observer { reminder ->
            reminderAdapter.submitList(reminder)

        })

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.logout -> navController!!.navigate(R.id.action_homeFragment_to_loginFragment)
            R.id.profile -> navController!!.navigate(R.id.action_homeFragment_to_profileFragment)
            R.id.addNewReminderFloatingButton -> {
                addNewReminderFloatingButton.startAnimation(rotateOpen)
                addNewReminderFloatingButton.startAnimation(rotateClose)
                navController!!.navigate(R.id.action_homeFragment_to_fullReminderFragment)
            }
        }
    }
}