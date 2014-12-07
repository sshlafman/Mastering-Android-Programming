package com.sshlafman.map_editor;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class FilesList extends ListActivity {
	private String[] filesList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		filesList = getFilesDir().list();
		ListAdapter adapter = new ArrayAdapter<String>(this, R.layout.filelist_row,
				R.id.list_row_filename, filesList);
		
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra("fName", filesList [position]);
		
		setResult(RESULT_OK, resultIntent);
		this.finish();
	}
	
	
}
