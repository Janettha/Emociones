package janettha.activity1.Adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
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

import janettha.activity1.EmocionesDelegate.ActividadesDelegate;
import janettha.activity1.EmocionesDelegate.EmocionesDelegate;
import janettha.activity1.EmocionesVo.ListaUsuariosVo;
import janettha.activity1.EmocionesDto.ActividadRedaccionesDto;
import janettha.activity1.EmocionesDao.EmocionesDao;
import janettha.activity1.EmocionesVo.MenuActividadesVo;
import janettha.activity1.EmocionesDto.EmocionDto;
import janettha.activity1.Fragments.FragmentRedacciones;
import janettha.activity1.Util.Factory;
import janettha.activity1.Util.TemplatePDF;
import janettha.activity1.R;
import janettha.activity1.Util.LockableViewPager;

public class FragmentActivityRedacciones extends FragmentActivity {
    private static final String TAG = "FragmentActivityRedacci";

    private static final int NUM_PAGES = 3;
    private LockableViewPager mPager;
    public final int LIM_emociones = 16;

    public static final String ARG_r = "Redaccion";
    public static final String ARG_u = "User";
    public static final String ARG_sexo = "SexoUser";
    public static final String ARG_e1 = "Emocion1";
    public static final String ARG_IDe1 = "Emocion1ID";
    public static final String ARG_e2 = "Emocion2";
    public static final String ARG_IDe2 = "Emocion2ID";
    public static final String ARG_e3 = "Emocion3";
    public static final String ARG_IDe3 = "Emocion3ID";
    private TextToSpeech tts;

    private String sexo;
    private String userU;

    EmocionesDelegate emocionesDelegate;
    ActividadesDelegate actividadesDelegate;
    List<ActividadRedaccionesDto> listActB = new ArrayList<>();

    int r[] = new int[3];
    int A2;
    private LinearLayout mDotLayout;

    Context context;
    TemplatePDF templatePDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        mPager = findViewById(R.id.pager);
        mDotLayout = findViewById(R.id.dotsLayoutA2);
        mDotLayout.removeAllViews();

        context = getBaseContext();

        SharedPreferences sharedPreferences = getSharedPreferences(ListaUsuariosVo.keySP, MODE_PRIVATE);
        sexo = sharedPreferences.getString("sexo", "m");
        userU = sharedPreferences.getString("usuario", "");

        emocionesDelegate = new EmocionesDelegate();
        actividadesDelegate = new ActividadesDelegate();
        SQLiteDatabase db = Factory.getBaseDatos(context);
        //emocionesDao.Emociones(getBaseContext(),sexo);
        //emocionesDelegate.getEmociones(sexo, db);
        listActB = actividadesDelegate.obtieneActividadesB(db, sexo);
        Log.d(TAG, "onCreate: size: "+listActB.size());
        db.close();
        Log.e(TAG,"YA ENTRAMOS");

        //fillData(this, sexo);

        templatePDF = new TemplatePDF(context);

        Bundle b = getIntent().getExtras();
        if(b != null){
            A2 = b.getInt("a2",0);
        }
        if(A2>=LIM_emociones){
            randomID();
            Log.d(TAG, "onCreate: RANDOM: "+r[0]+" : "+r[1]+" : "+r[2]);
        }else{
            secuencialID();
            Log.d(TAG, "onCreate: SECUENCIA"+r[0]+" : "+r[1]+" : "+r[2]);
        }

