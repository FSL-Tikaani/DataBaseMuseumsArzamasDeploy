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

import com.example.build_courses.Adapters.GalleryAdapter;
import com.example.build_courses.MainActivity;
import com.example.build_courses.Models.PlacesModel;
import com.example.build_courses.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PlacesInfoActivity extends AppCompatActivity {

    TextView tv_name, tv_description, tv_address;
    RecyclerView recyclerView_gallery;
    GalleryAdapter galleryAdapter;
    String url_address, placesID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_info);

        init();
        getDataFromDatabase();
    }


    // Устанавливаем галлерею
    private void installGallery(PlacesModel model){
        // Массив
        ArrayList<String> images = model.getArray_urls_gallery_places();
        //Инициализация
        recyclerView_gallery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
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
        recyclerView_gallery.setAdapter(galleryAdapter);
    }




    // Получаем данные с Firebase
    private void getDataFromDatabase() {
        placesID = getIntent().getStringExtra("placesID");
        PlacesModel.getPlacesQuery(placesID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PlacesModel place = ds.getValue(PlacesModel.class);
                    //Передаём данные
                    setDataInTextView(place);
                }
            }
            // Ошибка
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Инициализация
    private void init(){
        setTitle("Достопримечательность");
        // Получаем ID места
        placesID = getIntent().getStringExtra("placesID");
        // TextView
        tv_name  = findViewById(R.id.tv_places_name);
        tv_description = findViewById(R.id.tv_places_description);
        tv_address = findViewById(R.id.tv_places_address);
        // Gallery
        recyclerView_gallery = findViewById(R.id.recyclerView_excursions);
    }

    //Устанавливаем данные в пустые TextView
    private void setDataInTextView(PlacesModel placeModel){
        tv_name.setText(placeModel.getName_places());
        tv_description.setText(placeModel.getDescription_places());
        tv_address.setText(placeModel.getAddress_places());
        // Устанвливаем галерею
        installGallery(placeModel);

        url_address = placeModel.getUrl_address_places();
    }

    // Открытие GoogleMaps
    public void openGoogleMapsOnClick(View view){
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(url_address));
        startActivity(intent);
    }

    // Колхоз, по другому не роботатет    :(
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}