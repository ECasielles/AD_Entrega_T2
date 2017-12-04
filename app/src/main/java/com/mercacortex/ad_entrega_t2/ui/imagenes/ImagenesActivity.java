package com.mercacortex.ad_entrega_t2.ui.imagenes;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.mercacortex.ad_entrega_t2.R;
import com.mercacortex.ad_entrega_t2.utils.Memoria;
import com.mercacortex.ad_entrega_t2.utils.RestClient;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * 5. Crear una aplicación que permita ver una secuencia de imágenes descargadas de Internet.
 * Se podrá introducir la ruta al fichero con enlaces a imágenes en la red,
 * el cual estará almacenado en un servidor web (local o en Intenet) y contendrá en cada línea un enlace a una imagen.
 * Cuando se pulse el botón de descarga, se podrán mostrar una a una las imágenes descargadas
 * usando los botones inferiores (para pasar a la imagen anterior o a la siguiente).
 * Se deberán usar Android Asynchronous Http Client y Picasso.
 * ejemplo de fichero enlaces.txt:
 * https://i.imgur.com/tGbaZCY.jpg
 * http://192.168.2.50/imagen/foto.jpg
 * http://192.168.2.50/noexiste.png
 * https://i.imgur.com/MU2dD8E.jpg
 */
public class ImagenesActivity extends AppCompatActivity {

    public static final String IMAGENES = "imagenes.txt";

    EditText edtPath;
    Button btnDownload, btnDown, btnUp;
    TextView txvPosition;
    ImageView imvImage;
    ProgressDialog progress;
    Memoria memoria;

    int currentImage;
    ArrayList<String> images;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagenes);
        progress = new ProgressDialog(this);
        currentImage = 0;
        memoria = new Memoria(this);

        edtPath = (EditText) findViewById(R.id.edtPath);
        btnDownload = (Button) findViewById(R.id.btnDownload);
        btnUp = (Button) findViewById(R.id.btnUp);
        btnDown = (Button) findViewById(R.id.btnDown);
        imvImage = (ImageView) findViewById(R.id.imvImage);
        txvPosition = (TextView) findViewById(R.id.txvPosition);

        btnDown.setEnabled(false);
        btnUp.setEnabled(false);
        txvPosition.setText("");
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDownload:
                if (edtPath.getText().toString().isEmpty())
                    Toast.makeText(this, "Dirección vacía", Toast.LENGTH_SHORT).show();
                else
                    downloadImages();
                break;
            case R.id.btnUp:
                currentImage = (currentImage + 1) % images.size();
                setImage();
                break;
            case R.id.btnDown:
                currentImage = (currentImage + images.size() - 1) % images.size();
                setImage();
                break;
        }
    }

    private void setImage() {
        try {
            Picasso.with(this).load(images.get(currentImage)).error(R.drawable.error).placeholder(R.drawable.placeholder).into(imvImage);
            txvPosition.setText((currentImage + 1) + "/" + images.size());
        } catch (IndexOutOfBoundsException ex) {
            Toast.makeText(this, "No hay imagenes...", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException ex) {
            Toast.makeText(this, "Archivo de imágenes no válido...", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadImages() {
        String path = edtPath.getText().toString();
        if (!path.startsWith("http://") && !path.startsWith("https://"))
            path = "http://" + path;
        edtPath.setText(path);
        RestClient.get(path, new FileAsyncHttpResponseHandler(this) {
            List<String> urls = new ArrayList<>();

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
                btnDown.setEnabled(false);
                btnUp.setEnabled(false);
                txvPosition.setText("");
                Toast.makeText(ImagenesActivity.this, "Fallo en la conexión", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File response) {
                progress.dismiss();
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = reader.readLine()) != null)
                        if (!line.isEmpty())
                            urls.add(line);
                } catch (IOException e) {
                    Toast.makeText(ImagenesActivity.this, "Fallo de lectura del archivo", Toast.LENGTH_SHORT);
                }
                images = new ArrayList<>();
                if (urls.size() > 0)
                    for (String url : urls)
                        if (Patterns.WEB_URL.matcher(url).matches())
                            images.add(url);
                if (images.size() > 0) {
                    btnDown.setEnabled(true);
                    btnUp.setEnabled(true);
                    txvPosition.setText((currentImage + 1) + "/" + images.size());
                    setImage();
                } else {
                    btnDown.setEnabled(false);
                    btnUp.setEnabled(false);
                    txvPosition.setText("");
                    Toast.makeText(ImagenesActivity.this, "Fichero mal formateado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
