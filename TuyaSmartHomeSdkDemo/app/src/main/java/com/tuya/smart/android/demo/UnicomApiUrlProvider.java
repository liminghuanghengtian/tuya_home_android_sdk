package com.tuya.smart.android.demo;

import android.content.Context;

import com.tuya.smart.android.base.provider.ApiUrlProvider;
import com.tuya.smart.android.user.bean.Domain;

/**
 * @author xushun
 * @Des:
 * @data 2019/5/14.
 */
public class UnicomApiUrlProvider extends ApiUrlProvider {
    public UnicomApiUrlProvider(Context context) {
        super(context);

        //联通云域名
        Domain domain = new Domain();
        domain.setMobileApiUrl("https://a1-prod.smartont.net/api.json");
        domain.setLogUrl("https://a1-prod.smartont.net/log.json");
        domain.setMobileMqttUrl("m1-prod.smartont.net");
        domain.setGwApiUrl("http://a.gw-prod.smartont.net/gw.json");
        domain.setGwMqttUrl("m2-prod.smartont.net");
        domain.setMobileMediaMqttUrl("s-prod.smartont.com");
        setDefaultDomain(domain);

        //联通云 region
        setRegion("A1");
    }
    @Override
    public String getApiUrlByCountryCode(String countryCode){
        return getApiUrl();
    }
}
