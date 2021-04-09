package org.kawanfw.sql.android;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aceql.sdk.jdbc.examples.MyRemoteConnection;

import org.kawanfw.test.util.MessageDisplayer;

import java.io.IOException;
import java.sql.SQLException;

public class MainActivity extends Activity implements View.OnClickListener {


    ScrollView scrollView;
    LinearLayout layout;
    LayoutParams params;
    LinearLayout mainLayout;
    Button but;
    Button quitButton;
    private Context thisOne;
    boolean clicked = false;

    // index of message displayed on screen
    private int idTv = 0;

    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {

	super.onCreate(savedInstanceState);
	MessageDisplayer.setActivity(this);

	MessageDisplayer.setVerbose(true);
	thisOne = this;
	layout = new LinearLayout(this);
	mainLayout = new LinearLayout(this);

	mainLayout.setOrientation(LinearLayout.VERTICAL);
	mainLayout.setLayoutParams(new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.FILL_PARENT,
		LinearLayout.LayoutParams.FILL_PARENT, 1));

	scrollView = new ScrollView(this);

	scrollView.setOverScrollMode(ScrollView.OVER_SCROLL_IF_CONTENT_SCROLLS);

	// Add button
	but = new Button(this);
	but.setText("Launch");
	but.setOnClickListener(this);

	quitButton = new Button(this);
	quitButton.setText("Close");
	quitButton.setOnClickListener(this);

	params = new LayoutParams(LayoutParams.WRAP_CONTENT,
		LayoutParams.WRAP_CONTENT);

	setText2(Version.getFullVersion());
	mainLayout.addView(but, params);

	// Embbed mainLayout in a ScrollView
	scrollView.addView(mainLayout);
	setContentView(scrollView);
    }

    public void onClick(View v) {

	if (v == quitButton) {
	    System.exit(1);
	}

	// createFiles();

	// SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSSS");
	// long now = System.currentTimeMillis();

	// Remove button from screen
	mainLayout.removeView(but);

	// setText2("Started  : " + sdf.format(new Date(now)));
    if(!clicked) {
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    clicked = true;
                    MyRemoteConnection.aceqlCodeToRun();
                } catch (SQLException | IOException | ClassNotFoundException e) {

                    e.printStackTrace();
                }
            }

        };
        t.start();
    }
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    if (msg != null && msg.obj != null) {
		// Print message on screen
		TextView valueTV = new TextView(thisOne);
		valueTV.setText(msg.obj.toString());
		valueTV.setId(idTv);
		mainLayout.addView(valueTV, idTv++);
		scrollView.smoothScrollTo(0, mainLayout.getBottom());

	    } else if (msg != null && msg.obj == null) {
		mainLayout.addView(quitButton);
		scrollView.smoothScrollTo(0, mainLayout.getBottom());
	    }

	}
    };
    public void displayMessage(String message) {
        setText2(message);
    }

    private void setText2(String msg) {
	Message message = Message.obtain(handler, 1, msg);
	handler.sendMessage(message);
    }

}