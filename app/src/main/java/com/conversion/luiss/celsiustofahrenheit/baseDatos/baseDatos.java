package com.conversion.luiss.celsiustofahrenheit.baseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.conversion.luiss.celsiustofahrenheit.modelo.Conversion;

import java.util.ArrayList;

/**
 * Created by luiss on 27/07/2016.
 */
public class baseDatos {

    private String TAG = "baseDatos";
    private String TablaConversion = "conversion";

    //Variables que contienen la cracion de la tabla Conversion
    public static final String crearTablaConversion = "create table if not exists "
            + " conversion (idConversion integer primary key autoincrement, "
            + " fecha TEXT,gradosCelcius REAL, gradosFahrenheit REAL);";


    /**
     * Método que abre la base de datos y crea la tabla que se necesita
     * @param context recibe el contexto que de la actividad que lo necesita
     * @param TAG Recibe le tag de la clase que lo manda llamar
     * @return regresa un objeto de tipo SQLiteDatabase ya abierto
     */
    public SQLiteDatabase abrirBaseDatos(Context context, String TAG) {

        SQLiteDatabase baseDatos = null;

        String nombreBd = "datos";
        try {
            baseDatos = context.openOrCreateDatabase(nombreBd, context.MODE_WORLD_WRITEABLE, null);
            //Siempre que se abra conexión se intenta crear la tabla, y se valida en el query si no existe se cree
            baseDatos.execSQL(crearTablaConversion);
        } catch (Exception e) {
            Log.i(TAG, "Error al abrir o crear la base de datos" + nombreBd + e);
        }

        return baseDatos;
    }

    /**
     * Método que obtiene todos los registros de la base de datos
     * @param baseDatos Regresa una lista de Conversiones para que sea utilizada en la activdad
     * @return
     */
    public ArrayList<Conversion> obtenerListConversiones(SQLiteDatabase baseDatos)
    {
        int idTarea = 0;

        //Leer la base de datos para crear los botones de los focos
        String[] columnas = new String[] { "idConversion","fecha","gradosCelcius","gradosFahrenheit" };
        Cursor cConversionBd = baseDatos.query(TablaConversion, columnas,null,null,null,null,"idConversion");
        ArrayList<Conversion> listConversiones = new ArrayList<>();

        try {

            int idfecha = cConversionBd.getColumnIndex("fecha");
            int idgradosCelcius = cConversionBd.getColumnIndex("gradosCelcius");
            int idgradosFahrenheit = cConversionBd.getColumnIndex("gradosFahrenheit");

            Conversion obj;
            for (cConversionBd.moveToFirst(); !cConversionBd.isAfterLast(); cConversionBd.moveToNext()) {
                //Esto lo puedo pasar al constructor pero así queda mas explisito
                obj = new Conversion();
                obj.setFecha(cConversionBd.getString(idfecha));
                obj.setGradosCelcius(cConversionBd.getFloat(idgradosCelcius));
                obj.setGradosFahrenheit(cConversionBd.getFloat(idgradosFahrenheit));
                listConversiones.add(obj);
            }
        }catch (Exception e){
            Log.e(TAG,"Error al obtenerListConversiones " + e.getMessage());
        }finally {
            cConversionBd.close();
        }

        return listConversiones;
    }

    /**
     * Método que inserta las conversiones
     * @param baseDatos Recibe un objeto de base de datos previamente abierto
     * @param conversion Recibe un objeto de tipo conversion
     * @return regresa true si se insertó correctamente
     */
    public boolean insertarConversion(SQLiteDatabase baseDatos, Conversion conversion) {

        boolean bRespuesta = false;
        ContentValues values = new ContentValues();

        //agregamos los valores para la tabla
        values.put("fecha", conversion.getFecha());
        values.put("gradosCelcius", conversion.getGradosCelcius());
        values.put("gradosFahrenheit", conversion.getGradosFahrenheit());

        try {
            if ( baseDatos.insert(TablaConversion, null, values) > 0) {
                bRespuesta = true;
            }
        } catch (Exception e) {
            Log.i(TAG, "Error al insertar " + TablaConversion+ e);
        }

        return bRespuesta;

    }


}
