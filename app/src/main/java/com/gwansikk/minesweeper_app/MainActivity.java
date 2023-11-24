package com.gwansikk.minesweeper_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    /*
     * 게임판의 크기와 총 지뢰 개수를 상수로 정의 (n*n)
     * 게임판의 크기를 변경하고자 하면 아래의 값을 변경하면 됩니다.
     */
    private final int GRID_SIZE = 9;

    /*
     * 총 지뢰의 수
     * 총 지뢰 개수를 변경하고자 하면 아래의 값을 변경하면 됩니다.
     */
    private final int TOTAL_MINES = 10;

    private TableLayout tableLayout; // 게임판
    private int flagCount = 0; // FLAG 개수
    private final BlockButton[][] buttons = new BlockButton[GRID_SIZE][GRID_SIZE]; // 버튼 배열
    private final boolean[][] mines = new boolean[GRID_SIZE][GRID_SIZE]; // 지뢰 배열
    private boolean isFlagMode = false; // 깃발 모드 여부

    // 타이머
    private TextView textViewTimer; // 타이머

    Handler timerHandler = new Handler();
    Runnable timerRunnable;
    long startTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  FLAG 깃발 모드를 토글 버튼으로 구현
        ToggleButton toggleButton = findViewById(R.id.modeSwitch);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> isFlagMode = isChecked);

        // 타이머
        textViewTimer = findViewById(R.id.timer);

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                textViewTimer.setText(String.format("⏱️ %02d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 500);
            }
        };

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);

        tableLayout = findViewById(R.id.tableLayout);
        setUpGame();
    }


    /*
     * 게임을 시작하기 위한 초기화 작업
     * 버튼 생성, 지뢰 배치, 주변 지뢰 개수 계산
     */
    private void setUpGame() {
        reset(); // 게임판을 초기화합니다.
        createButtons(); // 1. 버튼 생성
        placeMines(); // 2. 지뢰 배치
        calculateMinesAround(); // 3. 주변 지뢰 개수 계산
    }

    /*
     * 게임판을 초기화합니다.
     */
    private void reset() {
        tableLayout.removeAllViews();
        isFlagMode = false; // 깃발 모드 여부, 기본값인 false로 설정
        updateMinesCount(); // 인디케이터 지뢰 초기화
    }

    // 인디케이터의 지뢰 개수를 갱신합니다.
    @SuppressLint("DefaultLocale")
    private void updateMinesCount() {
        TextView minesCountTextview = findViewById(R.id.minesCount);
        minesCountTextview.setText(String.format("💣 %d", TOTAL_MINES - flagCount));
    }

    /*
     * 버튼을 생성하여 테이블에 배치합니다.
     */
    private void createButtons() {
        /*
         * 화면의 가로 길이를 구하여, 게임판의 크기로 나눠 버튼의 크기를 정합니다.
         * 이렇게 하면 화면의 크기에 따라 적절한 버튼의 크기가 조정됩니다.
         */
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int buttonSize = screenWidth / GRID_SIZE;

        // 테이블의 행을 생성하고, 각 행에 버튼을 추가합니다.
        for (int i = 0; i < GRID_SIZE; i++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < GRID_SIZE; j++) {
                buttons[i][j] = createButton(i, j, buttonSize);
                row.addView(buttons[i][j]);
            }
            tableLayout.addView(row);
        }
    }

    /*
     * 버튼을 생성하고, 버튼의 크기를 동적으로 조정
     */
    private BlockButton createButton(int x, int y, int buttonSize) {
        BlockButton button = new BlockButton(this);
        button.setLayoutParams(new TableRow.LayoutParams(buttonSize, buttonSize));
        button.setCoordinates(x, y); // 해당 블럭의 좌표를 저장합니다.
        button.setBlockClickListener(new BlockClickListener());
        return button;
    }

    /*
     * 지뢰를 랜덤하게 배치합니다.
     */
    private void placeMines() {
        int mineCount = 0;
        Random random = new Random();

        while (mineCount < TOTAL_MINES) {
            int x = random.nextInt(GRID_SIZE);
            int y = random.nextInt(GRID_SIZE);

            // 랜덤으로 지뢰를 배치하되, 이미 지뢰가 있는 곳은 배치하지 않습니다.
            if (!mines[x][y]) {
                mines[x][y] = true;
                buttons[x][y].setMine(true);
                mineCount++; // 지뢰를 배치한 후 지뢰의 개수를 증가시킵니다. TOTAL_MINES에 도달하게 되면 지뢰 배치를 종료합니다.
            }
        }
    }


    private void calculateMinesAround() {
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                if (!mines[x][y]) {
                    int mineCount = 0;

                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            int nx = x + dx;
                            int ny = y + dy;

                            if (nx >= 0 && ny >= 0 && nx < GRID_SIZE && ny < GRID_SIZE && mines[nx][ny]) {
                                mineCount++;
                            }
                        }
                    }

                    buttons[x][y].setMinesAround(mineCount);
                }
            }
        }
    }

    /*
     * 블럭을 클릭 했을 때의 동작을 정의합니다.
     * 깃발 모드일 경우에는 깃발을 토글합니다.
     * 깃발 모드가 아닐 경우에는 블럭을 엽니다.
     * 열린 블럭이 지뢰일 경우 게임 오버 처리합니다.
     */
    private class BlockClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            BlockButton blockButton = (BlockButton) view;

            if (isFlagMode) {
                // 깃발 모드일 경우 깃발을 토글합니다. (on-off)
                blockButton.toggleFlag();

                if (blockButton.isFlagged()) {
                    // 깃발 모드일 경우 지뢰 개수를 감소시킵니다.
                    flagCount++;
                } else {
                    // 깃발 모드가 아닐 경우 지뢰 개수를 증가시킵니다.
                    flagCount--;
                }

                updateMinesCount(); // 인디케이터 지뢰 개수 갱신
            } else {
                // 깃발 모드가 아닐 경우 블럭을 엽니다.
                handleBlockClick(blockButton);
            }
        }

        /*
         * 블럭을 클릭했을 때의 동작을 정의합니다.
         */
        private void handleBlockClick(BlockButton blockButton) {
            boolean isFlagged = blockButton.isFlagged();

            // 깃발 모드가 아닐 경우 블럭을 열지 못하도록 합니다.
            if (isFlagged) {
                return;
            }

            if (blockButton.isMine()) {
                // 해당 버튼이 지뢰일 경우 게임 오버 처리합니다.
                gameOver();
            } else {
                // 해당 버튼이 지뢰가 아닐 경우 주변 지뢰 개수를 표시합니다.
                // 지뢰 개수는 초기화 작업에서 미리 계산된 값을 표시하게 됩니다
                openBlock(blockButton);
            }
        }
    }


    private void openBlock(BlockButton blockButton) {
        // 이미 열린 블럭이거나 지뢰일 경우 동작하지 않습니다.
        if (blockButton.isMine() || !blockButton.isEnabled()) {
            return;
        }

        blockButton.setEnabled(false); // 버튼을 비활성화 합니다.
        blockButton.setOpenBackgroundColor(); // 버튼의 배경색을 변경합니다.
        int minesAround = blockButton.getMinesAround(); // 미리 계산된 지뢰 개수를 가져옵니다.

        if (minesAround > 0) {
            // 주변에 지뢰가 0개 이상일 경우
            // 주변 지뢰 개수를 표시합니다.
            blockButton.setShowText();
        } else {
            // 주변의 지뢰 갯수가 0일 경우 주변의 빈 블럭을 오픈합니다.
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = blockButton.getXCoordinate() + dx;
                    int ny = blockButton.getYCoordinate() + dy;

                    if (nx >= 0 && ny >= 0 && nx < GRID_SIZE && ny < GRID_SIZE && buttons[nx][ny].isEnabled()) {
                        openBlock(buttons[nx][ny]);
                    }
                }
            }
        }
    }


    /*
     * 게임 오버 처리
     * 게임 오버가 되면 지뢰의 위치를 보여주고, 재도전 또는 결과 보기를 선택할 수 있습니다.
     */
    private void gameOver() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 게임 오버 메시지를 설정합니다.
        builder.setTitle("☠️ Game Over!").setMessage("다시 도전하시겠습니까?");

        // 재도전, 게임을 초기화하여 재시작합니다.
        builder.setPositiveButton("재도전", (dialog, id) -> setUpGame());

        // 결과 보기, 지뢰의 위치를 보여줍니다.
        builder.setNegativeButton("결과 보기", (dialog, id) -> {
            Toast.makeText(getApplicationContext(), "지뢰의 위치가 보여집니다.", Toast.LENGTH_SHORT).show();

            // 지뢰의 위치를 보여줍니다.
            for (int x = 0; x < GRID_SIZE; x++) {
                for (int y = 0; y < GRID_SIZE; y++) {
                    if (buttons[x][y].isMine()) {
                        buttons[x][y].setText("💣️");
                    }
                }
            }
        });

        // 다이얼로그를 생성하고 보여줍니다.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}