package com.example.WebBanHang.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminChatController {

    @GetMapping("/chat")
    public String chatPage(Model model) {
        model.addAttribute("activePage", "chat");
        model.addAttribute("pageTitle", "Tin nhắn");
        model.addAttribute("contentFragment", "admin/chat :: content");
        return "admin/layout";
    }
}
