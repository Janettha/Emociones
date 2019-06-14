package janettha.activity1.EmocionesVo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import janettha.activity1.EmocionesDao.ActividadesDao;
import janettha.activity1.EmocionesDao.EmocionesDao;
import janettha.activity1.EmocionesDelegate.ActividadesDelegate;
import janettha.activity1.EmocionesDelegate.EmocionesDelegate;
import janettha.activity1.EmocionesDelegate.SesionDelegate;
import janettha.activity1.R;
import janettha.activity1.Util.Factory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    LinearLayout linearLayoutDatos;
    private Button btnChangePassword, btnRemoveUser,
            changePassword, remove, signOut;
    private TextView email;

    private EditText oldEmail, password, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    SesionDelegate delegate;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    Boolean salir;

    FrameLayout fondo;
    ImageView imageView;
    boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fondo = findViewById(R.id.RelativeLayoutFondo);
        linearLayoutDatos = findViewById(R.id.datos_layout);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        email = (TextView) findViewById(R.id.useremail);

        sp = getSharedPreferences("firstTime", MODE_PRIVATE);
        editor = sp.edit();
        salir = false;

        firstTime = sp.getBoolean("firstTime", true);

        linearLayoutDatos.setVisibility(View.GONE);
        fondo.setVisibility(View.VISIBLE);

        if(getIntent().getExtras() != null){
            salir = getIntent().getExtras().getBoolean("salir");
        }

        if(salir) {
            Log.d(TAG, "onCreate: not null " + getIntent().getExtras());
            Animation animacion = AnimationUtils.loadAnimation(this, R.anim.animacion_portada);
            if(fondo != null) {
                Log.d(TAG, "onCreate: salir: " + true);
                salir = true;
                getIntent().getExtras().clear();
                fondo.startAnimation(animacion);
            }

            animacion.setAnimationListener(new AnimationHandler());
            /*if (getIntent().getExtras().getBoolean("salir")) {
                Log.d(TAG, "onCreate: salir: " + true);
                onfinish();
            }
            */
        }else{
            Log.d(TAG, "onCreate: Primera vez");
            Animation animacion = AnimationUtils.loadAnimation(this, R.anim.animacion_portada);
            if(fondo != null) {
                fondo.startAnimation(animacion);
            }

            animacion.setAnimationListener(new AnimationHandler());
            /*
            Intent intent = new Intent(this, SplashScreen.class);
            startActivity(intent);
            this.finish();
            */
        }

/*

        */
    }

    @SuppressLint("SetTextI18n")
    private void setDataToView(FirebaseUser user) {
        if(user != null)
            email.setText(user.getEmail());
        else
            email.setText("Bienvenido");
    }

    // this listener will be called when there is change in firebase user session
    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(MainActivity.this, LoginVo.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            } else {
                setDataToView(user);
            }
        }
    };

    //sign out method
    public void signOut() {
        auth.signOut();
        // this listener will be called when there is change in firebase user session
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
            // user auth state is changed - user is null
            // launch login activity
                startActivity(new Intent(MainActivity.this, LoginVo.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
//        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
        MainActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onfinish(){
        Log.d(TAG, "onfinish: ");
        finish();
    }

    class AnimationHandler implements Animation.AnimationListener {

        @Override
        public void onAnimationEnd(Animation animation) {
            if (salir) {
                Log.d(TAG, "onCreate: salir: endanimation" + true);
                onfinish();
            }/*else{
                Log.d(TAG, "onAnimationEnd: inicia");
                //Intent intento = new Intent(MainActivity.this, ListaUsuariosVo.class);
                //MainActivity.this.startActivity(intento);
                inicia();
            }
            */
            Log.d(TAG, "onAnimationEnd: salir: "+salir);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {        }

        @Override
        public void onAnimationStart(Animation animation) {
            if (!salir) {
                Log.d(TAG, "onAnimationEnd: inicia");
                //Intent intento = new Intent(MainActivity.this, ListaUsuariosVo.class);
                //MainActivity.this.startActivity(intento);
                inicia();
            }
            Log.d(TAG, "onAnimationStart: salir: "+salir);
        }
    }

    private void inicia() {
        delegate = new SesionDelegate(this);
        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setDataToView(user);

        SQLiteDatabase db = Factory.getBaseDatos(this);
        EmocionesDelegate emocionesDelegate = new EmocionesDelegate();
        ActividadesDelegate actividadesDelegate = new ActividadesDelegate();

        Log.d(TAG, "onCreate: "+firstTime);
        if(firstTime) {
            /*TODO Carga de emociones*/
            emocionesDelegate.cargaEmociones(this, db);
            //emocionesDelegate.cargaEmociones(this, db, "m");

            /*TODO Carga de actividades 1*/
            actividadesDelegate.cargaActividadImagenes(this, db, "f");
            actividadesDelegate.cargaActividadImagenes(this, db, "m");
            /*TODO Carga de actividades 2*/
            actividadesDelegate.cargaActividadRedacciones(this, db, "f");
            actividadesDelegate.cargaActividadRedacciones(this, db, "m");

            editor.putBoolean("firstTime", false);
            editor.commit();
            Log.d(TAG, "onCreate: "+sp.getBoolean("firstTime", true));
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // user auth state is changed - user is null
            // launch login activity
            Log.d(TAG, "onAuthStateChanged: manda a registrarse");
            startActivity(new Intent(MainActivity.this, RegistrarseVo.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            //startActivity(new Intent(MainActivity.this, LoginVo.class));
            finish();
        }else{
            Log.d(TAG, "onAuthStateChanged: manda a Lista de Usuarios");
            startActivity(new Intent(MainActivity.this, ListaUsuariosVo.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            finish();
            //startActivity(new Intent(MainActivity.this, LoginUsuarioVo.class));
        }
        /*authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        };*/
/*
        btnChangePassword = (Button) findViewById(R.id.change_password_button);
        btnRemoveUser = (Button) findViewById(R.id.remove_user_button);
        changePassword = (Button) findViewById(R.id.changePass);
        remove = (Button) findViewById(R.id.remove);
        signOut = (Button) findViewById(R.id.sign_out);
        oldEmail = (EditText) findViewById(R.id.old_email);
        password = (EditText) findViewById(R.id.password);
        newPassword = (EditText) findViewById(R.id.newPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        oldEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);

                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);

                changePassword.setVisibility(View.VISIBLE);

                remove.setVisibility(View.GONE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError("Contraseña demasiada corta, ingrese al menos 6 caracteres");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Contraseña modificafa, ingrese con su nueva contraseña", Toast.LENGTH_SHORT).show();
                                        signOut();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Falló el cambio de contraseña, inténtelo de nuevo", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError("Ingrese contraseña");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null) {
                    user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, RegistrarseVo.class));
                                finish();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                            }
                        });
                }
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        db.close();
    */
    }
}
