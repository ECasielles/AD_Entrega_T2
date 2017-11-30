package com.mercacortex.ad_entrega_t2.ui.alarma;

import android.content.Context;

import com.mercacortex.ad_entrega_t2.utils.Memoria;

import java.util.ArrayList;

/**
 */
public class AlarmRepository extends ArrayList<Alarm> {

    private static final String ALARMAS = "alarmas.txt";
    private static AlarmRepository alarmRepository;
    private static Memoria memoria;
    private static long MAX_MILIS = 120000;

    private AlarmRepository() {
        initialize();
    }

    public static AlarmRepository getInstance(Context context) {
        if (alarmRepository == null) {
            memoria = new Memoria(context);
            alarmRepository = new AlarmRepository();
        }
        return alarmRepository;
    }

    private void initialize() {
        if (memoria.disponibleLectura()) {
            String content = memoria.leerExterna(ALARMAS, Memoria.UTF8).getContenido();
            if (content == null) {
                memoria.escribirExterna(ALARMAS, "", false, Memoria.UTF8);
                for (int i = 0; i < 5; i++) {
                    String[] texts = {"Comer", "Siesta", "Merendar", "Trabajar", "Dormir"};
                    String text = texts[i];
                    Alarm alarm = new Alarm(i, text, (long) (Math.random() * MAX_MILIS));
                    addToFile(alarm.getId(), alarm.getText(), alarm.getTime());
                }
            } else {
                String[] lectura = content.split("\n");
                for (int i = 0; i < lectura.length; i++) {
                    String[] line = lectura[i].split(";");
                    add(new Alarm(Integer.parseInt(line[0]), line[1], Long.parseLong(line[2])));
                }
            }
        }
    }

    private void addToFile(int id, String text, long milis) {
        if (memoria.disponibleLectura()) {
            memoria.escribirExterna(ALARMAS, id + ";" + text + ";" + milis + "\n", true, Memoria.UTF8);
            add(new Alarm(id, text, milis));
        }
    }

}
