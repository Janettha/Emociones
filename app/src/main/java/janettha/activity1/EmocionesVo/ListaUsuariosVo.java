package janettha.activity1.EmocionesVo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import janettha.activity1.Adaptadores.RecyclerViewAdapterUsuarios;
import janettha.activity1.EmocionesDelegate.SesionDelegate;
import janettha.activity1.EmocionesDelegate.UsuariosDelegate;
import janettha.activity1.EmocionesDto.TutoresDto;
import janettha.activity1.EmocionesDto.UsuarioDto;
import janettha.activity1.R;
import janettha.activity1.Util.DatePickerFragment;
import janettha.activity1.Util.Factory;
import janettha.activity1.Util.Fecha;

public class ListaUsuariosVo extends AppCompatActivity {
    private static final String TAG = "ListaUsuariosVo";

    RelativeLayout rootview;
    Toolbar toolbar;
    TextView nombreTutor;
    RecyclerView recyclerView;
    RecyclerViewAdapterUsuarios adapter;
    LinearLayoutManager layoutManager;
    FloatingActionButton agregarUsuario;

    /*Dialogo agregar usuario*/
    ImageView logoM;
    ImageView logoF;
    Dialog dialogoAgregar;
    EditText nombreAdd;
    EditText apellidosAdd;
    EditText usuarioAdd;
    EditText passAdd;
    EditText fechaAdd;
    TextView edadAdd;
    RadioGroup sexoAdd;
    RadioButton femeninoAdd;
    RadioButton masculinoAdd;
    Button agregarAdd;
    Button regresarAdd;
    TextView loginAdd;

    /*Dialogo login usuario*/
    Dialog dialogoLogin;
    EditText usuarioLogin;
    EditText passLogin;
    Button logLogin;
    Button regresarLogin;
    TextView agregarLogin;

    private FirebaseAuth mAuth;
    private FirebaseUser tutor;
    private DatabaseReference mReferenceTutor;
    private DatabaseReference mDatabaseUser;
    private ValueEventListener mTutorListener;
    private ValueEventListener mUserListener;

    String idTutor;
    boolean agregado;
    SesionDelegate sesionDelegate;
    UsuariosDelegate usuariosDelegate;
    List<UsuarioDto> listUsers = new ArrayList<UsuarioDto>();
    UsuarioDto usuarioDto;

    SQLiteDatabase db;
    public static final String keySP = "UserSex";
    private SharedPreferences sp;
    private SharedPreferences.Editor editorSP;
    String emailTutor;

    MenuItem cerrarSesion;
    MenuItem cambiarPass;
    MenuItem infoPersonal;

