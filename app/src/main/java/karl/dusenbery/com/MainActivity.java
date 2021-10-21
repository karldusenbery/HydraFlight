package karl.dusenbery.com;

import android.app.Application;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.amplitude.api.Amplitude;
import com.amplitude.api.AmplitudeClient;
import com.amplitude.api.AmplitudeUserProvider;
import com.amplitude.experiment.Experiment;
import com.amplitude.experiment.ExperimentClient;
import com.amplitude.experiment.ExperimentConfig;
import com.amplitude.experiment.ExperimentUser;
import com.amplitude.experiment.Variant;

import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private Button mStartButton;
    private Button mPauseButton;
    private Button mResetButton;

    private Button mPlusWaterButton;
    private Button mPlusCoffeeButton;



    private TextView mPlusWater;
    private TextView mPlusCoffee;

    private Chronometer mChronometer;

    private long lastPause;

    private int mDrank = 0;
    private int mCoffeesDrank = 0;
    private int mDrinkNow = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartButton = (Button) findViewById(R.id.start_button);
        mPauseButton = (Button) findViewById(R.id.pause_button);
        mResetButton = (Button) findViewById(R.id.reset_button);

        mPlusWaterButton = (Button) findViewById(R.id.btnPlusWater);
        mPlusWater = (TextView) findViewById(R.id.tvDrank);

        mPlusCoffeeButton = (Button) findViewById(R.id.btnPlusCoffee);
        mPlusCoffee = (TextView) findViewById(R.id.tvCoffeesDrank);

        mChronometer = (Chronometer) findViewById(R.id.chronometer);

        mStartButton = (Button) findViewById(R.id.start_button);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lastPause != 0){
                    mChronometer.setBase(mChronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
                }
                else{
                    mChronometer.setBase(SystemClock.elapsedRealtime());
                }

                mChronometer.start();
                mStartButton.setEnabled(false);
                mPauseButton.setEnabled(true);
            }
        });

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lastPause = SystemClock.elapsedRealtime();
                mChronometer.stop();
                mPauseButton.setEnabled(false);
                mStartButton.setEnabled(true);
            }
        });

        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChronometer.stop();
                mChronometer.setBase(SystemClock.elapsedRealtime());
                lastPause = 0;
                mStartButton.setEnabled(true);
                mPauseButton.setEnabled(false);
            }
        });

        mPlusWaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrank++;
                String sDrank;
                sDrank = "Drank: " + Integer.toString(mDrank);
                mPlusWater.setText(sDrank);
            }
        });

        mPlusCoffeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCoffeesDrank++;
                String sCoffeesDrank;
                sCoffeesDrank = Integer.toString(mCoffeesDrank);
                mPlusCoffee.setText(sCoffeesDrank);
            }
        });


        //--- Amplitude initialization (4 parts) ---

        // (1) Set the environment's API key
        String apiKey = "client-38IdDywfH8nJz7wvZF1C1nBSX8nzg82s";

        // --- Initializing the Amplitude Analytics client here ---
        AmplitudeClient amplitudeClient = Amplitude.getInstance();
        amplitudeClient.initialize(this, apiKey);

        // Set a User ID for the user.
        String userId = "john.dusenbery@gmail.com";
        amplitudeClient.setUserId(userId);

        // (2) Configure and initialize the experiment client
        ExperimentConfig config = new ExperimentConfig();
        ExperimentClient client = Experiment.initialize(getApplication(), apiKey, config);

        // Set the user provider before fetching variants
        client.setUserProvider(new AmplitudeUserProvider(amplitudeClient));

        // No need to pass a user into the client fetch the user
        // will be populated by the AmplitudeUserProvider
        Future<ExperimentClient> future = client.fetch(null);

        // (3) Fetch variants for a user
        ExperimentUser user = ExperimentUser.builder()
                .userId(userId)
                .deviceId("a9367070ab4dad1b")
                .userProperty("premium", true)
                .build();
        try {
            client.fetch(user).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // (4) Lookup a flag's variant
        String flagKey = "button-location";
        Variant variant = client.variant(flagKey);
        if (variant.is("big_under")) {
            // Flag is big_under
        } else {
            // Flag is off
        }


    }
}
