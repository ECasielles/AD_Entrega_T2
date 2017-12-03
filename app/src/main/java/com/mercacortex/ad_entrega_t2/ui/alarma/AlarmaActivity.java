package com.mercacortex.ad_entrega_t2.ui.alarma;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mercacortex.ad_entrega_t2.R;

import java.util.List;

/**
 * 2. Se desea programar varias alarmas (cinco como máximo) para que suenen de
 * forma consecutiva cada cierto intervalo de tiempo.
 * Se podrán introducir las alarmas (el tiempo y el texto asociado) por pantalla.
 * Se creará el fichero alarmas.txt en memoria externa.
 * Cada línea del fichero tendrá el tiempo (en minutos) que tardará en sonar la alarma
 * y el texto que se mostrará en la notificación al cumplirse la alarma.
 * Crear una aplicación que lance las alarmas de forma consecutiva.
 * Cuando se cumpla el tiempo de cada alarma, se avisará mediante un sonido y una notificación.
 * Inmediatamente comenzará a contar el tiempo de la siguiente alarma y así sucesivamente hasta terminar todas las alarmas.
 * En pantalla se mostrará el tiempo restante para sonar la siguiente alarma y el número de alarmas que quedan.
 * Por ejemplo, si el fichero alarmas.txt contiene los valores:
 * 4, paso por el primer kilómetro
 * 5, paso por el segundo kilómetro
 * 7, paso por el tercer kilómetro
 * 9, paso por el cuarto kilómetro
 * la 1ª alarma sonará a los 4 minutos de arrancar la aplicación,
 * la 2ª sonará 5 minutos después de la primera, la 3ª sonará 7 minutos después de la segunda, . . .
 */
public class AlarmaActivity extends AppCompatActivity{

    TextView txvAlarms, txvName, txvTime;
    AlarmRepository repository;
    AlarmCountDownManager manager;
    int[] mediaArrayId = { //R.raw.akbar,
            R.raw.golf, R.raw.ping, R.raw.slip };
    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarma);
        txvAlarms = (TextView) findViewById(R.id.txvAlarms);
        txvName = (TextView) findViewById(R.id.txvName);
        txvTime = (TextView) findViewById(R.id.txvTime);
        repository = AlarmRepository.getInstance(this);
        manager = new AlarmCountDownManager(repository);
        manager.startNext();
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.cancelAll();
    }

    public void launchNext() {
        player = MediaPlayer.create(this, mediaArrayId[(int)(Math.random() * mediaArrayId.length)]);
        player.start();
        manager.startNext();
    }

    class AlarmCountDownManager {
        List<Alarm> alarms;
        Alarm currentAlarm;
        int currentAlarmPosition;
        int MAX_ALARMS = 5;
        long interval = 400;
        CountDownTimer timer;

        AlarmCountDownManager(List<Alarm> alarms) {
            this.alarms = alarms.subList(0, MAX_ALARMS);
            this.currentAlarmPosition = 0;
        }

        void startNext() {
            if(currentAlarmPosition < alarms.size()) {
                currentAlarm = alarms.get(currentAlarmPosition++);
                txvAlarms.setText("Quedan " + (alarms.size() - alarms.indexOf(currentAlarm)) + " alarmas");
                txvName.setText(currentAlarm.getText());

                timer = new CountDownTimer(currentAlarm.getTime(), interval) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int mins = (int) millisUntilFinished / 60000;
                        int secs = (int) (millisUntilFinished - mins * 60000) / 1000;
                        txvTime.setText(String.format("%02d:%02d", mins, secs));
                    }
                    @Override
                    public void onFinish() {
                        launchNext();
                    }
                }.start();
            } else {
                txvAlarms.setText("¡Alarmas terminadas!");
                txvName.setText("");
                txvTime.setText("");
            }
        }

        public void cancelAll() {
            timer.cancel();
        }
    }

}