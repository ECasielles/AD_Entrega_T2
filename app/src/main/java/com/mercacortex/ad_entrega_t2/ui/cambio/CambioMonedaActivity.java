package com.mercacortex.ad_entrega_t2.ui.cambio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mercacortex.ad_entrega_t2.R;
import com.mercacortex.ad_entrega_t2.utils.Conexion;
import com.mercacortex.ad_entrega_t2.utils.MySingleton;
import com.mercacortex.ad_entrega_t2.utils.RestClient;
import com.mercacortex.ad_entrega_t2.utils.Resultado;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import cz.msebera.android.httpclient.Header;

public class CambioMonedaActivity extends AppCompatActivity {

    private EditText edt_path, edt_savePage;
    private RadioGroup rg_connectionType;
    private Button btn_connect, btn_savePage;
    private WebView wv_web;

    String currentResponse = "";
    String fileName = "";

    RequestQueue requestQueue;
    public static String TAG = "Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio_moneda);

        requestQueue = MySingleton.getInstance(CambioMonedaActivity.this).getRequestQueue();

        edt_path = (EditText)findViewById(R.id.edt_path);
        edt_savePage = (EditText)findViewById(R.id.edt_savePage);
        rg_connectionType = (RadioGroup)findViewById(R.id.rg_connectionType);
        wv_web = (WebView)findViewById(R.id.wv_web);
        btn_connect = (Button)findViewById(R.id.btn_connect);
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doConnection();
            }
        });
        btn_savePage = (Button)findViewById(R.id.btn_savePage);
        btn_savePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePage();
            }
        });

        checkLastFilePreference();
    }

    private void checkLastFilePreference(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CambioMonedaActivity.this);
        String preferenceFile = preferences.getString("pageFile", "");
        if(!preferenceFile.isEmpty()) edt_savePage.setText(preferenceFile);
    }
    private void doConnection(){
        if(isNetworkAvailable()){
            switch (rg_connectionType.getCheckedRadioButtonId()){
                case R.id.rb_aahc:
                    if (checkPath(edt_path.getText().toString()))
                        aahcRequest(edt_path.getText().toString());
                    break;
                case R.id.rb_java:
                    if (checkPath(edt_path.getText().toString()))
                        new JavaConnection(CambioMonedaActivity.this).execute(getHttpStartingPath(edt_path.getText().toString()));
                    break;
                case R.id.rb_volley:
                    if (checkPath(edt_path.getText().toString())) makeRequest(getHttpStartingPath(edt_path.getText().toString()));
                    break;
            }
        } else
            Toast.makeText(CambioMonedaActivity.this, "No hay conexión a internet disponible", Toast.LENGTH_SHORT).show();
    }
    private void savePage(){
        if(!edt_savePage.getText().toString().isEmpty()){
            fileName = edt_savePage.getText().toString();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CambioMonedaActivity.this);
            preferences.edit().putString("pageFile", fileName).apply();

            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                File file = new File(Environment.getExternalStorageDirectory(), fileName);
                try {
                    if (!currentResponse.isEmpty()){
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
                        writer.write(currentResponse);
                        writer.close();
                        Toast.makeText(CambioMonedaActivity.this, "Pagina guardada en "+ fileName, Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(CambioMonedaActivity.this, "No hay pagina que guardar", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private boolean checkPath(String path){
        if(path.isEmpty()){
            Toast.makeText(CambioMonedaActivity.this, "Dirección vacía", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void aahcRequest(String path){
        final ProgressDialog progress = new ProgressDialog(CambioMonedaActivity.this);
        String reformattedPath = getHttpStartingPath(path);
        RestClient.get(reformattedPath, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setMessage("Conectando...");
                progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        RestClient.cancelRequests(getApplicationContext(), true);
                    }
                });
                progress.show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progress.dismiss();
                Toast.makeText(CambioMonedaActivity.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                progress.dismiss();
                currentResponse = responseString;
                wv_web.loadDataWithBaseURL(null, responseString, "text/html", "UTF-8", null);
            }
        });
    }
    private String getHttpStartingPath(String path){
        String result = path;
        if(!result.startsWith("http://")) result = "http://"+result;
        return result;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    public void makeRequest(String url) {
        final String path = url;
        final ProgressDialog progress = new ProgressDialog(CambioMonedaActivity.this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Conectando...");
        progress.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        wv_web.loadDataWithBaseURL(path, response, "text/html", "utf-8", null);
                        currentResponse = response;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progress.dismiss();
                        String message = "";
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            message = "Timeout Error " + error.getMessage();
                        } else if (error instanceof AuthFailureError) {
                            message = "AuthFailure Error " + error.getMessage();
                        } else if (error instanceof ServerError) {
                            message = "Server Error " + error.getMessage();
                        } else if (error instanceof NetworkError) {
                            message = "Network Error " + error.getMessage();
                        } else if (error instanceof ParseError) {
                            message = "Parse Error " + error.getMessage();
                        }
                        wv_web.loadDataWithBaseURL(null, message, "text/html", "utf-8", null);
                    }
                });
        stringRequest.setTag(TAG);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 1, 1));
        requestQueue.add(stringRequest);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(TAG);
        }
    }
    /**
     * AsyncTask for JAVA.NET communication
     */
    class JavaConnection extends AsyncTask<String, String, Resultado> {
        ProgressDialog progress;
        Context context;
        JavaConnection(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(context);
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setMessage("Conectando . . .");
            progress.setCancelable(true);
            progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    JavaConnection.this.cancel(true);
                }
            });
            progress.show();
            super.onPreExecute();
        }
        @Override
        protected Resultado doInBackground(String... params) {
            return Conexion.conectarJava(params[0]);
        }
        @Override
        protected void onPostExecute(Resultado resultado) {
            progress.dismiss();
            if(resultado.getCodigo()){
                wv_web.loadDataWithBaseURL(null, resultado.getContenido(), "text/html", "UTF-8", null);
                currentResponse = resultado.getContenido();
            }else{
                wv_web.loadDataWithBaseURL(null, resultado.getMensaje(), "text/plain", "UTF-8", null);
            }

            super.onPostExecute(resultado);
        }
        @Override
        protected void onCancelled() {
            progress.dismiss();
            wv_web.loadDataWithBaseURL(null, "Cancelado", "text/plain", "UTF-8", null);
            super.onCancelled();
        }
    }

}
