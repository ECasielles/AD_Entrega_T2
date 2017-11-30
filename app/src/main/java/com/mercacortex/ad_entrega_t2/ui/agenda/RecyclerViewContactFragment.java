package com.mercacortex.ad_entrega_t2.ui.agenda;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercacortex.ad_entrega_t2.R;

/**
 *
 */
public class RecyclerViewContactFragment extends Fragment {

    public static final String TAG = "RecyclerViewContactFragment";
    private ListContactListener callback;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    ContactAdapter adapter;

    public interface ListContactListener {
        void addNewContact();
    }

    public RecyclerViewContactFragment() {
        setRetainInstance(true);
    }

    public static RecyclerViewContactFragment newInstance(Bundle arguments) {
        RecyclerViewContactFragment recyclerViewContactFragment = new RecyclerViewContactFragment();
        if(arguments != null) {
            recyclerViewContactFragment.setArguments(arguments);
        }
        return recyclerViewContactFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (ListContactListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() +
                    " must implement ListContactListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_contact, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
        adapter = new ContactAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.addNewContact();
            }
        });
    }

}
