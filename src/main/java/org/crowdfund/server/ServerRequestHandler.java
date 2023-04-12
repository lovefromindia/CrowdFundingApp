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

            //TODO Server side validations to protect DBDriver
            if(operation.equals(Constants.OPERATION_CREATE))
            {
                clientWriter.writeUTF(SendRequest.fetchResult(DB_IP,DB_PORT,request).toString());
            }

            else if(operation.equals(Constants.OPERATION_EXIST))
            {
                clientWriter.writeUTF(SendRequest.fetchResult(DB_IP,DB_PORT,request).toString());
            }

            else if(operation.equals(Constants.OPERATION_DETAILS))
            {
                clientWriter.writeUTF(SendRequest.fetchResult(DB_IP,DB_PORT,request).toString());
            }

            else if(operation.equals(Constants.OPERATION_INVEST))
            {
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
