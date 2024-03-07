package com.hospital.admin.config.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hospital.admin.config.entities.Configs;
import com.hospital.admin.config.repositories.ConfigsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfigInfoService {
    private final ConfigsRepository repository;

    /*조회시에 내가 원했던 클래스 형태로 바꿔야함 */

    //단일타입일때
    public <T> Optional<T> get(String code, Class<T> clazz){
        return get(code, clazz, null);
    }

    //지네릭을 쓰는 복합적타입인 경우
    public <T> Optional<T> get(String code, TypeReference<T> typeReference){
        //클래스클래스가 널값이면 타입레퍼런스로 처리
        return get(code, null, typeReference);

    }

    //TypeReference : 복합적인 형태 list , map
    public <T> Optional<T> get(String code, Class<T> clazz, TypeReference typeReference){
        //단일형 자료형
        //레포지티에서 code 찾아서 있으면 변환하고 없으면 따로 처리 안하고 종료
        Configs config = repository.findById(code).orElse(null);
        //config가 아예 없거나 데이터가 조회는 됐지만 널값이나 비어있으면 처리하지않고 종료
        if(config == null || !StringUtils.hasText(config.getData())){
            return Optional.ofNullable(null);
        }
        //변환시 자바타임패키지도 같이 추가 (나중에 쓸수도있으니까)
        ObjectMapper om = new ObjectMapper(); //데이터 치환
        om.registerModule(new JavaTimeModule());

        //json 형식으로 저장된 문자열
        //원래 형태로 바꾸자 ( 원래   T반환값 데이터 T data)
        String jsonString = config.getData();
        try {
            T data = null;
            if(clazz == null) {
                //TypeReference 처리
                data = om.readValue(jsonString, new TypeReference<T>() {});

            } else {
                //Class로 처리
                data = om.readValue(jsonString, clazz); //readValue 원래 자료형으로 돌려줌

            }
            return Optional.ofNullable(data);

        } catch(JsonProcessingException e){
            e.printStackTrace();
            return Optional.ofNullable(null);
        }
    }
}
