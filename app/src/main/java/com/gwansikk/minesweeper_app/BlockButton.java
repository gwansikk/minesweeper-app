package com.gwansikk.minesweeper_app;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;


public class BlockButton extends androidx.appcompat.widget.AppCompatButton {
    private int x, y; // 블록의 위치
    private boolean isMine; // 지뢰 여부
    private boolean isFlagged; // 깃발 표시 여부
    private int minesAround; // 주변 지뢰 개수, (게임 초기 설정 때 정해짐)

    public BlockButton(Context context) {
        super(context);
    }

    // 해당 블럭의 좌표를 설정합니다.
    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 해당 블럭의 x 좌표를 가져옵니다.
    public int getXCoordinate() {
        return x;
    }

    // 해당 블럭의 y 좌표를 가져옵니다.
    public int getYCoordinate() {
        return y;
    }

    // 해당 블럭의 지뢰 여부를 설정합니다.
    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    // 해당 블럭이 지뢰인지 확인합니다.
    public boolean isMine() {
        return isMine;
    }

    // 해당 블럭의 깃발 여부를 토글합니다.
    public void toggleFlag() {
        isFlagged = !isFlagged;
        if (isFlagged) {
            this.setText("⛳️");
        } else {
            this.setText("");
        }
    }

    // 해당 블럭의 깃발 여부를 가져옵니다.
    public boolean isFlagged() {
        return isFlagged;
    }

    // 해당 블럭의 주변 지뢰 개수를 설정합니다.
    public void setMinesAround(int minesAround) {
        this.minesAround = minesAround;
    }

    // 해당 블럭의 주변 지뢰 개수를 가져옵니다.
    public int getMinesAround() {
        return minesAround;
    }

    // 해당 블럭을 엽니다.
    public void setBlockClickListener(View.OnClickListener listener) {
        this.setOnClickListener(listener);
    }

    // 해당 블럭의 배경색을 변경합니다.
    public void setOpenBackgroundColor() {
        this.setBackgroundColor(Color.rgb(192,192,192));
    }

    // 해당 블럭의 텍스트를 설정합니다.
    public void setShowText() {
        this.setText(String.valueOf(minesAround)); // 텍스트를 주변의 지뢰 개수로 설정합니다.
        this.setTypeface(null, Typeface.BOLD); // 텍스트를 굵게 표시합니다.

        // 지뢰 개수에 따른 색상을 배열로 관리합니다.
        int[] colors = {Color.BLUE, Color.rgb(5,123,2), Color.RED, Color.MAGENTA, Color.YELLOW, Color.CYAN, Color.BLACK, Color.DKGRAY};

        // 지뢰 개수에 해당하는 색상을 설정합니다.
        // minesAround가 1부터 시작하기 때문에 배열 인덱스에 맞추기 위해 -1을 합니다.
        if (minesAround >= 1 && minesAround <= colors.length) {
            this.setTextColor(colors[minesAround - 1]);
        }

    }

}
