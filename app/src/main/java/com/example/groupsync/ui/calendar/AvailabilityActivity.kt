package com.example.groupsync.ui.calendar

import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AvailabilityActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_availability)

        val submitButton = findViewById<Button>(R.id.btn_submit_availability)
        submitButton.setOnClickListener {
            submitAvailability()
        }
    }

    private fun submitAvailability() {
        val user = FirebaseAuth.getInstance().currentUser
        val db = FirebaseFirestore.getInstance()

        if (user != null) {
            val userId = user.uid
            val availability = hashMapOf<String, Any>()

            // Example: Collecting availability for Monday 9AM
            val monday9am = findViewById<CheckBox>(R.id.cb_monday_9am).isChecked
            availability["Monday_9AM"] = monday9am

            // Repeat for other checkboxes/days/hours

            db.collection("users").document(userId).collection("availability")
                .document("this_week").set(availability)
                .addOnSuccessListener {
                    // Handle success, e.g., notify the user
                }
                .addOnFailureListener {
                    // Handle failure, e.g., notify the user
                }
        }
    }
}
