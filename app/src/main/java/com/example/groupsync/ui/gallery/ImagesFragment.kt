package com.example.groupsync.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groupsync.databinding.FragmentImagesBinding
import com.google.firebase.database.*

class ImagesFragment : Fragment() {

    private var _binding: FragmentImagesBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseRef: DatabaseReference
    private lateinit var imageAdapter: ImageAdapter
    private var uploads: MutableList<Upload> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads")
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.setHasFixedSize(true)
        imageAdapter = ImageAdapter(requireContext(), mutableListOf())
        binding.recyclerView.adapter = imageAdapter

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                uploads.clear()
                for (postSnapshot in snapshot.children) {
                    val upload = postSnapshot.getValue(Upload::class.java)
                    upload?.let { uploads.add(it) }
                }
                if (isAdded) {
                    imageAdapter = ImageAdapter(requireContext(), uploads)
                    binding.recyclerView.adapter = imageAdapter
                }
                binding.progressCircle.setVisibility(View.INVISIBLE);
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
                Toast.makeText(context, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                binding.progressCircle.setVisibility(View.INVISIBLE);
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}