package com.example.groupsync.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.groupsync.databinding.FragmentAvailabilityBinding

class AvailabilityFragment : Fragment() {

    private var _binding: FragmentAvailabilityBinding? = null
    private val binding get() = _binding!!

    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUser: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAvailabilityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        currentUser = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        binding.btnSubmitAvailability.setOnClickListener { submitCheckBoxes() }
    }

    private fun submitCheckBoxes() {
        val availabilityMap = hashMapOf<String, Any>()
        val daysOfWeek = listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
        val hours = listOf("6pm", "7pm", "8pm", "9pm", "10pm", "11pm", "12am")

        daysOfWeek.forEach { day ->
            hours.forEach { hour ->
                val checkBoxId = resources.getIdentifier("cb_${day}_$hour", "id", requireContext().packageName)
                val checkBox = view?.findViewById(checkBoxId) as? CheckBox
                availabilityMap["$day/$hour"] = checkBox?.isChecked ?: false

            }
        }

        if (currentUser.isNotEmpty()) {
            firestore.collection("users").document(currentUser)
                .set(mapOf("availability" to availabilityMap))
                .addOnSuccessListener {
                    // Successfully updated Firestore
                    Toast.makeText(context, "Upload Successful", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    // Handle errors
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
