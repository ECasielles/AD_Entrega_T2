package com.mercacortex.ad_entrega_t2.ui.web;

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

/**
 * 4. Crear una aplicación que pida una dirección web y muestre en pantalla la página indicada en esa dirección.
 * Se podrá elegir entre java.net, Android Asynchronous Http Client o Volley para realizar la conexión con el servidor web.
 * Todas las comunicaciones se realizarán de forma asíncrona.
 * Se debe verificar si hay conexión a Internet
 * También se podrá almacenar en un archivo en la memoria externa la página descargada.
 * Para ello se añadirá un botón y un cuadro de texto en el que se pedirá el nombre con el que se guardará el fichero.
 */
public class WebActivity extends AppCompatActivity {

    public static String TAG = "WebActivity";

    private ProgressDialog progress;
    private EditText edtPath, edtSavePage;
    private RadioGroup rgpConnection;
    private Button btnConnect, btnSavePage;
    private WebView wvwWeb;

    private String currentResponse = "";
    private String fileName = "";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        progress = new ProgressDialog(this);
        edtPath = (EditText) findViewById(R.id.edtPath);
        edtSavePage = (EditText) findViewById(R.id.edtSave);
        rgpConnection = (RadioGroup) findViewById(R.id.rgpConnection);
        wvwWeb = (WebView) findViewById(R.id.wvwWeb);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = edtPath.getText().toString();
                if (url.isEmpty())
                    Toast.makeText(WebActivity.this, "Dirección url vacía", Toast.LENGTH_SHORT).show();
                else if (isNetworkAvailable())
                    doConnection(url);
                else
                    Toast.makeText(WebActivity.this, "No hay conexión a internet disponible", Toast.LENGTH_SHORT).show();
            }
        });
        btnSavePage = (Button) findViewById(R.id.btnSavepage);
        btnSavePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtSavePage.getText().toString().isEmpty()) savePage();
            }
        });
        requestQueue = MySingleton.getInstance(this).getRequestQueue();
        checkLastFilePreference();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) requestQueue.cancelAll(TAG);
    }

    private void savePage() {
        fileName = edtSavePage.getText().toString();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WebActivity.this);
        preferences.edit().putString("pageFile", fileName).apply();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            try {
                if (!currentResponse.isEmpty()) {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
                    writer.write(currentResponse);
                    writer.close();
                    Toast.makeText(WebActivity.this, "Pagina guardada en " + fileName, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(WebActivity.this, "No hay pagina que guardar", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkLastFilePreference() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WebActivity.this);
        String preferenceFile = preferences.getString("pageFile", "");
        if (!preferenceFile.isEmpty()) edtSavePage.setText(preferenceFile);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private void doConnection(String path) {
        if (!path.startsWith("http://") && !path.startsWith("https://"))
            path = "http://" + path;
        switch (rgpConnection.getCheckedRadioButtonId()) {
            case R.id.rbtAahc:
                conectarAahc(path);
                break;
            case R.id.rbtJava:
                new JavaConnection(WebActivity.this).execute(path);
                break;
            case R.id.rbtVolley:
                makeRequest(path);
                break;
        }
    }

    public void conectarAahc(String path) {
        RestClient.get(path, new TextHttpResponseHandler() {
            @Override
            public void onStart() {
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setMessage("Conectando...");
                progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        RestClient.cancelRequests(WebActivity.this, true);
                    }
                });
                progress.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progress.dismiss();
                Toast.makeText(WebActivity.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                progress.dismiss();
                currentResponse = responseString;
                wvwWeb.loadDataWithBaseURL(null, responseString, "text/html", "UTF-8", null);
            }
        });
    }

    //Volley
    public void makeRequest(String url) {
        final String path = url;
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
                        wvwWeb.loadDataWithBaseURL(path, response, "text/html", "utf-8", null);
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
                        wvwWeb.loadDataWithBaseURL(null, message, "text/html", "utf-8", null);
                    }
                });
        stringRequest.setTag(TAG);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 1, 1));
        requestQueue.add(stringRequest);
    }

    /**
     * AsyncTask for JAVA.NET communication
     */
    class JavaConnection extends AsyncTask<String, String, Resultado> {
        ProgressDialog progress;
        Context context;

        JavaConnection(Context context) {
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
            if (resultado.getCodigo()) {
                wvwWeb.loadDataWithBaseURL(null, resultado.getContenido(), "text/html", "UTF-8", null);
                currentResponse = resultado.getContenido();
            } else {
                wvwWeb.loadDataWithBaseURL(null, resultado.getMensaje(), "text/plain", "UTF-8", null);
            }

            super.onPostExecute(resultado);
        }

        @Override
        protected void onCancelled() {
            progress.dismiss();
            wvwWeb.loadDataWithBaseURL(null, "Cancelado", "text/plain", "UTF-8", null);
            super.onCancelled();
        }
    }

}
