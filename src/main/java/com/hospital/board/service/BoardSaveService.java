package com.hospital.board.service;

import com.hospital.board.controllers.RequestBoard;
import com.hospital.board.entities.BoardData;
import com.hospital.board.repositories.BoardDataRepository;
import com.hospital.file.service.FileUploadService;
import com.hospital.member.MemberUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class BoardSaveService {
    private final BoardDataRepository boardDataRepository;
    private final FileUploadService fileUploadService;
    private final MemberUtil memberUtil; //로그인한 사용자 정보
    private final HttpServletRequest request;
    private final PasswordEncoder encoder; //비회원 비밀번호 해시화

    public BoardData save(RequestBoard form){
        String mode = form.getMode();
        mode = StringUtils.hasText(mode) ? mode : "write";

        Long seq = form.getSeq();
        BoardData data = null; //seq값이 없으면 안되니까

        if (seq != null && mode.equals("update")){ //글 수정
            //게시글 없을때는 수정하면 안되니까 예외 추가 BoardDataNotFoundException
            data = boardDataRepository.findById(seq).orElseThrow(BoardDataNotFoundException::new);

        } else{ //글작성시 새로운 엔티티를 만든다
            data = new BoardData();
            data.setGid(form.getGid());
            data.setIp(request.getRemoteAddr());
            data.setUa(request.getHeader("User-Agent"));
            data.setMember(memberUtil.getMember());
        }
        //수정 & 삭제시 필요한 데이터
        data.setPoster(form.getPoster());
        data.setSubject(form.getSubject());
        data.setContent(form.getContent());
        data.setCategory(form.getCategory());

        //추가필드 - 숫자
        data.setNum1(form.getNum1());
        data.setNum2(form.getNum2());
        data.setNum3(form.getNum3());

        //추가필드 - 한줄 텍스트
        data.setText1(form.getText1());
        data.setText2(form.getText2());
        data.setText3(form.getText3());

        //추가필드 - 여러줄 텍스트
        data.setLongText1(form.getLongText1());
        data.setLongText2(form.getLongText2());
        data.setLongText3(form.getLongText3());

        //비회원 비밀번호
        String guestPw = form.getGuestPw();
        if(StringUtils.hasText(guestPw)){ //값이 존재하면 해시화
            String hash = encoder.encode(guestPw);
            data.setGuestPw(hash);
        }

        //공지글 처리 - 관리자만 가능
        if(memberUtil.isLogin()){
            data.setNotice(form.isNotice());
        }

        //저장 처리
        boardDataRepository.saveAndFlush(data);

        //파일 업로드 완료 처리
        fileUploadService.processDone(data.getGid());

        return data;

    }
}
