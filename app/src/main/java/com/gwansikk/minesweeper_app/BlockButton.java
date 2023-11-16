package com.gwansikk.minesweeper_app;


import android.content.Context;
import android.graphics.Color;
import android.view.View;


public class BlockButton extends androidx.appcompat.widget.AppCompatButton {
    private int x, y; // 블록의 위치
    private boolean isMine; // 지뢰 여부
    private boolean isFlagged; // 깃발 표시 여부
    private int minesAround;

    public BlockButton(Context context) {
        super(context);
    }

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getXCoordinate() {
        return x;
    }

    public int getYCoordinate() {
        return y;
    }

    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    public boolean isMine() {
        return isMine;
    }

    public void toggleFlag() {
        isFlagged = !isFlagged;
        if (isFlagged) {
            this.setText("F");
        } else {
            this.setText("");
        }
    }

    public boolean isFlagged() {
        return isFlagged;
    }

    public void setMinesAround(int minesAround) {
        this.minesAround = minesAround;
    }

    public int getMinesAround() {
        return minesAround;
    }

    public void setBlockClickListener(View.OnClickListener listener) {
        this.setOnClickListener(listener);
    }

    public void setOpenBackgroundColor() {
        this.setBackgroundColor(Color.LTGRAY);
    }

}
