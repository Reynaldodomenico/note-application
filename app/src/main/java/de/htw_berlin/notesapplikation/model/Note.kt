package de.htw_berlin.notesapplikation.model

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val Title: String,
    val Subtitle: String,
    val Description: String,
    val ImagePath: String
): Parcelable