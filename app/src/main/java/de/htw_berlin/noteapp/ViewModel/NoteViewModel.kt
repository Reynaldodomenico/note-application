package de.htw_berlin.noteapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import de.htw_berlin.noteapp.Database.NoteDatabase
import de.htw_berlin.noteapp.Model.Notes
import de.htw_berlin.noteapp.Repository.NoteRepos

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    val repos: NoteRepos

    init {
        val dao = NoteDatabase.getDatabaseInstance(application).myNoteDao()
        repos = NoteRepos(dao)
    }

    fun addNotes(notes: Notes){
        repos.insertNote(notes)
    }

    fun getNotes(): LiveData<List<Notes>> = repos.getAllNote()

    fun getHighNote(): LiveData<List<Notes>> = repos.getHighNote()

    fun getMediumNote(): LiveData<List<Notes>> = repos.getMediumNote()

    fun getLowNote(): LiveData<List<Notes>> = repos.getLowNote()

    fun deleteNotes(id:Int){
        repos.deleteNote(id)
    }

    fun updateNotes(notes: Notes){
        repos.updateNotes(notes)
    }

}