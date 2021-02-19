package com.example.remindr.auth

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.remindr.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*


class LoginFragment : Fragment(), View.OnClickListener {

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        view.findViewById<TextView>(R.id.register_btn_in_login).setOnClickListener(this)
        view.findViewById<Button>(R.id.login_btn).setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.register_btn_in_login -> navController!!.navigate(R.id.action_loginFragment_to_registerFragment)
            R.id.login_btn -> {
                when {
                    TextUtils.isEmpty(et_password_login.text.toString().trim {it <= ' '}) -> {
                        Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show()

                    }
                    TextUtils.isEmpty(et_email_login.text.toString().trim {it <= ' '}) -> {
                        Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show()

                    }
                    else -> {
                        val username: String = et_email_login.text.toString().trim {it <= ' ' }
                        val password: String = et_password_login.text.toString().trim {it <= ' ' }

                        FirebaseAuth.getInstance()
                                .signInWithEmailAndPassword(username, password)
                                .addOnCompleteListener(
                                        OnCompleteListener<AuthResult> { task ->
                                            if (task.isSuccessful) {
                                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                                navController!!.navigate(R.id.action_loginFragment_to_homeFragment)
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