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
    private final PuckCallback puckCallback;
    
    //======================================================================================
    //--  コンストラクタ
    //======================================================================================
    public Puck(int centerX, int centerY, int radius, PuckCallback pc) {
        this.centerPoint = new Point(centerX, centerY);
        this.radius = radius;
        this.puckCallback = pc;

        paint = new Paint();
    }

    //======================================================================================
    //--  描画メソッド
    //======================================================================================
    public void draw(Canvas canvas) {
        paint.setColor(Color.rgb(0xC0, 0xC0, 0xC0));
        canvas.drawCircle(centerPoint.x, centerPoint.y, radius, paint);
        paint.setColor(Color.argb(0x40, 0xFF, 0xFF, 0xFF));
        canvas.drawCircle(centerPoint.x, centerPoint.y, (int)(radius*0.7), paint);
    }

    //======================================================================================
    //--  移動メソッド
    //======================================================================================
    private int energyX = 3;   // X方向の力
    private int energyY = 5;   // Y方向の力

    public void move() {
        Point moveDis = puckCallback.changeEnergy(this, new Point(energyX, energyY));
        centerPoint.offset(moveDis.x, moveDis.y);  // 中心の移動
    }

    //======================================================================================
    //--  勢いの追加メソッド
    //======================================================================================
    public void scaleEnergy(int scaleX, int scaleY) {
        energyX *= scaleX;  energyY *= scaleY;
    }

    public void addEnergy(int eneX, int eneY) {
        energyX += eneX;  energyY += eneY;
    }

    public void setEnergy(int eneX, int eneY) {
        energyX = eneX; energyY = eneY;
    }

    //======================================================================================
    //======================================================================================
    //--  内部クラス
    //======================================================================================
    //======================================================================================

    //======================================================================================
    //--  パックコールバック
    //======================================================================================

    /*
     * @para  puck   移動させたいパック
     * @para  changeEnergy  移動させたい距離
     * メソッド内でパックの勢い(energyX, energyY)も変更される
     * 返却値は移動すべき距離
     */

    public interface PuckCallback {
        Point changeEnergy(Puck puck, Point moveDistance);
    }
}
