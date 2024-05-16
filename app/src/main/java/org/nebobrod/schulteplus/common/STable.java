/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.common;

import static org.nebobrod.schulteplus.common.Const.AVERAGE_IDLE_LIMIT;
import static org.nebobrod.schulteplus.common.Const.KEY_PRF_EX_B0;
import static org.nebobrod.schulteplus.common.Const.KEY_PRF_EX_S0;
import static org.nebobrod.schulteplus.common.Const.KEY_PRF_EX_S1;
import static org.nebobrod.schulteplus.common.Const.KEY_PRF_EX_S2;
import static org.nebobrod.schulteplus.common.Const.KEY_PRF_EX_S3;
import static org.nebobrod.schulteplus.Utils.bHtml;
import static org.nebobrod.schulteplus.Utils.cHtml;
import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.Utils.pHtml;
import static org.nebobrod.schulteplus.Utils.tHtml;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.data.DataOrmRepo;
import org.nebobrod.schulteplus.data.DataRepos;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.ExResultBasics;
import org.nebobrod.schulteplus.data.ExResultSchulte;
import org.nebobrod.schulteplus.data.Turn;
import org.nebobrod.schulteplus.data.fbservices.DataFirestoreRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Schulte Table -- a List of Cells represented as a rectangular
 * (xSize, ySize)
 */
public class STable extends Exercise {
	public static final String TAG = "STable";

	private ArrayList<Double> probabilities = new ArrayList<>();
	private double probabilitiesSum = 0;
	private ArrayList<SCell> area = new ArrayList<>();
	private int xSize, ySize;
	/** shift of coordinate-center for probabilities */
	private double dX, dY;
	/** surface for probabilities
	 * if 0 -> uniform, 0.4 : 1.0 -> normal, 1.4 : 2.0 -> remove 1.0 and cut 10% of low prob to zero, see KEY_PRF_PROB_ZERO */
	private double w;
	/** turnNumber is expected Cell (not an attempt) */
	private int expectedValue;


	// Simplified constructor overloading
	public STable(int x, int y) {
		this(x, y, 0, 0, 0);
	}

	public STable(int x, int y, double dX, double dY, double w) {

		this.xSize = x;
		this.ySize = y;
//		this.sequence = sequence;

		this.dX = dX;
		this.dY = dY;
		this.w = w;
		if (ExerciseRunner.isRatings()) {
			this.dX = 0;
			this.dY = 0;
			this.w = 0;
		}

		this.reset();
	}

/*	public STable() {
		if (ExerciseRunner.isRatings()) {
			new STable(ExerciseRunner.getInstance().getX(), ExerciseRunner.getInstance().getY());
		} else {
			ExerciseRunner.loadPreference();
			new STable(ExerciseRunner.getInstance().getX(), ExerciseRunner.getInstance().getY(), ExerciseRunner.probDx(), ExerciseRunner.probDy(), ExerciseRunner.probW());
		}
	}*/

	public void reset()
	{
		int value = 1;
		setFinished(false);
		probabilities = fillProbabilities (xSize, ySize, dX, dY, w);
		random = new Random();
		area.clear();
		for (int x = xSize; x > 0; x--){
			for (int y = ySize; y > 0; y--){
				area.add(new SCell(x, y, value++));
			}
		}
		expectedValue = 1;
		shuffle();

		// prepare parent
		{
			seed = random.nextLong();
			setSeed(seed);

			switch (ExerciseRunner.getExType().substring(0, 7)) {
				case KEY_PRF_EX_S0:
					exResult = new ExResultSchulte(seed, 0L, 0, 0, 0F, 0F, 0, 0,  "");
					break;
				case KEY_PRF_EX_B0:
					exResult = new ExResultBasics(seed, 0L, 0, 0, 0, "");
					break;
				default:
					exResult = new ExResult(seed, 0L, 0, 0, "");
			}

			// Put in local DB and get id
			DataRepos repos = new DataRepos(exResult.getClass());
			repos.put(exResult);
			exerciseId = exResult.getId();
		}
		// first record in journal has time, the others time of turn
		this.journal.clear();
		this.journal.add(new Turn(exResult, System.nanoTime(), 0L, expectedValue,0, 0, 0, true));
		Log.d(TAG, "reset: \n" + area.toString());
	}

