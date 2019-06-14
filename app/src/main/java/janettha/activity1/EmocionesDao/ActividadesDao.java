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

import janettha.activity1.EmocionesDto.ActividadImagenesDto;
import janettha.activity1.EmocionesDto.ActividadRedaccionesDto;
import janettha.activity1.EmocionesDto.EmocionDto;
import janettha.activity1.EmocionesDto.IndicesActividadDto;
import janettha.activity1.EmocionesDto.UsuarioDto;
import janettha.activity1.R;
import janettha.activity1.Util.Factory;

public class ActividadesDao {
    private static final String TAG = "ActividadesDao";

    private EmocionesDao emocionesDao;

    public ActividadesDao(){
         emocionesDao = new EmocionesDao();
    }

    public boolean actividadImagenes(Context c, SQLiteDatabase db, String s){
        boolean ok = true;
        //List<EmocionDto> emociones = new ArrayList<>();
        try {
            //InputStream fileE = view.getResources().openRawResource(R.raw.emocionesDao);
            InputStream fileE = c.getResources().openRawResource(R.raw.preactividad);
            BufferedReader brE = new BufferedReader(new InputStreamReader(fileE));
            //Lectura de emocion en actividad de redacciones
            int i = 0;
            String line1, ruta="";
            if (fileE != null) {
                while ((line1 = brE.readLine()) != null) {
                    String[] array = line1.split(","); // Split according to the hyphen and put them in an array
                    //btnList.add(i, new EmocionDto(Integer.parseInt(array[0]), array[1], array[2], array[0]+".png", array[3]));
                    Log.d(TAG, "actividadImagenes: "+array[0]+" "+array[1]+" "+array[2]);
                    int id = Integer.parseInt(array[0]);
                    int id2 = Integer.parseInt(array[1]);
                    int id3 = Integer.parseInt(array[2]);
                    //System.out.print("----------------------ID-----------------"+id+","+id2+","+id3);
                    // emocionesDao.get(id).getSexo()
                    //emociones.add(new EmocionDto(id+"_"+s, id, emocionesDao.getEmocion(id, s, ).get(id).getName(), s, emociones.get(id).getUrl(), emociones.get(id).getColor(), emociones.get(id).getColorB()));
                    List<EmocionDto> emociones = new ArrayList<>();
                    emociones.add(emocionesDao.getEmocion(id, s, db));
                    Log.d(TAG, "actividadImagenes: EMOCION: "+emociones.get(0).getString());
                    //emociones.add(new EmocionDto(id2+"_"+s, id2, emociones.get(id2).getName(), s, emociones.get(id2).getUrl(), emociones.get(id2).getColor(), emociones.get(id2).getColorB()));
                    emociones.add(emocionesDao.getEmocion(id2, s, db));
                    Log.d(TAG, "actividadImagenes: EMOCION: "+emociones.get(1).getString());
                    //emociones.add(new EmocionDto(id3+"_"+s, id3, emociones.get(id3).getName(), s, emociones.get(id3).getUrl(), emociones.get(id3).getColor(), emociones.get(id3).getColorB()));
                    emociones.add(emocionesDao.getEmocion(id3, s, db));
                    Log.d(TAG, "actividadImagenes: EMOCION: "+emociones.get(2).getString());

                    ActividadImagenesDto a0 = new ActividadImagenesDto(i, s, emociones);
                    Log.d(TAG, "actividadImagenes: lista: "+a0.getListEmociones().size());
                    //ActividadImagenesDto af = new ActividadImagenesDto(i, "f", emociones);
                    //listAct0.add(i, a0);
                    insertaActividadA(db, s, a0);
                    i++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok;
    }

    public boolean actividadRedacciones(Context c, SQLiteDatabase db, String s){
        boolean ok = true;
        //List<ActividadRedaccionesDto> listActB = new ArrayList<>();
        InputStream fileE;
        try {
            if(s.equals("f"))
                fileE = c.getResources().openRawResource(R.raw.redaccionesf);
            else
                fileE = c.getResources().openRawResource(R.raw.redaccionesm);

            BufferedReader brE = new BufferedReader(new InputStreamReader(fileE));
            int i = 0;
            String line1;

            if(fileE != null){

                while ((line1 = brE.readLine()) != null) {

                    String[] array = line1.split("-"); // Split according to the hyphen and put them in an array

                    List<String> explicaciones = new ArrayList<String>();
                    explicaciones.add(0,array[1]);
                    explicaciones.add(1,array[3]);
                    explicaciones.add(2,array[5]);
                    explicaciones.add(3,array[7]);

                    List<EmocionDto> e = new ArrayList<EmocionDto>();
                    e.add(0, emocionesDao.getEmocion(Integer.parseInt(array[2]), s, db));
                    Log.d(TAG, "actividadRedacciones: "+e.get(0));
                    e.add(1, emocionesDao.getEmocion(Integer.parseInt(array[4]), s, db));
                    Log.d(TAG, "actividadRedacciones: "+e.get(1));
                    e.add(2, emocionesDao.getEmocion(Integer.parseInt(array[6]), s, db));
                    Log.d(TAG, "actividadRedacciones: "+e.get(2));
                    ActividadRedaccionesDto a2 = new ActividadRedaccionesDto(Integer.parseInt(array[0]), s, e, explicaciones);

                    Log.e("ActividadRedaccionesDto",a2.emocionMain().getName() + "-" +a2.emocionB().getName() + "-" + a2.emocionC().getName());
                    Log.e("ActividadRedaccionesDto", String.valueOf(i));
                    //listActB.add(i, a2);
                    ok = insertaActividadB(db, s, a2);
                    i++;
                    Log.d(TAG, "actividadRedacciones: i: "+i);
                }
            }else{
                Log.e("FileE", "Archivo vacio");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok;
    }


    public boolean insertaRespuestasUsuario(SQLiteDatabase db, UsuarioDto usuario){
        boolean ok = true;
        int indice;
        String query = "SELECT * FROM actividades";
        Cursor cursor = db.rawQuery(query, null);
        indice = cursor.getCount() + 1;
        ContentValues raw = new ContentValues();
            raw.put("id_actividad", indice);
            raw.put("usuario", usuario.getUser());
            raw.put("act_1", usuario.getIndiceA1());
            raw.put("act_2", usuario.getIndiceA2());
            raw.put("act_3", usuario.getIndiceA3());
            ok = db.insert("actividades", null, raw) >0 && ok;
        return ok;
    }

    public boolean modificaRespuestaUsuario(SQLiteDatabase db, UsuarioDto usuario){
        boolean ok = true;
        ContentValues raw = new ContentValues();
        raw.put("act_1", usuario.getIndiceA1());
        raw.put("act_2", usuario.getIndiceA2());
        raw.put("act_3", usuario.getIndiceA3());
        ok = db.update("actividades", raw, "usuario = "+usuario.getUser(), null) >0 && ok;
        return ok;
    }

    public UsuarioDto obtieneRespuestasUsuario(SQLiteDatabase db, UsuarioDto usuario){
        UsuarioDto dto = new UsuarioDto();
        String query = "SELECT * FROM actividades WHERE usuario = ?";
        Cursor cursor = db.rawQuery(query, new String[]{usuario.getUser()});
            dto.setUser(cursor.getString(cursor.getColumnIndex("id_actividad")));
            dto.setUser(cursor.getString(cursor.getColumnIndex("usuario")));
            dto.setIndiceA1(cursor.getInt(cursor.getColumnIndex("act_1")));
            dto.setIndiceA2(cursor.getInt(cursor.getColumnIndex("act_2")));
            dto.setIndiceA3(cursor.getInt(cursor.getColumnIndex("act_3")));
        return usuario;
    }

    public boolean insertaActividadA(SQLiteDatabase db, String sexo, ActividadImagenesDto actividad){
        boolean ok = true;
        ContentValues row = new ContentValues();
        try{
            row.put("id_act_1", actividad.emocionMain().getEmocion());
            row.put("emocion1", actividad.emocionMain().getEmocion());
            row.put("emocion2", actividad.emocionB().getEmocion());
            row.put("emocion3", actividad.emocionC().getEmocion());
            row.put("sexo", sexo);
            ok = db.insert("actividad1", null, row)>0 && ok;
        }catch (SQLException e) {
            Log.d(TAG, "insertaActividadA: ERROR: " + e.getMessage());
        }
        return ok;
    }

    public List<ActividadImagenesDto> obtieneActividadesA(SQLiteDatabase db, String sexo){
        List<ActividadImagenesDto> lista = new ArrayList<>();
        String query = "SELECT * FROM actividad1 WHERE sexo = ?";
        try{
            Cursor cursor = db.rawQuery(query, new String[]{sexo});
            while (cursor.moveToNext()){
                ActividadImagenesDto dto = new ActividadImagenesDto();
                dto.setId(cursor.getInt(cursor.getColumnIndex("id_act_1")));
                dto.setSexo(cursor.getString(cursor.getColumnIndex("sexo")));
                dto.setEmocionMain(emocionesDao.getEmocion(cursor.getInt(cursor.getColumnIndex("emocion1")), sexo, db));
                dto.setEmocionB(emocionesDao.getEmocion(cursor.getInt(cursor.getColumnIndex("emocion2")), sexo, db));
                dto.setEmocionC(emocionesDao.getEmocion(cursor.getInt(cursor.getColumnIndex("emocion3")), sexo, db));
                lista.add(dto);
            }
        }catch(SQLException e){
            Log.d(TAG, "obtieneActividadesA: ERROR: "+e.getMessage());
        }
        return lista;
    }

    public boolean insertaActividadB(SQLiteDatabase db, String sexo, ActividadRedaccionesDto actividad){
        boolean ok = true;
        ContentValues row = new ContentValues();
        try{
            row.put("id_act_2", actividad.getId());
            row.put("sexo", sexo);
            row.put("redaccion", actividad.getRedaccion());
            row.put("emocion1", actividad.emocionMain().getEmocion());
            row.put("expl_a", actividad.getExpl1());
            row.put("emocion2", actividad.emocionB().getEmocion());
            row.put("expl_b", actividad.getExpl2());
            row.put("emocion3", actividad.emocionC().getEmocion());
            row.put("expl_c", actividad.getExpl3());
            ok = db.insert("actividad2", null, row)>0 && ok;
            if(!ok){
                ok = db.update("actividad2", row, "id_act_2 = ? AND sexo = ?", new String[]{actividad.getId()+"", sexo})>0 && ok;
                Log.d(TAG, "insertaActividadB: uodate: "+ok);
            }
        }catch (SQLException e) {
            Log.d(TAG, "insertaActividadB: ERROR: " + e.getMessage());
        }
        Log.d(TAG, "insertaActividadB: "+actividad.getString());
        return ok;
    }

    public List<ActividadRedaccionesDto> obtieneActividadesB(SQLiteDatabase db, String sexo){
        List<ActividadRedaccionesDto> lista = new ArrayList<>();
        String query = "SELECT * FROM actividad2 WHERE sexo = ?";
        try{
            Cursor cursor = db.rawQuery(query, new String[]{sexo});
            while (cursor.moveToNext()){
                ActividadRedaccionesDto dto = new ActividadRedaccionesDto();
                dto.setId(cursor.getInt(cursor.getColumnIndex("id_act_2")));
                dto.setSexo(cursor.getString(cursor.getColumnIndex("sexo")));
                dto.setRedaccion(cursor.getString(cursor.getColumnIndex("redaccion")));
                dto.setEmocionMain(emocionesDao.getEmocion(cursor.getInt(cursor.getColumnIndex("emocion1")), sexo, db));
                dto.setExpl1(cursor.getString(cursor.getColumnIndex("expl_a")));
                dto.setEmocionB(emocionesDao.getEmocion(cursor.getInt(cursor.getColumnIndex("emocion2")), sexo, db));
                dto.setExpl2(cursor.getString(cursor.getColumnIndex("expl_b")));
                dto.setEmocionC(emocionesDao.getEmocion(cursor.getInt(cursor.getColumnIndex("emocion3")), sexo, db));
                dto.setExpl3(cursor.getString(cursor.getColumnIndex("expl_c")));
                lista.add(dto);
            }
        }catch(SQLException e){
            Log.d(TAG, "obtieneActividadesB: ERROR: "+e.getMessage());
        }
        Log.d(TAG, "obtieneActividadesB: lista: "+lista.size());
        return lista;
    }

    public void obtieneTodasLasActividades(SQLiteDatabase db, String usuario){
        String query = "SELECT * FROM actividades";

    }

    public boolean modificaRespuestaActividadUno(SQLiteDatabase db, String usuario, int indice) {
        boolean ok = true;
        ContentValues raw = new ContentValues();
        raw.put("act_1", indice);
        ok = db.update("actividades", raw, "usuario = ?", new String[]{usuario}) >0 && ok;
        Log.d(TAG, "modificaRespuestaActividadUno: "+indice);
        return ok;
    }

    public boolean modificaRespuestaActividadDos(SQLiteDatabase db, String usuario, int indice) {
        boolean ok = true;
        ContentValues raw = new ContentValues();
        raw.put("act_2", indice);
        ok = db.update("actividades", raw, "usuario = ?", new String[]{usuario}) >0 && ok;
        Log.d(TAG, "modificaRespuestaActividadDos: "+indice);
        return ok;
    }

    public IndicesActividadDto obtieneIndices(SQLiteDatabase db, String usuario) {
        IndicesActividadDto dto = new IndicesActividadDto();
        String query = "SELECT * FROM actividades WHERE usuario = ?";
        Cursor cursor = db.rawQuery(query, new String[]{usuario});
        if(cursor.moveToNext()) {
            dto.setIdActividad(cursor.getInt(cursor.getColumnIndex("id_actividad")));
            dto.setUsuario(cursor.getString(cursor.getColumnIndex("usuario")));
            dto.setIndiceA(cursor.getInt(cursor.getColumnIndex("act_1")));
            dto.setIndiceB(cursor.getInt(cursor.getColumnIndex("act_2")));
        }
        return dto;
    }
}
