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
import com.example.build_courses.Models.ExcursionModel;
import com.example.build_courses.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExcursionInfoActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    RecyclerView gallery_excursion;
    GalleryAdapter galleryAdapter;
    TextView tv_name, tv_them, tv_time, tv_description;
    String email_museum, name_excursion, phone_number, excursionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excursion_info);

        init();
        getDataFromDataBase();
    }

    // Получаем данные с Firebase
    private void getDataFromDataBase() {
        ExcursionModel.getExcursionQuery(excursionID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    ExcursionModel excursion = ds.getValue(ExcursionModel.class);
                    if(excursion != null){
                        // Подставляем данные
                        setData(excursion);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Метод, который инициализирует галерею
    private void installGallery(ExcursionModel model){
        // Массив с сслыками на изображания
        ArrayList<String> array_urls = model.getArray_urls_gallery();
        // Инициализация
        gallery_excursion.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        galleryAdapter = new GalleryAdapter(array_urls, GalleryAdapter.MODE_HORIZONTAL);
        // OnClick
        galleryAdapter.setOnImageClickListener(new GalleryAdapter.OnImageClickListener() {
            @Override
            public void onClick(View v, String url) {
                Intent intent = new Intent(getApplicationContext(), ImageViewerActivity.class);
                intent.putExtra("imageURL", url);
                startActivity(intent);
            }
        });
        // Устанавливаем адаптер
        gallery_excursion.setAdapter(galleryAdapter);
    }

    // Установка данных в пустые TextView
    private void setData(ExcursionModel excursionModel){
        tv_name.setText(excursionModel.getName());
        tv_them.setText(excursionModel.getThem());
        tv_time.setText(excursionModel.getTime());
        tv_description.setText(excursionModel.getDescription());
        phone_number = excursionModel.getPhone_number();
        // Передаём данные в метод, который заполняет галерею изображениями
        installGallery(excursionModel);

        email_museum = excursionModel.getEmail_museum();
        name_excursion = excursionModel.getName();
    }

    // Инициализация
    private void init() {
        setTitle("Экскурсия");
        // Получаем ID экскурсии, что бы получить нужные данные
        excursionID = getIntent().getStringExtra("ExcursionID");
        // TextView
        tv_name = findViewById(R.id.tv_name_excursion);
        tv_them = findViewById(R.id.tv_them);
        tv_time = findViewById(R.id.tv_time);
        tv_description = findViewById(R.id.tv_description);
        // Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Excursions");
        // Gallery
        gallery_excursion = findViewById(R.id.gallery_excursions);
    }

    // При нажатии на кнопку, переносит на новую активность
    public void go_excursionOnClick(View view){
        Intent intent = new Intent(this, FormExcursionActivity.class);
        intent.putExtra("email_museum", email_museum);
        intent.putExtra("name_excursion", name_excursion);
        startActivity(intent);
    }
}