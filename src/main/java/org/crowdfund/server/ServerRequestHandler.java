package org.crowdfund.server;

import org.crowdfund.services.SendRequest;
import org.crowdfund.utils.Constants;
import org.crowdfund.utils.DefaultJsonResponse;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ServerRequestHandler implements Runnable {

    Socket clientConnection;
    private static final String DB_IP = "localhost";
    private static final int DB_PORT = 2222;

    public ServerRequestHandler(Socket clientConnection)
    {
        this.clientConnection = clientConnection;
    }

    @Override
    public void run()
    {

        try(DataOutputStream clientWriter = new DataOutputStream(clientConnection.getOutputStream());
            DataInputStream clientReader = new DataInputStream(clientConnection.getInputStream()))
        {


            JSONObject request = new JSONObject(clientReader.readUTF());

            System.out.println("Client: " + clientConnection.getInetAddress() + " REQUEST" + request);

            String operation = request.getString("operation");

            if(operation.equals(Constants.OPERATION_CREATE))
            {
                if( !request.has("clientId") ||
                    !request.has("productName")||
                    !request.has("productInfo") ||
                    !request.has("askAmount") ||
                    !request.has("password"))
                {
                    clientWriter.writeUTF(DefaultJsonResponse.getDefaultResponse(200, Constants.MISSING_FIELDS).toString());
                }

                else
                    clientWriter.writeUTF(SendRequest.fetchResult(DB_IP,DB_PORT,request).toString());
            }

            else if(operation.equals(Constants.OPERATION_EXIST))
            {

                if(!request.has("id"))
                    clientWriter.writeUTF(DefaultJsonResponse.getDefaultResponse(200, Constants.MISSING_FIELDS).toString());

                else
                    clientWriter.writeUTF(SendRequest.fetchResult(DB_IP,DB_PORT,request).toString());
            }

            else if(operation.equals(Constants.OPERATION_DETAILS))
            {
                if(!request.has("id"))
                    clientWriter.writeUTF(DefaultJsonResponse.getDefaultResponse(200, Constants.MISSING_FIELDS).toString());

                else
                    clientWriter.writeUTF(SendRequest.fetchResult(DB_IP,DB_PORT,request).toString());
            }

            else if(operation.equals(Constants.OPERATION_INVEST))
            {
                if(!request.has("id") || !request.has("clientId") || !request.has("amount"))
                    clientWriter.writeUTF(DefaultJsonResponse.getDefaultResponse(200, Constants.MISSING_FIELDS).toString());

                else
                    clientWriter.writeUTF(SendRequest.fetchResult(DB_IP,DB_PORT,request).toString());
            }

            else
            {
                clientWriter.writeUTF(DefaultJsonResponse.getDefaultResponse(404, "NOT FOUND").toString());
                System.out.println(clientConnection.getInetAddress() + " SEND MALICIOUS OPERATION");
            }

        }
        catch (Exception exception)
        {

            System.out.println(exception.getMessage());

        }
        finally
        {
            System.out.println("Client: " + clientConnection.getInetAddress() + " EXITED!");
        }
    }

}
