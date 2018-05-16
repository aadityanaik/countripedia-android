package com.halfwitdevs.countripedia;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class URLHandler {
    URL link;

    public URLHandler(String url) throws MalformedURLException {
        link = new URL(url);
    }

    public String getResponse() {
        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) link.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            Scanner input = new Scanner
                    (new InputStreamReader(connection.getInputStream()));

            StringBuilder output = new StringBuilder();

            while(input.hasNextLine()) {
                output.append(input.nextLine());
            }

            connection.disconnect();

            return output.toString();
        } catch (IOException e) {
            if(connection != null) {
                connection.disconnect();
            }
            // could not initialize connection
            return null;
        }
    }
}
