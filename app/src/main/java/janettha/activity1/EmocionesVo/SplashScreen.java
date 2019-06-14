package janettha.activity1.EmocionesVo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import janettha.activity1.R;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SystemClock.sleep(3000);

        Intent intentLogin = new Intent(this, LoginVo.class);
        Intent intentListUsuario = new Intent(this, ListaUsuariosVo.class);

        if(getIntent().getExtras().getString("activity") == null){
            Log.d(TAG, "onCreate: "+getIntent().getExtras());
            this.startActivity(intentLogin);
        }

        switch (getIntent().getExtras().getString("activity")) {
            case "list_usuarios":
                this.startActivity(intentListUsuario);
                break;
            default:
                this.startActivity(intentLogin);

        }

        finish();
    }

}
