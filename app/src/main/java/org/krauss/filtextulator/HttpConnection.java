package org.krauss.filtextulator;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by jrkrauss on 3/04/17.
 */

public class HttpConnection {

    private String ip_address;

    public HttpConnection(String ip_address) {
        this.ip_address = ip_address;
    }

    /**
     * Opens a HTTP connection with the server
     *
     * @return  OK if the connection succeed
     */
    public String checkConnection() {

        String result = "";

        try {
            URL url = new URL("http://" + ip_address + ":8080");


            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                result = "OK";
            } else {
                result = "NOK";
            }
            httpConn.disconnect();

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return result;
    }

    /**
     * Returns the filter list available in the server
     *
     * @param random  true: If the user pressed the Random button, otherwise: false
     * @return  A JSON with the filter list available in the server. If Random = true, returns randomly 4 filters at a time
     */
    public InputStream getFilterList(boolean random) {

        InputStream in = null;

        try {

            in = getConnection(new URL("http://" + ip_address + ":8080/Filtextulator/" + (random ? "getranflist" : "getflist")));

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        return in;
    }

    /**
     * Send the list of filters and the text to be processed and returned
     *
     * @param f  Filters list to be processed
     * @param t  Text typed by the user in which the filters will be applied
     * @return  A JSON containing the accumulative result for each filter applied on the text
     */
    public InputStream processFilterList(List<String> f, String t) {

        String filterlist = "";
        String textToProcess = "'" + t + "'";
        InputStream in = null;


        for (String s : f) {
            filterlist += s + ",";
        }

        filterlist = filterlist.replaceAll("\\s+", "_").toLowerCase().substring(0, filterlist.length() - 1);

        try {

            in = getConnection(new URL("http://" + ip_address + ":8080/Filtextulator/processflist?filters=" + filterlist + "&text=" + textToProcess));

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }

        return in;
    }


    private InputStream getConnection(URL u) {

        URL url = u;
        URLConnection urlConn;
        HttpURLConnection httpConn;
        InputStream result = null;

        try {

            urlConn = url.openConnection();

            if (!(urlConn instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }

            httpConn = (HttpURLConnection) urlConn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {

                result = httpConn.getInputStream();

            }
            httpConn.disconnect();

        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return result;
    }
}
