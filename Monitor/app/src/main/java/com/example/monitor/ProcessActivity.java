package com.example.monitor;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessActivity extends AppCompatActivity {
    private TextView text,text3,InternalStgText;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_main);

        Typeface customfontBold = Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Bold.ttf");

       // text = findViewById(R.id.textView);
        backBtn = (ImageButton) findViewById(R.id.imageButton);
        InternalStgText = findViewById(R.id.InternalStorage);
        InternalStgText.setTypeface(customfontBold);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Button Click","Clicked");
                openNewActivity();
            }
        });
    /*
        try {
            Map<String, String> outputmap = getCPUInfo();
            outputmap.forEach((key, value) -> text.append(key+ "= "+value+"\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        */



    }
    public void openNewActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    
    private List getNumOfProcesses(){
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        return procInfos;
    }

    public static Map<String, String> getCPUInfo () throws IOException {

        BufferedReader br = new BufferedReader (new FileReader("/proc/cpuinfo"));

        String str;

        Map<String, String> output = new HashMap<>();

        while ((str = br.readLine ()) != null) {

            String[] data = str.split (":");

            if (data.length > 1) {

                String key = data[0].trim ().replace (" ", "_");
                if (key.equals ("model_name")) key = "cpu_model";

                output.put (key, data[1].trim ());

            }

        }

        br.close ();

        return output;

    }


}
