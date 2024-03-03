package com.example.groupsync.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groupsync.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

private var _binding: FragmentHomeBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val textView: TextView = binding.textHome
    homeViewModel.text.observe(viewLifecycleOwner) {
      textView.text = it
    }

      // Observe LiveData from ViewModel
      val recyclerView: RecyclerView = binding.recyclerView
      homeViewModel.events.observe(viewLifecycleOwner) { newList ->
          // Update UI with the new list of events
          Log.i("title", newList[0].title)
          val adapter = EventsAdapter(requireContext(), newList, homeViewModel)
          val manager = LinearLayoutManager(requireContext())
          recyclerView.setLayoutManager(manager)
          recyclerView.setHasFixedSize(true)
          recyclerView.adapter = adapter

          // Show empty text if no events
          textView.visibility = if (newList.isEmpty()) View.VISIBLE else View.INVISIBLE
      }

    return root
  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}