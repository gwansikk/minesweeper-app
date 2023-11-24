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
    private int openCount = 0; // 열린 블럭 개수
    private BlockButton[][] buttons; // 버튼 배열
    private boolean[][] mines; // 지뢰 배열
    private boolean isFlagMode = false; // 깃발 모드 여부

    // 타이머
    private TextView textViewTimer;

    Handler timerHandler = new Handler();
    Runnable timerRunnable;
    long startTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tableLayout = findViewById(R.id.tableLayout);

        // 초기화 버튼
        findViewById(R.id.resetButton).setOnClickListener(v -> setUpGame());

        //  MODE 토글 버튼
        ToggleButton toggleButton = findViewById(R.id.modeSwitch);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> isFlagMode = isChecked);

        // 타이머 설정
        textViewTimer = findViewById(R.id.timer);

        timerRunnable = new Runnable() {
            @SuppressLint("DefaultLocale")
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                seconds = seconds % 60;

                textViewTimer.setText(String.format("⏱️ %03d", seconds));
                timerHandler.postDelayed(this, 500);
            }
        };

        // 게임 시작
        setUpGame();
    }


    // 게임을 시작하기 위한 초기화 작업
    private void setUpGame() {
        gameInit(); // 1. 게임 초기 설정
        createButtons(); // 2. 버튼 생성
        placeMines(); // 3. 지뢰 배치
        calculateMinesAround(); // 4. 주변 지뢰 개수 계산
        updateMinesCount(); // 5. 상태바 지뢰 초기화
        setUpTimer(); // 6. 상태바 타이머 초기화
    }

    // 게임 시작을 위해 초기 설정을 합니다.
    private void gameInit() {
        tableLayout.removeAllViews(); // 게임판 초기화
        buttons = new BlockButton[GRID_SIZE][GRID_SIZE]; // 버튼 배열 초기화
        mines = new boolean[GRID_SIZE][GRID_SIZE]; // 지뢰 배열 초기화
        isFlagMode = false; // 깃발 모드 여부, 기본값인 false로 설정
        flagCount = 0; // 깃발 개수 초기화
        openCount = 0; // 열린 블럭 개수 초기화
    }

    // 타이머를 초기화하고 시작합니다.
    private void setUpTimer() {
        timerHandler.removeCallbacks(timerRunnable);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
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


    // 버튼을 생성하고, 버튼의 크기를 동적으로 조정합니다.
    private BlockButton createButton(int x, int y, int buttonSize) {
        BlockButton button = new BlockButton(this);
        button.setLayoutParams(new TableRow.LayoutParams(buttonSize, buttonSize)); // 버튼의 크기를 동적으로 조정합니다.
        button.setCoordinates(x, y); // 해당 블럭의 좌표를 저장합니다.
        button.setBlockClickListener(new BlockClickListener()); // 블럭을 클릭했을 때의 동작을 정의합니다.
        return button;
    }

    // 지뢰를 랜덤 배치합니다.
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

    // 주변 지뢰 개수를 계산합니다.
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


    // 블럭을 여는 동작을 정의합니다.
    private void openBlock(BlockButton blockButton) {
        // 이미 열린 블럭이거나 지뢰일 경우 동작하지 않습니다.
        if (blockButton.isMine() || !blockButton.isEnabled()) {
            return;
        }

        openCount++; // 열린 블럭 개수를 증가시킵니다.
        blockButton.setEnabled(false); // 버튼을 비활성화 합니다.
        blockButton.setOpenBackgroundColor(); // 버튼의 배경색을 변경합니다.
        int minesAround = blockButton.getMinesAround(); // 미리 계산된 지뢰 개수를 가져옵니다.

        if (openCount == GRID_SIZE * GRID_SIZE - TOTAL_MINES) {
            // 열린 블럭 개수가 총 블럭 개수에서 지뢰 개수를 뺀 값과 같을 경우
            // 모든 블럭을 열었으므로 게임 승리 처리합니다.
            gameWin();
            return;
        }

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
     * 게임 승리 처리
     * 모든 블럭을 열었을 경우 게임 승리 처리합니다.
     */
    private void gameWin() {
        timerHandler.removeCallbacks(timerRunnable); // 타이머를 종료합니다.

        AlertDialog.Builder builder = new AlertDialog.Builder(this); // 다이얼로그를 생성합니다.

        builder.setTitle("🎉 Game Win!").setMessage("축하합니다!" + "\n" + "걸린 시간: " + textViewTimer.getText() + "초"); // 게임 승리 메시지를 설정합니다.
        builder.setPositiveButton("재시작", (dialog, id) -> setUpGame()); // 재시작, 게임을 초기화하여 재시작합니다.

        // 다이얼로그를 생성하고 보여줍니다.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /*
     * 게임 오버 처리
     * 게임 오버가 되면 지뢰의 위치를 보여주고, 재도전 또는 결과 보기를 선택할 수 있습니다.
     */
    private void gameOver() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("☠️ Game Over!").setMessage("다시 도전하시겠습니까?"); // 게임 오버 메시지를 설정합니다.
        builder.setPositiveButton("재도전", (dialog, id) -> setUpGame()); // 재도전, 게임을 초기화하여 재시작합니다.

        // 결과 보기, 지뢰의 위치를 보여줍니다.
        builder.setNegativeButton("지뢰 보기", (dialog, id) -> {
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