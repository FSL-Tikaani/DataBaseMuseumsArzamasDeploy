package com.example.build_courses.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.build_courses.Activity.TestActivity;
import com.example.build_courses.R;


public class QrFragment extends Fragment {

    Button btn_go_Test;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_qr, container, false);
        // Инициализация
        btn_go_Test = root.findViewById(R.id.btn_goTest);
        // OnClick
        btn_go_Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), TestActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}
