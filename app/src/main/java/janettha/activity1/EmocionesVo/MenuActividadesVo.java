package janettha.activity1.EmocionesVo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import janettha.activity1.EmocionesDelegate.ActividadesDelegate;
import janettha.activity1.EmocionesDelegate.UsuariosDelegate;
import janettha.activity1.EmocionesDto.IndicesActividadDto;
import janettha.activity1.EmocionesDto.UsuarioDto;
import janettha.activity1.Adaptadores.FragmentActivityRedacciones;
import janettha.activity1.R;
import janettha.activity1.Util.Factory;
import janettha.activity1.Util.MediaPlayerSounds;

import static android.provider.Settings.System.AIRPLANE_MODE_ON;
import static janettha.activity1.EmocionesVo.LoginUsuarioVo.keySP;

public class MenuActividadesVo extends AppCompatActivity {
    private static final String TAG = "MenuActividadesVo";

    private LinearLayout rootview, btnA1, btnA2, btnA3;
    private int a1, a2;
    private ProgressBar actUno, actDos;
    private TextView progresoUno, progresoDos;
    private ImageView imageView, imageView2;
    private VideoView video;

    //private SoundManager soundManager;

    private MediaPlayerSounds mediaPlayerSounds;

    private DatabaseReference mDatabaseUser;

