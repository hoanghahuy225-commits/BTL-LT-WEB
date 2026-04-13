package com.example.WebBanHang.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import com.example.WebBanHang.model.User;
import com.example.WebBanHang.repository.OrderRepository;
import com.example.WebBanHang.repository.ChatConversationRepository;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice(basePackages = "com.example.WebBanHang.controller.admin")
public class AdminCommonAdvice {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ChatConversationRepository chatConversationRepository;

    @ModelAttribute
    public void addCommonAttributes(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("currentUser", currentUser);
        }
        
        // Pending order count for sidebar badge
        try {
            long pendingCount = orderRepository.countByOrderStatus("Pending");
            model.addAttribute("pendingOrderCount", pendingCount);
        } catch (Exception e) {
            model.addAttribute("pendingOrderCount", 0);
        }

        // Unread chat count for topbar bell and sidebar
        try {
            Integer unreadSum = chatConversationRepository.sumTotalUnreadCount();
            model.addAttribute("unreadChatCount", unreadSum != null ? unreadSum : 0);
        } catch (Exception e) {
            model.addAttribute("unreadChatCount", 0);
        }
    }
}
