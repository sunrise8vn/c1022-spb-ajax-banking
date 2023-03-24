package com.cg.api;

import com.cg.exception.UnauthorizedException;
import com.cg.model.User;
import com.cg.service.bill.IBillService;
import com.cg.service.user.IUserService;
import com.cg.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequestMapping("/api/bills")
public class BillAPI {

    @Autowired
    private IUserService userService;

    @Autowired
    private IBillService billService;

    @Autowired
    private AppUtils appUtils;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder() {
        String username = appUtils.getPrincipalUsername();

        Optional<User> userOptional = userService.findByUsername(username);

        if (!userOptional.isPresent()) {
            throw new UnauthorizedException("User invalid");
        }

        billService.createOrder(userOptional.get());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
