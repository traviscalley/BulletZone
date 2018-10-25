package tgc1002.gridsim;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity
{
    // class variables
    private TextView historyText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);

        String historyData = getIntent().getExtras().getString("HistoryData");

        historyText = findViewById(R.id.historyText);
        historyText.setText(historyData);
        historyText.setMovementMethod(new ScrollingMovementMethod());
    }
}