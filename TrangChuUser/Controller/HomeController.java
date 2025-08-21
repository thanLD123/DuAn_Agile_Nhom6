package com.example.demo.controllers.user;

import com.example.demo.dto.product.ProductDto;
import com.example.demo.dto.product.SearchProductDto;
import com.example.demo.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {



    @Autowired
    private ProductService productService;

    /**
     * Hiển thị trang chủ cho người dùng với quyền hợp lệ
     * @param model mô hình để truyền dữ liệu vào view
     * @return tên view nếu người dùng có quyền hợp lệ
     */
    @GetMapping("/home")
    public String getHomePage(Model model) {
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        // Kiểm tra quyền của người dùng
        if (isAdminOrEmployee((List<GrantedAuthority>) authorities)) {
            return "redirect:/";
        } else {
            return "redirect:/admin";
        }
    }

    /**
     * Kiểm tra quyền của người dùng (User, Customer, Anonymous)
     * @param authorities danh sách quyền của người dùng
     * @return true nếu người dùng có quyền hợp lệ, ngược lại false
     */
    private boolean isAdminOrEmployee(List<GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equalsIgnoreCase("ROLE_USER") ||
                        role.equalsIgnoreCase("ROLE_CUSTOMER") ||
                        role.equalsIgnoreCase("ROLE_ANONYMOUS"));
    }

    /**
     * Hiển thị trang chủ với các sản phẩm tìm kiếm
     * @param model mô hình để truyền dữ liệu vào view
     * @param searchProductDto đối tượng chứa các tham số tìm kiếm
     * @param pageable thông tin phân trang
     * @return tên view cho trang chủ
     */
    @GetMapping("/")
    public String getHomePageWithSearch(Model model, SearchProductDto searchProductDto,
                                        @PageableDefault(size = 20, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) {
        // Lấy danh sách sản phẩm dựa trên các tham số tìm kiếm và phân trang
        Page<ProductDto> products = productService.searchProduct(searchProductDto, pageable);

        // Tạo URL với các tham số tìm kiếm và sắp xếp
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString("/");

        // Thêm các tham số tìm kiếm vào URL
        addSearchParamsToUrl(urlBuilder, searchProductDto, pageable);

        // Truyền dữ liệu vào model
        model.addAttribute("url", urlBuilder.toUriString());
        model.addAttribute("products", products);
        model.addAttribute("dataFilter", searchProductDto==null ? new SearchProductDto() : searchProductDto);
//        List<ProductDto> topProducts = productService.getTop10BestSellingProducts();
//        model.addAttribute("topProducts", topProducts);
//        List<ProductDto> newestProducts = productService.getTop10NewestProducts();
//        model.addAttribute("newestProducts", newestProducts);
        return "user/home";
    }



    /**
     * Thêm các tham số tìm kiếm vào URL
     * @param urlBuilder đối tượng UriComponentsBuilder để xây dựng URL
     * @param searchProductDto đối tượng chứa các tham số tìm kiếm
     * @param pageable thông tin phân trang
     */
    private void addSearchParamsToUrl(UriComponentsBuilder urlBuilder, SearchProductDto searchProductDto, Pageable pageable) {
        if (searchProductDto != null) {
            // Thêm từ khóa tìm kiếm nếu có
            if (searchProductDto.getKeyword() != null && !searchProductDto.getKeyword().isEmpty()) {
                urlBuilder.queryParam("keyword", searchProductDto.getKeyword());
            }

            // Thêm giá trị minPrice và maxPrice nếu có
            if (searchProductDto.getMinPrice() != null) {
                urlBuilder.queryParam("minPrice", searchProductDto.getMinPrice());
            }
            if (searchProductDto.getMaxPrice() != null) {
                urlBuilder.queryParam("maxPrice", searchProductDto.getMaxPrice());
            }


            // Thêm giới tính nếu có
            if (searchProductDto.getGender() != null) {
                urlBuilder.queryParam("gender", searchProductDto.getGender());
            }

            // Thêm tham số sắp xếp nếu có
            if (pageable.getSort().isSorted()) {
                List<String> sortStrings = pageable.getSort().stream()
                        .map(order -> order.getProperty() + "," + (order.isDescending() ? "desc" : "asc"))
                        .collect(Collectors.toList());
                urlBuilder.queryParam("sort", String.join(",", sortStrings));
            }
        }

    }


}
