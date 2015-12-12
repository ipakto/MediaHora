package fyr.uclm.esi.mediahora.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Rosana on 08/12/2015.
 */
public class ConectorBD {
    static final String NOMBRE_BD = "TareasPendientes";
    static final String NOMBRE_TABLA = "Tareas";
    static final String COLUMNA_ID = "_id";
    static final String COLUMNA_TAREA = "tarea";
    static final String COLUMNA_IMPORTANCIA = "importancia";
    private ValoresSQLiteHelper dbHelper;
    private SQLiteDatabase db;

    //Constructor public
    public ConectorBD(Context ctx) {
        dbHelper = new ValoresSQLiteHelper(ctx, NOMBRE_BD, null, 1);
    }

    //Abre la conexión con la base de datos
    public ConectorBD abrir() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    //Cierra la conexión con la base de datos
    public void cerrar() {
        if (db != null) db.close();
    }
    //inserta una tarea en la BD
    public long insertarValor(long fecha, int pasos) {
        ContentValues nuevoValor = new ContentValues();
        nuevoValor.put(COLUMNA_TAREA, fecha);
        nuevoValor.put(COLUMNA_IMPORTANCIA, pasos);
        return db.insert(NOMBRE_TABLA, null, nuevoValor);
    }
    //devuelve todas las tareas public
        Cursor obtenerTodosValores() {
            return db.query(NOMBRE_TABLA, new String[] {COLUMNA_ID, COLUMNA_TAREA, COLUMNA_IMPORTANCIA}, null, null, null, null, null);
        }
    //borra una tarea
        public boolean borrarTarea(long numFila) {
            return db.delete(NOMBRE_TABLA, COLUMNA_ID + "=" + numFila, null) > 0;
        }
}

