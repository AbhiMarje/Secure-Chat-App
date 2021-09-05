package com.example.securechatapp.utilities;

import java.util.HashMap;

public class Global {

    public static String MSG_DATA = "data";
    public static String FCM_TOKEN;
    public static String USER_NAME;
    public static String MSG_REGISTRATION_IDS = "registration_ids";
    public static HashMap<String, String> remoteMsgHeaders = null;
    public static HashMap<String, String> getRemoteMsgHeaders() {
        if (remoteMsgHeaders == null) {
            remoteMsgHeaders = new HashMap<>();
            remoteMsgHeaders.put(
                    "Authorization",
                    "key=AAAAyQAVIpQ:APA91bHW9929FZQXfuhZ1j66NgFIUSTGvzUHH_CE7KOegyNrrrOVPZ2R-Zwg75JLZT0zENPW-jz6ypIwJOs5UhgB3UiiivkPgFBVbJD7XFtAtwJTnVw3Np1Yfg4kJ_c8c94GmkTbotpc"
            );
            remoteMsgHeaders.put(
                    "Content-Type",
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }

}
