package org.nebobrod.schulteplus.ui.dashboard;

import static org.nebobrod.schulteplus.Utils.getAppContext;
import static org.nebobrod.schulteplus.Utils.timeStampDateLocal;
import static org.nebobrod.schulteplus.Utils.timeStampTimeLocal;

import android.content.Context;
import android.os.Bundle;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.data.Achievement;
import org.nebobrod.schulteplus.data.OrmUtils;
import org.nebobrod.schulteplus.databinding.FragmentDashboardBinding;
import org.nebobrod.schulteplus.fbservices.AchievementsFbData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements AchievementsFbData.DashboardCallback, OrmUtils.OrmGetCallback {
	private static final String TAG = "Dashboard";

	private FragmentDashboardBinding binding;
	ListView elvChart;
	RadioGroup rgSource;
	ArrayList<Spanned> list;
	ArrayList<Achievement> listAchievement;
	ArrayAdapter<Spanned> adapter;
	ArrayAdapter<Achievement> arrayAdapter;

	public View onCreateView(@NonNull LayoutInflater inflater,
							 ViewGroup container, Bundle savedInstanceState) {
		DashboardViewModel dashboardViewModel =
				new ViewModelProvider(this).get(DashboardViewModel.class);

		binding = FragmentDashboardBinding.inflate(inflater, container, false);
		View root = binding.getRoot();

		final TextView textView = binding.textDashboard;
		textView.setText(R.string.txt_dashboard_desc);
		dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

		elvChart = binding.elvDashboard;
		rgSource = binding.rgSource;

//		local datasource
		listAchievement = OrmUtils.getAchievementList();
		arrayAdapter = new AchievementsAdapter(getAppContext(), R.layout.fragment_dashboard_elv_item, listAchievement);
		elvChart.setAdapter(arrayAdapter);
		arrayAdapter.notifyDataSetChanged();

//		firebase datasource (Spanned yet)
		list = new ArrayList<>();
		adapter = new ArrayAdapter<>(this.getActivity(), R.layout.layout_one_textview, list);
		elvChart.setAdapter(adapter);
		AchievementsFbData.basicQueryValueListener(DashboardFragment.this::onCallback, list);
		adapter.notifyDataSetChanged();

		rgSource.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				switch(checkedId)
				{
					case R.id.rb_local:
						OrmUtils.achieveGet25(DashboardFragment.this::onComplete);
						arrayAdapter = new AchievementsAdapter(getAppContext(), R.layout.fragment_dashboard_elv_item, listAchievement);
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
			}
		});

		return root;
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
		listAchievement = (ArrayList<Achievement>) result;
		arrayAdapter.notifyDataSetChanged();
	}


	private static class AchievementsAdapter extends ArrayAdapter<Achievement> {

		public AchievementsAdapter(Context context, int textViewResourceId, List<Achievement> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.fragment_dashboard_elv_item, null);
			}
			Achievement achievement = getItem(position);

			fillText(v, R.id.tv_name, "" + (position + 1)); // achievement.getName() -- not needed in personal list
			fillText(v, R.id.tv_date, timeStampDateLocal(achievement.getTimeStamp()));
			fillText(v, R.id.tv_time, timeStampTimeLocal(achievement.getTimeStamp()));
			fillText(v, R.id.tv_record_text, achievement.getRecordText());
			fillText(v, R.id.tv_record_value, achievement.getRecordValue());
			fillText(v, R.id.tv_special_mark, achievement.getSpecialMark());

			return v;
		}

		private <T> void fillText(View v, int id, T text) {
			TextView textView = (TextView) v.findViewById(id);
			textView.setText((CharSequence) text);
		}
	}



}