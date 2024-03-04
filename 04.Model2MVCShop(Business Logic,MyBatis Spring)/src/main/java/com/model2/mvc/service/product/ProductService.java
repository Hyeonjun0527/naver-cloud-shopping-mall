package com.model2.mvc.service.product;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;

import java.util.Map;

public interface ProductService {

	public Product addProduct(Product productVO) throws Exception;
	public Product getProduct(int productNo) throws Exception;
	public Map<String, Object> getProductList(Search search) throws Exception;
	public Product updateProduct(Product productVO) throws Exception;
	public void deleteProduct(String productName) throws Exception;


}
