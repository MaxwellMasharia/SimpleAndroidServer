package code.maxwell.simpleandroidhttpserver;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    boolean isServerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button mButton = findViewById(R.id.btn_start_server);
        TextView textView = findViewById(R.id.text_view);
        textView.setText(getIPAddress());
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServer();
            }
        });
//        try {
//            InputStream dataIn = getAssets().open("dir/test.txt");
//            int dataSize = dataIn.available();
//            byte[] buffer = new byte[dataSize];
//            int readBytes = dataIn.read(buffer);
//            Log.d(TAG, "onCreate: >> Read " + readBytes + " bytes");
//            StringBuilder sb = new StringBuilder();
//            for (byte b : buffer) {
//                sb.append((char) b);
//            }
//
//            Log.d(TAG, "onCreate: >> Text \n" + sb.toString());
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void startServer() {
        Intent intent = new Intent(MainActivity.this, ServerService.class);
        startService(intent);
    }

    private String getIPAddress() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (wifiManager != null) {
            return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        }
        return null;
    }
}
