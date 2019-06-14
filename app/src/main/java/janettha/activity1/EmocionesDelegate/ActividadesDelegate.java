package janettha.activity1.EmocionesDelegate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import janettha.activity1.EmocionesDao.ActividadesDao;
import janettha.activity1.EmocionesDto.ActividadImagenesDto;
import janettha.activity1.EmocionesDto.ActividadRedaccionesDto;
import janettha.activity1.EmocionesDto.IndicesActividadDto;

public class ActividadesDelegate {
    private static final String TAG = "ActividadesDelegate";

    ActividadesDao dao;

    public ActividadesDelegate(){
        dao = new ActividadesDao();
    }

    public boolean cargaActividadImagenes(Context context, SQLiteDatabase db, String sexo){
        return dao.actividadImagenes(context, db, sexo);
    }

    public List<ActividadImagenesDto> obtieneActividadesA(SQLiteDatabase db, String sexo){
        return dao.obtieneActividadesA(db, sexo);
    }

    public boolean cargaActividadRedacciones(Context context, SQLiteDatabase db, String sexo){
        return dao.actividadRedacciones(context, db, sexo);
    }

    public List<ActividadRedaccionesDto> obtieneActividadesB(SQLiteDatabase db, String sexo){
        return dao.obtieneActividadesB(db, sexo);
    }

    public IndicesActividadDto obtieneIndices(SQLiteDatabase db, String userU) {
        return dao.obtieneIndices(db, userU);
    }
}
