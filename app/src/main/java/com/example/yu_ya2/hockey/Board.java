package com.example.yu_ya2.hockey;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by YU-YA on 2016/12/02.
 */

public class Board extends SurfaceView implements SurfaceHolder.Callback, Puck.PuckCallback {

    private final SurfaceHolder holder;
    private static final int DRAW_INTERVAL = 1000 / 80;  // 描画感覚

    private Puck puck;     // ホッケーのパック（丸いやつ）

    private Mallet player1, player2;  // マレット
    private static final double DEFAULT_PUCK_WIDTH_SCALE = 0.3;  // Viewのサイズに対するマレットの幅の比
    private static final double DEFAULT_PUCK_HEIGHT_SCALE = 0.05;  // Viewのサイズに対するマレットの高さの比

    private static final int DEFAULT_PUCK_SIZE = 50;  // パックのサイズ

    private int boardWidth;
    private int boardHeight;

    //======================================================================================
    //--  コンストラクタ
    //======================================================================================
    public Board(Context context, SurfaceView sv) {
        super(context);

        holder = sv.getHolder();
        holder.addCallback(this);
    }

    //======================================================================================
    //--  サーフェイス関連
    //======================================================================================
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        startDrawThread();

        //-- ボードサイズの保存
        this.boardWidth = width;
        this.boardHeight = height;

        //-- パックの作成
        if ( this.puck == null ) {
            this.puck = new Puck(width/2, height/2, DEFAULT_PUCK_SIZE, this);
        }

        //-- マレットの作成
        if ( player1 == null || player2 == null ) {
            Paint player1Paint = new Paint();
            Paint player2Paint = new Paint();
            player1Paint.setColor(Color.BLUE);
            player2Paint.setColor(Color.RED);

            int malletWidth = (int) (width * DEFAULT_PUCK_WIDTH_SCALE);
            int malletHeight = (int)(height * DEFAULT_PUCK_HEIGHT_SCALE);

            int startPointLeft = width / 2 - malletWidth / 2;  // 開始時のマレットの左端の位置

            int startPointTop1 = height - 2 * malletHeight;    // 開始時のマレット1の上端の位置
            int startPointTop2 = malletHeight;                 // 開始時のマレット2の上端の位置

            player1 = new Mallet(startPointLeft, startPointTop1, startPointLeft+malletWidth, startPointTop1+malletHeight, player1Paint);
            player2 = new Mallet(startPointLeft, startPointTop2, startPointLeft+malletWidth, startPointTop2+malletHeight, player2Paint);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopDrawThread();
    }

    //======================================================================================
    //--  描画スレッド
    //======================================================================================
    private class DrawThread extends Thread {
        private final AtomicBoolean isFinished = new AtomicBoolean(false);

        public void finish() {
            isFinished.set(true);
        }

        @Override
        public void run() {
            while (!isFinished.get()) {
                if (holder.isCreating()) {
                    continue;
                }

                Canvas canvas = holder.lockCanvas();
                if (canvas == null) {
                    continue;
                }
                drawGame(canvas);

                holder.unlockCanvasAndPost(canvas);

                puck.move();  // Puckの移動

                //-- Player1
                HockeyMain.ButtonEventFlag btnFlag = boardCallback.getButtonEventFlag1();
                if ( btnFlag.isBtnLeft() ) { player1.move(5, 0); }
                if ( btnFlag.isBtnRight() ) { player1.move(-5, 0); }

                synchronized (this) {
                    try {
                        wait(DRAW_INTERVAL);
                    } catch (InterruptedException e) {
                    }
                }
            }

        }
    }

    //======================================================================================
    //--  描画スレッド操作メソッド
    //======================================================================================
    private DrawThread drawThread;

    public void startDrawThread() {
        stopDrawThread();

        drawThread = new DrawThread();
        drawThread.start();
    }

    public boolean stopDrawThread() {
        if (drawThread == null) {
            return false;
        }
        drawThread.finish();
        drawThread = null;

        return true;
    }

