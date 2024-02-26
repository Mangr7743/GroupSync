package com.example.groupsync.ui.gallery

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.groupsync.R
import com.example.groupsync.databinding.FragmentGalleryBinding
import com.squareup.picasso.Picasso

class GalleryFragment : Fragment() {

    private lateinit var mButtonChooseImage: Button
    private lateinit var mButtonUpload: Button
    private lateinit var mTextViewShowUploads: TextView
    private lateinit var mEditTextFileName: EditText
    private lateinit var mImageView: ImageView
    private lateinit var mProgressBar: ProgressBar

    private var mImageUri: Uri? = null


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
    mTextViewShowUploads = binding.textViewShowUploads
    mEditTextFileName = binding.editTextFileName
    mImageView = binding.imageView
    mProgressBar = binding.progressBar

    mButtonChooseImage.setOnClickListener {
        openFileChooser()
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