package com.example.pdfviewer;

import java.io.File;
import java.util.concurrent.TimeUnit;

import com.qoppa.viewer.QPDFViewerView;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
				
		super.onCreate(savedInstanceState);
		
		try{
		    QPDFViewerView viewer = new QPDFViewerView (this);
		    viewer.setActivity(this);
		    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		    File file = new File(path, "architecture.pdf");
		    viewer.loadDocument(file.getPath());
		    viewer.showHideToolbar();
		    setContentView(viewer);
		    
		    for(int i = viewer.getCurrentPageNumber(); i < viewer.getDocument().getPageCount();++i){
		    	viewer.goToPage(i);
		    	setContentView(viewer);
		    	TimeUnit.SECONDS.sleep(2);
		    }

		}
		catch(Exception e){
			System.out.print(e);
		}
		
		
		/*
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		*/
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
