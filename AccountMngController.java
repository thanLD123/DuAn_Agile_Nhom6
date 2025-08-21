package com.example.demo.controllers.admin;

import com.example.demo.entities.Account;
import com.example.demo.repositories.AccountRepository;
import com.example.demo.sercurity.CustomUserDetails;
import com.example.demo.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AccountMngController {
    private final AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    public AccountMngController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/admin-only/account-management")
    public String viewAccountManagementPage(Model model) {
        List<Account> accountList = accountService.findAllAccount();
        model.addAttribute("accountList", accountList);
        return "/admin/account";
    }
    @GetMapping("/admin-only/account-employee")
    public String viewAccount(Model model) {
        Long roleId = 2L; // Giá trị cố định
        List<Account> accountList = accountRepository.findAccountsByRoleIdNative(roleId);
        model.addAttribute("accountList", accountList);
        return "/admin/accountemployee";
    }
    @GetMapping("/admin-only/account-user")
    public String viewAccount1(Model model) {
        Long roleId = 3L; // Giá trị cố định
        List<Account> accountList = accountRepository.findAccountById(roleId);
        model.addAttribute("accountList", accountList);
        return "/admin/accountuser";
    }
    @PostMapping("/account/block/{id}")
    public String blockAccount(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        var accountPresent = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (accountPresent.getAccount().getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errMessage", "Bạn không thể tự khóa tài khoản của mình");
            return "redirect:/admin-only/account-management";
        }
        if (accountService.countAllByRole_IdAndIsNonLockedTrue(1) <= 2) {
            redirectAttributes.addFlashAttribute("errMessage", "Cần tối thiểu 2 tài khoản quản lý");
            return "redirect:/admin-only/account-management";
        }
        Account account = accountService.blockAccount(id);

        redirectAttributes.addFlashAttribute("message", "Tài khoản " + account.getEmail() + " đã khóa thành công");
        return "redirect:/admin-only/account-management";


    }
    @PostMapping("/accountEmployee/block/{id}")
    public String blockAccountemployee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        var accountPresent = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (accountPresent.getAccount().getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errMessage", "Bạn không thể tự khóa tài khoản của mình");
            return "redirect:/admin-only/account-employee";
        }
        Account account = accountService.blockAccount(id);

        redirectAttributes.addFlashAttribute("message", "Tài khoản " + account.getEmail() + " đã khóa thành công");
        return "redirect:/admin-only/account-employee";


    }
    @PostMapping("/accountUser/block/{id}")
    public String blockAccountUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        var accountPresent = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (accountPresent.getAccount().getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errMessage", "Bạn không thể tự khóa tài khoản của mình");
            return "redirect:/admin-only/account-user";
        }
        Account account = accountService.blockAccount(id);

        redirectAttributes.addFlashAttribute("message", "Tài khoản " + account.getEmail() + " đã khóa thành công");
        return "redirect:/admin-only/account-user";


    }

    @PostMapping("/accountEmployee/open/{id}")
    public String openAccountEmployee(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Account account = accountService.openAccount(id);
        redirectAttributes.addFlashAttribute("message", "Tài khoản " + account.getEmail() + " đã mở khóa thành công");
        return "redirect:/admin-only/account-employee";
    }
    @PostMapping("/accountUser/open/{id}")
    public String openAccountUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Account account = accountService.openAccount(id);
        redirectAttributes.addFlashAttribute("message", "Tài khoản " + account.getEmail() + " đã mở khóa thành công");
        return "redirect:/admin-only/account-user";
    }

    @PostMapping("/account/change-roleEmployee")
    public String openAccountEmployee(@ModelAttribute("email") String email, @ModelAttribute("role") Long roleId, RedirectAttributes redirectAttributes) {
        Account account = accountService.changeRole(email, roleId);
        redirectAttributes.addFlashAttribute("message", "Tài khoản " + account.getEmail() + " đã đổi thành quyền thành công");
        return "redirect:/admin-only/account-employee";
    }
    @PostMapping("/account/change-roleUser")
    public String openAccountUser(@ModelAttribute("email") String email, @ModelAttribute("role") Long roleId, RedirectAttributes redirectAttributes) {
        Account account = accountService.changeRole(email, roleId);
        redirectAttributes.addFlashAttribute("message", "Tài khoản " + account.getEmail() + " đã đổi thành quyền thành công");
        return "redirect:/admin-only/account-user";
    }
}
