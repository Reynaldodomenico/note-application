package de.htw_berlin.noteapp.Adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import de.htw_berlin.noteapp.Fragment.HomeFragmentDirections
import de.htw_berlin.noteapp.Model.Notes
import de.htw_berlin.noteapp.R
import de.htw_berlin.noteapp.databinding.ItemNoteBinding

class NoteAdapter(val requireContext: Context, var notesList: List<Notes>) : RecyclerView.Adapter<NoteAdapter.noteViewHolder>() {

    fun filtering(newFilteredList: ArrayList<Notes>) {
        notesList = newFilteredList
        notifyDataSetChanged()
    }

    class noteViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): noteViewHolder {
        return noteViewHolder(
            ItemNoteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: noteViewHolder, position: Int) {
        val data = notesList[position]
        holder.binding.title.text = data.title
        holder.binding.subtitle.text = data.subtitle
        holder.binding.date.text = data.date

        var byteArray = data.image
        holder.binding.image.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))

        when (data.priority){
            "1" -> {holder.binding.priority.setBackgroundResource(R.drawable.green_circle)}
            "2" -> {holder.binding.priority.setBackgroundResource(R.drawable.yellow_circle)}
            "3" -> {holder.binding.priority.setBackgroundResource(R.drawable.red_circle)}
        }

        holder.binding.root.setOnClickListener {

            val action = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(data)
            Navigation.findNavController(it).navigate(action)

        }
    }
    override fun getItemCount() = notesList.size

}