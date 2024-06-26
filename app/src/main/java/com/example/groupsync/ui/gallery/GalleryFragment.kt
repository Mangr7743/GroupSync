package com.example.groupsync.ui.gallery

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.groupsync.databinding.FragmentGalleryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class GalleryFragment : Fragment() {

    private lateinit var mButtonChooseImage: Button
    private lateinit var mButtonUpload: Button
    //private lateinit var mTextViewShowUploads: TextView
    private lateinit var mEditTextFileName: EditText
    private lateinit var mImageView: ImageView
    private lateinit var mProgressBar: ProgressBar

    private var mImageUri: Uri? = null

    private lateinit var mStorageRef: StorageReference
    private lateinit var mDatabaseRef: DatabaseReference

    private var mUploadTask: StorageTask<UploadTask.TaskSnapshot>? = null


    private var _binding: FragmentGalleryBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    //val galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
    //val view = inflater.inflate(R.layout.fragment_gallery, container, false)
      _binding = FragmentGalleryBinding.inflate(inflater, container, false)
      val root: View = binding.root

    mButtonChooseImage = binding.buttonChooseImage
    mButtonUpload = binding.buttonUpload
    mEditTextFileName = binding.editTextFileName
    mImageView = binding.imageView
    mProgressBar = binding.progressBar

    mStorageRef = FirebaseStorage.getInstance().getReference("uploads")
    mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads")

    mButtonChooseImage.setOnClickListener {
        openFileChooser()
    }

    mButtonUpload.setOnClickListener {
        if (mUploadTask != null && mUploadTask!!.isInProgress) {
            Toast.makeText(context, "Upload in progress", Toast.LENGTH_SHORT).show()
        } else {
            if (isAdded){
                uploadFile()
            }
        }
    }

    return root
  }

    private fun openFileChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(intent, 1)
    }

    private fun getFileExtension(uri: Uri): String? {
        val cresolver = requireActivity().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cresolver.getType(uri))
    }
    private fun uploadFile() {
        // Retrieve the event document id from arguments. See
        // EventDetailsFragment.kt::onCreateView() for more details.

        val firestoreId = arguments?.getString("firestoreId")
        Log.i("fire", firestoreId!!)

        if(!firestoreId.isNullOrEmpty()) {

            // Ensure the user is authenticated to upload an image.
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {

                mImageUri?.let { uri ->
                    val fileExtension = getFileExtension(uri)
                    // Storage looks like:
                    //  uploads
                    //  |_  <userId>
                    //      |_ <timestamp>.png
                    // We store images by user who uploaded, but references to them in the db will
                    // be by event id.
                    val fileReference: StorageReference =
                        mStorageRef.child("$userId/${System.currentTimeMillis()}.$fileExtension")

                    mUploadTask = fileReference.putFile(uri)
                        .addOnSuccessListener { taskSnapshot ->
                            // Reset progress bar after a delay to show completion
                            Handler().postDelayed({ mProgressBar.progress = 0 }, 500)

                            // Update the Realtime Database to get a reference to this image.
                            taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                                // Realtime Database for images looks like:
                                //  uploads
                                //  |_  <firebaseId> (this represents an event)
                                //      |_  <uploadId> (uniquely generated per image)
                                //          |_ name: ...
                                //          |_ imageUrl: ...

                                val upload = Upload(
                                    mEditTextFileName.text.toString().trim(),
                                    downloadUri.toString()
                                )
                                val uploadId = mDatabaseRef.child(firestoreId!!).push().key
                                mDatabaseRef.child(firestoreId).child(uploadId!!).setValue(upload)

                                Toast.makeText(context, "Upload Successful", Toast.LENGTH_LONG)
                                    .show()

                                activity?.finish()
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                        .addOnProgressListener { taskSnapshot ->
                            val progress =
                                (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                            mProgressBar.progress = progress.toInt()
                        }
                } ?: run {
                    Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show()
                }
            } else {
                // User is not authenticated, handle accordingly
                Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            mImageUri = data.data

            Picasso.get().load(mImageUri).into(mImageView)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}