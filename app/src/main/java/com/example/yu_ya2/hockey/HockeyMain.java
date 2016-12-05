package com.example.yu_ya2.hockey;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HockeyMain extends Activity implements Board.BoardCallback {

    private SurfaceView surfaceView;
    private Board board;

    private ButtonEventFlag buttonEventFlag1;
    private ButtonEventFlag buttonEventFlag2;

    TextView text1;
    TextView text2;
    private int player1Score = 0;
    private int player2Score = 0;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_layout);
        surfaceView = (SurfaceView)findViewById(R.id.board_view);
        board = new Board(this, surfaceView);
        board.setCallback(this);  // コールバックセット

        //-- イベント付与
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        text1 = (TextView) findViewById(R.id.player1_score_text);
        text1.setText(Integer.toString(player1Score));
        text2 = (TextView) findViewById(R.id.player2_score_text);
        text2.setText(Integer.toString(player2Score));
    }

    //======================================================================================
    //--  コールバック
    //======================================================================================
    @Override
    public ButtonEventFlag getButtonEventFlag1() {
        return buttonEventFlag1;
    }

    @Override
    public ButtonEventFlag getButtonEventFlag2() {
        return buttonEventFlag2;
    }

    //======================================================================================
    //--  スコア加算メソッド
    //======================================================================================
    @Override
    public void addScorePlayer1() {
        player1Score++;

        handler.post(new Runnable() {
            @Override
            public void run() {
                text1.setText(String.valueOf(player1Score));

                return;
            }
        });
    }

    @Override
    public void addScorePlayer2() {
        player2Score++;

        handler.post(new Runnable() {
            @Override
            public void run() {
                text2.setText(String.valueOf(player2Score));

                return;
            }
        });
    }

    //======================================================================================
    //--  ボタンイベント設定メソッド
    //======================================================================================
    private void initListener() {

        buttonEventFlag1 = new ButtonEventFlag();  // player1のフラグセット
        buttonEventFlag2 = new ButtonEventFlag();  // player1のフラグセット

        //--- プレイヤー1
        //-- ボタン関連
        Button p1LftBtn = (Button) findViewById(R.id.player1_left_button);
        Button p1RhtBtn = (Button) findViewById(R.id.player1_right_button);
        Button p1SmsBtn = (Button) findViewById(R.id.player1_smash_button);
        Button p1SpBtn1 = (Button) findViewById(R.id.player1_special_button1);
        Button p1SpBtn2 = (Button) findViewById(R.id.player1_special_button2);
        Button p1SpBtn3 = (Button) findViewById(R.id.player1_special_button3);
        Button p1SpBtn4 = (Button) findViewById(R.id.player1_special_button4);

        //- リスナーセット
        p1LftBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    buttonEventFlag1.setBtnLeft(true);
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    buttonEventFlag1.setBtnLeft(false);
                }
                return false;
            }
        });

        p1RhtBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    buttonEventFlag1.setBtnRight(true);
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    buttonEventFlag1.setBtnRight(false);
                }
                return false;
            }
        });

        p1SmsBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    buttonEventFlag1.setBtnSmash(true);
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    buttonEventFlag1.setBtnSmash(false);
                }
                return false;
            }
        });

        p1SpBtn1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    buttonEventFlag1.setBtnSp1(true);
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    buttonEventFlag1.setBtnSp1(false);
                }
                return false;
            }
        });

        p1SpBtn2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    buttonEventFlag1.setBtnSp2(true);
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    buttonEventFlag1.setBtnSp2(false);
                }
                return false;
            }
        });

        p1SpBtn3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    buttonEventFlag1.setBtnSp3(true);
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    buttonEventFlag1.setBtnSp3(false);
                }
                return false;
            }
        });

        p1SpBtn4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    buttonEventFlag1.setBtnSp4(true);
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    buttonEventFlag1.setBtnSp4(false);
                }
                return false;
            }
        });

        //--- プレイヤー2
        //-- ボタン関連
        Button p2LftBtn = (Button) findViewById(R.id.player2_left_button);
        Button p2RhtBtn = (Button) findViewById(R.id.player2_right_button);
        Button p2SmsBtn = (Button) findViewById(R.id.player2_smash_button);
        Button p2SpBtn1 = (Button) findViewById(R.id.player2_special_button1);
        Button p2SpBtn2 = (Button) findViewById(R.id.player2_special_button2);
        Button p2SpBtn3 = (Button) findViewById(R.id.player2_special_button3);
        Button p2SpBtn4 = (Button) findViewById(R.id.player2_special_button4);

        //- リスナーセット
        p2LftBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    buttonEventFlag2.setBtnLeft(true);
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    buttonEventFlag2.setBtnLeft(false);
                }
                return false;
            }
        });

        p2RhtBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    buttonEventFlag2.setBtnRight(true);
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    buttonEventFlag2.setBtnRight(false);
                }
                return false;
            }
        });

        p2SmsBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    buttonEventFlag2.setBtnSmash(true);
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    buttonEventFlag2.setBtnSmash(false);
                }
                return false;
            }
        });

        p2SpBtn1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    buttonEventFlag2.setBtnSp1(true);
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    buttonEventFlag2.setBtnSp1(false);
                }
                return false;
            }
        });

        p2SpBtn2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    buttonEventFlag2.setBtnSp2(true);
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    buttonEventFlag2.setBtnSp2(false);
                }
                return false;
            }
        });

        p2SpBtn3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    buttonEventFlag2.setBtnSp3(true);
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    buttonEventFlag2.setBtnSp3(false);
                }
                return false;
            }
        });

        p2SpBtn4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ( event.getAction() == MotionEvent.ACTION_DOWN ) {
                    buttonEventFlag2.setBtnSp4(true);
                } else if ( event.getAction() == MotionEvent.ACTION_UP ) {
                    buttonEventFlag2.setBtnSp4(false);
                }
                return false;
            }
        });

    }

    //======================================================================================
    //======================================================================================
    //--  内部クラス
    //======================================================================================
    //======================================================================================


    //======================================================================================
    //--  ボタンイベントクラス
    //======================================================================================
    public static class ButtonEventFlag {

        //-- プレイヤー1
        private boolean btnLeft = false;
        private boolean btnRight = false;
        private boolean btnSmash = false;
        private boolean btnSp1 = false;
        private boolean btnSp2 = false;
        private boolean btnSp3 = false;
        private boolean btnSp4 = false;

        //======================================================================================
        //--  setter/getter
        //======================================================================================
        public boolean isBtnLeft() {
            return btnLeft;
        }

        public void setBtnLeft(boolean btnLeft) {
            this.btnLeft = btnLeft;
        }

        public boolean isBtnRight() {
            return btnRight;
        }

        public void setBtnRight(boolean btnRight) {
            this.btnRight = btnRight;
        }

        public boolean isBtnSmash() {
            return btnSmash;
        }

        public void setBtnSmash(boolean btnSmash) {
            this.btnSmash = btnSmash;
        }

        public boolean isBtnSp1() {
            return btnSp1;
        }

        public void setBtnSp1(boolean btnSp1) {
            this.btnSp1 = btnSp1;
        }

        public boolean isBtnSp2() {
            return btnSp2;
        }

        public void setBtnSp2(boolean btnSp2) {
            this.btnSp2 = btnSp2;
        }

        public boolean isBtnSp3() {
            return btnSp3;
        }

        public void setBtnSp3(boolean btnSp3) {
            this.btnSp3 = btnSp3;
        }

        public boolean isBtnSp4() {
            return btnSp4;
        }

        public void setBtnSp4(boolean btnSp4) {
            this.btnSp4 = btnSp4;
        }
    }
}
