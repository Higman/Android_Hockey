package com.example.yu_ya2.hockey;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by YU-YA on 2016/12/02.
 */

public class Board extends SurfaceView implements SurfaceHolder.Callback, Puck.PuckCallback, Mallet.MalletCallback {

    private final SurfaceHolder holder;
    private static final int DRAW_INTERVAL = 1000 / 80;  // 描画感覚

    private Puck puck;     // ホッケーのパック（丸いやつ）

    private Mallet player1, player2;  // マレット
    private static final double DEFAULT_PUCK_WIDTH_SCALE = 0.35;  // Viewのサイズに対するマレットの幅の比
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

            player1 = new Mallet(startPointLeft, startPointTop1, startPointLeft+malletWidth, startPointTop1+malletHeight, player1Paint, this);
            player2 = new Mallet(startPointLeft, startPointTop2, startPointLeft+malletWidth, startPointTop2+malletHeight, player2Paint, this);
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
            while ( !isFinished.get() ) {
                if ( holder.isCreating() ) {
                    continue;
                }

                Canvas canvas = holder.lockCanvas();
                if ( canvas == null ) {
                    continue;
                }

                long startTime = System.currentTimeMillis();

                drawGame(canvas);

                holder.unlockCanvasAndPost(canvas);

                long waitTime = DRAW_INTERVAL - (System.currentTimeMillis() - startTime);
                if ( waitTime > 0 ) {
                    synchronized (this) {
                        try {
                            wait(waitTime);
                        } catch (InterruptedException e) {

                        }
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
    private static final int MALLET_MOVE_DEFAULT_SPEED = 5;
    private int mallet_speed1 = MALLET_MOVE_DEFAULT_SPEED;
    private int mallet_speed2 = MALLET_MOVE_DEFAULT_SPEED;

    private void drawGame(Canvas canvas) {
        canvas.drawColor(Color.CYAN);
        this.puck.draw(canvas);
        this.player1.draw(canvas);
        this.player2.draw(canvas);

        //-- Player1
        HockeyMain.ButtonEventFlag btnFlag1 = boardCallback.getButtonEventFlag1();

        player1.setPowerAdd(0, 0);  // マレットが生み出すパックの勢いの初期化

        if ( btnFlag1.isBtnLeft() ) { player1.setPowerAdd(mallet_speed1/2, 0);   player1.move(-mallet_speed1, 0); }
        if ( btnFlag1.isBtnRight() ) { player1.setPowerAdd(-mallet_speed1/2, 0);  player1.move(mallet_speed1, 0); }
        if ( btnFlag1.isBtnSmash() ) { player1.smash(puck, true); }
        if ( btnFlag1.isBtnSp1() ) { mallet_speed1 = 10; }
        if ( btnFlag1.isBtnSp2() ) { mallet_speed1 = MALLET_MOVE_DEFAULT_SPEED; }

        player1.smashMove();        // スマッシュ動作

        //-- Player2
        player2.setPowerAdd(0, 0);  // マレットが生み出すパックの勢いの初期化

        HockeyMain.ButtonEventFlag btnFlag2 = boardCallback.getButtonEventFlag2();
        if ( btnFlag2.isBtnLeft() ) { player2.setPowerAdd(mallet_speed2/2, 0);  player2.move(mallet_speed2, 0); }
        if ( btnFlag2.isBtnRight() ) { player2.setPowerAdd(-mallet_speed2/2, 0);  player2.move(-mallet_speed2, 0); }
        if ( btnFlag2.isBtnSmash() ) { player2.smash(puck, false); }

        player2.smashMove();        // スマッシュ動作


        puck.move();  // Puckの移動

        //-- ゴール判定
        if ( puck.centerPoint.y+puck.radius <= 0 ) {
            boardCallback.addScorePlayer1();
            puck = new Puck(boardWidth/2, boardHeight/2, DEFAULT_PUCK_SIZE, this);
        } else if ( puck.centerPoint.y-puck.radius >= boardHeight ) {
            boardCallback.addScorePlayer2();
            puck = new Puck(boardWidth/2, boardHeight/2, DEFAULT_PUCK_SIZE, this);
        }
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
    public Point changeEnergy(Puck puck, Point moveDistance) {
        //-- 移動前のパックの端の座標
        int puckLeft = puck.centerPoint.x - puck.radius;      // パックの最左端
        int puckRight = puck.centerPoint.x + puck.radius;     // パックの最右端
        int puckTop = puck.centerPoint.y - puck.radius;       // パックの最上端
        int puckBottom = puck.centerPoint.y + puck.radius;    // パックの最下端

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
            int dis = puck.centerPoint.x - puck.radius;
            newCenterX -= dis;  newPuckLeft -= dis;  newPuckRight -= dis;
            retDistance.x = -dis;
            puck.scaleEnergy(-1, 1);
        } else if ( newPuckRight > boardWidth ) {
            int dis = boardWidth - (puck.centerPoint.x + puck.radius);
            newCenterX += dis;  newPuckLeft += dis;  newPuckRight += dis;
            retDistance.x = dis;
            puck.scaleEnergy(-1, 1);
        }

        //-- プレイヤー1のマレットとの判定
        Rect player1Range = new Rect(player1.rect.left-puck.radius, player1.rect.top-puck.radius, player1.rect.right+puck.radius, player1.rect.bottom+puck.radius);  // マレットとパックが接触している可能性がある範囲
        if ( player1Range.contains(newCenterX, newCenterY) ) {   // 大雑把な接触判定
            if ( player1.rect.contains(newCenterX, newPuckBottom) ) {  // パックの下端がマレットと接触した場合
                int dis = puckBottom - player1.rect.top;
                retDistance.y = -dis;
                puck.scaleEnergy(1, -1);
                player1.givePower(puck);
            } else if ( player1.rect.contains(newCenterX, newPuckTop) )  {  // パックの上端がマレットと接触した場合
                int dis = puckTop - player1.rect.bottom;
                retDistance.y = -dis;
                puck.scaleEnergy(1, -1);
                player1.givePower(puck);
            } else if ( player1.rect.contains(newPuckLeft, newCenterY) ) {  // パックの左端がマレットと接触した場合
                int dis = puckLeft - player1.rect.right;
                retDistance.x = -dis;
                puck.scaleEnergy(-1, 1);
                player1.givePower(puck);
            } else if ( player1.rect.contains(newPuckRight, newCenterY) ) {  // パックの右端がマレットと接触した場合
                int dis = puckRight - player1.rect.left;
                retDistance.x = -dis;
                puck.scaleEnergy(-1, 1);
                player1.givePower(puck);
            } else {  // 角がパックと接触したかどうかを判定
                int left = player1.rect.left;
                int top = player1.rect.top;
                int right = player1.rect.right;
                int bottom = player1.rect.bottom;
                if ( getDistance(newCenterX, newCenterY, left, top) <= puck.radius ) {
                    int sum = Math.abs(moveDistance.x) + Math.abs(moveDistance.y);
                    retDistance.x = -2 * sum / 3;   retDistance.y = -2 * sum / 3;
                    puck.setEnergy(retDistance.x, retDistance.y);
                    player1.givePower(puck);
                } else if ( getDistance(newCenterX, newCenterY, right, top) <= puck.radius ) {
                    int sum = Math.abs(moveDistance.x) + Math.abs(moveDistance.y);
                    retDistance.x = 2 * sum / 3;   retDistance.y = -2 * sum / 3;
                    puck.setEnergy(retDistance.x, retDistance.y);
                    player1.givePower(puck);
                } else if ( getDistance(newCenterX, newCenterY, left, bottom) <= puck.radius ) {
                    int sum = Math.abs(moveDistance.x) + Math.abs(moveDistance.y);
                    retDistance.x = -2 * sum / 3;   retDistance.y = 2 * sum / 3;
                    puck.setEnergy(retDistance.x, retDistance.y);
                    player1.givePower(puck);
                } else if ( getDistance(newCenterX, newCenterY, right,bottom) <= puck.radius ) {
                    int sum = Math.abs(moveDistance.x) + Math.abs(moveDistance.y);
                    retDistance.x = 2 * sum / 3;   retDistance.y = 2 * sum / 3;
                    puck.setEnergy(retDistance.x, retDistance.y);
                    player1.givePower(puck);
                }
            }
        }

        //-- プレイヤー2のマレットとの判定
        Rect player2Range = new Rect(player2.rect.left-puck.radius, player2.rect.top-puck.radius, player2.rect.right+puck.radius, player2.rect.bottom+puck.radius);  // マレットとパックが接触している可能性がある範囲
        if ( player2Range.contains(newCenterX, newCenterY) ) {   // 大雑把な接触判定
            if ( player2.rect.contains(newCenterX, newPuckBottom) ) {  // パックの下端がマレットと接触した場合
                int dis = puckBottom - player2.rect.top;
                retDistance.y = -dis;
                puck.scaleEnergy(1, -1);
                player2.givePower(puck);
            } else if ( player2.rect.contains(newCenterX, newPuckTop) )  {  // パックの上端がマレットと接触した場合
                int dis = puckTop - player2.rect.bottom;
                retDistance.y = -dis;
                puck.scaleEnergy(1, -1);
                player2.givePower(puck);
            } else if ( player2.rect.contains(newPuckLeft, newCenterY) ) {  // パックの左端がマレットと接触した場合
                int dis = puckLeft - player2.rect.right;
                retDistance.x = -dis;
                puck.scaleEnergy(-1, 1);
                player2.givePower(puck);
            } else if ( player2.rect.contains(newPuckRight, newCenterY) ) {  // パックの右端がマレットと接触した場合
                int dis = puckRight - player2.rect.left;
                retDistance.x = -dis;
                puck.scaleEnergy(-1, 1);
                player2.givePower(puck);
            } else {  // 角がパックと接触したかどうかを判定
                int left = player2.rect.left;
                int top = player2.rect.top;
                int right = player2.rect.right;
                int bottom = player2.rect.bottom;
                if ( getDistance(newCenterX, newCenterY, left, top) <= puck.radius ) {  //左上角
                    int sum = Math.abs(moveDistance.x) + Math.abs(moveDistance.y);
                    retDistance.x = -2 * sum / 3;   retDistance.y = -2 * sum / 3;
                    puck.setEnergy(retDistance.x, retDistance.y);
                    player2.givePower(puck);
                } else if ( getDistance(newCenterX, newCenterY, right, top) <= puck.radius ) {  // 右上角
                    int sum = Math.abs(moveDistance.x) + Math.abs(moveDistance.y);
                    retDistance.x = 2 * sum / 3;   retDistance.y = -2 * sum / 3;
                    puck.setEnergy(retDistance.x, retDistance.y);
                    player2.givePower(puck);
                } else if ( getDistance(newCenterX, newCenterY, left, bottom) <= puck.radius ) {  // 左下角
                    int sum = Math.abs(moveDistance.x) + Math.abs(moveDistance.y);
                    retDistance.x = -2 * sum / 3;   retDistance.y = 2 * sum / 3;
                    puck.setEnergy(retDistance.x, retDistance.y);
                    player2.givePower(puck);
                } else if ( getDistance(newCenterX, newCenterY, right,bottom) <= puck.radius ) {  // 右下角
                    int sum = Math.abs(moveDistance.x) + Math.abs(moveDistance.y);
                    retDistance.x = 2 * sum / 3;   retDistance.y = 2 * sum / 3;
                    puck.setEnergy(retDistance.x, retDistance.y);
                    player2.givePower(puck);
                }
            }
        }

        return retDistance;
    }

    //======================================================================================
    //--  マレットコールバック
    //======================================================================================
    @Override
    public Point calcDistance(Mallet mallet, Point moveDistance) {
        //-- 返却用Point
        Point retDistance = new Point(moveDistance.x, moveDistance.y);

        //-- 移動後のマレットの端の座標
        int newMalletLeft = mallet.rect.left + moveDistance.x;       // マレットの最左端
        int newMalletRight = mallet.rect.right + moveDistance.x;     // マレットの最右端
        int newMalletTop = mallet.rect.top + moveDistance.y;         // マレットの最上端
        int newMalletBottom = mallet.rect.bottom + moveDistance.y;   // マレットの最下端

        //-- ボードの壁との判定
        if ( newMalletLeft < 0 ) {
            int dis = mallet.rect.left;
            newMalletLeft -= dis;  newMalletRight -= dis;
            retDistance.x = -dis;
        } else if ( newMalletRight > boardWidth ) {
            int dis = boardWidth - mallet.rect.right;
            newMalletLeft += dis;  newMalletRight += dis;
            retDistance.x = dis;
        }

        //-- パックとの判定
        // パックの最端の座標
        int puckLeft = puck.centerPoint.x-puck.radius;
        int puckTop = puck.centerPoint.y-puck.radius;
        int puckRight = puck.centerPoint.x+puck.radius;
        int puckBottom = puck.centerPoint.y+puck.radius;

        //-- マレットがパックを壁と挟み込む形になるとき
        if ( mallet.rect.top <= puck.centerPoint.y && puck.centerPoint.y <= mallet.rect.bottom  ) {  // パックがマレットと同じy軸上にいるか
            if ( puck.centerPoint.x <= puck.radius || puck.centerPoint.x >= boardWidth - puck.radius ) {  // パックが壁に引っ付いているか
                if ( new Rect(newMalletLeft, newMalletTop, newMalletRight, newMalletBottom).intersect(puckLeft, puckTop, puckRight, puckBottom) ) {  // 接触しているか
                    retDistance.set(0, 0);

                    return retDistance;
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
        void addScorePlayer1();
        void addScorePlayer2();
    }
}
