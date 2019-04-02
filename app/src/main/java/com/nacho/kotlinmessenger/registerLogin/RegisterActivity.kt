package com.nacho.kotlinmessenger.registerLogin

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.nacho.kotlinmessenger.R
import com.nacho.kotlinmessenger.models.User
import com.nacho.kotlinmessenger.messages.LatestMessagesActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextUsername.requestFocus()

       buttonRegister.setOnClickListener {
          performRegister()

       }

        textViewAlreadyHaveAccount.setOnClickListener {
            Log.d("Jesus", "Try to open log in activity")
            // iniciar activity login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        buttonPhoto.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)


            Log.d("Jesus", "Attempt to show photo selector")

        }
    }

    var selectedPhotoUri : Uri? = null;
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode == Activity.RESULT_OK && data != null){

            Log.d("Jesus", "Photo was selected")
                //procedemos a chekear que imagen fue seleccionada si es que se devolvió algunos datos
                //en el resultado de la activity llamada al clickear el boton para elegir foto

            //una 'uri' es básicamente una url que representa la ruta en que se almacena una foto en el dispositivo
             selectedPhotoUri = data.data
            //obtenemos la foto con la clase MediaStore y la guardamos en una variable bitmap
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            photo_imageview_register.setImageBitmap(bitmap) // ---------> Usamos un método más moderno para el photo selector,
                                                            // pues los BitmapDrawable comentados más abajo estan deprecated por la API,
                                                            // además utilizamos una dependencia de terceros CircleView, más apropiada
                                                            // para la funcionalidad que se busca que un button modicficado con un <shape> como background

            //también se debe ocultar el botón
            buttonPhoto.alpha = 0f


//            val bitmapDrawable = BitmapDrawable(bitmap)
//            buttonPhoto.setBackgroundDrawable(bitmapDrawable)

            }
    }

    private fun performRegister() {
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        Log.d("Jesus", "Email is: " + email)
        Log.d("Jesus", "Password: $password")

        if(email.isEmpty()||password.isEmpty()){
            Toast.makeText(this, "Email or password is empty",Toast.LENGTH_SHORT).show()
            return
        }



        // Autenticación con Firebase para crear toUser con email y password
        val auth : FirebaseAuth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task ->

                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in toUser's information
                    Log.d("Jesus", "User succesfully created with uid: ${task.result?.user?.uid}")
                    Toast.makeText(
                        this, "Authentication succesful!",
                        Toast.LENGTH_SHORT
                    ).show()
                //Luego de crear el usuario subimos la foto de perfil a FirebaseStore
                    uploadImageToFirebaseStore()

                }

            }
            .addOnFailureListener {
                Log.d("Jesus", "Failed to create toUser: ${it.message}")
                Toast.makeText(this, "Failed to create toUser: ${it.message}",Toast.LENGTH_SHORT).show()

            }


//        editTextEmail.text.clear()
//        editTextPassword.text.clear()
//        editTextUsername.text.clear()
       // buttonPhoto.setBackgroundResource(R.drawable.rounded_button_oval)
        editTextUsername.requestFocus()
    }

    private fun uploadImageToFirebaseStore() {
        if(selectedPhotoUri == null) return
        //UUID: identificador único (aleatorio en este caso)
        val filename =UUID.randomUUID().toString()
       val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        //metemos la uri de la foto en la referencia de furebase storage
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Jesus", "Photo uploaded successfully: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("Jesus", "Photo location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }

    }

    private fun saveUserToFirebaseDatabase(profleImageUrl: String?) {


        val uid = FirebaseAuth.getInstance().uid ?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, editTextUsername.text.toString(), profleImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Jesus","User saved to Firebase Database")
                //vamos a la activity de los mensajes
                val intent = Intent(this, LatestMessagesActivity::class.java)
                //clear rest of activities acumulated in stack, if any. Además, hace
                //que al apretar el botón de back muestre el escritorio y no la activity previa (register o login)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d("Jesus","Error at saving toUser: ${it.message}")
            }
    }


}



