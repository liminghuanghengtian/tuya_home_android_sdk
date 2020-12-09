package com.tuya.smart.android.demo.base.bean;

import android.graphics.Color;

/**
 * Created by letian on 15/12/8.
 */
public class RgbBean {
    private int red;
    private int blue;
    private int green;

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public void setColor(int color) {
        this.green = Color.green(color);
        this.red = Color.red(color);
        this.blue = Color.blue(color);
    }

    public static int getProgress(float progress) {
        return (int) ((1 - progress) * 255);
    }

    public int getColor() {
        return Color.argb(255, red, green, blue);
    }
}
