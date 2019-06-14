package janettha.activity1.Adaptadores;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import janettha.activity1.EmocionesDelegate.EmocionesDelegate;
import janettha.activity1.EmocionesDto.ActividadImagenesDto;
import janettha.activity1.EmocionesDto.EmocionDto;
import janettha.activity1.EmocionesDao.EmocionesDao;
import janettha.activity1.EmocionesDto.RespuestaDto;
import janettha.activity1.EmocionesVo.MenuActividadesVo;
import janettha.activity1.Util.Factory;
import janettha.activity1.Util.SQLite;
import janettha.activity1.Util.TemplatePDF;
import janettha.activity1.R;
import janettha.activity1.Util.LockableViewPager;
import janettha.activity1.Util.MediaPlayerSounds;


public class SliderAdapterImagenes extends PagerAdapter {
    private static final String TAG = "SliderAdapterImagenes";

    Context context;
    LayoutInflater layoutInflater;
    LockableViewPager vp;

    static int A1;
    private int currentVP;

    List<ActividadImagenesDto> listActA = new ArrayList<>();
    EmocionesDelegate emocionesDelegate;

    private MediaPlayerSounds mediaPlayerSounds;

    private String sexo;
    private String user;
    private String tutor;

    private DatabaseReference mDatabaseUser;

    /*DIALOG*/
    private Dialog dialog;
    private LinearLayout llPreactivityDialog;
    private TextView nameEmocionDialog;
    private ImageView imgEmocionDialog;
    private Button btnBack;

    /*PDF RespuestaDto*/
    TemplatePDF templatePDF;
    ArrayList<RespuestaDto> respuestaDtos = new ArrayList<>();
    RespuestaDto respuestaDtoPDF;
    String fInicio, fFin;

    SQLiteDatabase db;

