package janettha.activity1.EmocionesDao;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import janettha.activity1.EmocionesDto.UsuarioDto;
import janettha.activity1.EmocionesVo.ListaUsuariosVo;
import janettha.activity1.Util.Factory;

public class UsuarioDao {
    private static final String TAG = "UsuarioDao";

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("users");

    public UsuarioDao(){    }
/*
    public void existeUsuarioFirebase(ListaUsuariosVo a, UsuarioDto usuario, ValueEventListener listenerExiste) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //CollectionReference usuarios = reference.collection

        Query query = reference.child("users").orderByChild("user").equalTo(usuario.getUser());
        if(query.equals(UsuarioDto.class))
            query.addListenerForSingleValueEvent(listenerExiste);
        else{
            Log.d(TAG, "existeUsuarioFirebase: insert: "+usuario.getString());
            SQLiteDatabase db = Factory.getBaseDatos(a.getBaseContext());
            boolean ok = insertaUsuario(db, usuario);
            db.close();
            if(ok) {
                mDatabaseRef.child(usuario.getUser()).setValue(usuario);
                a.guardarUsuario(usuario);
            }
        }
    }
    */

    public void existeUsuarioFirebase(String usuario, ValueEventListener listenerExiste) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //CollectionReference usuarios = reference.collection

        Query query = reference.child("users").orderByChild("user").equalTo(usuario);
        if (query.equals(UsuarioDto.class)){
            query.addListenerForSingleValueEvent(listenerExiste);
        }
    }

    public boolean insertaUsuario(SQLiteDatabase db, UsuarioDto usuario){
        boolean ok = true;
        ContentValues raw = new ContentValues();
        raw.put("usuario", usuario.getUser());
        raw.put("password",usuario.getPassword());
        raw.put("nombre", usuario.getNombre());
        raw.put("apellidos", usuario.getApellidos());
        raw.put("sexo", usuario.getSexo());
        raw.put("cumple", usuario.getCumple());
        raw.put("edad", usuario.getEdad());
        raw.put("tutor", usuario.getTutor());
        ok = db.insert("usuario", null, raw) > 0 && ok;
        if(!ok) {
            ok = db.update("usuario", raw, "usuario = ?", new String[]{usuario.getUser()}) > 0 && ok;
        }
        Log.d(TAG, "insertaUsuario: Usuario: "+usuario.getString());
        return ok;
    }

    public UsuarioDto obtieneUsuario(SQLiteDatabase db, String idUsuario) {
        UsuarioDto user = new UsuarioDto();
        user.setUser("NULL");
        String query = "SELECT * FROM usuario WHERE usuario = ?";
        try{
            Cursor cursor = db.rawQuery(query, new String[]{idUsuario});
            if(cursor.moveToNext()) {
                user.setUser(cursor.getString(cursor.getColumnIndex("usuario")));
                user.setNombre(cursor.getString(cursor.getColumnIndex("nombre")));
                user.setApellidos(cursor.getString(cursor.getColumnIndex("apellidos")));
                user.setSexo(cursor.getString(cursor.getColumnIndex("sexo")));
                user.setCumple(cursor.getString(cursor.getColumnIndex("cumple")));
                user.setEdad(cursor.getInt(cursor.getColumnIndex("edad")));
            }
        }catch (SQLException e){
            Log.d(TAG, "obtieneUsuario: ERROR: "+e.getMessage());
        }
        Log.d(TAG, "obtieneUsuario: usuario: "+idUsuario+user.getString());
        return  user;
    }

    public List<UsuarioDto> obtieneUsuarios(SQLiteDatabase db){
        List<UsuarioDto> usuarios = new ArrayList<>();
        String query = "SELECT * FROM usuario";
        try{
            Cursor cursor = db.rawQuery(query, null);
            while(cursor.moveToNext()){
                UsuarioDto dto = new UsuarioDto();
                dto.setUser(cursor.getString(cursor.getColumnIndex("usuario")));
                dto.setNombre(cursor.getString(cursor.getColumnIndex("nombre")));
                dto.setApellidos(cursor.getString(cursor.getColumnIndex("apellidos")));
                dto.setSexo(cursor.getString(cursor.getColumnIndex("sexo")));
                dto.setCumple(cursor.getString(cursor.getColumnIndex("cumple")));
                dto.setEdad(cursor.getInt(cursor.getColumnIndex("edad")));
                usuarios.add(dto);
            }
        }catch (SQLException e){
            Log.d(TAG, "obtieneUsuarios: ERROR: "+e.getMessage());
        }
        Log.d(TAG, "obtieneUsuarios: usuarios: "+usuarios.size());
        return usuarios;
    }

    public void agregarUsuarioFirebase(ListaUsuariosVo activity, UsuarioDto user) {
        //FirebaseDatabase.getInstance().getReference().child("users")
        Log.d(TAG, "agregarUsuarioFirebase: user: "+user.getString());
        Log.d(TAG, "agregarUsuarioFirebase: FIREBASE_REF: "+mDatabaseRef.toString());
        mDatabaseRef.child(user.getUser()).setValue(user);
        Toast.makeText(activity, "Usuario agregado.", Toast.LENGTH_SHORT).show();
    }
}
