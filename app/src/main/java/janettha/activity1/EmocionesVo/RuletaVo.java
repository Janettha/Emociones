package janettha.activity1.EmocionesVo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.ui.phone.CompletableProgressDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import github.hellocsl.cursorwheel.CursorWheelLayout;
import janettha.activity1.Adaptadores.ImagenAdapterRuleta;
import janettha.activity1.EmocionesDao.EmocionesDao;
import janettha.activity1.EmocionesDelegate.EmocionesDelegate;
import janettha.activity1.R;
import janettha.activity1.Util.Factory;
import janettha.activity1.Util.Fecha;
import janettha.activity1.Util.MediaPlayerSounds;

/*
* https://github.com/lassana/continuous-audiorecorder
* */
public class RuletaVo extends AppCompatActivity implements CursorWheelLayout.OnMenuSelectedListener {
    private static final String TAG = "RuletaVo";

    CursorWheelLayout wheel_img;
    TextView textWheel;
    EmocionesDelegate emocionesDelegate;
    View v;
    LinearLayout lwheel;
    ImageView EmocionDialog;
    TextView NameEmocionDialog;
    Button buttonRecord, buttonStop, buttonPlay;
    Dialog dialog;
    LinearLayout llActivity2;
    boolean DialogFlag = false;

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFile;
    private static String mFileS;

    private final String extension = ".mp3";
    private static String mFileName = null;

    private RecordButton mRecordButton;

    MediaPlayerSounds mediaPlayerSounds;

    private MediaRecorder mRecorder;
    private StorageReference mStorage;

    private CompletableProgressDialog mProgress;

    public final String keySP = "UserSex";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editorSP;
    private String sexo, userName;

    private FirebaseAuth mAuth;

