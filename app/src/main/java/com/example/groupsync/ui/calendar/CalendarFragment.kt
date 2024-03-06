package com.example.groupsync.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.groupsync.databinding.FragmentCalendarBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CalendarFragment : Fragment() {
    private var binding: FragmentCalendarBinding? = null
    private var databaseReference: DatabaseReference? = null
    private var stringDateSelected: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseReference = FirebaseDatabase.getInstance().getReference("Calendar")
        binding!!.calendarView.setOnDateChangeListener { calendarView, i, i1, i2 ->
            stringDateSelected =
                Integer.toString(i) + Integer.toString(i1 + 1) + Integer.toString(
                    i2
                )
            calendarClicked()
        }
    }

    private fun calendarClicked() {
        databaseReference!!.child(stringDateSelected!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
//                        binding?.editText?.setText(snapshot.value.toString())
                    } else {
//                        binding?.editText?.setText("null")
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun buttonSaveEvent(view: View?) {
        databaseReference!!.child(stringDateSelected!!)
//            .setValue(binding?.editText?.getText().toString())
    }
}

