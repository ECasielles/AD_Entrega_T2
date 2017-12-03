package com.mercacortex.ad_entrega_t2.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.mercacortex.ad_entrega_t2.R;
import com.mercacortex.ad_entrega_t2.ui.agenda.AgendaActivity;
import com.mercacortex.ad_entrega_t2.ui.alarma.AlarmaActivity;
import com.mercacortex.ad_entrega_t2.ui.calendario.CalendarioActivity;
import com.mercacortex.ad_entrega_t2.ui.cambio.CambioActivity;
import com.mercacortex.ad_entrega_t2.ui.imagenes.ImagenesActivity;
import com.mercacortex.ad_entrega_t2.ui.subida.SubidaActivity;
import com.mercacortex.ad_entrega_t2.ui.web.WebActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAgenda:
                startActivity(new Intent(this, AgendaActivity.class));
                break;
            case R.id.btnAlarmas:
                startActivity(new Intent(this, AlarmaActivity.class));
                break;
            case R.id.btnCalendario:
                startActivity(new Intent(this, CalendarioActivity.class));
                break;
            case R.id.btnWeb:
                startActivity(new Intent(this, WebActivity.class));
                break;
            case R.id.btnImagenes:
                startActivity(new Intent(this, ImagenesActivity.class));
                break;
            case R.id.btnCambio:
                startActivity(new Intent(this, CambioActivity.class));
                break;
            case R.id.btnSubir:
                startActivity(new Intent(this, SubidaActivity.class));
                break;
        }
    }
}