package janettha.activity1.EmocionesVo;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import janettha.activity1.EmocionesDelegate.SesionDelegate;
import janettha.activity1.EmocionesDto.TutoresDto;
import janettha.activity1.EmocionesDto.UsuarioDto;
import janettha.activity1.R;
import janettha.activity1.Util.Factory;

public class RegistrarseVo extends AppCompatActivity {
    private static final String TAG = "RegistrarseVo";

    private EditText inputEmail, inputPassword, inputName, inputSurnames, inputUser;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;

    private SesionDelegate sesionDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputName = (EditText) findViewById(R.id.nombre);
        inputSurnames = (EditText) findViewById(R.id.apellidos);
        inputUser = (EditText) findViewById(R.id.usuario);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        sesionDelegate = new SesionDelegate();
        //Get Firebase auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("tutores");

        Log.d(TAG, "onCreate: Registro ");
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrarseVo.this, ReiniciarPasswordVo.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrarseVo.this, LoginVo.class));
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TutoresDto tutorDto = new TutoresDto();
                tutorDto.setEmail(inputEmail.getText().toString().trim());
                tutorDto.setPass(inputPassword.getText().toString().trim());
                tutorDto.setName(inputName.getText().toString().trim());
                tutorDto.setSurnames(inputSurnames.getText().toString().trim());
                tutorDto.setTutor(inputUser.getText().toString().trim());

                if (TextUtils.isEmpty(tutorDto.getEmail())) {
                    inputEmail.setError("Este campo es requerido");
                    return;
                }else if (TextUtils.isEmpty(tutorDto.getPass())) {
                    inputPassword.setError("Este campo es requerido");
                    return;
                }else if (tutorDto.getPass().length() < 6) {
                    Toast.makeText(getApplicationContext(), R.string.minimum_password, Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    /*
                    TutoresDto tutor = new TutoresDto();
                    tutor.setEmail(email);
                    tutor.setPass(password);
                    tutor.setName(nombre);
                    tutor.setSurnames(apellidos);
                    tutor.setTutor(usuario);
                    tutor.setRegistro(Factory.formatoFechaHora());
                    SQLiteDatabase db = Factory.getBaseDatos(getApplicationContext());
                    Log.d(TAG, "onClick: "+tutor.getString());
                    sesionDelegate.insertaTutor(tutor, db);
                    db.close();
                    */
                    crearUsuarioFirebase(tutorDto);
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
            }
        });
    }

    private void crearUsuarioFirebase(final TutoresDto tutoresDto) {
        //Te recomiendo usar listener .addOnSuccessListener y .addOnFailureListener para asegurar ambos eventos
        mFirebaseAuth.createUserWithEmailAndPassword(tutoresDto.getEmail(), tutoresDto.getPass())
            .addOnCompleteListener(RegistrarseVo.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                }
            })
        .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+ e.getMessage());
                if(e.getMessage().equals("The email address is already in use by another account.")){
                    Toast.makeText(RegistrarseVo.this, "Este correo ya está registrado, ingrese...", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(RegistrarseVo.this, "Algo salió mal, revise sus datos.", Toast.LENGTH_SHORT).show();
                }
            }
        })
        .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                updateProfile(inputName.getText().toString());

                FirebaseUser tutorFA = mFirebaseAuth.getCurrentUser();
                tutoresDto.setRegistro(Factory.formatoFechaHora());

                Toast.makeText(getApplication().getBaseContext(), inputName.getText().toString(), Toast.LENGTH_SHORT).show();

                //FirebaseDatabase.getInstance().getReference().child("tutores").child(inputUser.getText().toString()).setValue(user);
                mDatabaseRef.child(inputUser.getText().toString()).setValue(tutoresDto);
                SQLiteDatabase db = Factory.getBaseDatos(getBaseContext());
                if(sesionDelegate.insertaTutor(tutoresDto, db)){
                    Toast.makeText(RegistrarseVo.this, "¡Bienvenido!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RegistrarseVo.this, "Algo salió mal, revice su conexión", Toast.LENGTH_SHORT).show();
                }
                db.close();

                Intent intent = new Intent(RegistrarseVo.this, ListaUsuariosVo.class);
                intent.putExtra("tutor", tutoresDto.getTutor());
                startActivity(intent);

                finish();
            }
        });
    }

    private void updateProfile(String inputName) {
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(inputName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("UpdateNameTutor", "User profile updated.");
                        }else{
                            Log.d("UpdateNameTutor", "User profile updated FAIL.");
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        existeUser();
        progressBar.setVisibility(View.GONE);
    }

    public void existeUser(){
        ValueEventListener tutorListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    TutoresDto tutor = dataSnapshot.getValue(TutoresDto.class);
                    Log.e("TutorOK", "onDataChange: Message data is updated: " + tutor.getString());
                    Toast.makeText(RegistrarseVo.this, tutor.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.e("TutorFAIL", "onCancelled: Failed to read message");

            }
        };
        mDatabaseRef.addValueEventListener(tutorListener);

        // copy for removing at onStop()
        //mTutorListener = tutorListener;
    }
}
