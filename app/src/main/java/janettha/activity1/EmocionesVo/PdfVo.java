package janettha.activity1.EmocionesVo;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.Calendar;

import janettha.activity1.R;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class PdfVo extends AppCompatActivity {
    private static final String TAG = "PdfVo";

    KonfettiView konfettiView;
    PDFView pdfView;
    File file;
    Button correo, menu;

    public final String keySP = "UserSex";
    private SharedPreferences sharedPreferences;
    private String user, sexo;

    private FirebaseAuth mAuth;

    public static final int REQUEST_STORAGE_PERMISSION = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        pdfView = (PDFView)findViewById(R.id.pdfView);
        correo = (Button) findViewById(R.id.correo);
        menu = (Button) findViewById(R.id.backMenu);
        konfettiView = findViewById(R.id.konfettiView);

        konfettiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.d(TAG, "onClick: holo");
                konfettiView.build()
                        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(new Size(12, 5f))
                        .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                        .streamFor(300, 5000L);
            }
        });

        cargaConfetti();

        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences(keySP, MODE_PRIVATE);
        //editorSP = sharedPreferences.edit();
        String nuevoUser = mAuth.getCurrentUser()+"_"+ Calendar.getInstance().getTime();
        user = sharedPreferences.getString("usuario", nuevoUser);
        sexo = sharedPreferences.getString("sexo", "m");
        Log.d(TAG, "onCreate: s: "+sexo+" u: "+user);

        requestPermissions();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            file = new File(bundle.getString("path",""));
        }
        pdfView.fromFile(file)              //se muestra el pdf desde archivo
                .enableSwipe(true)          //se pueden cambiar las paginas del pdf
                .swipeHorizontal(false)     //se pueden cambiar las hojas del pdf horizontal
                .enableDoubletap(true)      //se hace un zoom en el pdf
                .enableAntialiasing(true)   //se visualiza el pdf en pantallas de baja resolución
                .load();                    //se carga pdf

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PdfVo.this, MenuActividadesVo.class);
                intent.putExtra("sexo", sexo);
                intent.putExtra("usuario", user);
                startActivity(intent);
            }
        });

        correo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(user);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        cargaConfetti();
    }

    private void cargaConfetti() {
        konfettiView.callOnClick();
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        }, REQUEST_STORAGE_PERMISSION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_STORAGE_PERMISSION:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    @Override
    public void onBackPressed() {
        //final Intent intent = new Intent(this, loginUser.class);
        startActivity(new Intent(PdfVo.this, MenuActividadesVo.class));
    }

    private void sendEmail(String user){
        Uri uri;
        if (Build.VERSION.SDK_INT < 24) {
            uri = Uri.fromFile(file);
        } else {
            uri = Uri.parse(file.getPath()); // My work-around for new SDKs, causes ActivityNotFoundException in API 10.
        }
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, mAuth.getCurrentUser().getEmail());
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Reporte de actividades: "+user);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,"Hola, cualquier duda o aclaración no dudes en contactarnos.");
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Mandar correo usando: "));
    }
}
