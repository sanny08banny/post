package com.example.sammwangi.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.sammwangi.DAOs.PostItem;

import java.util.List;

public class HistogramView extends View {
    private List<PostItem> postItems; // Your list of PostItem objects

    public HistogramView(Context context) {
        super(context);
    }

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HistogramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPostItems(List<PostItem> postItems) {
        this.postItems = postItems;
        invalidate(); // Request a redraw when the data changes
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (postItems == null || postItems.isEmpty()) {
            return;
        }

        int canvasWidth = getWidth();
        int canvasHeight = getHeight();

        // Draw the histogram bars
        Paint barPaint = new Paint();
        barPaint.setColor(Color.BLUE);
        barPaint.setStyle(Paint.Style.FILL);

        int barCount = postItems.size();
        int barWidth = canvasWidth / barCount;
        int maxPostAmount = getMaxPostAmount();
        float scaleFactor = (float) canvasHeight / maxPostAmount;

        for (int i = 0; i < barCount; i++) {
            PostItem postItem = postItems.get(i);
            int barHeight = (int) (postItem.getPostAmount() * scaleFactor);
            int left = i * barWidth;
            int right = left + barWidth;
            int top = canvasHeight - barHeight;
            int bottom = canvasHeight;
            canvas.drawRect(left, top, right, bottom, barPaint);
        }

        // Draw axis lines
        Paint axisPaint = new Paint();
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(2f);

        canvas.drawLine(0, canvasHeight, canvasWidth, canvasHeight, axisPaint); // X-axis
        canvas.drawLine(0, 0, 0, canvasHeight, axisPaint); // Y-axis (vertical line)
    }

    private int getMaxPostAmount() {
        int maxAmount = 0;
        for (PostItem postItem : postItems) {
            if (postItem.getPostAmount() > maxAmount) {
                maxAmount = postItem.getPostAmount();
            }
        }
        return maxAmount;
    }
}
