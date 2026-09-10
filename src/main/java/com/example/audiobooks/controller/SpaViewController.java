package com.example.audiobooks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaViewController {

    @GetMapping({
        "/",
        "/setup",
        "/library",
        "/login",
        "/register",
        "/whos-listening",
        "/recently-played",
        "/favorites",
        "/collections",
        "/collections/**",
        "/equalizer",
        "/settings",
        "/upload",
        "/now-playing",
        "/book/**",
        "/ebook/**"
    })
    public String forwardSpaRoutes() {
        return "forward:/index.html";
    }
}
