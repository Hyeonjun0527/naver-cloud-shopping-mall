package com.model2.mvc.web.user;

import com.model2.mvc.common.Page;
import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.CookieUtil;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
@RequestMapping("/product/*")
public class ProductController {
    ///Field
    private ProductService productService;
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[91m";
    private static final String ORANGE = "\u001B[38;5;208m";
    private static final String YELLOW = "\u001B[93m";
    private static final String GREEN = "\u001B[92m";
    private static final String BLUE = "\u001B[94m";

    @Autowired
    public void setProductService(@Qualifier("productServiceImpl") ProductService productService) {
        this.productService = productService;
    }

    @Value("#{commonProperties['pageUnit']}")
    //@Value("#{commonProperties['pageUnit'] ?: 3}")
    int pageUnit;

    @Value("#{commonProperties['pageSize']}")
    //@Value("#{commonProperties['pageSize'] ?: 2}")
    int pageSize;

    ///Constructor
    public ProductController() {
        System.out.println(YELLOW);
        System.out.println("생성자 :: " + this.getClass());
        System.out.println(RESET);
    }

    ///Method
    @RequestMapping("/addProduct")
    public String addProduct(@RequestParam("fileList") List<MultipartFile> fileList,
                             @ModelAttribute("product") Product product,
                             RedirectAttributes redirectAttributes,
                             Model model) throws Exception {
        System.out.println("/addProduct이 시작됩니다..");

        if (fileList.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/product/addProductView.jsp";
        }
//        String fileName = getProductFileName(file);
        List<String> fileNameList = new ArrayList<>();

        for(MultipartFile file : fileList){
            fileNameList.add(getProductFileName(file));
        }
        System.out.println("fileNameList :: "+fileNameList);

        String fileNamesStr = String.join(",", fileNameList);
        System.out.println(fileNamesStr);

        //원래 이렇게 a넣으면 안될거 같은데..?
        product.setProTranCode("a");
        product.setFileName(fileNamesStr);
        //Business Logic
        System.out.println("당연히 prodNo 0이다. product :: " + product);
        productService.addProduct(product);
        //Model 과 View 연결 - 이 부분은 @RequestParam + Model 전략을 쓰면 해야됨
        //model.addAttribute("product", product);
        model.addAttribute("fileNameList",fileNameList);


        redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + fileNamesStr + "' and added product information.");
        System.out.println("/addProduct이 끝났습니다..");
        System.out.println("forward:/product/addProductView.jsp" + "합니다.");
        return "forward:/product/getProduct.jsp";
    }//end of addProduct
    @RequestMapping("/getProduct")
    public String getProduct(HttpServletRequest request,
                             HttpServletResponse response,
                             @ModelAttribute("product") Product product,
                             @RequestParam("menu") String menu,
                             Model model) throws Exception {

        System.out.println("/getProduct이 시작됩니다..");
        //Business Logic
        product = productService.getProduct(product.getProdNo());
        //Model 과 View 연결
        // 메서드내에서 참조변수에 도메인을 재할당해버린 경우에는
        // ModelAttribute가 인식을 하지 못한다.
        List<String> fileNameList = Arrays.asList(product.getFileName().split(","));//1234,1234,1,

        System.out.println(fileNameList.toString());

        model.addAttribute("fileNameList", fileNameList);
        model.addAttribute("menu", menu);
        model.addAttribute("product", product);

        CookieUtil.addValue(request, response, "history", String.valueOf(product.getProdNo()));

        System.out.println("/getProduct이 끝났습니다..");

        if (menu.equals("manage")) {
            System.out.println("forward:/updateProduct.jsp" + "합니다.");
            return "forward:/product/updateProduct.jsp";
        } else {//search, ok
            return "forward:/product/getProduct.jsp";
        }//end of else
    }//end of getProduct

