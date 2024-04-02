package com.example.groupsync.ui.event

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.groupsync.databinding.FragmentInviteBinding
import com.example.groupsync.ui.home.EventMetadata
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class InviteFragment : Fragment() {
    private var _binding: FragmentInviteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentInviteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val firestoreId = arguments?.getString("firestoreId")
        if (!firestoreId.isNullOrEmpty()) {
            val metadata = fetchFirestoreEventData(firestoreId)
        }

        binding.copyButton.setOnClickListener { onCodeCopied() }
        binding.shareButton.setOnClickListener { onCodeShared() }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchFirestoreEventData(id: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db.collection("events").document(id)
                .get()
                .addOnSuccessListener { document ->
                    val metadata = EventMetadata(
                        id = id,
                        inviteCode = document.data?.get("inviteCode").toString()
                    )

                    binding.inviteCode.text = metadata.inviteCode
                    return@addOnSuccessListener // Exit the loop after finding the matching document
                }
                .addOnFailureListener { e ->
                    // Handle failure to fetch event data
                }
        }
    }

    private fun onCodeCopied() {
        val code = binding.inviteCode.text

        val clipboard =
            getSystemService(requireContext(), ClipboardManager::class.java) as ClipboardManager
        val clip = ClipData.newPlainText("Invite Code", code)
        clipboard.setPrimaryClip(clip)
    }

    private fun onCodeShared() {
        val code = "Join my event on GroupSync! Here's the code: ${binding.inviteCode.text}"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, code)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}