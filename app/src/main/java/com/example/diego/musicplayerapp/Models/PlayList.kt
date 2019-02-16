package com.example.diego.musicplayerapp.Models

interface PlayList {
    val songlist: ArrayList<Song> //Lista de canciones a cargar
    fun add(element: Song)//funcion de interfaz para agregar elementos
}