package com.example.remindr

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_profile.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController

    // Get the user profile from Firebase
    private val user = Firebase.auth.currentUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigation
        navController = Navigation.findNavController(view)

        // set event listeners on elements
        view.findViewById<TextView>(R.id.apply_changes_profile_button).setOnClickListener(this)

        user?.let {
            // Name, email address, and profile photo Url
            val email = user.email

            view.findViewById<EditText>(R.id.et_email_profile).setText(email)
        }



    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.apply_changes_profile_button -> {
                when {
                    TextUtils.isEmpty(et_email_profile.text.toString().trim {it <= ' '}) -> {
                        Toast.makeText(context, "Please enter username", Toast.LENGTH_SHORT).show()

                    }
                    TextUtils.isEmpty(et_password_profile.text.toString().trim {it <= ' '}) -> {
                        Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show()

                    }

                    else -> {
                        val email: String = et_email_profile.text.toString().trim {it <= ' ' }

                    /* // Take the first part of email as a username
                        val username = email.split(",@")[0]
                        val profileUpdates = userProfileChangeRequest {
                            displayName = username
                        }

                        user!!.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                }
                            }
                    */

                        user!!.updateEmail(email)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                }
                            }

                    }
                }
            }
        }
    }


}