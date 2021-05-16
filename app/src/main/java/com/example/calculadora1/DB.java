package com.example.calculadora1;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {

    //Roberto Carlos Hernandez Melendez USIS016520
    //Duma Roberto Zelaya Mejia USIS007420
    //Jose Roberto Del Rio Maravilla USIS015220

    static String nombre_bd = "DB_usuario";
    static String tblusuario = "CREATE TABLE tblusuario(idusu integer primary key autoincrement, nombres text, apellidos text, usuario text, contra text)";
    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, nombre_bd, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tblusuario);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor administracion_usuarios(String accion, String[] datos){
        Cursor datocursor = null;
        SQLiteDatabase sqLiteDatabaseW = getWritableDatabase();
        SQLiteDatabase sqLiteDatabaseR = getReadableDatabase();

        switch (accion){
            case "consultar":
                datocursor = sqLiteDatabaseR.rawQuery("select * from tblusuario",null);
                break;

            case "nuevo":
                sqLiteDatabaseW.execSQL("INSERT INTO tblusuario(nombres, apellidos, usuario, contra) VALUES ('"+datos[1]+"','"+datos[2]+"','"+datos[3]+"','"+datos[4]+"')");
                break;
            case "modificar":
                sqLiteDatabaseW.execSQL("update tblusuario set nombres='"+datos[1]+"',apellidos='"+datos[2]+"',usuario='"+datos[3]+"',contra='"+datos[4]+"' where idusu='"+datos[0]+"'");
                break;
            case "eliminar":
                sqLiteDatabaseW.execSQL("DELETE FROM tblusuario WHERE idusu='"+ datos[0]+"'");
                break;
        }

        return datocursor; }

    public Cursor eliminar(String accion,String idd){
        Cursor datocursor = null;
        SQLiteDatabase sqLiteDatabaseW = getWritableDatabase();
        SQLiteDatabase sqLiteDatabaseR = getReadableDatabase();

        switch (accion){

            case "eliminar":
                sqLiteDatabaseW.execSQL("DELETE FROM tblusuario WHERE idusu='"+ idd+"'");
                break;
        }

        return datocursor; }
}