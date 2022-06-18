package com.example.monitor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    Button button;
    double percentAvail;
    double availableMegs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);

        int cores = getNumCores();
        getMeminfo();

    // Handler ** Do not touch **
        final Handler h = new Handler();
        h.post(new Runnable() {
            @Override
            public void run() {

                getMeminfo();
                h.postDelayed(this, 2000);
            }
        });
        // Handler finish here

        // Button on click "see Process" button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Button Click","Clicked");
                openNewActivity();
            }
        });
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
}