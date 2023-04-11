package com.example.licentamobilebanking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {
    ImageButton imageButton;
    TextInputEditText tietTelefon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         imageButton=findViewById(R.id.addBtn);
        TextInputEditText tietTelefon=findViewById(R.id.pass_edit_text);


        tietTelefon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tietTelefon.setText("");
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilitatiSingleton.phoneNumber=tietTelefon.getText().toString();
                Intent intent=new Intent(MainActivity.this,otpverification.class);
                startActivity(intent);
            }
        });

    }
}