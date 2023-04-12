package org.crowdfund.db;

import org.crowdfund.db.dao.DB;
import org.crowdfund.db.dao.InMemoryDB;
import org.crowdfund.utils.Constants;
import org.crowdfund.utils.DefaultJsonResponse;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class DBRequestHandler implements Runnable {

    Socket serverConnection;
    DB DBObject;
    public DBRequestHandler(Socket serverConnection)
    {
        this.serverConnection = serverConnection;
        this.DBObject = InMemoryDB.getInstance();
    }

    @Override
    public void run()
    {

        try(DataOutputStream serverWriter = new DataOutputStream(serverConnection.getOutputStream());
            DataInputStream serverReader = new DataInputStream(serverConnection.getInputStream()))
        {

            JSONObject request = new JSONObject(serverReader.readUTF());

            String operation = request.has("operation")?request.getString("operation"):"null";

            //TODO Server side validations to protect DBDriver
            switch (operation)
            {

                case Constants.OPERATION_CREATE -> {

                    serverWriter.writeUTF(
                            DBObject.write(
                                    request.has("clientId")?request.getLong("clientId"):null,
                                    request.has("productName")?request.getString("productName"):null,
                                    request.has("productInfo")?request.getString("productInfo"):null,
                                    request.has("askAmount")?request.getDouble("askAmount"):null,
                                    request.has("password")?request.getString("password"):null
                            ).toString());
                }


                case Constants.OPERATION_EXIST ->
                        serverWriter.writeUTF(DBObject.checkIfPresent(request.has("id")?request.getLong("id"):null).toString());

                case Constants.OPERATION_DETAILS ->
                    //here get("password") will result into null object so normal details will be shown
                        serverWriter.writeUTF(DBObject.getById(
                                request.has("id")?request.getLong("id"):null,
                                request.has("password")?request.getString("password"):null
                        ).toString());

                case Constants.OPERATION_INVEST -> serverWriter.writeUTF(DBObject.updateFundRaised(
                                request.has("id")?request.getLong("id"):null,
                                request.has("clientId")?request.getLong("clientId"):null,
                                request.has("amount")?request.getDouble("amount"):null
                                )
                        .toString());

                default -> {
                    serverWriter.writeUTF(DefaultJsonResponse.getDefaultResponse(404, "OPERATION NOT FOUND").toString());
                    System.out.println(serverConnection.getInetAddress() + " SEND MALICIOUS OPERATION");
                }

            }

        }
        catch (Exception exception)
        {

            System.out.println(exception.getMessage());

        }

    }

}
