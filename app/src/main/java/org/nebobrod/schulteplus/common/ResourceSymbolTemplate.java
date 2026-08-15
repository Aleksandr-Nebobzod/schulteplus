/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.interpolateColors;
import static org.nebobrod.schulteplus.Utils.stringRepeat;
import static org.nebobrod.schulteplus.common.Const.*;

import org.nebobrod.schulteplus.R;

import java.util.ArrayList;

/**
 * Реализация SymbolTemplate через ресурсы Android (этап 1.5).
 * Логика перенесена из STable.
 */
public class ResourceSymbolTemplate implements SymbolTemplate {

	@Override
	public ArrayList<Object> buildTemplate(String symbolType, int size) {
		ArrayList<Object> template = new ArrayList<>();
		Object[] sourceArray;
		Object letter = "";
		int[] colorSourceArray;
		int[] colorArray;
		switch (symbolType) {
			case KEY_SYMBOL_TYPE_NUMBER_ROME:
				sourceArray = getRes().getStringArray(R.array.number_rome);
				for (int i = 0; i < size; i++) {
					// it takes values from sourceArray by circle (if size of source is not enough)
					template.add(sourceArray[i % (sourceArray.length)].toString().substring(5));
				}
				break;
			case KEY_SYMBOL_TYPE_LETTER_LATIN:
				sourceArray = getRes().getStringArray(R.array.letter_latin);
				for (int i = 0; i < size; i++) {
					// it takes values from sourceArray by circle (if size of source is not enough)
					letter = sourceArray[i % (sourceArray.length)] +
							stringRepeat(".", i / (sourceArray.length));
					template.add(letter);
				}
				break;
			case KEY_SYMBOL_TYPE_LETTER_CYRILLIC:
				sourceArray = getRes().getStringArray(R.array.letter_cyrillic);
				for (int i = 0; i < size; i++) {
					// it takes values from sourceArray by circle (if size of source is not enough)
					letter = sourceArray[i % (sourceArray.length)] +
							stringRepeat(".", i / (sourceArray.length));
					template.add(letter);
				}
				break;
			case KEY_SYMBOL_TYPE_LETTER_DEVANAGARI:
				sourceArray = getRes().getStringArray(R.array.letter_devanagari);
				for (int i = 0; i < size; i++) {
					// it takes values from sourceArray by circle (if size of source is not enough)
					// 1-st symbol for devanagari
					letter = ((String)sourceArray[i % (sourceArray.length)]).substring(0, 1) +
							stringRepeat(".", i / (sourceArray.length));
					template.add(letter);
				}
				break;
			case KEY_SYMBOL_TYPE_COLOR_RED:
				colorSourceArray = getRes().getIntArray(R.array.color_red);
				colorArray = interpolateColors(colorSourceArray[0], colorSourceArray[1], size);
				for (int i : colorArray) {
					template.add(i);
				}
				break;
			case KEY_SYMBOL_TYPE_COLOR_BLUE:
				colorSourceArray = getRes().getIntArray(R.array.color_blue);
				colorArray = interpolateColors(colorSourceArray[0], colorSourceArray[1], size);
				for (int i : colorArray) {
					template.add(i);
				}
				break;
			default: 	// KEY_SYMBOL_TYPE_NUMBER
				for (int i = 0; i < size; i++) {
					// it takes values from cicle
					template.add(i+1);
				}
		}
		return template;
	}
}
