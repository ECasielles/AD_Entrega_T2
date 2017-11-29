package com.mercacortex.ad_entrega_t2.ui.agenda;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercacortex.ad_entrega_t2.R;

import java.util.List;

/**
 * Created by icenri on 11/28/17.
 */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private List<Contact> contacts;

    public ContactAdapter(Activity activity) {
        contacts = ContactRepository.getInstance(activity);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        holder.txvName.setText(contacts.get(position).getName());
        holder.txvEmail.setText(contacts.get(position).getEmail());
        holder.txvPhone.setText(contacts.get(position).getPhone());
    }

    @Override
    public int getItemCount() {
        return contacts != null ? contacts.size() : -1;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView txvName, txvEmail, txvPhone;

        public ContactViewHolder(View itemView) {
            super(itemView);
            txvName = (TextView) itemView.findViewById(R.id.txvName);
            txvEmail = (TextView) itemView.findViewById(R.id.txvEmail);
            txvPhone = (TextView) itemView.findViewById(R.id.txvPhone);
        }
    }
}
