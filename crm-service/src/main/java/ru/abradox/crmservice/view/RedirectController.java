package ru.abradox.crmservice.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.abradox.crmservice.config.RedirectProperties;

@Controller
@RequiredArgsConstructor
public class RedirectController {

    private final RedirectProperties properties;

    @GetMapping("/view/redirect")
    public String getRedirectPage(Model model) {
        model.addAttribute("LK_URL", properties.getLkUrl());
        model.addAttribute("REGISTRATION_URL", properties.getRegistrationUrl());
        model.addAttribute("BLOCKED_URL", properties.getBlockedUrl());
        return "redirect";
    }
}
