package janettha.activity1.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import janettha.activity1.EmocionesDelegate.EmocionesDelegate;
import janettha.activity1.EmocionesDto.ActividadImagenesDto;
import janettha.activity1.EmocionesDto.EmocionDto;
import janettha.activity1.EmocionesDao.EmocionesDao;
import janettha.activity1.EmocionesVo.ListaUsuariosVo;
import janettha.activity1.EmocionesVo.MenuActividadesVo;
import janettha.activity1.EmocionesVo.PreactividadVo;
import janettha.activity1.R;
import janettha.activity1.Util.Factory;
import janettha.activity1.Util.SoundManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentImagenes extends Fragment {
    private static final String TAG = "FragmentImagenes";

    private static final String ARG_PAGE = "section_number";
    private int mPageNumber;

    EmocionesDelegate emocionesDelegate;
    List<ActividadImagenesDto> btnList = new ArrayList<ActividadImagenesDto>();
    private final int LIM_emociones = 11;

    private String sexo;
    private int idEmocionMain, idEmocionB, idEmocionC;

    private static SoundManager soundManager;

    /*DIALOG*/
    Dialog dialog;
    LinearLayout llPreactivityDialog;
    TextView nameEmocionDialog;
    ImageView imgEmocionDialog;
    Button btnBack;

    public static FragmentImagenes create(int pageNumber, Context context, ActividadImagenesDto actividad0, String s) {

        FragmentImagenes fragment = new FragmentImagenes();
        Bundle args = new Bundle();

        //soundManager = new SoundManager(context);

        args.putInt(ARG_PAGE, actividad0.getIDMain());
        //args.putString(Activity1.ARG_tx,emocionesDao.get(id).getName());
        args.putInt(PreactividadVo.ARG_Main,actividad0.emocionMain().getEmocion());
        args.putInt(PreactividadVo.ARG_B,actividad0.emocionB().getEmocion());
        args.putInt(PreactividadVo.ARG_C,actividad0.emocionC().getEmocion());
        args.putString(ListaUsuariosVo.keySP, s);

        fragment.setArguments(args);
        return fragment;
    }

    public FragmentImagenes() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        emocionesDelegate = new EmocionesDelegate();

        if(getArguments() != null) {
            mPageNumber = getArguments().getInt(ARG_PAGE);
            idEmocionMain = getArguments().getInt(PreactividadVo.ARG_Main);
            idEmocionB = getArguments().getInt(PreactividadVo.ARG_B);
            idEmocionC = getArguments().getInt(PreactividadVo.ARG_C);
            sexo = getArguments().getString(ListaUsuariosVo.keySP);
            //emocionesDao.Emociones(getContext(), idSexo);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*VIEW*/
        ImageView imgFeel, backMenu;
        Button btnA1, btnA2, btnA3, btNext;

        Uri ruta;
        int rMain, rB, rC;
        int r = 0;

        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_preactivity, container, false);

        backMenu = rootView.findViewById(R.id.backMenu);
        imgFeel = (ImageView) rootView.findViewById(R.id.imgFeel);
        btnA1 = (Button) rootView.findViewById(R.id.ans1);
        btnA2 = (Button) rootView.findViewById(R.id.ans2);
        btnA3 = (Button) rootView.findViewById(R.id.ans3);

        SQLiteDatabase db = Factory.getBaseDatos(getContext());

        rMain = emocionesDelegate.obtieneEmocion(idEmocionMain, sexo, db).getEmocion();
        rB = emocionesDelegate.obtieneEmocion(idEmocionB, sexo, db).getEmocion();
        rC = emocionesDelegate.obtieneEmocion(idEmocionC, sexo, db).getEmocion();

        r = (int) (Math.random() * LIM_emociones ) ;
        if(r < 4){
            rootView.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(rMain, sexo, db).getColor()));
            interfaceFrame(rootView,imgFeel, btnA1, btnA2, btnA3, sexo, rMain, rMain, rB, rC);
        }else if(r>3 && r<8) {
            rootView.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(rMain, sexo, db).getColor()));
            interfaceFrame(rootView,imgFeel, btnA1, btnA2, btnA3, sexo, rMain, rC, rMain, rB);
        }else if(r>7 && r<12) {
            rootView.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(rMain, sexo, db).getColor()));
            interfaceFrame(rootView,imgFeel, btnA1, btnA2, btnA3, sexo, rMain, rB, rC, rMain);
        }

        db.close();

        backMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: "+getActivity().toString());
                Intent intent = new Intent(getContext(), MenuActividadesVo.class);
                startActivityForResult(intent, 0);
                getActivity().getSupportFragmentManager().getFragments().clear();
                getActivity().finish();
            }
        });

        return rootView;
    }

    public int getPageNumber() {
        return mPageNumber;
    }

    private void interfaceFrame(View v, ImageView txFeel, Button txtFeel1, Button txtFeel2, Button txtFeel3, String s, int r, int r1, int r2, int r3){

        Uri ruta;

        final int ex_1, ex_2, ex_3;
        //expl = getArguments().getString(Activity1.ARG_r);
        ex_1 = r1;
        ex_2 = r2;
        ex_3 = r3;

        final int ran, ran1, ran2, ran3;
        ran = r;
        ran1 = r1;
        ran2 = r2;
        ran3 = r3;

        final SQLiteDatabase db = Factory.getBaseDatos(getContext());

        /*Pictures de botones*/
        ruta = Uri.parse(emocionesDelegate.obtieneEmocion(r, sexo, db).getUrl());
        Picasso.with(v.getContext())
                .load(ruta).fit()
                .into(txFeel); //fit para la imagen en la vista

        /*Nombre y color de botones*/
        v.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(r, sexo, db).getColor()));
        txtFeel1.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(r, sexo, db).getColorB()));
        txtFeel2.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(r, sexo, db).getColorB()));
        txtFeel3.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(r, sexo, db).getColorB()));

        txtFeel1.setText(emocionesDelegate.obtieneEmocion(ex_1, sexo, db).getName());
        txtFeel2.setText(emocionesDelegate.obtieneEmocion(ex_2, sexo, db).getName());
        txtFeel3.setText(emocionesDelegate.obtieneEmocion(ex_3, sexo, db).getName());


        txtFeel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean respTF;
                if(ran == ran1) respTF = true;
                else            respTF = false;
                soundManager.playSound(soundManager.loadSoundTF(respTF));
                MyCustomAlertDialog(v, emocionesDelegate.obtieneEmocion(ran1, sexo, db), emocionesDelegate.obtieneEmocion(ran1, sexo, db).getName(), respTF);

            }
        });
        txtFeel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean respTF;
                if(ran == ran2) respTF = true;
                else            respTF = false;
                soundManager.playSound(soundManager.loadSoundTF(respTF));
                MyCustomAlertDialog(v, emocionesDelegate.obtieneEmocion(ran2, sexo, db), emocionesDelegate.obtieneEmocion(ran2, sexo, db).getName(), respTF);
            }
        });
        txtFeel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean respTF;
                if(ran == ran3) respTF = true;
                else            respTF = false;
                soundManager.playSound(soundManager.loadSoundTF(respTF));
                MyCustomAlertDialog(v, emocionesDelegate.obtieneEmocion(ran3, sexo, db), emocionesDelegate.obtieneEmocion(ran3, sexo, db).getName(),respTF);
            }
        });

        db.close();
    }

    public void MyCustomAlertDialog(View v, EmocionDto em, String respuesta, boolean resp) {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_act0);
        dialog.setTitle("Ya casi lo logras");

        llPreactivityDialog = (LinearLayout) dialog.findViewById(R.id.lLaCT0);
        nameEmocionDialog = (TextView) dialog.findViewById(R.id.nameRespuesta);
        imgEmocionDialog = (ImageView) dialog.findViewById(R.id.imgRespuesta);
        btnBack = (Button) dialog.findViewById(R.id.btnBack);

        if(resp == false) {
            //dialog.setTitle("Inténtalo de nuevo");
            btnBack.setBackgroundResource(R.color.Incorrecto);
            btnBack.setText(" Inténtalo de nuevo ");
        }else if(resp == true) {
            //dialog.setTitle("Correcto");
            btnBack.setBackgroundResource(R.color.Correcto);
            btnBack.setText(" Correcto ");
        }

        Uri ruta = Uri.parse(em.getUrl());
        Picasso.with(getContext())
                .load(ruta).fit()
                .into(imgEmocionDialog);
        nameEmocionDialog.setText(respuesta);
        //nameEmocionDialog.setTextColor(Color.parseColor(em.getColorB()));
        llPreactivityDialog.setBackgroundColor(Color.parseColor(em.getColor()));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
