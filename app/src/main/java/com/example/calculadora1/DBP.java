package com.example.calculadora1;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBP extends SQLiteOpenHelper {

    //Roberto Carlos Hernandez Melendez USIS016520
    //Duma Roberto Zelaya Mejia USIS007420
    //Jose Roberto Del Rio Maravilla USIS015220

    static String nombre_bd = "DB_Prod";
    static String tblproducto = "CREATE TABLE tblproducto(idprod integer primary key autoincrement, idUsu text, idl text, nombre text, descripcion text, presentacion text, precio text, urlfoto text)";
    public DBP(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nombre_bd, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tblproducto);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor administracion_de_productos(String accion, String[] datos){
        Cursor datocursor = null;
        SQLiteDatabase sqLiteDatabaseW = getWritableDatabase();
        SQLiteDatabase sqLiteDatabaseR = getReadableDatabase();

        switch (accion){
            case "consultar":
                datocursor = sqLiteDatabaseR.rawQuery("select * from tblproducto",null);
                break;

            case "nuevo":
                sqLiteDatabaseW.execSQL("INSERT INTO tblproducto(idUsu, idl, nombre, descripcion, presentacion, precio, urlfoto) VALUES ('"+datos[1]+"','"+datos[2]+"','"+datos[3]+"','"+datos[4]+"','"+datos[5]+"','"+datos[6]+"','"+datos[7]+"')");
                break;
            case "modificar":
                sqLiteDatabaseW.execSQL("update tblproducto set IdUsu='"+datos[1]+"',idl='"+datos[2]+"',nombre='"+datos[3]+"',descripcion='"+datos[4]+"',presentacion='"+datos[5]+"',precio='"+datos[6]+"',urlfoto='"+datos[7]+"' where idprod='"+datos[0]+"'");
                break;
            case "eliminar":
                sqLiteDatabaseW.execSQL("DELETE FROM tblproducto WHERE idprod='"+ datos[0]+"'");
                break;
        }

        return datocursor; }

    public Cursor eliminar(String accion,String idd){
        Cursor datocursor = null;
        SQLiteDatabase sqLiteDatabaseW = getWritableDatabase();
        SQLiteDatabase sqLiteDatabaseR = getReadableDatabase();

        switch (accion){

            case "eliminar":
                sqLiteDatabaseW.execSQL("DELETE FROM tblproducto WHERE idpeli='"+ idd+"'");
                break;
        }

        return datocursor; }
}