    //클라-searchBoundFirst,searchBoundEnd,currentPage,search,menu,products,page
    //currentPage의
    @RequestMapping("/likeProduct")
    public String likeProduct(@ModelAttribute("search") Search search,
                              @RequestParam(value = "menu",required = false) String menu,
                              @RequestParam(value = "searchBoundFirst", required = false) Integer searchBoundFirst,
                              @RequestParam(value = "searchBoundEnd", required = false) Integer searchBoundEnd,
                              Model model,
                              HttpServletRequest request) throws Exception {
        //search,searchBoundFirst,End는 스스로 안에서 페이지 이동할때의 경우임.


        //@ModelAttribute("products") ArrayList<Product> products

        System.out.println("/likeProduct이 시작됩니다..");
        //바인딩 : 클라-searchBoundFirst, searchBoundEnd > search도메인

        if(searchBoundFirst == null || searchBoundEnd == null) {
            searchBoundFirst = 0;
            searchBoundEnd = 0;
        }
        //바인딩 : 클라-searchBoundFirst, searchBoundEnd > search도메인
        if (!(searchBoundFirst == 0) || !(searchBoundEnd == 0)) {
            search.setSearchBoundFirst(searchBoundFirst);
            search.setSearchBoundEnd(searchBoundEnd);
//                System.out.println("searchBound[0]"+searchBound[0]);
//                System.out.println("searchBound[1]"+searchBound[1]);
        }

        //바인딩 : 클라-currentPage > search도메인 > page도메인
        int currentPage = 1;
        //경로 1,2,3,4로 들어왔을경우
        if (search.getCurrentPage() != 0) {
            currentPage = search.getCurrentPage();
        }

        search.setCurrentPage(currentPage);//current의 default Value를 1로 설정했음.
        search.setPageSize(pageSize);

        //createLike에서 가져옴
        Map<String,Object> createLikeData = (Map<String, Object>)request.getAttribute("createLikeData");

        //menu,currentPage,products
        List<Product> products =(List<Product>)(createLikeData.get("products"));

        int productsSize = products != null ? products.size() : 0;

        Page page = new Page(
                currentPage,
                productsSize,
                pageUnit,
                pageSize);

        System.out.println("ListProductAction ::" + page);
        System.out.println("products :: " + products);

        //Model 과 View 연결
        model.addAttribute("totalCount", productsSize);
        model.addAttribute("products",products);
        model.addAttribute("search",search);
        model.addAttribute("resultPage", page);
        model.addAttribute("menu", menu);

        System.out.println("/likeProduct이 끝났습니다..");

        //네비게이션
        if (menu != null) {
            return "forward:/product/likeProduct.jsp";
        } else {
            System.out.println("forward:/product/getProduct.jsp" + "합니다.");
            return "forward:/product/getProduct.jsp";
        }//end of else
    }//end of likeProduct

    //클라-searchBoundFirst,searchBoundEnd,search,menu,products,page
    @RequestMapping("/listProduct")
    public String listProduct(@ModelAttribute(value = "search") Search search,
                              @RequestParam(value = "searchBoundFirst", required = false) Integer searchBoundFirst,
                              @RequestParam(value = "searchBoundEnd", required = false) Integer searchBoundEnd,
                              @RequestParam(value = "menu", required = false) String menu,
                              Model model) throws Exception {
        System.out.println("/listProduct이 시작됩니다..");
        System.out.println("searchBound :: " + searchBoundFirst + " " + searchBoundEnd);
        System.out.println("searchType :: "+ search.getSearchType());
//        if(search.getSearchType() == null) {
//            search.setSearchType("1");
//        }

        if(searchBoundFirst ==null && searchBoundEnd == null) {
        searchBoundFirst = 0;
        searchBoundEnd = 0;
        }
        //바인딩 : 클라-searchBoundFirst, searchBoundEnd > search도메인
        if (!(searchBoundFirst == 0) || !(searchBoundEnd == 0)) {
            search.setSearchBoundFirst(searchBoundFirst);
            search.setSearchBoundEnd(searchBoundEnd);
//                System.out.println("searchBound[0]"+searchBound[0]);
//                System.out.println("searchBound[1]"+searchBound[1]);
        }


        //바인딩 : 클라-currentPage > search도메인 > page도메인
        int currentPage = 1;
        //경로 1,2,3,4로 들어왔을경우
        if (search.getCurrentPage() != 0) {
            currentPage = search.getCurrentPage();
        }

        search.setCurrentPage(currentPage);
        search.setPageSize(pageSize);

        Map<String, Object> productMap = productService.getProductList(search);//Like와 다른 부분

        Page page = new Page(
                currentPage,
                (Integer) productMap.get("totalCount"),
                pageUnit,
                pageSize);

        System.out.println("ListProductAction ::" + page);
        System.out.println("products :: " + productMap.get("list"));
        System.out.println("search :: " + search);

        //Model 과 View 연결
        //model.addAttribute("products", products);
        //model.addAttribute("search", search);
        model.addAttribute("totalCount", productMap.get("totalCount"));
        model.addAttribute("list", productMap.get("list"));
        model.addAttribute("resultPage", page);
        model.addAttribute("menu", menu);

        System.out.println("/listProduct이 끝났습니다..");

        //네비게이션
        if (menu != null) {
            System.out.println("forward:/product/listProduct.jsp" + "합니다.");
            return "forward:/product/listProduct.jsp";
        } else {
            System.out.println("forward:/product/getProduct.jsp" + "합니다.");
            return "forward:/product/getProduct.jsp";
        }//end of else
    }//end of listProduct

