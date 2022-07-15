package com.example.monitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    Button button;
    double percentAvail;
    double availableMegs;
    double totalMemory;

    TextView textView ;
    TextView cpuText ;
    TextView RamText ;
    TextView RamCountText;
    TextView CpuCountText;
    TextView RamUsageText;
    TextView RamUsageCircularText, CpuUsageCircularText;
    private TextView text2, text4, text5;
    private ProgressBar RamprogressBar;
    private ProgressBar ProgressBarCircularRAM , progressBarCircularCPU;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

      //  button = (Button) findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        cpuText = findViewById(R.id.cpu);
        RamText = findViewById(R.id.ram);
        RamUsageText = findViewById(R.id.ramUsage);
        RamCountText = findViewById(R.id.ramCount);
        CpuCountText = findViewById(R.id.cpuCount);
        RamprogressBar = findViewById(R.id.progressBar);
        CpuUsageCircularText = findViewById(R.id.text_view_progress_cpu);
        text2 = findViewById(R.id.textView2);
        text4 = findViewById(R.id.textView4);
        text5 = findViewById(R.id.textView5);
        RamUsageCircularText = findViewById(R.id.text_view_progress);
        ProgressBarCircularRAM = findViewById(R.id.progress_bar_circular);
        progressBarCircularCPU = findViewById(R.id.progress_bar_circular_cpu);
        CpuCountText.setText("");

       Typeface customfontBold = Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Bold.ttf");
        Typeface customfont = Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Regular.ttf");
        Typeface customLight = Typeface.createFromAsset(getAssets(),"fonts/TitilliumWeb-Light.ttf");

        textView.setTypeface(customfontBold);
        cpuText.setTypeface(customfont);
        RamText.setTypeface(customfont);
        CpuCountText.setTypeface(customfontBold);
        RamCountText.setTypeface(customfontBold);
        RamUsageCircularText.setTypeface(customLight);
        text2.setTypeface(customfont);
        text4.setTypeface(customfont);
        text4.setTypeface(customfont);
        RamUsageText.setTypeface(customLight);
        CpuUsageCircularText.setTypeface(customLight);

        int cores = getNumCores();
        getMeminfo();
        totalMemory = totalMemory / 1000;
        totalMemory = Math.round(totalMemory * 100);
        totalMemory = totalMemory/100;

        RamCountText.setText(String.valueOf(totalMemory + " GB"));
        CpuCountText.setText(String.valueOf(cores));

        // Handler ** Do not touch **
        final Handler h = new Handler();
        h.post(new Runnable() {
            @Override
            public void run() {
                double num = readUsage();
               // textView.setText(String.valueOf(num));
                getMeminfo();
                RamUsageText.setText(String.valueOf(availableMegs+"MB" + " / " + totalMemory+"MB"));
                RamprogressBar.setProgress( (int)percentAvail);
                ProgressBarCircularRAM.setProgress((int)percentAvail);
                progressBarCircularCPU.setProgress(50); //Change later

                percentAvail = Math.round(percentAvail * 100);
                percentAvail = percentAvail/100;
                RamUsageCircularText.setText(String.valueOf(percentAvail+"%"));
                CpuUsageCircularText.setText("50%"); //Will change Later

                h.postDelayed(this, 2000);
            }
        });
        // Handler finish here

        // Button on click "see Process" button
       /* button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Button Click","Clicked");
                openNewActivity();
            }
        }); */

    }


    public void openNewActivity(){
        Intent intent = new Intent(this, ProcessActivity.class);
        startActivity(intent);
    }

    // All functions to get ram and cpu
    private void getMeminfo(){
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        availableMegs = mi.availMem / 0x100000L;
        totalMemory= mi.totalMem/ 0x100000L;

    //Percentage can be calculated for API 16+
        percentAvail = mi.availMem / (double)mi.totalMem * 100.0;
    }

    private int getNumCores() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by one or more digits
                if(Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch(Exception e) {
            //Default to return 1 core
            System.out.println("Error Occured !!!!!!!!!!!!!");
            return 1;
        }
    }

    private double readUsage() {

        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();

            String[] toks = load.split(" +");  // Split on one or more spaces

            long idle1 = Long.parseLong(toks[4]);


            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8] );


            try {
                Thread.sleep(360);
            } catch (Exception e) {}

            reader.seek(0);
            load = reader.readLine();
            reader.close();

            toks = load.split(" +");

            long idle2 = Long.parseLong(toks[4] );
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8] );

            return (double)(cpu2 - cpu1 ) / ((cpu2 + idle2 ) - (cpu1 + idle1 ));

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return (GetUsage() * 100 ); // Usage in percentage
    }



    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }



    private double GetUsage(){
        return Math.random();
    }

}