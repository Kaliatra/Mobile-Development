package com.dicoding.kaliatra.ui.dictionary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.SearchView
import com.dicoding.kaliatra.databinding.FragmentDictionaryBinding

class DictionaryFragment : Fragment() {

    private lateinit var binding: FragmentDictionaryBinding
    private val dictionaryViewModel: DictionaryViewModel by viewModels()
    private val dictionaryAdapter = DictionaryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDictionaryBinding.inflate(inflater, container, false)

        binding.recyclerViewDictionary.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewDictionary.adapter = dictionaryAdapter

        dictionaryViewModel.filteredEntries.observe(viewLifecycleOwner) { entries ->
            dictionaryAdapter.submitList(entries)

            val layoutManager = binding.recyclerViewDictionary.layoutManager as LinearLayoutManager

            // Search for the first exact match based on 'tulisanlatin'
            val firstExactMatchPosition = entries.indexOfFirst {
                if (it is DictionaryItem.DictionaryEntry) {
                    it.data.tulisanlatin.equals(binding.searchView.query.toString(), ignoreCase = true)
                } else {
                    false
                }
            }

            if (firstExactMatchPosition != -1) {
                layoutManager.scrollToPositionWithOffset(firstExactMatchPosition, 0)
            }
        }

        dictionaryViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        dictionaryViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        dictionaryViewModel.fetchAllDictionaryEntries()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    dictionaryViewModel.filterDictionaryEntries(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    dictionaryViewModel.filterDictionaryEntries(it)
                }
                return false
            }
        })

        return binding.root
    }
}
