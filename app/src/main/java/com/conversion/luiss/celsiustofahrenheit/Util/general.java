package com.conversion.luiss.celsiustofahrenheit.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by luiss on 27/07/2016.
 */
public class general {

    /**
     * Método que se conecta a un webservice dado con capacidad de enviar un solo parametro,
     * se construye un poco generico para poder ser reutilizado mas facilmente
     * @param sUrl Indica la URL del servicio
     * @param sNameSpace Indica el NameSpace del servicio
     * @param sMetodo Indica el método qeu se va consumir del servicio
     * @param iParametro Indica el parametro, en este caso los grados celcius a convertir
     * @return Regresa la respuesta obtenida, en este caso los grados Fahrenheit
     */
    public String getDatoWebService(String sUrl,String sNameSpace,String sMetodo, int iParametro)
    {
        String sRespuesta;
        try {

            SoapObject request = new SoapObject(sNameSpace, sMetodo);
            request.addProperty("Celsius", iParametro);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.implicitTypes = true;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE(sUrl);
            String sSoapAction  = sNameSpace + sMetodo;
            androidHttpTransport.call(sSoapAction, envelope);
            Object response = envelope.getResponse();
            sRespuesta = response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            sRespuesta = "-1"; //error
        }

        return sRespuesta;
    }

    /**
     * Método que valida si la aplicación tiene acceso a internet, ya sea por wifi o datos
     * @param context Recibe el contexto desde donde se está llamando a este método
     * @return Regresa true cuando se tiene conectividad a internet y false cuando no está conectado
     */
    public boolean isOnline(Context context) {
        try {

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                return true;
            }
        }catch(Exception e){
            Log.e("ERROR","Error al detectar conectividad");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Método encargado de lanzar un Toast en pantalla
     * @param context Recibe el contexto de la actividad que necesita se muestre el mensaje
     * @param sMensaje Recibe el mensaje a mostrar
     * @param iDuracion Recibe la duración de dicho mensaje
     */
    public void ponerMensaje(Context context,String sMensaje,int iDuracion)
    {
        Toast.makeText(context, sMensaje, iDuracion).show();
    }
}
