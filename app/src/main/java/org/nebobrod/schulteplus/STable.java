package org.nebobrod.schulteplus;


import static org.nebobrod.schulteplus.Const.SEQ1_SINGLE;
import static org.nebobrod.schulteplus.Utils.bHtml;
import static org.nebobrod.schulteplus.Utils.cHtml;
import static org.nebobrod.schulteplus.Utils.pHtml;
import static org.nebobrod.schulteplus.Utils.tHtml;

import android.content.Context;
import android.content.res.Resources;
import android.database.SQLException;
import android.util.Log;

import androidx.annotation.NonNull;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.nebobrod.schulteplus.data.ClickGroup;
import org.nebobrod.schulteplus.data.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class STable {
	public static final String TAG = "STable";
	private Context mContext;
	private Resources mRes;
	private ArrayList<SCell> area = new ArrayList<>();
	private int xSize, ySize;
	private int turnNumber;
	private int sequence;
	private boolean isFinished = false;

	public STable(int x, int y, int sequence, Context c) {
		mContext = c;
		mRes = mContext.getResources();
		this.xSize = x;
		this.ySize = y;
		this.sequence = sequence;

		this.reset();
	}

	// Simplified constructor overloading for previous calls
	public STable(int x, int y, Context c){
		this(x, y, SEQ1_SINGLE, c);
	}

	public void reset()
	{
		int value = 1;
		isFinished = false;
		area.clear();
		for (int x = xSize; x > 0; x--){
			for (int y = ySize; y > 0; y--){
				area.add(new SCell(x, y, value++));
			}
		}
		turnNumber = 1;
		shuffle();
		// first record in journal has time, the others time of turn
		this.journal.clear();
		this.journal.add(new Turn(System.nanoTime(), 0L, turnNumber,0, 0, 0, true));
		Log.d(TAG, "reset: \n" + area.toString());
	}

	public String getResults()
	{
		int time = 0, turns = 0, turnsMissed = 0;	// time tends to milliseconds
		float average = 0, rmsd = 0;
		String results = "";	// this in seconds.00

		turns = this.journal.size()-1;
		results += mRes.getString(R.string.lbl_turns) + ":" + tHtml()  + bHtml(""+ turns);

		// time spent & average deviation
		for (int i=1; i<=turns; i++) {
			time += this.journal.get(i).time;
			if (!this.journal.get(i).isCorrect) turnsMissed++;
		}
		if (turnsMissed != 0) { // if there are no missed turns do not show it
			results += pHtml() + mRes.getString(R.string.lbl_turns_missed) + ":" + tHtml()  + cHtml( bHtml(""+ turnsMissed));
		}
		results += pHtml() + mRes.getString(R.string.lbl_time) + ":" + tHtml()  + bHtml(String.format("%.2f", (time /1000F))) + " " + mRes.getString(R.string.lbl_mu_second);
		average = (float) time / turns;

		// root mean square deviation
		for (int i=1; i<=turns; i++) {
			rmsd += Math.pow ((average - this.journal.get(i).time), 2);
//			Log.d(TAG, "getResults: RMSD " +i+ "_" + String.format("%.2f", rmsd) );
		}
		rmsd = (float) Math.sqrt((float) (rmsd / turns));

		results +=  pHtml() + mRes.getString(R.string.lbl_average) + tHtml()  +  bHtml(String.format("%.2f", (average / 1000))) + " " + mRes.getString(R.string.lbl_mu_second) +
				pHtml()+ mRes.getString(R.string.lbl_sd) + tHtml() + bHtml(String.format("%.2f", (rmsd / 1000))) + " " + mRes.getString(R.string.lbl_mu_second);
		Log.d(TAG, "getResults: "+ this.journal);
		Log.d(TAG, "getResults: " + results);

		return results;
	}

	public boolean endChecked() {
		return (turnNumber > xSize*ySize? isFinished = true: false);
/*		if (turnNumber > xSize*ySize) {
			isFinished = true;
		}
		return isFinished;*/
	}

	// This object keeps what user send as a turn
	class Turn {
		Long timeStamp, time;
		int expected;
		int x, y, position;
		boolean isCorrect;

		public Turn(Long timeStamp, Long time, int expected, int x, int y, int position, boolean isCorrect) {
			this.timeStamp = timeStamp;
			this.time = time;
			this.expected = expected;
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
					", exp=" + expected +
					", x=" + x +
					", y=" + y +
					", pos=" + position +
					", isCorrect=" + isCorrect +
					'}';
		}
	}
	public List<Turn> journal = new ArrayList<Turn>();

	// This initiates user's turn handler
	public boolean checkTurn (int position)
	{
		int attemptNumber = journal.size();
		int turnY = (position) / xSize + 1;
		int turnX = (position) % xSize + 1;
		boolean result = false;

		if (this.area.get(position).getValue() == turnNumber) {
			result = true;
			turnNumber++;
		}
		this.journal.add(new Turn(
				System.nanoTime(), (System.nanoTime() - journal.get(attemptNumber - 1).timeStamp) / 1000000,
				(result ? turnNumber : turnNumber-1), turnX, turnY, position, result));
		writeTurn(this.journal.get(this.journal.size()-1));
		return result;
	}

	public void writeTurn (@NonNull Turn turn)
	{
		ClickGroup group = new ClickGroup();
		group.setName(turn.toString());
		try {
			DatabaseHelper helper = new DatabaseHelper(MainActivity.getInstance());
			Dao<ClickGroup, Integer> dao = helper.getGroupDao();
			dao.create(group);
		} catch (SQLException | java.sql.SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void shuffle()
	{
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

	public int getX() { return xSize; }

	public int getY() { return ySize; }

	public int getTurnNumber() { return turnNumber; }

	public boolean isFinished() { return isFinished; }

	public void setFinished(boolean finished) { isFinished = finished; }
}
