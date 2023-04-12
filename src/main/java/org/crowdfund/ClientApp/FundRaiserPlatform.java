package org.crowdfund.ClientApp;

import org.crowdfund.services.SendRequest;
import org.crowdfund.utils.Constants;
import org.crowdfund.utils.Validator;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

public class FundRaiserPlatform
{

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 1111;
    private static final BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

    private static boolean mainMenu()
    {

        boolean flag = true;

        try
        {

            System.out.println("1. Create New Round");

            System.out.println("2. Invest in Round");

            System.out.println("3. Check Statistics of Round(only owner can view the stats)");

            System.out.println("4. Exit");

            System.out.print("Enter choice: ");

            int option = Integer.parseInt(stdIn.readLine());

            JSONObject response,request;

            String clientId;

            switch (option)
            {

                case 1:

                    String productName, productInfo, password;

                    Double askAmount;

                    System.out.print("Enter your id(10 digit id): ");
                    clientId = stdIn.readLine();


                    System.out.println(Constants.NEWLINE + ".......................!!!INSTRUCTIONS!!!......................" + Constants.NEWLINE +
                            "ProductName must have 3-20 characters" + Constants.NEWLINE +
                            "...............................................................");
                    System.out.print("Enter ProductName: ");
                    productName = stdIn.readLine();

                    System.out.print("Description of Product Idea: ");
                    productInfo = stdIn.readLine();

                    System.out.print("Enter the ask amount(must be between 10 thousand and 1 crore): ");
                    askAmount = Double.parseDouble(stdIn.readLine());

                    System.out.println(Constants.NEWLINE + ".......................!!!INSTRUCTIONS!!!......................" + Constants.NEWLINE +
                            "Password will be useful when logging in as owner of funds from main menu" +
                            "Password must be least 8 characters long" + Constants.NEWLINE +
                            "Must have least one uppercase letter" + Constants.NEWLINE +
                            "Must have least one lowercase letter" + Constants.NEWLINE +
                            "Must have least one digit" + Constants.NEWLINE +
                            "Must have least one special character from among the given: @#.^$*" + Constants.NEWLINE +
                            "...............................................................");
                    System.out.print("Enter a password: ");
                    password = stdIn.readLine();

                    if(!Validator.checkName(productName) || !Validator.checkPasswordStrength(password))
                    {

                        System.out.println(Constants.NAME_PASSWORD_STRENGTH);

                        System.out.println("YOUR FUND CREATION FAILED!!!!");

                    }

                    else if(!Validator.checkUserId(clientId))
                    {
                        System.out.println("INVALID ID");

                        System.out.println("YOUR FUND CREATION FAILED!!!!");
                    }

                    else if(Double.compare(askAmount,10000.00) < 0 || Double.compare(askAmount, 10000000.00) > 0)
                    {

                        System.out.println(Constants.INVALID_AMOUNT);

                        System.out.println("YOUR FUND CREATION FAILED!!!!");

                    }

                    else
                    {

                        request = new JSONObject();
                        request.put("operation", Constants.OPERATION_CREATE);
                        request.put("clientId",Long.parseLong(clientId));
                        request.put("productName", productName);
                        request.put("productInfo",productInfo);
                        request.put("askAmount", askAmount);
                        request.put("password", password);

                        response = SendRequest.fetchResult(SERVER_IP, SERVER_PORT,request);

                        if(response.getInt("status") == 500)
                        {
                            System.out.println(Constants.SERVER_ERROR);
                        }
                        else if(response.getString("message").equals(Constants.INVALID_INPUT) || response.getString("message").equals(Constants.MISSING_FIELDS))
                        {
                            System.out.println("ROUND CANNOT BE CREATED DUE TO " + response.getString("message"));
                        }
                        else
                        {
                            System.out.println("*****TAKE NOTE OF THE BELOW ID WHICH CAN BE SHARED WITH INTERESTED BUYERS*****");
                            System.out.println("--------> " + response.getLong("id") + " <--------");
                        }

                    }

                    break;

                case 2:

                    long id;

                    System.out.print("Enter RoundId: ");

                    id = Long.parseLong(stdIn.readLine());

                    request = new JSONObject();
                    request.put("operation", Constants.OPERATION_EXIST);
                    request.put("id",id);

                    response = SendRequest.fetchResult(SERVER_IP, SERVER_PORT,request);

                    if(response.getInt("status") == 500)
                    {
                        System.out.println(Constants.SERVER_ERROR);
                    }
                    else if(response.getString("message").equals(Constants.INVALID_INPUT) || response.getString("message").equals(Constants.MISSING_FIELDS))
                    {
                        System.out.println("ROUND CANNOT BE FOUND DUE TO " + response.getString("message"));
                    }
                    else if(!response.getBoolean("present"))
                    {
                        System.out.println("ROUND DOESN'T EXIST. TRY CONTACTING THE OWNER! ");
                    }
                    else
                    {

                        //TODO WHILE LOOP OPTIONS LOGIC PATH FROM HERE
                        // 1) round details
                        // 2) buy shares
                        // 3) exit
                        boolean menu_flag = true;

                        double amount;

                        while (menu_flag)
                        {

                            System.out.println("1. Get Round Details");

                            System.out.println("2. Buy shares");

                            System.out.println("3. Exit");

                            System.out.print("Enter choice: ");

                            try
                            {

                                option = Integer.parseInt(stdIn.readLine());

                                switch (option)
                                {

                                    case 1:

                                        request = new JSONObject();
                                        request.put("operation", Constants.OPERATION_DETAILS);
                                        request.put("id",id);

                                        response = SendRequest.fetchResult(SERVER_IP, SERVER_PORT,request);

                                        if(response.getInt("status") == 500)
                                        {
                                            System.out.println(Constants.SERVER_ERROR);
                                        }
                                        else if(response.getString("message").equals(Constants.INVALID_INPUT) || response.getString("message").equals(Constants.MISSING_FIELDS))
                                        {
                                            System.out.println("ROUND CANNOT BE FOUND DUE TO " + response.getString("message"));
                                        }
                                        else if(!response.getBoolean("present"))
                                        {
                                            System.out.println("ROUND DOESN'T EXIST. TRY CONTACTING THE OWNER!");
                                        }
                                        else if(response.getString("message").equals(Constants.SUCCESS))
                                        {
                                            System.out.println("DETAILS ARE BELOW:" + Constants.NEWLINE + "----------xxxxxx---------");

                                            JSONObject details = response.getJSONObject("details");
                                            System.out.println("FUND ROUND ID: " + details.get("id"));
                                            System.out.println("OWNER ID: " + details.get("ownerId"));
                                            System.out.println("PRODUCT NAME: " + details.get("productName"));
                                            System.out.println("PRODUCT INFO: " + details.get("productInfo"));
                                            System.out.println("ASK AMOUNT: " + details.get("askAmount"));
                                            System.out.println("FUND RAISED TILL NOW: " + details.get("fundRaised"));
                                            System.out.println("SHARES LEFT FOR BUY(%): " + details.get("%sharesLeft"));

                                        }

                                        break;

                                    case 2:

                                        System.out.print("Enter your id(10 digit id): ");

                                        clientId = stdIn.readLine();

                                        if(!Validator.checkUserId(clientId))
                                        {

                                            throw new RuntimeException(Constants.INVALID_INPUT +" OPERATION FAILED!!!!");

                                        }

                                        System.out.print("Enter Amount: ");

                                        amount = Double.parseDouble(stdIn.readLine());

                                        if(Double.compare(amount,0.00) <= 0)
                                        {

                                            throw new RuntimeException(Constants.INVALID_AMOUNT);

                                        }

                                        request = new JSONObject();
                                        request.put("operation", Constants.OPERATION_INVEST);
                                        request.put("id",id);
                                        request.put("clientId",Long.parseLong(clientId));
                                        request.put("amount",amount);

                                        response = SendRequest.fetchResult(SERVER_IP, SERVER_PORT,request);

                                        if(response.getInt("status") == 500)
                                        {
                                            System.out.println(Constants.SERVER_ERROR);
                                        }
                                        else if(response.getString("message").equals(Constants.INVALID_INPUT) || response.getString("message").equals(Constants.MISSING_FIELDS))
                                        {
                                            System.out.println("ROUND CANNOT BE FOUND DUE TO " + response.getString("message"));
                                        }
                                        else if(!response.getBoolean("present"))
                                        {
                                            System.out.println("ROUND DOESN'T EXIST. TRY CONTACTING THE OWNER!");
                                        }
                                        else if(response.getString("message").equals(Constants.SUCCESS))
                                        {

                                            double ownership = response.getDouble("ownership");
                                            double amountLeft = response.getDouble("amountLeft");
                                            if(Double.compare(ownership,0.00) > 0)
                                            {
                                                if(Double.compare(amountLeft,0.00) > 0)
                                                {
                                                    System.out.println("AMOUNT EQUIVALENT SHARES COULD NOT BE BOUGHT DUE TO UNAVAILABILITY OF SHARES");
                                                    System.out.println("PARTIAL BOUGHT HOLDING(%): " + ownership);
                                                    System.out.println("REMAINING AMOUNT RS." + amountLeft + " WILL RETURN TO YOUR ACCOUNT SOON!!");
                                                    System.out.println("CONGRATS ON BUYING SHARE HOLDING(%): " + ownership);
                                                }
                                                else
                                                {
                                                    System.out.println("CONGRATS ON BUYING SHARE HOLDING(%): " + ownership);
                                                }
                                            }
                                            else
                                            {
                                                System.out.println("SORRY COULD NOT BUY SHARES DUE TO UNAVAILABILITY OF SHARES" + Constants.NEWLINE +
                                                        "AMOUNT RS. " + amountLeft + " WILL RETURN TO YOUR ACCOUNT SOON!!");
                                            }

                                        }

                                        break;

                                    case 3:

                                        menu_flag = false;

                                        break;

                                    default:

                                        System.out.println(Constants.INVALID_OPERATION);

                                }

                            }

                            catch (Exception exception)
                            {

                                System.out.println(Constants.INVALID_INPUT);

                            }

                            System.out.println("\n............................................\n");

                        }

                    }

                    break;

                case 3:

                    System.out.print("Enter RoundId: ");

                    id = Long.parseLong(stdIn.readLine());

                    System.out.print("Enter a password: ");

                    password = stdIn.readLine();

                    request = new JSONObject();
                    request.put("operation", Constants.OPERATION_DETAILS);
                    request.put("id",id);
                    request.put("password", password);

                    response = SendRequest.fetchResult(SERVER_IP, SERVER_PORT,request);

                    if(response.getInt("status") == 500)
                    {
                        System.out.println(Constants.SERVER_ERROR);
                    }
                    else if(response.getString("message").equals(Constants.INVALID_INPUT) || response.getString("message").equals(Constants.MISSING_FIELDS))
                    {
                        System.out.println("ROUND CANNOT BE FOUND DUE TO " + response.getString("message"));
                    }
                    else if(!response.getBoolean("present"))
                    {
                        System.out.println("ROUND DOESN'T EXIST. TRY CONTACTING THE OWNER!");
                    }
                    else if(response.getString("message").equals(Constants.AUTHENTICATION_FAILURE))
                    {
                        System.out.println(response.getString("message"));
                    }
                    else if(response.getString("message").equals(Constants.SUCCESS))
                    {

                        JSONObject details = response.getJSONObject("details");
                        System.out.println("ADMIN DETAILS ARE BELOW:" + Constants.NEWLINE + "----------xxxxxx---------");

                        System.out.println("FUND ROUND ID: " + details.get("id"));
                        System.out.println("OWNER ID: " + details.get("ownerId"));
                        System.out.println("PRODUCT NAME: " + details.get("productName"));
                        System.out.println("PRODUCT INFO: " + details.get("productInfo"));
                        System.out.println("ASK AMOUNT: " + details.get("askAmount"));
                        System.out.println("FUND RAISED TILL NOW: " + details.get("fundRaised"));
                        System.out.println("SHARES LEFT FOR BUY(%): " + details.get("%sharesLeft"));
                        System.out.println(Constants.NEWLINE);
                        System.out.println("ID OF SHAREHOLDERS | SHARE HOLDING(%)");
                        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" + Constants.NEWLINE);
                        Map<String, Object> shareHolders = details.getJSONObject("shareHolders").toMap();
                        shareHolders.forEach((ownerId, ownership)-> System.out.println(ownerId + " | " + ownership));
                        System.out.println(Constants.NEWLINE + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" + Constants.NEWLINE);

                    }

                    break;

                case 4:

                    flag = false;

                    break;

                default:

                    System.out.println("Invalid Operation!!!");

            }

        }

        catch (Exception exception)
        {

            System.out.println(Constants.INVALID_INPUT);

        }

        return flag;

    }

    public static void main(String[] args) {

        try
        {
            
            boolean flag = true;

            while(flag)
            {

                flag = mainMenu();

                System.out.println("\n............................................\n");

            }

        }

        catch (Exception exception)
        {

            System.out.println(exception.getMessage());

        }

        finally
        {

            try
            {

                stdIn.close();

            }

            catch (Exception exception)
            {

                System.out.println(exception.getMessage());

            }

        }

    }

}


