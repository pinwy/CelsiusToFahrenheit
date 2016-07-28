package com.conversion.luiss.celsiustofahrenheit.modelo;

/**
 * Created by luiss on 27/07/2016.
 */
public class Conversion {

    private String Fecha;
    private float GradosCelcius;
    private float GradosFahrenheit;

    public Conversion() {
        Fecha = "";
        GradosCelcius = 0;
        GradosFahrenheit = 0;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public float getGradosCelcius() {
        return GradosCelcius;
    }

    public void setGradosCelcius(float gradosCelcius) {
        GradosCelcius = gradosCelcius;
    }

    public float getGradosFahrenheit() {
        return GradosFahrenheit;
    }

    public void setGradosFahrenheit(float gradosFahrenheit) {
        GradosFahrenheit = gradosFahrenheit;
    }
}