    private SharedPreferences sharedPreferences;
    String userU, sexo;
    SQLiteDatabase db;
    ActividadesDelegate actividadesDelegate;
    IndicesActividadDto indicesActividadDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu_actividades);
        //usuarios = findViewById(R.id.usuarios);
        rootview = findViewById(R.id.rootview);
        btnA1 = findViewById(R.id.menu_act1);
        btnA2 = findViewById(R.id.menu_act2);
        btnA3 = findViewById(R.id.menu_act3);

        actUno = findViewById(R.id.bar_act1);
        actDos = findViewById(R.id.bar_act2);
        progresoUno = findViewById(R.id.txt_act1);
        progresoDos = findViewById(R.id.txt_act2);
        video = findViewById(R.id.videoViewPropiedad);
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);

        actividadesDelegate = new ActividadesDelegate();
        //Toolbar myToolbar = findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);

        sharedPreferences = getSharedPreferences(keySP, MODE_PRIVATE);
        sexo = sharedPreferences.getString("sexo", "m");
        userU = sharedPreferences.getString("usuario", "");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference("users");

        Log.d(TAG, "onCreate: s: "+sexo+" u: "+userU);
        Toast.makeText(getBaseContext(), "Usuario: "+userU, Toast.LENGTH_SHORT).show();

        db = Factory.getBaseDatos(this);
        //usuarioDto = delegateUsuario.traeUsuario(db, userU);
        indicesActividadDto = actividadesDelegate.obtieneIndices(db, userU);
        db.close();

        //se modifica la hora de último acceso del usuario seleccionado
        FirebaseDatabase.getInstance().getReference().child("users").child(userU).child("finS").setValue(Factory.formatoFechaHora());

        a1 = indicesActividadDto.getIndiceA();
        a2 = indicesActividadDto.getIndiceB();
        modificaProgresoFirebase(userU);

        inidiceActividad();

        modificaTres();

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayerSounds = new MediaPlayerSounds(this);

        /*
        usuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipalVo.this, ListaUsuariosVo.class);
                startActivity(intent);
            }
        });
        */
        btnA1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mediaPlayerSounds.playSound(mediaPlayerSounds.loadNewAct());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String time = Calendar.getInstance().getTime().toString();
                FirebaseDatabase.getInstance().getReference().child("users").child(userU).child("finS").setValue(time);
                Intent intent = new Intent(MenuActividadesVo.this, ImagenesVo.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                intent.putExtra("a1", a1);
                intent.putExtra("sexo", sexo);
                intent.putExtra("usuario", userU);
                startActivity( intent );
            }
        });

        btnA2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = Calendar.getInstance().getTime().toString();
                FirebaseDatabase.getInstance().getReference().child("users").child(userU).child("finS").setValue(time);
                Intent intent = new Intent(MenuActividadesVo.this, FragmentActivityRedacciones.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );

                if(a1 >= 12) {
                    Log.e("FragActivityRedacciones","YA ENTRAMOS - a1="+a1);
                    try {
                        mediaPlayerSounds.playSound(mediaPlayerSounds.loadNewAct());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    intent.putExtra("a2", a2);
                    intent.putExtra("sexo", sexo);
                    intent.putExtra("usuario", userU);
                    startActivity(intent);
                }else{
                    Snackbar.make(rootview, "Termina la Actividad Imágenes para continuar", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        btnA3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActividadesVo.this, RuletaVo.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );

                if(a2 >= 16) {
                    try {
                        mediaPlayerSounds.playSound(mediaPlayerSounds.loadNewAct());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    intent.putExtra("a3", 0);
                    intent.putExtra("sexo", sexo);
                    intent.putExtra("usuario", userU);
                    startActivity(intent);
                }else{
                    Snackbar.make(rootview, "Termina la Actividad Redacciones para continuar", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void modificaProgresoFirebase(String user) {
        FirebaseDatabase.getInstance().getReference().child("users").child(user).child("indiceA1").setValue(a1);
        FirebaseDatabase.getInstance().getReference().child("users").child(user).child("indiceA2").setValue(a2);
        FirebaseDatabase.getInstance().getReference().child("users").child(user).child("indiceA3").setValue(0);
    }

    private void modificaTres() {
        String uriPath, ruta, ruta2;
        if(sexo.equals("m")){
            uriPath = "android.resource://" + getBaseContext().getPackageName() + "/" + getBaseContext().getResources().getIdentifier("actividad_tres_m", "raw", getBaseContext().getPackageName());
            ruta = "android.resource://janettha.activity1/drawable/actividad_imagenes_m";
            ruta2 = "android.resource://janettha.activity1/drawable/actividad_redacciones_m";
        }else {
            uriPath = "android.resource://" + getBaseContext().getPackageName() + "/" + getBaseContext().getResources().getIdentifier("actividad_tres", "raw", getBaseContext().getPackageName());
            ruta = "android.resource://janettha.activity1/drawable/actividad_imagenes";
            ruta2 = "android.resource://janettha.activity1/drawable/actividad_redacciones";
        }

        //Imagen Menu imagenes
        Picasso.with(getBaseContext())
                .load(Uri.parse(ruta)).fit()
                .into(imageView);

        //Imagen Menu redacciones
        Picasso.with(getBaseContext())
                .load(Uri.parse(ruta2)).fit()
                .into(imageView2);

        //Imagen de video
        video.setVideoURI(Uri.parse(uriPath));
        video.start();
        //TODO quitar opacidad en dialogo
        video.setZOrderOnTop(true);
        //TODO repetir video
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if(db.isOpen()) db.close();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        startActivity(new Intent(MenuActividadesVo.this, ListaUsuariosVo.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
    }
    /*
        new AlertDialog.Builder(this)
                .setTitle("¿Realmente deseas salir?")
                .setMessage("El usuario actual será olvidado.")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MenuActividadesVo.super.onBackPressed();
                        if (arg1 == 0) {
                            }else{
                            String time = Calendar.getInstance().getTime().toString();
                            FirebaseDatabase.getInstance().getReference().child("users").child(userU).child("finS").setValue(time);
                            finish();
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                }).create().show();
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_activity1, menu);

        menu.findItem(R.id.action_previous).setEnabled(true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: //hago un case por si en un futuro agrego mas opciones
                Log.i("ActionBar", "Atrás!");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void inidiceActividad(){
        ValueEventListener mUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, UsuarioDto>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, UsuarioDto>>() {
                };
                Map<String, UsuarioDto> objectHashMap = dataSnapshot.getValue(objectsGTypeInd);
                ArrayList<UsuarioDto> objectArrayList = new ArrayList<UsuarioDto>(objectHashMap.values());
                for (int i = 0; i < objectArrayList.size(); i++) {
                    UsuarioDto userT = objectArrayList.get(i);
                    if (userT.getUser().equals(userU)) { // add newUser != null
                        a1 = userT.getIndiceA1();
                        a2 = userT.getIndiceA2();
                        int a3 = userT.getIndiceA3();
                        Log.e("Indices", a1 + "-" + a2 + "-" + a3);
                        modificaProgreso();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.e("DB Users/A123", "onCancelled: Failed to read DB Users");

            }
        };
        mDatabaseUser.addValueEventListener(mUserListener);
    }

    private void modificaProgreso() {
        //Porcentaje de actividades
        Log.d(TAG, "onCreate: "+a1+" - "+a2);
        if(a1>=12) {
            actUno.setProgress(100);
            progresoUno.setText("100%");
        }else {
            actUno.setProgress((a1*100)/12);
            progresoUno.setText(a1+"/"+12);
        }

        if(a2>=16) {
            actDos.setProgress(100);
            progresoDos.setText("100%");
        }else {
            actDos.setProgress((a2*100)/16);
            progresoDos.setText(a2+"/"+16);
        }
    }

    static boolean isAirplaneModeOn(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        return Settings.System.getInt(contentResolver, AIRPLANE_MODE_ON, 0) != 0;
    }
}
