package de.htw_berlin.notesapplikation.fragments.add

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import de.htw_berlin.notesapplikation.R
import de.htw_berlin.notesapplikation.databinding.FragmentAddBinding
import de.htw_berlin.notesapplikation.model.Note
import de.htw_berlin.notesapplikation.viewmodel.NoteViewModel
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class AddFragment : Fragment(),EasyPermissions.PermissionCallbacks,EasyPermissions.RationaleCallbacks {
    private lateinit var binding: FragmentAddBinding
    private lateinit var mNoteViewModel: NoteViewModel
    private var READ_STORAGE_PERM = 123
    private var selectedImagePath = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_add, container, false)

        mNoteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        // Add menu
        setHasOptionsMenu(true)

        return binding.root
    }

    companion object {
        val IMAGE_REQUEST_CODE = 1_000
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add){
            insertDataToDatabase()
        }
        if(item.itemId == R.id.menu_image){
            readStorageTask()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDatabase() {
        val title = binding.addTitleEt.text.toString()
        val subtitle = binding.addSubtitleEt.text.toString()
        val description = binding.addDescriptionEt.text.toString()
        val image = selectedImagePath

        if(inputCheck(title, subtitle, description)){
            // Create Note Object
            val note = Note(0, title, subtitle, description, image)
            // Add Data to Database
            mNoteViewModel.addNote(note)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_LONG).show()
            // Navigate Back
            findNavController().navigate(R.id.action_AddFragment_to_ListFragment)
        }else{
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
        }
    }

    private fun inputCheck(title: String, subtitle: String, description: String): Boolean{
        return !(TextUtils.isEmpty(title) && TextUtils.isEmpty(subtitle) && TextUtils.isEmpty(description))
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null){
            startActivityForResult(intent, IMAGE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                var selectedImageUrl = data.data
                if (selectedImageUrl != null) {
                    try {
                        var inputStream =
                            requireActivity().contentResolver.openInputStream(selectedImageUrl)
                        var bitmap = BitmapFactory.decodeStream(inputStream)
                        binding.imageAdd.setImageBitmap(bitmap)

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

    private fun hasReadStoragePerm():Boolean{
        return EasyPermissions.hasPermissions(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun readStorageTask(){
        if (hasReadStoragePerm()){

            pickImageGallery()
        }else{
            EasyPermissions.requestPermissions(
                requireActivity(),
                getString(R.string.storage_permission_text),
                READ_STORAGE_PERM,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,requireActivity())
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(),perms)){
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onRationaleAccepted(requestCode: Int) {
    }

    override fun onRationaleDenied(requestCode: Int) {
    }
}