package janettha.activity1.Util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RequiresApi;

import janettha.activity1.R;

public class SoundManager {

    private Context context;
    private SoundPool sndPool;
    //private SoundPool.Builder sndPool21;
    private float rate = 1f, leftVolume = .25f, rightVolume = .25f;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SoundManager(Context context){
        //Reproduce sonidos
        //sndPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
/*
            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_UNKNOWN)
                    .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                    .build();
  */
            sndPool = new SoundPool.Builder()
                    //.setAudioAttributes(audioAttrib)
                    .setMaxStreams(6)
                    .build();
        }else{
            sndPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }
    }

    /* METODOS PARA REPRODUCIR SONIDOS - CORRECTO / INCORRECTO */
    public int loadSoundTF(boolean answer){
        if (answer)
            return sndPool.load(context, R.raw.t, 1);
        else
            return sndPool.load(context, R.raw.f, 1);
    }

    public int loadRuleta(){
        return sndPool.load(context, R.raw.ruleta, 1);
    }

    public int loadInicio(){
        return sndPool.load(context, R.raw.inicoin, 1);
    }

    public int loadNewAct(){
        return sndPool.load(context, R.raw.nuevactividad, 1);
    }

    public int loadFinEx(){
        return sndPool.load(context, R.raw.finejercicios, 1);
    }

    public void playSound(int id){
        sndPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener(){
            @Override
            public void onLoadComplete(SoundPool arg0, int arg1, int arg2) {
                sndPool.play(R.raw.f, leftVolume, rightVolume, 1, 0, rate);
            }});

    }
}
