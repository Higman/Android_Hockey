package com.example.yu_ya2.hockey;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by YU-YA2 on 2016/12/03.
 */

public class Mallet {

    public final Rect rect;   // マレットの矩形
    private final Paint paint;  // 色

    //======================================================================================
    //--  コンストラクタ
    //======================================================================================
    public Mallet(int left, int top, int right, int bottom, Paint paint) {
        this.rect = new Rect(left, top, right, bottom);
        this.paint = paint;
    }

    //======================================================================================
    //--  移動メソッド
    //======================================================================================
    public void move(int dx, int dy) {
        rect.offset(dx, dy);
    }

    //======================================================================================
    //--  描画メソッド
    //======================================================================================
    public void draw(Canvas canvas) {
        canvas.drawRect(rect, paint);
    }
}
