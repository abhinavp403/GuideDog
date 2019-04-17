package com.example.abhinav.hackholyokeapp;

import android.content.Intent;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private Camera mCamera = null;
    private CameraView mCameraView = null;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout rlayout = (LinearLayout) findViewById(R.id.mainlayout);

        try {
            mCamera = Camera.open();//you can use open(int) to use different cameras
        } catch (Exception e) {
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());
        }

        if (mCamera != null) {
            mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
            FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
            camera_view.addView(mCameraView);//add the SurfaceView to the layout
        }
        //btn to close the application
        ImageButton imgClose = (ImageButton) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });

        rlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Log.d("ONCLICK: ", "Entered OnClick");
                mCamera.takePicture(null, null, mPicture);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                byte[] image = stream.toByteArray();
                //Log.d("IMAGE: ", image.toString());
            }

            Camera.PictureCallback mPicture = new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Log.d("Image Taken:", data.toString());
                    Intent intent = new Intent(MainActivity.this, RecognizeConceptsActivity.class);
                    intent.putExtra("photo", data);
                    startActivity(intent);
                    finish();
                }
            };
        });
    }
}