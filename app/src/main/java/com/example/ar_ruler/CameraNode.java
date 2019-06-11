package com.example.ar_ruler;

import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;

public class CameraNode extends Node {
    public void onUpdate(FrameTime p0) {
        Scene scene = this.getScene();
        if (scene != null) {
            Camera camera = scene.getCamera();
            Vector3 cameraPosition = camera.getWorldPosition();
            Vector3 nodePosition = this.getWorldPosition();
            Vector3 direction = Vector3.subtract(cameraPosition, nodePosition);
            this.setWorldRotation(Quaternion.lookRotation(direction, Vector3.up()));
        }
    }
}
