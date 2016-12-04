package com.example.yu_ya2.hockey;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by YU-YA2 on 2016/12/03.
 */

public class Mallet {

    public final Rect rect;   // マレットの矩形
    private final Paint paint;  // 色

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
