package com.schaff.clickatellsample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

/**
 * This is the mains activity that links to all the others in this sample app.
 *
 * @author Dominic Schaff <dominic.schaff@gmail.com>
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.act_http).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HttpActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.act_rest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RestActivity.class);
                startActivity(intent);
            }
        });
    }
}
