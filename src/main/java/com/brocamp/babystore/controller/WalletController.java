package com.brocamp.babystore.controller;

import com.brocamp.babystore.model.Wallet;
import com.brocamp.babystore.security.CustomUser;
import com.brocamp.babystore.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class WalletController {
    private WalletService walletService;
    @GetMapping("user_home/wallet")
    public String loadAllWallet(Authentication authentication,
                                Model model){
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        List<Wallet> walletList = walletService.findAllByUserId(customUser.getId());
        double amountInWallet = walletService.findSumOfWalletAmount(customUser.getId());
        model.addAttribute("walletHistory",walletList);
        model.addAttribute("amountInWallet",amountInWallet);
        model.addAttribute("users",customUser);
        return "user/wallet";
    }
}
