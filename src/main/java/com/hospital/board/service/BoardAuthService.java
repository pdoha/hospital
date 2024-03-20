package com.hospital.board.service;

import com.hospital.board.entities.Board;
import com.hospital.board.service.config.BoardConfigInfoService;
import com.hospital.commons.exceptions.UnAuthorizedException;
import com.hospital.member.Authority;
import com.hospital.member.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardAuthService {
    private final BoardConfigInfoService configInfoService;



    private final PasswordEncoder encoder;


    private final MemberUtil memberUtil;

    //글쓰기, 글보기, 글목록 접근 권한 체크
    public void accessCheck(String mode, String bid){
        Board board = configInfoService.get(bid);
        accessCheck(mode, board);
    }

    public void accessCheck(String mode, Board board){
        if(memberUtil.isAdmin()){ //관리자는 체크 필요없음
            return;
        }
        if(!board.isActive()) { //미노출 게시판
            throw new UnAuthorizedException();
        }

        boolean accessible = false;
        Authority target = Authority.ALL;

        //모드값에 따른 타입
        if(mode.equals("write") || mode.equals("update")) { //글쓰기, 글수정 페이지일떄
            target = board.getWriteAccessType();
        } else if(mode.equals("view")) { //글보기 페이지
            target = board.getViewAccessType();
        } else if(mode.equals("list")) { //글목록 페이지
            target = board.getListAccessType();
        }

        //접근 제한
        if(target == Authority.ALL){ //전체 접근 가능
            accessible = true;
        }
        if(target == Authority.USER && memberUtil.isLogin()){ //회원 + 관리자
            accessible = true;
        }
        if(target == Authority.ADMIN && memberUtil.isAdmin()) { //관리자
            accessible = true;
        }

        if(!accessible){ //접근 불가 페이지
            throw new UnAuthorizedException();
        }
    }


}