    public RuletaVo() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity2);

        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences(keySP, MODE_PRIVATE);
        //editorSP = sharedPreferences.edit();
        sexo = sharedPreferences.getString("sexo", "m");
        String nuevoUser = mAuth.getCurrentUser()+"_"+Calendar.getInstance().getTime();
        userName = sharedPreferences.getString("usuario", nuevoUser);

        //Crear directorio en la memoria interna.
        mFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiosEmociones/"+userName+"/";
        mFileS = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audiosEmociones/";
        File directorio = new File(mFileS, userName);
        //Muestro un mensaje en el logcat si no se creo la carpeta por algun motivo
        if (!directorio.mkdirs()) {
            Log.e("FILE", "Error: No se creo el directorio privado");
        }

        //View rootView = R.layout.activity_activity2.inflate(R.layout.fragment_preactivity, container, false);
        v = initViews();
        emocionesDelegate = new EmocionesDelegate();
        //listImg = new EmocionesDao();
        loadData(sexo, v);
        wheel_img.setOnMenuSelectedListener(this);
        /* Recording audio FIREBASE */
        mStorage = FirebaseStorage.getInstance().getReference();

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mediaPlayerSounds = new MediaPlayerSounds(RuletaVo.this);
        wheel_img.setOnMenuItemClickListener(new CursorWheelLayout.OnMenuItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                try {
                    mediaPlayerSounds.playSound(mediaPlayerSounds.loadRuleta());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadData(String s, View view) {
        //EmocionesDao e = new EmocionesDao();
        //listImg.Emociones(getApplicationContext(), s);
        ImagenAdapterRuleta adapter = new ImagenAdapterRuleta(getBaseContext(), view, sexo);
        wheel_img.setAdapter(adapter);
    }

    @SuppressLint("WrongViewCast")
    private View initViews() {
        wheel_img = (CursorWheelLayout) findViewById(R.id.wheel);
        return wheel_img;
    }



    @Override
    public void onItemSelected(CursorWheelLayout parent, View view, int pos) {

        final LinearLayout lwheel = (LinearLayout) findViewById(R.id.wheelLayout);
        final Button buttonDialog = (Button) findViewById(R.id.EmocionDialog);
        final TextView textWheel = (TextView) findViewById(R.id.textWheel);


        if(parent.getId() == R.id.wheel){
            //Toast.makeText(getBaseContext(), listImg.get(pos).getName(), Toast.LENGTH_SHORT).show();

            wheel_img.setOnMenuSelectedListener(new CursorWheelLayout.OnMenuSelectedListener() {
                public void onItemSelected(CursorWheelLayout parent, final View view, int pos) {

                    Fecha fecha = new Fecha();
                    DialogFlag = true;
                    //Toast.makeText(Activity2.this, "Top Menu click position:" + pos + " Dialog: "+DialogFlag, Toast.LENGTH_SHORT).show();

                    final int posicion = wheel_img.getSelectedPosition();
                    //mFileName = ""mFile;
                    mFileName = mFile;
                    //mFileName += listImg.get(wheel_img.getSelectedPosition()).getId() + "_" + listImg.get(wheel_img.getSelectedPosition()).getName() + extension;
                    SQLiteDatabase db = Factory.getBaseDatos(view.getContext());
                    mFileName += (pos+1)+"_"+emocionesDelegate.obtieneEmocion(pos, sexo, db).getName()+"_"+ fecha.getTime()+extension;

                    textWheel.setText(String.valueOf(posicion+1));
                    lwheel.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(posicion, sexo, db).getColor()));
                    buttonDialog.setVisibility(View.VISIBLE);
                    buttonDialog.setText(emocionesDelegate.obtieneEmocion(posicion, sexo, db).getName());
                    buttonDialog.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(posicion, sexo, db).getColorB()));
                    buttonDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                mediaPlayerSounds.playSound(mediaPlayerSounds.loadInicio());
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            MyCustomAlertDialog(posicion, view.getContext());
                        }
                    });
                    db.close();
                }
            });

        }
    }

    public void MyCustomAlertDialog(int pos, Context context){
        dialog = new Dialog(this);
        SQLiteDatabase db = Factory.getBaseDatos(context);
        dialog.setContentView(R.layout.dialog_act2);
        dialog.setTitle(emocionesDelegate.obtieneEmocion(pos, sexo, db).getName());

        Toast.makeText(this,"Pincha el botón verde para grabar.", Toast.LENGTH_SHORT).show();

        llActivity2 = (LinearLayout) dialog.findViewById(R.id.llact2);
        buttonRecord = (Button) dialog.findViewById(R.id.btnRecord);
        buttonStop = (Button) dialog.findViewById(R.id.btnStop);
        buttonPlay = (Button) dialog.findViewById(R.id.btnPlay);
        EmocionDialog = (ImageView) dialog.findViewById(R.id.imgEmocion);
        NameEmocionDialog = (TextView) dialog.findViewById(R.id.nameEmocion);

        Uri ruta = Uri.parse(emocionesDelegate.obtieneEmocion(pos, sexo, db).getUrl());
        Picasso.with(v.getContext())
                .load(ruta).fit()
                .into(EmocionDialog);
        llActivity2.setBackgroundColor(Color.parseColor(emocionesDelegate.obtieneEmocion(pos, sexo, db).getColor()));
        NameEmocionDialog.setText(emocionesDelegate.obtieneEmocion(pos, sexo, db).getName());
        buttonRecord.setEnabled(true);
        buttonStop.setEnabled(false);

        db.close();

        // Permisos
        if(checkPermissionFromDevice()) {
            buttonRecord.setEnabled(true);
            buttonRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        //mRecorder.prepare();
                        //mRecorder.start();
                    buttonRecord.setEnabled(false);
                    buttonStop.setEnabled(true);
                    //buttonRecord.setEnabled(false);
                    startRecording();
                    buttonRecord.setVisibility(View.INVISIBLE);
                    buttonStop.setVisibility(View.VISIBLE);
                    buttonStop.setEnabled(true);
                    Toast.makeText(RuletaVo.this, "Grabando...", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            requestPermissions();

        }


        buttonStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //buttonRecord.setEnabled(true);
                buttonStop.setEnabled(true);

                stopRecording();
                buttonStop.setVisibility(View.INVISIBLE);
                buttonPlay.setVisibility(View.VISIBLE);
                buttonPlay.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Grabación terminada...", Toast.LENGTH_SHORT).show();
                //DialogFlag = false;

            }
        });

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStop.setVisibility(View.INVISIBLE);
                mostrarFile();
                dialog.cancel();
            }
        });

        dialog.show();
    }

    public void mostrarFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        String path = mFileName;
        Uri uri = Uri.parse(path);
        //Uri uri = Uri.parse(mFileS+userName);
        Toast.makeText(this, path+"\n"+uri.toString(), Toast.LENGTH_SHORT).show();
        intent.setDataAndType(uri,"audio/mp3");
        if (intent.resolveActivity(getPackageManager()) != null) {
            //startActivity(intent);
            startActivity(Intent.createChooser(intent,"Abrir archivo"));
        }
    }

    private void requestPermissions() {
        Toast.makeText(this, "Por favor, conceda permiso para continuar.", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permiso otorgago.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permiso denegado.", Toast.LENGTH_SHORT).show();
                    requestPermissions();
                }
            }
            break;
        }
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        uploadAudio();
    }


    private void uploadAudio() {
        SQLiteDatabase db = Factory.getBaseDatos(this);
        String path = emocionesDelegate.obtieneEmocion(wheel_img.getSelectedPosition(), sexo, db).getEmocion() + "_" + emocionesDelegate.obtieneEmocion(wheel_img.getSelectedPosition(), sexo, db).getName() + extension;
        db.close();
        StorageReference filepath = mStorage.child("Audio").child(path);
        Uri uri = Uri.fromFile(new File(mFileName));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Audio guardado.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public class RecordButton extends android.support.v7.widget.AppCompatButton {

        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Grabación terminada.");
                } else {
                    setText("Iniciando grabación.");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Iniciando grabación.");
            setOnClickListener(clicker);
        }
    }

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return (write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED);
    }

}
