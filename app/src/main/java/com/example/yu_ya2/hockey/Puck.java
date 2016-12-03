package com.example.yu_ya2.hockey;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * Created by YU-YA on 2016/12/02.
 */

public class Puck {

    public final Point centerPoint;    // 円の中心
    public final int radius;           // 半径
    private final Paint paint;         // 色


    //======================================================================================
    //--  コンストラクタ
    //======================================================================================
    public Puck(int centerX, int centerY, int radius) {
        this.centerPoint = new Point(centerX, centerY);
        this.radius = radius;

        paint = new Paint();
    }

    //======================================================================================
    //--  描画メソッド
    //======================================================================================
    public void draw(Canvas canvas) {
        paint.setColor(Color.rgb(0x69, 0x69, 0x69));
        canvas.drawCircle(centerPoint.x, centerPoint.y, radius, paint);
        paint.setColor(Color.argb(0x40, 0x00, 0x00, 0x00));
        canvas.drawCircle(centerPoint.x, centerPoint.y, (int)(radius*0.7), paint);
    }

    //======================================================================================
    //--  移動メソッド
    //======================================================================================
    private int energyX = 0;   // X方向の力
    private int energyY = 1;   // Y方向の力

    public void move() {
        centerPoint.offset(energyX, energyY);  // 中心の移動
    }

    //======================================================================================
    //======================================================================================
    //--  内部クラス
    //======================================================================================
    //======================================================================================

    //======================================================================================
    //--  当たり判定コールバック
    //======================================================================================

    /*
     * 返却値を Point をx方向,
     *
     */
    public interface CollisionCallback {
        Point puckHit();
    }
}
