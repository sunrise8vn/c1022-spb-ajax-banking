package com.cg.service.bill;

import com.cg.model.Bill;
import com.cg.model.User;
import com.cg.service.IGeneralService;

public interface IBillService extends IGeneralService<Bill> {

    void createOrder(User user);
}
