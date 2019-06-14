package janettha.activity1.EmocionesVo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.inputmethodservice.Keyboard;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import janettha.activity1.EmocionesDelegate.SesionDelegate;
import janettha.activity1.EmocionesDto.TutoresDto;
import janettha.activity1.R;
import janettha.activity1.Util.Factory;

public class LoginVo extends AppCompatActivity {
    private static final String TAG = "LoginVo";

    private ConstraintLayout root;
    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;

    private FirebaseAuth mFirebaseAuth;

    private SesionDelegate sesionDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Get Firebase auth instance
        mFirebaseAuth = FirebaseAuth.getInstance();

        if (mFirebaseAuth.getCurrentUser() != null) {
            Log.d(TAG, "onCreate: "+mFirebaseAuth.getCurrentUser().getEmail());
            goToMain();
            finish();
        }

        setContentView(R.layout.activity_login);
        root = findViewById(R.id.rootview);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        sesionDelegate = new SesionDelegate();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginVo.this, RegistrarseVo.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginVo.this, ReiniciarPasswordVo.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    inputEmail.setError("Correo electrónico requerido");
                    return;
                } else if (!isValidEmail(email)) {
                    inputEmail.setError("Verifique su correo");
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    inputPassword.setError("Contraseña requerida");
                    return;
                }else if (!isValidPassword(password)) {
                    inputPassword.setError("Contraseña invalida");
                    return;
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    //authenticate user
                    loginUserFirebase(email, password);
                }

                if (root != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
                }
            }
        });
    }

    private void loginUserFirebase(final String email, final String password) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(LoginVo.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                }
            })
            .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    sesionDelegate.cargarUser(getBaseContext(), email);
                }
            })
            .addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (password.length() < 6) {
                        inputPassword.setError(getString(R.string.minimum_password));
                    } else {
                        Toast.makeText(LoginVo.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                    }
                }
            });
    }


    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    private void goToMain() {
        Intent intent = new Intent(this, ListaUsuariosVo.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("activity", "list_usuarios");
        startActivity(intent);
    }

}
