package com.example.ar_ruler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class SecondActivity extends AppCompatActivity {
    //    DrawView drawView;
    ArrayList<Float> xylist;
    Paint paint;
    Canvas canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        xylist = new ArrayList<>();

        final Intent intent = getIntent();
        xylist = (ArrayList<Float>) intent.getSerializableExtra("xylist");

        final ImageView profileImage = this.findViewById(R.id.profile_image);

        paint = new Paint();
        profileImage.post(() -> {
            //height is ready
            int profileImageWidth = profileImage.getWidth();
            int profileImageHeight = profileImage.getHeight();
            Bitmap tempBitmap;
            tempBitmap = Bitmap.createBitmap(profileImageWidth, profileImageHeight, Bitmap.Config.ARGB_8888);
            ArrayList<Float> xlist = new ArrayList<>();
            ArrayList<Float> ylist = new ArrayList<>();
            for (int i = 0; i < xylist.size(); i = i + 2) {
                xlist.add(xylist.get(i));
            }
            for (int i = 1; i < xylist.size(); i = i + 2) {
                ylist.add(xylist.get(i));
            }
            int minx = Math.round(Collections.min(xlist));
            int maxx = Math.round(Collections.max(xlist));
            int miny = Math.round(Collections.min(ylist));
            int maxy = Math.round(Collections.max(ylist));
            int max_xlen = maxx - minx;
            int max_ylen = maxy - miny;
            float ratio_x = (float) (profileImage.getWidth() * 0.6) / max_xlen;
            float ratio_y = (float) (profileImage.getHeight() * 0.6) / max_ylen;
            float ratio = Math.min(ratio_x, ratio_y);
            for (int i = 0; i < xlist.size(); i++) {
                xlist.set(i, (xlist.get(i) - minx - (float) max_xlen / 2) * ratio + profileImage.getWidth() * 0.5f);
            }
            for (int i = 0; i < ylist.size(); i++) {
                ylist.set(i, (ylist.get(i) - miny - (float) max_ylen / 2) * ratio + profileImage.getHeight() * 0.5f);
            }
            int point_num = Math.min(xlist.size(), ylist.size());
            canvas = new Canvas(tempBitmap);
            paint.setStrokeWidth(5);
            paint.setColor(Color.BLACK);
            for (int i = 0; i < point_num - 1; i++) {
                canvas.drawLine(xlist.get(i), ylist.get(i), xlist.get(i + 1), ylist.get(i + 1), paint);
            }
            profileImage.setImageBitmap(tempBitmap);
            profileImage.setOnClickListener(v -> finish());
        });
    }
}