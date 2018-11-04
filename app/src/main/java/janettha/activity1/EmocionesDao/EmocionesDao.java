package janettha.activity1.EmocionesDao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import janettha.activity1.EmocionesDto.EmocionDto;
import janettha.activity1.R;
import janettha.activity1.Util.Factory;

/**
 * Created by janeth on 08/11/2017.
 */

public class EmocionesDao {
    private static final String TAG = "EmocionesDao";
;

    public EmocionesDao(){};

    //public List<EmocionDto> EmocionesDao(Context c, String s)  {
    public boolean Emociones(Context c, SQLiteDatabase db)  {
        List<EmocionDto> emociones = new ArrayList<EmocionDto>();
        boolean ok = true;
        try {
            //InputStream fileE = view.getResources().openRawResource(R.raw.emociones);
            InputStream fileE = c.getResources().openRawResource(R.raw.emociones);
            BufferedReader brE = new BufferedReader(new InputStreamReader(fileE));
            //Lectura de emocion
            int i = 0;
            String line1, ruta="";
            if (fileE != null) {
                while ((line1 = brE.readLine()) != null) {
                    String[] array = line1.split(","); // Split according to the hyphen and put them in an array
                    Log.d(TAG, "Emociones: sexo: "+array[2]);
                    if(array[2].equals("f")){
                        ruta = "android.resource://janettha.activity1/drawable/f";
                    }else if(array[2].equals("m")){
                        ruta = "android.resource://janettha.activity1/drawable/m";
                    }
                    Log.d(TAG, "Emociones: ruta: "+ruta);
                    //new EmocionDto(Integer.parseInt(array[0])+"_"+array[2], Integer.parseInt(array[0]), array[1], array[2], ruta+String.valueOf(array[0]), array[3], array[4])
                    //new EmocionDto(+"_"+array[2], Integer.parseInt(array[0]), , , , , array[4])
                    EmocionDto dto = new EmocionDto();
                    dto.setEmocion(Integer.parseInt(array[0]));
                    //dto.setEmocion();
                    dto.setName(array[1]);
                    dto.setSexo(array[2]);
                    dto.setUrl(ruta+String.valueOf(array[0]));
                    dto.setColor(array[3]);
                    dto.setColorB(array[4]);
                    emociones.add(i, dto);
                    Log.d(TAG, "Emociones: "+i+": "+emociones.get(i).getString());
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Emociones: "+emociones.size());
        ok = addEmociones(db, emociones)&&ok;

        return ok;
    }
/*
    public List<EmocionDto> getEmociones() {
        return emociones;
    }

    public EmocionDto getEmocion(int i) {
        return emociones.get(i);
    }
    */

    private boolean addEmociones(SQLiteDatabase db, List<EmocionDto> emociones){
        boolean ok = true;
        ContentValues rows = new ContentValues();
        try {
            for (EmocionDto e : emociones) {
                //rows.put("id_emocion", e.getId());
                rows.put("nombre", e.getName());
                rows.put("sexo", e.getSexo());
                rows.put("color", e.getColor());
                rows.put("background", e.getColorB());
                rows.put("imagen", e.getUrl());
                rows.put("emocion", e.getEmocion());
                ok = db.insert("emocion", null, rows)>0 && ok;
                Log.d(TAG, "addEmociones:INSERT emocion: "+e.getString());
                Log.d(TAG, "addEmociones: INSERT-ok: "+ok);
                if(!ok){
                   // ok = db.update("emocion", rows, "emocion = ? AND sexo = '?'", new String[]{e.getId()+"", e.getSexo()})>0 && ok;
                    ok = updateEmociones(e, db);
                    Log.d(TAG, "addEmociones:UPDATE emocion: "+e.getString());
                    Log.d(TAG, "addEmociones: UPDATE-ok: "+ok);
                }
            }
        }catch(SQLException e){
            Log.d(TAG, "addEmociones: ERROR: "+e.getMessage());
        }
        return ok;
    }

    public boolean updateEmociones(EmocionDto e, SQLiteDatabase db){
        boolean ok = true;
        ContentValues rows = new ContentValues();
        String query = "UPDATE emocion SET emocion = ?, nombre = ?, sexo = ?, color = ?, background = ?, " +
                "imagen = ? WHERE emocion = ?";
            //ok = db.update("emocion", rows, "emocion = ? AND sexo = '?'", new String[]{e.getId()+"", e.getSexo()})>0 && ok;
        Log.d(TAG, "updateEmociones: Emocion: "+e.getEmocion()+" sexo: "+e.getSexo());
        String condicion = e.getEmocion()+"_"+e.getSexo();
        Cursor cursor = db.rawQuery(query, new String[]{e.getEmocion()+"", e.getName(), e.getSexo(), e.getColor(), e.getColorB(), e.getUrl(), e.getEmocion()+""});
        //ok = cursor.getColumnCount()>0 && ok;
        ok = true;
        Log.d(TAG, "updateEmociones: UPDATE-ok: "+ok);
        return ok;
    }

    public EmocionDto getEmocion(int id, String sexo, SQLiteDatabase db){
        Log.d(TAG, "getEmocion: listaEmociones: "+getEmociones(sexo, db).size());
        EmocionDto emocion = new EmocionDto();
        String query = "SELECT * FROM emocion WHERE emocion = ? AND sexo = ?";
        try {
            String key = id+"_"+sexo;
            Log.d(TAG, "getEmocion: key: "+id);
            Cursor cursor = db.rawQuery(query, new String[]{id+"", sexo});
            if(cursor.moveToNext()) {
                //emocion.setId(cursor.getString(cursor.getColumnIndex("id_emocion")));
                emocion.setEmocion(cursor.getInt(cursor.getColumnIndex("emocion")));
                emocion.setName(cursor.getString(cursor.getColumnIndex("nombre")));
                emocion.setSexo(cursor.getString(cursor.getColumnIndex("sexo")));
                emocion.setColor(cursor.getString(cursor.getColumnIndex("color")));
                emocion.setColorB(cursor.getString(cursor.getColumnIndex("background")));
                emocion.setUrl(cursor.getString(cursor.getColumnIndex("imagen")));
                Log.d(TAG, "getEmocion: EMOCION: " + emocion.getString());
            }
        }catch(SQLException e){
            Log.d(TAG, "getEmocion: ERROR: "+e.getMessage());
        }
        return emocion;
    }

    public List<EmocionDto> getEmociones(String sexo, SQLiteDatabase db){
        List<EmocionDto> emociones = new ArrayList<>();
        String query = "SELECT emocion, nombre, sexo, color, background, imagen FROM emocion " +
                "WHERE sexo = ?";
        try {
            Cursor cursor = db.rawQuery(query, new String[]{sexo});
            while(cursor.moveToNext()){
                EmocionDto dto = new EmocionDto();
                //dto.setId(cursor.getString(cursor.getColumnIndex("id_emocion")));
                dto.setEmocion(cursor.getInt(cursor.getColumnIndex("emocion")));
                dto.setName(cursor.getString(cursor.getColumnIndex("nombre")));
                dto.setSexo(cursor.getString(cursor.getColumnIndex("sexo")));
                dto.setColor(cursor.getString(cursor.getColumnIndex("color")));
                dto.setColorB(cursor.getString(cursor.getColumnIndex("background")));
                dto.setUrl(cursor.getString(cursor.getColumnIndex("imagen")));
                Log.d(TAG, "getEmociones: EMOCION: "+dto.getString());
                emociones.add(dto);
            }
            Log.d(TAG, "getEmociones: emociones: "+emociones.size());
        }catch(SQLException e){
            Log.d(TAG, "addEmociones: ERROR: "+e.getMessage());
        }
        return emociones;
    }

}