	/**
	 * Set Cell graphics by ExType
	 * mono, two-colored sequences, four-colored sequences
	 */
	public TextView setViewContent (TextView view, int position) {
		int value = area.get(position).getValue();
		@ColorInt int color;
		//		 https://stackoverflow.com/questions/51719485/adding-border-to-textview-programmatically
		Drawable img = AppCompatResources.getDrawable(getAppContext(), R.drawable.ic_border);
		color = ContextCompat.getColor(getAppContext(), R.color.light_grey_D);

		switch (ExerciseRunner.getExType()){
			case KEY_PRF_EX_S1:
//				view.setText(value); // value keeps its sequence
//				color = ContextCompat.getColor(getAppContext(), R.color.transparent);
				break;
			case KEY_PRF_EX_S2:
				if (value % 2 != 0) { // odd
					value = 1 + value / 2; // 1:25 red
					color = ContextCompat.getColor(getAppContext(), R.color.light_grey_A_blue);
				} else { // even
					value = 25 - value / 2; // 24:1 blue
					color = ContextCompat.getColor(getAppContext(), R.color.light_grey_A_red);
				}
//				img.setColorFilter(Color.valueOf(getColor(R.color.light_grey_A_red)).toArgb(), PorterDuff.Mode.SRC_IN);
				break;
			case KEY_PRF_EX_S3:
				switch (value % 4) {
					case 1: // Growing
						value = 1 + value / 4; // 1:25 blue
						color = ContextCompat.getColor(getAppContext(), R.color.light_grey_A_blue);
						break;
					case 2: // Downward
						value = (102 - value) / 4; // 25:1 red
						color = ContextCompat.getColor(getAppContext(), R.color.light_grey_A_red);
						break;
					case 3: // Convergent
						value +=1; // 1,25:12,13 green
						value = (0 == (value % 8) ? 26 - (value / 8) : (value + 4) / 8);
						color = ContextCompat.getColor(getAppContext(), R.color.light_grey_A_green);
						break;
					case 0: // Divergent
						value = (0 == (value % 8) ? 13 + (value / 8) : 13 - value / 8); // 12,13:1,25 yellow
						color = ContextCompat.getColor(getAppContext(), R.color.light_grey_A_yellow);
						break;
				}
				break;
			default:
		}
		view.setText("" + value);
		img.setColorFilter(color, PorterDuff.Mode.DST_ATOP);
		view.setBackground(img);

		return view;
	}

	private ArrayList<Double> fillProbabilities(int xSize, int ySize, double dX, double dY, double w) {
		ArrayList<Double> probArray = new ArrayList<>(Collections.nCopies(xSize * ySize, (Double) 0.5));
		// -- prefilled with 50% probabilities (but it isn't used anywhere)

		probabilitiesSum = 0;

		if (w == 0) { // uniform dispersion
			return probArray;
		} else {
			double xStep = (2D/xSize);
			double yStep = (2D/ySize);
			for (int j = 0; j< ySize; j++) { // Rows or vertical step
				String output = "Row: " + j + ": ";
//				System.out.print("\nRow: " + j);
				for (int i = 0; i< xSize; i++) { // Columns or horizontal step
					Double probWeigh = Math.pow(100 * camelSurface(-1 + i * xStep + xStep/2, -1 + j * yStep + yStep/2, dX, dY, w),
							3) / 10000;
					probArray.set(j*xSize + i, probWeigh);
					probabilitiesSum += probArray.get(j*xSize + i);
					output = output + String.format ("%.2f | ", probArray.get(j*xSize + i)) + " |";
//					System.out.printf("\t'%3.3f'", result.get(i*xSize + j));
				}
				Log.d(TAG, "fillProbabilities: " + output);
			}
			return probArray;
		}
	}


