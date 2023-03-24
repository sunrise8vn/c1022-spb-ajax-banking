package com.cg.model.dto.product;

import com.cg.model.Product;
import com.cg.model.ProductAvatar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductCreateResDTO {

    private Long id;
    private String title;
    private Long quantity;
    private BigDecimal price;
    private String description;

    private ProductAvatarDTO productAvatar;

    public ProductCreateResDTO(Product product, ProductAvatar productAvatar) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.quantity = product.getQuantity();
        this.price = product.getPrice();
        this.description = product.getDescription();
        this.productAvatar = productAvatar.toProductAvatarDTO();
    }
}
