package de.htw_berlin.noteapp.Fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import de.htw_berlin.noteapp.Model.Notes
import de.htw_berlin.noteapp.R
import de.htw_berlin.noteapp.ViewModel.NoteViewModel
import de.htw_berlin.noteapp.databinding.FragmentCreateNoteBinding
import java.io.ByteArrayOutputStream
import java.util.*


class CreateNoteFragment: Fragment() {

    lateinit var binding: FragmentCreateNoteBinding
    var priority: String = "1"
    val viewModel: NoteViewModel by viewModels()
    private val pickImage = 100
    var imageBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentCreateNoteBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)

        binding.greenCircle.setImageResource(R.drawable.ic_baseline_check_24)

        binding.greenCircle.setOnClickListener {
            priority = "1"
            binding.greenCircle.setImageResource(R.drawable.ic_baseline_check_24)
            binding.yellowCircle.setImageResource(0)
            binding.redCircle.setImageResource(0)
        }

        binding.yellowCircle.setOnClickListener {
            priority = "2"
            binding.yellowCircle.setImageResource(R.drawable.ic_baseline_check_24)
            binding.greenCircle.setImageResource(0)
            binding.redCircle.setImageResource(0)
        }

        binding.redCircle.setOnClickListener {
            priority = "3"
            binding.redCircle.setImageResource(R.drawable.ic_baseline_check_24)
            binding.yellowCircle.setImageResource(0)
            binding.greenCircle.setImageResource(0)
        }

        binding.btnCreateSave.setOnClickListener {
            createNote(it)
        }

        return binding.root
    }

    private fun createNote(it: View?) {

        val title = binding.title.text.toString()
        val subtitle = binding.subtitle.text.toString()
        val note = binding.note.text.toString()

        val d = Date()
        val date: CharSequence = DateFormat.format("d MMMM yyyy", d.time)

        val outputStream = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        val data = Notes(
            null, title = title, subtitle = subtitle, note = note, date = date.toString(), priority, outputStream.toByteArray(),
        )
        viewModel.addNotes(data)

        Navigation.findNavController(it!!).navigate(R.id.action_createNoteFragment_to_homeFragment)

        Toast.makeText(requireContext(), "Note Created", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.create_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
        }

        if (item.itemId == R.id.btnImage) {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            var imageUri = data?.data
            var inputStream = requireActivity().contentResolver.openInputStream(imageUri!!)
            imageBitmap = BitmapFactory.decodeStream(inputStream)
            binding.image.setImageBitmap(imageBitmap)
        }
    }

}