	/**
	 * Gathering statistics of passed exercise
	 */
	// TODOne 02.03.24: 25.12.2023 later return this as Map to display in ListView
	public ExResult getResults() {

		int time = 0, turns = 0, turnsMissed = 0;	// time tends to milliseconds
		float average = 0, rmsd = 0;
		String results = "";	// this in seconds.00

		turns = this.journal.size()-1; // 'cos 1-st position journal[0] means no turns (start record)
		results += getRes().getString(R.string.lbl_turns) + ":" + tHtml()  + bHtml(""+ turns);

		// time spent & average deviation
		for (int i=1; i<=turns; i++) {
			time += this.journal.get(i).getTime();
			if (!this.journal.get(i).isCorrect()) turnsMissed++;
		}
		// if there are no missed turns do not show it
		if (turnsMissed != 0) {
			results += pHtml() + getRes().getString(R.string.lbl_turns_missed) + ":" + tHtml()  + cHtml( bHtml(""+ turnsMissed));
		}
		results += pHtml() + getRes().getString(R.string.lbl_time) + ":" + tHtml()  + bHtml(String.format(Locale.ENGLISH, "%.2f", (time /1000F)))
				+ " " + getRes().getString(R.string.lbl_mu_second);
		average = (float) time / turns;

		// root mean square deviation
		for (int i=1; i<=turns; i++) {
			rmsd += Math.pow ((average - this.journal.get(i).getTime()), 2);
//			Log.d(TAG, "getResults: RMSD " + i + "_" + String.format(Locale.ENGLISH, "%.2f", rmsd) );
		}
		rmsd = (float) Math.sqrt((float) (rmsd / turns));

		results +=  pHtml() + getRes().getString(R.string.lbl_average) + tHtml()  +  bHtml(String.format(Locale.ENGLISH, "%.2f", (average / 1000)))
				+ " " + getRes().getString(R.string.lbl_mu_second)
				+ pHtml() + getRes().getString(R.string.lbl_sd) + tHtml() + bHtml(String.format(Locale.ENGLISH, "%.2f", (rmsd / 1000)))
				+ " " + getRes().getString(R.string.lbl_mu_second);
		Log.d(TAG, "getResults: "+ this.journal);
		Log.d(TAG, "getResults: " + results);

/*		exResult = (ExerciseRunner.getExType().contains(KEY_PRF_EX_S0) ?
				new ExResultSchulte(time, turns, turnsMissed, average, rmsd, 0, 0, "" ) :
				new ExResultBasics(time, turns, 0, 0, ""));*/
		switch (ExerciseRunner.getExType().substring(0, 7)) {
			case KEY_PRF_EX_S0:
				((ExResultSchulte) exResult).update(time, turns, turnsMissed, average, rmsd, 0, 0, "" );
				break;
			case KEY_PRF_EX_B0:
				((ExResultBasics) exResult).update(time, turns, 0, 0, "");
				break;
			default:
				exResult.update(time, 0, 0, "");
		}
		return exResult;
	}

	public boolean checkIsFinished() {
		return (expectedValue > xSize*ySize? isFinished(): false);
/*		if (turnNumber > xSize*ySize) {
			isFinished = true;
		}
		return isFinished;*/
	}

	public List<Turn> journal = new ArrayList<>();

	/**
	 * Answers was it correct cell? puts turn-data into journal
	 * @param position number of clicked Cell
	 */
	public boolean isCorrectTurn(int position)
	{
		int attemptNumber = journal.size();
		int turnY = (position) / xSize + 1;
		int turnX = (position) % xSize + 1;
		boolean result = false;

		if (this.area.get(position).getValue() == expectedValue) {
			result = true;
			expectedValue++;
		}
		this.journal.add(new Turn(
				exResult,
				System.nanoTime(),
				(System.nanoTime() - journal.get(attemptNumber - 1).getTimeStamp()) / 1000000,
				(result ? expectedValue : expectedValue -1),
				turnX, turnY, position, result));
		writeTurn(this.journal.get(this.journal.size()-1));
		return result;
	}

	/**
	 * This writes turn-data to local db (just for extra history assurance)
	 * @param turn
	 */
	public void writeTurn (@NonNull Turn turn) {

		DataOrmRepo localRepo = new DataOrmRepo<>(turn.getClass());
		localRepo.put(turn);

		DataFirestoreRepo centralRepo = new DataFirestoreRepo<Turn>(Turn.class);
		centralRepo.create(turn);

		//dao.create(group); // // DONE: 03.05.2024 got rid of click-group examples FROM TODOne: 29.01.2024 move into ClickGroup with "this" as put-method
	}

