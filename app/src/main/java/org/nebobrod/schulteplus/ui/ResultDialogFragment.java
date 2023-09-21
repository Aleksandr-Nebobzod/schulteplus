package org.nebobrod.schulteplus.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.nebobrod.schulteplus.R;

public class ResultDialogFragment extends DialogFragment  {
	private static final String TAG = "ResultDialogFragment";
	private String message;
	public OnInputListener mOnInputListener;

	public interface OnInputListener {
		void sendInput(boolean input);
	}

	// Create a new instance of ResultDialogFragment, providing "s" as an argument.
	static ResultDialogFragment newInstance(String s) {
		ResultDialogFragment f = new ResultDialogFragment();

		// Supply input as an argument.
		Bundle args = new Bundle();
		args.putString("message", s);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		message = getArguments( ).getString("message");
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		//View view = inflater.inflate(R.layout.activity_schulte_result_df, null);
/*		TextView txtTitle, txtMessage;
		Button btnOk, btnCancel;

		txtMessage = view.findViewById(R.id.txtMessage);
		btnCancel = view.findViewById(R.id.btnCancel);
		btnOk = view.findViewById(R.id.btnOK);

		getDialog().setTitle("R.e.s.u.l.t");
		txtMessage.setText(message);

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doNegativeClick();
			}
		});

		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				doPositiveClick();
			}
		});*/

		//return view;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

//that was 1-st attempt it's not perfect (make standard dialog)
// move this functionality to onViewCreated ^... nope that doesn't work -- shows too late
	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.title_result)
				.setMessage(message)
				.setPositiveButton(R.string.alert_dialog_ok,
						(dialogInterface, i) -> doPositiveClick())
				.setNegativeButton(R.string.alert_dialog_cancel,
						(dialogInterface, i) -> doNegativeClick())
				.create();
//		return super.onCreateDialog(savedInstanceState);
	}

	private void doPositiveClick() {
		mOnInputListener.sendInput(true);
		getDialog().dismiss();
	}

	private void doNegativeClick() {
		mOnInputListener.sendInput(false);
		this.dismiss();
	}

	@Override public void onAttach(Context context) {
		super.onAttach(context);
		try {
			mOnInputListener
					= (OnInputListener)getActivity();
		}
		catch (ClassCastException e) {
			Log.e(TAG, "onAttach: ClassCastException: "
					+ e.getMessage());
		}
	}

}
