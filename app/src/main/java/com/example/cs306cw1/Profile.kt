package com.example.cs306cw1

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.login.*
import kotlinx.android.synthetic.main.profile.*
import java.io.ByteArrayOutputStream

/**
 * Initialises profile activity
 */
class Profile : AppCompatActivity(){

    private val DEFAULT_IMAGE_URL = "https://picsum.photos/200"
    lateinit var mAuth: FirebaseAuth
    private val REQUEST_IMAGE_CAPTURE = 100
    private lateinit var imageUri: Uri

    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        val v = findViewById<FrameLayout>(R.id.profileLayout)
        currentUser?.let{user ->
            Glide.with(this)
                .load(user.photoUrl)
                .into(image_view)
            edit_text_name.setText(user.displayName)
            text_email.text = user.email

        }

        //opens camera
        image_view.setOnClickListener{
            takePictureIntent()
        }

        //saves the users options
        button_save.setOnClickListener{
            //Checks if user has a selected image
            val photo = when{
                ::imageUri.isInitialized -> imageUri
                currentUser?.photoUrl == null -> Uri.parse(DEFAULT_IMAGE_URL)
                else -> currentUser.photoUrl
            }

            val name = edit_text_name.text.toString().trim()

            //checks if the user entered text
            if(name.isEmpty()){
                edit_text_name.error = "name required"
                edit_text_name.requestFocus()
                return@setOnClickListener
            }

            //sets up profile change request
            val updates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(photo)
                .build()

            progressbar.visibility = View.VISIBLE

            //Commits the users profile updates to firebase
            currentUser?.updateProfile(updates)
                ?.addOnCompleteListener{task ->
                    progressbar.visibility = View.INVISIBLE
                    if(task.isSuccessful){
                        showMessage(v, getString(R.string.profUpdated))
                    } else {
                        showMessage(v, getString(R.string.updatefail))
                    }
                }

        }
    }

    /**
     * Opens camera
     */
    private fun takePictureIntent(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { pictureIntent ->
            pictureIntent.resolveActivity(this?.packageManager!!)?.also {
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    /**
     * Gets the result from the takePictureIntent
     * @param requestCode
     * @param resultCode
     * @param data the return data from the camera intent
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap
            uploadImageAndSaveUri(imageBitmap)
        }
    }

    /**
     * Uploads the image to Firebase storage and assigns it to the user
     */
    private fun uploadImageAndSaveUri(bitmap: Bitmap){
        val baos = ByteArrayOutputStream()
        //get storage reference from firebase
        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("pics/${FirebaseAuth.getInstance().currentUser?.uid}")
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        val upload = storageRef.putBytes(image)
        //enable progress bar
        progressbar_pic.visibility = View.VISIBLE
        //upload image
        upload.addOnCompleteListener{uploadTask ->
            progressbar_pic.visibility = View.INVISIBLE
            if (uploadTask.isSuccessful){
                storageRef.downloadUrl.addOnCompleteListener{urlTask ->
                    urlTask.result?.let {
                        imageUri = it
                        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                        image_view.setImageBitmap(bitmap)
                    }
                }
            } else {
                uploadTask.exception?.let{
                    Toast.makeText(this, "Fail", Toast.LENGTH_LONG).show()
                }
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