package de.htw_berlin.noteapp.Repository

import androidx.lifecycle.LiveData
import de.htw_berlin.noteapp.Dao.NoteDao
import de.htw_berlin.noteapp.Model.Notes

class NoteRepos(val dao: NoteDao) {

    fun getAllNote(): LiveData<List<Notes>> = dao.getNotes()

    fun getHighNote(): LiveData<List<Notes>> = dao.getHighNotes()

    fun getMediumNote(): LiveData<List<Notes>> = dao.getMediumNotes()

    fun getLowNote(): LiveData<List<Notes>> = dao.getLowNotes()

    fun insertNote(notes: Notes){
        dao.insertNotes(notes)
    }

    fun deleteNote(id:Int) {
        dao.deleteNotes(id)
    }

    fun updateNotes(notes: Notes){
        dao.updateNotes(notes)
    }
}