package org.crowdfund.db.dao;

import org.crowdfund.models.FundRound;
import org.crowdfund.models.Transaction;
import org.crowdfund.utils.Constants;
import org.crowdfund.utils.DefaultJsonResponse;
import org.crowdfund.utils.Pair;
import org.crowdfund.utils.Validator;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryDB implements DB{
    private InMemoryDB(){}
    private final ConcurrentMap<Long, FundRound> fundRounds = new ConcurrentHashMap<>();

    //stores List of transactions of users mapped to userId
    //concurrent because if same client buys and hence
    //tries to create new transaction then it may occur
    //both make new ArrayList for same client
    //so one of the update will be lost
    private final ConcurrentMap<Long, List<Transaction>> transactions = new ConcurrentHashMap<>();

    private static volatile InMemoryDB dbInstance;
    public static InMemoryDB getInstance() {

        if(dbInstance == null)
        {
            synchronized (InMemoryDB.class)
            {
                if(dbInstance == null)
                    dbInstance = new InMemoryDB();
            }

        }
        return dbInstance;

    }


    @Override
    public JSONObject write(Long ownerId, String productName, String productInfo, Double askAmount, String password)
    {

        try
        {

            if( Validator.nullCheck(ownerId) ||
                Validator.nullCheck(productName) ||
                Validator.nullCheck(productInfo) ||
                Validator.nullCheck(askAmount) ||
                Validator.nullCheck(password))
                    return DefaultJsonResponse.getDefaultResponse(200, Constants.MISSING_FIELDS);

            else if(Double.compare(askAmount,10000) < 0 || Double.compare(askAmount, 10000000) > 0)
                return DefaultJsonResponse.getDefaultResponse(200, Constants.INVALID_INPUT);

            //storing in db
            FundRound round = new FundRound(ownerId, productName, productInfo, askAmount, password);
            fundRounds.put(round.getId(), round);

            //TODO remove log line
            System.out.println(round.getDetails());

            JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("message", Constants.SUCCESS);
            response.put("id", round.getId());

            return response;

        }
        catch(Exception exception)
        {
            System.out.println("Error occurred while writing: " + ownerId + ", " + productName + ", " + productInfo + ", " + askAmount);
        }

        return DefaultJsonResponse.getDefaultResponse(500, Constants.SERVER_ERROR);

    }

    @Override
    public JSONObject checkIfPresent(Long id)
    {

        try
        {

            if(Validator.nullCheck(id))
                return DefaultJsonResponse.getDefaultResponse(200, Constants.MISSING_FIELDS);

            //fetching details from db
            FundRound round = fundRounds.get(id);

            JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("message", Constants.SUCCESS);
            response.put("present", !Validator.nullCheck(round));

            return response;

        }
        catch(Exception exception)
        {

            System.out.println("Error Occurred for ID: " + id);

        }

        return DefaultJsonResponse.getDefaultResponse(500, Constants.SERVER_ERROR);

    }

    @Override
    public JSONObject getById(Long id, String password)
    {

        try
        {

            if(Validator.nullCheck(id))
                return DefaultJsonResponse.getDefaultResponse(200, Constants.MISSING_FIELDS);

            //fetching details from db
            FundRound round = fundRounds.get(id);

            JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("message", Constants.SUCCESS);
            response.put("present", !Validator.nullCheck(round));

            if(!Validator.nullCheck(round))
            {
                if(!Validator.nullCheck(password))
                {
                    if(round.authenticate(password))
                        response.put("details", round.getAdminDetails());
                    else
                        response.put("message",Constants.AUTHENTICATION_FAILURE);
                }
                else
                {
                    response.put("details", round.getDetails());
                }
            }

            return response;

        }
        catch(Exception exception)
        {

            System.out.println("Error Occurred for ID: " + id);

        }

        return DefaultJsonResponse.getDefaultResponse(500, Constants.SERVER_ERROR);

    }

    @Override
    public JSONObject updateFundRaised(Long id, Long clientId, Double amount)
    {

        try
        {

            if(Validator.nullCheck(id) || Validator.nullCheck(clientId) || Validator.nullCheck(amount))
                return DefaultJsonResponse.getDefaultResponse(200, Constants.MISSING_FIELDS);


            else if(Double.compare(amount,0.00) <= 0)
                return DefaultJsonResponse.getDefaultResponse(200, Constants.INVALID_INPUT);

            //fetching details from db
            FundRound round = fundRounds.get(id);

            JSONObject response = new JSONObject();
            response.put("status", 200);
            response.put("message", Constants.SUCCESS);
            response.put("present", !Validator.nullCheck(round));

            if(!Validator.nullCheck(round))
            {
                Pair<Double,Double> result = round.invest(amount, clientId);

                //store the non-zero buying transaction details in Transactions DB also
                if(Double.compare(result.getFirst(),0.00) > 0)
                {

                    if(!transactions.containsKey(clientId))
                        transactions.put(clientId, new ArrayList<>());

                    transactions.get(clientId).add(new Transaction(round.getProductName(), amount-result.getSecond(), result.getFirst()));

                    //TODO: remove below log line
                    System.out.println(clientId + " bought " + result.getFirst() + "% in product(id) " + id + " for Rs. " + (amount-result.getSecond()));

                }

                response.put("ownership", result.getFirst());
                response.put("amountLeft", result.getSecond());

            }

            return response;

        }
        catch(Exception exception)
        {

            System.out.println("Error Occurred for ID: " + id);

        }

        return DefaultJsonResponse.getDefaultResponse(500, Constants.SERVER_ERROR);

    }

}
