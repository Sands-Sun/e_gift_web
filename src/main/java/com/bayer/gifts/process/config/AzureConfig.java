package com.bayer.gifts.process.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix ="aad")
public class AzureConfig {

	private String clientId;
    private String authority;
    private String redirectUriSignin;
    private String redirectUriGraph;
    private String secretKey;
    private String msGraphEndpointHost;
    private String createEventUrl;
    private String proxIp;
    private Integer proxPort;
    private Boolean openProxy;

    public String getAuthority(){
        if (!authority.endsWith("/")) {
            authority += "/";
        }
        return authority;
    }
}
