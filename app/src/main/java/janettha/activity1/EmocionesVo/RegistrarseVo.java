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
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String nombre = inputName.getText().toString().trim();
                String apellidos = inputSurnames.getText().toString().trim();
                String usuario = inputUser.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }else{
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
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                mFirebaseAuth.createUserWithEmailAndPassword(email, password)//Te recomiendo usar listener .addOnSuccessListener y .addOnFailureListener para asegurar ambos eventos
                        .addOnCompleteListener(RegistrarseVo.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(RegistrarseVo.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {   //y evitar estos  ifs
                                    Toast.makeText(RegistrarseVo.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                        updateProfile(inputName.getText().toString());

                                        FirebaseUser tutorFA = mFirebaseAuth.getCurrentUser();
                                        //,  )
                                        TutoresDto tutor = new TutoresDto();
                                        //new TutoresDto(
                                        tutor.setTutor(inputUser.getText().toString());
                                        tutor.setName(inputName.getText().toString());
                                        tutor.setSurnames(inputSurnames.getText().toString());
                                        tutor.setEmail(tutorFA.getEmail().toString());
                                        tutor.setPass(inputPassword.getText().toString());
                                        tutor.setRegistro(Factory.formatoFechaHora());

                                        Toast.makeText(getApplication().getBaseContext(), inputName.getText().toString(), Toast.LENGTH_SHORT).show();

                                        //FirebaseDatabase.getInstance().getReference().child("tutores").child(inputUser.getText().toString()).setValue(user);
                                        mDatabaseRef.child(inputUser.getText().toString()).setValue(tutor);
                                        SQLiteDatabase db = Factory.getBaseDatos(getBaseContext());
                                        sesionDelegate.insertaTutor(tutor, db);
                                        db.close();
                                        Intent intent = new Intent(RegistrarseVo.this, ListaUsuariosVo.class);
                                        intent.putExtra("tutor", tutor.getTutor());
                                        startActivity(intent);

                                        finish();
                                }
                            }
                        });
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
