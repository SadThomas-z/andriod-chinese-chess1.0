package com.XQ.xq1_0;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class XQActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_xq);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.xq, menu);
		return true;
	}

}
