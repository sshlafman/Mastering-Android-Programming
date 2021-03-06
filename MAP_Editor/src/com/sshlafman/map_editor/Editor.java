package com.sshlafman.map_editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class Editor extends Activity {
	private static final String TAG = "AlertDialogFragment";

	private static String dialogTypeKey = "type";
	private static final int SAVE_DIALOG = 1;
	private static final int SHOWPATH_DIALOG = 2;

	private static final int LOAD_FILE_REQ_CODE = 234;

	private String filename = null;

	public String getFilename() {
		return filename;
	}

	public static class AlertDialogFragment extends DialogFragment {

		public static AlertDialogFragment newInstance(int type) {
			AlertDialogFragment frag = new AlertDialogFragment();
			Bundle args = new Bundle();

			args.putInt(dialogTypeKey, type);
			frag.setArguments(args);

			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog dialog = null;
			int dialogType = getArguments().getInt(dialogTypeKey);

			switch (dialogType) {
			case SAVE_DIALOG:
				dialog = create_save_dialog();
				break;
			case SHOWPATH_DIALOG:
				dialog = create_showpath_dialog();
				break;
			default:
				dialog = null;
			}

			return dialog;
		}

		private AlertDialog create_save_dialog() {
			AlertDialog dialog;
			AlertDialog.Builder saveDialogBuilder;
			LayoutInflater layoutInflater = getActivity().getLayoutInflater();
			final View dialogLayout = layoutInflater.inflate(
					R.layout.save_dialog, (ViewGroup) getActivity()
							.findViewById(R.layout.main), false);

			saveDialogBuilder = new AlertDialog.Builder(getActivity());
			saveDialogBuilder
					.setCancelable(false)
					.setPositiveButton(R.string.save_button,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									((Editor) getActivity())
											.save_dialog_positive_button_click(
													dialog, dialogLayout);
								}
							})
					.setNegativeButton(R.string.cancel_button,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									((Editor) getActivity())
											.save_dialog_negative_click(dialog);
								}
							});

			saveDialogBuilder.setView(dialogLayout);
			dialog = saveDialogBuilder.create();
			return dialog;
		}

		private AlertDialog create_showpath_dialog() {
			AlertDialog.Builder showpathDialogBuilder = new AlertDialog.Builder(
					getActivity());
			
			String pathString = getActivity().getFilesDir().toString();
			String fileName = ((Editor)getActivity()).getFilename(); 
			if (fileName != null) {
				pathString += File.separator + fileName;
			}

			AlertDialog dialog = showpathDialogBuilder
					.setMessage(pathString)
					.setCancelable(false)
					.setPositiveButton(R.string.ok_button,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									((Editor) getActivity())
											.showpath_dialog_positive_click(dialog);
								}
							}).create();

			return dialog;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	protected void save_dialog_positive_button_click(DialogInterface dialog,
			View dialogLayout) {
		TextView etFileName = (TextView) dialogLayout
				.findViewById(R.id.et_filename);
		filename = etFileName.getText().toString();

		if (filename == null) {
		} else {
			Log.i(TAG, "The filename is " + filename);
			performSaveFile(filename);
			dialog.dismiss();
		}
	}

	protected void save_dialog_negative_click(DialogInterface dialog) {
		dialog.cancel();
	}

	protected void showpath_dialog_positive_click(DialogInterface dialog) {
		dialog.dismiss();
	}

	private void performSaveFile(String f) {
		TextView tvFilename = (TextView) findViewById(R.id.filename);
		tvFilename.setText(f);

		EditText et_content = (EditText) findViewById(R.id.et_content);
		String content = et_content.getText().toString();

		try {
			FileOutputStream out = openFileOutput(f, Context.MODE_PRIVATE);
			out.write(content.getBytes());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void performLoadFile(String f) {
		StringBuilder content = new StringBuilder();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					getFilesDir() + File.separator + f));

			try {
				String line = null;

				while ((line = reader.readLine()) != null) {
					content.append(line);
					content.append(System.getProperty("line.separator"));
				}
			} finally {
				reader.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		TextView tvFilename = (TextView) findViewById(R.id.filename);
		tvFilename.setText(f);

		EditText et_content = (EditText) findViewById(R.id.et_content);
		et_content.setText(content);
	}

	private void showAlertDialog(int dialog_type) {
		DialogFragment newFragment = AlertDialogFragment
				.newInstance(dialog_type);
		newFragment.show(getFragmentManager(), "dialog");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.editor_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean retVal = false;

		switch (item.getItemId()) {
		case R.id.new_menu_item:
			new_file();
			retVal = true;
			break;

		case R.id.save_menu_item:
			save_file();
			retVal = true;
			break;

		case R.id.load_menu_item:
			load_file();
			retVal = true;
			break;

		case R.id.showpath_menu_item:
			show_path();
			retVal = true;
			break;

		case R.id.quit_menu_item:
			quit();
			retVal = true;
			break;

		default:
			retVal = super.onOptionsItemSelected(item);
		}

		return retVal;
	}

	private void new_file() {
		filename = null;
		EditText content = (EditText) findViewById(R.id.et_content);
		content.setText("");

		TextView fname = (TextView) findViewById(R.id.filename);
		fname.setText(getString(R.string.empty_filename));
	}

	private void save_file() {
		if (filename != null) {
			performSaveFile(filename);
		} else {
			showAlertDialog(SAVE_DIALOG);
		}
	}

	private void load_file() {
		Intent intent = new Intent(this, FilesList.class);
		startActivityForResult(intent, LOAD_FILE_REQ_CODE);
	}

	private void show_path() {
		showAlertDialog(SHOWPATH_DIALOG);
	}

	private void quit() {
		this.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == LOAD_FILE_REQ_CODE) {
			if (resultCode == RESULT_OK) {
				String fName = data.getStringExtra("fName");
				filename = fName;
				performLoadFile(fName);
			}
		}
	}

}
