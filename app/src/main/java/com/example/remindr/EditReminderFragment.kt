package com.example.remindr

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.remindr.database.Reminder
import com.example.remindr.database.ReminderViewModel
import kotlinx.android.synthetic.main.fragment_edit_reminder.*
import kotlinx.coroutines.launch
import java.util.*


class EditReminderFragment : Fragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var navController: NavController
    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var coordinateX: String
    private lateinit var coordinateY: String
    private lateinit var clickedReminder: Reminder

    private lateinit var pickedImage: Bitmap

    // variables for the calendar
    private var minute = 0
    private var hour = 0
    private var day = 0
    private var month = 0
    private var year = 0

    private var savedMinute = 0
    private var savedHour = 0
    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_reminder, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        reminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)

        view.findViewById<LinearLayout>(R.id.layout_time_edit_reminder).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.layout_day_edit_reminder).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.layout_location_edit_reminder).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.layout_image_edit_reminder).setOnClickListener(this)
        view.findViewById<Button>(R.id.update_button_edit_reminder).setOnClickListener(this)
        view.findViewById<Button>(R.id.delete_button_edit_reminder).setOnClickListener(this)



        coordinateX = arguments?.getString("location_x").toString()
        coordinateY = arguments?.getString("location_y").toString()
        clickedReminder = arguments?.getParcelable("reminder")!!

        if (coordinateX.startsWith("L")) {
            tv_location_chosen_edit_reminder.text = "$coordinateX, $coordinateY"
        } else {
            val x = clickedReminder.location_x
            val y = clickedReminder.location_y
            tv_location_chosen_edit_reminder.text = "$x, $y"

        }

        println()
        println(clickedReminder.location_x)

        getDateTimeCalendar()


        image_edit_reminder.setImageBitmap(clickedReminder.reminder_image)
        et_message_edit_reminder.setText(clickedReminder.message)
        tv_date_chosen_edit_reminder.text = clickedReminder.date
        tv_time_chosen_edit_reminder.text = clickedReminder.time
        pickedImage = clickedReminder.reminder_image
    }

    private fun getDateTimeCalendar() {
        val cal = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
    }

    @SuppressLint("SetTextI18n")
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        savedDay = dayOfMonth
        savedMonth = month
        savedYear = year
        getDateTimeCalendar()

        tv_date_chosen_edit_reminder.text = "$savedDay.$savedMonth.$savedYear"
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        getDateTimeCalendar()

        tv_time_chosen_edit_reminder.text = "$savedHour:$savedMinute"

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.layout_day_edit_reminder -> {
                getDateTimeCalendar()
                context?.let { DatePickerDialog(it, this, year, month, day).show() }

            }

            R.id.layout_time_edit_reminder -> {
                getDateTimeCalendar()
                context?.let { TimePickerDialog(it, this, hour, minute, true).show() }

            }

            R.id.layout_location_edit_reminder -> {
                val bundle = bundleOf("previous_location" to  "edit", "reminder" to clickedReminder)
                navController.navigate(R.id.action_editReminderFragment_to_mapsFragment, bundle)

            }


            R.id.layout_image_edit_reminder -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (context?.let { context?.let { checkSelfPermission(it, android.Manifest.permission.READ_EXTERNAL_STORAGE) } } == PackageManager.PERMISSION_DENIED){
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissions(permissions, PERMISSION_CODE)
                    } else{
                        //chooseImageGallery();
                    }
                }else{
                    //chooseImageGallery();
                }
            }

            R.id.update_button_edit_reminder -> {
                insertDataToDatabase()
                navController.navigate(R.id.action_editReminderFragment_to_homeFragment)
            }

            R.id.delete_button_edit_reminder -> {
                reminderViewModel.deleteReminder(clickedReminder)
                navController.navigate(R.id.action_editReminderFragment_to_homeFragment)
            }

        }
    }

    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    chooseImageGallery()
                }else{
                    Toast.makeText(context,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK){
            //image_edit_reminder.setImageURI(data?.data)
            //pickedImage = data?.data!!
        }
    }

    private fun insertDataToDatabase() {
        val title = et_message_edit_reminder.text.toString()
        val time = tv_time_chosen_edit_reminder.text.toString()
        val date = tv_date_chosen_edit_reminder.text.toString()

        lifecycleScope.launch {
            val reminder = Reminder(clickedReminder.id, title, coordinateX, coordinateY, date, time, "null", false, getBitmap())
            println(reminder)
            reminderViewModel.updateReminder(reminder)
        }
        Toast.makeText(context, "Reminder updated successfully", Toast.LENGTH_SHORT).show()
    }


    private suspend fun getBitmap(): Bitmap {
        val loading = context?.let { ImageLoader(it) }
        val request = context?.let {
            ImageRequest.Builder(it)
                    .data(pickedImage)
                    .build()
        }

        val result = (request?.let { loading?.execute(it) } as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
    }

}