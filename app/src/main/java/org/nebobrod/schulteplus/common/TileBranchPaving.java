/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

import java.util.*;

/**
 * ERROR java.lang.StackOverflowError
 * at org.nebobrod.schulteplus.common.TileBranchPaving.backtrack(TileBranchPaving.java:58)
 */
public class TileBranchPaving {

	private static final int ROWS = 10;
	private static final int COLS = 10;
	private static final int TOTAL_TILES = 25;
	private static final int AVERAGE_TILE_SQUARE = ROWS * COLS / TOTAL_TILES;
	private static final int[][] TILE_SIZES = {{2, 2}, {1, 2}, {2, 1}, {2, 3}, {3, 2}, {1, 4}, {4, 1}};

	private static int[][] bestField;
	private static int minEmptySpaces = Integer.MAX_VALUE;

	public static void main(String[] args) {
		bestField = new int[ROWS][COLS];
		generateField();
		printField(bestField);
	}

	static void generateField() {
		int[][] field = new int[ROWS][COLS];
		List<int[]> tiles = generateTiles();
		backtrack(field, tiles, 0, 0, 0, new ArrayList<>());
	}

	static void backtrack(int[][] field, List<int[]> tiles, int row, int col, int emptySpaces, List<int[]> selectedTiles) {
		if (emptySpaces >= minEmptySpaces) {
			return; // Прекращаем поиск, если текущее количество пустых мест уже больше, чем минимальное
		}
		if (row == ROWS) {
			// Достигли конца поля, проверяем текущее решение
			if (emptySpaces < minEmptySpaces) {
				minEmptySpaces = emptySpaces;
				bestField = copyField(field);
			}
			return;
		}
		if (col == COLS) {
			// Переходим на следующую строку
			backtrack(field, tiles, row + 1, 0, emptySpaces, selectedTiles);
			return;
		}
		// Пробуем уложить каждую доступную плитку
		for (int[] tile : tiles) {
			if (canPlace(field, row, col, tile)) {
				List<int[]> updatedTiles = new ArrayList<>(tiles);
				updatedTiles.remove(tile);
				List<int[]> updatedSelectedTiles = new ArrayList<>(selectedTiles);
				updatedSelectedTiles.add(new int[]{tile[0], row, col});
				placeTile(field, row, col, tile);
				int newEmptySpaces = calculateEmptySpaces(field);
				backtrack(field, updatedTiles, row, col + tile[1], newEmptySpaces, updatedSelectedTiles);
				removeTile(field, row, col, tile);
			}
		}
		// Пропускаем ячейку и переходим к следующей
		backtrack(field, tiles, row, col + 1, emptySpaces + 1, selectedTiles);
	}

	static boolean canPlace(int[][] field, int row, int col, int[] tile) {
		if (row + tile[1] > ROWS || col + tile[2] > COLS) {
			return false; // Плитка не поместится в поле
		}
		for (int i = row; i < row + tile[1]; i++) {
			for (int j = col; j < col + tile[2]; j++) {
				if (field[i][j] != 0) {
					return false; // Есть перекрытие с другой плиткой
				}
			}
		}
		return true;
	}

	static void placeTile(int[][] field, int row, int col, int[] tile) {
		for (int i = row; i < row + tile[1]; i++) {
			for (int j = col; j < col + tile[2]; j++) {
				field[i][j] = tile[0];
			}
		}
	}

	static void removeTile(int[][] field, int row, int col, int[] tile) {
		for (int i = row; i < row + tile[1]; i++) {
			for (int j = col; j < col + tile[2]; j++) {
				field[i][j] = 0;
			}
		}
	}

	static int[][] copyField(int[][] field) {
		int[][] copy = new int[ROWS][COLS];
		for (int i = 0; i < ROWS; i++) {
			System.arraycopy(field[i], 0, copy[i], 0, COLS);
		}
		return copy;
	}

	/**
	 * @return tiles Shuffled list. Each tile has: <p> <code>int[3]</code> 0 num, 1 rows down, 2 cols right
	 */
	static List<int[]> generateTiles() {
		List<int[]>tiles = new ArrayList<>();
		int spaceDelta = 0;
		for (int i = 0; i < TOTAL_TILES; i++) {
			int[] tileSize = selectTileSize(spaceDelta);
			spaceDelta = tileSize[0] * tileSize[1] - (AVERAGE_TILE_SQUARE - spaceDelta);

			int[] element = new int[3];
			element[0] = i + 1;
			element[1] = tileSize[0];
			element[2] = tileSize[1];

			tiles.add(element);
		}

//		tiles.add(new int[]{25, 2, 2});

//		Collections.shuffle(tiles);
		int sum = 0;
		System.out.println("Tiles left: " + tiles.size());
		for (int i = 0; i < tiles.size(); i++) {
			sum += tiles.get(i)[1] * tiles.get(i)[2];
			System.out.println(Arrays.toString(tiles.get(i)));
		}
		System.out.println("sum of squares: " + sum);
		return tiles;
	}

	static int[] selectTileSize(int spaceDelta) {
		List<int[]> tileSizes = new ArrayList<>(Arrays.asList(TILE_SIZES));

		// normal situation
		if (spaceDelta == 0) {
			return tileSizes.get(randomInt(0, tileSizes.size()-1));
		}

		// lack of space or extra space requires equation
		Collections.shuffle(tileSizes);
		for (int[] size : tileSizes) {
			if (size[0] * size[1] + spaceDelta == AVERAGE_TILE_SQUARE) {
				return size;
			}
		}
		return TILE_SIZES[0]; // default size
	}

	private static int randomInt(int min, int max) {
		return (int) (Math.random() * (max - min + 1) + min);
	}

	static int calculateEmptySpaces(int[][] field) {
		int count = 0;
		for (int[] row : field) {
			for (int cell : row) {
				if (cell == 0) {
					count++;
				}
			}
		}
		return count;
	}

	static void printField(int[][] field) {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				System.out.printf("[%02d]", field[i][j]);
			}
			System.out.println();
		}
	}
}
