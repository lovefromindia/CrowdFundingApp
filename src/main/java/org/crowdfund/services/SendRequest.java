package org.crowdfund.services;

import org.crowdfund.utils.Constants;
import org.crowdfund.utils.DefaultJsonResponse;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

//SendRequest is decoupled from FundRaiserPlatform so that in future if the server is moved
//then for redirecting request to new server only this file has to be changed
public class SendRequest
{

    private SendRequest(){}

    public static JSONObject fetchResult(String ip, int port, JSONObject request)
    {


        JSONObject response;

        try(Socket serverConnection = new Socket(ip, port);
            DataInputStream serverReader = new DataInputStream(serverConnection.getInputStream());
            DataOutputStream serverWriter = new DataOutputStream(serverConnection.getOutputStream()))
        {

            serverWriter.writeUTF(request.toString());

            response = new JSONObject(serverReader.readUTF());

        }
        catch (Exception exception)
        {

//            System.out.println(exception.getMessage());
            response = DefaultJsonResponse.getDefaultResponse(500, Constants.SERVER_ERROR);

        }

        return response;

    }
}
