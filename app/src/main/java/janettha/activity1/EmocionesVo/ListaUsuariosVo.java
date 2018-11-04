package janettha.activity1.EmocionesVo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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

    SQLiteDatabase db;
    public static final String keySP = "UserSex";
    private SharedPreferences sp;
    private SharedPreferences.Editor editorSP;
    String emailTutor;

    MenuItem cerrarSesion;
    MenuItem cambiarPass;
    MenuItem infoPersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_list);
        rootview = findViewById(R.id.rootview);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        agregarUsuario = findViewById(R.id.agregar_usuario);
        setSupportActionBar(toolbar);

        sp = getSharedPreferences(keySP, MODE_PRIVATE);
        editorSP = sp.edit();

        mAuth = FirebaseAuth.getInstance();
        mReferenceTutor = FirebaseDatabase.getInstance().getReference("tutores");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference("users");

        sesionDelegate = new SesionDelegate();
        usuariosDelegate = new UsuariosDelegate();
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
        agregado = false;

        if (listUsers.size() == 0) {
            dialogoAgregarUsuario();
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
        }

        agregarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogoAgregarUsuario();
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
                final String[] edad = edadAdd.getText().toString().split(" ");
                UsuarioDto user = new UsuarioDto();
                user.setNombre(nombreAdd.getText().toString());
                user.setApellidos(apellidosAdd.getText().toString());
                user.setUser(usuarioAdd.getText().toString());
                user.setPassword(passAdd.getText().toString());
                user.setCumple(fechaAdd.getText().toString());
                user.setEdad(Integer.valueOf(edad[0]));
                user.setSexo(sexo[0]);
                user.setTutor(idTutor);
                Log.d(TAG, "onClick: tutor: "+idTutor);
                user.setInicioS(Factory.formatoFechaHora());

                SQLiteDatabase db = Factory.getBaseDatos(getBaseContext());
                //Existe en la base de datos / lista
                if(existeUsuarioDb(db, user.getUser())){
                    Log.d(TAG, "existeUsuario: EXISTE: FALSE");
                    //Toast.makeText(getBaseContext(), "El usuario "+user.getUser()+" ya esxiste, eija otro usuario para registrar o inicie sesion.", Toast.LENGTH_SHORT).show();
                    Snackbar.make(rootview, "El usuario "+user.getUser()+" ya existe, inicie sesion.", Snackbar.LENGTH_SHORT).show();
                    dialogoAgregar.cancel();
                }else{
                    Log.d(TAG, "onClick: EXISTE: NO");
                    usuariosDelegate.usuarioExisteFirebase(ListaUsuariosVo.this, user);
                }
                if(db != null) db.close();
                dialogoAgregar.cancel();
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
                String usuario = usuarioLogin.getText().toString();
                String password = passLogin.getText().toString();
                SQLiteDatabase db = Factory.getBaseDatos(getBaseContext());
                //Hacer otro metodo para revisar password, o aquí mismo
                if(existeUsuarioDb(db, usuario)){
                    Log.d(TAG, "existeUsuario: EXISTE: ");
                    //Toast.makeText(getBaseContext(), "El usuario "+user.getUser()+" ya esxiste, eija otro usuario para registrar o inicie sesion.", Toast.LENGTH_SHORT).show();
                    Snackbar.make(rootview, "El usuario "+usuario+" ya existe o los datos son incorrectos.", Snackbar.LENGTH_SHORT).show();
                    //dialogoAgregar.cancel();
                }else {
                    Log.d(TAG, "onClick: Ingresando alumno.");
                    //SQLiteDatabase db = Factory.getBaseDatos(getBaseContext()); 
                    UsuarioDto u = usuariosDelegate.traeUsuario(db, usuario);
                    //boolean ok = usuariosDelegate.agregaUsuario(db, u);
                    if(u.getUser().equals("NULL")){
                        Log.d(TAG, "onClick: ERROR: Usuario no encontrado");
                    }else {
                        Log.d(TAG, "onClick: USUARIO ENCONTRADO");
                        guardarUsuario(u);
                        dialogoLogin.cancel();
                        Toast.makeText(ListaUsuariosVo.this, "Usuario agregado.", Toast.LENGTH_SHORT).show();
                    }
                }
                if(db != null) db.close();
                dialogoAgregar.cancel();
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
            Toast.makeText(this, "Usuario "+u.getUser()+" agregado.", Toast.LENGTH_SHORT).show();
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
        Boolean ok = true;
        UsuarioDto u = usuariosDelegate.traeUsuario(db, user);
        ok = !u.getUser().equals("NULL") && true;
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
        dialogoAgregar.cancel();
        return ok;
    }

    private void showDatePickerDialog() {
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
        newFragment.show(getSupportFragmentManager(), "datePicker");
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
                    ListaUsuariosVo.super.onBackPressed();
                    if (arg1 != 0) {
                        SQLiteDatabase db = Factory.getBaseDatos(getApplicationContext());
                        if(sesionDelegate.cerrarSesion(db)){
                            FirebaseAuth.getInstance().signOut();
                            // this listener will be called when there is change in firebase user session
                            FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
                                @Override
                                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user == null) {
                                        // user auth state is changed - user is null
                                        // launch login activity
                                        Toast.makeText(ListaUsuariosVo.this, "Hasta pronto", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ListaUsuariosVo.this, LoginVo.class));
                                        finish();
                                    }
                                }
                            };
                        }else{
                            Toast.makeText(ListaUsuariosVo.this, "Inténtelo de nuevo", Toast.LENGTH_SHORT).show();
                        }
                        db.close();
                    }
                }
            }).create().show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Salir")
                .setMessage("¿Realmente desea salir?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        ListaUsuariosVo.super.onBackPressed();
                        if (arg1 == 0) {
                            finish();
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
}
