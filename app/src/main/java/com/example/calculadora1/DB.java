package com.example.calculadora1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    Context miContext;
    static String nombreDB = "db_productos";
    static String tblproductos = "CREATE TABLE tblproductos(idProducto integer primary key autoincrement, codigo text, descripcion text, marca text, presentacion text, precio text, urlPhoto text)";

    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nombreDB, factory, version);
        miContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {db.execSQL(tblproductos);}
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public Cursor administracion_productos(String accion, String[] datos){
        try {
            Cursor datosCursor = null;
            SQLiteDatabase sqLiteDatabaseW = getWritableDatabase();
            SQLiteDatabase sqLiteDatabaseR = getReadableDatabase();
            switch (accion) {
                case "consultar":
                    datosCursor = sqLiteDatabaseR.rawQuery("select * from tblproductos order by descripcion", null);
                    break;
                case "nuevo":
                    sqLiteDatabaseW.execSQL("INSERT INTO tblproductos(codigo,descripcion,marca,presentacion,precio,urlPhoto) VALUES ('" + datos[1] + "','" + datos[2] + "','" + datos[3] + "','" + datos[4] + "','" + datos[5] + "','" + datos[6] + "')");
                    break;
                case "modificar":
                    sqLiteDatabaseW.execSQL("UPDATE tblproductos SET codigo='" + datos[1] + "',descripcion='" + datos[2] + "',marca='" + datos[3] + "',presentacion='" + datos[4] + "',precio='" + datos[5] + "',urlPhoto='" + datos[6] + "' WHERE idProducto='" + datos[0] + "'");
                    break;
                case "eliminar":
                    sqLiteDatabaseW.execSQL("DELETE FROM tblproductos WHERE idProducto='" + datos[0] + "'");
                    break;
            }
            return datosCursor;
        }catch (Exception e){
            Toast.makeText(miContext, "Error en la BD "+ e.getMessage(), Toast.LENGTH_LONG).show();
            return null;
        }
    }
}
