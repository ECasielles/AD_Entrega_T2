package com.mercacortex.ad_entrega_t2.ui.agenda;

/**
 * Contacto contiene los nombres,
 * teléfonos y emails de nuestros amigos.
 */
public class Contact {
    String name, email, phone;

    public Contact(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name + ';' + email + ';' + phone;
    }


}
