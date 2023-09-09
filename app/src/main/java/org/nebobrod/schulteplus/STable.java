package org.nebobrod.schulteplus;

import java.util.ArrayList;
import java.util.Random;

public class STable {
	private ArrayList<SCell> area = new ArrayList<>();
	private int xSize, ySize;
	private int turnNumber = 1;

	public STable(int x, int y) {
		this.xSize = x;
		this.ySize = y;

		int value = 1;
		for (x = xSize; x>0; x--){
			for (y = ySize;y>0;y--){

				area.add(new SCell(x,y,value++));
			}
		}
	}

	public void shuffle(){
		Random r = new Random();
		ArrayList<SCell> clonedArea = (ArrayList<SCell>) area.clone();

		for (int i = xSize * ySize-1; i>=0; i--){
			int j = r.nextInt(clonedArea.size());
			area.add( i, clonedArea.get(j));
			clonedArea.remove(j);
		}

	}

	public ArrayList<SCell> getArea() {
		return area;
	}

	public SCell getSCell(int x, int y) {
		return area.get(x * this.xSize + y * this.ySize);
	}

	public int getX() {
		return xSize;
	}

	public int getY() {
		return ySize;
	}


}
