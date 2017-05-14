package ro.pub.cs.systems.eim.lab06.singlethreadedserver.network;

import android.util.Log;
import android.widget.EditText;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import ro.pub.cs.systems.eim.lab06.singlethreadedserver.general.Constants;
import ro.pub.cs.systems.eim.lab06.singlethreadedserver.general.Utilities;

public class ServerThread extends Thread {

    private boolean isRunning;

    private ServerSocket serverSocket;
    private EditText serverTextEditText;

    private class CommunicationThread extends Thread {
        private Socket socket;

        public CommunicationThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {

            try {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                PrintWriter printWriter = Utilities.getWriter(socket);
                printWriter.println(serverTextEditText.getText().toString());
                socket.close();
                Log.v(Constants.TAG, "Connection closed");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ServerThread(EditText serverEditText) {
        this.serverTextEditText = serverEditText;
    }

    public void startServer() {
        isRunning = true;
        start();
        Log.v(Constants.TAG, "startServer() method invoked " + serverSocket);
    }

    public void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
        Log.v(Constants.TAG, "stopServer() method invoked");
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(Constants.SERVER_PORT);
            while (isRunning) {
                Socket socket = serverSocket.accept();
                new CommunicationThread(socket).start();
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }
}
