package com.example.groupsync.ui.event

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.groupsync.MainActivity
import com.example.groupsync.R
import com.example.groupsync.databinding.FragmentEventdetailsBinding
import com.example.groupsync.ui.home.EventMetadata
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class EventDetailsFragment : Fragment() {
    private var _binding: FragmentEventdetailsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEventdetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val firestoreId = arguments?.getString("firestoreId")
        if (!firestoreId.isNullOrEmpty()) {
            val metadata = fetchFirestoreEventData(firestoreId)
        }

        // Set up navigation buttons for Gallery and Images
        val bundle = Bundle()
        bundle.putString("firestoreId", firestoreId)
        binding.galleryButton.setOnClickListener{
            findNavController().navigate(R.id.nav_gallery, bundle)
        }
        binding.imagesButton.setOnClickListener{
            findNavController().navigate(R.id.nav_images, bundle)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchFirestoreEventData(id: String) {
        db.collection("events")
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                    val metadata = EventMetadata(
                        id,
                        document.data?.get("title").toString(),
                        document.data?.get("description").toString(),
                        document.data?.get("imageUrl").toString()
                    )

                    (activity as MainActivity).supportActionBar!!.title = metadata.title;
                    binding.detailsTitle.text = metadata.title
                    binding.detailsDesc.text = metadata.subtitle
                    Picasso.get().load(metadata.image).into(binding.detailsImage)
            }
    }
}