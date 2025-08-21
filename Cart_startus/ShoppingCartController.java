package com.example.demo.controllers.user;

import com.example.demo.dto.Account.AccountDto;
import com.example.demo.dto.AddressShipping.AddressShippingDto;
import com.example.demo.dto.Cart.CartDto;
import com.example.demo.dto.product.ProductDetailDto;
import com.example.demo.entities.Product;
import com.example.demo.entities.ProductDetail;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.ShopApiException;
import com.example.demo.services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class ShoppingCartController {
    private final CartService cartService;
    private final AddressShippingService addressShippingService;

    @Autowired
    private AccountService accountService;
   @Autowired
   private ProductDetailService productDetailService;
    public ShoppingCartController(CartService cartService, AddressShippingService addressShippingService) {
        this.cartService = cartService;
        this.addressShippingService = addressShippingService;
    }

    @GetMapping("/shoping-cart")
    public String viewShoppingCart(Model model) {
        List<CartDto> cartDtoList = cartService.getAllCartByAccountId();
//        Page<DiscountCodeDto> discountCodeList = discountCodeService.getAllAvailableDiscountCode(PageRequest.of(0, 15));
        List<AddressShippingDto> addressShippingDtos = addressShippingService.getAddressShippingByAccountId();
        AccountDto accountDto = accountService.getAccountLogin();
//        model.addAttribute("discountCodes", discountCodeList.getContent());
        model.addAttribute("addressShippings", addressShippingDtos);
        model.addAttribute("profile", accountDto);
        model.addAttribute("carts", cartDtoList);
        return "user/shoping-cart";
    }

    @ResponseBody
    @PostMapping("/api/addToCart")
    public void addToCart(@RequestBody CartDto cartDto) throws NotFoundException {
        cartService.addToCart(cartDto);
    }

    @ResponseBody
    @PostMapping("/api/deleteCart/{id}")
    public void deleteCart(@PathVariable Long id) {
        cartService.deleteCart(id);
    }

    @ResponseBody
    @PostMapping("/api/updateCart")
    public void updateCart(@RequestBody CartDto cartDto) throws NotFoundException {
        cartService.updateCart(cartDto);
    }

    @ResponseBody
    @RequestMapping(value = "/shoping-cart/update-profile", method = RequestMethod.POST)
    public ResponseEntity<?> updateProfileShopingCart(@RequestBody AccountDto accountDto){
        try {
            accountService.updateProfile(accountDto);
            return ResponseEntity.ok("Cập nhật thông tin thành công !");
        } catch (ShopApiException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Cập nhật thông tin thất bại !");
        }
    }
    @ResponseBody
    @GetMapping("/api/product/{productId}/stock")
    public ResponseEntity<Integer> getStockQuantity(@PathVariable Long productId) {
        // Lấy danh sách chi tiết sản phẩm
        List<ProductDetailDto> productDetails = productDetailService.getByProductId(productId);

        // Kiểm tra nếu không có chi tiết sản phẩm nào
        if (productDetails.isEmpty()) {
            // Trả về mã 404 nếu không có chi tiết sản phẩm
            return ResponseEntity.notFound().build();
        }

        // Lấy số lượng từ phần tử đầu tiên
        int stockQuantity = productDetails.get(0).getQuantity();
        return ResponseEntity.ok(stockQuantity);
    }

//    $('.btn-num-product-up').on('click', function () {
//        var row = $(this).closest('.table-row');
//        var quantityInput = row.find(".num-product");
//        var currentQuantity = Number(quantityInput.val());
//        var productId = row.data('product-id'); // Giả định bạn có data attribute chứa productId
//
//        // Gọi AJAX để lấy số lượng trong kho
//        $.ajax({
//                url: '/api/product/' + productId + '/stock',
//                method: 'GET',
//                success: function(stockQuantity) {
//            // Tăng số lượng
//            if (currentQuantity < stockQuantity) {
//                quantityInput.val(currentQuantity + 1);
//            } else {
//                // Nếu số lượng vượt quá kho, hiển thị thông báo và đặt lại về 1
//                Swal.fire({
//                        text: 'Số lượng không thể vượt quá số lượng sản phẩm trong kho (' + stockQuantity + ')',
//                        title: "Thông báo",
//                        icon: 'warning',
//                        confirmButtonText: "Đồng ý"
//                });
//
//                // Đặt lại số lượng về 1
//                quantityInput.val(1);
//            }
//
//            // Cập nhật các giá trị khác
//            $('#selected-voucher').html('');
//            $('#selected-voucher').attr('data-selected', "");
//            voucherChoosed = null;
//            calculateVoucherPrice();
//
//            updateSubtotal(row);
//            calculateTotal(); // Cập nhật tổng số lượng
//            updateToServer(row, quantityInput.val());
//        }
//    });
//    });
}
