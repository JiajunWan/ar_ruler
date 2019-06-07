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
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.View;

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
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ArrayList<AnchorInfoBean> dataArray = new ArrayList<>();
    private ArrayList<Node> lineNodeArray = new ArrayList<>();
    private ArrayList<Node> sphereNodeArray = new ArrayList<>();
    private ArrayList<Node> startNodeArray = new ArrayList<>();
    private ArrayList<Node> endNodeArray = new ArrayList<>();
    private AnchorNode startNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.initView();
    }

    public void initView() {
        final ImageView UI_Last = findViewById(R.id.UI_Last);
        UI_Last.setOnClickListener(v -> {
            Fragment fragment;
            ArSceneView arSceneView;
            switch (dataArray.size()) {
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
                    arSceneView = ((ArFragment) fragment).getArSceneView();
                    arSceneView.getScene().removeChild(startNode);
                    break;
                default:
                    dataArray.remove(dataArray.size() - 1);
                    int index = startNodeArray.size() - 1;
                    (startNodeArray.get(index)).removeChild(lineNodeArray.remove(index));
                    (endNodeArray.get(index)).removeChild(sphereNodeArray.remove(index + 1));
                    fragment = getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
                    if (fragment == null) {
                        throw new ClassCastException("null cannot be cast to non-null type ArFragment");
                    }
                    arSceneView = ((ArFragment) fragment).getArSceneView();
                    arSceneView.getScene().removeChild(startNodeArray.remove(index));
                    fragment = getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
                    if (fragment == null) {
                        throw new ClassCastException("null cannot be cast to non-null type ArFragment");
                    }
                    arSceneView = ((ArFragment) fragment).getArSceneView();
                    arSceneView.getScene().removeChild(endNodeArray.remove(index));
            }
        });

        final ImageView UI_Post = findViewById(R.id.UI_Post);
        UI_Post.setOnClickListener(v -> {
            if (dataArray.size() < 4) {
                ToastUtils.showLong("At least four points");
            } else {
                ArrayList<Float> tempJsonArray = new ArrayList<>();
                Iterable dataArray = MainActivity.this.dataArray;
                int i = 0;

                for (Object object : dataArray) {
                    int i1 = i++;
                    if (i1 < 0) {
                        throw new ArrayIndexOutOfBoundsException("Index overflow");
                    }

                    AnchorInfoBean anchorInfoBean = (AnchorInfoBean) object;
                    if (i1 == MainActivity.this.dataArray.size() - 1) {
                        Pose startPose = (MainActivity.this.dataArray.get(0)).getAnchor().getPose();
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
                        AnchorNode node = new AnchorNode(anchorInfoBean.getAnchor());
                        tempJsonArray.add(node.getWorldPosition().x);
                        tempJsonArray.add(node.getWorldPosition().z);
                    }
                }

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SecondActivity.class);
                intent.putExtra("NodePositionArray", tempJsonArray);
                ActivityUtils.startActivity(intent);
            }
        });
        initAr();
    }

    public void initAr() {
        Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
        if (fragment == null) {
            throw new ClassCastException("null cannot be cast to non-null type ArFragment");
        } else {
            ((ArFragment) fragment).setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
                Anchor anchor = hitResult.createAnchor();
                AnchorInfoBean anchorInfoBean = new AnchorInfoBean("", anchor, 0.0);
                dataArray.add(anchorInfoBean);
                if (dataArray.size() > 1) {
                    Anchor endAnchor = (dataArray.get(dataArray.size() - 1)).getAnchor();
                    Anchor startAnchor = (dataArray.get(dataArray.size() - 2)).getAnchor();
                    Pose startPose = endAnchor.getPose();
                    Pose endPose = startAnchor.getPose();
                    float dx = startPose.tx() - endPose.tx();
                    float dy = startPose.ty() - endPose.ty();
                    float dz = startPose.tz() - endPose.tz();
                    anchorInfoBean.setLength(Math.sqrt((double) (dx * dx + dy * dy + dz * dz)));
                    drawLine(startAnchor, endAnchor, anchorInfoBean.getLength());
                } else {
                    startNode = new AnchorNode(hitResult.createAnchor());
                    AnchorNode anchorNode = startNode;
                    Fragment fragmentById = getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
                    if (fragmentById == null) {
                        throw new ClassCastException("null cannot be cast to non-null type ArFragment");
                    }

                    ArSceneView arSceneView = ((ArFragment) fragmentById).getArSceneView();
                    anchorNode.setParent(arSceneView.getScene());
                    MaterialFactory.makeOpaqueWithColor(MainActivity.this, new Color(0.33F, 0.87F, 0.0F))
                            .thenAccept(material -> {
                                ModelRenderable sphere = ShapeFactory.makeSphere(0.01F, Vector3.zero(), material);
                                Node node = new Node();
                                node.setParent(startNode);
                                node.setLocalPosition(Vector3.zero());
                                node.setRenderable(sphere);
                                sphereNodeArray.add(node);
                            });
                }
            });
        }
    }

    public void drawLine(Anchor firstAnchor, Anchor secondAnchor, final double length) {
        AnchorNode firstAnchorNode = new AnchorNode(firstAnchor);
        startNodeArray.add(firstAnchorNode);
        AnchorNode secondAnchorNode = new AnchorNode(secondAnchor);
        endNodeArray.add(secondAnchorNode);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
        if (fragment == null) {
            throw new ClassCastException("null cannot be cast to non-null type ArFragment");
        }

        ArSceneView arSceneView = ((ArFragment) fragment).getArSceneView();
        firstAnchorNode.setParent(arSceneView.getScene());
        secondAnchorNode.setParent(arSceneView.getScene());

        MaterialFactory.makeOpaqueWithColor(this, new Color(0.53F, 0.92F, 0.0F))
                .thenAccept(material -> {
                    ModelRenderable sphere = ShapeFactory.makeSphere(0.01F, Vector3.zero(), material);
                    Node node = new Node();
                    node.setParent(secondAnchorNode);
                    node.setLocalPosition(Vector3.zero());
                    node.setRenderable(sphere);
                    sphereNodeArray.add(node);
                });

        Vector3 firstWorldPosition = firstAnchorNode.getWorldPosition();
        Vector3 secondWorldPosition = secondAnchorNode.getWorldPosition();

        Vector3 difference = Vector3.subtract(firstWorldPosition, secondWorldPosition);
        Vector3 directionFromTopToBottom = difference.normalized();
        Quaternion rotationFromAToB = Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());

        MaterialFactory.makeOpaqueWithColor(this, new Color(0.33F, 0.87F, 0.0F))
                .thenAccept(material -> {
                    ModelRenderable lineMode = ShapeFactory.makeCube(
                            new Vector3(0.01F, 0.01F, difference.length()), Vector3.zero(), material);
                    Node lineNode = new Node();
                    lineNode.setParent(firstAnchorNode);
                    lineNode.setWorldPosition(Vector3.add(firstWorldPosition, secondWorldPosition).scaled(0.5F));
                    lineNode.setWorldRotation(rotationFromAToB);
                    lineNode.setRenderable(lineMode);
                    lineNodeArray.add(lineNode);

                    ViewRenderable.builder()
                            .setView(this, R.layout.renderable_text)
                            .build()
                            .thenAccept(r -> {
                                View view = r.getView();
                                if (view == null) {
                                    throw new ClassCastException("null cannot be cast to non-null type android.widget.TextView");
                                } else {
                                    TextView textView = (TextView) view;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    String var11 = String.format(Locale.US,"%.1f", length * (double) 100);
                                    textView.setText(stringBuilder.append(var11).append("CM").toString());
                                    r.setShadowCaster(false);
                                    FaceToCameraNode faceToCameraNode = new FaceToCameraNode();
                                    faceToCameraNode.setParent(lineNode);
                                    faceToCameraNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0.0F, 1.0F, 0.0F), 90.0F));
                                    faceToCameraNode.setLocalPosition(new Vector3(0.0F, 0.02F, 0.0F));
                                    faceToCameraNode.setRenderable(r);
                                }
                            });
                });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
        Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
        if (fragment == null) {
            throw new ClassCastException("null cannot be cast to non-null type com.gj.arcoredraw.MyArFragment");
        } else {
            fragment.onDestroy();
        }
    }
}
