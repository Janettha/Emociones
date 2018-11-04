package janettha.activity1.EmocionesDelegate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import janettha.activity1.EmocionesDao.TutorDao;
import janettha.activity1.EmocionesDao.UsuarioDao;
import janettha.activity1.EmocionesDto.TutoresDto;

public class SesionDelegate {
    private static final String TAG = "SesionDelegate";

    private Context context;
    private TutorDao dao;

    public SesionDelegate(){
        dao = new TutorDao();
    }

    public SesionDelegate(Context context){
        this.context = context;
        dao = new TutorDao();
    }

    public ValueEventListener velExisteUsuario = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    TutoresDto tutor = snapshot.getValue(TutoresDto.class);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(context, "Algo salió mal, inténtelomás tarde.", Toast.LENGTH_SHORT).show();
        }
    };

    public void cargarUser(Context context, String tutor){
        dao.cargarUser(context, tutor);
    }

    public TutoresDto getTutor(String tutor, SQLiteDatabase db){
        return dao.getTutor(tutor, db);
    }

    public TutoresDto getTutorEmail(String email, SQLiteDatabase db){
        return dao.getTutorEmail(email, db);
    }

    public boolean updateTutor(TutoresDto dto, SQLiteDatabase db){
        return dao.updateTutor(dto, db);
    }

    public boolean insertaTutor(TutoresDto dto, SQLiteDatabase db){
        return dao.insertaTutor(dto, db);
    }

    public boolean cerrarSesion(SQLiteDatabase db) {
        return dao.cerrarSesion(db);
    }
}
