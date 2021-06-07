package com.example.build_courses.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.build_courses.Mail.JavaMailAPI;
import com.example.build_courses.R;

import java.util.Calendar;

public class FormExcursionActivity extends AppCompatActivity {

    String currentDateTime;
    EditText et_name_person, et_peoples, et_contact, et_date;
    String name, peoples, contact, date;
    String email_museum, message, excursion;
    Calendar dateAndTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_excursion);

        init();
        setInitialDateTime();
    }

    // Инициализация
    private void init(){
        setTitle("Записаться на экскурсию");
        et_name_person = findViewById(R.id.et_name_user);
        et_peoples = findViewById(R.id.et_kolvo_people);
        et_contact = findViewById(R.id.et_contact);
        et_date = findViewById(R.id.et_datetime);
        et_date.setText(date);
    }

    // Метод, который открывает окно с возможностью изменить дату
    public void onClickEditDate(View view){
        // отображаем диалоговое окно для выбора даты
        new DatePickerDialog(FormExcursionActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // Установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
        }
    };

    // Установка начальных даты и времени
    private void setInitialDateTime() {
        currentDateTime = (DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        et_date.setText(currentDateTime);
    }

    // Метод, который при нажатии на кнопку отправляет заявку на почту музея
    public void onClickWriteRequest(View view){

        name = et_name_person.getText().toString();
        peoples = et_peoples.getText().toString();
        contact = et_contact.getText().toString();

        email_museum = getIntent().getStringExtra("email_museum");
        excursion = getIntent().getStringExtra("name_excursion");
        // Формируем текст сообщения
        message = name + " хочет записаться на экскурсию " + excursion + "\n"
                  + "Дата: " + currentDateTime + "\n"
                  + "Количество человек:"  + peoples + "\n"
                  + "Контактные данные:" + contact;
        // Непосредственно отправляем письмо на почту
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, email_museum, "Заявка на экскурсию", message);
        javaMailAPI.execute();
    }
}