/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * Фон ячейки в paved-режиме «Мешанины»: рамка только по заданным сторонам
 * (периметр плитки, см. {@link PavingMap#isOuterSide}) и число плитки,
 * масштабированное на всю площадь плитки (TP-15): каждая ячейка рисует один
 * и тот же рисунок с центром плитки — визуально число одно на всю плитку.
 */
public class PavedCellDrawable extends Drawable {

	private static final float REFERENCE_TEXT_SIZE = 100; // px, эталон для масштаба

	private final Paint borderPaint = new Paint();
	private final Paint numberPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final float borderWidth;

	private boolean west, north, east, south;

	private String numberText = "";
	private int numberColor = Color.TRANSPARENT;
	private int minRow, minCol, maxRow, maxCol;	// bounds плитки в ячейках
	private int row, col;						// ячейка, которой принадлежит drawable
	private int mode;							// -1 маленький, 0 максимальный, 1 плитка (stretch)

	public PavedCellDrawable(@ColorInt int borderColor, float borderWidth) {
		this.borderWidth = borderWidth;
		borderPaint.setColor(borderColor);
	}

	/** @param west левая сторона, north верхняя, east правая, south нижняя */
	public void setSides(boolean west, boolean north, boolean east, boolean south) {
		this.west = west;
		this.north = north;
		this.east = east;
		this.south = south;
		invalidateSelf();
	}

	/** Цвет рамки (подсветка плитки — TP-19: анимируется граница, символ не перекрывается) */
	public void setBorderColor(@ColorInt int color) {
		borderPaint.setColor(color);
		invalidateSelf();
	}

	/**
	 * Число плитки (TP-15): рисуется по центру плитки. Режимы масштаба:
	 * -1 — маленький: символ 1:1 с ячейкой (единый кегль для всех плиток);
	 * 0 — максимальный: вписать в плитку без искажения (во что упрётся);
	 * 1 — плитка: растяжка по площади плитки (искажение пропорций).
	 * @param tileBounds [minRow, minCol, maxRow, maxCol] — границы плитки в ячейках
	 * @param row, col — координаты ячейки, которой принадлежит drawable
	 */
	public void setNumber(String text, @ColorInt int color, int[] tileBounds, int row, int col, int mode) {
		this.numberText = text;
		this.numberColor = color;
		this.minRow = tileBounds[0];
		this.minCol = tileBounds[1];
		this.maxRow = tileBounds[2];
		this.maxCol = tileBounds[3];
		this.row = row;
		this.col = col;
		this.mode = mode;
		invalidateSelf();
	}

	@Override
	public void draw(Canvas canvas) {
		float w = getBounds().width();
		float h = getBounds().height();
		if (north) canvas.drawRect(0, 0, w, borderWidth, borderPaint);
		if (south) canvas.drawRect(0, h - borderWidth, w, h, borderPaint);
		if (west) canvas.drawRect(0, 0, borderWidth, h, borderPaint);
		if (east) canvas.drawRect(w - borderWidth, 0, w, h, borderPaint);

		if (!numberText.isEmpty() && numberColor != Color.TRANSPARENT && w > 0 && h > 0) {
			numberPaint.setColor(numberColor);
			numberPaint.setTextSize(REFERENCE_TEXT_SIZE);
			float textW = numberPaint.measureText(numberText);
			Paint.FontMetrics fm = numberPaint.getFontMetrics();
			float textH = fm.descent - fm.ascent;
			float tileW = (maxCol - minCol + 1) * w;
			float tileH = (maxRow - minRow + 1) * h;
			// центр плитки в локальных координатах этой ячейки
			float cx = (minCol + maxCol + 1) / 2f * w - col * w;
			float cy = (minRow + maxRow + 1) / 2f * h - row * h;
			canvas.save();
			canvas.translate(cx, cy);
			switch (mode) {
				case -1:	// норма: символ 1:1 с ячейкой по меньшей стороне (з1: не-квадратные ячейки)
					float small = Math.min(w, h) / textH;
					canvas.scale(small, small);
					break;
				case 0:		// максимальный: вписать в плитку без искажения (во что упрётся)
					float fit = Math.min(tileW / textW, tileH / textH);
					canvas.scale(fit, fit);
					break;
				default:	// 1 плитка: растяжка по площади плитки (искажение пропорций)
					canvas.scale(tileW / textW, tileH / textH);
			}
			canvas.drawText(numberText, -textW / 2, -(fm.ascent + fm.descent) / 2, numberPaint);
			canvas.restore();
		}
	}

	@Override
	public void setAlpha(int alpha) {
		borderPaint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(@Nullable ColorFilter colorFilter) {
		// not used
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}
}
