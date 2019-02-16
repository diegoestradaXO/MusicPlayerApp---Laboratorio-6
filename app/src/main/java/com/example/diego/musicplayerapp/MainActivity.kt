package com.example.diego.musicplayerapp

import android.content.*
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.net.Uri
import android.view.View
import android.widget.MediaController
import android.widget.MediaController.MediaPlayerControl
import com.example.diego.musicplayerapp.Models.Song
import com.example.diego.musicplayerapp.Models.SongAdapter
import java.util.*
import com.example.diego.musicplayerapp.MusicService.MusicBinder
import android.os.IBinder
import android.content.Context.BIND_AUTO_CREATE
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity(), MediaPlayerControl {

    //instancias
    private var controller: MusicController? = null
    private var musicSrv: MusicService? = null
    private var playIntent: Intent? = null
    private var musicBound = false
    private var paused = false
    private var playbackPaused = false
    private var songList: ArrayList<Song> = ArrayList()
    private var songView: ListView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Se carga la vista para la cancion
        songView = findViewById(R.id.song_list)
        //obtencion de la lista de canciones
        getSongList()
        Collections.sort(songList, object : Comparator<Song> {
            override fun compare(a: Song, b: Song): Int {
                return a.getTitle().compareTo(b.getTitle())
            }
        })
        val songAdt = SongAdapter(this, songList)
        songView!!.adapter = songAdt
        setController()
    }
    //Connection with Service
    private val musicConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MusicService.MusicBinder
            //get service
            musicSrv = binder.service
            //pass list
            musicSrv!!.setList(songList)
            musicBound = true
        }
        override fun onServiceDisconnected(name: ComponentName) {
            musicBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        if (playIntent == null) {
            playIntent = Intent(this, MusicService::class.java)
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE)
            startService(playIntent)
        }
    }
    //FUNCION QUE OBTIENE LA LISTA CON SU INFORMACION
    fun getSongList() {
        val musicResolver = contentResolver
        val musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val musicCursor = musicResolver.query(musicUri, null, null, null, null)
        if (musicCursor != null && musicCursor.moveToFirst()) {
            val titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
            val idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
            val artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
            do {
                val thisId = musicCursor.getLong(idColumn)
                val thisTitle = musicCursor.getString(titleColumn)
                val thisArtist = musicCursor.getString(artistColumn)
                songList.add(Song(thisTitle, thisArtist, thisId))//SE CREA UN NUEVO CONTACTO Y SE AGREGA A LA LISTA
            } while (musicCursor.moveToNext())
        }
    }
    private fun setController() {
        controller = MusicController(this)
        controller!!.setPrevNextListeners(
            { playNext() },
            { playPrev() })
        controller!!.setMediaPlayer(this)
        controller!!.setAnchorView(findViewById(R.id.song_list))
        controller!!.isEnabled = true
    }

    //Reproduce siguiente
    private fun playNext() {
        musicSrv!!.playNext()
        controller!!.show(0)
    }

    //Reproduce anterior
    private fun playPrev() {
        musicSrv!!.playPrev()
        if(playbackPaused){
            setController()
            playbackPaused=false
        }
        controller!!.show(0)
    }


    //metodos implementados de Controller
    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getAudioSessionId(): Int {
        TODO("not implemented")
    }

    override fun getBufferPercentage(): Int {
        return 0
    }

    override fun getCurrentPosition(): Int {
        return if(musicSrv!=null && musicBound && musicSrv!!.isPng())
            musicSrv!!.getPosn()
        else 0
    }

    override fun getDuration(): Int {
        return if(musicSrv!=null && musicBound && musicSrv!!.isPng())
            musicSrv!!.getDur()
        else 0
    }

    override fun isPlaying(): Boolean {
        if(musicSrv!=null && musicBound)
            return musicSrv!!.isPng()
        return false
    }

    override fun pause() {
        playbackPaused=true
        musicSrv!!.pausePlayer()
    }

    override fun seekTo(pos: Int) {
        musicSrv!!.seek(pos)
    }


    override fun start() {
        musicSrv!!.go()

    }

    override fun canPause(): Boolean {
        return true
    }

    fun songPicked(view: View) {
        musicSrv!!.setSong(Integer.parseInt(view.tag.toString()))
        musicSrv!!.playSong()
        if(playbackPaused){
            setController()
            playbackPaused=false
        }
        controller!!.show(0)
    }
    override fun onPause() {
        super.onPause()
        paused = true
    }
    override fun onResume() {
        super.onResume()
        if (paused) {
            setController()
            paused = false
        }
    }

    override fun onStop() {
        controller!!.hide()
        super.onStop()
    }

    override fun onDestroy() {
        stopService(playIntent)
        musicSrv = null
        super.onDestroy()
    }
}