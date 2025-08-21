package com.example.demo.controllers.admin;

import com.example.demo.dto.Bill.*;
import com.example.demo.entities.Bill;
import com.example.demo.entities.DiscountCode;
import com.example.demo.entities.enumClass.BillStatus;
import com.example.demo.entities.enumClass.InvoiceType;
import com.example.demo.repositories.DiscountCodeRepository;
import com.example.demo.repositories.ProductDetailRepository;
import com.example.demo.services.BillService;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class BillController {
    @Autowired
    private BillService billService;
    @Autowired
    private ProductDetailRepository productDetailRepository;
    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    @GetMapping("/list-in-store-invoice")
    public ResponseEntity<?> listInStoreInvoice() {

        var listBillId = billService.findAllInStoreInvoiceId();
        listBillId.forEach(id -> {

        });
        return null;

    }
    @GetMapping("/bill-list")
    public String getBill(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sort", defaultValue = "createDate,asc") String sortField,
            @RequestParam(name = "maDinhDanh", required = false) String maDinhDanh,
            @RequestParam(name = "ngayTaoStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayTaoStart,
            @RequestParam(name = "ngayTaoEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayTaoEnd,
            @RequestParam(name = "trangThai", required = false) String trangThai,
            @RequestParam(name = "loaiDon", required = false) String loaiDon,
            @RequestParam(name = "soDienThoai", required = false) String soDienThoai,
            @RequestParam(name = "hoVaTen", required = false) String hoVaTen
    ) {
        // Cập nhật trạng thái cho hóa đơn
        billService.updateStatusChoHangVe();

        int pageSize = 8;
        Sort sort = Sort.by(Sort.Direction.fromString(sortField.split(",")[1]), sortField.split(",")[0]);
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        // Khai báo biến billPage
        Page<BillDtoInterface> billPage;

        // Chuyển đổi ngày nếu có
        LocalDateTime convertedNgayTaoStart = (ngayTaoStart != null) ?
                ngayTaoStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;

        LocalDateTime convertedNgayTaoEnd = (ngayTaoEnd != null) ?
                ngayTaoEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;

        // Kiểm tra tham số tìm kiếm
        boolean hasFilters = maDinhDanh != null || convertedNgayTaoStart != null || convertedNgayTaoEnd != null ||
                trangThai != null || loaiDon != null || soDienThoai != null || hoVaTen != null;

        // Lấy tất cả hóa đơn nếu không có bộ lọc
        if (!hasFilters) {
            billPage = billService.findAll(pageable);
        } else {
            // Trimming tham số và tìm kiếm theo bộ lọc
            billPage = billService.searchListBill(
                    maDinhDanh != null ? maDinhDanh.trim() : null,
                    convertedNgayTaoStart,
                    convertedNgayTaoEnd,
                    trangThai,
                    loaiDon,
                    soDienThoai != null ? soDienThoai.trim() : null,
                    hoVaTen != null ? hoVaTen.trim() : null,
                    pageable
            );
        }

        // Thêm dữ liệu vào model
        model.addAttribute("items", billPage);
        model.addAttribute("sortField", sortField);
        model.addAttribute("maDinhDanh", maDinhDanh);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("loaiDon", loaiDon);
        model.addAttribute("soDienThoai", soDienThoai);
        model.addAttribute("hoVaTen", hoVaTen);
        model.addAttribute("billStatus", BillStatus.values());
        model.addAttribute("invoiceType", InvoiceType.values());

        return "admin/bill";
    }
//@GetMapping("/bill-list")
//public String getBill(
//        Model model,
//        @RequestParam(name = "page", defaultValue = "0") int page,
//        @RequestParam(name = "sort", defaultValue = "createDate,asc") String sortField,
//        @RequestParam(name = "maDinhDanh", required = false) String maDinhDanh,
//        @RequestParam(name = "ngayTaoStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayTaoStart,
//        @RequestParam(name = "ngayTaoEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayTaoEnd,
//        @RequestParam(name = "trangThai", required = false) String trangThai,
//        @RequestParam(name = "loaiDon", required = false) String loaiDon,
//        @RequestParam(name = "soDienThoai", required = false) String soDienThoai,
//        @RequestParam(name = "hoVaTen", required = false) String hoVaTen
//) {
//    // Update status for bills awaiting confirmation but lacking inventory
//    billService.updateStatusChoHangVe();
//
//    int pageSize = 8;
//    String[] sortParams = sortField.split(",");
//    String sortFieldName = sortParams[0];
//    Sort.Direction sortDirection = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
//    Sort sort = Sort.by(sortDirection, sortFieldName);
//    Pageable pageable = PageRequest.of(page, pageSize, sort);
//
//    // Initialize the Page variable as null to catch potential issues
//    Page<BillDtoInterface> billPage = null;
//
//    // Convert dates if they are provided
//    LocalDateTime convertedNgayTaoStart = (ngayTaoStart != null) ?
//            ngayTaoStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
//
//    LocalDateTime convertedNgayTaoEnd = (ngayTaoEnd != null) ?
//            ngayTaoEnd.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
//
//    // Log the conversion for debugging
//    if (convertedNgayTaoStart != null) {
//        System.out.println("Converted NgayTaoStart: " + convertedNgayTaoStart);
//    }
//    if (convertedNgayTaoEnd != null) {
//        System.out.println("Converted NgayTaoEnd: " + convertedNgayTaoEnd);
//    }
//
//    // Check for search parameters and execute appropriate logic
//    boolean hasFilters = maDinhDanh != null || convertedNgayTaoStart != null || convertedNgayTaoEnd != null ||
//            trangThai != null || loaiDon != null || soDienThoai != null || hoVaTen != null;
//
//
//    if (hasFilters) {
//        // Trimming parameters
//        billPage = billService.searchListBill(
//                maDinhDanh != null ? maDinhDanh.trim() : null,
//                convertedNgayTaoStart,
//                convertedNgayTaoEnd,
//                trangThai,
//                loaiDon,
//                soDienThoai != null ? soDienThoai.trim() : null,
//                hoVaTen != null ? hoVaTen.trim() : null,
//                pageable
//        );
//    } else {
//        // Fetch all bills on initial load
//        billPage = billService.findAll(pageable);
//    }
//
//
//
//    // Check if billPage is null and log for debugging
//    if (billPage == null) {
//        System.out.println("BillPage is null. Check your billService methods.");
//        billPage = Page.empty(); // Use an empty page to avoid null errors
//    } else {
//        System.out.println("Fetched Bill Page: " + billPage.getContent().size() + " items"); // Log number of items fetched
//    }
//
//    // Adding the necessary attributes to the model
//
//    model.addAttribute("items", billPage);
//
//    model.addAttribute("sortField", sortFieldName);
//    model.addAttribute("sortDirection", sortDirection);
//    model.addAttribute("maDinhDanh", maDinhDanh);
//    model.addAttribute("trangThai", trangThai);
//    model.addAttribute("loaiDon", loaiDon);
//    model.addAttribute("soDienThoai", soDienThoai);
//    model.addAttribute("hoVaTen", hoVaTen);
//    model.addAttribute("billStatus", BillStatus.values());
//    model.addAttribute("invoiceType", InvoiceType.values());
//
//    return "admin/bill";
//}
    @GetMapping("/update-bill-status/{billId}")
    public String updateBillStatus2(Model model,
                                    @PathVariable Long billId,
                                    @RequestParam String trangThaiDonHang,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Kiểm tra tính hợp lệ của trạng thái trước khi cập nhật
            if (!isValidEnumValue(trangThaiDonHang)) {
                redirectAttributes.addFlashAttribute("error",
                        "Trạng thái đơn hàng không hợp lệ: " + trangThaiDonHang);
                return "redirect:/admin/getbill-detail/" + billId;
            }

            // Gọi service để cập nhật trạng thái
            Bill bill = billService.updateStatus(trangThaiDonHang, billId);
            redirectAttributes.addFlashAttribute("message",
                    "Hóa đơn " + bill.getCode() + " cập nhật trạng thái thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi khi cập nhật trạng thái.");
        }

        return "redirect:/admin/getbill-detail/" + billId;
    }

    // Hàm kiểm tra giá trị Enum hợp lệ
    private boolean isValidEnumValue(String value) {
        for (BillStatus status : BillStatus.values()) {
            if (status.name().equals(value)) {
                return true;
            }
        }
        return false;
    }


    @GetMapping("/getbill-detail/{maHoaDon}")
    public String getBillDetail(Model model, @PathVariable("maHoaDon") Long maHoaDon) {

        BillDetailDtoInterface billDetailDtoInterface = billService.getBillDetail(maHoaDon);
        List<BillDetailProduct> billDetailProducts = billService.getBillDetailProduct(maHoaDon);
        Double total = Double.valueOf("0");
        for (BillDetailProduct billDetailProduct :
                billDetailProducts) {
            int q = billDetailProduct.getSoLuong();
            total += billDetailProduct.getGiaTien() * q;
        }
        model.addAttribute("billDetailProduct", billDetailProducts);
        model.addAttribute("billdetail", billDetailDtoInterface);
        model.addAttribute("total", total);
        return "admin/bill-detail";
    }

    @PostMapping("/api/bill-detail/check-bill")
    public ResponseEntity<?> checkBill(@RequestBody List<Map<String, Integer>> mapProductDetail) {
        if (mapProductDetail.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        for (Map<String, Integer> item : mapProductDetail) {
            Long productDetailId = Long.valueOf(item.get("productDetailId").toString());
            Integer quantity = Integer.valueOf(item.get("quantity").toString());
            var productDetail = productDetailRepository.findById(productDetailId);
            if (productDetail.isPresent()) {
                if (productDetail.get().getQuantity() < quantity) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            }
        }
        return new ResponseEntity<>(HttpStatus.OK);


    }



    @GetMapping("admin/api/bill-detail/rollback-voucher/{voucherId}")
    public ResponseEntity<?> rollbackvoucher(@PathVariable Long voucherId) {
        if (voucherId == 0)
            return ResponseEntity.badRequest().build();
        Optional<DiscountCode> optVoucher = discountCodeRepository.findById(voucherId);
        if (!optVoucher.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        var voucher=optVoucher.get();
        voucher.setMaximumUsage(voucher.getMaximumUsage() + 1);
        discountCodeRepository.save(voucher);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @ResponseBody
    @GetMapping("/api/product/{billId}/bill")
    public ResponseEntity<List<BillDetailProduct>> getAllProductByBillId(@PathVariable Long billId) {
        return ResponseEntity.ok(billService.getBillDetailProduct(billId));
    }

    @ResponseBody
    @GetMapping("/api/bill/validToReturn")
    public Page<BillDto> getAllValidBillToReturn(Pageable pageable) {
        return billService.getAllValidBillToReturn(pageable);
    }
    @GetMapping("/generate-pdf/{maHoaDon}")
    public ResponseEntity<String> generatePDF(@PathVariable Long maHoaDon) {
        // Your HTML content as a string
        String htmlContent = billService.getHtmlContent(maHoaDon);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/html; charset=utf-8");

        return new ResponseEntity<>(htmlContent, headers, HttpStatus.OK);
    }
    @ResponseBody
    @GetMapping("/api/bill/validToReturn/search")
    public Page<BillDto> getAllValidBillToReturnSearch(SearchBillDto searchBillDto, Pageable pageable) {
        return billService.searchBillJson(searchBillDto, pageable);
    }

}
