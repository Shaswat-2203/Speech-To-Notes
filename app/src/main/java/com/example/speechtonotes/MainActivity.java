package com.example.speechtonotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private String str="",saveText;
    private static final int REQUEST_CODE=100;
    private Button mic;
    private Button save;
    private static final int WRITE_EXTERNAL_STORAGE_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=findViewById(R.id.textView);
        mic=findViewById(R.id.mic);
        save=findViewById(R.id.save);
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveText=textView.getText().toString().trim();
                if(saveText.isEmpty()){
                    Toast.makeText(MainActivity.this,"Nothing to save",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                            String[] permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permissions,WRITE_EXTERNAL_STORAGE_CODE);
                        }
                        else
                        {
                            saveToTxtFile(saveText);
                        }
                    }
                    else{
                        saveToTxtFile(saveText);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_CODE:{
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    saveToTxtFile(saveText);
                }
                else {
                    Toast.makeText(this,"Storage Permission is required",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveToTxtFile(String saveText) {
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(System.currentTimeMillis());
        try{
            File path= Environment.getExternalStorageDirectory();
            File dir=new File(path+"/My Files/");
            dir.mkdirs();
            String fileName="MyFile_"+timeStamp+".txt";
            File file=new File(dir,fileName);
            FileWriter fw=new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw=new BufferedWriter(fw);
            bw.write(saveText);
            bw.close();
            Toast.makeText(this,fileName+" is saved to\n"+dir,Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private void speak() {

        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"SPEAK OUT!");
        try{
            startActivityForResult(intent,REQUEST_CODE);
        }
        catch (Exception e){
            Toast.makeText(this,"Problem in recording speech",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_CODE:{
                if (resultCode==RESULT_OK&&null!=data){
                    ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    str+=" "+result.get(0)+".";
                    textView.setText(str);
                }
                break;
            }

        }
    }
}