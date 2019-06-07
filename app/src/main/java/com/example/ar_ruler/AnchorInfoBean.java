package com.example.ar_ruler;

import com.google.ar.core.Anchor;

class AnchorInfoBean {
    private String dataText;
    private Anchor anchor;
    private Double length;

    AnchorInfoBean(String dataText, Anchor anchor, double length) {
        super();
        this.dataText = dataText;
        this.anchor = anchor;
        this.length = length;
    }

    String getDataText() {
        return this.dataText;
    }

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
