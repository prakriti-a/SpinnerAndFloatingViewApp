package com.prakriti.spinnerapp;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class FloatingViewService extends Service implements View.OnClickListener, View.OnTouchListener { // android.app package
// Service must be public & declared in Manifest file
    // here, we inflate the float_view.xml file

    private View floatingView;
    private WindowManager windowManager;

    private View collapsedView, expandedView, rootView;
    private WindowManager.LayoutParams windowManagerParams;

    int startXPos, startYPos;
    float startTouchX, startTouchY;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { // abstract method
        // returns communication channel to the service - a binder thru which clients can call service
        return null;
    }

    @Override
    public void onCreate() { // init UI in Service class, called when service is created
        super.onCreate();
        floatingView = LayoutInflater.from(this).inflate(R.layout.float_view_layout, null);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManagerParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, // width
                WindowManager.LayoutParams.WRAP_CONTENT, // height
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // type of Window Manager for Android Oreo> (below - TYPE_PHONE)
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, // flags for view
                PixelFormat.TRANSLUCENT); // format of window mgr
        windowManagerParams.gravity = Gravity.TOP | Gravity.START; // position of view
        windowManagerParams.x = 200; // offset from top left corner position
        windowManagerParams.y = 200;

        windowManager.addView(floatingView, windowManagerParams);

        collapsedView = floatingView.findViewById(R.id.collapsedView);
        floatingView.findViewById(R.id.collapsedCloseButton).setOnClickListener(this);
        floatingView.findViewById(R.id.collapsedImageView).setOnClickListener(this);

        expandedView = floatingView.findViewById(R.id.expandedView);
        floatingView.findViewById(R.id.imgLion).setOnClickListener(this);
        floatingView.findViewById(R.id.imgLeopard).setOnClickListener(this);
        floatingView.findViewById(R.id.imgPreviousButton).setOnClickListener(this);
        floatingView.findViewById(R.id.imgNextButton).setOnClickListener(this);
        floatingView.findViewById(R.id.expandedCloseButton).setOnClickListener(this);
        floatingView.findViewById(R.id.expandedOpenButton).setOnClickListener(this);

        rootView = floatingView.findViewById(R.id.rootView); // for touchListener
        rootView.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.collapsedImageView:
                Toast.makeText(this, "Bird image tapped", Toast.LENGTH_SHORT).show();
                collapsedView.setVisibility(View.GONE);
                expandedView.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Expanded View opened", Toast.LENGTH_SHORT).show();
                break;

            case R.id.collapsedCloseButton:
                stopSelf(); // service will be stopped & view is removed from window mgr; method from service class
                Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
            break;

            case R.id.imgLion:
                Toast.makeText(this, "Lion image tapped", Toast.LENGTH_SHORT).show();
            break;

            case R.id.imgLeopard:
                Toast.makeText(this, "Leopard image tapped", Toast.LENGTH_SHORT).show();
                break;

            case R.id.imgPreviousButton:
                Toast.makeText(this, "Previous button tapped", Toast.LENGTH_SHORT).show();
            break;

            case R.id.imgNextButton:
                Toast.makeText(this, "Next button tapped", Toast.LENGTH_SHORT).show();
                break;

            case R.id.expandedCloseButton: // now only collapse view will be visible
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                Toast.makeText(this, "Collapsed View opened", Toast.LENGTH_SHORT).show();
                break;

            case R.id.expandedOpenButton: // open main activity
                Toast.makeText(this, "Opening App...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // to avoid 2 instances of main activity
                startActivity(intent);
                stopSelf();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // access positions
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startXPos = windowManagerParams.x; // access start pos of view on screen
                startYPos = windowManagerParams.y;
                startTouchX = event.getRawX(); // when user starts touching the view
                startTouchY = event.getRawY();
                return true;

            case MotionEvent.ACTION_UP: // user has lifted finger -- dragging is finished & pos has changed
                int startToEndXPosDiff = (int) (event.getRawX() - startTouchX); // current pos - start pos -> cast to int
                int startToEndYPosDiff = (int) (event.getRawY() - startTouchY);
                // to differentiate between clicking & dragging
                if(startToEndXPosDiff<5 && startToEndYPosDiff<5) {
                    if(collapsedView.getVisibility() == View.VISIBLE) {
                        collapsedView.setVisibility(View.GONE); // change view bcoz its interpreted as click not drag
                        expandedView.setVisibility(View.VISIBLE);
                    }
                }
                return true;

            case MotionEvent.ACTION_MOVE: // update pos of view on screen
                windowManagerParams.x = startXPos + (int) (event.getRawX() - startTouchX); // init pos + (curr pos - start touch pos)
                windowManagerParams.y = startYPos + (int) (event.getRawY() - startTouchY);
                windowManager.updateViewLayout(floatingView, windowManagerParams); // update coordinates
                return true;
        }
        return false;
    }

    // to prevent memory issues
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(floatingView != null) {
            windowManager.removeView(floatingView);
        }
    }

}
