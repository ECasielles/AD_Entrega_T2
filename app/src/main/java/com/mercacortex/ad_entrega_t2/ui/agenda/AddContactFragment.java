package com.mercacortex.ad_entrega_t2.ui.agenda;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.mercacortex.ad_entrega_t2.R;

import java.util.regex.Pattern;

/**
 * Vista para añadir nuevo contacto
 */
public class AddContactFragment extends Fragment {

    public static final String TAG = "AddContactFragment";
    private OnContactAddedListener callback;
    private FloatingActionButton fab;
    private TextInputLayout tilName, tilEmail, tilPhone;
    private EditText edtName, edtEmail, edtPhone;

    interface OnContactAddedListener {
        void listContacts();
    }

    public static AddContactFragment newInstance (Bundle arguments) {
        AddContactFragment addContactFragment = new AddContactFragment();
        if(arguments != null) {
            addContactFragment.setArguments(arguments);
        }
        return addContactFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (OnContactAddedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + " must implement OnContactAddedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_contact, container, false);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        tilName = (TextInputLayout) rootView.findViewById(R.id.tilName);
        tilEmail = (TextInputLayout) rootView.findViewById(R.id.tilEmail);
        tilPhone = (TextInputLayout) rootView.findViewById(R.id.tilPhone);
        edtName = tilName.getEditText();
        edtEmail = tilEmail.getEditText();
        edtPhone = tilPhone.getEditText();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtName.getText().toString().isEmpty())
                    setEmptyError(tilName);
                else if(edtEmail.getText().toString().isEmpty())
                    setEmptyError(tilEmail);
                else if (edtPhone.getText().toString().isEmpty())
                    setEmptyError(tilPhone);
                else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText()).matches())
                    setFormatError(tilEmail);
                else if (!Pattern.matches("^([0-9]| )*", edtPhone.getText()))
                    setFormatError(tilPhone);
                else
                    addNewContact(edtName.getText().toString(),
                            edtEmail.getText().toString(),
                            edtPhone.getText().toString());
            }
        });
    }

    private void setFormatError(TextInputLayout tilEmail) {
        tilEmail.setError("Formato de datos no válido");
    }

    private void setEmptyError(TextInputLayout textInputLayout){
        textInputLayout.setError("No se admiten vampos vacíos");
    }

    private void addNewContact(String name, String email, String phone){
        if(ContactRepository.getInstance(getActivity()).addNew(name, email, phone)) {
            callback.listContacts();
            Toast.makeText(getContext(), "Agregado con éxito: " + name, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getContext(), "El contacto ya existe", Toast.LENGTH_SHORT).show();
    }
}
