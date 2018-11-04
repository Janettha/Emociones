package janettha.activity1.EmocionesVo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import janettha.activity1.Adaptadores.SliderAdapterImagenes;
import janettha.activity1.EmocionesDelegate.ActividadesDelegate;
import janettha.activity1.EmocionesDelegate.EmocionesDelegate;
import janettha.activity1.EmocionesDto.ActividadImagenesDto;
import janettha.activity1.EmocionesDto.EmocionDto;
import janettha.activity1.EmocionesDao.EmocionesDao;
import janettha.activity1.R;
import janettha.activity1.Util.Factory;
import janettha.activity1.Util.LockableViewPager;

public class ImagenesVo extends Activity {
    private static final String TAG = "ImagenesVo";

    public static final String ARG_Main = "Answer 1";
    public static final String ARG_B = "Answer 2";
    public static final String ARG_C = "Answer 3";

    private LockableViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    public final int LIM_emociones = 11;
    private SharedPreferences sharedPreferences;
    public String sexo, userU;

    int r[] = new int[3];
    int A1;

    EmocionesDelegate emocionesDelegate;
    ActividadesDelegate actividadesDelegate;
    List<ActividadImagenesDto> listAct0 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acta);

        mSlideViewPager = findViewById(R.id.slideViewPager);
        mDotLayout = findViewById(R.id.dotsLayout);

        //se determina número de actividad
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            A1 = bundle.getInt("a1", 0);
        }
        if (A1 > LIM_emociones) randomID();
        else secuencialID();

        sharedPreferences = getSharedPreferences(ListaUsuariosVo.keySP, MODE_PRIVATE);
        sexo = sharedPreferences.getString("sexo", "m");
        userU = sharedPreferences.getString("usuario", "");

        Log.d(TAG, "onCreate: sexo: "+sexo);

        emocionesDelegate = new EmocionesDelegate();
        actividadesDelegate = new ActividadesDelegate();
        //emocionesDao.Emociones(this, sexo);
        //emocionesDao.getEmociones();
        //fillData(this, sexo);
        SQLiteDatabase db = Factory.getBaseDatos(this);
        listAct0 = actividadesDelegate.obtieneActividadesA(db, sexo);
        db.close();
        addDotsIndicator(0);

        Log.e("emocionesA1List",listAct0.get(r[0]).emocionMain().getEmocion()+
                "-"+listAct0.get(r[1]).emocionMain().getEmocion()+
                "-"+listAct0.get(r[2]).emocionMain().getEmocion());
        SliderAdapterImagenes sliderAdapter = new SliderAdapterImagenes(this, userU, A1, listAct0.get(r[0]), listAct0.get(r[1]), listAct0.get(r[2]), sexo, mSlideViewPager);
        mSlideViewPager.setAdapter(sliderAdapter);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        mSlideViewPager.setPagingEnabled(false);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mSlideViewPager.setPagingEnabled(false);
            mSlideViewPager.setPosition(position);
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public void addDotsIndicator(int position){
        SQLiteDatabase db = Factory.getBaseDatos(this);
        TextView[] mDots = new TextView[3];
        mDotLayout.removeAllViews();
        for(int i = 0; i< mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.white));
            mDotLayout.addView(mDots[i]);
        }
        if (mDots.length > 0){
            if(position == 0) {
                mDotLayout.setBackgroundColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(r[0], sexo, db).getColor()));
                mDots[position].setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(r[0], sexo, db).getColorB()));
            }else if(position == 1){
                mDotLayout.setBackgroundColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(r[1], sexo, db).getColor()));
                mDots[position].setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(r[1], sexo, db).getColorB()));
            }else if(position == 2) {
                mDotLayout.setBackgroundColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(r[2], sexo, db).getColor()));
                mDots[position].setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(r[2], sexo, db).getColorB()));
            }
        }
        db.close();
    }

    private void secuencialID(){
        r[0] = A1;
        r[1] = A1+1;
        r[2] = A1+2;
    }

    private void randomID(){
        r[0] = (int) (Math.random() * LIM_emociones ) ;
        r[1] = (int) (Math.random() * LIM_emociones ) ;
        r[2] = (int) (Math.random() * LIM_emociones ) ;

        while(r[0] == r[1]){
            r[1] = (int) (Math.random() * LIM_emociones ) ;
        }
        while(r[0] == r[2] || r[1] == r[2]){
            r[2] = (int) (Math.random() * LIM_emociones ) ;
        }
    }
/*
    private void fillData (Context c, String s){
        try {
            InputStream fileE = c.getResources().openRawResource(R.raw.preactividad);
            BufferedReader brE = new BufferedReader(new InputStreamReader(fileE));

            //Lectura de emocion en actividad de redacciones
            int i = 0;
            String line1, ruta="";
            if (fileE != null) {
                while ((line1 = brE.readLine()) != null) {
                    String[] array = line1.split(","); // Split according to the hyphen and put them in an array

                    List<EmocionDto> lEmociones = new ArrayList<EmocionDto>();
                    ActividadImagenesDto a0;

                    int id = Integer.parseInt(array[0]);
                    int id2 = Integer.parseInt(array[1]);
                    int id3 = Integer.parseInt(array[2]);

                    lEmociones.add(new EmocionDto(id, emocionesDao.getEmocion(id).getName(), s, emocionesDao.getEmocion(id).getUrl(), emocionesDao.getEmocion(id).getColor(), emocionesDao.getEmocion(id).getColorB()));
                    lEmociones.add(new EmocionDto(id2, emocionesDao.getEmocion(id2).getName(), s, emocionesDao.getEmocion(id2).getUrl(), emocionesDao.getEmocion(id2).getColor(), emocionesDao.getEmocion(id2).getColorB()));
                    lEmociones.add(new EmocionDto(id3, emocionesDao.getEmocion(id3).getName(), s, emocionesDao.getEmocion(id3).getUrl(), emocionesDao.getEmocion(id3).getColor(), emocionesDao.getEmocion(id3).getColorB()));
                    a0 = new ActividadImagenesDto(i, lEmociones);
                    listAct0.add(i, a0);
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ImagenesVo.this, MenuActividadesVo.class));
    }

}
