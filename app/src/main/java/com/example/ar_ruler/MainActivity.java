package com.example.ar_ruler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.google.ar.core.Anchor;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ArrayList<AnchorInfoBean> anchorInfoBeanArray = new ArrayList<>();
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
            switch (anchorInfoBeanArray.size()) {
                case 0:
                    ToastUtils.showLong("No Record");
                    break;
                case 1:
                    anchorInfoBeanArray.clear();
                    lineNodeArray.clear();
                    sphereNodeArray.clear();
                    startNodeArray.clear();
                    endNodeArray.clear();
                    fragment = getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
                    arSceneView = ((MyArFragment) Objects.requireNonNull(fragment)).getArSceneView();
                    arSceneView.getScene().removeChild(startNode);
                    break;
                default:
                    anchorInfoBeanArray.remove(anchorInfoBeanArray.size() - 1);
                    int index = startNodeArray.size() - 1;
                    (startNodeArray.get(index)).removeChild(lineNodeArray.remove(index));
                    (endNodeArray.get(index)).removeChild(sphereNodeArray.remove(index + 1));
                    fragment = getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
                    arSceneView = ((MyArFragment) Objects.requireNonNull(fragment)).getArSceneView();
                    arSceneView.getScene().removeChild(startNodeArray.remove(index));
                    arSceneView = ((MyArFragment) fragment).getArSceneView();
                    arSceneView.getScene().removeChild(endNodeArray.remove(index));
            }
        });

        final ImageView UI_Post = findViewById(R.id.UI_Post);
        UI_Post.setOnClickListener(v -> {
            if (anchorInfoBeanArray.size() < 4) {
                ToastUtils.showLong("At least four points");
            } else {
                ArrayList<Float> NodePositionArray = new ArrayList<>();
                Iterable dataArray = MainActivity.this.anchorInfoBeanArray;
                int i = 0;

                for (Object object : dataArray) {
                    int i1 = i++;
                    if (i1 < 0) {
                        throw new ArrayIndexOutOfBoundsException("Index overflow");
                    }

                    AnchorInfoBean anchorInfoBean = (AnchorInfoBean) object;
                    if (i1 == MainActivity.this.anchorInfoBeanArray.size() - 1) {
                        Pose startPose = (MainActivity.this.anchorInfoBeanArray.get(0)).getAnchor().getPose();
                        Pose endPose = anchorInfoBean.getAnchor().getPose();
                        float dx = startPose.tx() - endPose.tx();
                        float dy = startPose.ty() - endPose.ty();
                        float dz = startPose.tz() - endPose.tz();
                        if (Math.sqrt((double) (dx * dx + dy * dy + dz * dz)) > (double) 1) {
                            AnchorNode node = new AnchorNode(anchorInfoBean.getAnchor());
                            NodePositionArray.add(node.getWorldPosition().x);
                            NodePositionArray.add(node.getWorldPosition().z);
                        }
                    } else {
                        AnchorNode node = new AnchorNode(anchorInfoBean.getAnchor());
                        NodePositionArray.add(node.getWorldPosition().x);
                        NodePositionArray.add(node.getWorldPosition().z);
                    }
                }

                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SecondActivity.class);
                intent.putExtra("xylist", NodePositionArray);
                startActivity(intent);
            }
        });
        initAr();
    }

    public void initAr() {
        Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
        ((ArFragment) Objects.requireNonNull(fragment)).setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            Anchor anchor = hitResult.createAnchor();
            AnchorInfoBean anchorInfoBean = new AnchorInfoBean("", anchor, 0.0);
            anchorInfoBeanArray.add(anchorInfoBean);
            if (anchorInfoBeanArray.size() > 1) {
                Anchor endAnchor = (anchorInfoBeanArray.get(anchorInfoBeanArray.size() - 1)).getAnchor();
                Anchor startAnchor = (anchorInfoBeanArray.get(anchorInfoBeanArray.size() - 2)).getAnchor();
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
                ArSceneView arSceneView = ((MyArFragment) Objects.requireNonNull(fragmentById)).getArSceneView();
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

    public void drawLine(Anchor firstAnchor, Anchor secondAnchor, final double length) {
        AnchorNode firstAnchorNode = new AnchorNode(firstAnchor);
        startNodeArray.add(firstAnchorNode);
        AnchorNode secondAnchorNode = new AnchorNode(secondAnchor);
        endNodeArray.add(secondAnchorNode);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.UI_ArSceneView);
        ArSceneView arSceneView = ((MyArFragment) Objects.requireNonNull(fragment)).getArSceneView();
        firstAnchorNode.setParent(arSceneView.getScene());
        secondAnchorNode.setParent(arSceneView.getScene());

        MaterialFactory.makeOpaqueWithColor(this, new Color(0.50F, 0.90F, 0.0F))
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
        Vector3 direction = difference.normalized();
        Quaternion rotation = Quaternion.lookRotation(direction, Vector3.up());

        MaterialFactory.makeOpaqueWithColor(this, new Color(0.33F, 0.87F, 0.0F))
                .thenAccept(material -> {
                    ModelRenderable lineMode = ShapeFactory.makeCube(
                            new Vector3(0.01F, 0.01F, difference.length()), Vector3.zero(), material);
                    Node lineNode = new Node();
                    lineNode.setParent(firstAnchorNode);
                    lineNode.setWorldPosition(Vector3.add(firstWorldPosition, secondWorldPosition).scaled(0.5F));
                    lineNode.setWorldRotation(rotation);
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
                                    String var11 = String.format(Locale.US, "%.1f", length * (double) 100);
                                    textView.setText(stringBuilder.append(var11).append("CM").toString());
                                    r.setShadowCaster(false);
                                    CameraNode cameraNode = new CameraNode();
                                    cameraNode.setParent(lineNode);
                                    cameraNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0.0F, 1.0F, 0.0F), 90.0F));
                                    cameraNode.setLocalPosition(new Vector3(0.0F, 0.02F, 0.0F));
                                    cameraNode.setRenderable(r);
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
