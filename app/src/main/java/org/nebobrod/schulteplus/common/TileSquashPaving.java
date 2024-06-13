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

/**
 * Squash algorithm <p>
 *     Each tile (requestor) randomly tries to push/pull its side
 *     gets answers from affected neighbours with:
 *     1) new dependencies (reason for recursive check); 2) empty dependencies (reason to move); 3) null (can't move) <p>
 *     After a squash cycle The diversity index calculated
 */
public class TileSquashPaving {
	/** Allowed [0]Row X [1]Cols */
	private static final boolean DEBUG_SETTINGS = false;
	static final int[][] TILE_SIZES = {{2, 2},
			{1, 2}, {2, 1},
			{2, 3}, {3, 2},
			{1, 3}, {3, 1},
			{1, 4}, {4, 1}, {1, 1}};

	private static final int ROWS = 5;
	private static final int COLS = 5;
	private static final int TILE_NUMBER = ROWS * COLS;
	private static final int DIVERSITY_MIN_TARGET = 20;
	private static final int CYCLES_LIMIT = 39; 	// this value is enough for quick result
													// and good for testing (int side = x / 10)

	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_WHITE = "\u001B[37m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_BLUE = "\u001B[34m";
	private static final String ANSI_PURPLE = "\u001B[35m";
	private static final String ANSI_CYAN = "\u001B[36m";
	private static final String ANSI_BLACK = "\u001B[30m";		// not use for fonts
	static final String[] ANSI_COLORS = {ANSI_RESET, ANSI_WHITE, ANSI_RED, ANSI_GREEN, ANSI_YELLOW, ANSI_BLUE, ANSI_PURPLE, ANSI_CYAN};

	static final int[] TILE_QUANTITIES_BY_SIZE = new int[TILE_SIZES.length];
	// Main list of tiles
	static List<Tile> tiles;

	// Main field
	static int[][] field = new int[ROWS *2][COLS *2];
	static int height = field.length;
	static int width = field[0].length;


	public static void main(String[] args) {

		// Init the tile-list in static environment
		TileSquashPaving tilePaving = new TileSquashPaving();
//		tiles = Collections.nCopies(TILE_NUMBER, tilePaving.new Tile()); 	// Error UnsupportedOperationException
		tiles = new ArrayList<>();
		for (int i = 0; i < TILE_NUMBER; i++) {
			tiles.add(tilePaving.new Tile());
		}

		// Define the field with tile.set() squared 2x2 by the  field's coordinates
		newField();

		// Main cycle
		int cycle = 0;
		int divMinReached = 0x7FFFFFFF; // Max int
		int[][] resultField = new int[height][width];

		while ((diversity10x() > DIVERSITY_MIN_TARGET) && (cycle <= CYCLES_LIMIT)) {
			// run Tightness
			for (Tile t : tiles) {
				int dir = 1; 	// -1 or 1
				int side = 0;
				int steps = 3;
				while (steps-- > 0) {
					dir = (randomInt(0, 1) * 2) - 1; 	// -1 or 1
					side = randomInt(0, 3);
					/* debug set */
					if (DEBUG_SETTINGS) {
				/*{
					dir = 1;
					side = cycle / 10;			// see CYCLES_LIMIT comment
				}*/
						dir = -1;
						side = 2;
						if (t.num != 5) continue;

						System.out.println("for: " + t);
						System.out.println("dir " + dir + ", side " + side);
					}	/* debug set finished */

					// List of agreed neighbours
					List<Integer> dep = t.canMove(dir, side);
					if (dep == null) {if (DEBUG_SETTINGS) {System.out.println("X can't move");} continue;}
					dep = dependCheck(dir, side, dep, t.num);
					if (dep == null) {if (DEBUG_SETTINGS) {System.out.println("X can't move others");} continue;}
					if (DEBUG_SETTINGS) {print2dArray(resultField);}
					break;
				}

				if (steps > 0) {
					dependMove(dir, side, t.canMove(dir, side), t.num);
				}

				if (DEBUG_SETTINGS && printZeroesField()) {
					int a =1;
				}
			}

			int _div = diversity10x();
			if (divMinReached > _div) {
				divMinReached = _div;
				resultField = field;
			}
			System.out.println("Cycle " + cycle++ + " DIVERSITY: " + _div + ", " + Arrays.toString(TILE_QUANTITIES_BY_SIZE));
			if (DEBUG_SETTINGS) {if ((cycle % 10) == 9) print2dArray(resultField);}
		}

		// Final Report
		print2dArray(resultField);
		System.out.println(" DIVERSITY reached: " + divMinReached);
	}

	/////////////////////////////////////
	/** Class represents a tile on the {@link #field} */
	private class Tile {
		int num;            // number or other kind of symbol on the tile
		int rowAddress;        // y-coordinate of top-left cell
		int colAddress;        // x-coordinate of top-left cell
		int size;            // From the TILE_SIZES[]
		// field[][] is used

		/**
		 * Set the tile and stamp it on the field
		 * @param num number or index for other kind of symbol on the tile
		 * @param rowAddress y-coordinate of top-left cell
		 * @param colAddress x-coordinate of top-left cell
		 * @param size From the {@link #TILE_SIZES} index (0 means 2x2)
		 */
		public void set(int num, int rowAddress, int colAddress, int size) {
			this.num = num;
			this.rowAddress = rowAddress;
			this.colAddress = colAddress;
			if (this.size != size) {
				TILE_QUANTITIES_BY_SIZE[this.size]--;
				TILE_QUANTITIES_BY_SIZE[size]++;
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

		/** Put tile's num into appropriate cells of the {@link #field} */
		private void stamp() {

			try {
				for (int row = rowAddress; row < rowAddress + TILE_SIZES[size][0]; row++) {
					for (int col = colAddress; col < colAddress + TILE_SIZES[size][1]; col++) {
						if ((field[row][col] == 0) || (field[row][col] == num)) {
							field[row][col] = num;                            // take a space
						} else {
							System.out.println("no space for " + this);
							System.out.println("field["+row+"]["+col+"] == " + field[row][col]);
							print2dArray(field);
							throw new RuntimeException("NO SPACE TO STAMP!");        // Check if not clean
						}
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Stamp is wrong -- " + this);
				print2dArray(field);
				throw new RuntimeException(e);
			}
		}
		private void setFieldFree() {

			for (int row = rowAddress; row < rowAddress + TILE_SIZES[size][0]; row++) {
				for (int col = colAddress; col < colAddress + TILE_SIZES[size][1]; col++) {
					if ((field[row][col] == num) || (field[row][col] == 0)) {
						field[row][col] = 0;                            // set free a space
					} else {
						System.out.println(this);
						print2dArray(field);
						throw new RuntimeException("NO FIELD!");        // Check if not clean
					}
				}
			}
			if (DEBUG_SETTINGS) {System.out.println("cleaned space " + this);}
		}

		/** Check borders and self-ability to move into new size
		 *
		 * @param dir direction minus or plus 1 of ax
		 * @param side 0 East, 1 South, 2 West, 3 North
		 * @return list of neighbours should be moved also (before) excluding `this`
		 */
		List<Integer> canMove (int dir, int side) {
			int[] newSizeArrow = TILE_SIZES[size].clone();
			int newSize;
			List<Integer>  can;

			try {
				switch (side) {
					case 0:
						newSizeArrow[1]+=dir;
						break;
					case 1:
						newSizeArrow[0]+=dir;
						break;
					case 2:
						newSizeArrow[1]-=dir;
						break;
					case 3:
						newSizeArrow[0]-=dir;
						break;
					default:
						newSizeArrow[0] = 0;	// warranted future error of size-check
						newSizeArrow[1] = 0;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				return null;
			}

			newSize = getTileSize(newSizeArrow);
			if (newSize == -1) {
				can = null;
			} else {
				can = getDepends(side, this.num);
			}
			return can;
		}

		/**
		 * Check every cell of field behind (outside) the requested side of move
		 * @param side  0 East, 1 South, 2 West, 3 North
		 * @param requestor 0 for self-request or requestor.num for recursive call
		 * @return list of dependent tile.num (except of <code>requestor</code>)
		 * <p> or empty list (which means can move with no dependencies)
		 * <p> or <code>null</code> (means non-movable side)
		 */
		List<Integer> getDepends (int side, int requestor) {
			if (requestor == 0) {requestor = this.num;}
			List<Integer>  result = new ArrayList<>();
			int checkNum = 0;
			try {
				switch (side) {
					case 0:
						for (int row = rowAddress; row < rowAddress + TILE_SIZES[size][0]; row++) {
							checkNum = field[row][colAddress + TILE_SIZES[size][1]];
							if ((checkNum != 0) && (checkNum != num) && (checkNum != requestor)) {
								if (!result.contains(checkNum)) {
									result.add(checkNum);
								}
							}
						}
						break;
					case 1:
						for (int col = colAddress; col < colAddress + TILE_SIZES[size][1]; col++) {
							checkNum = field[rowAddress + TILE_SIZES[size][0]][col];
							if ((checkNum != 0) && (checkNum != num) && (checkNum != requestor)) {
								if (!result.contains(checkNum)) {
									result.add(checkNum);
								}
							}
						}
						break;
					case 2:
						for (int row = rowAddress; row < rowAddress + TILE_SIZES[size][0]; row++) {
							checkNum = field[row][colAddress - 1];
							if ((checkNum != 0) && (checkNum != num) && (checkNum != requestor)) {
								if (!result.contains(checkNum)) {
									result.add(checkNum);
								}
							}
						}
						break;
					case 3:
						for (int col = colAddress; col < colAddress + TILE_SIZES[size][1]; col++) {
							checkNum = field[rowAddress - 1][col];
							if ((checkNum != 0) && (checkNum != num) && (checkNum != requestor)) {
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
				if (DEBUG_SETTINGS) {System.out.println(this + " with CheckNum" + checkNum + " rise e: " + e.getLocalizedMessage());}
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
			TILE_QUANTITIES_BY_SIZE[size]--;
			if (DEBUG_SETTINGS) {System.out.print("from size: " + size);}
			size = getTileSize(newSizeArrow);
			if (DEBUG_SETTINGS) {System.out.println(" to size: " + this);}
			this.stamp();
			TILE_QUANTITIES_BY_SIZE[size]++;
		}
	}
/////////////////////////////////////

	private static List<Integer> dependCheck(int dir, int side, List<Integer> checkedTiles, int requestor) {

		if (DEBUG_SETTINGS) {System.out.println("MyLog dependencies (" + requestor + ") " + checkedTiles );}
		List<Integer> newTiles = new ArrayList<>();

		for (Integer t: checkedTiles ) {

			if(tiles.get(t-1).canMove(dir, getOpposite(side)) == null) {return null;}

			newTiles = tiles.get(t-1).getDepends(getOpposite(side), requestor);
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

	private static List<Integer> dependMove(int dir, int side, List<Integer> checkedTiles, int requestor) {

		if (DEBUG_SETTINGS) {System.out.println("MyLog dependMove:  (" + requestor + ") " + checkedTiles);}
		List<Integer> newTiles = new ArrayList<>();
		tiles.get(requestor-1).setFieldFree();

		for (Integer t: checkedTiles ) {

			newTiles = tiles.get(t-1).getDepends(getOpposite(side), requestor);

			if (newTiles.isEmpty()){
				tiles.get(t-1).setFieldFree();
				tiles.get(t-1).move(dir, getOpposite(side));
			} else {
				newTiles.addAll(Objects.requireNonNull(dependMove(dir, getOpposite(side), newTiles, t)));
			}
		}

		tiles.get(requestor-1).move(dir, side);
//		System.out.println("Success neighbour moved: " + (o + 1));
		return newTiles;
	}

	private static int diversity10x() {
		float div = 0;
		float ave = tiles.size() / (float) TILE_SIZES.length;

		for (int i = 0; i < TILE_SIZES.length; i++) {
			div += (TILE_QUANTITIES_BY_SIZE[i] - ave) * (TILE_QUANTITIES_BY_SIZE[i] - ave);
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
				TILE_QUANTITIES_BY_SIZE[0]++;
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

	private static void print2dArray(int[][] arr) {

		System.out.print("\033\143"); 			// cls for linux

		System.out.println("\nThe Field \n");
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
				Integer val = arr[i][j];
				System.out.printf(tileColor(val) + "[%02d]" + ANSI_COLORS[0], val);
			}
			System.out.println();
		}
	}

	private static String tileColor(int num) {

		int col = num % (ANSI_COLORS.length - 1) + 1;
		return ANSI_COLORS[col];
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
}
