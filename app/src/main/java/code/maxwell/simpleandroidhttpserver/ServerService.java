package code.maxwell.simpleandroidhttpserver;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerService extends Service {
    private static final String TAG = "ServerService";
    private static final String MY_IP_ADDRESS = "192.168.137.26";
    Notification notification;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notification = new NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_server)
                .setContentTitle("Server Started")
                .setContentText("Server is running ...")
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(101, notification);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return START_STICKY;
    }

    private void startServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        Log.d(TAG, "startServer: >> Server started ... Listening @port " + serverSocket.getLocalSocketAddress());

        while (true) {
            final Socket socket = serverSocket.accept();
            Log.d(TAG, "startServer: >> Server connected to the client >> " + socket.getRemoteSocketAddress());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream dataIn = socket.getInputStream();
                        OutputStream dataOut = socket.getOutputStream();

                        HashMap<String, String> headers = getRequestHeaders(dataIn);
                        String requestedPath = headers.get("PATH");
                        assert requestedPath != null;
                        if (requestedPath.equals("/")) {
                            InputStream fileIn = getAssets().open("web/index.html");
                            int fileSize = fileIn.available();
                            byte[] buffer = new byte[fileSize];
                            int readBytes = fileIn.read(buffer);
                            fileIn.close();
                            Log.d(TAG, "Server:  >> Read "+readBytes);

                            /*Send the response Headers*/
                            PrintWriter printWriter = new PrintWriter(dataOut);
                            printWriter.println("HTTP/1.1 200 OK");
                            printWriter.println("Content-Length : "+fileSize);
                            printWriter.println("Content-Type : text/html");
                            printWriter.println();
                            printWriter.flush();

                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(dataOut);
                            bufferedOutputStream.write(buffer);
                            bufferedOutputStream.flush();
                        }

                        dataIn.close();
                        dataOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private String readLine(InputStream inputStream) throws IOException {
        boolean isFirstLine = true;
        StringBuilder sb = new StringBuilder();
        while (true) {
            int readByte = inputStream.read();
            if (readByte == -1) {
                if (isFirstLine) {
                    return null;
                } else {
                    return sb.toString();
                }
            } else if (readByte == 10) {
                return sb.toString();
            }
            sb.append((char) readByte);
            isFirstLine = false;
        }
    }

    private HashMap<String, String> getRequestHeaders(InputStream inputStream) throws IOException {
        HashMap<String, String> headers = new HashMap<>();
        while (true) {
            String line = readLine(inputStream);
            if (line != null) {
                if (line.trim().length() == 0) {
                    return headers;
                }
                if (line.startsWith("GET") || line.startsWith("POST")) {
                    String key = "PATH";
                    String value = line.split(" ")[1];
                    headers.put(key, value);
                } else {
                    String key = line.split(":")[0];
                    String value = line.split(":")[1];
                    headers.put(key, value);
                }
            }
        }
    }
}
