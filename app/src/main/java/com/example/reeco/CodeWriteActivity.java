package com.example.reeco;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class CodeWriteActivity extends AppCompatActivity {
    EditText edtCodeWrite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 다크모드 무효화
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_code_compile);

        Button btnOpenFile = findViewById(R.id.btn_openFile);
        Button btnSaveFile = findViewById(R.id.btn_saveFile);
        Button btnCompile = findViewById(R.id.btn_compile);
        edtCodeWrite = findViewById(R.id.edt_codeWrite);

        btnCompile.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CompileResultActivity.class);

            startActivity(intent);
        });

        btnOpenFile.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/*");
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 1 || resultCode != Activity.RESULT_OK) {
            return;
        }
        if (data == null) {
            return;
        }

        Uri uri = data.getData();

        try {
            edtCodeWrite.setText(readTextFromUri(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream =
                     getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }
}
