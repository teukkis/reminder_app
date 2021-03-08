package com.example.remindr.auth

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.remindr.R
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.tasks.OnCompleteListener
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigation
        navController = Navigation.findNavController(view)
        view.findViewById<TextView>(R.id.login_btn_in_register).setOnClickListener(this)

        // Form
        view.findViewById<Button>(R.id.register_btn).setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.login_btn_in_register -> navController.navigate(R.id.action_registerFragment_to_loginFragment)
            R.id.register_btn -> {
                when {
                    TextUtils.isEmpty(et_password_register.text.toString().trim {it <= ' '}) -> {
                        Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show()

                    }
                    TextUtils.isEmpty(et_email_register.text.toString().trim {it <= ' '}) -> {
                        Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show()

                    }
                    else -> {
                        val username: String = et_email_register.text.toString().trim {it <= ' ' }
                        val password: String = et_password_register.text.toString().trim {it <= ' ' }

                        FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(username, password)
                            .addOnCompleteListener(
                                OnCompleteListener<AuthResult> { task ->
                                    if (task.isSuccessful) {
                                        task.result!!.user!!
                                        Toast.makeText( context, "All good", Toast.LENGTH_SHORT).show()
                                        navController.navigate(R.id.action_registerFragment_to_loginFragment)
                                    }
                                    else {
                                        Toast.makeText(context, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                    }
                }
            }
        }
    }
}