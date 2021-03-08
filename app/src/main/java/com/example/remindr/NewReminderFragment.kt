package com.example.remindr

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.work.Data
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.remindr.database.Reminder
import com.example.remindr.database.ReminderViewModel
import kotlinx.android.synthetic.main.fragment_new_reminder.*
import kotlinx.coroutines.launch
import java.util.*
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit


class FullReminderFragment : Fragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var navController: NavController
    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var createdReminder:Reminder
    private var generatedId: Int = (0..10000).random()
    private lateinit var coordinateX: String
    private lateinit var coordinateY: String
    private lateinit var pickedImage: Uri
    private lateinit var cal: Calendar


    private var title = ""
    private var time = ""
    private var date = ""


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
        return inflater.inflate(R.layout.fragment_new_reminder, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        reminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)

        // OnClickListeners for clickables
        view.findViewById<LinearLayout>(R.id.layout_day_new_reminder).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.layout_time_new_reminder).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.layout_location_new_reminder).setOnClickListener(this)
        view.findViewById<LinearLayout>(R.id.layout_image_new_reminder).setOnClickListener(this)
        view.findViewById<Button>(R.id.create_button_new_reminder).setOnClickListener(this)

        // Arguments from google maps
        coordinateX = arguments?.getString("location_x").toString()
        coordinateY = arguments?.getString("location_y").toString()

        // Initialize input fields
        //tv_day_chosen_new_reminder.text = "$day.$month,$year"
        //tv_time_chosen_new_reminder.text = "$hour:$minute"
        //tv_location_chosen_new_reminder.text = "$coordinateX, $coordinateY"

        getDateTimeCalendar()
    }


    // Handle actions of clickable components
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.layout_day_new_reminder -> {
                getDateTimeCalendar()
                context?.let { DatePickerDialog(it, this, year, month, day).show() }

            }
            // Time field clicked
            R.id.layout_time_new_reminder -> {
                getDateTimeCalendar()
                context?.let { TimePickerDialog(it, this, hour, minute, true).show() }
            }
            // Location clicked
            // Open up Google Maps
            R.id.layout_location_new_reminder -> {
                val bundle = bundleOf("previous_location" to "new")
                navController.navigate(R.id.action_fullReminderFragment_to_mapsFragment, bundle)

            }

            // Image layout clicked
            R.id.layout_image_new_reminder -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (context?.let { context?.let { checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) } } == PackageManager.PERMISSION_DENIED) {
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestPermissions(permissions, PERMISSION_CODE)
                    } else {
                        chooseImageGallery();
                    }
                } else {
                    chooseImageGallery();
                }
            }

            // Create button clicked
            R.id.create_button_new_reminder -> {
                if (this::pickedImage.isInitialized) {
                    workManagerReminder()
                    insertDataToDatabase()
                    navController.navigate(R.id.action_fullReminderFragment_to_homeFragment)
                } else {
                    Toast.makeText(context, "Pick an image before create", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun workManagerReminder() {
        title = et_message_new_reminder.text.toString()
        println("------------------------")

        println(generatedId)
        println("------------------------")

        val reminderParameters = Data.Builder()
                .putInt("generated_ id", generatedId)
                .putString("generatedId", generatedId.toString())
                .putString("title", title)
                .putString("date", date)
                .putString("time", time)
                .build()

        val timeToSetOffAlarm = cal.timeInMillis - System.currentTimeMillis()
        println("------------------------")
        println(timeToSetOffAlarm)
        println(cal.timeInMillis)
        println(System.currentTimeMillis())
        println("------------------------")
        val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(reminderParameters)
            .setInitialDelay(timeToSetOffAlarm, TimeUnit.MILLISECONDS)
            .build()

        context?.let {
            WorkManager
                .getInstance(it)
                .enqueue(reminderRequest)
        }
    }


    // Add a new reminder to the database
    private fun insertDataToDatabase() {

        // Take values from input fields
        title = et_message_new_reminder.text.toString()
        time = tv_time_chosen_new_reminder.text.toString()
        date = tv_day_chosen_new_reminder.text.toString()

        val reminderSeen = false
        // Use android coroutine for executing database operations
        lifecycleScope.launch {
            val reminder = Reminder(generatedId, title, coordinateX, coordinateY, date, time, "null", reminderSeen, getBitmap())
            reminderViewModel.addReminder(reminder)
        }

        Toast.makeText(context, "A new reminder added successfully", Toast.LENGTH_SHORT).show()
    }


    private fun getDateTimeCalendar() {
        cal = Calendar.getInstance()
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
        cal.set(savedYear, savedMonth, savedDay, savedHour, savedMinute)
        println(savedMonth)
        val monthToDisplay = savedMonth + 1
        date = "$savedDay.$monthToDisplay.$savedYear"
        tv_day_chosen_new_reminder.text = date
    }

    @SuppressLint("SetTextI18n")
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        savedHour = hourOfDay
        savedMinute = minute
        getDateTimeCalendar()
        cal.set(savedYear, savedMonth, savedDay, savedHour, savedMinute)
        time = "$savedHour:$savedMinute"
        tv_time_chosen_new_reminder.text = time

    }


    // CODE FOR PICKING IMAGE FROM THE GALLERY
    // TODO TooLargeException is probably caused by this code ( Intent )

    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        this.startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImageGallery()
                } else {
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK){
            image_new_reminder.setImageURI(data?.data)
            pickedImage = data?.data!!
        }
    }



    // Build a bitmap from Uri data
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



    // Permission codes
    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
        private const val CHANNEL_ID = "REMINDR_APPLICATION_NOTIFICATION_CHANNEL"
        private const val notificationId = 101


        fun sendNot(context: Context, title: String, time: String) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("message", "message")
            val pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_ONE_SHOT)

            val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.woman)
            val smallIcon = BitmapFactory.decodeResource(context.resources, R.drawable.cat)


            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText("$title, $time")
                    .setLargeIcon(smallIcon)
                    .setStyle(NotificationCompat.BigPictureStyle().bigPicture(largeIcon))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setGroup(CHANNEL_ID)

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "notification_title"
                val descriptionText = "Notification_description"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply { description=descriptionText }
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(notificationId, notificationBuilder.build())



        }


    }

}