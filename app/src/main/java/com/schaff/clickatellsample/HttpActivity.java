package com.schaff.clickatellsample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * This is example usage of using the ClickatellHttp class file.
 * Notice that the auth details need to be changed before it can be used.
 */
public class HttpActivity extends ActionBarActivity {

    /**
     * The object for the ClickatellHttp class.
     */
    ClickatellHttp httpApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http);

        // Initialize the object:
        httpApi = new ClickatellHttp("YOUR_USERNAME", "YOUR_API_ID", "YOUR_PASSWORD");

        // Authentication Card:
        findViewById(R.id.http_authentication).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAuth();
            }
        });

        // Send a single message card:
        findViewById(R.id.http_send_single_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendSingleMessage();
            }
        });

        // Check your balance card:
        findViewById(R.id.http_balance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBalance();
            }
        });

        // Send multiple messages card:
        findViewById(R.id.http_send_multiple_messages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMultipleMessages();
            }
        });

        // Get the status of a message card:
        findViewById(R.id.http_get_message_status).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetMessageStatus();
            }
        });

        // Get the charge and status of a message card:
        findViewById(R.id.http_get_message_charge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetMessageCharge();
            }
        });

        // Stop a message from being sent, if it has not been sent yet, card:
        findViewById(R.id.http_stop_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StopMessage();
            }
        });

        // Check for coverage card:
        findViewById(R.id.http_get_coverage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCoverage();
            }
        });
    }

    /**
     * This tests for authentication details, to make sure they are correct.
     * Created a toast message on success, or error.
     */
    private void getAuth() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (httpApi.testAuth()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Authentication Succeeded", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    ShowException(e);
                }
            }
        }).start();
    }

    /**
     * This gets the balance of the current account.
     * Shows the balance as a toast.
     */
    private void getBalance() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final double balance = httpApi.getBalance();
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
     * This creates the alert to show a alert dialog for the input of a message.
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
     * This sends the given message and displays the output. The output is also shown via a toast.
     *
     * @param number  The number to send to. Should be in international format.
     * @param content The message the will be sent.
     */
    private void SendSingleMessage(final String number, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ClickatellHttp.Message result = httpApi.sendMessage(number, content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText et = (EditText) findViewById(R.id.http_send_single_message_reply);
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
     * This shows the alert to send one message to multiple numbers.
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
     * This sends the given messages to the given numbers. The numbers should be space separated.
     *
     * @param numbers The numbers to send the messages to. The numbers should be space separated.
     * @param content The message the will be sent.
     */
    private void SendMultipleMessages(final String numbers, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ClickatellHttp.Message[] result = httpApi.sendMessage(numbers.split(" "), content);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String data = result[0].toString();
                            for (int i = 1; i < result.length; i++) {
                                data += "\n" + result[i].toString();
                            }
                            EditText et = (EditText) findViewById(R.id.http_send_multiple_messages_reply);
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
     * This shows the alert to get the message ID, so that a status lookup can be done.
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
     * This get the status of the given message id. The status will get shown as a toast and in the card.
     *
     * @param messageId The message ID to do the lookup on.
     */
    private void GetMessageStatus(final String messageId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final int result = httpApi.getMessageStatus(messageId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText et = (EditText) findViewById(R.id.http_get_message_status_reply);
                            et.setText("" + result);
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
     * This displays an alert to get a message ID to do a lookup on.
     */
    private void GetMessageCharge() {
        try {
            new MaterialDialog.Builder(this)
                    .title("Get Message Charge and Status for given Message Id")
                    .customView(R.layout.alert_get_message_id)
                    .positiveText("Send")
                    .negativeText("Cancel")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            EditText messageIdET = (EditText) dialog.findViewById(R.id.get_message_id_message_id);
                            String messageId = messageIdET.getText().toString();
                            GetMessageCharge(messageId);
                        }
                    })
                    .build()
                    .show();
        } catch (Exception e) {
        }
    }

    /**
     * This does a charge and status lookup on the given message ID.
     *
     * @param messageId The message ID to do the lookup on.
     */
    private void GetMessageCharge(final String messageId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ClickatellHttp.Message result = httpApi.getMessageCharge(messageId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText et = (EditText) findViewById(R.id.http_get_message_charge_reply);
                            et.setText(String.format("%s\nCharge: %s, Status: %s", result.toString(), result.charge, result.status));
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
     * This displays an alert to get the message ID of the message that needs to get stopped.
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
     * This attempts to stop the message of the message ID. It then displays the status of the message in a toast, and on the UI,
     *
     * @param messageId The message ID to try to stop.
     */
    private void StopMessage(final String messageId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final int result = httpApi.stopMessage(messageId);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText et = (EditText) findViewById(R.id.http_stop_message_reply);
                            et.setText("Status: " + result);
                            et.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Status: " + result, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    ShowException(e);
                }
            }
        }).start();
    }

    /**
     * Display an alert to get the number that should be checked.
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
     * This does a lookup on the number specified, the results are shown in a toast and in the card.
     *
     * @param number The number to do the lookup on.
     */
    private void GetCoverage(final String number) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final double result = httpApi.getCoverage(number);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText et = (EditText) findViewById(R.id.http_get_coverage_reply);
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
