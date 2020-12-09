package com.tuya.smart.android.demo.base.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.tuya.smart.android.demo.R;


/**
 * Created by letian on 15/12/3.
 */
public class LampView extends ColorPicker {
    private static final String TAG = "LampView";
    private int lampHeight;
    private int lampWidth;
    private VectorDrawable lamp;

    public LampView(Context context) {
        super(context);
        initLamp(null, 0);
    }

    public LampView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLamp(attrs, 0);
    }

    public LampView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLamp(attrs, defStyle);
    }

    private void initLamp(AttributeSet attrs, int defStyle) {
        lamp = VectorDrawable.getDrawable(getContext(), R.drawable.lamp);

        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.LampView, defStyle, 0);
        final Resources b = getContext().getResources();

        lampWidth = a.getDimensionPixelSize(
                R.styleable.LampView_lamp_width,
                b.getDimensionPixelSize(R.dimen.lamp_width));

        lampHeight = a.getDimensionPixelSize(
                R.styleable.LampView_lamp_height,
                b.getDimensionPixelSize(R.dimen.lamp_height));
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lamp != null) {
            canvas.translate(-(lampWidth >> 1), -(lampHeight >> 1));
            lamp.setBounds(0, 0, lampWidth, lampHeight);
            lamp.setAllowCaching(false);
            lamp.setColor(getNewCenterColor());
            lamp.draw(canvas);
        }
    }

    public void setLamp(VectorDrawable lamp) {
        this.lamp = lamp;
    }

}
