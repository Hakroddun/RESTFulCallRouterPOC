package com.hakroddun.RESTFulCallRouter;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class CoreController
{
    @PostMapping(path = "/routeMessage")
    public ResponseEntity<String> saveUserDetails(@RequestBody RouterRequest routerRequest)
    {
        try
        {
            return new RestTemplate().exchange(routerRequest.url, getHttpMethod(routerRequest), getHttpEntity(routerRequest, getHttpHeaders(routerRequest)), String.class);
        }
        catch (HttpClientErrorException | HttpServerErrorException e)
        {
            System.err.println(e.getMessage());
            return new ResponseEntity<>("", e.getStatusCode());
        }
    }

    private HttpHeaders getHttpHeaders(@RequestBody RouterRequest routerRequest)
    {
        HttpHeaders headers = new HttpHeaders();
        if(routerRequest.credentials != null)
        {
            headers.add("Authorization", "Basic " + getAuth(routerRequest.credentials));
        }
        return headers;
    }

    private HttpMethod getHttpMethod(@RequestBody RouterRequest routerRequest)
    {
        HttpMethod methodType;
        if(routerRequest.callType.toLowerCase().equals("post"))
        {
            methodType = HttpMethod.POST;
        }
        else
        {
            methodType = HttpMethod.GET;
        }
        return methodType;
    }

    private HttpEntity<String> getHttpEntity(@RequestBody RouterRequest routerRequest, HttpHeaders headers)
    {
        HttpEntity<String> httpEntity;
        if(routerRequest.callType.toLowerCase().equals("post"))
        {
            httpEntity = new HttpEntity<>(routerRequest.body, headers);
        }
        else
        {
            httpEntity = new HttpEntity<>(headers);
        }
        return httpEntity;
    }

    public String getAuth(String creds)
    {
        String plainCreds = creds;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        return base64Creds;
    }


}
