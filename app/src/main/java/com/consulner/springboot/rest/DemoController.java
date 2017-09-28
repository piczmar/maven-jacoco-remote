package com.consulner.springboot.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/demo")
public class DemoController {

  @RequestMapping(method= RequestMethod.GET)
  public @ResponseBody DemoDTO demo(){
    return new DemoDTO("Hello there!!");
  }

}