        addDotsIndicator(0);

        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(viewListener);

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

    }

    private void secuencialID(){
        r[0] = A2;
        r[1] = A2 + 1;
        if(A2 == 16) {
            r[2] = 0;
        }else if(A2 < 13){
            r[2] = A2 + 2;
        }
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

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @SuppressLint("WrongViewCast")
        @Override
        public Fragment getItem(int position) {
            FragmentRedacciones f;
            if(position == 0) {
                f = FragmentRedacciones.create(position, context, userU, A2, listActB.get(r[0]), sexo, mPager, templatePDF);
                return f;
            }else if(position == 1) {
                f = FragmentRedacciones.create(position, context, userU, A2, listActB.get(r[1]), sexo, mPager, templatePDF);
                return f;
            }else if(position == 2) {
                f = FragmentRedacciones.create(position, context, userU, A2, listActB.get(r[2]), sexo, mPager, templatePDF);
                return f;
            }else return null;


        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener(){

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mPager.setPagingEnabled(false);
            mPager.setPosition(position);
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public void addDotsIndicator(int position){
        SQLiteDatabase db = Factory.getBaseDatos(getBaseContext());
        TextView[] mDots = new TextView[3];
        mDotLayout.removeAllViews();
        for(int i = 0; i< mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            //mDots[i].setTextColor(getResources().getColor(R.color.white));
            mDotLayout.addView(mDots[i]);
        }
        if (mDots.length > 0){
            if(position == 0) {
                //mDotLayout.setBackgroundColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(listActB.get(r[0]).emocionMain().getEmocion(), sexo, db).getColor()));
                ActividadRedaccionesDto dto = listActB.get(r[0]);
                Log.d(TAG, "addDotsIndicator: dto: "+dto.emocionMain()+" - "+dto.emocionB()+" - "+dto.emocionC());
                mDots[position].setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(dto.emocionMain().getEmocion(), sexo, db).getColorB()));
                mDots[1].setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(dto.emocionMain().getEmocion(), sexo, db).getColor()));
                mDots[2].setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(dto.emocionMain().getEmocion(), sexo, db).getColor()));
                Log.e("ActRedaccionesDto-Dots",listActB.get(r[0]).emocionMain().getName());
            }else if(position == 1){
                //mDotLayout.setBackgroundColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(listActB.get(r[1]).emocionMain().getEmocion(), sexo, db).getColor()));
                mDots[position].setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(listActB.get(r[1]).emocionMain().getEmocion(), sexo, db).getColorB()));
                mDots[0].setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(listActB.get(r[1]).emocionMain().getEmocion(), sexo, db).getColor()));
                mDots[2].setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(listActB.get(r[1]).emocionMain().getEmocion(), sexo, db).getColor()));
                Log.e("ActRedaccionesDto-Dots",listActB.get(r[1]).emocionMain().getName());
            }else if(position == 2) {
                //mDotLayout.setBackgroundColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(listActB.get(r[2]).emocionMain().getEmocion(), sexo, db).getColor()));
                mDots[position].setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(listActB.get(r[2]).emocionMain().getEmocion(), sexo, db).getColorB()));
                mDots[1].setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(listActB.get(r[2]).emocionMain().getEmocion(), sexo, db).getColor()));
                mDots[0].setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(listActB.get(r[2]).emocionMain().getEmocion(), sexo, db).getColor()));
                Log.e("ActRedaccionesDto-Dots",listActB.get(r[2]).emocionMain().getName());
            }
            Log.e("ActRedaccionesDto-Dots",listActB.get(r[0]).emocionMain().getName()+"-"+listActB.get(r[1]).emocionMain().getName()+"-"+listActB.get(r[2]).emocionMain().getName());
        }else{
            Log.e("Dots", "No pudieron ser creados");
        }
        db.close();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FragmentActivityRedacciones.this, MenuActividadesVo.class));
    }

/*
    private void fillData (Context c, String s){
        InputStream fileE;
        try {
            if(s.equals("f"))
                fileE = c.getResources().openRawResource(R.raw.redaccionesf);
            else
                fileE = c.getResources().openRawResource(R.raw.redaccionesm);

            BufferedReader brE = new BufferedReader(new InputStreamReader(fileE));
            int i = 0;
            String line1;

            if(fileE != null){

                while ((line1 = brE.readLine()) != null) {

                    String[] array = line1.split("-"); // Split according to the hyphen and put them in an array

                    List<String> explicaciones = new ArrayList<String>();
                    explicaciones.add(0,array[1]);
                    explicaciones.add(1,array[3]);
                    explicaciones.add(2,array[5]);
                    explicaciones.add(3,array[7]);

                    List<EmocionDto> e = new ArrayList<EmocionDto>();
                    e.add(0, emocionesDao.getEmocion(Integer.parseInt(array[2])));
                    e.add(1, emocionesDao.getEmocion(Integer.parseInt(array[4])));
                    e.add(2, emocionesDao.getEmocion(Integer.parseInt(array[6])));
                    ActividadRedaccionesDto a2 = new ActividadRedaccionesDto(Integer.parseInt(array[0]), e, explicaciones);

                    Log.e("ActividadRedaccionesDto",a2.emocionMain().getName() + "-" +a2.emocionB().getName() + "-" + a2.emocionC().getName());
                    Log.e("ActividadRedaccionesDto", String.valueOf(i));
                    listActB.add(i, a2);
                    i++;
                }
            }else{
                Log.e("FileE", "Archivo vacio");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */

}
