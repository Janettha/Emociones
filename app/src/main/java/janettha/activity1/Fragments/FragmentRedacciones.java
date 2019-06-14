package janettha.activity1.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import janettha.activity1.Adaptadores.FragmentActivityRedacciones;
import janettha.activity1.EmocionesDelegate.EmocionesDelegate;
import janettha.activity1.EmocionesDto.ActividadRedaccionesDto;
import janettha.activity1.EmocionesDto.EmocionDto;
import janettha.activity1.EmocionesDao.EmocionesDao;
import janettha.activity1.EmocionesDto.RespuestaDto;
import janettha.activity1.EmocionesVo.MenuActividadesVo;
import janettha.activity1.Util.Factory;
import janettha.activity1.Util.TemplatePDF;
import janettha.activity1.R;
import janettha.activity1.Util.LockableViewPager;
import janettha.activity1.Util.MediaPlayerSounds;


public class FragmentRedacciones extends Fragment {
    private static final String TAG = "FragmentRedacciones";

    @SuppressLint("StaticFieldLeak")
    static Context context;
    LayoutInflater layoutInflater;

    static LockableViewPager vp;
    //boolean vp;

    EmocionesDelegate emocionesDelegate;

    private static int mPageNumber;
    private static int idDBA2;
    private String textoRedaccion;
    private String exEmocion1, exEmocion2, exEmocion3, sexo, user;
    private int idEmocion1;
    private int idEmocion2;
    private int idEmocion3;
    TextToSpeech t1;
    private static MediaPlayerSounds mediaPlayerSounds;

    ImageView backMenu;

    /*DIALOG*/
    Dialog dialog;
    TextView indicaciones, explicacionDialogo, nameEmocionDialog;
    ImageView imgEmocionDialog;
    Button btnBack;

    SQLiteDatabase db;
    private DatabaseReference mDatabaseUser;

    /*PDF RespuestaDto*/
    @SuppressLint("StaticFieldLeak")
    static TemplatePDF templatePDF;
    ArrayList<RespuestaDto> respuestaDtos = new ArrayList<>();
    RespuestaDto respuestaDtoPDF;
    static String fInicio, fFin;

