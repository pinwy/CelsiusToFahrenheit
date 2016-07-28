package com.conversion.luiss.celsiustofahrenheit.Util;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by luiss on 27/07/2016.
 */
public class general {

    /**
     * Funcion que se conecta al webservice y regrasa un valor string
     * esta funcion remplazara a cualquiera que tome info del webservice
     * @param sUrl contiene la direccion completa a la que se va conectar
     * @param sNameSpace contiene el espacio de nombres Ejem. http://tempuri.org/
     * @param sMetodo contiene el metodo del webservice al que se va accesar
     * @return sStatus regresa el valor obtenido desde el webservice
     */
    public String getDatoWebService(String sUrl,String sNameSpace,String sMetodo, int iParametro)
    {
        System.out.println("Entro a getDatoWebService");

        String sStatus;
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
            sStatus = response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            sStatus = "-1"; //error
        }

        return sStatus;
    }
}
