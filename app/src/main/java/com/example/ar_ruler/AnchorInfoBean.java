package com.example.ar_ruler;

import com.google.ar.core.Anchor;

class AnchorInfoBean {
    private String dataText;
    private Anchor anchor;
    private Double length;

    Anchor getAnchor() {
        return this.anchor;
    }

    double getLength() {
        return this.length;
    }

    void setLength(double var1) {
        this.length = var1;
    }
}
