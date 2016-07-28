package com.conversion.luiss.celsiustofahrenheit.baseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.conversion.luiss.celsiustofahrenheit.modelo.Conversion;

/**
 * Created by luiss on 27/07/2016.
 */
public class baseDatos {

    private String TAG = "baseDatos";
    private String NombreBd = "datos";
    private String TablaConversion = "conversion";

    //Variables que contienen la cracion de la tabla Conversion
    public static final String crearTablaConversion = "create table if not exists "
            + " conversion (idConversion integer primary key autoincrement, "
            + " fecha TEXT,gradosCelcius REAL, gradosFahrenheit REAL);";


    // Procedimiento para abrir la base de datos
    // si no existe se creará, también se creará la tabla focos
    public SQLiteDatabase abrirBaseDatos(Context context, String TAG) {

        SQLiteDatabase baseDatos = null;

        try {
            //deleteDatabase(sNombreBd);
            baseDatos = context.openOrCreateDatabase(NombreBd, context.MODE_WORLD_WRITEABLE, null);
            baseDatos.execSQL(crearTablaConversion);
        } catch (Exception e) {
            Log.i(TAG, "Error al abrir o crear la base de datos" + NombreBd + e);
        }

        return baseDatos;
    }

    public int obtenerListConversiones(SQLiteDatabase baseDatos, Context context)
    {
        int idTarea = 0;

        //Leer la base de datos para crear los botones de los focos
        String[] columnas = new String[] { "idConversion","fecha","gradosCelcius","gradosFahrenheit" };
        Cursor cConversionBd = baseDatos.query(TablaConversion, columnas,null,null,null,null,"idConversion");

        int idfecha = cConversionBd.getColumnIndex("fecha");
        int idgradosCelcius = cConversionBd.getColumnIndex("gradosCelcius");
        int idgradosFahrenheit = cConversionBd.getColumnIndex("gradosFahrenheit");

        //Sacar esta funcionalidad de aquí, regresar una lista de Conversion e imprimir en la actividad para que sea mas generica la funcion
        Conversion obj = new Conversion();
        for (cConversionBd.moveToFirst(); !cConversionBd.isAfterLast(); cConversionBd.moveToNext()) {
            obj.setFecha(cConversionBd.getString(idfecha));
            obj.setGradosCelcius(cConversionBd.getFloat(idgradosCelcius));
            obj.setGradosFahrenheit(cConversionBd.getFloat(idgradosFahrenheit));
            Log.i(TAG,"fecha: " + cConversionBd.getString(idfecha) + " " + "gradosCelcius: " + cConversionBd.getString(idgradosCelcius) + " " + "gradosFahrenheit: " + cConversionBd.getString(idgradosFahrenheit));
        }

        return idTarea;
    }

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
