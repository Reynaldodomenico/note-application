package de.htw_berlin.noteapp.Fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.htw_berlin.noteapp.Model.Notes
import de.htw_berlin.noteapp.R
import de.htw_berlin.noteapp.ViewModel.NoteViewModel
import de.htw_berlin.noteapp.databinding.FragmentEditNoteBinding
import java.io.ByteArrayOutputStream
import java.util.*

class EditNoteFragment : Fragment() {

    lateinit var binding: FragmentEditNoteBinding
    val notes by navArgs<EditNoteFragmentArgs>()
    var priority: String = "1"
    private val pickImage = 100
    val viewModel: NoteViewModel by viewModels()
    var imageBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentEditNoteBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)

        imageBitmap = BitmapFactory.decodeByteArray(notes.data.image, 0, notes.data.image.size)
        binding.image.setImageBitmap(BitmapFactory.decodeByteArray(notes.data.image, 0, notes.data.image.size))

        binding.title.setText(notes.data.title)
        binding.subtitle.setText(notes.data.subtitle)
        binding.note.setText(notes.data.note)

        when (notes.data.priority) {
            "1" -> {
                priority = "1"
                binding.greenCircle.setImageResource(R.drawable.ic_baseline_check_24)
                binding.yellowCircle.setImageResource(0)
                binding.redCircle.setImageResource(0)
            }
            "2" -> {
                priority = "2"
                binding.yellowCircle.setImageResource(R.drawable.ic_baseline_check_24)
                binding.greenCircle.setImageResource(0)
                binding.redCircle.setImageResource(0)
            }
            "3" -> {
                priority = "3"
                binding.redCircle.setImageResource(R.drawable.ic_baseline_check_24)
                binding.yellowCircle.setImageResource(0)
                binding.greenCircle.setImageResource(0)
            }
        }


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

        binding.btnEditSave.setOnClickListener {
            updateNote(it)
        }

        binding.image

        return binding.root
    }

    private fun updateNote(it: View?) {

        val title = binding.title.text.toString()
        val subtitle = binding.subtitle.text.toString()
        val note = binding.note.text.toString()

        val d = Date()
        val date: CharSequence = DateFormat.format("d MMMM yyyy", d.time)

        val outputStream = ByteArrayOutputStream()
        imageBitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        val data = Notes(
            notes.data.id, title = title, subtitle = subtitle, note = note, date = date.toString(), priority, outputStream.toByteArray(),
        )
        viewModel.updateNotes(data)

        Toast.makeText(requireContext(), "Note Edited", Toast.LENGTH_SHORT).show()

        Navigation.findNavController(it!!).navigate(R.id.action_editNoteFragment_to_homeFragment)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.delete_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
        }

        if (item.itemId == R.id.btnDelete) {

            val bottomSheet: BottomSheetDialog= BottomSheetDialog(requireContext())
            bottomSheet.setContentView(R.layout.dialog_delete)

            val textviewYes = bottomSheet.findViewById<TextView>(R.id.btnYes)
            val textviewNo = bottomSheet.findViewById<TextView>(R.id.btnNo)

            textviewYes?.setOnClickListener {
                viewModel.deleteNotes(notes.data.id!!)
                bottomSheet.dismiss()
                Toast.makeText(requireContext(), "Note Deleted", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.navigate(R.id.action_editNoteFragment_to_homeFragment)
            }

            textviewNo?.setOnClickListener {
                bottomSheet.dismiss()
            }

            bottomSheet.show()
        }

        if (item.itemId == R.id.btnImage) {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickImage) {
            var imageUri = data?.data
            var inputStream = requireActivity().contentResolver.openInputStream(imageUri!!)
            imageBitmap = BitmapFactory.decodeStream(inputStream)
            binding.image.setImageBitmap(imageBitmap)
        }
    }

}
