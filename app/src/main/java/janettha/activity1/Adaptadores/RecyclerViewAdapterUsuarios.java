package janettha.activity1.Adaptadores;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import janettha.activity1.EmocionesDelegate.UsuariosDelegate;
import janettha.activity1.EmocionesDto.UsuarioDto;
import janettha.activity1.EmocionesVo.ListaUsuariosVo;
import janettha.activity1.EmocionesVo.MenuActividadesVo;
import janettha.activity1.R;
import janettha.activity1.Util.Factory;

import static android.content.Context.MODE_PRIVATE;

public class RecyclerViewAdapterUsuarios extends RecyclerView.Adapter<RecyclerViewAdapterUsuarios.RecyclerViewUsuarios>{
    private static final String TAG = "RecyclerViewAdapterUsua";

    private String tutor;
    //private Activity activity;
    private ListaUsuariosVo listaUsuariosVo;
    private Context context ;
    private List<UsuarioDto> usuariosList;
    private UsuariosDelegate usuariosDelegate;

    public static final String keySP = "UserSex";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editorSP;

    public RecyclerViewAdapterUsuarios(String t, ListaUsuariosVo a, Context c, List<UsuarioDto> list){
        tutor = t;
        listaUsuariosVo = a;
        context = c;
        usuariosList = list;
        Log.d(TAG, "RecyclerViewAdapterUsuarios: lista: "+usuariosList.size());
        usuariosDelegate = new UsuariosDelegate();
        sharedPreferences = context.getSharedPreferences(keySP, MODE_PRIVATE);
        editorSP = sharedPreferences.edit();
    }

    @NonNull
    @Override
    public RecyclerViewUsuarios onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_usuario, parent, false);
        RecyclerViewUsuarios rv = new RecyclerViewUsuarios(view);
        return rv;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(RecyclerViewUsuarios holder, final int position) {
        int img;
        final UsuarioDto dto = usuariosList.get(position);
        if (dto.getSexo().equals("f")) {
            img = R.drawable.f0;
        } else {
            img = R.drawable.m0;
            holder.rootview.setBackgroundColor(R.color.Act0Clear);
        }
        Log.d(TAG, "onBindViewHolder: Usuario: "+dto.getString());
        holder.imagen.setImageDrawable(context.getResources().getDrawable(img));
        final String nombreCompleto = dto.getNombre() + " " + dto.getApellidos();
        holder.nombreCompleto.setText(nombreCompleto);
        holder.rootview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editorSP.putString("usuario", dto.getUser());
                editorSP.putString("password", dto.getPassword());
                editorSP.putString("nombre", dto.getNombre());
                editorSP.putString("apellidos", dto.getApellidos());
                editorSP.putString("sexo", dto.getSexo());
                editorSP.putString("fechaNacimiento", dto.getCumple());
                editorSP.putInt("edad", dto.getEdad());
                editorSP.putString("tutor", dto.getTutor());
                editorSP.apply();

                Intent intent = new Intent(context, MenuActividadesVo.class);
                intent.putExtra("tutor", tutor);
                intent.putExtra("usuario", dto.getUser());
                intent.putExtra("sexo", dto.getSexo());
                Log.d(TAG, "onClick: USUARIO: "+dto.getString());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return usuariosList.size();
    }

    public class RecyclerViewUsuarios extends RecyclerView.ViewHolder {
        private static final String TAG = "RecyclerViewUsuarios";

        ConstraintLayout rootview;
        ImageView imagen;
        TextView nombreCompleto;
        TextView usuario;
        TextView edad;
        Button configuracion;

        public RecyclerViewUsuarios(View view) {
            super(view);
            rootview = view.findViewById(R.id.rootview);
            imagen = view.findViewById(R.id.imagen);
            nombreCompleto = view.findViewById(R.id.nombre);
            usuario = view.findViewById(R.id.usuario);
            edad = view.findViewById(R.id.edad);
            configuracion = view.findViewById(R.id.configuracionSesion);
        }
    }
}
