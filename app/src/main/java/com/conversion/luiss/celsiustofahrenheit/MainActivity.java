package com.conversion.luiss.celsiustofahrenheit;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.conversion.luiss.celsiustofahrenheit.Util.Constantes;
import com.conversion.luiss.celsiustofahrenheit.Util.general;
import com.conversion.luiss.celsiustofahrenheit.baseDatos.baseDatos;
import com.conversion.luiss.celsiustofahrenheit.modelo.Conversion;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    //Variables Globales
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

        btnConvertir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sGrados = txtGradosCelcius.getText().toString();
                if(!sGrados.equals("")){
                    aTConvertirGrados = new ATConvertirGrados();
                    aTConvertirGrados.execute(txtGradosCelcius.getText().toString());
                }else{
                    Toast.makeText(getApplicationContext(),"Teclee un valor",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //Se agrega el tag para saber desde que pantalla se mandó llamar está funcion
        baseDatos = bd.abrirBaseDatos(this, TAG);
    }


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
                lblResultado.setText(String.valueOf(d) + "º F");
                //Guardar en base de datos
                guardarBaseDatos();
            }else{
                //Error en el consumo
                Log.i(TAG, "Error en el consumo");
            }
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdate");
        }

    }

    private double validarRespuesta(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    private boolean guardarBaseDatos(){
        boolean bRespuesta = false;

        Conversion obj = new Conversion();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fecha = sdf.format(new Date());
            float fGradosCelcius = Float.parseFloat(txtGradosCelcius.getText().toString());
            float fGradosFahrenheit = Float.parseFloat(lblResultado.getText().toString());


            obj.setFecha(fecha);
            obj.setGradosCelcius(fGradosCelcius);
            obj.setGradosFahrenheit(fGradosFahrenheit);

            if ( bd.insertarConversion(baseDatos,obj)){
                //TODO: Obtener los datos de la base de datos (en este caso tambien los va mostrar) si da tiempo sacar funcionaildad
                bd.obtenerListConversiones(baseDatos,getApplicationContext());
                bRespuesta = true;
            }
        }catch (Exception e){
            Log.e(TAG,"Error al guardarBaseDatos " + e);
        }

        return bRespuesta;
    }
}