    public static FragmentRedacciones create(int pageNumber, Context c, String user, int numA2DB, ActividadRedaccionesDto actividadDto, String sexo, LockableViewPager lockVP, TemplatePDF tPDF) {

        FragmentRedacciones fragment = new FragmentRedacciones();
        Bundle args = new Bundle();
        vp = lockVP;
        templatePDF = tPDF;
        context = c;

        fInicio = Factory.formatoFechaHora();
        mPageNumber = pageNumber;
        int a2 = actividadDto.emocionMain().getEmocion();
        idDBA2 = numA2DB;

        Log.e("Fragment A2a","numPager: "+ mPageNumber);
        Log.e("Fragment A2a","numActividad: "+ a2);
        Log.e("Fragment A2a","nActividad_DB: "+ idDBA2);
        Log.e("Fragment A2a","nameActividad: "+ actividadDto.emocionMain().getName());

        args.putString(FragmentActivityRedacciones.ARG_r, actividadDto.getRedaccion());
        args.putString(FragmentActivityRedacciones.ARG_e1, actividadDto.getExpl1());
        args.putInt(FragmentActivityRedacciones.ARG_IDe1, actividadDto.emocionMain().getEmocion());
        args.putString(FragmentActivityRedacciones.ARG_e2, actividadDto.getExpl2());
        args.putInt(FragmentActivityRedacciones.ARG_IDe2, actividadDto.emocionB().getEmocion());
        args.putString(FragmentActivityRedacciones.ARG_e3, actividadDto.getExpl3());
        args.putInt(FragmentActivityRedacciones.ARG_IDe3, actividadDto.emocionC().getEmocion());
        args.putString(FragmentActivityRedacciones.ARG_sexo, sexo);
        args.putString(FragmentActivityRedacciones.ARG_u,user);

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentRedacciones() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        emocionesDelegate = new EmocionesDelegate();

        if(getArguments() != null) {
            textoRedaccion = getArguments().getString(FragmentActivityRedacciones.ARG_r);
            idEmocion1 = getArguments().getInt(FragmentActivityRedacciones.ARG_IDe1);
            exEmocion1 = getArguments().getString(FragmentActivityRedacciones.ARG_e1);
            idEmocion2 = getArguments().getInt(FragmentActivityRedacciones.ARG_IDe2);
            exEmocion2 = getArguments().getString(FragmentActivityRedacciones.ARG_e2);
            idEmocion3 = getArguments().getInt(FragmentActivityRedacciones.ARG_IDe3);
            exEmocion3 = getArguments().getString(FragmentActivityRedacciones.ARG_e3);
            user = getArguments().getString(FragmentActivityRedacciones.ARG_u);
            sexo = getArguments().getString(FragmentActivityRedacciones.ARG_sexo);
            //emocionesDao.Emociones(getContext(), sexo);
        }

        respuestaDtoPDF = new RespuestaDto();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference("users");
        mediaPlayerSounds = new MediaPlayerSounds(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        db = Factory.getBaseDatos(getContext());
        int id[] = new int[3];
        int r;

        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_act1, container, false);

        backMenu = rootView.findViewById(R.id.backMenu);

        /*REDACCION*/
        ((TextView) rootView.findViewById(R.id.txtText)).setText(textoRedaccion);
        indicaciones = rootView.findViewById(R.id.Instruccion);
        Button btn = rootView.findViewById(R.id.btnSpeak);

        /*background*/
        LinearLayout bgAct1 = rootView.findViewById(R.id.LRed);
        TextView txRedaccion = rootView.findViewById(R.id.txtText);

        /*RESPUESTAS*/
        ImageButton btnE1 = rootView.findViewById(R.id.imgRedaccion);
        TextView txR1 = rootView.findViewById(R.id.txRedaccion);
        ImageButton btnE2 = rootView.findViewById(R.id.imgRedaccion2);
        TextView txR2 = rootView.findViewById(R.id.txRedaccion2);
        ImageButton btnE3 = rootView.findViewById(R.id.imgRedaccion3);
        TextView txR3 = rootView.findViewById(R.id.txRedaccion3);

        id[0] = emocionesDelegate.obtieneEmocion(idEmocion1, sexo, db).getEmocion();
        id[1] = emocionesDelegate.obtieneEmocion(idEmocion2, sexo, db).getEmocion();
        id[2] = emocionesDelegate.obtieneEmocion(idEmocion3, sexo, db).getEmocion();

        if(sexo.equals("f")){
            indicaciones.setText("¿Cómo se siente Lili?");
        }else{
            indicaciones.setText("¿Cómo se siente Juan Carlos?");
        }

        //bgAct1.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(id[0], sexo, db).getColor()));
        //txRedaccion.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(id[0], sexo, db).getColor()));

        r = (int) (Math.random() * 3 ) ;
        switch (r){
            case 0:
                //rootView.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(id[0], sexo, db).getColor()));
                interfaceFrame(rootView, btnE1, txR1, btnE2, txR2, btnE3, txR3, sexo,id[0], id[0], id[1], id[2], id[1], exEmocion1, exEmocion2, exEmocion3);
                break;
            case 1:
                //rootView.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(id[0], sexo, db).getColor()));
                interfaceFrame(rootView, btnE1, txR1, btnE2, txR2, btnE3, txR3, sexo, id[0], id[2], id[0], id[1], id[1], exEmocion3, exEmocion1, exEmocion2);
                break;
            case 2:
                //rootView.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(id[0], sexo, db).getColor()));
                interfaceFrame(rootView, btnE1, txR1, btnE2, txR2, btnE3, txR3, sexo, id[0], id[1], id[2], id[0], id[1], exEmocion2, exEmocion3, exEmocion1);
                break;
        }

        t1=new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.getDefault());
                }
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = textoRedaccion;
                Toast.makeText(getContext(), "Reproduciendo...",Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        db.close();

        backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: "+getActivity().toString());
                Intent intent = new Intent(getContext(), MenuActividadesVo.class);
                startActivityForResult(intent, 0);
                getActivity().finish();
            }
        });

        return rootView;
    }

    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (db.isOpen())
            db.close();
    }
    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    private void interfaceFrame(View v,
                                ImageButton imgFeel, TextView txFeel1,
                                ImageButton imgFeel2, TextView txFeel2,
                                ImageButton imgFeel3, TextView txFeel3,
                                String s, int r, int r1, int r2, int r3, int rOpcional,
                                String e1, String e2, String e3){
        db = Factory.getBaseDatos(getContext());
        Uri ruta;
        final String ex[] = new String [3];
        final int ran[] = new int[5];

        ex[0] = e1;
        ex[1] = e2;
        ex[2] = e3;

        ran[0] = r;
        ran[1] = r1;
        ran[2] = r2;
        ran[3] = r3;
        ran[4] = rOpcional;

        /*Nombre de botones*/
        txFeel1.setText(emocionesDelegate.obtieneEmocion(r1, sexo, db).getName());
        txFeel1.setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(r, sexo, db).getColorB()));
        txFeel2.setText(emocionesDelegate.obtieneEmocion(r2, sexo, db).getName());
        txFeel2.setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(r, sexo, db).getColorB()));
        txFeel3.setText(emocionesDelegate.obtieneEmocion(r3, sexo, db).getName());
        txFeel3.setTextColor(android.graphics.Color.parseColor(emocionesDelegate.obtieneEmocion(r, sexo, db).getColorB()));

        /*Pictures de botones*/
        ruta = Uri.parse(emocionesDelegate.obtieneEmocion(r1, sexo, db).getUrl());
        Picasso.with(v.getContext())
                .load(ruta).fit()
                .into(imgFeel); //fit para la imagen en la vista

        ruta = Uri.parse(emocionesDelegate.obtieneEmocion(r2, sexo, db).getUrl());
        Picasso.with(v.getContext())
                .load(ruta).fit()
                .into(imgFeel2); //fit para la imagen en la vista

        ruta = Uri.parse(emocionesDelegate.obtieneEmocion(r3, sexo, db).getUrl());
        Picasso.with(v.getContext())
                .load(ruta).fit()
                .into(imgFeel3); //fit para la imagen en la vista

        imgFeel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ran[0] == ran[1]) {
                    try {
                        mediaPlayerSounds.playSound(mediaPlayerSounds.loadSoundTF(true));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    db = Factory.getBaseDatos(getContext());
                    MyCustomAlertDialog(v, ran[0], emocionesDelegate.obtieneEmocion(ran[1], sexo, db), "¡Correcto!");
                    db.close();
                } else if(ran[4] == ran[1]){
                    try {
                        mediaPlayerSounds.playSound(mediaPlayerSounds.loadSoundTF(true));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    db = Factory.getBaseDatos(getContext());
                    MyCustomAlertDialog(v, ran[0], emocionesDelegate.obtieneEmocion(ran[1], sexo, db), "Casi lo logras...\n"+ex[0]);
                    db.close();
                } else {
                    try {
                        mediaPlayerSounds.playSound(mediaPlayerSounds.loadSoundTF(false));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    db = Factory.getBaseDatos(getContext());
                    MyCustomAlertDialog(v, ran[0], emocionesDelegate.obtieneEmocion(ran[1], sexo, db), "Oh ohhh...\n"+ex[0]);
                    db.close();
                }
            }
        });
        imgFeel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ran[0] == ran[2]) {
                    try {
                        mediaPlayerSounds.playSound(mediaPlayerSounds.loadSoundTF(true));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    db = Factory.getBaseDatos(getContext());
                    MyCustomAlertDialog(v, ran[0], emocionesDelegate.obtieneEmocion(ran[2], sexo, db), "CORRECTO");
                    db.close();
                } else if(ran[4] == ran[2]){
                    try {
                        mediaPlayerSounds.playSound(mediaPlayerSounds.loadSoundTF(true));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    db = Factory.getBaseDatos(getContext());
                    MyCustomAlertDialog(v, ran[0], emocionesDelegate.obtieneEmocion(ran[2], sexo, db), "Casi lo logras...\n"+ex[1]);
                    db.close();
                } else{
                    try {
                        mediaPlayerSounds.playSound(mediaPlayerSounds.loadSoundTF(false));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    db = Factory.getBaseDatos(getContext());
                    MyCustomAlertDialog(v, ran[0], emocionesDelegate.obtieneEmocion(ran[2], sexo, db),"Oh ohhh...\n"+ex[1]);
                    db.close();
                }
            }
        });
        imgFeel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ran[0] == ran[3]) {
                    try {
                        mediaPlayerSounds.playSound(mediaPlayerSounds.loadSoundTF(true));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    db = Factory.getBaseDatos(getContext());
                    MyCustomAlertDialog(v, ran[0], emocionesDelegate.obtieneEmocion(ran[3], sexo, db), "CORRECTO");
                    db.close();
                }else if(ran[4] == ran[3]){
                    try {
                        mediaPlayerSounds.playSound(mediaPlayerSounds.loadSoundTF(true));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    db = Factory.getBaseDatos(getContext());
                    MyCustomAlertDialog(v, ran[0], emocionesDelegate.obtieneEmocion(ran[3], sexo, db), "Casi lo logras...\n"+ex[2]);
                    db.close();
                } else {
                    try {
                        mediaPlayerSounds.playSound(mediaPlayerSounds.loadSoundTF(false));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    db = Factory.getBaseDatos(getContext());
                    MyCustomAlertDialog(v, ran[0], emocionesDelegate.obtieneEmocion(ran[3], sexo, db), "Oh ohhh...\n"+ex[2]);
                    db.close();
                }
            }
        });
        db.close();

    }

    @SuppressLint("ResourceAsColor")
    public void MyCustomAlertDialog(View v, int mainE, EmocionDto em, String explicacion) {

        final boolean respuesta = (mainE == em.getEmocion());

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_act1);
        dialog.setTitle(em.getName());

        LinearLayout llDialog = dialog.findViewById(R.id.LLDialogA1);
        explicacionDialogo = dialog.findViewById(R.id.Explicacion);
        nameEmocionDialog = dialog.findViewById(R.id.nameRespuesta);
        imgEmocionDialog = dialog.findViewById(R.id.imgRespuesta);
        btnBack = dialog.findViewById(R.id.btnBack);

        llDialog.setBackgroundColor(Color.parseColor(em.getColor()));

        Uri ruta = Uri.parse(em.getUrl());
        Picasso.with(getContext())
                .load(ruta).fit()
                .into(imgEmocionDialog);
        explicacionDialogo.setText(explicacion);
        nameEmocionDialog.setText(em.getName());

        if(!respuesta){
            btnBack.setBackgroundResource(R.color.Incorrecto);
            btnBack.setText(" Inténtalo de nuevo ");
        }else {
            btnBack.setBackgroundResource(R.color.Correcto);
            nameEmocionDialog.setText(" ");
            btnBack.setText(em.getName());
            explicacionDialogo.setText(" ¡LO LOGRASTE! ");
        }

        fFin = Factory.formatoFechaHora();
        db = Factory.getBaseDatos(getContext());
        respuestaDtoPDF = new RespuestaDto(em.getEmocion(), fInicio, fFin, emocionesDelegate.obtieneEmocion(mainE, sexo, db).getEmocion(), respuesta);
        db.close();
        respuestaDtos.add(respuestaDtoPDF);
        templatePDF.addRespuesta(respuestaDtoPDF);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("Fragment A2 Dialog", "INI numPager: " + mPageNumber);
                Log.e("Fragment A2 Dialog", "INI vpNum: " + vp.getCurrentItem());
                if (respuesta && vp.getCurrentItem() == 2) {
                    try {
                        mediaPlayerSounds.playSound(mediaPlayerSounds.loadFinEx());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pdfConfig();
                    pdfView();
                    Log.e("DB/A2", "UserDB: " + mDatabaseUser.child(user).child("indiceA2").toString());
                    if (idDBA2 < 17) {
                        int indice = idDBA2 + 3;
                        modificaIndices(indice);
                        mDatabaseUser.child(user).child("indiceA2").setValue(indice);
                        Log.e("DB/A2", "User: " + user + " indiceA2: " + String.valueOf(indice));
                    }else{
                        try {
                            mediaPlayerSounds.playSound(mediaPlayerSounds.loadInicio());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (respuesta && vp.getCurrentItem() < 2) {
                    vp.setCurrentItem(vp.getCurrentItem() + 1);
                    vp.setPagingEnabled(true);
                }
                Log.e("Fragment A2 Dialog", "FIN numPager: " + mPageNumber);
                Log.e("Fragment A2 Dialog", "FIN vpNum: " + vp.getCurrentItem());

                dialog.cancel();
            }
        });

        dialog.show();

    }

    private void modificaIndices(int indice) {
        SQLiteDatabase db = Factory.getBaseDatos(context);
        emocionesDelegate.modificaRespuestasActividadDos(db, user, indice);
        db.close();
    }

    private void pdfConfig(){
        templatePDF.openPDF();
        templatePDF.addMetaData(user);
        templatePDF.addHeader(user, fInicio, fFin, Factory.formatoFechaHora());
        templatePDF.addParrafo(2);
        templatePDF.createTable(templatePDF.getRespuestasActB());
        templatePDF.closeDocument();
    }

    public void pdfView(){
        templatePDF.viewPDF();
    }

    public ArrayList<RespuestaDto> getRespuestaDtos(){
        return respuestaDtos;
    }

}
