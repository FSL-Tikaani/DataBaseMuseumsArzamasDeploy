package com.example.build_courses.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.build_courses.Adapters.ExcursionsAdapter;
import com.example.build_courses.Adapters.GalleryAdapter;
import com.example.build_courses.MainActivity;
import com.example.build_courses.Models.ExcursionModel;
import com.example.build_courses.Models.MuseumModel;
import com.example.build_courses.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MuseumInfoActivity extends AppCompatActivity {

    TextView tv_info_name_museum;
    TextView tv_info_description;
    TextView tv_info_contacts;
    TextView tv_info_address;
    TextView tv_info_work_time;

    RecyclerView gallery_view, excursionRecyclerView;
    GalleryAdapter galleryAdapter;
    DatabaseReference databaseReference;
    ExcursionsAdapter excursionsAdapter;
    String museumID;
    String url_maps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_info);

        init();
        getDataFromDataBase();
        initializeAdapter();
        initializeRecyclerView();

    }

    // Инициализация RecyclerView
    private void initializeRecyclerView() {
        excursionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        excursionRecyclerView.setAdapter(excursionsAdapter);
    }

    // Инициализация Адаптера для RecyclerView под экскурсии
    private void initializeAdapter() {
        // Массив
        ArrayList<ExcursionModel> excursions = new ArrayList<>();
        // Инициализация
        excursionsAdapter = new ExcursionsAdapter(new ArrayList<>());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds : snapshot.getChildren()){
                    ExcursionModel model = ds.getValue(ExcursionModel.class);
                    if(model != null){
                        excursions.add(model);
                        excursionsAdapter.setDataSetExcursion(excursions);
                        // OnClick
                        excursionsAdapter.setExcursionOnClickListener(new ExcursionsAdapter.ExcursionOnClickListener() {

                            // При нажатии на кнопку "Подробнее", переносим пользователя на другую активность
                            @Override
                            public void ExcursionOnExtrasClick(View view, String excursionID) {
                                Intent intent = new Intent(getApplicationContext(), ExcursionInfoActivity.class);
                                intent.putExtra("ExcursionID", excursionID);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            // Ошибка
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT).show();
            }
        };

        // Выбираем только подходящие экскурсии
        Query query = databaseReference.orderByChild("id_museum").equalTo(museumID);
        query.addValueEventListener(valueEventListener);

    }

    // Установка галереи
    private void setupGallery(MuseumModel museumModel) {
        // Массив
        ArrayList<String> images = museumModel.getArrayList();
        // Инициализация
        gallery_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        galleryAdapter = new GalleryAdapter(images, GalleryAdapter.MODE_HORIZONTAL);
        // OnClick
        galleryAdapter.setOnImageClickListener(new GalleryAdapter.OnImageClickListener() {
            @Override
            public void onClick(View v, String url) {
                Intent intent = new Intent(getApplicationContext(), ImageViewerActivity.class);
                intent.putExtra("imageURL", url);
                startActivity(intent);
            }
        });
        // Установка адаптера
        gallery_view.setAdapter(galleryAdapter);
    }





    // При нажатии на адрес, у пользователя будут открываться Google-карты
    public void onClickStartGoogleMaps(View view){
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(url_maps));
        startActivity(intent);
    }


    // Получаем данные с Firebase
    private void getDataFromDataBase(){
        MuseumModel.getMuseumQuery(museumID).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            MuseumModel museum = ds.getValue(MuseumModel.class);
                            //Передаём методу данные для заполнения
                            setDataInTextView(museum);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    //инициализация
    private void init(){
        setTitle("Музей");

        // Получаем ID музея
        museumID = getIntent().getStringExtra("museumID");
        // TextView
        tv_info_name_museum = findViewById(R.id.tv_info_name_museum);
        tv_info_description = findViewById(R.id.tv_info_museum_description);
        tv_info_contacts = findViewById(R.id.tv_info_museum_contacts);
        tv_info_address = findViewById(R.id.tv_info_museum_address);
        tv_info_work_time = findViewById(R.id.tv_info_museum_work_time);
        gallery_view = findViewById(R.id.gallery_museums);



        // RecyclerView
        excursionRecyclerView = findViewById(R.id.recyclerView_excursions);
        // FireBase
        databaseReference = FirebaseDatabase.getInstance().getReference("Excursions");
    }

    //  Заполняем данными пустые TextView
    private void setDataInTextView(MuseumModel museumModel){
        tv_info_name_museum.setText(museumModel.getName());
        tv_info_description.setText(museumModel.getDescription());
        tv_info_contacts.setText(museumModel.getContacts());
        tv_info_address.setText(museumModel.getAddress());
        tv_info_work_time.setText(museumModel.getWork_time());

        url_maps = museumModel.getUrl_address();
        // Передаём данные
        setupGallery(museumModel);

    }


    // Колхоз, по другому не роботатет    :(
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}