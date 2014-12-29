package com.schaff.clickatellsample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * This is example usage of using the ClickatellRest class file.
 * Notice that the auth details need to be changed before it can be used.
 */
public class RestActivity extends ActionBarActivity {

    /**
     * The object for the REST API.
     */
    ClickatellRest restApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);

        // Initialize the class:
        restApi = new ClickatellRest("YOUR_API_KEY");

        // Send a single message:
        findViewById(R.id.rest_send_single_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendSingleMessage();
            }
        });

        // Get the balance of the current account:
        findViewById(R.id.rest_balance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBalance();
            }
        });

        // Send a single message to multiple numbers:
        findViewById(R.id.rest_send_multiple_messages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMultipleMessages();
            }
        });

        // Get the status and charge of a message:
        findViewById(R.id.rest_get_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetMessageStatus();
            }
        });

        // Attempt to stop a message from being sent:
        findViewById(R.id.rest_stop_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StopMessage();
            }
        });

        // Check the coverage of a number:
        findViewById(R.id.rest_get_coverage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCoverage();
            }
        });
    }

    /**
     * Show the balance of the current account. The balance will be shown as a Toast.
     */
    private void getBalance() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final double balance = restApi.getBalance();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Balance is : " + balance, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    ShowException(e);
                }
            }
        }).start();
    }

    /**
     * Display the alert to send a message.
     */
    private void SendSingleMessage() {
        try {
            new MaterialDialog.Builder(this)
                    .title("Send Single Message")
                    .customView(R.layout.alert_send_single_message)
                    .positiveText("Send")
                    .negativeText("Cancel")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            EditText numberET = (EditText) dialog.findViewById(R.id.send_single_message_number);
                            String number = numberET.getText().toString();
                            EditText contentET = (EditText) dialog.findViewById(R.id.send_single_message_content);
                            String content = contentET.getText().toString();
                            SendSingleMessage(number, content);
                        }
                    })
                    .build()
                    .show();
        } catch (Exception e) {
        }
    }

    /**
     * Sends the given message to the given number.
     *
     * @param number  The number to send to. It should be in international format.
     * @param content The message to send.
     */
    private void SendSingleMessage(final String number, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ClickatellRest.Message result = restApi.sendMessage(number, content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText et = (EditText) findViewById(R.id.rest_send_single_message_reply);
                            et.setText(result.toString());
                            et.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    ShowException(e);
                }
            }
        }).start();
    }

    /**
     * This shows the dialog to send a single message to multiple numbers.
     */
    private void SendMultipleMessages() {
        try {
            new MaterialDialog.Builder(this)
                    .title("Send Multiple Messages, (Use a space to separate numbers)")
                    .customView(R.layout.alert_send_single_message)
                    .positiveText("Send")
                    .negativeText("Cancel")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            EditText numberET = (EditText) dialog.findViewById(R.id.send_single_message_number);
                            String numbers = numberET.getText().toString();
                            EditText contentET = (EditText) dialog.findViewById(R.id.send_single_message_content);
                            String content = contentET.getText().toString();
                            SendMultipleMessages(numbers, content);
                        }
                    })
                    .build()
                    .show();
        } catch (Exception e) {
        }
    }

    /**
     * This sends to given message to the given numbers.
     *
     * @param numbers The numbers that should get the message, Should be space separated.
     * @param content The message to send.
     */
    private void SendMultipleMessages(final String numbers, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ClickatellRest.Message[] result = restApi.sendMessage(numbers.split(" "), content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String data = result[0].toString();
                            for (int i = 1; i < result.length; i++) {
                                data += "\n" + result[i].toString();
                            }
                            EditText et = (EditText) findViewById(R.id.rest_send_multiple_messages_reply);
                            et.setText(data);
                            et.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (Exception e) {
                    ShowException(e);
                }
            }
        }).start();
    }

    /**
     * This displays the alert to get a message ID.
     */
    public void GetMessageStatus() {
        try {
            new MaterialDialog.Builder(this)
                    .title("Get Message Status for given Message Id")
                    .customView(R.layout.alert_get_message_id)
                    .positiveText("Send")
                    .negativeText("Cancel")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            EditText messageIdET = (EditText) dialog.findViewById(R.id.get_message_id_message_id);
                            String messageId = messageIdET.getText().toString();
                            GetMessageStatus(messageId);
                        }
                    })
                    .build()
                    .show();
        } catch (Exception e) {
        }
    }

    /**
     * This gets the status and charge of the given message ID. The data is shown as a toast and in the card.
     *
     * @param messageId The message ID to do the lookup on.
     */
    private void GetMessageStatus(final String messageId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ClickatellRest.Message result = restApi.getMessageStatus(messageId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText et = (EditText) findViewById(R.id.rest_get_message_reply);
                            et.setText(String.format("%s\nCharge: %s, Status: %s Status Description: %s", result.toString(), result.charge, result.status, result.statusString));
                            et.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "" + result, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    ShowException(e);
                }
            }
        }).start();
    }

    /**
     * This displays the alert to get the message ID of the message ID that is to be stopped.
     */
    public void StopMessage() {
        try {
            new MaterialDialog.Builder(this)
                    .title("Stop Message Sending for given Message Id")
                    .customView(R.layout.alert_get_message_id)
                    .positiveText("Send")
                    .negativeText("Cancel")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            EditText messageIdET = (EditText) dialog.findViewById(R.id.get_message_id_message_id);
                            String messageId = messageIdET.getText().toString();
                            StopMessage(messageId);
                        }
                    })
                    .build()
                    .show();
        } catch (Exception e) {
        }
    }

    /**
     * This attempts to stop the message of the given message ID. Displays the result in a Toast and on the card.
     *
     * @param messageId The message ID to try to stop..
     */
    private void StopMessage(final String messageId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ClickatellRest.Message result = restApi.stopMessage(messageId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText et = (EditText) findViewById(R.id.rest_stop_message_reply);
                            et.setText(String.format("%s\nStatus: %s Status Description: %s", result.toString(), result.status, result.statusString));
                            et.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "" + result, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    ShowException(e);
                }
            }
        }).start();
    }

    /**
     * This displays an alert to get the number that should be checked.
     */
    private void GetCoverage() {
        try {
            new MaterialDialog.Builder(this)
                    .title("Check if we have coverage for a number")
                    .customView(R.layout.alert_get_number)
                    .positiveText("Send")
                    .negativeText("Cancel")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            EditText numberET = (EditText) dialog.findViewById(R.id.get_number_number);
                            String number = numberET.getText().toString();
                            GetCoverage(number);
                        }
                    })
                    .build()
                    .show();
        } catch (Exception e) {
        }
    }

    /**
     * This does a coverage lookup on the given number. Show the results in a Toast and on the card.
     *
     * @param number The number to do a lookup on.
     */
    private void GetCoverage(final String number) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final double result = restApi.getCoverage(number);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText et = (EditText) findViewById(R.id.rest_get_coverage_reply);
                            if (result < 0) {
                                et.setText("Message Cannot be Routed");
                                Toast.makeText(getApplicationContext(), "Message Cannot be Routed", Toast.LENGTH_LONG).show();
                            } else {
                                et.setText("Message can be routed, and it could cost as little as: " + result + " credits");
                                Toast.makeText(getApplicationContext(), "Message can be routed, and it could cost as little as: " + result + " credits", Toast.LENGTH_LONG).show();
                            }
                            et.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (Exception e) {
                    ShowException(e);
                }
            }
        }).start();
    }

    /**
     * This shows the given exception's message as a toast.
     *
     * @param exception The exception to show.
     */
    private void ShowException(final Exception exception) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}