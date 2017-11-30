package com.mercacortex.ad_entrega_t2.ui.calendario;

import android.content.Context;

import com.mercacortex.ad_entrega_t2.utils.Memoria;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by icenri on 11/30/17.
 */

public class CalendarRepository extends ArrayList<Date> {

    private static final String CALENDARIO = "calendario.txt";
    private static CalendarRepository calendarRepository;
    private static Memoria memoria;

    private CalendarRepository() {
        initialize();
    }

    public static CalendarRepository getInstance(Context context) {
        if (calendarRepository == null) {
            memoria = new Memoria(context);
            calendarRepository = new CalendarRepository();
        }
        return calendarRepository;
    }

    private void initialize() {
        if (memoria.disponibleLectura()) {
            String content = memoria.leerExterna(CALENDARIO, Memoria.UTF8).getContenido();
            if (content == null) {
                memoria.escribirExterna(CALENDARIO, "", false, Memoria.UTF8);
                addToFile("17/10/12");
                addToFile("17/11/1");
                addToFile("17/12/6");
                addToFile("17/12/8");
                addToFile("17/12/25");
                addToFile("17/12/26");
                addToFile("17/12/27");
                addToFile("17/12/28");
                addToFile("17/12/29");
                addToFile("17/12/30");
                addToFile("17/12/31");
                addToFile("18/1/1");
                addToFile("18/1/2");
                addToFile("18/1/3");
                addToFile("18/1/4");
                addToFile("18/1/5");
                addToFile("18/2/26");
                addToFile("18/2/27");
                addToFile("18/2/28");
                addToFile("18/3/1");
                addToFile("18/3/2");
                addToFile("18/3/26");
                addToFile("18/3/27");
                addToFile("18/3/28");
                addToFile("18/3/29");
                addToFile("18/3/30");
                addToFile("18/5/1");
            } else {
                String[] lectura = content.split("\n");
                for (int i = 0; i < lectura.length; i++) {
                    String[] line = lectura[i].split("/");
                    add(new Date(Integer.parseInt(line[0]) +  100, Integer.parseInt(line[1]) - 1,
                            Integer.parseInt(line[2])));
                }
            }
        }
    }

    private void addToFile(String holiday) {
        if (memoria.disponibleLectura()) {
            memoria.escribirExterna(CALENDARIO, holiday + "\n", true, Memoria.UTF8);
            String[] line = holiday.split("/");
            add(new Date(Integer.parseInt(line[0]), Integer.parseInt(line[1]),
                    Integer.parseInt(line[2])));
        }
    }
}
