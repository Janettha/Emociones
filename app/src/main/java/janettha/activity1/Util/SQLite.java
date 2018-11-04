package janettha.activity1.Util;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLite extends SQLiteOpenHelper {

    public SQLite(Context contexto, String nombre, CursorFactory factory,
                  int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            for (Consulta a : Consulta.values()) {
                db.execSQL(a.getConsulta());
            }
        } catch (SQLException ex) {
            Log.e("SQLite.onCreate", ex.getMessage());
        } catch (Exception ex) {
            Log.e("SQLite.onCreate", ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
//			case 2:
//				db.execSQL("CREATE TABLE IF NOT EXISTS [pedido_detalle_negado]([id_pedido] TEXT,[id_pedido_detalle] INTEGER,[id_pedido_detalle_opcion] INTEGER,	PRIMARY KEY([id_pedido],[id_pedido_detalle],[id_pedido_detalle_opcion]))");
//				db.execSQL("CREATE TABLE IF NOT EXISTS [conversacion] ([id_conversacion] INTEGER PRIMARY KEY AUTOINCREMENT, [id_origen] INTEGER, [id_estatus] INTEGER, [fecha] INTEGER, [mensaje] TEXT)");
//				db.execSQL("DELETE FROM centro WHERE id_centro = 110");
//				db.execSQL("INSERT INTO centro VALUES(374,'VERACRUZ','A136',3,'CA / CORDOBA / AV. 1 No. 3511')");
//				db.execSQL("INSERT INTO centro VALUES(218,'OAXACA','A138',3,'CA / CEN XOXOCOTLAN PALESTINA')");
//			break;
        }
    }

}

