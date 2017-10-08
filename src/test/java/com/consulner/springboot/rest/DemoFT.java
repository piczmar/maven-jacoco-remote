package com.consulner.springboot.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class DemoFT {

  private static final String port = "8080";
  private static final String appContext = "/RestDemo-0.0.1-SNAPSHOT";

  private RestTemplate restTemplate = new RestTemplate();

  private HttpHeaders headers = new HttpHeaders();

  private Properties prop = new Properties();

  @Before
  public void setup() throws IOException {
    InputStream stream = getClass().getResourceAsStream("/test.properties");
    prop.load(stream);
  }


  @Test
  public void testRetrieveStudentCourse() throws JSONException {

    HttpEntity<String> entity = new HttpEntity<String>(null, headers);

    ResponseEntity<String> response = restTemplate.exchange(
        createURLWithPort("/demo"),
        HttpMethod.GET, entity, String.class);

    String expected = "{\"message\":\"Hello there!!\"}";
    String resp = response.getBody();
    System.out.println(resp);
    JSONAssert.assertEquals(expected, resp, false);
  }

  private String createURLWithPort(String uri) {
    System.out.println(prop.stringPropertyNames());
    System.out.println(prop.getProperty("tomcat.host"));
    return String.format("http://%s:%s/%s/%s",
            prop.getProperty("tomcat.host"),
            port,
            appContext,
            uri);
  }
}

