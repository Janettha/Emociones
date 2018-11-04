package janettha.activity1.EmocionesDelegate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import janettha.activity1.EmocionesDao.ActividadesDao;
import janettha.activity1.EmocionesDao.EmocionesDao;
import janettha.activity1.EmocionesDto.ActividadImagenesDto;
import janettha.activity1.EmocionesDto.ActividadRedaccionesDto;
import janettha.activity1.EmocionesDto.EmocionDto;
import janettha.activity1.EmocionesDto.UsuarioDto;

public class EmocionesDelegate {
    private static final String TAG = "EmocionesDelegate";

    EmocionesDao dao;
    ActividadesDao daoActividades;

    public EmocionesDelegate(){
        dao = new EmocionesDao();
        daoActividades = new ActividadesDao();
    }

    public boolean cargaEmociones(Context c, SQLiteDatabase db){
        return dao.Emociones(c, db);
    }

    public EmocionDto obtieneEmocion(int id, String sexo, SQLiteDatabase db){
        return dao.getEmocion(id, sexo, db);
    }

    public List<EmocionDto> obtieneEmociones(String sexo, SQLiteDatabase db){
        return dao.getEmociones(sexo, db);
    }

    public boolean insertaRespuestasUsuario(SQLiteDatabase db, UsuarioDto respuestas){
        return daoActividades.insertaRespuestasUsuario(db, respuestas);
    }

    public boolean modificaRespuestasUsuario(SQLiteDatabase db, UsuarioDto respuestas){
        return daoActividades.modificaRespuestaUsuario(db, respuestas);
    }

    public UsuarioDto obtieneRespuestasUsuario(SQLiteDatabase db, UsuarioDto respuestas){
        return daoActividades.obtieneRespuestasUsuario(db, respuestas);
    }

    //Segunda version
    /*
    public boolean insertaActividadA(SQLiteDatabase db, String sexo, ActividadImagenesDto actividad){
        return daoActividades.insertaActividadA(db, sexo, actividad);
    }*/
/*
    public List<ActividadImagenesDto> obtieneActividadesA(SQLiteDatabase db, String sexo){
        return daoActividades.obtieneActividadesA(db, sexo);
    }
*/
    //Segunda version
    /*
    public boolean insertaActividadB(SQLiteDatabase db, String sexo, ActividadRedaccionesDto actividad){
        return daoActividades.insertaActividadB(db, sexo, actividad);
    }*/
/*
    public List<ActividadRedaccionesDto> obtieneActividadesB(SQLiteDatabase db, String sexo){
        return daoActividades.obtieneActividadesB(db, sexo);
    }
    */
}
