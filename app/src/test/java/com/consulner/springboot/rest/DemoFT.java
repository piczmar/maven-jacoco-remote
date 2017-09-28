package com.consulner.springboot.rest;

import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class DemoFT {

  private static final String port = "8080";
  private static  final String appContext = "/RestDemo-0.0.1-SNAPSHOT";

  RestTemplate restTemplate = new RestTemplate();

  HttpHeaders headers = new HttpHeaders();

  @Test
  public void testRetrieveStudentCourse() throws JSONException {

//    HttpEntity<String> entity = new HttpEntity<String>(null, headers);
//
//    ResponseEntity<String> response = restTemplate.exchange(
//        createURLWithPort("/demo"),
//        HttpMethod.GET, entity, String.class);
//
//    String expected = "{\"message\":\"Hello there!!\"}";
//    String resp = response.getBody();
//    System.out.println(resp);
//    JSONAssert.assertEquals(expected,resp , false);
  }

  private String createURLWithPort(String uri) {
    return "http://172.17.0.1:" + port + appContext +  uri;
  }
}

