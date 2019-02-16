package com.example.diego.musicplayerapp.Models

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.view.LayoutInflater
import android.support.design.widget.CoordinatorLayout.Behavior.setTag
import android.widget.TextView
import android.widget.LinearLayout
import com.example.diego.musicplayerapp.R

class SongAdapter(c: Context, theSongs: ArrayList<Song>) : BaseAdapter() {
    private var songs: ArrayList<Song>? = theSongs
    private var songInf: LayoutInflater? = LayoutInflater.from(c)
    override fun getCount(): Int {
        return songs!!.size
    }
    override fun getItem(arg0: Int): Any? {
        // TODO Auto-generated method stub
        return null
    }
    override fun getItemId(arg0: Int): Long {
        // TODO Auto-generated method stub
        return 0
    }
    
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val songLay = songInf!!.inflate(R.layout.song, parent, false) as LinearLayout
        //Se obtiene las vistas
        val songView = songLay.findViewById<View>(R.id.song_title) as TextView
        val artistView = songLay.findViewById<View>(R.id.song_artist) as TextView
        //Se obtiene la cancion por su posicion
        val currSong = songs?.get(position)
        //get title and artist
        songView.text = currSong?.getTitle()
        artistView.text = currSong?.getArtist()
        //Establece posicion
        songLay.tag = position
        return songLay
    }

}