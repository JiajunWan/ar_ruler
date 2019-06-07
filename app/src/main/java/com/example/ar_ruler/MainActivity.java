package com.example.ar_ruler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.NodeParent;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment.OnTapArPlaneListener;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {
    private ArrayList dataArray;
    private ArrayList lineNodeArray;
    private ArrayList sphereNodeArray;
    private ArrayList startNodeArray;
    private ArrayList endNodeArray;
    private AnchorNode startNode;
    private HashMap _$_findViewCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Full screen */
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        this.initView();
    }

    private void initView() {
        final ImageView UI_Last = findViewById(R.id.UI_Last);
        UI_Last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                ArSceneView arSceneView;
                switch(dataArray.size()) {
                    case 0:
                        ToastUtils.showLong("No Record");
                        break;
                    case 1:
                        dataArray.clear();
                        lineNodeArray.clear();
                        sphereNodeArray.clear();
                        startNodeArray.clear();
                        endNodeArray.clear();
                        fragment = getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
                        if (fragment == null) {
                            throw new ClassCastException("null cannot be cast to non-null type ArFragment");
                        }
                        arSceneView = ((ArFragment)fragment).getArSceneView();
                        arSceneView.getScene().removeChild(startNode);
                        break;
                    default:
                        dataArray.remove(dataArray.size() - 1);
                        int index = startNodeArray.size() - 1;
                        ((Node)startNodeArray.get(index)).removeChild((Node)lineNodeArray.remove(index));
                        ((Node)endNodeArray.get(index)).removeChild((Node)sphereNodeArray.remove(index + 1));
                        fragment = getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
                        if (fragment == null) {
                            throw new ClassCastException("null cannot be cast to non-null type ArFragment");
                        }
                        arSceneView = ((ArFragment)fragment).getArSceneView();
                        arSceneView.getScene().removeChild((Node)startNodeArray.remove(index));
                        fragment = getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
                        if (fragment == null) {
                            throw new ClassCastException("null cannot be cast to non-null type ArFragment");
                        }
                        arSceneView = ((ArFragment)fragment).getArSceneView();
                        arSceneView.getScene().removeChild((Node)endNodeArray.remove(index));
                }
            }
        });

        final ImageView UI_Post = findViewById(R.id.UI_Post);
        UI_Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataArray.size() < 4) {
                    ToastUtils.showLong("At least four points");
                }
                else {
                    ArrayList tempJsonArray = new ArrayList<Double>();
                    Iterable dataArray = MainActivity.this.dataArray;
                    int i = 0;

                    for (Object object : dataArray) {
                        int i1 = i++;
                        if (i1 < 0) {
                            throw new ArrayIndexOutOfBoundsException("Index overflow");
                        }

                        AnchorInfoBean anchorInfoBean = (AnchorInfoBean) object;
                        if (i1 == MainActivity.this.dataArray.size() - 1) {
                            Pose startPose = ((AnchorInfoBean) MainActivity.this.dataArray.get(0)).getAnchor().getPose();
                            Pose endPose = anchorInfoBean.getAnchor().getPose();
                            float dx = startPose.tx() - endPose.tx();
                            float dy = startPose.ty() - endPose.ty();
                            float dz = startPose.tz() - endPose.tz();
                            if (Math.sqrt((double) (dx * dx + dy * dy + dz * dz)) > (double) 1) {
                                AnchorNode node = new AnchorNode(anchorInfoBean.getAnchor());
                                tempJsonArray.add(node.getWorldPosition().x);
                                tempJsonArray.add(node.getWorldPosition().z);
                            }
                        } else {
                            AnchorNode nodex = new AnchorNode(anchorInfoBean.getAnchor());
                            tempJsonArray.add(nodex.getWorldPosition().x);
                            tempJsonArray.add(nodex.getWorldPosition().z);
                        }
                    }

                    Intent var19 = new Intent();
                    var19.setClass((Context)MainActivity.this, SecondActivity.class);
                    var19.putExtra("url", );
                    ActivityUtils.startActivity(var19);
                }
            }
        });
    }
}
