package com.model2.mvc.service.product.impl;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductDao;
import com.model2.mvc.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("productServiceImpl")
public class ProductServiceImpl implements ProductService{



	///Field
	private ProductDao productDao;//얘는 has a 인데 왜 동기화 문제가 안될까? 겹치지만 문제가 발생하지않음. 이 필드값을 수정하지 않아서임. 동기화문제의 발생 성립요건은 값을 바꾸는거임
	//private Purchase purchase;


	@Autowired

	public void setProductDao(@Qualifier("productDaoImpl") ProductDao productDao) {
		this.productDao = productDao;
	}



	///Constructor
	public ProductServiceImpl() {
		System.out.println(this.getClass());
	}


	///Method
	public Product addProduct(Product product) throws Exception {
		productDao.insertProduct(product);

		//판매중으로 바꿔야함
		product.setProTranCode("a");
		
		return product;
	}

	public Product getProduct(int prodNo) throws Exception {
		return productDao.findProduct(prodNo);
	}
	public Product updateProduct(Product product) throws Exception {
		productDao.updateProduct(product);//원래트랜코드도 유지해 왜냐면 원래있던거를 가져다가 수정해서 건내주기때문
		return  product;
	}

	public void deleteProduct(String prodName) throws Exception {
		productDao.deleteProduct(prodName);
	}

	public Map<String,Object> getProductList(Search search) throws Exception {
		return productDao.getProductList(search);
	}



}