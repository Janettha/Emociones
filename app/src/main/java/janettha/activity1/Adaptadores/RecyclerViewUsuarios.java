package janettha.activity1.Adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import janettha.activity1.EmocionesDto.UsuarioDto;
import janettha.activity1.EmocionesVo.ListaUsuariosVo;
import janettha.activity1.R;

import static android.content.Context.MODE_PRIVATE;

public class RecyclerViewUsuarios extends RecyclerView.ViewHolder {
    private static final String TAG = "RecyclerViewUsuarios";

    ConstraintLayout rootview;
    ImageView imagen;
    TextView nombreCompleto;
    TextView usuario;
    TextView edad;
    Button configuracion;

    ListaUsuariosVo activity;
    Context context;
    View view;
    String idUsuario;

    public static final String keySP = "UserSex";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editorSP;

    public RecyclerViewUsuarios(final ListaUsuariosVo a, final Context c, View itemView){
        super(itemView);
        activity = a;
        context = c;
        view = itemView;
        cargaElementos();
        sharedPreferences = context.getSharedPreferences(keySP, MODE_PRIVATE);
        editorSP = sharedPreferences.edit();
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void bind(UsuarioDto dto){
        int img;
        if (dto.getSexo().equals("f")) {
            img = R.drawable.f0;
        } else {
            img = R.drawable.m0;
            rootview.setBackgroundColor(R.color.Act0Clear);
        }
        Glide.with(context)
                .asBitmap()
                .load(img)
                .into(imagen);
        nombreCompleto.setText(dto.getNombre() + " " + dto.getApellidos());
        usuario.setText(dto.getUser());
        edad.setText(dto.getEdad());
        rootview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void cargaElementos() {
        rootview = view.findViewById(R.id.rootview);
        imagen = view.findViewById(R.id.imagen);
        nombreCompleto = view.findViewById(R.id.nombre);
        usuario = view.findViewById(R.id.usuario);
        edad = view.findViewById(R.id.edad);
        configuracion = view.findViewById(R.id.configuracionSesion);
    }


}
