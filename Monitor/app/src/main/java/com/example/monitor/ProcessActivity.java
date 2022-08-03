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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessActivity extends AppCompatActivity {
    private TextView totalInternalSizeText,
            AvailableInternalSizeText,
            text,
            text3,
            text6,
            InternalStgText;
    ImageButton backBtn;

    private static final String TAG = "ProcessActivity";
    private ArrayList<IndividualArray> theNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_main);

        Typeface customfontBold = Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Bold.ttf");
        Typeface customfont = Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Regular.ttf");
        Typeface customLight = Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Light.ttf");

       text3 = findViewById(R.id.textView3);
        text6 = findViewById(R.id.textView6);
      //  text = findViewById(R.id.textView7);
        backBtn = (ImageButton) findViewById(R.id.imageButton);
        InternalStgText = findViewById(R.id.InternalStorage);
        totalInternalSizeText = findViewById(R.id.totalInternalSize);
        AvailableInternalSizeText = findViewById(R.id.AvailableInternalSize);

        InternalStgText.setTypeface(customfontBold);
        text3.setTypeface(customLight);
        text6.setTypeface(customLight);
        totalInternalSizeText.setTypeface(customfont);
        AvailableInternalSizeText.setTypeface(customfont);

        long totalInternal, AvailableInternal, totalExternal, AvailableExternal;
        totalInternal = TotalInternalStorages();
        AvailableInternal = AvailableInternalStorages();


        String totalInternalResult = checkMB_GB(totalInternal);
        String availableInternalResult = checkMB_GB(AvailableInternal);

        totalInternalSizeText.setText(totalInternalResult);
        AvailableInternalSizeText.setText(availableInternalResult);

        InitializeNamesArray();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Button Click","Clicked");
                openNewActivity();
            }
        });

      /*  try {
            Map<String, String> outputmap = getCPUInfo();
            outputmap.forEach((key, value) -> text.append(key+ "= "+value+"\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    */


    }
    private void InitializeNamesArray(){
        try {
            Map<String, String> outputmap = getCPUInfo();
            outputmap.forEach((key, value) -> theNames.add(new IndividualArray(key, value)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        initRecycleView();
    }
    private void initRecycleView(){
        Log.d(TAG, "Recycler initialized :)");
        RecyclerView recyclerView = findViewById(R.id.recycleView);
        RecycleViewAdapter adapter = new RecycleViewAdapter(this, theNames);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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


}
