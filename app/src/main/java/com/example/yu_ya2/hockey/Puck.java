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
    private int energyX = 0;   // X方向の力
    private int energyY = 5;   // Y方向の力

    public void move() {
        Point moveDis = puckCallback.changeEnergy(this, new Point(energyX, energyY));
        centerPoint.offset(moveDis.x, moveDis.y);  // 中心の移動
    }

    public int getEnergyX() {
        return energyX;
    }

    public void setEnergyX(int energyX) {
        this.energyX = energyX;
    }

    public int getEnergyY() {
        return energyY;
    }

    public void setEnergyY(int energyY) {
        this.energyY = energyY;
    }

    //======================================================================================
    //--  勢いの追加メソッド
    //======================================================================================
    private final int MIN_ENERGY_Y = 5;
    private final int MAX_ENERGY_Y = 45;
    private final int MIN_ENERGY_X = 0;
    private final int MAX_ENERGY_X = 45;

    public void scaleEnergy(float scaleX, float scaleY) {
        energyX *= scaleX;  energyY *= scaleY;

        //-- 上限に達した時の補正
        if ( Math.abs(energyX) > MAX_ENERGY_X) { energyX = (int) (MAX_ENERGY_X * Math.signum(energyX)); }
        if ( Math.abs(energyY) > MAX_ENERGY_Y) { energyY = (int) (MAX_ENERGY_Y * Math.signum(energyY)); }

        //-- 下限に達した時の補正
        if ( Math.abs(energyX) < MIN_ENERGY_X) { energyX = (int) (MIN_ENERGY_X * Math.signum(energyX)); }
        if ( Math.abs(energyY) < MIN_ENERGY_Y) { energyY = (int) (MIN_ENERGY_Y * Math.signum(energyY)); }
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
