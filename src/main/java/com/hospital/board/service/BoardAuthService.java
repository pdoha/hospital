package com.hospital.board.service;

import com.hospital.board.service.config.BoardConfigInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardAuthService {
    private final BoardConfigInfoService configInfoService;

    //게시글 관련 권한 체크


}
