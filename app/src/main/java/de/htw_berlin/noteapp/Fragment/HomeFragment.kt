package de.htw_berlin.noteapp.Fragment

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import de.htw_berlin.noteapp.Adapter.NoteAdapter
import de.htw_berlin.noteapp.Model.Notes
import de.htw_berlin.noteapp.R
import de.htw_berlin.noteapp.ViewModel.NoteViewModel
import de.htw_berlin.noteapp.databinding.FragmentHomeBinding


class HomeFragment: Fragment() {

    lateinit var binding: FragmentHomeBinding
    val viewModel: NoteViewModel by viewModels()
    var oldNote = arrayListOf<Notes>()
    lateinit var adapter: NoteAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)

        viewModel.getNotes().observe(viewLifecycleOwner, { notesList ->
            val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            binding.recyclerViewNote.layoutManager = staggeredGridLayoutManager
            oldNote = notesList as ArrayList<Notes>
            adapter = NoteAdapter(requireContext(), notesList)
            binding.recyclerViewNote.adapter = adapter
        })


        binding.btnHigh.setOnClickListener {

            viewModel.getHighNote().observe(viewLifecycleOwner, { notesList ->
                oldNote = notesList as ArrayList<Notes>
                adapter = NoteAdapter(requireContext(), notesList)
                val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                binding.recyclerViewNote.layoutManager = staggeredGridLayoutManager
                binding.recyclerViewNote.adapter = adapter
                binding.btnHigh.setBackgroundResource(R.drawable.filter_selected)
                binding.btnMedium.setBackgroundResource(R.drawable.filter_template)
                binding.btnLow.setBackgroundResource(R.drawable.filter_template)
            })
        }

        binding.btnMedium.setOnClickListener {

            viewModel.getMediumNote().observe(viewLifecycleOwner, { notesList ->
                oldNote = notesList as ArrayList<Notes>
                adapter = NoteAdapter(requireContext(), notesList)
                val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                binding.recyclerViewNote.layoutManager = staggeredGridLayoutManager
                binding.recyclerViewNote.adapter = adapter
                binding.btnHigh.setBackgroundResource(R.drawable.filter_template)
                binding.btnMedium.setBackgroundResource(R.drawable.filter_selected)
                binding.btnLow.setBackgroundResource(R.drawable.filter_template)
            })
        }

        binding.btnLow.setOnClickListener {

            viewModel.getLowNote().observe(viewLifecycleOwner, { notesList ->
                oldNote = notesList as ArrayList<Notes>
                adapter = NoteAdapter(requireContext(), notesList)
                val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                binding.recyclerViewNote.layoutManager = staggeredGridLayoutManager
                binding.recyclerViewNote.adapter = adapter
                binding.btnHigh.setBackgroundResource(R.drawable.filter_template)
                binding.btnMedium.setBackgroundResource(R.drawable.filter_template)
                binding.btnLow.setBackgroundResource(R.drawable.filter_selected)
            })
        }

        binding.btnReset.setOnClickListener {

            viewModel.getNotes().observe(viewLifecycleOwner, { notesList ->
                oldNote = notesList as ArrayList<Notes>
                adapter = NoteAdapter(requireContext(), notesList)
                val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                binding.recyclerViewNote.layoutManager = staggeredGridLayoutManager
                binding.recyclerViewNote.adapter = adapter
                binding.btnHigh.setBackgroundResource(R.drawable.filter_template)
                binding.btnMedium.setBackgroundResource(R.drawable.filter_template)
                binding.btnLow.setBackgroundResource(R.drawable.filter_template)
            })
        }

        binding.btnAddNote.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_createNoteFragment)
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val item = menu.findItem(R.id.app_bar_search)
        val searchView = item.actionView as SearchView
        searchView.queryHint = "Enter Note..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                NoteFiltering(newText)
                return true
            }

        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun NoteFiltering(newText: String?) {

        val newFilteredList = arrayListOf<Notes>()
        for (i in oldNote){
            if (i.title.contains(newText!!) || i.subtitle.contains(newText!!)) {
                newFilteredList.add(i)
            }
        }

        adapter.filtering(newFilteredList)
    }

}