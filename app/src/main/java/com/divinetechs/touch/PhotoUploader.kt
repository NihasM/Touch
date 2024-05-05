package com.divinetechs.touch

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

object PhotoUploader {

    fun uploadPhoto(
        context: Context,
        mAuth: FirebaseAuth,
        imageUri: Uri?,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        if (imageUri != null) {
            val mStorageRef = FirebaseStorage.getInstance().getReference("Users/${mAuth.currentUser?.uid}")
            mStorageRef.putFile(imageUri).addOnSuccessListener {
                Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                onSuccess.invoke()
            }.addOnFailureListener {
                Toast.makeText(context, "Image uploaded failed", Toast.LENGTH_SHORT).show()
                onFailure.invoke()
            }
        } else {
            onSuccess.invoke()
        }
    }
}

