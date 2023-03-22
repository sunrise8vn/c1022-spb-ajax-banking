package com.cg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class HomeController {

    @RequestMapping("")
    public String showHomePage() {
        return "redirect:/customers";
    }

    @RequestMapping("/temp")
    public String showTempPage() {
        return "temp";
    }
}
