package com.cg.api;


import com.cg.exception.DataInputException;
import com.cg.exception.UnauthorizedException;
import com.cg.model.Product;
import com.cg.model.User;
import com.cg.service.cart.ICartService;
import com.cg.service.product.IProductService;
import com.cg.service.user.IUserService;
import com.cg.utils.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/carts")
public class CartAPI {

    @Autowired
    private IProductService productService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICartService cartService;

    @Autowired
    private AppUtils appUtils;

    @PostMapping("/add-cart/{productId}")
    public ResponseEntity<?> addCart(@PathVariable Long productId) {

        Optional<Product> productOptional = productService.findById(productId);

        if (!productOptional.isPresent()) {
            throw new DataInputException("Product invalid");
        }

        String username = appUtils.getPrincipalUsername();

        Optional<User> userOptional = userService.findByUsername(username);

        if (!userOptional.isPresent()) {
            throw new UnauthorizedException("User invalid");
        }

        cartService.addCart(productId, userOptional.get());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
