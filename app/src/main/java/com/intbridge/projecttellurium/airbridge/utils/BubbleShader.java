package com.intbridge.projecttellurium.airbridge.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.github.siyamed.shapeimageview.shader.ShaderHelper;
import com.intbridge.projecttellurium.airbridge.R;

/**
 * Shader for bubble image
 * Created by Derek on 7/9/2016.
 */
public class BubbleShader extends ShaderHelper {
    private static final int DEFAULT_HEIGHT_DP = 10;

    public enum ArrowPosition {
        @SuppressLint("RtlHardcoded")
        LEFT,
        RIGHT
    }

    private final Path path = new Path();

    private int triangleHeightPx;
    private ArrowPosition arrowPosition = ArrowPosition.LEFT;

    public BubbleShader() {
    }

    @Override
    public void init(Context context, AttributeSet attrs, int defStyle) {
        super.init(context, attrs, defStyle);
        borderWidth = 0;
        if(attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShaderImageView, defStyle, 0);
            triangleHeightPx = typedArray.getDimensionPixelSize(R.styleable.ShaderImageView_siTriangleHeight, 0);
            int arrowPositionInt = typedArray.getInt(R.styleable.ShaderImageView_siArrowPosition, ArrowPosition.LEFT.ordinal());
            arrowPosition = ArrowPosition.values()[arrowPositionInt];
            typedArray.recycle();
        }

        if(triangleHeightPx == 0) {
            triangleHeightPx = dpToPx(context.getResources().getDisplayMetrics(), DEFAULT_HEIGHT_DP);
        }
    }

    @Override
    public void draw(Canvas canvas, Paint imagePaint, Paint borderPaint) {
        canvas.save();
        canvas.concat(matrix);
        canvas.drawPath(path, imagePaint);
        canvas.restore();
    }

    @Override
    public void calculate(int bitmapWidth, int bitmapHeight,
                          float width, float height,
                          float scale,
                          float translateX, float translateY) {
        path.reset();
        float x = -translateX;
        float y = -translateY;
        float scaledTriangleHeight = triangleHeightPx / scale;
        float resultWidth = bitmapWidth + 2 * translateX;
        float resultHeight = bitmapHeight + 2 * translateY;
        float centerY  = resultHeight / 2f + y;
        float centerX  = resultWidth / 2f + x;

        path.setFillType(Path.FillType.EVEN_ODD);
        float rectLeft;
        float rectRight;
        float r = 30f;
        //float[] rad = {22.0f,22.0f,0,22.0f,0,22.0f,0,0};
        switch (arrowPosition) {
            case LEFT:
                rectLeft = scaledTriangleHeight + x;
                rectRight = resultWidth + rectLeft;

                //RectF rectL = new RectF(rectLeft, y, rectRight, resultHeight + y);
                //float[] radL = {r,r,r,r,0,0,0,0};

                // TODO: remind original author the bug
                // there is a bug at original BubbleShader, right value should use rectRight-rectLeft
                addThreeRoundedRect(path, rectLeft, y, rectRight-rectLeft, resultHeight + y, r, r, true);

                break;
            case RIGHT:
                // TODO: cannot do right still buggy
                rectLeft = x;
                float imgRight = resultWidth + rectLeft;
                rectRight = imgRight - scaledTriangleHeight;
                addThreeRoundedRect(path, rectLeft-rectRight, y, rectRight, resultHeight + y, r, r, true);
                path.moveTo(imgRight, centerY);
                path.lineTo(rectRight, centerY - scaledTriangleHeight);
                path.lineTo(rectRight, centerY + scaledTriangleHeight);
                path.lineTo(imgRight, centerY);
                break;
        }
    }

    @Override
    public void reset() {
        path.reset();
    }

    public int getTriangleHeightPx() {
        return triangleHeightPx;
    }

    public void setTriangleHeightPx(final int triangleHeightPx) {
        this.triangleHeightPx = triangleHeightPx;
    }

    public ArrowPosition getArrowPosition() {
        return arrowPosition;
    }

    public void setArrowPosition(final ArrowPosition arrowPosition) {
        this.arrowPosition = arrowPosition;
    }

    private void addThreeRoundedRect(Path path, float left, float top, float right, float bottom, float rx, float ry, boolean leftCorner) {
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width/2) rx = width/2;
        if (ry > height/2) ry = height/2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        path.rQuadTo(0, -ry, -rx, -ry);//top-right corner
        path.rLineTo(-widthMinusCorners, 0);
        path.rQuadTo(-rx, 0, -rx, ry); //top-left corner
        if(leftCorner) {
            path.rLineTo(0, heightMinusCorners/2);
            path.rQuadTo(0, ry+heightMinusCorners/2, -rx, ry+heightMinusCorners/2);
            path.rLineTo(widthMinusCorners + rx*2, 0);
            path.rQuadTo(rx, 0, rx, -ry); //bottom-right corner
        } else {
            path.rLineTo(0, heightMinusCorners);
            path.rQuadTo(0, ry, rx, ry);//bottom-left corner
            path.rLineTo(widthMinusCorners + rx, 0);
        }

    }
}
