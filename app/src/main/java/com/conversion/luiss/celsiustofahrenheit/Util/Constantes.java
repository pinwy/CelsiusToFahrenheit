package com.conversion.luiss.celsiustofahrenheit.Util;

import android.widget.Toast;

/**
 * Created by luiss on 27/07/2016.
 */
public class Constantes {

    //Estos datos fueron utilizados pero el servicio esta caido
    public static final String SOAP_NAMESPACE = "http://www.w3schools.com/webservices/";
    public static final String SOAP_URL = "http://www.w3schools.com/webservices/tempconvert.asmx";
    public static final String SOAP_METODO_CONVERTIR = "CelsiusToFahrenheit";

    //Como se me comentó que el webservice sería publico busqué un equivalente, y al parecer es el mismo
    public static final String SOAP_NAMESPACE2 = "http://www.w3schools.com/xml/";
    public static final String SOAP_URL2 = "http://www.w3schools.com/xml/tempconvert.asmx";
    public static final String SOAP_METODO_CONVERTIR2 = "CelsiusToFahrenheit";

    public static final int DURACION_MENSAJE_LARGO = 1;
    public static final int DURACION_MENSAJE_CORTO = 2;

}
