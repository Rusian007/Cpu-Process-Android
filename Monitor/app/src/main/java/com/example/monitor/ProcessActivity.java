package com.example.monitor;

import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessActivity extends AppCompatActivity {
    private TextView text,text3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_main);

        text = findViewById(R.id.textView);
        text3 = findViewById(R.id.textView3);

        try {
            Map<String, String> outputmap = getCPUInfo();
            outputmap.forEach((key, value) -> text3.append(key+ "= "+value+"\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }


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
