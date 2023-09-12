package org.nebobrod.schulteplus;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class STable {
	public static final String TAG = "STable";
	private Context mContext;
	private Resources mRes;
	private ArrayList<SCell> area = new ArrayList<>();
	private int xSize, ySize;
	private int turnNumber = 1;

	public STable(int x, int y, Context c) {
		mContext = c;
		mRes = mContext.getResources();
		this.xSize = x;
		this.ySize = y;
		// first record in journal has time, the others time of turn
		this.journal.add(new Turn(System.nanoTime(), 0L, 0, 0, 0, true));

		int value = 1;
		for (x = xSize; x>0; x--){
			for (y = ySize;y>0; y--){
				area.add(new SCell(x,y,value++));
			}
		}
	}

	public String getResults(){
		int time = 0, turns;	// all this tend to milliseconds
		float average = 0, rmsd = 0;
		String results = "";	// this in seconds.00

		turns = this.journal.size()-1;
		results += mRes.getString(R.string.lbl_turns) + ": " + turns;

		// time spent & average deviation
		for (int i=1; i<=turns; i++) {
			time += this.journal.get(i).time;
		}
		results += "\n" + mRes.getString(R.string.lbl_time) + ": " + String.format("%.2f", (time /1000F));
		average = (float) time / turns;

		// root mean square deviation
		for (int i=1; i<=turns; i++) {
			rmsd += Math.pow ((average - this.journal.get(i).time), 2);
//			Log.d(TAG, "getResults: RMSD " +i+ "_" + String.format("%.2f", rmsd) );
		}
		rmsd = (float) Math.sqrt((float) (rmsd / turns));

		results += "\n" + mRes.getString(R.string.lbl_pace) + " ave: " + String.format("%.2f", (average / 1000)) + " sd: " + String.format("%.3f", (rmsd / 1000)) + " " + mRes.getString(R.string.lbl_mu_second);
		Log.d(TAG, "getResults: "+ this.journal);
		Log.d(TAG, "getResults: \n" + results);

		return results;
	}

	public boolean checkEnd() {
		return (turnNumber > xSize*ySize? true: false);
	}

	class Turn {
		Long timeStamp, time;
		int x, y, position;
		boolean isCorrect;

		public Turn(Long timeStamp, Long time, int x, int y, int position, boolean isCorrect) {
			this.timeStamp = timeStamp;
			this.time = time;
			this.x = x;
			this.y = y;
			this.position = position;
			this.isCorrect = isCorrect;
		}


		@Override
		public String toString() {
			return "\nTurn{" +
					"stamp=" + timeStamp +
					", time=" + time +
					", x=" + x +
					", y=" + y +
					", pos=" + position +
					", isCorrect=" + isCorrect +
					'}';
		}
	}
	public List<Turn> journal = new ArrayList<Turn>();

	public boolean checkTurn (int position) {
		int attemptNumber = journal.size();
		int turnY = (position + 1) / xSize + 1;
		int turnX = (position + 1) % xSize;

		if (this.area.get(position).getValue() == turnNumber) {
			this.journal.add(new Turn(
					System.nanoTime(),(System.nanoTime() - journal.get(attemptNumber - 1).timeStamp) / 1000000, turnX, turnY, position, true));
			turnNumber++;
			return true;
		} else {
			this.journal.add(new Turn(
					System.nanoTime(), (System.nanoTime() - journal.get(attemptNumber - 1).timeStamp) / 1000000, turnX, turnY, position, false));
			return false;
		}
	}

	public void shuffle(){
		Random r = new Random();
		ArrayList<SCell> clonedArea = (ArrayList<SCell>) area.clone();

		for (int i = xSize * ySize-1; i>=0; i--){
			int j = r.nextInt(clonedArea.size());
			area.set( i, clonedArea.get(j));	// instead of .add, .set replaces current O
//			Log.d(TAG, "shuffle: i=" + i +" j="+ j);
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
