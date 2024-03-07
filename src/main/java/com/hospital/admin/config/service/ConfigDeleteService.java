package com.hospital.admin.config.service;

import com.hospital.admin.config.entities.Configs;
import com.hospital.admin.config.repositories.ConfigsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigDeleteService {
    private final ConfigsRepository repository;

    public void delete(String code){
        //코드 불러오고 없으면 null값
        Configs config = repository.findById(code).orElse(null);
        if(config == null){
            //null값이면 삭제할 필요없음 그냥 종료
            return;
        }

        repository.delete(config); //상태를 삭제로 바꾼다
        repository.flush();
    }
}
