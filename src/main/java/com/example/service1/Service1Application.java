package com.example.service1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;


import com.example.service1.ServiceBase;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Service1Application {

    private static ApplicationInfoManager applicationInfoManager; //com.netflix.appinfo
    private static EurekaClient eurekaClient;//com.netflix.discovery.EurekaClient

    public static synchronized ApplicationInfoManager initializeApplicationInfoManager(EurekaInstanceConfig instanceConfig) {
        if (applicationInfoManager == null) {
            //applicationInfoManager가 없으면 EurekaConfigBasedInstanceInfoProvider 객체를 생성해 instanceInfo를 만들고
            //ApplicationInfoManager객체 생성후 instanceConfig, instanceInfo 값을 넣어 applicationInfoManager에 저장 후   applicationInfoManager리턴
            InstanceInfo instanceInfo = new EurekaConfigBasedInstanceInfoProvider(instanceConfig).get();
            applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        }
        return applicationInfoManager;
    }


    //eureka Client 식별 해주는 함수 .
    private static synchronized EurekaClient initializeEurekaClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig clientConfig) {
        if (eurekaClient == null) {
            //eurekaClient 가 없으면 DiscoveryClient객체를 새로 생성해줘서 넣어주고 리턴
            //위의 initializeApplicationInfoManager에서 생성한   applicationInfoManager 값과 com.netflix.discovery
            // 의 clientConfig값을 넣어   DiscoveryClient 객체 생성
            eurekaClient = new DiscoveryClient(applicationInfoManager, clientConfig);
        }

        return eurekaClient;
    }



    public static void main(String[] args) {

        HashMap map  = new HashMap();
        map.put("server.port",8101);
        //map.put("server.address", "192.168.1.155");
       // map.put("server.servlet.context-path", "/service");

        SpringApplication sa = new SpringApplication(Service1Application.class);
        sa.setDefaultProperties(map);
        sa.run(args);


        DynamicPropertyFactory configInstance = com.netflix.config.DynamicPropertyFactory.getInstance();
        ApplicationInfoManager applicationInfoManager = initializeApplicationInfoManager(new MyDataCenterInstanceConfig());
        EurekaClient eurekaClient = initializeEurekaClient(applicationInfoManager, new DefaultEurekaClientConfig());

        ServiceBase serviceBase = new ServiceBase(applicationInfoManager, eurekaClient, configInstance);
        try {
            serviceBase.start();
        } finally {
            // the stop calls shutdown on eurekaClient
            //serviceBase.stop();
        }
    }

}
