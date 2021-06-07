package com.example.build_courses.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.build_courses.Activity.MuseumInfoActivity;
import com.example.build_courses.Adapters.MuseumsAdapter;
import com.example.build_courses.Models.MuseumModel;
import com.example.build_courses.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MuseumsFragment extends Fragment {

    RecyclerView museumsRecyclerView;
    MuseumsAdapter museumsAdapter;
    DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_museums, container, false);

        init(root);
        initializeAdapter();
        initializeRecyclerView();

        return root;
    }

    // Инициализация
    private void init(View root){
        progressDialog = new ProgressDialog(getContext());
        museumsRecyclerView = root.findViewById(R.id.museums_recycler_view);
    }


    // Инициализируем адптер для RecyclerView
    private void initializeAdapter() {
        // Показываем окно загрузки
        progressDialog.setMessage("Загрузка...");
        progressDialog.show();

        // Массив для данных
        ArrayList<MuseumModel> museums = new ArrayList<>();
        museumsAdapter = new MuseumsAdapter(new ArrayList<>());

        databaseReference = FirebaseDatabase.getInstance().getReference("Museums");

        //записываем данные в массив из БД

        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    MuseumModel DB = ds.getValue(MuseumModel.class);
                    if (DB != null) {
                        museums.add(DB);
                        museumsAdapter.setDataSet(museums);

                        museumsAdapter.setMuseumsOnClickListener(new MuseumsAdapter.MuseumsOnClickListener() {
                            // При нажатии на адресс будут открываться Google-карты
                            @Override
                            public void MuseumOnAddressClick(View view, String museumID) {
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                        Uri.parse(DB.getUrl_address()));
                                startActivity(intent);
                            }
                            // При нажатии на "Подробнее" будет открываться новая активность
                            @Override
                            public void MuseumsOnExtrasClick(View view, String museumID) {
                                Intent intent = new Intent(getContext(), MuseumInfoActivity.class);
                                intent.putExtra("museumID", museumID);
                                startActivity(intent);
                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Ошибка", Toast.LENGTH_SHORT).show();
            }
        };
        // Закрываем окно загрузки
        hideProgress(progressDialog);

        databaseReference.addValueEventListener(vListener);
    }


    // Функция, которая закрывает ProgressDialog
    public void hideProgress(ProgressDialog mProgressDialog) {
        if(mProgressDialog != null) {
            if(mProgressDialog.isShowing()) { //check if dialog is showing.

                //get the Context object that was used to great the dialog
                Context context = ((ContextWrapper)mProgressDialog.getContext()).getBaseContext();

                //if the Context used here was an activity AND it hasn't been finished or destroyed
                //then dismiss it
                if(context instanceof Activity) {
                    if(!((Activity)context).isFinishing() && !((Activity)context).isDestroyed())
                        mProgressDialog.dismiss();
                } else //if the Context used wasn't an Activity, then dismiss it too
                    mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }
    }


    // Инициализация RecyclerView
    private void initializeRecyclerView() {
        museumsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        museumsRecyclerView.setAdapter(museumsAdapter);
    }
}