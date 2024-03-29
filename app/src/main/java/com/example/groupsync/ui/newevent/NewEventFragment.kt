package com.example.groupsync.ui.newevent

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.groupsync.R
import com.example.groupsync.databinding.FragmentNeweventBinding
import com.example.groupsync.ui.gallery.Upload
import com.example.groupsync.ui.home.HomeViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class NewEventFragment : Fragment() {
    private var _binding: FragmentNeweventBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.

    private lateinit var mTitleField: EditText
    private lateinit var mDescField: EditText
    private lateinit var mChooseImgButton: MaterialButton
    private lateinit var mCreateEventButton: MaterialButton
    private lateinit var mImageView: ShapeableImageView

    private var mImageUri: Uri? = null

    private lateinit var mStorageRef: StorageReference
    private lateinit var mDatabaseRef: DatabaseReference
    private lateinit var mFirestoreRef: CollectionReference

    private var mUploadTask: StorageTask<UploadTask.TaskSnapshot>? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNeweventBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mTitleField = binding.textinputTitle
        mDescField = binding.textinputDescription
        mChooseImgButton = binding.buttonUploadCoverImage
        mCreateEventButton = binding.buttonCreateEvent
        mImageView = binding.eventCoverImage

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads")
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads")
        mFirestoreRef = FirebaseFirestore.getInstance().collection("events")

        mChooseImgButton.setOnClickListener {
            openFileChooser()
        }

        mCreateEventButton.setOnClickListener {
            if (mUploadTask != null && mUploadTask!!.isInProgress) {
                Toast.makeText(context, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else {
                createEvent()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getFileExtension(uri: Uri): String? {
        val cresolver = requireActivity().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cresolver.getType(uri))
    }

    public fun createEvent() {
        if (mTitleField.text.toString() == "" || mDescField.text.toString() == "") {
            Toast.makeText(context, "Ensure all fields are filled", Toast.LENGTH_SHORT).show()
            return
        }

        mImageUri?.let { uri ->
            val fileReference: StorageReference = mStorageRef.child(
                System.currentTimeMillis().toString() + "." + getFileExtension(uri)
            )

            mUploadTask = fileReference.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    Toast.makeText(context, "Upload Successful", Toast.LENGTH_LONG).show()

                    val upload = Upload(
                        taskSnapshot.uploadSessionUri.toString()
                    )
                    val uploadId = mDatabaseRef.push().key
                    mDatabaseRef.child(uploadId!!).setValue(upload)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }

            val urlTask = (mUploadTask as UploadTask).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                fileReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    mFirestoreRef.document().set(
                        hashMapOf(
                            "title" to mTitleField.text.toString(),
                            "description" to mDescField.text.toString(),
                            "imageUrl" to downloadUri.toString()
                        )
                    )
//                        .addOnSuccessListener {
//                            ViewModelProvider(this).get(HomeViewModel::class.java)
//                                .fetchDataFromFirestore();
//                        }
                    findNavController().navigateUp()

                }
            }
        } ?: run {
            Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

    public fun openFileChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            mImageUri = data.data

            Picasso.get().load(mImageUri).into(mImageView)
        }

    }
}