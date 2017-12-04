package com.mercacortex.ad_entrega_t2.ui.subida;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.mercacortex.ad_entrega_t2.R;
import com.mercacortex.ad_entrega_t2.utils.RestClient;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

/**
 * 7. Crear una aplicación que permita elegir un fichero usando algún explorador de archivos
 * instalado en el dispositivo y lo suba a la carpeta uploads situada en el servidor web del propio equipo o en Internet.
 * Solo se permitirán archivos jpg, png, html, txt, mp3 o pdf y de un tamaño máximo de 1 Mb.
 * En caso de que ya exista el archivo, no se subirá.
 * Se tendrán en cuenta los errores que se pueden dar en la comunicación a través de la red.
 * Se mejorará la seguridad para evitar que alguien pueda subir archivos a la carpeta uploads o acceder a su contenido.
 */
public class SubidaActivity extends AppCompatActivity {

    public final static String WEB = "https://alumno.mobi/~alumno/superior/casielles/test/php/upload.php";
    private static final int FILE_PICKER_REQUEST_CODE = 1;
    private Button btnAbrir;
    private TextView txvInfo;
    MaterialFilePicker filePicker;
    ProgressDialog progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subida);
        progreso = new ProgressDialog(this);
        btnAbrir = (Button) findViewById(R.id.btnAbrir);
        txvInfo = (TextView) findViewById(R.id.txvInfo);
        filePicker = new MaterialFilePicker().withActivity(this)
                .withRequestCode(FILE_PICKER_REQUEST_CODE).withFilterDirectories(true)
                .withHiddenFiles(false);
        btnAbrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txvInfo.getText().toString().isEmpty())
                    filePicker.start();
                else
                    subida();
            }
        });
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            txvInfo.setText(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
            btnAbrir.setText("Subir el archivo");
        } else
                Toast.makeText(this, "Error: " + resultCode, Toast.LENGTH_SHORT).show();
    }

    private void subida() {
        String fichero = txvInfo.getText().toString();
        File myFile;
        Boolean existe = true;
        myFile = new File(fichero);
        RequestParams params = new RequestParams();

        try {
            params.put("fileToUpload", myFile);
        } catch (FileNotFoundException e) {
            existe = false;
            txvInfo.setText("Error en el fichero: " + e.getMessage());
        }
        if (existe)
            RestClient.post(WEB, params, new TextHttpResponseHandler() {
                @Override
                public void onStart() {
                    // called before request is started
                    progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progreso.setMessage("Conectando . . .");
                    //progreso.setCancelable(false);
                    progreso.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            RestClient.cancelRequests(getApplicationContext(), true);
                        }
                    });
                    progreso.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String response) {
                    progreso.dismiss();
                    txvInfo.setText(response);
                    txvInfo.setText("Correcto: " + statusCode + " " + response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String response, Throwable t) {
                    progreso.dismiss();
                    txvInfo.setText("Fallo: " + statusCode + " " + t.getMessage() + " " + response);
                }
            });
    }

}
