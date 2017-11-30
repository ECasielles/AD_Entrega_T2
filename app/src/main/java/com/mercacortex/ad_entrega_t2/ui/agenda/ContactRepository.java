package com.mercacortex.ad_entrega_t2.ui.agenda;

import android.content.Context;

import com.mercacortex.ad_entrega_t2.utils.Memoria;

import java.util.ArrayList;

/**
 * Contiene los contactos de la agenda
 */
public class ContactRepository extends ArrayList<Contact> {

    private static final String CONTACTOS = "contactos.txt";
    private static ContactRepository contactRepository;
    private static Memoria memoria;

    private ContactRepository() {
        initialize();
    }

    public static ContactRepository getInstance(Context context) {
        if (contactRepository == null) {
            memoria = new Memoria(context);
            contactRepository = new ContactRepository();
        }
        return contactRepository;
    }

    private void initialize() {
        String content = memoria.leerInterna(CONTACTOS, Memoria.UTF8).getContenido();
        if (content == null) {
            memoria.escribirInterna(CONTACTOS, "", false, Memoria.UTF8);
            for (int i = 0; i < 20; i++) {
                String[] names = {"Pepe", "Juana", "Pedro", "Magdalena", "Rafa", "Soraya", "Antonio", "Vicky"};
                String name = names[(int) (Math.random() * names.length)];
                Contact contact = new Contact(name, name + i + "@gmail.com", "952" + (int) (Math.random() * 1000000));
                addToFile(contact.getName(), contact.getEmail(), contact.getPhone());
            }
        } else {
            String[] lectura = content.split("\n");
            for (int i = 0; i < lectura.length; i++) {
                String[] line = lectura[i].split(";");
                add(new Contact(line[0], line[1], line[2]));
            }
        }
    }

    public boolean addNew(String name, String email, String phone) {
        boolean duplicate = false;
        for (int i = 0; i < contactRepository.size() && !duplicate; i++) {
            if (get(i).getEmail().equals(email))
                duplicate = true;
        }
        if (!duplicate) {
            addToFile(name, email, phone);
            return true;
        } else
            return false;
    }

    private void addToFile(String name, String email, String phone) {
        memoria.escribirInterna(CONTACTOS, name + ";" + email + ";" + phone + "\n", true, Memoria.UTF8);
        add(new Contact(name, email, phone));
    }

}
