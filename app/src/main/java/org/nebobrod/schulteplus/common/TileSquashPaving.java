/*
 * Copyright (c) 2024  "Smart Rovers"
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;




import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TileSquashPaving {
	/** Allowed [0]Row X [1]Cols */
	private static final boolean DEBUG_SETTINGS = false;
	static final int[][] TILE_SIZES = {{2, 2},
			{1, 2}, {2, 1}, {2, 3}, {3, 2},
			{1, 3}, {3, 1}, {1, 4}, {4, 1}, {1, 1}};
	static final int[] TILE_QUANTITIES = new int[TILE_SIZES.length];
	private static final int ROWS = 5;
	private static final int COLS = 5;
	private static final int TILE_NUMBER = ROWS * COLS;

	static List<Tile> tiles;

	static int[][] field = new int[ROWS *2][COLS *2];
	static int _cell;
	static int height = field.length;
	static int width = field[0].length;
	/** elements meaning 1: right, 2: down, 3: left, 4: up */
	static List<Integer> validSides;

	public static void main(String[] args) {

		// Init tile-list
		TileSquashPaving tilePaving = new TileSquashPaving();
//		tiles = Collections.nCopies(TILE_NUMBER, tilePaving.new Tile()); 	// UnsupportedOperationException
		tiles = new ArrayList<>();
		for (int i = 0; i < TILE_NUMBER; i++) {
			tiles.add(tilePaving.new Tile());
		}

		// Field with squared tiles 2x2
		newField();

		int cycle = 0;
		while ((diversity10x() > 20) && (cycle <= 39)) {
			// run Tightness
			for (Tile t : tiles) {
				int dir = (randomInt(0, 1) * 2) - 1; 	// -1 or 1
				int side = randomInt(0, 3);
				/* debug set */
				if (DEBUG_SETTINGS) {

				/*{
					dir = 1;
					side = cycle / 10;
				}*/
					dir = -1;
					side = 2;
					if (t.num != 5) continue;

					System.out.println("for: " + t);
					System.out.println("dir " + dir + ", side " + side);
				}	/* debug set finished */

				List<Integer> dep = t.canMove(dir, side);
				if (dep == null) {if (DEBUG_SETTINGS) {System.out.println("X can't move");} continue;}
				dep = dependCheck(dir, side, dep, t.num);
				if (dep == null) {if (DEBUG_SETTINGS) {System.out.println("X can't move others");} continue;}
				if (DEBUG_SETTINGS) {printField();}
				dependMove(dir, side, t.canMove(dir, side), t.num);

				if (printZeroesField()) {
					int a =1;
				}
			}
			System.out.println("Cycle " + cycle++ + " DIVERSITY: " + diversity10x() + ", " + Arrays.toString(TILE_QUANTITIES));
			if (DEBUG_SETTINGS) {if ((cycle % 10) == 9) printField();}
		}

		// Final Report
		printField();
	}


	/////////////////////////////////////
	private class Tile {
		int num;            // number or other kind of symbol on the tile
		int rowAddress;        // y-coordinate of top-left cell
		int colAddress;        // x-coordinate of top-left cell
		int size;            // From the TILE_SIZES[]
		// field[][] is used


/*	public Tile(int num, int rowAddress, int colAddress, int size) {
		this.num = num;
		this.rowAddress = rowAddress;
		this.colAddress = colAddress;
		this.size = size;
	}*/

		/**
		 * Set the tile and stamp it on the field
		 * @param num number or other kind of symbol on the tile
		 * @param rowAddress y-coordinate of top-left cell
		 * @param colAddress x-coordinate of top-left cell
		 * @param size From the TILE_SIZES[] (0 means 2x2)
		 */
		public void set(int num, int rowAddress, int colAddress, int size) {
			this.num = num;
			this.rowAddress = rowAddress;
			this.colAddress = colAddress;
			if (this.size != size) {
				TILE_QUANTITIES[this.size]--;
				TILE_QUANTITIES[size]++;
				this.size = size;
			}
			stamp();
		}

		@Override
		public String toString() {
			return "Tile{" +
					"num=" + num +
					", rowAddress=" + rowAddress +
					", colAddress=" + colAddress +
					", size=" + size +
					'}';
		}

		private void stamp() {

			try {
				for (int row = rowAddress; row < rowAddress + TILE_SIZES[size][0]; row++) {
					for (int col = colAddress; col < colAddress + TILE_SIZES[size][1]; col++) {
						if ((field[row][col] == 0) || (field[row][col] == num)) {
							field[row][col] = num;                            // take a space
							_cell = field[row][col];
						} else {
							System.out.println("no space for " + this);
							System.out.println("field["+row+"]["+col+"] == " + field[row][col]);
							printField();
							throw new RuntimeException("NO SPACE TO STAMP!");        // Check if not clean
						}
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Stamp is wrong -- " + this);
				printField();
				throw new RuntimeException(e);
			}
		}
		private void setFieldFree() {

			for (int row = rowAddress; row < rowAddress + TILE_SIZES[size][0]; row++) {
				for (int col = colAddress; col < colAddress + TILE_SIZES[size][1]; col++) {
					if ((field[row][col] == num) || (field[row][col] == 0) || true) {
						field[row][col] = 0;                            // set free a space
					} else {
						System.out.println(this.toString());
						printField();
						throw new RuntimeException("NO FIELD!");        // Check if not clean
					}
				}
			}
			if (DEBUG_SETTINGS) {System.out.println("cleaned space " + this.toString());}
		}

		/** Check borders and self-ability to move into new size
		 *
		 * @param dir minus or plus 1 of
		 * @param side 0 East, 1 South, 2 West, 3 North
		 * @return list of neighbours should be moved also (before) excluding `this`
		 */
		List<Integer> canMove (int dir, int side) {
			int val = 0;
			int[] newSizeArrow = TILE_SIZES[size].clone();
			int newSize;
			List<Integer>  can = new ArrayList<>();

			try {
				switch (side) {
					case 0:
						val = field[rowAddress][colAddress + TILE_SIZES[size][1] + dir];
						newSizeArrow[1]+=dir;
						break;
					case 1:
						val = field[rowAddress + TILE_SIZES[size][0] + dir][colAddress];
						newSizeArrow[0]+=dir;
						break;
					case 2:
						val = field[rowAddress][colAddress + dir];
						newSizeArrow[1]-=dir;
						break;
					case 3:
						val = field[rowAddress + dir][colAddress];
						newSizeArrow[0]-=dir;
						break;
					default:
						val = 0;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				can = null;
				return can;
			}

			newSize = getTileSize(newSizeArrow);
			if (newSize == -1) {
				can = null;
			} else {
				can = getDepends(dir, side, this.num);
			}
			return can;
		}

		/**
		 * @param dir
		 * @param side
		 * @return set of movable tiles behind the 'side' can move their sides (opposite one) to dir
		 */
		List<Integer> canMoveOthers (int dir, int side, int exclude) {
			List<Integer>  can = new ArrayList<>();
			int checkNum = 0;
			try {
				switch (side) {
					case 0:
						for (int row = rowAddress; row < rowAddress + TILE_SIZES[size][0]; row++) {
							checkNum = field[row][colAddress + TILE_SIZES[size][1] + 1];
							if ((checkNum != num) && (checkNum != exclude)) {
								List<Integer> newDependencies = tiles.get(checkNum-1).canMove(dir, getOpposite(side));
								if (!newDependencies.isEmpty()) {
									if (!can.contains(checkNum-1)) {
										can.add(checkNum-1);
									}
								} else {
									can.clear();
									break;
								}
							}
						}
						break;
					case 1:
						for (int col = colAddress; col < colAddress + TILE_SIZES[size][1]; col++) {
							checkNum = field[rowAddress + TILE_SIZES[size][0] + 1][col];
							if ((checkNum != num) && (checkNum != exclude)) {
								List<Integer> newDependencies = tiles.get(checkNum-1).canMove(dir, getOpposite(side));
								if (!newDependencies.isEmpty()) {
									if (!can.contains(checkNum-1)) {
										can.add(checkNum-1);
									}
								} else {
									can.clear();
									break;
								}
							}
						}
						break;
					case 2:
						for (int row = rowAddress; row < rowAddress + TILE_SIZES[size][0]; row++) {
							checkNum = field[row][colAddress - 1];
							if ((checkNum != num) && (checkNum != exclude)) {
								List<Integer> newDependencies = tiles.get(checkNum-1).canMove(dir, getOpposite(side));
								if (!newDependencies.isEmpty()) {
									if (!can.contains(checkNum-1)) {
										can.add(checkNum-1);
									}
								} else {
									can.clear();
									break;
								}
							}
						}
						break;
					case 3:
						for (int col = colAddress; col < colAddress + TILE_SIZES[size][1]; col++) {
							checkNum = field[rowAddress - 1][col];
							if ((checkNum != num) && (checkNum != exclude)) {
								List<Integer> newDependencies = tiles.get(checkNum-1).canMove(dir, getOpposite(side));
								if (!newDependencies.isEmpty()) {
									if (!can.contains(checkNum-1)) {
										can.add(checkNum-1);
									}
								} else {
									can.clear();
									break;
								}
							}
						}
						break;
					default:
						can.clear();
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println(this + " with CheckNum" + checkNum + " rise e: " + e.getLocalizedMessage());
			}

			return can;
		}

		/**
		 *
		 * @param dir
		 * @param side
		 * @param asker 0 for self-request, initiator.num for recursive call
		 * @return
		 */
		List<Integer> getDepends (int dir, int side, int asker) {
			if (asker == 0) {asker = this.num;}
			List<Integer>  result = new ArrayList<>();
			int checkNum = 0;
			try {
				switch (side) {
					case 0:
						for (int row = rowAddress; row < rowAddress + TILE_SIZES[size][0]; row++) {
							checkNum = field[row][colAddress + TILE_SIZES[size][1]];
							if ((checkNum != 0) && (checkNum != num) && (checkNum != asker)) {
								if (!result.contains(checkNum)) {
									result.add(checkNum);
								}
							}
						}
						break;
					case 1:
						for (int col = colAddress; col < colAddress + TILE_SIZES[size][1]; col++) {
							checkNum = field[rowAddress + TILE_SIZES[size][0]][col];
							if ((checkNum != 0) && (checkNum != num) && (checkNum != asker)) {
								if (!result.contains(checkNum)) {
									result.add(checkNum);
								}
							}
						}
						break;
					case 2:
						for (int row = rowAddress; row < rowAddress + TILE_SIZES[size][0]; row++) {
							checkNum = field[row][colAddress - 1];
							if ((checkNum != 0) && (checkNum != num) && (checkNum != asker)) {
								if (!result.contains(checkNum)) {
									result.add(checkNum);
								}
							}
						}
						break;
					case 3:
						for (int col = colAddress; col < colAddress + TILE_SIZES[size][1]; col++) {
							checkNum = field[rowAddress - 1][col];
							if ((checkNum != 0) && (checkNum != num) && (checkNum != asker)) {
								if (!result.contains(checkNum)) {
									result.add(checkNum);
								}
							}
						}
						break;
					default:
						result.clear();
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				result = null;
				System.out.println(this + " with CheckNum" + checkNum + " rise e: " + e.getLocalizedMessage());
			}

			return result;
		}

		public void move(int dir, int side) {
			int[] newSizeArrow = TILE_SIZES[size].clone();

			switch (side) {
				case 0:
					newSizeArrow[1] += dir;
					break;
				case 1:
					newSizeArrow[0] += dir;
					break;
				case 2:
					colAddress += dir;
					newSizeArrow[1] -= dir;
					break;
				case 3:
					rowAddress += dir;
					newSizeArrow[0] -= dir;
					break;
				default:
					break;
			}
			TILE_QUANTITIES[size]--;
			if (DEBUG_SETTINGS) {System.out.print("from size: " + size);}
			size = getTileSize(newSizeArrow);
			if (DEBUG_SETTINGS) {System.out.println(" to size: " + this.toString());}
			this.stamp();
			TILE_QUANTITIES[size]++;
		}
	}
/////////////////////////////////////

	private static List<Integer> dependCheck(int dir, int side, List<Integer> checkedTiles, int asker) {

		if (DEBUG_SETTINGS) {System.out.println("MyLog dependencies (" + asker + ") " + checkedTiles );}
		List<Integer> newTiles = new ArrayList<>();

		for (Integer t: checkedTiles ) {

			if(tiles.get(t-1).canMove(dir, getOpposite(side)) == null) {return null;}

			newTiles = tiles.get(t-1).getDepends(dir, getOpposite(side), asker);
			if (newTiles == null) {
				return null;
			} else {
				newTiles = dependCheck(dir, getOpposite(side), newTiles, t);
			}
			if (newTiles == null) {
				return null;
			}
		}
		return newTiles;
	}

	private static List<Integer> dependMove(int dir, int side, List<Integer> checkedTiles, int asker) {

		if (DEBUG_SETTINGS) {System.out.println("MyLog dependMove:  (" + asker + ") " + checkedTiles);}
		List<Integer> newTiles = new ArrayList<>();
		tiles.get(asker-1).setFieldFree();

		for (Integer t: checkedTiles ) {

			newTiles = tiles.get(t-1).getDepends(dir, getOpposite(side), asker);
			/*
					если больше одной зависимости, то вызываем рекурсию в цикле*, на выходе из цикла делае движение
					если тут зависимость только одна и == exclude то прямо после этого делаем движение
					(может, можно и вызвать , всё равно вернет null)
					* -- передаём пока 1 родитель (предполагаем, что для исключений не понадобится спискок)
					 */
/*			newTiles.removeAll(checkedTiles);
			if (newTiles.contains(0)) newTiles.remove((Integer) 0);
			if (newTiles.contains(t)) newTiles.remove(t);
			if (newTiles.contains(asker)) newTiles.remove((Integer) asker);*/


			if (newTiles.isEmpty()){
				tiles.get(t-1).setFieldFree();
				tiles.get(t-1).move(dir, getOpposite(side));
			} else {
				newTiles.addAll(Objects.requireNonNull(dependMove(dir, getOpposite(side), newTiles, t)));
			}
//			tiles.get(t-1).setFieldFree();
//			tiles.get(t-1).move(dir, getOpposite(side));
		}

//		if(newTiles != null) if (!newTiles.isEmpty())
		tiles.get(asker-1).move(dir, side);
//						System.out.println("Success neighbour moved: " + (o + 1));
		return newTiles;
	}

	private static int diversity10x() {
		float div = 0;
		float ave = tiles.size() / TILE_SIZES.length;

		for (int i = 0; i < TILE_SIZES.length; i++) {
			div += (TILE_QUANTITIES[i] - ave) * (TILE_QUANTITIES[i] - ave);
/*			if ((i % 2) == 0) {
				div += TILE_QUANTITIES[i];
			} else {
				div -= TILE_QUANTITIES[i];
			}*/
		}
		return (int) div;
	}

	private static int getOpposite(int side) {

		// 0 -> 2, 1 -> 3, 2 -> 0, 3 -> 1
		return ((side < 2) ? side + 2 : side - 2);
	}

	private static void newField() {

		int i = 0;
		for (int row = 0; row < height; row += 2) {
			for (int col = 0; col < width; col += 2) {
				Tile tile = tiles.get(i);
				tile.set(i+1, row, col, 0); 	// size 0 is 2x2
				TILE_QUANTITIES[0]++;
				tiles.set(i, tile);
				i++;
			}
		}
		/* debug set */
		if (DEBUG_SETTINGS) {

//			field = new int[][]{{1, 1, 4, 4}, {1, 1, 4, 4}, {2, 2, 5, 5}, {2, 2, 5, 5}, {3, 3, 6, 6}, {3, 3, 6, 6}};  // 6x4
			field = new int[][]{{1, 1, 2, 2, 3, 3}, {1, 1, 2, 2, 3, 3}, {4, 4, 5, 5, 6, 6}, {4, 4, 5, 5, 6, 6}}; // 4x6
			tiles.get(0).set(1, 0, 0, 0);
			tiles.get(1).set(2, 0, 2, 0);
			tiles.get(2).set(3, 0, 4, 0);
			tiles.get(3).set(4, 2, 0, 0);
			tiles.get(4).set(5, 2, 2, 0);
			tiles.get(5).set(6, 2, 4, 0);

			field = new int[][]{{1, 1, 2, 2, 3, 3}, {1, 1, 2, 2, 3, 3}, {4, 4, 4, 5, 6, 6}, {4, 4, 4, 5, 6, 6}}; // 4x6
			tiles.get(0).set(1, 0, 0, 0);
			tiles.get(1).set(2, 0, 2, 0);
			tiles.get(2).set(3, 0, 4, 0);
			tiles.get(3).set(4, 2, 0, 3);
			tiles.get(4).set(5, 2, 3, 2);
			tiles.get(5).set(6, 2, 4, 0);

			field = new int[][]{
					{1, 2, 2, 3, 3, 3},
					{1, 2, 2, 3, 3, 3},
					{1, 2, 2, 5, 6, 6},
					{4, 4, 4, 5, 6, 6}}; // 4x6
			tiles.get(0).set(1, 0, 0, 6);
			tiles.get(1).set(2, 0, 1, 4);
			tiles.get(2).set(3, 0, 3, 3);
			tiles.get(3).set(4, 3, 0, 5);
			tiles.get(4).set(5, 2, 3, 2);
			tiles.get(5).set(6, 2, 4, 0);
		}


	}

	private static int randomInt(int min, int max) {
		return (int) (Math.random() * (max - min + 1) + min);
	}


	private static int getTileSize(int[] currentSize) {
		for (int i = 0; i < TILE_SIZES.length; i++) {
			if (TILE_SIZES[i][0] == currentSize[0] && TILE_SIZES[i][1] == currentSize[1]) {
				return i;
			}
		}
		return -1; // If no matching tile size is found
	}

	private static void printField() {
		System.out.println("\nThe Field \n");
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				System.out.printf("[%02d]", field[i][j]);
			}
			System.out.println();
		}
	}
	private static boolean printZeroesField() {
		boolean result = false;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (field[i][j] == 0) {
					System.out.printf("%d-%d:[%02d]; ", i, j, field[i][j]);
					result = true;
				}
			}
		}
		if (result) System.out.println("\n -- Zeroes");
		return result;
	}

	/*
	private static void runTightnessPass(int[][] field) {

		int previousTile = -1;
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {

				// Tile's Number
				int currentTile = field[row][col];
				if (currentTile == previousTile) {
					continue; 						// skip if same
				}
				int[] currentSize = getCurrentSize(field, row, col);
				int tileSizeNum = getTileSize(currentSize);

				// Try to offer new Size
				int[] newSize = getNewSize(row, col, currentSize);
				if (getTileSize(newSize) != -1) {
					previousTile = currentTile;
					continue;
				}

				int newX = col;
				int newY = row;
				if (newSize[0] > currentSize[0]) {
					newX += 1; // right
				} else if (newSize[0] < currentSize[0]) {
					newX -= 1; // left
				}
				if (newSize[1] > currentSize[1]) {
					newY += 1; // down
				} else if (newSize[1] < currentSize[1]) {
					newY -= 1; // up
				}
				field[row][col] = 0; // Set the current tile to 0
			}
		}
	}

	private static int[] getCurrentSize(int[][] field, int row, int col) {
		int width = field[0].length;
		int height = field.length;
		int rightSteps = 0;
		int leftSteps = 0;
		int downSteps = 0;
		int upSteps = 0;

		// Count how many steps to the right we can take without changing the cell value
		for (int c = col; c < width; c++) {
			if (field[row][c] != 0) {
				break;
			}
			rightSteps++;
		}

		// Count how many steps to the left we can take without changing the cell value
		for (int c = col; c >= 0; c--) {
			if (field[row][c] != 0) {
				break;
			}
			leftSteps++;
		}

		// Count how many steps down we can take without changing the cell value
		for (int r = row; r < height; r++) {
			if (field[r][col] != 0) {
				break;
			}
			downSteps++;
		}

		// Count how many steps up we can take without changing the cell value
		for (int r = row; r >= 0; r--) {
			if (field[r][col] != 0) {
				break;
			}
			upSteps++;
		}

		return new int[]{rightSteps + leftSteps, downSteps + upSteps};
	}

		private static int[] getNewSize(int row, int col, int[] currentSize) {
		Random random = new Random();
		int direction = random.nextInt(2) -1;			// 1 Grow, -1 Shrink
		validSides = new ArrayList<>(4);
		Collections.fill(validSides, 0);
		validSides = Collections.nCopies(4, 0);
		// 1 if the side can be reduced, 0 otherwise
		// Check if it's possible to decrease the tile size in each direction
		if (col + currentSize[1] < width) validSides.set(0, 1); // right
		if (row + currentSize[0] < height) validSides.set(1, 1); // down
		if (col > 0) validSides.set(2, 1); // left
		if (row > 0) validSides.set(3, 1); // up
		// validSides.removeIf(side -> side == 0); // Remove sides that cannot be reduced

		// Get random from available movements
		int validSidesSum = 0;
		for (Integer side : validSides) {
			if (side != 0) validSidesSum++;
		}
		int randomSideIndex = random.nextInt(validSidesSum);

		// Select chosen side
		int sideToReduce = 0; // 1: right, 2: down, 3: left, 4: up
		for (int i = 0; i < validSides.size(); i++) {
			if (validSides.get(i) != 0) {
				randomSideIndex--;
			}
			if (randomSideIndex == 0) {
				sideToReduce = i;
				// break;
			} else {
				validSides.set(i, 0); //
			}
		}

		// Make the answer
		switch (sideToReduce) {
			case 1:
			case 3:
				currentSize[1]--;
				break;
			case 2:
			case 4:
				currentSize[0]--;
				break;
			default:
				return new int[]{0, 0}; 	// zero size tile
		}

		return currentSize;
	}

*/

}
