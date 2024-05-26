/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.*;

/**
 * one more unsuccessful attempt of chatGPT
 */
public class TileFilling {
	public static void main(String[] args) {
		// Инициализация поля 10x10
		int[][] field = new int[10][10];
		Random rand = new Random();

		// Массив с номерами плиток
		List<Integer> tiles = new ArrayList<>();
		for (int i = 1; i <= 25; i++) {
			// Для приблизительно равного количества плиток каждого размера
			int count = i <= 5 ? 5 : i <= 10 ? 3 : 1;
			for (int j = 0; j < count; j++) {
				tiles.add(i);
			}
		}

		// Перемешиваем массив плиток
		Collections.shuffle(tiles);

		// Заполняем поле случайными плитками
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (field[i][j] == 0) {
					int idx = rand.nextInt(tiles.size());
					int tile = tiles.get(idx);
					int width = (tile >= 2 && tile <= 5) || (tile >= 12 && tile <= 15) || (tile >= 18 && tile <= 20) ? 2 : 1;
					int height = (tile >= 3 && tile <= 5) || (tile >= 9 && tile <= 15) || (tile >= 19 && tile <= 25) ? 2 : 1;

					// Проверка на возможность размещения плитки
					if (i + height <= 10 && j + width <= 10) {
						for (int h = i; h < i + height; h++) {
							for (int w = j; w < j + width; w++) {
								field[h][w] = tile;
							}
						}
					}
					tiles.remove(idx);
					if (idx < tiles.size()) {
						tiles.add(idx, 0);
					} else {
						tiles.add(0);
					}
				}
			}
		}

		// Вывод итогового поля
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (field[i][j] != 0) {
					System.out.print(String.format("%02d", field[i][j]) + "\t");
				} else {
					System.out.print("--\t");
				}
			}
			System.out.println();
		}
	}
}
