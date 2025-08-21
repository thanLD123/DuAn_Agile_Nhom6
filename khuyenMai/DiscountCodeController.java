package com.example.demo.controllers.admin;

import com.example.demo.dto.DiscountCode.DiscountCodeDto;
import com.example.demo.dto.DiscountCode.SearchDiscountCodeDto;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.exceptions.ShopApiException;
import com.example.demo.services.DiscountCodeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Calendar;
import java.util.Date;

@Controller
public class DiscountCodeController {
    private final DiscountCodeService discountCodeService;

    public DiscountCodeController(DiscountCodeService discountCodeService) {
        this.discountCodeService = discountCodeService;
    }

    @GetMapping("/admin-only/discount-code")
    public String viewDiscountCodePage(Model model, SearchDiscountCodeDto searchDiscountCodeDto,
                                       @RequestParam(name = "page", defaultValue = "1") Integer page) {

        Page<DiscountCodeDto> discountCodes = discountCodeService.getAllDiscountCode(searchDiscountCodeDto, page);

        model.addAttribute("discountCodes", discountCodes.getContent());
        model.addAttribute("dataSearch", searchDiscountCodeDto);
        model.addAttribute("totalPage", discountCodes.getTotalPages());
        model.addAttribute("currentPage", page);

        return "/admin/discount";
    }

    @GetMapping("/admin-only/discount-code-create")
    public String viewDiscountCodeCreatePage(Model model) {
        DiscountCodeDto discountCodeDto = new DiscountCodeDto();
        discountCodeDto.setType(1);
        discountCodeDto.setStartDate(new Date());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        Date endDate = calendar.getTime();
        discountCodeDto.setEndDate(endDate);

        model.addAttribute("DiscountCode", discountCodeDto);
        model.addAttribute("action", "/admin/discount-code-save");
        return "admin/discount-code-create";
    }

    @GetMapping("/admin-only/discount-code-edit/{id}")
    public String viewDiscountCodeEditPage(Model model, @PathVariable Long id) {
        try {
            DiscountCodeDto discountCodeDto = discountCodeService.getDiscountCodeById(id);
            model.addAttribute("DiscountCode", discountCodeDto);
            model.addAttribute("action", "/admin/discount-code-update");
            return "admin/discount-code-edit";
        } catch (NotFoundException ex) {
            // Redirecting to a 404 error page or a custom error page
            return "error/404"; // Ensure this view exists
        } catch (Exception ex) {
            // Log unexpected exceptions
            return "error/general"; // A general error page
        }
    }

    @PostMapping("/admin/discount-code-save")
    public String saveDiscountCode(Model model, RedirectAttributes redirectAttributes, @ModelAttribute("DiscountCode") DiscountCodeDto discountCodeDto) {
        try {
            DiscountCodeDto dto = discountCodeService.saveDiscountCode(discountCodeDto);
            redirectAttributes.addFlashAttribute("message", "Mã giảm giá " + dto.getCode() + " đã thêm thành công");
        } catch (ShopApiException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/admin-only/discount-code-create";
        }
        return "redirect:/admin-only/discount-code";
    }

    @PostMapping("/admin/discount-code-update")
    public String updateDiscountCode(Model model, RedirectAttributes redirectAttributes, @ModelAttribute("DiscountCode") DiscountCodeDto discountCodeDto) {
        try {
            DiscountCodeDto dto = discountCodeService.updateDiscountCode(discountCodeDto);
            redirectAttributes.addFlashAttribute("message", "Mã giảm giá " + dto.getCode() + " đã cập nhật thành công");
        } catch (ShopApiException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/admin-only/discount-code-edit/" + discountCodeDto.getId();
        }
        return "redirect:/admin-only/discount-code";
    }

    @PostMapping("/admin/update-discount-status/{status}")
    public String updateDiscountCodeStatus(Model model, RedirectAttributes redirectAttributes, @ModelAttribute("id") Long id, @PathVariable int status) {
        try {
            discountCodeService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("message", "Mã giảm giá đã được cập nhật");
        } catch (NotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/admin-only/discount-code";
    }

    @ResponseBody
    @GetMapping("/api/private/discount-code")
    public Page<DiscountCodeDto> getAllDiscountCodes(SearchDiscountCodeDto searchDiscountCodeDto, Integer page) {
        return discountCodeService.getAllDiscountCode(searchDiscountCodeDto, page);
    }

    @ResponseBody
    @GetMapping("/api/private/discount-code-valid")
    public Page<DiscountCodeDto> getAllValidDiscountCodes(Pageable pageable) {
        return discountCodeService.getAllAvailableDiscountCode(pageable);
    }
}
