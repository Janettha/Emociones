package janettha.activity1.EmocionesDelegate;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import janettha.activity1.EmocionesDao.TutorDao;
import janettha.activity1.EmocionesDao.UsuarioDao;
import janettha.activity1.EmocionesDto.UsuarioDto;
import janettha.activity1.EmocionesVo.ListaUsuariosVo;
import janettha.activity1.Util.Factory;
import janettha.activity1.Util.SQLite;

public class UsuariosDelegate {
    private static final String TAG = "UsuariosDelegate";

    UsuarioDao usuarioDao;
    TutorDao tutorDao;
    ListaUsuariosVo activity;
    UsuarioDto user;

    public UsuariosDelegate(){
        usuarioDao = new UsuarioDao();
        tutorDao = new TutorDao();
    }

    public void usuarioExisteFirebase(ListaUsuariosVo a, UsuarioDto usuario){
        activity = a;
        //Usuario vacio, sólo recibe password y user
        user = usuario;
        usuarioDao.existeUsuarioFirebase(a, usuario, listenerExiste);
    }

    ValueEventListener listenerExiste = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                Log.d(TAG, "onDataChange: existe: "+true);
                // dataSnapshot is the "issue" node with all children with id 0
                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<HashMap<String, UsuarioDto>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, UsuarioDto>>() {
                    };
                    Map<String, UsuarioDto> objectHashMap = dataSnapshot.getValue(objectsGTypeInd);
                    ArrayList<UsuarioDto> objectArrayList = new ArrayList<UsuarioDto>(objectHashMap.values());
                    //revisar for, debe ser un if
                    for (int i = 0; i < objectArrayList.size(); i++) {
                        UsuarioDto userT = objectArrayList.get(i);
                        SQLiteDatabase db = Factory.getBaseDatos(activity.getBaseContext());
                        if(usuarioDao.obtieneUsuario(db, userT.getUser()).getUser().equals("NULL")) {
                            //el usuario no existe, por lo tanto se va a agregar:
                            // a la base de datos y a la lista del recycler view
                            Log.d(TAG, "onDataChange: "+userT.getString());
                            if(agregaUsuario(db, userT)){
                                Log.d(TAG, "onDataChange: Usuario Agregado");
                                if(activity.registroNuevoUsuario(db, user)) {
                                    Log.d(TAG, "onDataChange: Se va a agregar a Firebase");
                                    agregaUsuarioFirebase(activity, user);
                                }
                            }
                        }else
                            Toast.makeText(activity.getBaseContext(), "Algo salió mal, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                        db.close();
                    }
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void agregaUsuarioFirebase(ListaUsuariosVo activity, UsuarioDto user) {
        usuarioDao.agregarUsuarioFirebase(activity, user);
    }


    public boolean usuarioExiste(String usuario){
        boolean ok = true;
        return ok;
    }

    public boolean agregaUsuario(SQLiteDatabase db, UsuarioDto usuario){
        return usuarioDao.insertaUsuario(db, usuario);
    }

    public UsuarioDto traeUsuario(SQLiteDatabase db, String idUsuario){
        return usuarioDao.obtieneUsuario(db, idUsuario);
    }

    public List<UsuarioDto> getUsuarios(SQLiteDatabase db) {
        return usuarioDao.obtieneUsuarios(db);
    }
}