    public SliderAdapterImagenes(Context context, String userU, int numA1, ActividadImagenesDto a0, ActividadImagenesDto a1, ActividadImagenesDto a2, String s, LockableViewPager v) {

        if(context != null) Log.e("Context", context.toString());
        this.context = context;
        this.vp = v;
        A1 = numA1;

        //emocionesDao = new EmocionesDao();
        emocionesDelegate = new EmocionesDelegate();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference("users");

        listActA.add(0,a0);
        listActA.add(1,a1);
        listActA.add(2,a2);

        Log.e("emocionesA1List",a0.emocionMain().getEmocion()+"-"+a1.emocionMain().getEmocion()+"-"+a2.emocionMain().getEmocion());

        mediaPlayerSounds = new MediaPlayerSounds(context);

        sexo = s;
        //emocionesDao.Emociones(context, idSexo);
        respuestaDtoPDF = new RespuestaDto();
        user = userU;
        //Toast.makeText(context, "Usuario: "+user, Toast.LENGTH_SHORT).show();
/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundManager = new SoundManager(context);
        }
*/
        templatePDF = new TemplatePDF(context);
        pdfConfig();

    }


    @Override
    public int getCount() {
        return listActA.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View rootView = layoutInflater.inflate(R.layout.fragment_preactivity, container, false);

        ImageView backMenu = rootView.findViewById(R.id.backMenu);
        int r[] = new int[4];
        currentVP = position;
        fInicio = Factory.formatoFechaHora();


        r[1] = listActA.get(position).emocionMain().getEmocion();
        r[2] = listActA.get(position).emocionB().getEmocion();
        r[3] = listActA.get(position).emocionC().getEmocion();

        Log.e("emocionesA1",r[1]+"-"+r[2]+"-"+r[3]+" - posicion: "+position);
        Log.e("emocionesA1",listActA.get(position).emocionMain().getEmocion()
                +"-"+listActA.get(position).emocionB().getEmocion()
                +"-"+listActA.get(position).emocionC().getEmocion()
                +" - posicion: "+position);

        SQLiteDatabase db = Factory.getBaseDatos(context);
        r[0] = (int) (Math.random() * 3);
        if (r[0] == 0) {
            //rootView.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(r[1], sexo, db).getColor()));
            //interfaceFrame(rootView, imgFeel, btnA1, btnA2, btnA3, idSexo, rMain, rMain, rB, rC);
            interfaceFrame(rootView, r[1], r[1], r[2], r[3]);
        } else if (r[0] == 1) {
            //rootView.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(r[1], sexo, db).getColor()));
            //interfaceFrame(rootView, imgFeel, btnA1, btnA2, btnA3, idSexo, rMain, rC, rMain, rB);
            interfaceFrame(rootView, r[1], r[3], r[1], r[2]);
        } else if (r[0] == 2) {
            //rootView.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(r[1], sexo, db).getColor()));
            //interfaceFrame(rootView, imgFeel, btnA1, btnA2, btnA3, idSexo, rMain, rB, rC, rMain);
            interfaceFrame(rootView, r[1], r[2], r[3], r[1]);
        }
        Log.e("colorBG", emocionesDelegate.obtieneEmocion(r[1], sexo, db).getColor());

        db.close();
        container.addView(rootView);


        backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MenuActividadesVo.class);
                context.startActivity(intent);
            }
        });

        return rootView;
    }

    //private void interfaceFrame(View v, ImageView txFeel, Button txtFeel1, Button txtFeel2, Button txtFeel3, String s, int r, int r1, int r2, int r3) {
    private void interfaceFrame(View v, int r, int r1, int r2, int r3) {

        TextView indicaciones;
        ImageView txFeel;
        Button txtFeel1, txtFeel2, txtFeel3;

        Uri ruta;
        final int ex[] = new int[3];

        indicaciones = v.findViewById(R.id.Instruccion);
        txFeel = v.findViewById(R.id.imgFeel);

        txtFeel1 = v.findViewById(R.id.ans1);
        txtFeel2 = v.findViewById(R.id.ans2);
        txtFeel3 = v.findViewById(R.id.ans3);
/*
        if(sexo.equals("f")){
            indicaciones.setText("¿Cómo se siente Lili?");
        }else{
            indicaciones.setText("¿Cómo se siente Juan Carlos?");
        }
        */

        //expl = getArguments().getString(Activity1.ARG_r);
        ex[0] = r1;
        ex[1] = r2;
        ex[2] = r3;

        final int ran[] = new int[4];
        ran[0] = r;
        ran[1] = r1;
        ran[2] = r2;
        ran[3] = r3;

        /*Pictures de botones*/
        db = Factory.getBaseDatos(context);
        EmocionDto e = emocionesDelegate.obtieneEmocion(r, sexo, db);
        Log.d(TAG, "interfaceFrame: EMOCION: "+e.getString());
        ruta = Uri.parse(e.getUrl());
        Log.d(TAG, "interfaceFrame: RUTA: "+ruta);
        Picasso.with(v.getContext())
                .load(ruta).fit()
                .into(txFeel); //fit para la imagen en la vista

        /*Nombre y color de botones*/
        //v.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(r, sexo, db).getColor()));
        txtFeel1.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(r, sexo, db).getColorB()));
        txtFeel2.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(r, sexo, db).getColorB()));
        txtFeel3.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(r, sexo, db).getColorB()));

        txtFeel1.setText(emocionesDelegate.obtieneEmocion(ex[0], sexo, db).getName());
        txtFeel2.setText(emocionesDelegate.obtieneEmocion(ex[1], sexo, db).getName());
        txtFeel3.setText(emocionesDelegate.obtieneEmocion(ex[2], sexo, db).getName());

        db.close();

        txtFeel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean respTF;
                if (ran[0] == ran[1]) respTF=true;
                else             respTF=false;
                try {
                    mediaPlayerSounds.playSound(mediaPlayerSounds.loadSoundTF(respTF));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                db = Factory.getBaseDatos(context);
                MyCustomAlertDialog(v, emocionesDelegate.obtieneEmocion(ran[1], sexo, db), emocionesDelegate.obtieneEmocion(ran[1], sexo, db).getName(), respTF);
                db.close();
            }
        });
        txtFeel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean respTF;
                if (ran[0] == ran[2]) respTF=true;
                else             respTF=false;
                try {
                    mediaPlayerSounds.playSound(mediaPlayerSounds.loadSoundTF(respTF));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                db = Factory.getBaseDatos(context);
                MyCustomAlertDialog(v, emocionesDelegate.obtieneEmocion(ran[2], sexo, db), emocionesDelegate.obtieneEmocion(ran[2], sexo, db).getName(), respTF);
                db.close();
            }
        });
        txtFeel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean respTF;
                if (ran[0] == ran[3]) respTF=true;
                else             respTF=false;
                try {
                    mediaPlayerSounds.playSound(mediaPlayerSounds.loadSoundTF(respTF));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                db = Factory.getBaseDatos(context);
                MyCustomAlertDialog(v, emocionesDelegate.obtieneEmocion(ran[3], sexo, db), emocionesDelegate.obtieneEmocion(ran[3], sexo, db).getName(), respTF);
                db.close();
            }
        });
    }

    private void MyCustomAlertDialog(View v, EmocionDto em, String respuesta, final boolean resp) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_act0);
        dialog.setTitle("Ya casi lo logras");

        llPreactivityDialog = (LinearLayout) dialog.findViewById(R.id.lLaCT0);
        nameEmocionDialog = (TextView) dialog.findViewById(R.id.nameRespuesta);
        imgEmocionDialog = (ImageView) dialog.findViewById(R.id.imgRespuesta);
        btnBack = (Button) dialog.findViewById(R.id.btnBack);

        currentVP = vp.getPosition();
        fFin = Factory.formatoFechaHora();

        db = Factory.getBaseDatos(context);
            respuestaDtoPDF = new RespuestaDto(emocionesDelegate.obtieneEmocion(listActA.get(currentVP).emocionMain().getEmocion(), sexo, db).getEmocion(), fInicio, fFin, em.getEmocion(), resp);
        db.close();
        respuestaDtos.add(respuestaDtoPDF);

        Uri ruta = Uri.parse(em.getUrl());
        Picasso.with(context)
                .load(ruta).fit()
                .into(imgEmocionDialog);
        nameEmocionDialog.setText(respuesta);
        //nameEmocionDialog.setTextColor(Color.parseColor(em.getColorB()));
        llPreactivityDialog.setBackgroundColor(Color.parseColor(em.getColor()));

        if(!resp) {
            //dialog.setTitle("Inténtalo de nuevo");
            btnBack.setBackgroundResource(R.color.Incorrecto);
            btnBack.setText(" Inténtalo de nuevo ");
        }else if(resp) {
            //dialog.setTitle("Correcto");
            btnBack.setBackgroundResource(R.color.Correcto);
            btnBack.setText(" Correcto ");
        }
            //answer = true;
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(resp){
                    btnBack.setBackgroundResource(R.color.Correcto);
                    btnBack.setText(" Correcto ");
                    dialog.cancel();
                    vp.setPagingEnabled(true);
                    if(currentVP == 2) {
                        pdfConfig();
                        pdfView();
                        try {
                            mediaPlayerSounds.playSound(mediaPlayerSounds.loadFinEx());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //mDatabaseUser.child(user).child("indiceA1").toString();
                        //Log.e("DB/A1", "UserDB: "+mDatabaseUser.child(user).child("indiceA1").toString());
                        if(A1<12) {
                            int indice=listActA.get(0).emocionMain().getEmocion()+3;
                            modificaIndices(indice);
                            mDatabaseUser.child(user).child("indiceA1").setValue(indice);
                            Log.e("DB/A1", "User: " + user + " indiceA1: " + String.valueOf(indice));
                        }else{
                            try {
                                mediaPlayerSounds.playSound(mediaPlayerSounds.loadInicio());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    vp.setCurrentItem(currentVP+1);
                }else{
                    btnBack.setBackgroundResource(R.color.Incorrecto);
                    btnBack.setText(" Inténtalo de nuevo ");
                    dialog.cancel();
                }
            }
        });

        dialog.show();
    }

    private void modificaIndices(int indice) {
        SQLiteDatabase db = Factory.getBaseDatos(context);
        emocionesDelegate.modificaRespuestasActividadUno(db, user, indice);
        db.close();
    }

    private void pdfConfig(){
        templatePDF.openPDF();
        templatePDF.addMetaData(user);
        templatePDF.addHeader(user, fInicio, fFin, Factory.fechaFormat(context, Factory.formatoFechaHora()));
        templatePDF.addParrafo(1);
        templatePDF.createTable(respuestaDtos);
        templatePDF.closeDocument();
    }

    private void pdfView(){
        templatePDF.viewPDF();
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }

}
