package com.cg.service.cart;


import com.cg.model.Cart;
import com.cg.model.CartDetail;
import com.cg.model.Product;
import com.cg.model.User;
import com.cg.repository.CartDetailRepository;
import com.cg.repository.CartRepository;
import com.cg.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Cart> findAll() {
        return null;
    }

    @Override
    public Optional<Cart> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Boolean existById(Long id) {
        return null;
    }

    @Override
    public void addCart(Long productId, User user) {
        Optional<Cart> cartOptional = cartRepository.findByUser(user);

        Optional<Product> productOptional = productRepository.findById(productId);
        Product product = productOptional.get();

        if (!cartOptional.isPresent()) {
            Cart cart = new Cart();
            cart.setTotalAmount(BigDecimal.ZERO);
            cart.setUser(user);
            cartRepository.save(cart);

            Long productQuantity = 1L;
            BigDecimal productAmount = product.getPrice().multiply(BigDecimal.valueOf(productQuantity));

            CartDetail cartDetail = new CartDetail();
            cartDetail.setCart(cart);
            cartDetail.setProduct(product);
            cartDetail.setProductTitle(product.getTitle());
            cartDetail.setProductPrice(product.getPrice());
            cartDetail.setProductQuantity(productQuantity);
            cartDetail.setProductAmount(productAmount);
            cartDetailRepository.save(cartDetail);

            cart.setTotalAmount(productAmount);
            cartRepository.save(cart);
        }
        else {
            Cart cart = cartOptional.get();
            Optional<CartDetail> cartDetailOptional = cartDetailRepository.findByProductAndCart(product, cart);

            if (!cartDetailOptional.isPresent()) {
                Long productQuantity = 1L;
                BigDecimal productAmount = product.getPrice().multiply(BigDecimal.valueOf(productQuantity));

                CartDetail cartDetail = new CartDetail();
                cartDetail.setCart(cart);
                cartDetail.setProduct(product);
                cartDetail.setProductTitle(product.getTitle());
                cartDetail.setProductPrice(product.getPrice());
                cartDetail.setProductQuantity(productQuantity);
                cartDetail.setProductAmount(productAmount);
                cartDetailRepository.save(cartDetail);

                BigDecimal totalAmount = cartDetailRepository.getTotalAmount(cart);

                cart.setTotalAmount(totalAmount);
                cartRepository.save(cart);
            }
            else {
                CartDetail cartDetail = cartDetailOptional.get();
                Long currentQuantity = cartDetail.getProductQuantity();
                Long plusQuantity = 1L;
                Long newQuantity = currentQuantity + plusQuantity;
                BigDecimal productPrice = product.getPrice();
                BigDecimal productAmount = productPrice.multiply(BigDecimal.valueOf(newQuantity));

                cartDetail.setProductTitle(product.getTitle());
                cartDetail.setProductPrice(productPrice);
                cartDetail.setProductQuantity(newQuantity);
                cartDetail.setProductAmount(productAmount);
                cartDetailRepository.save(cartDetail);

                BigDecimal totalAmount = cartDetailRepository.getTotalAmount(cart);

                cart.setTotalAmount(totalAmount);
                cartRepository.save(cart);

            }
        }
    }

    @Override
    public Cart save(Cart cart) {
        return null;
    }

    @Override
    public void delete(Cart cart) {

    }

    @Override
    public void deleteById(Long id) {

    }
}
