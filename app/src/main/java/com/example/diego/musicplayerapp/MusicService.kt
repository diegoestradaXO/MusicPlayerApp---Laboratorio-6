package com.example.diego.musicplayerapp

import android.support.v4.app.NotificationCompat
import com.example.diego.musicplayerapp.Models.Song
import android.app.*
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import java.util.*
import android.app.NotificationManager
import android.app.PendingIntent



@Suppress("DEPRECATION")
class MusicService: Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener{

    //Media player and song info
    private var player: MediaPlayer = MediaPlayer()
    lateinit var songs: ArrayList<Song>
    private var songTitle = ""
    private var songArtist = ""
    private var songPosn: Int = 0
    private val NOTIFY_ID = 1
    private val musicBind = MusicBinder()

    override fun onCreate() {

        super.onCreate()
        initMusicPlayer()

    }
    fun initMusicPlayer(){
        //set player properties
        player.setWakeMode(applicationContext,
            PowerManager.PARTIAL_WAKE_LOCK)
        player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player.setOnPreparedListener(this)
        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)
    }

    override fun onBind(arg0: Intent): IBinder? {
        return musicBind
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if(player.currentPosition >0)
            mp!!.reset()
        playNext()

    }
    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        mp!!.reset()
        return false
    }

    override fun onPrepared(mp: MediaPlayer?) {
        //start playback
        val start = mp?.start()
        //Creacion de notification
        val mBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Reproductor")
            //Consistente
            .setOngoing(true)
            .setContentText(songTitle)


        val notificationIntent =
            Intent(this, MainActivity::class.java)//Redireccion a la main activity
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val resultPendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            0
        )
        mBuilder.setContentIntent(resultPendingIntent)
        val mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotificationManager.notify(1, mBuilder.build())//Show notification
    }

    override fun onUnbind(intent: Intent): Boolean {
        player.stop()
        player.release()
        return false
    }


    //Se pasan la lista de canciones del main al servicio
    fun setList(theSongs: ArrayList<Song>) {
        songs = theSongs
    }

    inner class MusicBinder : Binder() {
        internal val service :MusicService
            get() = this@MusicService
    }

    fun playSong(){
        //se reproduce
        player.reset()
        //get song
        val playSong = songs[songPosn]
        //get artist
        songArtist=playSong.getArtist()
        //get title
        songTitle=playSong.getTitle()
        //get id
        val currSong = playSong.getID()
        //set uri
        val trackUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            currSong
        )
        try {
            player.setDataSource(applicationContext, trackUri)
        } catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error setting data source", e)
        }
        player.prepareAsync()

    }

    fun setSong(songIndex: Int){
        songPosn = songIndex
    }

    fun getPosn(): Int {
        return player.currentPosition
    }

    fun getDur(): Int {
        return player.duration
    }

    fun isPng(): Boolean {
        return player.isPlaying
    }

    fun pausePlayer() {
        player.pause()
    }

    fun seek(posn: Int) {
        player.seekTo(posn)
    }

    fun go() {
        player.start()
    }

    fun playPrev() {
        songPosn--
        if (songPosn<0)
            songPosn = songs.size -1
        playSong()
    }
    //reproduce siguiente
    fun playNext(){
        songPosn++
        if (songPosn == songs.size)
            songPosn = 0
        playSong()
    }

    override fun onDestroy() {
        stopForeground(true)
    }

}