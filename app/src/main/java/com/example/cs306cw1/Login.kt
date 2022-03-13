package com.example.cs306cw1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.login.*


class Login : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        mAuth = FirebaseAuth.getInstance()

        //Set id's
        val emailText = findViewById<EditText>(R.id.input_email)
        val passText = findViewById<EditText>(R.id.input_password)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        val new_email = findViewById<EditText>(R.id.new_email)
        val new_password = findViewById<EditText>(R.id.new_password)

        //Checks User input then logs user in if correct
        btnLogin.setOnClickListener{
            val email = emailText.text.toString()
            val password = passText.text.toString()

            if (TextUtils.isEmpty(email)){
                emailText.error = getString(R.string.enterEmail)
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)){
                passText.error = getString(R.string.enterPword)
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        //Toggles create new user UI to visible
        btnCreate.setOnClickListener{
            newPword.visibility = if (newPword.visibility == View.INVISIBLE){
                View.VISIBLE
            } else{
                View.INVISIBLE
            }
            newEmail.visibility = if (newEmail.visibility == View.INVISIBLE){
                View.VISIBLE
            } else{
                View.INVISIBLE
            }
            btnAddUser.visibility = if (btnAddUser.visibility == View.INVISIBLE){
                View.VISIBLE
            } else{
                View.INVISIBLE
            }
        }

        //Creates new user using input params
        btnAddUser.setOnClickListener{
            val nEmail = new_email.text.toString()
            val nPassword = new_password.text.toString()

            if (!TextUtils.isEmpty(nEmail)&&!TextUtils.isEmpty(nPassword)){
                createNewUser(nEmail, nPassword)
            }
        }

    }

    /**
     * Logs in firebase user if they exist.
     * @param email users email
     * @param password  users password
     */

    private fun loginUser(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this){ task ->
            if (task.isSuccessful){
                val startIntent = Intent(this, MainActivity::class.java)
                startActivity(startIntent)
                finish()
            } else {
                val v =findViewById<ConstraintLayout>(R.id.loginLayout)
                showMessage(v, getString(R.string.authFail) + " .${task.exception}")

            }
        }
    }

    /**
     * Creates new user
     * @param email new users email
     * @param password new users password
     */
    private fun createNewUser(email: String, password: String){
        val v =findViewById<ConstraintLayout>(R.id.loginLayout)
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i("TAG", "createUserWithEmail:success")
                    val user = mAuth.currentUser
                    showMessage(v, getString(R.string.newUser))
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("User", user)
                    startActivity(intent)


                } else {
                    // If sign in fails, display a message to the user.
                    Log.i("TAG", "createUserWithEmail:failure", task.exception)
                    showMessage(v, getString(R.string.authFail))

                }
            }
    }

    /**
     * Creates snackbar showing displayed message
     * @param view view the message will be displayed on
     * @param message message to be displayed
     */
    private fun showMessage(view: View, message: String){
        Snackbar.make(view, message, Snackbar .LENGTH_LONG).show()
    }

}


