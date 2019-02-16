package com.example.diego.musicplayerapp.Models

import android.R.id

//Clase de modelo para la creacion de las canciones
class Song (val songTitle:String,val songArtist: String, val id: Long){
    //Funcion para obtener el id de una cancion, con el fin de referirse a ella
    fun getID(): Long {
        return this.id
    }
    //Funcion para obtener el nombre de la cancion
    fun getTitle(): String {
        return songTitle
    }
    //Funcion para obtener el nombre del artista
    fun getArtist(): String {
        return songArtist
    }
}