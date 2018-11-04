package janettha.activity1.EmocionesDao;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import janettha.activity1.EmocionesDto.TutoresDto;
import janettha.activity1.Util.Factory;

import static android.content.Context.MODE_PRIVATE;

public class TutorDao {
    private static final String TAG = "TutorDao";

    private FirebaseAuth mAuth;
    private ValueEventListener mTutorListener;
    private DatabaseReference mDatabaseTutor;

    public static final String keySP = "UserSex";
    private SharedPreferences sp;
    private SharedPreferences.Editor editorSP;

    public TutorDao() {
        mAuth = FirebaseAuth.getInstance();
        mDatabaseTutor = FirebaseDatabase.getInstance().getReference("tutores");
    }

    public boolean insertaTutor(TutoresDto dto, SQLiteDatabase db){
        boolean ok = true;
        ContentValues rows = new ContentValues();
        try {
            rows.put("tutor", dto.getTutor());
            rows.put("nombre", dto.getName());
            rows.put("apellidos", dto.getSurnames());
            rows.put("correo", dto.getEmail());
            rows.put("password", dto.getPass());
            rows.put("registro", dto.getRegistro());
            ok = db.insert("tutor", null, rows)>0 && ok;
            Log.d(TAG, "insertaTutor: INSERT-ok: "+ok);
            if(!ok){
                // ok = db.update("emocion", rows, "emocion = ? AND sexo = '?'", new String[]{e.getId()+"", e.getSexo()})>0 && ok;
                ok = updateTutor(dto, db);
                Log.d(TAG, "insertaTutor: UPDATE-ok: "+ok);
            }
        }catch(SQLException e){
            Log.d(TAG, "addEmociones: ERROR: "+e.getMessage());
        }
        return ok;
    }

    public boolean updateTutor(TutoresDto dto, SQLiteDatabase db) {
        String query = "UPDATE tutor SET tutor = ?, nombre = ?, apellidos = ?, correo = ?, password = ?, registro = ? " +
                "WHERE tutor = ?";
        Log.d(TAG, "updateTutor: UPDATE-ok: "+dto.getString());
        Cursor cursor = db.rawQuery(query, new String[]{dto.getTutor(), dto.getName(), dto.getSurnames(), dto.getEmail(), dto.getPass(), dto.getRegistro(), dto.getTutor()});
        Log.d(TAG, "updateTutor: : "+cursor.toString());
        return true;
    }

    public TutoresDto getTutor(String tutor, SQLiteDatabase db){
        TutoresDto dto = new TutoresDto();
        String query = "SELECT * FROM tutor WHERE tutor = ?";
        try {
            Log.d(TAG, "getTutor: key: "+tutor);
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(tutor)});
            if(cursor.moveToNext()) {
                dto.setTutor(cursor.getString(cursor.getColumnIndex("tutor")));
                dto.setName(cursor.getString(cursor.getColumnIndex("nombre")));
                dto.setSurnames(cursor.getString(cursor.getColumnIndex("apellidos")));
                dto.setEmail(cursor.getString(cursor.getColumnIndex("corre")));
                dto.setPass(cursor.getString(cursor.getColumnIndex("password")));
                dto.setRegistro(cursor.getString(cursor.getColumnIndex("registro")));
                Log.d(TAG, "getTutor: TUTOR: " + dto.getString());
            }
        }catch(SQLException e){
            Log.d(TAG, "getTutor: ERROR: "+e.getMessage());
        }
        return dto;
    }

    public TutoresDto getTutorEmail(String email, SQLiteDatabase db){
        TutoresDto dto = new TutoresDto();
        String query = "SELECT * FROM tutor";
        try {
            Log.d(TAG, "getTutorEmail: key: "+email);
            Cursor cursor = db.rawQuery(query, null);
            if(cursor.moveToNext()) {
                dto.setTutor(cursor.getString(cursor.getColumnIndex("tutor")));
                dto.setName(cursor.getString(cursor.getColumnIndex("nombre")));
                dto.setSurnames(cursor.getString(cursor.getColumnIndex("apellidos")));
                dto.setEmail(cursor.getString(cursor.getColumnIndex("correo")));
                dto.setPass(cursor.getString(cursor.getColumnIndex("password")));
                dto.setRegistro(cursor.getString(cursor.getColumnIndex("registro")));
                Log.d(TAG, "getTutorEmail: TUTOR: " + dto.getString());
            }
        }catch(SQLException e){
            Log.d(TAG, "getTutorEmail: ERROR: "+e.getMessage());
        }
        return dto;
    }

    public void cargarUser(final Context context, final String correoTutor){
        Log.d(TAG, "cargarUser: "+correoTutor);
        mTutorListener = mDatabaseTutor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {}
                GenericTypeIndicator<HashMap<String, TutoresDto>> objectsGTypeInd = new GenericTypeIndicator<HashMap<String, TutoresDto>>() {
                };
                Map<String, TutoresDto> objectHashMap = dataSnapshot.getValue(objectsGTypeInd);
                ArrayList<TutoresDto> objectArrayList = new ArrayList<TutoresDto>(objectHashMap.values());
                for (int i = 0; i < objectArrayList.size(); i++) {
                    TutoresDto tutor = objectArrayList.get(i);
                    if (tutor.getEmail().equals(correoTutor)) { // add newTutor != null
                        Log.d(TAG, "onDataChange: TUTOR: "+tutor.getString());
                        TutoresDto dto = new TutoresDto();
                        dto.setTutor(tutor.getTutor());
                        dto.setRegistro(tutor.getRegistro());
                        dto.setName(tutor.getName());
                        dto.setSurnames(tutor.getSurnames());
                        dto.setEmail(tutor.getEmail());
                        dto.setPass(tutor.getPass());
                        SQLiteDatabase db = Factory.getBaseDatos(context);
                        if(insertaTutor(tutor, db)){
                            sp = context.getSharedPreferences(keySP, MODE_PRIVATE);
                            editorSP = sp.edit();
                            editorSP.putString("tutor", tutor.getTutor());
                            editorSP.commit();
                            Log.d(TAG, "onDataChange: Tutor insertado: "+tutor.getString());
                        }
                        Log.d(TAG, "onDataChange: Tutor: "+dto.getString());
                        db.close();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.e(TAG, "onCancelled: Failed to read message");

            }
        });

    }

    public boolean cerrarSesion(SQLiteDatabase db) {
        boolean ok = true;
        ok = db.delete("tutor", null, null) >0 && ok;
        ok = db.delete("usuario", null, null) >0 && ok;
        return ok;
    }
}
