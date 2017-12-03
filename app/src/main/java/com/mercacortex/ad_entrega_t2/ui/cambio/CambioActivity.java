package com.mercacortex.ad_entrega_t2.ui.cambio;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.mercacortex.ad_entrega_t2.R;
import com.mercacortex.ad_entrega_t2.utils.Memoria;
import com.mercacortex.ad_entrega_t2.utils.RestClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

/**
 * 6. Modificar la aplicación Conversor de moneda para que utilice el fichero cambio.txt
 * (con el ratio del cambio euros/dólares) almacenado en un servidor web situado en otro equipo del aula o en Internet.
 * Cuando se realice una conversión, se descargará el fichero cambio.txt
 * del servidor web y se empleará el valor contenido en dicho fichero.
 * Se tendrán en cuenta los errores que se pueden dar en la comunicación a través de la red.
 */
public class CambioActivity extends AppCompatActivity {

    private static final String CAMBIO = "cambio.txt";
    EditText edtPath, edtEuro, edtDolar;
    Button btnCargar;
    RadioGroup rgpCambio;
    RadioButton rbtEurosADolares, rbtDolaresAEuros;
    Memoria memoria;
    ProgressDialog progress;
    float cambio = -1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambio);
        progress = new ProgressDialog(this);
        memoria = new Memoria(this);
        edtDolar = (EditText) findViewById(R.id.edtDolares);
        edtEuro = (EditText) findViewById(R.id.edtEuros);
        edtPath = (EditText) findViewById(R.id.edtPath);
        btnCargar = (Button) findViewById(R.id.btnCargar);
        btnCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download(edtPath.getText().toString());
            }
        });
        rbtDolaresAEuros = (RadioButton) findViewById(R.id.rbtDolaresAEuros);
        rbtEurosADolares = (RadioButton) findViewById(R.id.rbtEurosADolares);
        rgpCambio = (RadioGroup) findViewById(R.id.rgpMoneda);
        rgpCambio.setEnabled(false);
        rbtEurosADolares.setEnabled(false);
        rbtDolaresAEuros.setEnabled(false);
        edtEuro.setEnabled(false);
        edtDolar.setEnabled(false);
        rbtEurosADolares.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content;
                if(cambio > 0) {
                    content = edtEuro.getText().toString();
                    if (!content.isEmpty())
                        try {
                            edtDolar.setText(String.valueOf(cambio * Float.parseFloat(content)));
                        } catch (NumberFormatException e) {
                            Toast.makeText(CambioActivity.this, "Formato no válido", Toast.LENGTH_SHORT).show();
                        }
                }
            }
        });
        rbtDolaresAEuros.setOnClickListener(new RadioButton.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content;
                if(cambio > 0) {
                    content = edtDolar.getText().toString();
                    if (!content.isEmpty())
                        try {
                            edtEuro.setText(String.valueOf(Float.parseFloat(content) / cambio));
                        } catch (NumberFormatException e) {
                            Toast.makeText(CambioActivity.this, "Formato no válido", Toast.LENGTH_SHORT).show();
                        }
                }
            }
        });
    }

    private void download(String path) {
        if (!path.startsWith("http://") && !path.startsWith("https://"))
            path = "http://" + path;
        RestClient.get(path, new FileAsyncHttpResponseHandler(this) {
            @Override
            public void onStart() {
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setMessage("Conectando . . .");
                progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        RestClient.cancelRequests(getApplicationContext(), true);
                    }
                });
                progress.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                progress.dismiss();
                Toast.makeText(CambioActivity.this, "Fallo en la conexión", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                progress.dismiss();
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line = reader.readLine();
                    reader.close();
                    if (memoria.disponibleEscritura()) {
                        memoria.escribirExterna(CAMBIO, line.replace(",", "."), false, Memoria.UTF8);
                        Toast.makeText(CambioActivity.this, "Escritura con éxito", Toast.LENGTH_SHORT).show();
                        if (memoria.disponibleLectura()) {
                            cambio = Float.parseFloat(memoria.leerExterna(CAMBIO, Memoria.UTF8).getContenido());
                            Toast.makeText(CambioActivity.this, "Cambio: " + cambio, Toast.LENGTH_SHORT).show();
                            rgpCambio.setEnabled(true);
                            rbtEurosADolares.setEnabled(true);
                            rbtDolaresAEuros.setEnabled(true);
                            edtEuro.setEnabled(true);
                            edtDolar.setEnabled(true);
                            edtEuro.setText(String.valueOf(1f));
                            edtDolar.setText(String.valueOf(1f * cambio));
                        } else
                            Toast.makeText(CambioActivity.this, "No se leyó el archivo", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Toast.makeText(CambioActivity.this, "Fallo de lectura del archivo", Toast.LENGTH_SHORT);
                } catch (Exception e) {
                    Toast.makeText(CambioActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT);
                }
            }
        });
    }

}
