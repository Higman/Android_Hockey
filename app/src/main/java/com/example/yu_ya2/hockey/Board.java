package com.example.yu_ya2.hockey;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by YU-YA on 2016/12/02.
 */

public class Board extends SurfaceView implements SurfaceHolder.Callback {

    private final SurfaceHolder holder;
    private static final int DRAW_INTERVAL = 1000 / 80;  // 描画感覚

    private Puck puck;     // ホッケーのパック（丸いやつ）
    private static final int DEFAULT_PUCK_SIZE = 50;  // パックのサイズ

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

        this.puck = new Puck(width/2, height/2, DEFAULT_PUCK_SIZE);
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
        if ( this.puck != null ) { this.puck.draw(canvas); }
    }
}
