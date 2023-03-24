package com.cg.service.product;

import com.cg.model.Product;
import com.cg.model.dto.product.ProductCreateReqDTO;
import com.cg.model.dto.product.ProductCreateResDTO;
import com.cg.service.IGeneralService;

public interface IProductService extends IGeneralService<Product> {

    ProductCreateResDTO create(ProductCreateReqDTO productCreateReqDTO);
}
