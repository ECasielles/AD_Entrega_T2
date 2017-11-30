package com.mercacortex.ad_entrega_t2.ui.agenda;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.mercacortex.ad_entrega_t2.R;

/**
 * 1. Crear una aplicación que permita añadir a una agenda los nombres,
 * teléfonos y emails de nuestros amigos.
 * Los datos se guardarán en un fichero en memoria interna.
 * También se podrán ver en pantalla los datos de los contactos existentes.
 *
 * @author Enrique Casielles Lapeira
 * @version 1.0
 * @see AppCompatActivity
 */
public class AgendaActivity extends AppCompatActivity implements AddContactFragment.OnContactAddedListener,
RecyclerViewContactFragment.ListContactListener {

    AddContactFragment addContactFragment;
    RecyclerViewContactFragment recyclerViewContactFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        FragmentManager fragmentManager = getSupportFragmentManager();
        recyclerViewContactFragment = (RecyclerViewContactFragment) fragmentManager.findFragmentByTag(RecyclerViewContactFragment.TAG);
        if (recyclerViewContactFragment == null){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            recyclerViewContactFragment = RecyclerViewContactFragment.newInstance(null);
            fragmentTransaction.replace(R.id.activity_agenda, recyclerViewContactFragment, RecyclerViewContactFragment.TAG).commit();
        }

    }

    @Override
    public void listContacts() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void addNewContact() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        addContactFragment = (AddContactFragment) fragmentManager.findFragmentByTag(AddContactFragment.TAG);
        if (addContactFragment == null){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            addContactFragment = AddContactFragment.newInstance(null);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.activity_agenda, addContactFragment, AddContactFragment.TAG).commit();
        }
    }

}
