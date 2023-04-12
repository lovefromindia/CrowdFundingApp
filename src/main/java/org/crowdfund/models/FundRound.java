package org.crowdfund.models;

import org.crowdfund.utils.Pair;
import org.crowdfund.utils.UUIDGenerator;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FundRound
{
    long id;
    String productName;
    long ownerId;
    String productInfo;
    volatile double askAmount;
    double fundRaised;
    private final String password;

    //stores the clientIds with their respective shareholding
    Map<Long, Double> shareHolders;

    public FundRound(long ownerId, String productName, String productInfo, double askAmount, String password)
    {
        this.id = UUIDGenerator.getUUID();
        this.productName = productName;
        this.ownerId = ownerId;
        this.productInfo = productInfo;
        this.askAmount = askAmount;
        this.fundRaised = 0;
        this.password = password;
        this.shareHolders = new HashMap<>();
    }

    public long getId()
    {
        return id;
    }

    public double getAskAmount()
    {
        return askAmount;
    }

    //returns <ownership, amountLeft(in case shares value < amount)>
    public synchronized Pair<Double, Double> invest(double amount, long clientId)
    {

        //if some shares are left
        if(Double.compare(askAmount-fundRaised,0) >= 0)
        {
            double shareValue = Math.min(askAmount - fundRaised, amount);

            fundRaised += shareValue;

            if(shareHolders.containsKey(clientId))
                shareHolders.put(clientId,shareHolders.get(clientId)+(shareValue/askAmount)*100);
            else
                shareHolders.put(clientId,(shareValue/askAmount)*100);

            return new Pair<>((shareValue/askAmount)*100,amount-shareValue);

        }

        return new Pair<>(0.00, amount);
    }

    public String getProductName()
    {
        return productName;
    }

    public JSONObject getAdminDetails()
    {

        JSONObject result = new JSONObject();
        result.put("id",id);
        result.put("productName",productName);
        result.put("ownerId",ownerId);
        result.put("productInfo",productInfo);
        result.put("askAmount",askAmount);
        result.put("fundRaised",fundRaised);
        result.put("%sharesLeft",100.00-((fundRaised/askAmount)*100));
        result.put("shareHolders", shareHolders);
        return result;

    }

    public JSONObject getDetails()
    {

        JSONObject result = new JSONObject();
        result.put("id",id);
        result.put("productName",productName);
        result.put("ownerId",ownerId);
        result.put("productInfo",productInfo);
        result.put("askAmount",askAmount);
        result.put("fundRaised",fundRaised);
        result.put("%sharesLeft",100.00-((fundRaised/askAmount)*100));
        return result;

    }

    public boolean authenticate(String password)
    {
        return password.equals(this.password);
    }
}
