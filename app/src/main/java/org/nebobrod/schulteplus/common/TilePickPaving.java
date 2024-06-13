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
 * ERROR -- 3-7 tiles can't be placed
 */
public class TilePickPaving {
	private static final int ROWS = 10;
	private static final int COLS = 10;
	private static final int TOTAL_TILES = 25;
	private static final int AVERAGE_TILE_SQUARE = ROWS * COLS / TOTAL_TILES;
	private static final int[][] TILE_SIZES = {{2, 2}, {1, 2}, {2, 1}, {2, 3}, {3, 2}, {1, 4}, {4, 1}};

	static List<int[]> tiles;
	static int[][] bestField;


	public static void main(String[] args) {
		tiles = generateTiles();
		bestField = new int[ROWS][COLS];
		int[][] field = generateRandomField(ROWS * COLS);
		printField(field);
	}

	/**
	 * @return {@link #tiles} Shuffled list. Each tile has: <p> <code>int[3]</code> 0 num, 1 rows down, 2 cols right
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

	static int[][] generateRandomField(int freeSpace) {
		int[][] field = new int[ROWS][COLS];
		Random random = new Random();
		int[] tile = new int[3];
		int tileIndex;

		for (int i = 0; i < ROWS-1; i++) {
			for (int j = 0; j < COLS-1; j++) {
				if (tiles.size() < 1) {return field;}
				tileIndex = random.nextInt(tiles.size());
				tile = tiles.get(tileIndex);
				System.out.println(i + "/" + j + "rem: " + calculateRemainingPlacements(field, tile));

				if (canPlace(field, i, j, tile)) {
					placeTile(field, i, j, tile);
					System.out.println("placed: " + Arrays.toString(tile));
					tiles.remove(tileIndex);
				}
			}
		}

		return field;
	}

	static int calculateRemainingPlacements(int[][] field, int[] tile) {
		int remainingPlacements = 0;
		for (int i = 0; i < ROWS - tile[1] + 1; i++) {
			for (int j = 0; j < COLS - tile[2] + 1; j++) {
				if (canPlace(field, i, j, tile)) {
					remainingPlacements++;
				}
			}
		}
		return remainingPlacements;
	}

	static boolean canPlace(int[][] field, int row, int col, int[] tile) {
		if ((row + tile[1] > ROWS) || (col + tile[2] > COLS)) {
			return false;
		}
		for (int i = row; i < row + tile[1]; i++) {
			for (int j = col; j < col + tile[2]; j++) {
				if (field[i][j] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	static void placeTile(int[][] field, int row, int col, int[] tile) {
		for (int i = row; i < row + tile[1]; i++) {
			for (int j = col; j < col + tile[2]; j++) {
				field[i][j] = tile[0]; // Пока что просто заполняем ячейки единицами, потому что нам нужны только размеры плиток
			}
		}
	}

	private static int randomInt(int min, int max) {
		return (int) (Math.random() * (max - min + 1) + min);
	}

	static void printField(int[][] field) {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				System.out.printf("[%02d]", field[i][j]);
			}
			System.out.println();
		}

		System.out.println("Tiles left: " + tiles.size());
		for (int i = 0; i < tiles.size(); i++) {
			System.out.println(Arrays.toString(tiles.get(i)));
		}
	}
}
