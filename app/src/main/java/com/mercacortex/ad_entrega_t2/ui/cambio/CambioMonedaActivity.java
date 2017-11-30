package com.mercacortex.ad_entrega_t2.ui.cambio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.android.volley.RequestQueue;
import com.mercacortex.ad_entrega_t2.R;
import com.mercacortex.ad_entrega_t2.utils.MySingleton;

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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Ejercicio4Activity.this);
        String preferenceFile = preferences.getString("pageFile", "");

        if(!preferenceFile.isEmpty()){
            edt_savePage.setText(preferenceFile);
        }
    }

    private void doConnection(){
        if(isNetworkAvailable()){
            switch (rg_connectionType.getCheckedRadioButtonId()){
                case R.id.rb_aahc:
                    if (checkPath(edt_path.getText().toString()))
                        aahcRequest(edt_path.getText().toString());
                    break;
                case R.id.rb_java:
                    if (checkPath(edt_path.getText().toString())){
                        new JavaConnection(Ejercicio4Activity.this).execute(getHttpStartingPath(edt_path.getText().toString()));
                    }
                    break;
                case R.id.rb_volley:
                    if (checkPath(edt_path.getText().toString())){
                        makeRequest(getHttpStartingPath(edt_path.getText().toString()));
                    }
                    break;
            }
        }else{
            Toast.makeText(Ejercicio4Activity.this, "No hay conexión a internet disponible", Toast.LENGTH_SHORT).show();
        }

    }

    private void savePage(){
        if(!edt_savePage.getText().toString().isEmpty()){
            fileName = edt_savePage.getText().toString();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Ejercicio4Activity.this);
            preferences.edit().putString("pageFile", fileName).apply();

            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                File file = new File(Environment.getExternalStorageDirectory(), fileName);

                try {
                    if (!currentResponse.isEmpty()){
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
                        writer.write(currentResponse);
                        writer.close();
                        Toast.makeText(Ejercicio4Activity.this, "Pagina guardada en "+ fileName, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Ejercicio4Activity.this, "No hay pagina que guardar", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private boolean checkPath(String path){
        boolean ok = true;

        if(path.isEmpty()){
            Toast.makeText(Ejercicio4Activity.this, "Dirección vacía", Toast.LENGTH_SHORT).show();
            ok = false;
        }

        return ok;
    }

    private void aahcRequest(String path){

        final ProgressDialog progress = new ProgressDialog(Ejercicio4Activity.this);

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
                Toast.makeText(Ejercicio4Activity.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                progress.dismiss();
                currentResponse = responseString;
                wv_web.loadDataWithBaseURL(null, responseString, "text/html", "UTF-8", null);
            }
        });
    }

    //To be made asynchronously
    public  Result connectJava(String texto) {
        URL url;
        HttpURLConnection urlConnection = null;
        int response;
        Result result= new Result();
        try {
            url = new URL(texto);
            urlConnection = (HttpURLConnection) url.openConnection();
            response = urlConnection.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                result.setCode(true);
                result.setContent(read(urlConnection.getInputStream()));
            } else {
                result.setCode(false);
                result.setMessage("Error en el acceso a la web: " + String.valueOf(response));
            }
        } catch (IOException e) {
            result.setCode(false);
            result.setMessage("Excepción: " + e.getMessage());
        } finally {
            try {
                if (urlConnection != null)
                    urlConnection.disconnect();
            } catch (Exception e) {
                result.setCode(false);
                result.setMessage("Excepción: " + e.getMessage());
            }
            return result;
        }
    }

    private String read(InputStream in) throws IOException{
        StringBuffer result = new StringBuffer();

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;

        while((line = reader.readLine()) != null){
            result.append(line);
        }

        reader.close();

        return result.toString();
    }

    private String getHttpStartingPath(String path){
        String result = path;

        if(!result.startsWith("http://")){
            result = "http://"+result;
        }

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
        final ProgressDialog progress = new ProgressDialog(Ejercicio4Activity.this);
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
// Set retry policy
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
    class JavaConnection extends AsyncTask<String, String, Result>{

        ProgressDialog progress;
        Context context;

        public JavaConnection(Context context){
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
        protected Result doInBackground(String... params) {
            Result result;

            result = connectJava(params[0]);

            return result;
        }

        @Override
        protected void onPostExecute(Result result) {
            progress.dismiss();

            if(result.isCode()){
                wv_web.loadDataWithBaseURL(null, result.getContent(), "text/html", "UTF-8", null);
                currentResponse = result.getContent();
            }else{
                wv_web.loadDataWithBaseURL(null, result.getMessage(), "text/plain", "UTF-8", null);
            }

            super.onPostExecute(result);
        }

        @Override
        protected void onCancelled() {
            progress.dismiss();
            wv_web.loadDataWithBaseURL(null, "Cancelado", "text/plain", "UTF-8", null);
            super.onCancelled();
        }
    }




}