    //======================================================================================
    //--  描画メソッド
    //======================================================================================
    private void drawGame(Canvas canvas) {
        canvas.drawColor(Color.CYAN);
        this.puck.draw(canvas);
        this.player1.draw(canvas);
        this.player2.draw(canvas);
    }

    //======================================================================================
    //--  セットコールバック
    //======================================================================================
    BoardCallback boardCallback;

    public void setCallback(BoardCallback boardCallback) {
        this.boardCallback = boardCallback;
    }

    //======================================================================================
    //--  パックコールバック
    //======================================================================================
    @Override
    public Point moveDistance(Puck puck, Point moveDistance) {
        //-- 移動後のpuckの中心座標
        int newCenterX = puck.centerPoint.x + moveDistance.x;
        int newCenterY = puck.centerPoint.y + moveDistance.y;

        //-- 返却用Point
        Point retDistance = new Point(moveDistance.x, moveDistance.y);

        //-- パックの端の座標
        int newPuckLeft = newCenterX - puck.radius;      // パックの最左端
        int newPuckRight = newCenterX + puck.radius;     // パックの最右端
        int newPuckTop = newCenterY - puck.radius;       // パックの最上端
        int newPuckBottom = newCenterY + puck.radius;    // パックの最下端

        //-- ボードの壁との判定
        if ( newPuckLeft < 0 ) {
            int dis = newPuckLeft;
            newCenterX -= dis;  newPuckLeft -= dis;  newPuckRight -= dis;
            retDistance.x = -dis;
        }
        if ( newPuckRight > boardWidth ) {
            int dis = boardWidth - newPuckRight;
            newCenterX -= dis;  newPuckLeft -= dis;  newPuckRight -= dis;
            retDistance.x = -dis;
        }

        //-- プレイヤー1のマレットとの判定
        Rect player1Range = new Rect(player1.rect.left-puck.radius, player1.rect.top-puck.radius, player1.rect.right+puck.radius, player1.rect.bottom+puck.radius);  // マレットとパックが接触している可能性がある範囲
        if ( player1Range.contains(newCenterX, newCenterY) ) {   // 大雑把な接触判定
            if ( player1.rect.contains(newCenterX, newPuckBottom) ) {  // パックの下端がマレットと接触した場合
                int dis = newPuckBottom - player1.rect.top;
                retDistance.y = -dis;
            } else if ( player1.rect.contains(newCenterX, newPuckTop) )  {  // パックの上端がマレットと接触した場合
                int dis = newPuckTop - player1.rect.bottom;
                retDistance.y = -dis;
            } else if ( player1.rect.contains(newPuckLeft, newCenterY) ) {  // パックの左端がマレットと接触した場合
                int dis = newPuckLeft - player1.rect.right;
                retDistance.x = -dis;
            } else if ( player1.rect.contains(newPuckRight, newCenterY) ) {  // パックの右端がマレットと接触した場合
                int dis = newPuckRight - player1.rect.left;
                retDistance.x = -dis;
            } else {  // 角がパックと接触したかどうかを判定
                int left = player1.rect.left;
                int top = player1.rect.top;
                int right = player1.rect.right;
                int bottom = player1.rect.bottom;
                if ( getDistance(newCenterX, newCenterY, left, top) <= puck.radius ) {

                }

            }
        }


        return retDistance;
    }

    //======================================================================================
    //--  2間の距離を求めるメソッド
    //======================================================================================
    protected int getDistance(double x, double y, double x2, double y2) {
        double distance = Math.sqrt((x2 - x) * (x2 - x) + (y2 - y) * (y2 - y));

        return (int) distance;
    }

    //======================================================================================
    //======================================================================================
    //--  内部クラス
    //======================================================================================
    //======================================================================================

    //======================================================================================
    //--  ボードコールバック
    //======================================================================================
    public interface BoardCallback {
        HockeyMain.ButtonEventFlag getButtonEventFlag1();
        HockeyMain.ButtonEventFlag getButtonEventFlag2();
    }
}
