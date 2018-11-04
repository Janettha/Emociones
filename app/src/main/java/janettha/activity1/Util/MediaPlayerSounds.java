package janettha.activity1.Util;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.SystemClock;

import janettha.activity1.R;

public class MediaPlayerSounds {

    MediaPlayer mediaPlayer;
    Context context;


    public MediaPlayerSounds(Context context){
        this.context = context;
    }

    /* METODOS PARA REPRODUCIR SONIDOS - CORRECTO / INCORRECTO */
    public int loadSoundTF(boolean answer){
        if (answer)
            return R.raw.t;
        else
            return R.raw.f;
    }

    public int loadRuleta(){
        return R.raw.ruleta;
    }

    public int loadInicio(){
        return R.raw.inicoin;
    }

    public int loadNewAct(){
        return R.raw.nuevactividad;
    }

    public int loadFinEx(){
        return R.raw.finejercicios;
    }


    public void playSound(final int idSound) throws InterruptedException {
        if(mediaPlayer != null)
            mediaPlayer.release();
        else
            mediaPlayer = null;

        Thread playSound = new Thread() {
            public void run() {
                mediaPlayer = MediaPlayer.create(context, idSound);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer arg0) {
                        //Inicia reproducción.
                        mediaPlayer.start();
                        //SystemClock.sleep(500);
                    }
                });
            }
        };
        playSound.start();
        playSound.sleep(500);
    }
}
