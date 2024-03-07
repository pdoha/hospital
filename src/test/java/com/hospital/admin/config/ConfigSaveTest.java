package com.hospital.admin.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hospital.admin.config.controllers.BasicConfig;
import com.hospital.admin.config.service.ConfigInfoService;
import com.hospital.admin.config.service.ConfigSaveService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;
import java.util.Optional;

@SpringBootTest
@Transactional
@TestPropertySource(properties = "spring.profiles.active=test")
public class ConfigSaveTest {
    //2가지 주입
    @Autowired
    private ConfigSaveService saveService;
    @Autowired
    private ConfigInfoService infoService;

    @Test
    @DisplayName("BasicConfig로 생성된 객체가 JSON으로 저장되는지 체크")
    void saveTest(){
        BasicConfig config = new BasicConfig();
        config.setSiteTitle("사이트 제목");
        config.setSiteDescription("사이트 설명");
        config.setSiteKeywords("사이트 키워드");
        config.setCssJsVersion(1);
        config.setJoinTerms("회원가입 약관");

        saveService.save("basic", config);

        //단일 타입일때
        BasicConfig config2 = infoService.get("basic", BasicConfig.class).get();
        System.out.println(config2);

        //복잡한 타입일때
        Optional<Map<String, String>> opt = infoService.get("basic", new TypeReference<>() {
        });
        System.out.println(opt.get());
    }
}
