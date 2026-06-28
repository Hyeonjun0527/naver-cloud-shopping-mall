package com.model2.mvc.service.test;
import com.model2.mvc.common.Search;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    locations = {"classpath:config/commonservice.xml"})
public class ProductServiceTest {

    ///Field
    private ProductService productService;
    ///Setter
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public void setProductService(@Qualifier("productServiceImpl") ProductService productService) {
        this.productService = productService;
    }
    ///Constructor
    public ProductServiceTest() {
    }
    ///Method
    public Product getProductInstance() {
        Product product = new Product();
        product.setFileName("testFileName");
        product.setManuDate("2021-05-05");
        product.setPrice(10000);
        product.setProdDetail("testProdDetail");
        product.setProdName("testProdName");
        product.setProdNo(10034);
        product.setRegDate(Date.valueOf("2021-05-05"));
        product.setProTranCode("1");
        return product;
    }

    //@Test
    public void testAddProduct() throws Exception {
        Product product = getProductInstance();
        productService.addProduct(product);
    }



    @Test
    public void testGetProduct() throws Exception {
        Product product = getProductInstance();
        System.out.println(productService.getProduct(product.getProdNo()));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        Product product = getProductInstance();
        productService.updateProduct(product);
    }

    //@Test
    public void testDeleteProduct() throws Exception {
    	Product product = getProductInstance();
    	product = productService.getProduct(product.getProdNo());
    	productService.deleteProduct(product.getProdName());
    }
    @Test
    public void testGetProductList() throws Exception {

        Search search = new Search();
        search.setCurrentPage(1);
        search.setPageSize(3);
        search.setSearchCondition("0");
        search.setSearchKeyword("");
        search.setSearchType("0");
        System.out.println( productService.getProductList(search) );
    }

}
