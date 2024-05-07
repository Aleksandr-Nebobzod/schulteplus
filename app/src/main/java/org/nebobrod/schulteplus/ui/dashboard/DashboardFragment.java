/*
 * Copyright (c) "Smart Rovers" 2024.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.nebobrod.schulteplus.ui.dashboard;

import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.common.Const.TIMESTAMP_FIELD_NAME;

import org.nebobrod.schulteplus.common.ExerciseRunner;
import org.nebobrod.schulteplus.common.Log;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.data.Achievement;
import org.nebobrod.schulteplus.data.AchievementArrayAdapter;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.ExResultArrayAdapter;
import org.nebobrod.schulteplus.data.DataOrmRepo;
import org.nebobrod.schulteplus.data.fbservices.DataFirestoreRepo;
import org.nebobrod.schulteplus.data.DataRepository;
import org.nebobrod.schulteplus.databinding.FragmentDashboardBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements DataOrmRepo.OrmGetCallback {
	private static final String TAG = "Dashboard";

	DashboardViewModel dashboardViewModel;
	private FragmentDashboardBinding binding;
	String[] exTypeValues, exTypeEntries; // Arrays for spinner exType
	Spinner spDashboard;
	RadioGroup rgSource;
	TextView tvTitle;
	ListView elvChart;
	ArrayList<Spanned> list;
	ArrayList<Achievement> listAchievement;
	ArrayList<ExResult> listExResult;
	ArrayAdapter<Spanned> adapter;
	ArrayAdapter<ExResult> exResultAdapter;
	ArrayAdapter<Achievement> arrayAdapter;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		dashboardViewModel =
				new ViewModelProvider(this).get(DashboardViewModel.class);
		// Binding and initiating
		binding = FragmentDashboardBinding.inflate(inflater, container, false);
		View root = binding.getRoot();
			// This prevents the bug#27 of App's backgrounding due to mis-click
			root.setOnClickListener(view -> {});
		spDashboard = binding.spDashboard;
		setDashboardSpinner(spDashboard);
		rgSource = binding.rgSource;
		tvTitle = binding.tvTitle;
		elvChart = binding.elvDashboard;

		// Copy data to clipboard
		elvChart.setOnItemLongClickListener((adapterView, view, i, l) -> {
			ListAdapter _adapter = ((ListView) adapterView).getAdapter();
			int count = _adapter.getCount();
			if (count == 0) {
				return false; // if empty
			}
			// Define item's Class
			Object item = _adapter.getItem(0);
			Class<?> itemClass = item.getClass();

			// Create StringBuilder to gather text
			StringBuilder stringBuilder = new StringBuilder();

			for (int j = 0; j < count; j++) {
				item = _adapter.getItem(j);
				try {
					Method method = itemClass.getDeclaredMethod("toTabSeparatedString");
					String result = (String) method.invoke(item);
					stringBuilder.append(result).append("\n");
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					// If toTabSeparatedString() doesn't exist
					stringBuilder.append(item.toString()).append("\n");
				}
			}
			// Copy text to clipboard
			ClipboardManager clipboard = (ClipboardManager) getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("label", stringBuilder.toString());
			clipboard.setPrimaryClip(clip);

			Toast.makeText(getAppContext(), "Table copied to clipboard", Toast.LENGTH_SHORT).show();
			return true;
		});

//		local achievement datasource
		listAchievement = new ArrayList<>(); // OrmRepo.getAchievementList();
		arrayAdapter = new AchievementArrayAdapter(getAppContext(), (List<Achievement>) Utils.markupListAsGroupedBy(listAchievement, TIMESTAMP_FIELD_NAME));
		elvChart.setAdapter(arrayAdapter);
		arrayAdapter.notifyDataSetChanged();

//		firebase achievement datasource (Spanned yet)
		list = new ArrayList<>();
		adapter = new ArrayAdapter<>(this.getActivity(), R.layout.item_one_textview, list);
		elvChart.setAdapter(adapter);
		new DataFirestoreRepo<>(Achievement.class).getListLimited(onCallback);
		adapter.notifyDataSetChanged();

//		ExResult adapter
		listExResult = new ArrayList<ExResult>(); //(ArrayList<ExResult>) dashboardViewModel.getResultsLiveData().getValue();
		exResultAdapter = new ExResultArrayAdapter(getAppContext(), (List<ExResult>) Utils.markupListAsGroupedBy(listExResult, TIMESTAMP_FIELD_NAME));
		elvChart.setAdapter(exResultAdapter);
		exResultAdapter.notifyDataSetChanged();

		//Change Local or WWW datasource & www-Filters
		rgSource.setOnCheckedChangeListener((radioGroup, checkedId) -> {
			updateListView(checkedId);
		});

/*
		//Change table datasource Achievements or ExResults & ExType
		spDashboard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (view == null) return;
				Log.d(TAG, "onItemSelected: " + position + " id: " + id + " View: " + view.toString());
				dashboardViewModel.setKey(((TextView) view).getText().toString());

//				dashboardViewModel.setKey(view.getText());
			}
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {}
		});
*/

		return root;
	}

	/**
	 * Copy listView's data to clipboard NOT used
	 */
