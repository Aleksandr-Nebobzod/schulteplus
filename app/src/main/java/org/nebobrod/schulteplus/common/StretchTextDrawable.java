/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * Текст ячейки в режиме кегля «плитка» (1): символ растягивается на всю ячейку
 * с искажением пропорций — canvas.scale по ширине/высоте ячейки независимо
 * («1» станет искажённо широкой, двоичные коды — «забором»; без измерений).
 */
public class StretchTextDrawable extends Drawable {

	private static final float REFERENCE_TEXT_SIZE = 100; // px, эталон масштаба

	private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final String text;

	public StretchTextDrawable(String text, @ColorInt int color) {
		this.text = text;
		textPaint.setColor(color);
	}

	@Override
	public void draw(Canvas canvas) {
		float w = getBounds().width();
		float h = getBounds().height();
		if (text.isEmpty() || w <= 0 || h <= 0) return;
		textPaint.setTextSize(REFERENCE_TEXT_SIZE);
		float textW = textPaint.measureText(text);
		Paint.FontMetrics fm = textPaint.getFontMetrics();
		float textH = fm.descent - fm.ascent;
		canvas.save();
		canvas.translate(w / 2, h / 2);
		canvas.scale(w / textW, h / textH);
		canvas.drawText(text, -textW / 2, -(fm.ascent + fm.descent) / 2, textPaint);
		canvas.restore();
	}

	@Override
	public void setAlpha(int alpha) {
		textPaint.setAlpha(alpha);
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
