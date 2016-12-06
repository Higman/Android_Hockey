package com.example.yu_ya2.hockey;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by YU-YA2 on 2016/12/03.
 */

public class Mallet {

    public final Rect rect;   // マレットの矩形
    private final Paint paint;  // 色

    private final PointF powerScale = new PointF(0.9f, 0.9f);  // パックの勢いを変化させる力
    private final Point powerAdd = new Point(0, 0);            // パックに勢いを発生させる力

    private MalletCallback malletCallback;

    //======================================================================================
    //--  コンストラクタ
    //======================================================================================
    public Mallet(int left, int top, int right, int bottom, Paint paint, MalletCallback mc) {
        this.rect = new Rect(left, top, right, bottom);
        this.paint = paint;

        this.malletCallback = mc;
    }

    //======================================================================================
    //--  移動メソッド
    //======================================================================================
    public void move(int dx, int dy) {
        //-- 通常の移動
        Point moveDis = malletCallback.calcDistance(this, new Point(dx, dy));
        rect.offset(moveDis.x, moveDis.y);
    }

    //======================================================================================
    //--  描画メソッド
    //======================================================================================
    public void draw(Canvas canvas) {
        canvas.drawRect(rect, paint);
    }

    //======================================================================================
    //--  パックに勢いを与えるメソッド
    //======================================================================================
    public void givePower(Puck puck) {
        puck.scaleEnergy(powerScale.x, powerScale.y);   // パックの勢いの更新
        puck.addEnergy(powerAdd.x, powerAdd.y);         // パックの勢いの発生
    }

    //======================================================================================
    //-- マレットの力の追加メソッド
    //======================================================================================
    public void setPowerAdd(int powX, int powY) {
        powerAdd.x = powX; powerAdd.y = powY;
    }

    public void setPowerScale(float powX, float powY) {
        powerScale.x = powX; powerScale.y = powY;
    }

    //======================================================================================
    //--  スマッシュメソッド
    //======================================================================================
    private int smashMoveY = 0;

    private static final int SMASH_MOVE_SPEED = 10;   // 移動スピード
    private int smashDirection;  // スマッシュする向き

    public boolean smash(Puck puck, boolean dir) {   // dir で向きを設定、真で上向き
        if ( smashMoveCnt >= 0 ) { return false; }  // 実行中

        if ( dir ) {
            smashDirection = 1;
        } else {
            smashDirection = -1;
        }

        smashMoveCnt = SMASH_MOVE_COUNT-1;  // 初期化

        //-- パックのスピードがマレットの移動スピードより小さい場合
        setPowerScale(0.5f, 1.5f);

        return true;
    }

    private int smashMoveCnt = 0;
    private static final int SMASH_MOVE_COUNT = 26;  // 移動回数   偶数限定

    public void smashMove() {
        if ( smashMoveCnt < 0 ) { setPowerScale(0.9f, 0.9f);  return; }  // スマッシュ未発動

        if ( smashMoveCnt >= SMASH_MOVE_COUNT / 2 ) {
            smashMoveY = -smashDirection * SMASH_MOVE_SPEED;    // 上方向
        } else {
            smashMoveY = smashDirection * SMASH_MOVE_SPEED;     // 下方向
        }

        rect.offset(0, smashMoveY);  // 移動

        smashMoveCnt--;  // 更新
    }

    //======================================================================================
    //======================================================================================
    //--  内部クラス
    //======================================================================================
    //======================================================================================

    //======================================================================================
    //--  マレットコールバック
    //======================================================================================

    public interface MalletCallback {
        Point calcDistance(Mallet mallet, Point moveDistance);
    }
}
