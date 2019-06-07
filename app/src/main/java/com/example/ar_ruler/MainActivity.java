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
    private ArrayList dataArray = new ArrayList<AnchorInfoBean>();
    private ArrayList lineNodeArray = new ArrayList<Node>();
    private ArrayList sphereNodeArray = new ArrayList<Node>();
    private ArrayList startNodeArray = new ArrayList<Node>();
    private ArrayList endNodeArray = new ArrayList<Node>();
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
                        ((Node) startNodeArray.get(index)).removeChild((Node) lineNodeArray.remove(index));
                        ((Node) endNodeArray.get(index)).removeChild((Node) sphereNodeArray.remove(index + 1));
                        fragment = getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
                        if (fragment == null) {
                            throw new ClassCastException("null cannot be cast to non-null type ArFragment");
                        }
                        arSceneView = ((ArFragment) fragment).getArSceneView();
                        arSceneView.getScene().removeChild((Node) startNodeArray.remove(index));
                        fragment = getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
                        if (fragment == null) {
                            throw new ClassCastException("null cannot be cast to non-null type ArFragment");
                        }
                        arSceneView = ((ArFragment) fragment).getArSceneView();
                        arSceneView.getScene().removeChild((Node) endNodeArray.remove(index));
                }
            }
        });

        final ImageView UI_Post = findViewById(R.id.UI_Post);
        UI_Post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataArray.size() < 4) {
                    ToastUtils.showLong("At least four points");
                } else {
                    ArrayList tempJsonArray = new ArrayList();
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
                    var19.setClass((Context) MainActivity.this, SecondActivity.class);
                    var19.putExtra("url", );
                    ActivityUtils.startActivity(var19);
                }
            }
        });
        initAr();
    }

    private void initAr() {
        Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
        if (fragment == null) {
            throw new ClassCastException("null cannot be cast to non-null type ArFragment");
        } else {
            ((ArFragment) fragment).setOnTapArPlaneListener((OnTapArPlaneListener) (new OnTapArPlaneListener() {
                public final void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                    Anchor var10003 = hitResult.createAnchor();
                    AnchorInfoBean anchorInfoBean = new AnchorInfoBean("", var10003, 0.0D);
                    MainActivity.this.dataArray.add(anchorInfoBean);
                    if (dataArray.size() > 1) {
                        Anchor endAnchor = ((AnchorInfoBean) dataArray.get(dataArray.size() - 1)).getAnchor();
                        Anchor startAnchor = ((AnchorInfoBean) dataArray.get(dataArray.size() - 2)).getAnchor();
                        Pose startPose = endAnchor.getPose();
                        Pose endPose = startAnchor.getPose();
                        float dx = startPose.tx() - endPose.tx();
                        float dy = startPose.ty() - endPose.ty();
                        float dz = startPose.tz() - endPose.tz();
                        anchorInfoBean.setLength(Math.sqrt((double) (dx * dx + dy * dy + dz * dz)));
                        MainActivity.this.drawLine(startAnchor, endAnchor, anchorInfoBean.getLength());
                    } else {
                        MainActivity.this.startNode = new AnchorNode(hitResult.createAnchor());
                        AnchorNode var10000 = MainActivity.access$getStartNode$p(MainActivity.this);
                        Fragment var10001 = MainActivity.this.getSupportFragmentManager().findFragmentById(id.UI_ArSceneView);
                        if (var10001 == null) {
                            throw new ClassCastException("null cannot be cast to non-null type ArFragment");
                        }

                        ArSceneView var12 = ((ArFragment) var10001).getArSceneView();
                        var10000.setParent((NodeParent) var12.getScene());
                        MaterialFactory.makeOpaqueWithColor((Context) MainActivity.this, new Color(0.33F, 0.87F, 0.0F)).thenAccept((Consumer) (new Consumer() {
                            // $FF: synthetic method
                            // $FF: bridge method
                            public void accept(Object var1) {
                                this.accept((Material) var1);
                            }

                            public final void accept(Material material) {
                                ModelRenderable sphere = ShapeFactory.makeSphere(0.02F, Vector3.zero(), material);
                                ArrayList var10000 = MainActivity.this.sphereNodeArray;
                                Node var3 = new Node();
                                ArrayList var6 = var10000;
                                int var5 = false;
                                var3.setParent((NodeParent) MainActivity.access$getStartNode$p(MainActivity.this));
                                var3.setLocalPosition(Vector3.zero());
                                var3.setRenderable((Renderable) sphere);
                                var6.add(var3);
                            }
                        }));
                    }

                }
            }));
        }
    }

    private final void drawLine(Anchor firstAnchor, Anchor secondAnchor, final double length) {
        if (VERSION.SDK_INT >= 24) {
            final AnchorNode firstAnchorNode = new AnchorNode(firstAnchor);
            this.startNodeArray.add(firstAnchorNode);
            final AnchorNode secondAnchorNode = new AnchorNode(secondAnchor);
            this.endNodeArray.add(secondAnchorNode);
            Fragment var10001 = this.getSupportFragmentManager().findFragmentById(id.UI_ArSceneView);
            if (var10001 == null) {
                throw new TypeCastException("null cannot be cast to non-null type com.gj.arcoredraw.MyArFragment");
            }

            ArSceneView var12 = ((MyArFragment) var10001).getArSceneView();
            Intrinsics.checkExpressionValueIsNotNull(var12, "(UI_ArSceneView as MyArFragment).arSceneView");
            firstAnchorNode.setParent((NodeParent) var12.getScene());
            var10001 = this.getSupportFragmentManager().findFragmentById(id.UI_ArSceneView);
            if (var10001 == null) {
                throw new TypeCastException("null cannot be cast to non-null type com.gj.arcoredraw.MyArFragment");
            }

            var12 = ((MyArFragment) var10001).getArSceneView();
            Intrinsics.checkExpressionValueIsNotNull(var12, "(UI_ArSceneView as MyArFragment).arSceneView");
            secondAnchorNode.setParent((NodeParent) var12.getScene());
            MaterialFactory.makeOpaqueWithColor((Context) this, new Color(0.53F, 0.92F, 0.0F)).thenAccept((Consumer) (new Consumer() {
                // $FF: synthetic method
                // $FF: bridge method
                public void accept(Object var1) {
                    this.accept((Material) var1);
                }

                public final void accept(Material material) {
                    ModelRenderable sphere = ShapeFactory.makeSphere(0.02F, new Vector3(0.0F, 0.0F, 0.0F), material);
                    ArrayList var10000 = MainActivity.this.sphereNodeArray;
                    Node var3 = new Node();
                    ArrayList var6 = var10000;
                    int var5 = false;
                    var3.setParent((NodeParent) secondAnchorNode);
                    var3.setLocalPosition(Vector3.zero());
                    var3.setRenderable((Renderable) sphere);
                    var6.add(var3);
                }
            }));
            final Vector3 firstWorldPosition = firstAnchorNode.getWorldPosition();
            final Vector3 secondWorldPosition = secondAnchorNode.getWorldPosition();
            final Vector3 difference = Vector3.subtract(firstWorldPosition, secondWorldPosition);
            Vector3 directionFromTopToBottom = difference.normalized();
            final Quaternion rotationFromAToB = Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());
            MaterialFactory.makeOpaqueWithColor((Context) this, new Color(0.33F, 0.87F, 0.0F)).thenAccept((Consumer) (new Consumer() {
                // $FF: synthetic method
                // $FF: bridge method
                public void accept(Object var1) {
                    this.accept((Material) var1);
                }

                public final void accept(Material material) {
                    ModelRenderable lineMode = ShapeFactory.makeCube(new Vector3(0.01F, 0.01F, difference.length()), Vector3.zero(), material);
                    Node var4 = new Node();
                    int var6 = false;
                    var4.setParent((NodeParent) firstAnchorNode);
                    var4.setRenderable((Renderable) lineMode);
                    var4.setWorldPosition(Vector3.add(firstWorldPosition, secondWorldPosition).scaled(0.5F));
                    var4.setWorldRotation(rotationFromAToB);
                    final Node lineNode = var4;
                    ArrayList var10000 = MainActivity.this.lineNodeArray;
                    var4 = new Node();
                    ArrayList var7 = var10000;
                    var6 = false;
                    var4.setParent((NodeParent) firstAnchorNode);
                    var4.setRenderable((Renderable) lineMode);
                    var4.setWorldPosition(Vector3.add(firstWorldPosition, secondWorldPosition).scaled(0.5F));
                    var4.setWorldRotation(rotationFromAToB);
                    var7.add(var4);
                    ViewRenderable.builder().setView((Context) MainActivity.this, -1300044).build().thenAccept((Consumer) (new Consumer() {
                        // $FF: synthetic method
                        // $FF: bridge method
                        public void accept(Object var1) {
                            this.accept((ViewRenderable) var1);
                        }

                        public final void accept(ViewRenderable it) {
                            Intrinsics.checkExpressionValueIsNotNull(it, "it");
                            View var10000 = it.getView();
                            if (var10000 == null) {
                                throw new TypeCastException("null cannot be cast to non-null type android.widget.TextView");
                            } else {
                                TextView var10 = (TextView) var10000;
                                StringBuilder var10001 = new StringBuilder();
                                StringCompanionObject var2 = StringCompanionObject.INSTANCE;
                                String var3 = "%.1f";
                                Object[] var4 = new Object[]{length * (double) 100};
                                StringBuilder var6 = var10001;
                                TextView var5 = var10;
                                String var11 = String.format(var3, Arrays.copyOf(var4, var4.length));
                                Intrinsics.checkExpressionValueIsNotNull(var11, "java.lang.String.format(format, *args)");
                                String var7 = var11;
                                var5.setText((CharSequence) var6.append(var7).append("CM").toString());
                                it.setShadowCaster(false);
                                FaceToCameraNode var8 = new FaceToCameraNode();
                                int var9 = false;
                                var8.setParent((NodeParent) lineNode);
                                var8.setLocalRotation(Quaternion.axisAngle(new Vector3(0.0F, 1.0F, 0.0F), 90.0F));
                                var8.setLocalPosition(new Vector3(0.0F, 0.02F, 0.0F));
                                var8.setRenderable((Renderable) it);
                            }
                        }
                    }));
                }
            }));
        }
    }
}