	/**
	 * Rearranges Cells in the Table
	 */
	public void shuffle() 	{

		if (!ExerciseRunner.isShuffled()) return; // if no-shuffle option in user Prefs


		ArrayList<SCell> clonedArea = (ArrayList<SCell>) area.clone();

		{ // uniform distribution:

			//todo maybe Collections.shuffle(area);

			for (int i = xSize * ySize-1; i>=0; i--){
				int j = random.nextInt(clonedArea.size());
				area.set( i, clonedArea.get(j));	// instead of .add, .set replaces current Object
//				Log.d(TAG, "shuffle: i=" + i +" j="+ j);
				clonedArea.remove(j);
			}
		}
		if (this.w != 0) { // custom distribution:
			double nextExpectedPosition = (random.nextDouble() * probabilitiesSum);
			int caughtPosition =0 ;
			double cumulativeBoundary = probabilitiesSum;
			int i = 0;
			Log.d(TAG, "probabilitiesSum: " + probabilitiesSum + " nextExpectedPosition: " + nextExpectedPosition);
			for (double prob : probabilities){
				cumulativeBoundary -= prob ;
				Log.d(TAG, "shuffle: i=" + i + " neExP="+ nextExpectedPosition +" prob="+ prob + " cB="+ cumulativeBoundary + " j=+ j");
				if (cumulativeBoundary < nextExpectedPosition) {
					caughtPosition = i;
					break;
				}
				i++;
			}
			Log.d(TAG, "shuffle: caughtPosition: " + caughtPosition + " expectedValue: " + expectedValue  + " " );
//			area.indexOf(caughtPosition);
			/** define the victim Cell, where we move the N which occupies necessary caughtPosition */
			if (area.get(caughtPosition).getValue() != expectedValue) {
				for (int k = 0; k < area.size(); k++) {
					if (area.get(k).getValue() == expectedValue) {
						Collections.swap(area, k, caughtPosition);
						Log.d(TAG, "shuffle: swap: " + caughtPosition + " and k: " + k);
						Log.d(TAG, "shuffle: value of caughtPosition: " + area.get(caughtPosition).getValue());
						break;
					}
				}
			}
//			int changedPosition = 0;
//			do {
//				changedPosition = r.nextInt(area.size());
//			} while (changedPosition == caughtPosition);


		}
	}

	/**
	 * Depending of w this function provides either hump or circle wave
	 * @param x
	 * @param y  coordinates (-1 : 1) of searchable value of f
	 * @param dX
	 * @param dY shift of center (-1 : 1)
	 * @param w coefficient of curve (0.4 : 1)
	 * @return value of probability at x,y (0.01 : 0.70)
	 */
	public static double camelSurface(double x, double y, double dX, double dY, double w) {
		// Safety of zero dividing:
		if (0 == (Math.pow ((Math.pow (w*x - dX, 2) + Math.pow (w*y - dY, 2) + w), 4) + w)) return 0;

		return Math.pow ((Math.pow (w*x - dX, 2) + Math.pow (w*y - dY, 2) + w), 2)
				/ (Math.pow ((Math.pow (w*x - dX, 2) + Math.pow (w*y - dY, 2) + w), 4) + w);

//		return Math.pow ((Math.pow (w*x - dX*x, 2) + Math.pow (w*y - dY*y, 2) + w), 2)
//				/ (Math.pow ((Math.pow (w*x - dX*x, 2) + Math.pow (w*y - dY*y, 2) + w), 4) + w);
//		This one was mistyped '*x' and it changes form... may be useful (straight vertical or horizontal)

	}

	public ArrayList<Double> getProbabilities() { return probabilities;}

	public ArrayList<SCell> getArea() {
		return area;
	}

	public int getExpectedPosition(){
		int i = area.size()-1;
		for ( ; i>=0; i--) {
			if (area.get(i).getValue() == expectedValue) break;
		}
		return i;
	}

	public SCell getSCell(int x, int y) {
		return area.get(x * this.xSize + y * this.ySize);
	}

	public int getX() { return xSize; }

	public int getY() { return ySize; }

	public int getExpectedValue() { return expectedValue; }

	@Override
	boolean validateResult() {

		// Spent seconds During the exercise
		int events = this.journal.size()-1;
		exResult.setNumValue((this.journal.get(events).getTimeStamp() - this.journal.get(0).getTimeStamp())/1000000000);

		// if an average turn duration exceeds allowed limit
		if ((exResult.getNumValue() / events) > AVERAGE_IDLE_LIMIT) {
			return false;
		}
		setValid(true);
		return true;
	}
}
