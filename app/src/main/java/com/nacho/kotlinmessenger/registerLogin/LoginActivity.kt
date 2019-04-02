package com.nacho.kotlinmessenger.registerLogin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.nacho.kotlinmessenger.R
import com.nacho.kotlinmessenger.messages.LatestMessagesActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        buttonLogin.setOnClickListener {
            val email = editTextEmailLogin.text.toString()
            val password = editTextPasswordLogin.text.toString()

            Log.d("Jesus", "Attempt to login with toUser email: $email/***")

            if(email.isEmpty()||password.isEmpty()){
                Toast.makeText(this, "Email or password is empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this){task ->
                    if(task.isSuccessful){
                        Log.d("Jesus", "signInWithEmail:success")
                        Toast.makeText(baseContext, "Success.",
                        Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LatestMessagesActivity::class.java)
                        //clear rest of activities acumulated in stack, if any. Además, hace
                        //que al apretar el botón de back muestre el escritorio y no la activity previa (register o login)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)



                    }
                }
                .addOnFailureListener {
                    Log.d("Jesus", "signInWithEmail:failure -- " + it.message)
                    Toast.makeText(this, "signInWithEmail:failure -- ~${it.message}", Toast.LENGTH_SHORT).show()
                }

//            editTextEmailLogin.text.clear()
//            editTextPasswordLogin.text.clear()
        }

        textViewBackToRegis.setOnClickListener {
            finish()
        }
    }

}