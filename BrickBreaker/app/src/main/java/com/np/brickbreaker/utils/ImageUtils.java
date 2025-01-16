package com.np.brickbreaker.utils;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Rect;

public class ImageUtils {

    // This method takes a Drawable and crops it into a grid with the specified rows and columns.
    public static Bitmap[] cropDrawableIntoGrid(Drawable drawable, int rows, int cols) {
        // Step 1: Convert Drawable to Bitmap
        Bitmap bitmap = drawableToBitmap(drawable);

        // Step 2: Calculate the width and height of each grid cell
        int cellWidth = bitmap.getWidth() / cols;
        int cellHeight = bitmap.getHeight() / rows;

        // Step 3: Create an array to store each cropped Bitmap
        Bitmap[] bitmaps = new Bitmap[rows * cols];

        // Step 4: Loop through each cell in the grid and crop the bitmap
        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Define the area (Rect) for the current cell
                Rect cropRect = new Rect(col * cellWidth, row * cellHeight,
                        (col + 1) * cellWidth, (row + 1) * cellHeight);

                // Crop the Bitmap and store it in the array
                bitmaps[index] = Bitmap.createBitmap(bitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height());
                index++;
            }
        }

        return bitmaps;  // Return the array of cropped bitmaps
    }

    // Utility method to convert a Drawable to a Bitmap
    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            // If the drawable is already a BitmapDrawable, return the Bitmap directly
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // Otherwise, create a new Bitmap and draw the Drawable onto it
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}