package org.crowdfund.utils;

import org.json.JSONObject;

public class DefaultJsonResponse {

    private DefaultJsonResponse(){};

    public static JSONObject getDefaultResponse(int status, String message)
    {

        JSONObject res = new JSONObject();

        res.put("status", status);

        res.put("message", message);
        
        return res;
    }

}