    //prodNo,menu,currentPage,
    @RequestMapping("/setLikeProduct")
    public String SetLikeProduct(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @RequestParam("prodNo") int prodNo,
                                 @RequestParam("menu") String menu,
                                 @RequestParam("currentPage") int currentPage,
                                 @CookieValue(name = "like", required = false) String likeCookie,
                                 Model model) throws Exception {
        System.out.println("/////////////////////////////////////////////////////////////setLikeProduct이 시작됩니다..");


        //Business Logic
        Product product = productService.getProduct(prodNo);

        //쿠키디버깅 ;
        if (likeCookie!=null && likeCookie.startsWith(";")) {
            likeCookie = likeCookie.substring(1);
        }


        System.out.println("setLikeProduct::likeCookie :: "+likeCookie);
        String[] likeCookies = null;

        if (likeCookie != null) {
            likeCookies = likeCookie.split(";");
        } // IF

        System.out.println("setLikeProduct::likeCookies::"+ Arrays.toString(likeCookies));

        boolean result = true;
        if(likeCookies!=null) {
            for (String element : likeCookies) {
                if (element.equals(String.valueOf(product.getProdNo()))) {//같은게 하나라도 있으면 false
                    result = false;
                } // IF
            } // FOR
        }
        if(result) {//같은게 없으면 true
            CookieUtil.addValue(request, response, "like", String.valueOf(product.getProdNo()));
        }

        request.setAttribute("currentPage", currentPage);
        //request.setAttribute("from", "setLikeProduct");
        //request.setAttribute("product",product);

        System.out.println("//////////////////////////////////////////////////////////////setLikeProduct이 끝났습니다..");
        System.out.println("forward:/product/listProduct?menu=search" + "합니다.");
        //어차피 찜리스트는 관리자는 실행할 수 없도록 해놓았음.
        return "forward:/product/listProduct"+"?currentPage="+currentPage;
    }//end of SetLikeProduct

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute("product") Product product,
                                @RequestParam("fileList") List<MultipartFile> fileList,
                                RedirectAttributes redirectAttributes,
                                Model model) throws Exception {
        System.out.println("/updateProduct 이 시작됩니다.");
        System.out.println("fileList::" + fileList);
        if (fileList.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            throw new Exception("파일이 ㅇ벗습니다.");
        }

//        String fileName = getProductFileName(file);
        List<String> fileNameList = new ArrayList<>();
        for(MultipartFile file : fileList) {
            fileNameList.add(getProductFileName(file));

        }

        System.out.println("fileName :: "+fileNameList);

        //Business Logic
        product = productService.getProduct(product.getProdNo());

//        product.setFileName(fileName);
        product.setFileName(String.join(",", fileNameList));
        product.setManuDate(product.getManuDate().replaceAll("-", ""));

        System.out.println("업데이트 완료 :: " + productService.updateProduct(product));

        //Model 과 View 연결
        model.addAttribute("product", product);
        model.addAttribute("fileNameList",fileNameList);

        System.out.println("/updateProduct 이 끝났습니다.");
        System.out.println("redirect:/getProduct" + "합니다.");
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + String.join(", ", fileNameList) + "' and added product information.");
        return "redirect:/product/getProduct?prodNo=" + product.getProdNo() + "&menu=ok";
    }//end of updateProduct

    //prodNo,
    @GetMapping("/updateProduct")
    public String updateProductView(@ModelAttribute Product product,
                                    Model model) throws Exception {

        System.out.println("/updateProductView" + "이 시작됩니다..");
        product = productService.getProduct(product.getProdNo());

        // Model 과 View 연결
        model.addAttribute("product", product);

        System.out.println("/updateProductView" + "이 끝났습니다.");
        System.out.println("forward:/product/updateProduct.jsp" + "합니다.");
        return "forward:/product/updateProduct.jsp";
    }

    public String getProductFileName(MultipartFile file) throws Exception{;

        String uploadDir = "C:/Users/osuma/git/Myshop/Myshop08/src/main/webapp/images/uploadFiles/";
        String fileName = file.getOriginalFilename();


        System.out.println("uploadDir :: "+uploadDir);
        System.out.println("fileName :: " + fileName);

        Path path = Paths.get(uploadDir + fileName);

        FileCopyUtils.copy(file.getBytes(), new File(path.toString()));
//      Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

}
