package com.schaff.clickatellsample;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an example of how to use the Clickatell REST API. NOTE: this is not
 * the only way, this is just an example. This class can also be used as a
 * library if you wish.
 *
 * @author Dominic Schaff <dominic.schaff@gmail.com>
 * @date Dec 2, 2014
 */
public class ClickatellRest {

    /**
     * @var The URL to use for the base of the REST API.
     */
   private static final String CLICKATELL_REST_BASE_URL = "https://api.clickatell.com/rest/";

    private static final int POST = 1, GET = 0, DELETE = 2;

    /**
     * @var The three private variables to use for authentication.
     */
    private String apiKey;

    /**
     * Create a REST object, and set the auth, but not test the auth.
     */
    public ClickatellRest(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * This will attempt to get your current balance.
     *
     * @return Your balance.
     * @throws Exception This will be thrown if your auth details were incorrect.
     */
    public double getBalance() throws Exception {
        // Send Request:
        String response = this.execute("account/balance", GET, null);
        JSONObject obj = new JSONObject(response);

        CheckForError(obj);

        JSONObject objData = obj.getJSONObject("data");
        return objData.getDouble("balance");
    }

    /**
     * This sends a single message.
     *
     * @param number  The number that you wish to send to. This should be in
     *                international format.
     * @param message The message you want to send,
     * @return The message ID. Or the error string.
     * @throws Exception This gets thrown on an auth failure.
     */
    public Message sendMessage(String number, String message) throws Exception {
        // Send Request:
        String response = this.execute("message", POST, "{\"to\":[\"" + number
                + "\"],\"text\":\"" + message + "\"}");
        JSONObject obj = new JSONObject(response);

        CheckForError(obj);
        Message msg = new Message();
        JSONObject objData = obj.getJSONObject("data");
        JSONArray msgArray = objData.getJSONArray("message");
        JSONObject firstMsg = msgArray.getJSONObject(0);
        msg.number = firstMsg.getString("to");
        if (!firstMsg.getBoolean("accepted")) {
            try {
                CheckForError(firstMsg);
            } catch (Exception e) {
                msg.error = e.getMessage();
            }
        } else {
            msg.message_id = firstMsg.getString("apiMessageId");
        }
        return msg;
    }

    /**
     * This is to send the same message to multiple people.
     *
     * @param numbers The array of numbers that are to be sent to.
     * @param message The message that you would like to send.
     * @return The hashmap that is returned is the number that was sent to, and
     * it's corresponding message ID, or error.
     * @throws Exception This gets thrown on auth errors.
     */
    public Message[] sendMessage(String[] numbers, String message)
            throws Exception {
        String number = numbers[0];
        for (int x = 1; x < numbers.length; x++) {
            number += "\",\"" + numbers[x];
        }
        ArrayList<Message> messages = new ArrayList<>();

        // Send Request:
        String response = this.execute("message", POST, "{\"to\":[\"" + number
                + "\"],\"text\":\"" + message + "\"}");
        JSONObject obj = new JSONObject(response);

        CheckForError(obj);
        JSONObject objData = obj.getJSONObject("data");
        JSONArray msgArray = objData.getJSONArray("message");
        for (int i = 0; i < msgArray.length(); i++) {
            Message msg = new Message();
            JSONObject firstMsg = msgArray.getJSONObject(i);
            msg.number = firstMsg.getString("to");
            if (!firstMsg.getBoolean("accepted")) {
                try {
                    CheckForError(firstMsg);
                } catch (Exception e) {
                    msg.error = e.getMessage();
                }
            } else {
                msg.message_id = firstMsg.getString("apiMessageId");
            }
            messages.add(msg);
        }
        return messages.toArray(new Message[0]);
    }

    /**
     * This will get the status and charge of the message given by the
     * messageId.
     *
     * @param messageId The message ID that should be searched for.
     * @return The returned hashmap contains all the information that could be
     * found for the message.
     * @throws Exception If there was an error with the request.
     */
    public Message getMessageStatus(String messageId) throws Exception {
        String response = this.execute("message/" + messageId, GET, null);
        JSONObject obj = new JSONObject(response);

        CheckForError(obj);

        Message msg = new Message();
        JSONObject objData = obj.getJSONObject("data");
        msg.message_id = objData.getString("apiMessageId");
        msg.charge = String.valueOf(objData.get("charge"));
        msg.status = objData.getString("messageStatus");
        msg.statusString = objData.getString("description");

        return msg;
    }

    /**
     * This will try to stop a message that has been sent. Note that only
     * messages that are going to be sent in the future can be stopped. Or if by
     * some luck you message has not been sent to the operator yet.
     *
     * @param messageId The message ID that is to be stopped.
     * @return Whether or not the message could be stopped.
     * @throws Exception If there was something wrong with the request.
     */
    public Message stopMessage(String messageId) throws Exception {
        // Send Request:
        String response = this.execute("message/" + messageId, DELETE, null);
        JSONObject obj = new JSONObject(response);

        CheckForError(obj);
        Message msg = new Message();
        JSONObject objData = obj.getJSONObject("data");
        msg.message_id = objData.getString("apiMessageId");
        msg.status = objData.getString("messageStatus");
        msg.statusString = objData.getString("description");

        return msg;
    }

    /**
     * This will allow you to use any feature of the API. Note that you can do
     * more powerful things with this function. And as such should only be used
     * once you have read the documentation, as the parameters are passed
     * directly to the API.
     *
     * @param numbers  The list of numbers that must be sent to.
     * @param message  The message that is to be sent.
     * @param features The extra features that should be included.
     * @return Message array will contain all the messages sent, with their details.
     * @throws Exception If there is anything wrong with the submission this will get
     *                   thrown.
     */
    public Message[] sendAdvancedMessage(String[] numbers,
                                         String message, HashMap<String, String> features) throws Exception {
        ArrayList<Message> messages = new ArrayList<>();
        String dataPacket = "{\"to\":[\"" + numbers[0];
        for (int x = 1; x < numbers.length; x++) {
            dataPacket += "\",\"" + numbers[x];
        }
        dataPacket += "\"],\"text\":\"" + message + "\"";
        for (Map.Entry<String, String> entry : features.entrySet()) {
            dataPacket += ",\"" + entry.getKey() + "\":\""
                    + entry.getValue() + "\"";
        }
        dataPacket += "}";

        // Send Request:
        String response = this.execute("message", POST, dataPacket);
        JSONObject obj = new JSONObject(response);

        CheckForError(obj);
        JSONObject objData = obj.getJSONObject("data");
        JSONArray msgArray = objData.getJSONArray("message");
        for (int i = 0; i < msgArray.length(); i++) {
            Message msg = new Message();
            JSONObject firstMsg = msgArray.getJSONObject(i);
            msg.number = firstMsg.getString("to");
            if (!firstMsg.getBoolean("accepted")) {
                try {
                    CheckForError(firstMsg);
                } catch (Exception e) {
                    msg.error = e.getMessage();
                }
            } else {
                msg.message_id = (String) firstMsg.get("apiMessageId");
            }
            messages.add(msg);
        }
        return messages.toArray(new Message[0]);
    }

    /**
     * This does a coverage call on the given number.
     *
     * @param number The number that you wish to do the lookup on.
     * @return -1 for error, Or the minimum cost the message could cost.
     * @throws Exception
     */
    public double getCoverage(String number) throws Exception {
        String response = this.execute("coverage/" + number, GET, null);
        JSONObject obj = new JSONObject(response);

        CheckForError(obj);
        JSONObject objData = obj.getJSONObject("data");

        if (!objData.getBoolean("routable")) {
            return -1;
        }
        return objData.getDouble("minimumCharge");
    }

    /**
     * This executes a POST query with the given parameters.
     *
     * @param targetURL The URL that should get hit.
     * @param method    The method that is to be used.
     * @param data      The data you want to send via the POST.
     * @return The content of the request.
     */
    private String execute(String targetURL, int method, String data) {
        HttpClient httpclient = new DefaultHttpClient();

        try {
            switch (method) {
                case POST:
                    HttpPost httppost = new HttpPost(CLICKATELL_REST_BASE_URL + targetURL);
                    httppost.addHeader("Content-Type", "application/json");
                    httppost.addHeader("Accept", "application/json");
                    httppost.addHeader("X-Version", "1");
                    httppost.addHeader("Authorization", "Bearer " + this.apiKey);
                    httppost.setEntity(new ByteArrayEntity(data.getBytes()));
                    HttpResponse responsePOST = httpclient.execute(httppost);
                    return inputStreamToString(responsePOST.getEntity().getContent());
                case DELETE:
                    HttpDelete httpdelete = new HttpDelete(CLICKATELL_REST_BASE_URL + targetURL);
                    httpdelete.addHeader("Content-Type", "application/json");
                    httpdelete.addHeader("Accept", "application/json");
                    httpdelete.addHeader("X-Version", "1");
                    httpdelete.addHeader("Authorization", "Bearer " + this.apiKey);
                    HttpResponse responseDELETE = httpclient.execute(httpdelete);
                    return inputStreamToString(responseDELETE.getEntity().getContent());
                case GET:
                    HttpGet httpget = new HttpGet(CLICKATELL_REST_BASE_URL + targetURL);
                    httpget.addHeader("Content-Type", "application/json");
                    httpget.addHeader("Accept", "application/json");
                    httpget.addHeader("X-Version", "1");
                    httpget.addHeader("Authorization", "Bearer " + this.apiKey);
                    HttpResponse responseGET = httpclient.execute(httpget);
                    return inputStreamToString(responseGET.getEntity().getContent());
                default:
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * This takes in the input stream and attempts to get the data into one stream and returns it.
     *
     * @param is The input stream.
     * @return The string of the entire input stream.
     * @throws IOException
     */
    private String inputStreamToString(InputStream is) throws IOException {
        String line = "";
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // Read response until the end
        while ((line = rd.readLine()) != null) {
            total.append(line);
            total.append("\n");
        }

        // Return full string
        System.out.println(total.toString().trim());
        return total.toString().trim();
    }

    /**
     * This is an internal function used to shorten other functions. Checks for
     * an error object, and throws it.
     *
     * @param obj
     *            The object that needs to be checked.
     * @throws Exception
     *             The exception that was found.
     */
    public void CheckForError(JSONObject obj) throws Exception {
        if (obj.has("error")) {
            JSONObject objError = obj.getJSONObject("error");
            if (objError != null) {
                throw new Exception(objError.getString("description"));
            }
        }
    }

    /**
     * This is the Message class that gets used as return values for some of the
     * functions.
     *
     * @author Dominic Schaff <dominic.schaff@gmail.com>
     *
     */
    public class Message {
        public String number = null, message_id = null, content = null,
                charge = null, status = null, error = null, statusString = null;

        public Message(String message_id) {
            this.message_id = message_id;
        }

        public Message() {
        }

        public String toString() {
            if (message_id != null) {
                return number + ": " + message_id;
            }
            return number + ": " + error;
        }
    }
}