/*	private View.OnLongClickListener copyData = view -> {
		ListAdapter _adapter = ((ListView) view).getAdapter();
		int count = _adapter.getCount();
		// Create StringBuilder to gather text
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < count; i++) {
			stringBuilder.append(_adapter.getItem(i).toString()).append("\n");
		}
		// Copy text to clipboard
		ClipboardManager clipboard = (ClipboardManager) getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData clip = ClipData.newPlainText("label", stringBuilder.toString());
		clipboard.setPrimaryClip(clip);

		Toast.makeText(getAppContext(), "Table copied to clipboard", Toast.LENGTH_SHORT).show();
		return true;
	};
	*/

	/** Spinner to choose the dashboard (Achievements or an ExType) */
	private void setDashboardSpinner(Spinner spinner) {
		// Language independent values
		exTypeValues = getRes().getStringArray(R.array.ex_type);

		// Language-dependent entries based on spinner-entries array (which was values indeed)
		int spLength = exTypeValues.length;
		exTypeEntries = new String[spLength];
		for (int i = 0; i < spLength; i++) {
			String newEntry = exTypeValues[i].replace("gcb_", "lbl_");
			exTypeEntries[i] = Utils.getString(newEntry);
		}

		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> spAdapter = new ArrayAdapter(
				getAppContext(),
				android.R.layout.simple_spinner_item, // R.layout.item_one_textview, //
				exTypeEntries);
		// set simple layout resource file for each item of spinner
		 spAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

		// Set the ArrayAdapter data on the Spinner which binds data to spinner
		spinner.setAdapter(spAdapter);
		spAdapter.notifyDataSetChanged();

		// Set livedata Key when Dashboard spinner changed
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
				dashboardViewModel.setKey(exTypeValues[position]);
				RadioButton _rb = getView().findViewById(R.id.rb_www);
				if (position == 0) {
					// Achievements allow only two filters and "www" is default
					getView().findViewById(R.id.rb_ex_top_m).setEnabled(false);
					getView().findViewById(R.id.rb_ac_top_progress).setEnabled(false);
					if (rgSource.getCheckedRadioButtonId() == R.id.rb_ex_top_m) {
						_rb.setChecked(true);
//						dashboardViewModel.setFilter(_rb.getText().toString());
					}
				} else {
					// for non Achievement other radio-buttons are enabled
					getView().findViewById(R.id.rb_ex_top_m).setEnabled(true);
//					getView().findViewById(R.id.rb_ac_top_progress).setEnabled(true);
				}
				// Pass checked radioButton to viewModel
				_rb = getView().findViewById(rgSource.getCheckedRadioButtonId());
				dashboardViewModel.setFilter(_rb.getText().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// do nothing
			}
		});
	}

	private void updateListView(int checkedRadioButtonId) {
		RadioButton checkedRadioButton;
		checkedRadioButton = getView().findViewById(checkedRadioButtonId);
		tvTitle.setText(checkedRadioButton.getHint());
		String _key = (dashboardViewModel.getKey().getValue() == null ? "gcb_achievements" : dashboardViewModel.getKey().getValue());
		// choose table Achievements vs. ExResult
		if (_key.equals("gcb_achievements")) {
			switch(checkedRadioButtonId)
			{
				case R.id.rb_local:
					DataOrmRepo.achieveGet25(DashboardFragment.this::onComplete);
					arrayAdapter = new AchievementArrayAdapter(getAppContext(), (List<Achievement>) Utils.markupListAsGroupedBy(listAchievement, TIMESTAMP_FIELD_NAME));
					elvChart.setAdapter(arrayAdapter);
					break;
				case R.id.rb_www:
					adapter = new ArrayAdapter<>(getAppContext(), R.layout.item_one_textview, list);
					elvChart.setAdapter(adapter);
					new DataFirestoreRepo<>(Achievement.class).getListLimited(onCallback);
					break;
				default:
					// do nothing
			}
		} else {
			// ExResults
			switch(checkedRadioButtonId)
			{
				case R.id.rb_local: // fetch from local DB
					exResultAdapter = new ExResultArrayAdapter(getAppContext(),
							(List<ExResult>) Utils.markupListAsGroupedBy(listExResult, TIMESTAMP_FIELD_NAME));
					dashboardViewModel.fetchResultsLimited(ExResult.class);
					break;
				case R.id.rb_www: // fetch common users' results from server DB
					exResultAdapter = new ExResultArrayAdapter(getAppContext(),
							(List<ExResult>) Utils.markupListAsGroupedBy(listExResult, TIMESTAMP_FIELD_NAME));
					dashboardViewModel.fetchResultsLimited(ExResult.class);
					break;
				case R.id.rb_ex_top_m: // fetch users' results from server DB sorted by speed

					break;
				default:
					// do nothing
			}


//			exResultAdapter = ExResult.getArrayAdapter(getAppContext(), listExResult);
/*			adapter = new ArrayAdapter<>(
					getAppContext(),
					R.layout.layout_one_textview,
					exResultToSpanned(dashboardViewModel.getResultsLiveData().getValue()));*/
//			elvChart.setAdapter(exResultAdapter);
//			exResultAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
	 * has returned, but before any saved state has been restored in to the view.
	 * This gives subclasses a chance to initialize themselves once
	 * they know their view hierarchy has been completely created.  The fragment's
	 * view hierarchy is not however attached to its parent at this point.
	 *
	 * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
	 * @param savedInstanceState If non-null, this fragment is being re-constructed
	 *                           from a previous saved state as given here.
	 */
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Watch for LiveData and refresh ExResult UI
		dashboardViewModel.getResultsLiveData().observe(getViewLifecycleOwner(), results -> {
			// Update the listExResult with fresh data
			listExResult = (ArrayList<ExResult>) results;
			exResultAdapter = new ExResultArrayAdapter(getAppContext(), (List<ExResult>) Utils.markupListAsGroupedBy(listExResult, TIMESTAMP_FIELD_NAME));
			elvChart.setAdapter(exResultAdapter);
			exResultAdapter.notifyDataSetChanged();
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume: Called");
		// Set chosen Item in the spinner in accordance with userPreferences
		for (int i = 0; i < exTypeValues.length; i++) {
			if (ExerciseRunner.getExType().equals(exTypeValues[i])) {
				spDashboard.setSelection(i);
				dashboardViewModel.setKey(exTypeValues[i]);
				break;
			}
		}
		updateListView(rgSource.getCheckedRadioButtonId());
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "onDestroyView: Called");
		binding = null;
	}

	// Backend's Achievements
	public DataRepository.RepoCallback<List<Achievement>> onCallback = new DataRepository.RepoCallback<List<Achievement>>() {
		@Override
		public void onSuccess(List<Achievement> result) {
			list.clear();
			if (result != null) {
				for (Object o : result) {
					list.add(Html.fromHtml(o.toString()));
				}
			}
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onError(Exception e) {
			list.clear();
			list.add(Html.fromHtml(e.getLocalizedMessage()));
			adapter.notifyDataSetChanged();
		}
	};

	// Local Achievements
	@Override
	public void onComplete(Object result) {
		listAchievement = (ArrayList<Achievement>) Utils.markupListAsGroupedBy((ArrayList<Achievement>) result, TIMESTAMP_FIELD_NAME);
		arrayAdapter.notifyDataSetChanged();
	}
}