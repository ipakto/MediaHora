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
    static final String NOMBRE_BD = "PasosRealizados";
    static final String NOMBRE_TABLA = "Valores";
    static final String COLUMNA_ID = "_id";
    static final String COLUMNA_FECHA = "fecha";
    static final String COLUMNA_PASOS = "pasos";
    static final String COLUMNA_KCAL = "kcal";
    static final String COLUMNA_TIEMPO = "tiempo";
    static final String COLUMNA_VELOCIDAD = "velocidad";
    static final String COLUMNA_DISTANCIA = "distancia";
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
    public long insertarValor(long fecha, int pasos,double kcal, int tiempo,double velocidad, int distancia) {
        ContentValues nuevoValor = new ContentValues();
        nuevoValor.put(COLUMNA_FECHA, fecha);
        nuevoValor.put(COLUMNA_PASOS, pasos);
        nuevoValor.put(COLUMNA_KCAL, kcal);
        nuevoValor.put(COLUMNA_TIEMPO, tiempo);
        nuevoValor.put(COLUMNA_VELOCIDAD, velocidad);
        nuevoValor.put(COLUMNA_DISTANCIA, distancia);
        return db.insert(NOMBRE_TABLA, null, nuevoValor);
    }
    //devuelve todas las tareas public
        public Cursor obtenerTodosValores() {
            //return db.rawQuery("SELECT * FROM Valores",null);
            return db.query(NOMBRE_TABLA, new String[] {COLUMNA_FECHA, COLUMNA_PASOS,COLUMNA_KCAL,COLUMNA_TIEMPO,COLUMNA_VELOCIDAD,COLUMNA_DISTANCIA}, null, null, null, null, null);
        }
    //borra una tarea
        public boolean borrarTarea(long numFila) {
            return db.delete(NOMBRE_TABLA, COLUMNA_ID + "=" + numFila, null) > 0;
        }
}

