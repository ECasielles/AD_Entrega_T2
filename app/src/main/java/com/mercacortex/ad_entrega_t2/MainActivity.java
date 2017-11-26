package com.mercacortex.ad_entrega_t2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        /*switch (view.getId()){
            case R.id.btnCodificacion:
                startActivity(new Intent(this, CodificacionActivity.class));
                break;
            case R.id.btnAdminFicheros:
                startActivity(new Intent(this, AdministradorArchivosActivity.class));
                break;
            case R.id.btnAahc:
                startActivity(new Intent(this, ConexionAAHCActivity.class));
                break;
            case R.id.btnAsincrona:
                startActivity(new Intent(this, ConexionAsincronaActivity.class));
                break;
            case R.id.btnVolley:
                startActivity(new Intent(this, ConexionVolleyActivity.class));
                break;
            case R.id.btnDescarga:
                startActivity(new Intent(this, DescargaActivity.class));
                break;
            case R.id.btnSubir:
                startActivity(new Intent(this, SubirArchivosActivity.class));
                break;
        }*/
    }
}