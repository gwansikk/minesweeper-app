package com.gwansikk.minesweeper_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TableLayout tableLayout;
    private final int GRID_SIZE = 9;
    private final int TOTAL_MINES = 10;
    private BlockButton[][] buttons = new BlockButton[GRID_SIZE][GRID_SIZE];
    private boolean[][] mines = new boolean[GRID_SIZE][GRID_SIZE];
    private boolean isFlagMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButton toggleButton = findViewById(R.id.modeSwitch);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> isFlagMode = isChecked);

        tableLayout = findViewById(R.id.tableLayout);
        setUpGame();
    }


    private void setUpGame() {
        createButtons();
        placeMines();
        calculateMinesAround();
    }

    private void createButtons() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int buttonSize = screenWidth / GRID_SIZE;

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


    private BlockButton createButton(int x, int y, int buttonSize) {
        BlockButton button = new BlockButton(this);
        button.setLayoutParams(new TableRow.LayoutParams(buttonSize, buttonSize));
        button.setCoordinates(x, y);
        button.setBlockClickListener(new BlockClickListener());
        return button;
    }

    private void placeMines() {
        int mineCount = 0;
        Random random = new Random();

        while (mineCount < TOTAL_MINES) {
            int x = random.nextInt(GRID_SIZE);
            int y = random.nextInt(GRID_SIZE);

            if (!mines[x][y]) {
                mines[x][y] = true;
                buttons[x][y].setMine(true);
                mineCount++;
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


    private class BlockClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            BlockButton blockButton = (BlockButton) view;
            if (isFlagMode) {
                blockButton.toggleFlag();
            } else {
                handleBlockClick(blockButton);
            }
        }

        private void handleBlockClick(BlockButton blockButton) {
            if (blockButton.isFlagged()) {
                return;
            }

            if (blockButton.isMine()) {
                gameOver();
            } else if (!blockButton.isFlagged()) {
                openBlock(blockButton);
            }
        }
    }


    private void openBlock(BlockButton blockButton) {
        if (blockButton.isMine() || !blockButton.isEnabled()) {
            return;
        }

        blockButton.setEnabled(false);
        blockButton.setOpenBackgroundColor();
        int minesAround = blockButton.getMinesAround();

        if (minesAround > 0) {
            blockButton.setText(String.valueOf(minesAround));
        } else {
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


    private void gameOver() {
        Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show();
    }
}