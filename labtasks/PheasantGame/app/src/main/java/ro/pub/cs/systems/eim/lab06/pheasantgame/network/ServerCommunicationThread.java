package ro.pub.cs.systems.eim.lab06.pheasantgame.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Random;

import ro.pub.cs.systems.eim.lab06.pheasantgame.general.Constants;
import ro.pub.cs.systems.eim.lab06.pheasantgame.general.Utilities;

public class ServerCommunicationThread extends Thread {

    private Socket socket;
    private TextView serverHistoryTextView;

    private Random random = new Random();

    private String expectedWordPrefix = new String();

    public ServerCommunicationThread(Socket socket, TextView serverHistoryTextView) {
        if (socket != null) {
            this.socket = socket;
            Log.d(Constants.TAG, "[SERVER] Created communication thread with: " + socket.getInetAddress() + ":" + socket.getLocalPort());
        }
        this.serverHistoryTextView = serverHistoryTextView;
    }

    public void run() {
        try {
            if (socket == null) {
                return;
            }
            boolean isRunning = true;
            BufferedReader requestReader = Utilities.getReader(socket);
            PrintWriter responsePrintWriter = Utilities.getWriter(socket);
            String line;

            while (isRunning) {

                // TODO exercise 7afdsfsd
                line = requestReader.readLine();
                System.out.println("[SERVER] primeste " + line);
                if (line.equals(Constants.END_GAME))
                    break;
                //serverHistoryTextView.setText(serverHistoryTextView.getText().toString() + "Server received " + line + "\n");

                if (!Utilities.wordValidation(line))
                    responsePrintWriter.println(line);
                else {

                    if (!line.startsWith(expectedWordPrefix)) {
                        responsePrintWriter.println(line);
                    }
                    else {
                        String prefix = line.substring(line.length() - 2, line.length());
                        List<String> wordList = Utilities.getWordListStartingWith(prefix);

                        if (wordList.isEmpty()) {
                            responsePrintWriter.println(Constants.END_GAME);
                            //serverHistoryTextView.setText(serverHistoryTextView.getText().toString() + "Server sent " + Constants.END_GAME + "\n");
                        }
                        else {
                            int index = random.nextInt(wordList.size());
                            String word = wordList.get(index);
                            System.out.println("[SERVER] trimite " + word);
                            responsePrintWriter.println(word);
                            //serverHistoryTextView.setText(serverHistoryTextView.getText().toString() + "Server sent " + word + "\n");
                            expectedWordPrefix = word.substring(word.length() - 2, word.length());
                        }
                    }
                }
            }
            socket.close();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }
}
