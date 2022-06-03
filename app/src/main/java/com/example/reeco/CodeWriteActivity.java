package com.example.reeco;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.amrdeveloper.codeview.CodeView;
import com.example.reeco.syntax.LanguageManager;
import com.example.reeco.syntax.LanguageName;
import com.example.reeco.syntax.ThemeName;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Objects;

public class CodeWriteActivity extends AppCompatActivity {
    CodeView edtCodeWrite;
    Uri uri;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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

        Intent receivedIntent = getIntent();

        String ip = receivedIntent.getStringExtra("ip");
        int port = receivedIntent.getIntExtra("port", 0);
        String user = receivedIntent.getStringExtra("user");
        String password = receivedIntent.getStringExtra("password");

        btnCompile.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CompileResultActivity.class);

            intent.putExtra("ip", ip);
            intent.putExtra("port", port);
            intent.putExtra("user", user);
            intent.putExtra("password", password);

            startActivity(intent);
        });

        btnOpenFile.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/*");
            //noinspection deprecation
            startActivityForResult(intent, 1);
        });

        // virtual device에서는 제대로 저장이 되지 않는 경우가 존재하나,
        // 실제 디바이스에서 정상적으로 작동하는 것으로 확인됨.
        btnSaveFile.setOnClickListener(v -> {
            if (uri == null) {
                return;
            }

            try {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        Objects.requireNonNull(outputStream)));
                writer.write(edtCodeWrite.getText().toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        uri = data.getData();

        LanguageManager langManager = new LanguageManager(this, edtCodeWrite);

        String[] split = uri.getPath().split("[.]");
        String fileExt = split[split.length - 1];

        switch (fileExt) {
            case "java":
                langManager.applyTheme(LanguageName.JAVA, ThemeName.WHITE);
                break;
            case "py":
                langManager.applyTheme(LanguageName.PYTHON, ThemeName.WHITE);
                break;
            case "go":
                langManager.applyTheme(LanguageName.GO_LANG, ThemeName.WHITE);
                break;
        }

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
