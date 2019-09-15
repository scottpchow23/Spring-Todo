package com.chowscott.todo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class GreetingController {

  @GetMapping(value="/")
  public String getGreeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
      model.addAttribute("name", name);
      return "greeting";
  }
  
}