    DatabaseReference usuarioDB;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_list);
        rootview = findViewById(R.id.rootview);
        toolbar = findViewById(R.id.toolbar);
        nombreTutor = findViewById(R.id.tutor);
        recyclerView = findViewById(R.id.recycler_view);
        agregarUsuario = findViewById(R.id.agregar_usuario);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences(keySP, MODE_PRIVATE);
        editorSP = sp.edit();

        mAuth = FirebaseAuth.getInstance();
        //usuarioDB = FirebaseDatabase.getInstance().getReference().child("users").child(usuarioDto.getUser());
        mReferenceTutor = FirebaseDatabase.getInstance().getReference("tutores");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference("users");

        sesionDelegate = new SesionDelegate();
        usuariosDelegate = new UsuariosDelegate(this);
        usuarioDto = new UsuarioDto();
        db  = Factory.getBaseDatos(this);
        listUsers = usuariosDelegate.getUsuarios(db);
        db.close();

        emailTutor = mAuth.getCurrentUser().getEmail();
        idTutor = sp.getString("tutor", "");
        if(idTutor.equals(" ") || idTutor.equals("") || idTutor.equals("null")||idTutor.equals(emailTutor)|| idTutor == null){
            db  = Factory.getBaseDatos(this);
            Log.d(TAG, "onCreate: email: "+emailTutor);
            TutoresDto tutorDto = sesionDelegate.getTutorEmail(emailTutor, db);
            Log.d(TAG, "onCreate: "+tutorDto.getString());
            idTutor = tutorDto.getTutor();
            Log.d(TAG, "onCreate: idTutor: "+idTutor);
            db.close();
            editorSP.putString("tutor", idTutor);
        }
        Log.d(TAG, "onCreate: idTutor: "+idTutor);
        nombreTutor.setText(emailTutor.split("@")[0]);
        agregado = false;

        if (listUsers.size() == 0) {
            if(checkPermissionFromDevice()){
                dialogoAgregarUsuario();
            }else{
                requestPermissions();
            }
            //Toast.makeText(this, "Agregue un alumno a la lista.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "onCreate: RECYCLERVIEW");
            recyclerView.setHasFixedSize(true);

            layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);

            adapter = new RecyclerViewAdapterUsuarios(idTutor, ListaUsuariosVo.this, this, listUsers);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            Log.d(TAG, "onCreate: RECYCLERVIEW: "+recyclerView.toString());

            if(!checkPermissionFromDevice()){
                requestPermissions();
            }
        }

        agregarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermissionFromDevice()){
                    dialogoAgregarUsuario();
                }else{
                    requestPermissions();
                }
                /*
                if (agregado) {
                    db  = Factory.getBaseDatos(getApplicationContext());
                    listUsers = usuariosDelegate.getUsuarios(db);
                    db.close();

                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    adapter = new RecyclerViewAdapterUsuarios(idTutor, ListaUsuariosVo.this, getApplicationContext(), listUsers);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                */
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(db.isOpen())
            db.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        db  = Factory.getBaseDatos(this);
        listUsers = usuariosDelegate.getUsuarios(db);
        db.close();
        if(listUsers.size() > 0){
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new RecyclerViewAdapterUsuarios(idTutor, ListaUsuariosVo.this, getApplicationContext(), listUsers);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        db  = Factory.getBaseDatos(this);
        listUsers = usuariosDelegate.getUsuarios(db);
        db.close();

        if(listUsers.size() > 0){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //usuarioDB.removeEventListener(addUserValueEventListener);
        //usuarioDB.removeEventListener(loginUsuarioValueEventListener);

        agregado = false;
        db  = Factory.getBaseDatos(this);
        listUsers = usuariosDelegate.getUsuarios(db);
        db.close();

        if(listUsers.size() > 0){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        agregado = false;
        db  = Factory.getBaseDatos(this);

        listUsers = usuariosDelegate.getUsuarios(db);
        db.close();

        if(listUsers.size() > 0){
            adapter.notifyDataSetChanged();
        }
    }

    private void dialogoAgregarUsuario() {
        final String[] sexo = {"m"};
        agregado = false;
        dialogoAgregar = new Dialog(this);
        dialogoAgregar.setContentView(R.layout.dialogo_agregar_usuario);

        /*Dialogo agregar usuario*/
        logoF = dialogoAgregar.findViewById(R.id.logoF_add);
        logoM = dialogoAgregar.findViewById(R.id.logoM_add);
        nombreAdd = dialogoAgregar.findViewById(R.id.nombre_add);
        apellidosAdd = dialogoAgregar.findViewById(R.id.apellidos_add);
        usuarioAdd = dialogoAgregar.findViewById(R.id.usuario_add);
        passAdd = dialogoAgregar.findViewById(R.id.password_add);
        fechaAdd = dialogoAgregar.findViewById(R.id.fecha_add);
        edadAdd = dialogoAgregar.findViewById(R.id.edad_add);
        sexoAdd = dialogoAgregar.findViewById(R.id.radio_group_add);
        femeninoAdd = dialogoAgregar.findViewById(R.id.femenino_add);
        masculinoAdd = dialogoAgregar.findViewById(R.id.masculino_add);
        loginAdd = dialogoAgregar.findViewById(R.id.login_add);
        agregarAdd = dialogoAgregar.findViewById(R.id.agregar_add);
        regresarAdd = dialogoAgregar.findViewById(R.id.regresar_add);

        fechaAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.fecha_add:
                        showDatePickerDialog();
                        break;
                }
            }
        });

        sexoAdd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = sexoAdd.findViewById(checkedId);
                int index = sexoAdd.indexOfChild(radioButton);
                switch (index) {
                    case 0:
                        femeninoAdd.setTextColor(getResources().getColor(R.color.colorTxt));
                        sexo[0] = "f";
                        logoF.setVisibility(View.VISIBLE);
                        logoM.setVisibility(View.GONE);
                        break;
                    case 1:
                        masculinoAdd.setTextColor(getResources().getColor(R.color.colorTxt));
                        sexo[0] = "m";
                        logoM.setVisibility(View.VISIBLE);
                        logoF.setVisibility(View.GONE);
                        break;
                }
            }
        });

        agregarAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(usuarioAdd.getText())) {
                    usuarioAdd.setError("Campo requerido");
                }else if (TextUtils.isEmpty(passAdd.getText())) {
                    passAdd.setError("Campo requerido");
                }else if (TextUtils.isEmpty(nombreAdd.getText())) {
                    nombreAdd.setError("Campo requerido");
                }else if (TextUtils.isEmpty(apellidosAdd.getText())) {
                    apellidosAdd.setError("Campo requerido");
                }else if (TextUtils.isEmpty(fechaAdd.getText())) {
                    fechaAdd.setError("Campo requerido");
                }else if (sexo.length<0) {
                    Toast.makeText(ListaUsuariosVo.this, "Elija sexo: femenino o masculino", Toast.LENGTH_SHORT).show();
                }else if(passAdd.getText().length() <6){
                    passAdd.setError("Contraseña demasiada corta");
                }else {
                    final String[] edad = edadAdd.getText().toString().split(" ");
                    //final UsuarioDto user = new UsuarioDto();
                    usuarioDto.setNombre(nombreAdd.getText().toString());
                    usuarioDto.setApellidos(apellidosAdd.getText().toString());
                    usuarioDto.setUser(usuarioAdd.getText().toString());
                    usuarioDto.setPassword(passAdd.getText().toString());
                    usuarioDto.setCumple(fechaAdd.getText().toString());
                    usuarioDto.setEdad(Integer.valueOf(edad[0]));
                    usuarioDto.setSexo(sexo[0]);
                    usuarioDto.setTutor(idTutor);
                    Log.d(TAG, "onClick: tutor: " + idTutor);
                    usuarioDto.setInicioS(Factory.formatoFechaHora());
                    Log.d(TAG, "onClick: usuario: "+usuarioDto.getString());

                    final SQLiteDatabase db = Factory.getBaseDatos(getBaseContext());
                    //Existe en la base de datos / lista
                    boolean existe = existeUsuarioDb(db, usuarioDto.getUser());
                    db.close();
                    if (existe) {
                        Log.d(TAG, "existeUsuario: EXISTE: TRUE");
                        muestraSnackbar(1);
                        dialogoAgregar.cancel();
                    } else {
                        usuarioDB = FirebaseDatabase.getInstance().getReference().child("users").child(usuarioDto.getUser());
                        usuarioDB.addValueEventListener(addUserValueEventListener);
                    }
                    dialogoAgregar.cancel();
                }
            }
        });

        loginAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoAgregar.cancel();
                dialogoLoginUsuario();
            }
        });

        regresarAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregado = false;
                dialogoAgregar.cancel();
            }
        });

        dialogoAgregar.show();
    }

    private void dialogoLoginUsuario(){
        //final String usuario;
        //final String password;

        /*Dialogo login usuario*/
        agregado = false;
        dialogoLogin = new Dialog(this);
        dialogoLogin.setContentView(R.layout.dialogo_login_usuario);

        logoF = dialogoLogin.findViewById(R.id.logoF_login);
        logoM = dialogoLogin.findViewById(R.id.logoM_login);
        usuarioLogin = dialogoLogin.findViewById(R.id.usuario_login);
        passLogin = dialogoLogin.findViewById(R.id.password_login);
        logLogin = dialogoLogin.findViewById(R.id.log_login);
        regresarLogin = dialogoLogin.findViewById(R.id.regresar_login);
        agregarLogin = dialogoLogin.findViewById(R.id.agregar_login);


        logLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final UsuarioDto[] usuario = {new UsuarioDto()};
                usuarioDto.setUser(usuarioLogin.getText().toString());
                usuarioDto.setPassword(passLogin.getText().toString());
                SQLiteDatabase db = Factory.getBaseDatos(getBaseContext());
                boolean existe = existeUsuarioDb(db, usuarioDto.getUser());
                db.close();
                if(existe){
                    Log.d(TAG, "existeUsuario: EXISTE ");
                    //Toast.makeText(getBaseContext(), "El usuario "+user.getUser()+" ya esxiste, eija otro usuario para registrar o inicie sesion.", Toast.LENGTH_SHORT).show();
                    //Snackbar.make(rootview, "El usuario "+ usuarioDto +" ya existe o los datos son incorrectos.", Snackbar.LENGTH_SHORT).show();
                    muestraSnackbar(2);
                    dialogoLogin.cancel();
                }else{
                    //SQLiteDatabase db = Factory.getBaseDatos(getBaseContext());
                    Log.d(TAG, "onClick: Usuario no encontrado en BD");
                    agregado = true;
                    usuarioDB = FirebaseDatabase.getInstance().getReference().child("users").child(usuarioDto.getUser());
                    usuarioDB.addValueEventListener(loginUsuarioValueEventListener);
                }
            }
        });

        agregarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoAgregarUsuario();
                dialogoLogin.cancel();
            }
        });

        regresarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregado = false;
                dialogoLogin.cancel();
            }
        });

        dialogoLogin.show();
    }

    public void guardarUsuario(UsuarioDto u) {
        if(u != null){
            agregado = true;
            listUsers.add(u);
            Log.d(TAG, "guardarUsuario: Se guardó usuario: "+u.getUser() + " size: "+listUsers.size());
            //Toast.makeText(this, "Nuevo alumno", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "Usuario "+u.getUser()+" agregado.", Toast.LENGTH_SHORT).show();
            //Snackbar.make(rootview, "Nuevo usuario agregado "+u.getUser(), Snackbar.LENGTH_SHORT).show();
            muestraSnackbar(0);
            /*db  = Factory.getBaseDatos(getApplicationContext());
            listUsers = usuariosDelegate.getUsuarios(db);
            db.close();*/

            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new RecyclerViewAdapterUsuarios(idTutor, ListaUsuariosVo.this, getApplicationContext(), listUsers);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else{
            Log.d(TAG, "guardarUsuario: No se guardó usuario: "+u.getUser());
            Toast.makeText(this, "Inténtelo más tarde.", Toast.LENGTH_SHORT).show();
        }
    }



    //false: no existe | true: existe : sólo en el recyclerView
    private boolean existeUsuarioDb(SQLiteDatabase db, String user) {
        Boolean ok = !usuariosDelegate.traeUsuario(db, user).getUser().equals("NULL");
        Log.d(TAG, "existeUsuario: "+ok);
        return ok;
    }


/*
    private boolean existeUsuarioFirebase(UsuarioDto user) {
        Boolean ok = true;
        ok = usuariosDelegate.usuarioExisteFirebase(this, user) && ok;
        if(ok){
            dialogoAgregar.cancel();
            dialogoLoginUsuario();
            usuarioAdd.setText(user.getUser());
        }
        Log.d(TAG, "isValidofirebase: "+ok);
        return ok;
    }
    */

    public void cierraLogin(UsuarioDto user){
        dialogoLogin.cancel();
        dialogoAgregarUsuario();
    }

    public void cierraRegistroExiste(UsuarioDto user){
        //cierra dialogo registro y abre login con datos
        dialogoAgregar.cancel();
        dialogoLoginUsuario();
        usuarioLogin.setText(user.getUser());
        passLogin.setText(user.getPassword());
        dialogoLogin.show();
        //dialogoAgregarUsuario();
    }

    //Se llama a traer desde usuarioDelegate
    public boolean registroNuevoUsuario(SQLiteDatabase db, UsuarioDto user){
        //el usuario no estuvo en Firebase ni en BD
        //cierra dialogo registro y agrega el usuario a la base de datos y al recycler view
        boolean ok = usuariosDelegate.agregaUsuario(db, user);
        guardarUsuario(user);
        return ok;
    }

    private void showDatePickerDialog() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.MyAlertDialogStyle);
            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    Fecha d = new Fecha(datePicker.getDayOfMonth(), datePicker.getMonth(), datePicker.getYear());

                    String selectedDate = d.getStringName();
                    int edad = d.calculaEdad();

                    fechaAdd.setText(selectedDate);
                    if(edad != 1) {
                        edadAdd.setText(edad + " años");
                    }else {
                        edadAdd.setText(edad + " año");
                    }

                    if(edad<1){
                        showDatePickerDialog();
                    }
                }
            });
            datePickerDialog.show();
        }
        /*
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate;
                int edad;
                Fecha d = new Fecha(day, month, year);

                selectedDate = d.getStringName();
                edad = d.calculaEdad();

                fechaAdd.setText(selectedDate);
                if(edad != 1)
                    edadAdd.setText(edad+" años");
                else
                    edadAdd.setText(edad+" año");
            }
        });
        newFragment.setStyle(R.style.MyAlertDialogStyle, R.style.AppTheme);
        newFragment.show(getSupportFragmentManager(), "datePicker");
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sesion, menu);
        cerrarSesion = menu.findItem(R.id.action_cerrar_sesion);
        cambiarPass = menu.findItem(R.id.action_cambia_pass);
        //infoPersonal = menu.findItem(R.id.action_informacion_personal);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_cambia_pass:
                Intent intentCambiarPass = new Intent(this, ReiniciarPasswordVo.class);
                startActivity(intentCambiarPass);
                ListaUsuariosVo.this.finish();
                return true;
                /*
            case R.id.action_cambiar_pass:
                dialogoCambiarPass();
                return true;
                */
            case R.id.action_cerrar_sesion:
                //Intent intentCerrar = new Intent(this, ConfiguracionCuenta.class);
                //startActivity(intentCerrar);
                dialogoCerrarSesion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dialogoCerrarSesion() {
        new AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Realmente desea cerrar sesion?")
            .setNegativeButton("No", null)
            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    if (arg1 != 0) {
                        SQLiteDatabase db = Factory.getBaseDatos(getApplicationContext());
                        if(sesionDelegate.cerrarSesion(listUsers.size(), db)){
                            FirebaseAuth.getInstance().signOut();
                            Log.d(TAG, "onClick: signOut");
                            // this listener will be called when there is change in firebase user session
                            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                                Log.d(TAG, "signOut: onAuthStateChanged: User: "+true);
                                // user auth state is changed - user is null
                                // launch login activity
                                Toast.makeText(ListaUsuariosVo.this, "Hasta pronto", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ListaUsuariosVo.this, LoginVo.class));
                                finish();
                                //ListaUsuariosVo.super.onBackPressed();
                            }else{
                                Log.d(TAG, "signOut: onAuthStateChanged: User: "+FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                Toast.makeText(ListaUsuariosVo.this, "Algo salió mal, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                            }
                            Log.d(TAG, "onClick: CERRAR SESION: "+true);
                        }else{
                            Log.d(TAG, "onClick: CERRAR SESION "+false);
                            Toast.makeText(ListaUsuariosVo.this, "Inténtelo de nuevo", Toast.LENGTH_SHORT).show();
                        }
                        db.close();
                    }
                }
            }).create().show();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setTitle("Salir")
                .setMessage("¿Realmente desea salir?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ListaUsuariosVo.super.onBackPressed();
                        if (arg1 != 0) {
                            Log.d(TAG, "back button: salir");
                            Intent intent = new Intent(ListaUsuariosVo.this, MainActivity.class);
                            intent.putExtra("salir", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            //MainActivity m = new MainActivity();
                            //m.onBackPressed();
                        }else{
                            Log.d(TAG, "back button: no salir");
                        }
                    }
                }).create().show();
    }

