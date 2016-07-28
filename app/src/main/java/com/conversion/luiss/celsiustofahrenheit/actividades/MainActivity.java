package com.conversion.luiss.celsiustofahrenheit.actividades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.conversion.luiss.celsiustofahrenheit.R;
import com.conversion.luiss.celsiustofahrenheit.Util.Constantes;
import com.conversion.luiss.celsiustofahrenheit.Util.general;
import com.conversion.luiss.celsiustofahrenheit.baseDatos.baseDatos;
import com.conversion.luiss.celsiustofahrenheit.modelo.Conversion;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Validator.ValidationListener {

    //Variables Globales
    @NotEmpty(message = "Es requerido")
    EditText txtGradosCelcius;
    Button btnConvertir;
    TextView lblResultado;

    general gn;
    baseDatos bd;
    //Se crea una variable global para manejar la base de datos, así abrimos solo una vez conexion y se reutiliza el objeto
    //durante el flujo de toda la pantalla
    SQLiteDatabase baseDatos;
    ATConvertirGrados aTConvertirGrados;
    String sRespuestaConversion;

    //Se crea un conexto general para toda la pantalla para evitar estar obtenniendolo cada vez que se necesite
    Context _this;

    //Validaciones con Saripaar
    Validator validator;

    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InicializarComponentes();
    }

    private void InicializarComponentes(){

        txtGradosCelcius = (EditText) findViewById(R.id.txtGradosCelcius);
        btnConvertir = (Button) findViewById(R.id.btnConvertir);
        lblResultado = (TextView) findViewById(R.id.lblResultado);
        gn = new general();
        bd = new baseDatos();
        _this = getApplicationContext();

        validator = new Validator(this);
        validator.setValidationListener(this);

        btnConvertir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validator.validate();
            }
        });

        //Se agrega el tag para saber desde que pantalla se mandó llamar está funcion
        baseDatos = bd.abrirBaseDatos(this, TAG);
    }

    /**
     * Clase de tarea asincrona para el consumo del webservice
     */
    private class ATConvertirGrados extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "doInBackground");

            int iCelcius = Integer.parseInt(params[0]);
            sRespuestaConversion = "";
            sRespuestaConversion = gn.getDatoWebService(Constantes.SOAP_URL2,Constantes.SOAP_NAMESPACE2,Constantes.SOAP_METODO_CONVERTIR2,iCelcius);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            if ( !sRespuestaConversion.equals("-1")) {
                //Mostrar datos en pantalla
                double d = Double.parseDouble(sRespuestaConversion);
                d = validarRespuesta(d);
                //Guardar en base de datos
                float fGradosCelcius = Float.parseFloat(txtGradosCelcius.getText().toString());
                String sGradosRespuesta = String.valueOf(d);
                float fGradosFahrenheit = Float.parseFloat(sGradosRespuesta);
                guardarBaseDatos(fGradosCelcius,fGradosFahrenheit);
                lblResultado.setText(sGradosRespuesta + getString(R.string.add_respuesta));
            }else{
                //Error en el consumo
                Log.i(TAG, getString(R.string.error_consumo));
            }
        }
    }

    /**
     * Método que valida la respuesta obtenida por le webservice, tiene como funcion darle formato a
     * un double, en caso que contenga mas de 2 deciamles los truncará a 2 decimales
     * @param dValor valor double a formatear
     * @return regresa un valor doble ya formateado a 2 caracteres
     */
    private double validarRespuesta(double dValor)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(dValor));
    }

    /**
     * Método que inserta en la base de datos los grados que se convirtieron así como la respuesta
     * @param fGradosCelcius recibe los grados celcius que se mandaron a convertir
     * @param fGradosFahrenheit recibe los grados fahrenheit que es obtienen como respueta del WS
     * @return
     */
    private boolean guardarBaseDatos(float fGradosCelcius, float fGradosFahrenheit){
        boolean bRespuesta = false;

        Conversion obj = new Conversion();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.formato_fecha));
            String fecha = sdf.format(new Date());

            obj.setFecha(fecha);
            obj.setGradosCelcius(fGradosCelcius);
            obj.setGradosFahrenheit(fGradosFahrenheit);

            if ( bd.insertarConversion(baseDatos,obj)){
                //TODO: Obtener los datos de la base de datos (en este caso tambien los va mostrar) si da tiempo sacar funcionaildad
                ArrayList<Conversion> lista = new ArrayList<>();
                lista = bd.obtenerListConversiones(baseDatos);
                if(lista!= null){
                    mostrarDatosBD(lista);
                    bRespuesta = true;
                }

            }
        }catch (Exception e){
            Log.e(TAG,getString(R.string.error_guardarBaseDatos) + e);
        }

        return bRespuesta;
    }

    /**
     * Método que se encarga de recorrer la lista de Conversiones en la cual se tienen los registros de la base de datos
     * estos son previamente llenados
     * @param list Recibe una lista con todos los registros de la base de dato
     */
    private void mostrarDatosBD(ArrayList<Conversion> list){

        int iTam = list.size();
        for( int i = 0 ; i < iTam ; i++ ){
            Log.i(TAG,"fecha: " + list.get( i ).getFecha() + " " + "gradosCelcius: "
                    + list.get( i ).getGradosCelcius() + " " + "gradosFahrenheit: "
                    + list.get( i ).getGradosFahrenheit());

        }

    }

    /**
     * Método utilizado por la libreria saripaar, el cual es llamado siempre y cuando se cumplan
     * todas las validaciones que se definieron para todos los controles
     * */
    @Override
    public void onValidationSucceeded() {
        String sGrados = txtGradosCelcius.getText().toString();
        if(!sGrados.equals("")){
            if(gn.isOnline(_this)){
                aTConvertirGrados = new ATConvertirGrados();
                aTConvertirGrados.execute(txtGradosCelcius.getText().toString());
                //ocultar el teclado
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtGradosCelcius.getWindowToken(), 0);
            }else{
                gn.ponerMensaje(_this,getString(R.string.mensaje_sin_internet),Constantes.DURACION_MENSAJE_LARGO);
            }

        }else{
            Toast.makeText(_this, R.string.campo_requerido,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *Método utilizado por la libreria saripaar, el cual es llamado cuando alguna valicaion de
     * controles no se cumple
     * */
    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors)
        {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {
                view.requestFocus();
                ((EditText) view).setError(message);
            }
            else
            {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
