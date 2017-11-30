package com.mercacortex.ad_entrega_t2.ui.alarma;

/**
 */
public class Alarm {
    int id;
    String text;
    long time;

    public Alarm(int id, String text, long time) {
        this.id = id;
        this.text = text;
        this.time = time;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return id + ";" + text + ";" + time;
    }

}