/*
        mTutorListener = mReferenceTutor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {}
                GenericTypeIndicator<HashMap<String, TutoresDto>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, TutoresDto>>() {
                };
                Map<String, TutoresDto> objectHashMap = dataSnapshot.getValue(objectsGTypeInd);
                ArrayList<TutoresDto> objectArrayList = new ArrayList<TutoresDto>(objectHashMap.values());
                for (int i = 0; i < objectArrayList.size(); i++) {
                    final TutoresDto tutor = objectArrayList.get(i);
                    if (tutor.getEmail().equals(mAuth.getCurrentUser().getEmail())) {
                        Log.e("MENUser:", "onTutorFound: Se encontró tutor: " + tutor.getName());
                        Toast.makeText(ListaUsuariosVo.this, tutor.toString(), Toast.LENGTH_SHORT).show();

                        mUserListener = mDatabaseUser.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                GenericTypeIndicator<HashMap<String, UsuarioDto>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, UsuarioDto>>() {
                                };
                                Map<String, UsuarioDto> objectHashMap = dataSnapshot.getValue(objectsGTypeInd);
                                ArrayList<UsuarioDto> objectArrayList = new ArrayList<UsuarioDto>(objectHashMap.values());
                                for (int j = 0; j < objectArrayList.size(); j++) {
                                    //UsuarioDto user = new UsuarioDto(userU, nameU, surnamesU, sexo, edadU, tutor.getUser());
                                    UsuarioDto user = objectArrayList.get(j);
                                    if (user.getTutor().equals(tutor.getUser())) {
                                        listUsers.add(user);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("MENUser", "onCancelled: Failed to read message");
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.e("MENUser", "onCancelled: Failed to read message");

            }
        });
    
        //lista.setText(muestraList());

    }

    private String muestraList(){
        String usuarios = "";
        for (int i = 0; i<listUsers.size(); i++){
            usuarios += listUsers.get(i).getUser();
        }
        Log.e("UsuariosList: ",usuarios);

        return usuarios;
    }
    */


    /* METODOS PARA SOLICITAR PERMISOS - con dialogo */

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return (write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissions() {
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED){
                    dialogoAgregarUsuario();
                }else{
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                        showDialogOK("Es necesario otorgar estos permisos para poder realizar las actividades",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                requestPermissions();
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                // proceed with logic by disabling the related features or quit the app.
                                                checkPermissionFromDevice();
                                                break;
                                        }
                                    }
                                });
                    }
                }
            }
            break;
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
                .setMessage(message)
                .setPositiveButton("Sí", okListener)
                .setNegativeButton("Cancelar", okListener)
                .create()
                .show();
    }

    private void muestraSnackbar(int indice){
        String[] mensajes = new String[3];
        mensajes[0] = "Nuevo usuario: "+usuarioDto.getUser();
        mensajes[1] = "El usuario "+usuarioDto.getUser()+" ya existe, inicie sesión";
        mensajes[2] = "El usuario "+ usuarioDto +" ya existe o los datos son incorrectos.";
        if(agregado) {
            Snackbar.make(rootview, mensajes[indice], Snackbar.LENGTH_SHORT).show();
        }
        agregado = false;
    }

    public ValueEventListener addUserValueEventListener =  new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            UsuarioDto u = dataSnapshot.getValue(UsuarioDto.class);
            if(u != null) {
                Log.d(TAG, "onClick: DBF notnull: user: "+u.getUser());
                Log.d(TAG, "onClick: EXISTE EN Firebase");
                if(agregado) {
                    muestraSnackbar(0);
                    adapter.notifyDataSetChanged();
                }else{
                    muestraSnackbar(1);
                }
                dialogoAgregar.cancel();
            } else  {
                Log.d(TAG, "onClick: DBF Null: user: "+usuarioDto.getUser());
                //Se agregara usuario a las 2 base de datos
                Log.d(TAG, "onClick: NO EXISTE EN Firebase");
                FirebaseDatabase.getInstance().getReference().child("users").child(usuarioDto.getUser()).setValue(usuarioDto);
                SQLiteDatabase db = Factory.getBaseDatos(getBaseContext());
                registroNuevoUsuario(db, usuarioDto);
                if (db != null) db.close();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(ListaUsuariosVo.this, "Revice su conexión", Toast.LENGTH_SHORT).show();
        }
    };

    public ValueEventListener loginUsuarioValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            UsuarioDto u = dataSnapshot.getValue(UsuarioDto.class);
            if(u != null) {
                Log.d(TAG, "login: Ingresando alumno: "+u.getString());
                //Se verifica password y usuario
                if(u.getPassword().equals(usuarioDto.getPassword())){
                    if(agregado) {
                        u.getUser();
                        u.getPassword();
                        u.setFinS(Factory.formatoFechaHora());
                        usuarioDto = u;
                        Log.d(TAG, "login EXISTE EN Firebase");
                        //Se agregara usuario a las 2 base de datos
                        SQLiteDatabase db = Factory.getBaseDatos(getBaseContext());
                        registroNuevoUsuario(db, usuarioDto);
                        if (db != null) db.close();
                    }
                }else{
                    Log.d(TAG, "onDataChange: ");
                    Toast.makeText(ListaUsuariosVo.this, "Verifique contraseña", Toast.LENGTH_SHORT).show();
                }
            } else  {
                Snackbar.make(rootview, "Usuario " + usuarioDto+" no encontrado", Snackbar.LENGTH_SHORT).show();
                Log.d(TAG, "onClick: DBF Null: user: "+usuarioDto.getUser());
            }
            dialogoLogin.cancel();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(ListaUsuariosVo.this, "Revice su conexión", Toast.LENGTH_SHORT).show();
            dialogoLogin.cancel();
        }
    };
}
