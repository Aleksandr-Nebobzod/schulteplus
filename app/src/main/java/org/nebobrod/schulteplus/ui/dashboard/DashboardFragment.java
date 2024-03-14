package org.nebobrod.schulteplus.ui.dashboard;

import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.Utils.getRes;
import static org.nebobrod.schulteplus.data.ExResult.TIMESTAMP_FIELD_NAME;


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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.nebobrod.schulteplus.ExerciseRunner;
import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.data.Achievement;
import org.nebobrod.schulteplus.data.ExResult;
import org.nebobrod.schulteplus.data.OrmRepo;
import org.nebobrod.schulteplus.databinding.FragmentDashboardBinding;
import org.nebobrod.schulteplus.fbservices.AchievementsFbData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements AchievementsFbData.DashboardCallback, OrmRepo.OrmGetCallback {
	private static final String TAG = "Dashboard";

	DashboardViewModel dashboardViewModel;
	private FragmentDashboardBinding binding;
	String[] exTypeValues, exTypeEntries; // Arrays for spinner exType
	Spinner spDashboard;
	RadioGroup rgSource;
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
		spDashboard = binding.spDashboard;
		setDashboardSpinner(spDashboard);
		rgSource = binding.rgSource;
		elvChart = binding.elvDashboard;
		// Copy data to clipboard
		elvChart.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
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
			}
		});

//		local achievement datasource
		listAchievement = new ArrayList<>(); // OrmRepo.getAchievementList();
		arrayAdapter = Achievement.getArrayAdapter(getAppContext(), (List<Achievement>) Utils.markupListAsGroupedBy(listAchievement, TIMESTAMP_FIELD_NAME));
		elvChart.setAdapter(arrayAdapter);
		arrayAdapter.notifyDataSetChanged();

//		firebase achievement datasource (Spanned yet)
		list = new ArrayList<>();
		adapter = new ArrayAdapter<>(this.getActivity(), R.layout.layout_one_textview, list);
		elvChart.setAdapter(adapter);
		AchievementsFbData.basicQueryValueListener(DashboardFragment.this::onCallback, list);
		adapter.notifyDataSetChanged();

//		ExResult adapter
		listExResult = new ArrayList<ExResult>(); //(ArrayList<ExResult>) dashboardViewModel.getResultsLiveData().getValue();
		exResultAdapter = ExResult.getArrayAdapter(getAppContext(), (List<ExResult>) Utils.markupListAsGroupedBy(listExResult, TIMESTAMP_FIELD_NAME));
		elvChart.setAdapter(exResultAdapter);
		exResultAdapter.notifyDataSetChanged();

		rgSource.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				updateListView(checkedId);
			}
		});

		return root;
	}

	private View.OnLongClickListener copyData = view -> {
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

	/**
	 * Spinner to choose the dashboard (Achievements or an ExType)
	 */
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
				android.R.layout.simple_spinner_item,
				exTypeEntries);
		// set simple layout resource file for each item of spinner
		// spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Set the ArrayAdapter data on the Spinner which binds data to spinner
		spinner.setAdapter(spAdapter);
		spAdapter.notifyDataSetChanged();

		// Set livedata Key when Dashboard spinner changed
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
				dashboardViewModel.setKey(exTypeValues[position]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
				// do nothing
			}
		});
	}

	private ArrayList<Spanned> exResultToSpanned(List<? extends ExResult> results) {
		// get array from our LiveData
		Object[] resList;
		if (results == null) {
			resList = new ExResult[1];
			resList[0] = new ExResult(100, 0, 0, "no");
		} else {
			resList = results.stream().toArray();
		}
		ArrayList<Spanned> listSpanned = new ArrayList<>();
		// collect list of Spanned
		for (Object o:
				resList) {
			listSpanned.add(Html.fromHtml(o.toString()));
		}
		return listSpanned;
	}

	private void updateListView(int checkedRadioButtonId) {
		String _key = (dashboardViewModel.getKey().getValue() == null ? "gcb_achievements" : dashboardViewModel.getKey().getValue());
		if (_key.equals("gcb_achievements")) {
			// only for Achievements yet
			switch(checkedRadioButtonId)
			{
				case R.id.rb_local:
					OrmRepo.achieveGet25(DashboardFragment.this::onComplete);
					arrayAdapter = Achievement.getArrayAdapter(getAppContext(), (List<Achievement>) Utils.markupListAsGroupedBy(listAchievement, TIMESTAMP_FIELD_NAME));
					elvChart.setAdapter(arrayAdapter);
					break;
				case R.id.rb_www:
					adapter = new ArrayAdapter<>(getAppContext(), R.layout.layout_one_textview, list);
					elvChart.setAdapter(adapter);
					AchievementsFbData.basicQueryValueListener(DashboardFragment.this::onCallback, list);
					break;
				default:
					// do nothing
			}
		} else {
			// fetch from local DB
			exResultAdapter = ExResult.getArrayAdapter(getAppContext(), (List<ExResult>) Utils.markupListAsGroupedBy(listExResult, TIMESTAMP_FIELD_NAME));
			dashboardViewModel.fetchResultsLimited(ExResult.class);

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
			exResultAdapter = ExResult.getArrayAdapter(getAppContext(), (List<ExResult>) Utils.markupListAsGroupedBy(listExResult, TIMESTAMP_FIELD_NAME));
			elvChart.setAdapter(exResultAdapter);
			exResultAdapter.notifyDataSetChanged();
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		// Set chosen Item in the spinner
		for (int i = 0; i < exTypeValues.length; i++) {
			if (ExerciseRunner.getExType().equals(exTypeValues[i])) {
				spDashboard.setSelection(i);
			}
		}
		updateListView(rgSource.getCheckedRadioButtonId());
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

	@Override
	public void onCallback(ArrayList<Spanned> lst) {
		list = (ArrayList<Spanned>) lst.clone();
		adapter.notifyDataSetChanged();
//		Html.fromHtml("");
	}

	@Override
	public void onComplete(Object result) {
//		listAchievement = (ArrayList<Achievement>) result;
		listAchievement = (ArrayList<Achievement>) Utils.markupListAsGroupedBy((ArrayList<Achievement>) result, TIMESTAMP_FIELD_NAME);
		arrayAdapter.notifyDataSetChanged();
	}
}