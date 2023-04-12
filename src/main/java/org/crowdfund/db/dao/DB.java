package org.crowdfund.db.dao;

import org.json.JSONObject;

public interface DB {

    JSONObject write(Long ownerId, String productName, String productInfo, Double askAmount, String password);

    JSONObject checkIfPresent(Long id);

    JSONObject getById(Long id, String password);

    JSONObject updateFundRaised(Long id, Long clientId, Double amount);

}
