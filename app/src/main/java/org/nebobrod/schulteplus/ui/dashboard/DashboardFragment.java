package org.nebobrod.schulteplus.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.nebobrod.schulteplus.R;
import org.nebobrod.schulteplus.Utils;
import org.nebobrod.schulteplus.databinding.FragmentDashboardBinding;
import org.nebobrod.schulteplus.fbservices.AchievementsFbData;
import org.nebobrod.schulteplus.fbservices.AchievementsHelper;

import java.util.ArrayList;

public class DashboardFragment extends Fragment implements AchievementsFbData.DashboardCallback {
	private static final String TAG = "Dashboard";

	private FragmentDashboardBinding binding;
	ListView elvChart;
	ArrayList<String> list;
	ArrayAdapter<String> adapter;

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

		list = new ArrayList<>();
		adapter = new ArrayAdapter<>(this.getActivity(), R.layout.fragment_dashboard_elv_item, list);
		elvChart.setAdapter(adapter);



//		retrieveAchievements1();

		AchievementsFbData.basicQueryValueListener(this, list);
		adapter.notifyDataSetChanged();

		return root;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		binding = null;
	}

	private void retrieveAchievements1() {
		DatabaseReference reference = FirebaseDatabase.getInstance("https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app").getReference().child("achievements");

		reference.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				list.clear();
				for (DataSnapshot ds : snapshot.getChildren()) {
					AchievementsHelper achieve = ds.getValue(AchievementsHelper.class);
					String achieveStr = "|* " + Utils.timeStampFormattedShort(achieve.getTimeStamp()) + " || " + achieve.getName() + " || " + achieve.getRecordText()	+ ": " + achieve.getRecordValue() + " || " + achieve.getSpecialMark() + " |";
					list.add(achieveStr);
				}
				adapter.notifyDataSetChanged();

			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}

	private void retrieveAchievements2() {
		DatabaseReference reference = FirebaseDatabase.getInstance("https://schulte-plus-default-rtdb.europe-west1.firebasedatabase.app").getReference();
		Query recentAchievesQuery = reference.child("achievements")
				.limitToLast(10).orderByValue();
		list.clear();
		recentAchievesQuery.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

				AchievementsHelper achieve = snapshot.getValue(AchievementsHelper.class);
				String achieveStr = "|* " + Utils.timeStampFormattedShort(achieve.getTimeStamp()) + " || " + achieve.getName() + " || " + achieve.getRecordText()	+ ": " + achieve.getRecordValue() + " || " + achieve.getSpecialMark() + " |";
				list.add(achieveStr);

			}

			@Override
			public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
				AchievementsHelper achieve = snapshot.getValue(AchievementsHelper.class);
				String achieveStr = "|* " + Utils.timeStampFormattedShort(achieve.getTimeStamp()) + " || " + achieve.getName() + " || " + achieve.getRecordText()	+ ": " + achieve.getRecordValue() + " || " + achieve.getSpecialMark() + " |";
				list.add(achieveStr);
			}

			@Override
			public void onChildRemoved(@NonNull DataSnapshot snapshot) {
				AchievementsHelper achieve = snapshot.getValue(AchievementsHelper.class);
				String achieveStr = "|* " + Utils.timeStampFormattedShort(achieve.getTimeStamp()) + " || " + achieve.getName() + " || " + achieve.getRecordText()	+ ": " + achieve.getRecordValue() + " || " + achieve.getSpecialMark() + " |";
				list.add(achieveStr);
			}

			@Override
			public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
				AchievementsHelper achieve = snapshot.getValue(AchievementsHelper.class);
				String achieveStr = "|* " + Utils.timeStampFormattedShort(achieve.getTimeStamp()) + " || " + achieve.getName() + " || " + achieve.getRecordText()	+ ": " + achieve.getRecordValue() + " || " + achieve.getSpecialMark() + " |";
				list.add(achieveStr);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				list.clear();
				list.add(error.getMessage());
				adapter.notifyDataSetChanged();
			}
		});
		adapter.notifyDataSetChanged();

	}

	@Override
	public void onCallback(ArrayList<String> lst) {
		list = (ArrayList<String>) lst.clone();
		adapter.notifyDataSetChanged();
	}

/*	private AchievementsFbData.DashboardCallback dashboardCallback = new AchievementsFbData.DashboardCallback() {
		@Override
		public void onCallback(ArrayList<String> lst) {
			list = (ArrayList<String>) lst.clone();
			adapter.notifyDataSetChanged();
			Log.d(TAG, "onCallback touched : " );
		}
	};*/


}