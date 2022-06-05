package de.htw_berlin.notesapplikation.fragments.update

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import de.htw_berlin.notesapplikation.R
import de.htw_berlin.notesapplikation.databinding.FragmentUpdateBinding
import de.htw_berlin.notesapplikation.fragments.add.AddFragment
import de.htw_berlin.notesapplikation.model.Note
import de.htw_berlin.notesapplikation.viewmodel.NoteViewModel

class UpdateFragment : Fragment() {
    private lateinit var binding: FragmentUpdateBinding
    private val args by navArgs<UpdateFragmentArgs>()
    private var selectedImagePath = ""

    private lateinit var mNoteViewModel: NoteViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update, container, false)

        mNoteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        binding.updateTitleEt.setText(args.currentNote.Title)
        binding.updateSubtitleEt.setText(args.currentNote.Subtitle)
        binding.updateDescriptionEt.setText(args.currentNote.Description)
        binding.imageUpdate.setImageBitmap(BitmapFactory.decodeFile(args.currentNote.ImagePath))
        selectedImagePath = args.currentNote.ImagePath

        // Add menu
        setHasOptionsMenu(true)

        return binding.root
    }

    companion object {
        val IMAGE_REQUEST_CODE = 1_000
    }

    private fun updateItem() {
        val title = binding.updateTitleEt.text.toString()
        val subtitle = binding.updateSubtitleEt.text.toString()
        val description = binding.updateDescriptionEt.text.toString()
        val image = selectedImagePath

        if (inputCheck(title, subtitle, description)) {
            // Create Note Object
            val updatedNote = Note(args.currentNote.id, title, subtitle, description, image)
            // Update Current Note
            mNoteViewModel.updateNote(updatedNote)
            Toast.makeText(requireContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show()
            // Navigate Back
            findNavController().navigate(R.id.action_UpdateFragment_to_ListFragment)
        } else {
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun inputCheck(title: String, subtitle: String, description: String): Boolean{
        return !(TextUtils.isEmpty(title) && TextUtils.isEmpty(subtitle) && TextUtils.isEmpty(description))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_delete) {
            deleteNote()
        }
        if (item.itemId == R.id.menu_add) {
            updateItem()
        }
        if (item.itemId == R.id.menu_image) {
            pickImageGallery()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteNote() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mNoteViewModel.deleteNote(args.currentNote)
            Toast.makeText(
                requireContext(),
                "Successfully removed: ${args.currentNote.Title}",
                Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_UpdateFragment_to_ListFragment)
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete ${args.currentNote.Title}?")
        builder.setMessage("Are you sure you want to delete ${args.currentNote.Title}?")
        builder.create().show()
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null){
            startActivityForResult(intent, IMAGE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                var selectedImageUrl = data.data
                if (selectedImageUrl != null) {
                    try {
                        var inputStream =
                            requireActivity().contentResolver.openInputStream(selectedImageUrl)
                        var bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.imageUpdate.setImageBitmap(bitmap)

                        selectedImagePath = getPathFromUri(selectedImageUrl)!!
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath:String? = null
        var cursor = requireActivity().contentResolver.query(contentUri,null,null,null,null)
        if (cursor == null){
            filePath = contentUri.path
        }else{
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }
}