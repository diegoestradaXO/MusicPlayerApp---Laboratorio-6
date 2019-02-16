package com.example.diego.musicplayerapp.Models

import android.app.Application

class Ext : Application() {

    companion object:PlayList{
        override val songlist: ArrayList<Song> =  ArrayList()
        var posicion = 0

        //Funcion que agrega la cancion a la lista
        override fun add(element: Song){
            songlist.add(element)
        }

    }

}