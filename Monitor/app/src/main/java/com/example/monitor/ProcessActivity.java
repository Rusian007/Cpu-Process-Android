package com.example.monitor;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
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
    private TextView totalInternalSizeText,
            AvailableInternalSizeText,
            totalExternalSizeText,
            AvailableExternalText,
            text,
            text3,
            text6,
            text7,
            text8,
            InternalStgText,
            ExternalStgText;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_main);

        Typeface customfontBold = Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Bold.ttf");
        Typeface customfont = Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Regular.ttf");
        Typeface customLight = Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Light.ttf");

       text3 = findViewById(R.id.textView3);
        text6 = findViewById(R.id.textView6);
        text7 = findViewById(R.id.textView7);
        text8 = findViewById(R.id.textView8);
        backBtn = (ImageButton) findViewById(R.id.imageButton);
        InternalStgText = findViewById(R.id.InternalStorage);
        ExternalStgText = findViewById(R.id.ExternalStorage);
        totalInternalSizeText = findViewById(R.id.totalInternalSize);
        AvailableInternalSizeText = findViewById(R.id.AvailableInternalSize);
        totalExternalSizeText = findViewById(R.id.totalExternalSize);
        AvailableExternalText = findViewById(R.id.AvailableExternalSize);

        InternalStgText.setTypeface(customfontBold);
        ExternalStgText.setTypeface(customfontBold);
        text3.setTypeface(customLight);
        text6.setTypeface(customLight);
        text7.setTypeface(customLight);
        text8.setTypeface(customLight);
        totalInternalSizeText.setTypeface(customfont);
        totalExternalSizeText.setTypeface(customfont);
        AvailableInternalSizeText.setTypeface(customfont);
        AvailableExternalText.setTypeface(customfont);

        long totalInternal, AvailableInternal, totalExternal, AvailableExternal;
        totalInternal = TotalInternalStorages();
        AvailableInternal = AvailableInternalStorages();

        totalExternal = TotalExternalStorages();
        AvailableExternal = AvailableExternalStorages();

        String totalInternalResult = checkMB_GB(totalInternal);
        String availableInternalResult = checkMB_GB(AvailableInternal);
        String totalExternalResult = checkMB_GB(totalExternal);
        String availableExternalResult = checkMB_GB(AvailableExternal);

        totalInternalSizeText.setText(totalInternalResult);
        totalExternalSizeText.setText(totalExternalResult);

        AvailableInternalSizeText.setText(availableInternalResult);
        AvailableExternalText.setText(availableExternalResult);

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

    private String checkMB_GB(long size) {
        if(size >1000){
            size = size / 1000;
            return String.valueOf(size)+" GB";
        }
        else return String.valueOf(size+" MB");
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

    private long AvailableInternalStorages(){
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long bytesAvailable = (long)stat.getFreeBlocks() * (long)stat.getBlockSize();
        long megAvailable = bytesAvailable / 1048576;
        return megAvailable;
    }
    private long TotalInternalStorages(){
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();
        long megAvailable = bytesAvailable / 1048576;
        return megAvailable;
    }
    private long AvailableExternalStorages(){
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getFreeBlocks() * (long)stat.getBlockSize();
        long megAvailable = bytesAvailable / 1048576;
        return megAvailable;
    }
    private long TotalExternalStorages(){
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();
        long megAvailable = bytesAvailable / 1048576;
        return megAvailable;
    }


}
