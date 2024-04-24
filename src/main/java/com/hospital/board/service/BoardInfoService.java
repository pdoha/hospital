package com.hospital.board.service;

import com.hospital.board.controllers.BoardDataSearch;
import com.hospital.board.controllers.RequestBoard;
import com.hospital.board.entities.Board;
import com.hospital.board.entities.BoardData;
import com.hospital.board.entities.QBoardData;
import com.hospital.board.repositories.BoardDataRepository;
import com.hospital.board.service.config.BoardConfigInfoService;
import com.hospital.commons.ListData;
import com.hospital.commons.Pagination;
import com.hospital.commons.Utils;
import com.hospital.file.entities.FileInfo;
import com.hospital.file.service.FileInfoService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardInfoService {
    private final BoardDataRepository boardDataRepository;
    private final EntityManager em;
    private final FileInfoService fileInfoService; //파일정보
    private final BoardConfigInfoService configInfoService; //게시판 설정
    private final HttpServletRequest request;
    private final Utils utils; //장비구분

    //게시글 개별 조회
    public BoardData get(Long seq){
        BoardData boardData = boardDataRepository.findById(seq).orElseThrow
                (BoardDataNotFoundException::new);
        //추가 정보는 가져와서 넣어준다 2차가공
        //에디터이미지, 첨부파일
        addBoardData(boardData);
        return boardData;
    }

    // RequestBoard -> BoardData 로 변환 작업
    //파라미터는 게시글 번호(Long) 또는 게시글 데이터 (BoardData) 가 될 수 있음 -> object 사용
    public RequestBoard getForm(Object data){
        BoardData boardData = null;
        if(data instanceof BoardData){
            boardData = (BoardData) data;
        } else { //게시글 번호인 경우
            Long seq = (Long) data;
            boardData = get(seq);
        }

        RequestBoard form = new ModelMapper().map(boardData, RequestBoard.class);
        form.setMode("update"); //양식을 변경하는 경우는 게시글 수정쪽에서만 !
        form.setBid(boardData.getBoard().getBid());

        return form;
    }

    //게시판 목록 조회
    //bid :  게시판 ID
    public ListData<BoardData> getList(String bid, BoardDataSearch search){
        Board board = configInfoService.get(bid); //게시판 설정 가져오기

        int page = Utils.onlyPositiveNumber(search.getPage(), 1);
        int limit = Utils.onlyPositiveNumber(search.getLimit(), board.getRowsPerPage());

        int offset = (page - 1) * limit; //레코드 시작 위치

        QBoardData boardData = QBoardData.boardData;
        BooleanBuilder andBuilder = new BooleanBuilder();

        //게시판 아이디는 필수로 고정
        andBuilder.and(boardData.board.bid.eq(bid));
        //검색조건 처리
        String sopt = search.getSopt(); //검색조건
        String skey = search.getSkey(); //검색키워드

        //검색 조건 값이 없으면 기본값 ALL
        sopt = StringUtils.hasText(sopt) ? sopt.toUpperCase() : "ALL";
        //키워드가 있을때만 처리
        if(StringUtils.hasText(skey)){
            skey = skey.trim();

            BooleanExpression subjectCond = boardData.subject.contains(skey); //제목 - subject LIKE '%skey%'
            BooleanExpression contentCond = boardData.content.contains(skey); //내용 - content LIKE '%skey%'

            if(sopt.equals("SUBJECT")){ //제목
                andBuilder.and(subjectCond);

            } else if(sopt.equals("CONTENT")){ //내용
                andBuilder.and(contentCond);

            } else if (sopt.equals("SUBJECT_CONTENT")){ //제목 + 내용
                BooleanBuilder orBuilder = new BooleanBuilder();
                orBuilder.or(subjectCond)
                        .or(contentCond);
                andBuilder.and(orBuilder);

            } else if (sopt.equals("POSTER")){ //작성자 + 아이디 + 회원명
                BooleanBuilder orBuilder = new BooleanBuilder();
                orBuilder.or(boardData.poster.contains(skey))
                        .or(boardData.member.userId.contains(skey))
                        .or(boardData.member.name.contains(skey));
                andBuilder.and(orBuilder);
            }
        }

        //특정 사용자로 게시글 한정 : 마이페이지에서 활용 가능
        String userId = search.getUserId();
        if(StringUtils.hasText(userId)){
            andBuilder.and(boardData.member.userId.eq(userId));
        }

        //게시글 분류로 조회
        String category = search.getCategory();
        if(StringUtils.hasText(category)){
            category = category.trim(); //양옆 좌우 여백 제거
            andBuilder.and(boardData.category.eq(category));
        }

        PathBuilder<BoardData> pathBuilder = new PathBuilder<>(BoardData.class, "boardData");

        List<BoardData> items = new JPAQueryFactory(em)
                .selectFrom(boardData)
                .leftJoin(boardData.member) //회원데이터 같이 가져오고
                .fetchJoin()
                .offset(offset) //시작번호 - 직접구한다
                .limit(limit)
                //검색조건
                .where(andBuilder)
                //정렬조건
                .orderBy(new OrderSpecifier(Order.DESC, pathBuilder.get("notice")),
                        new OrderSpecifier(Order.DESC, pathBuilder.get("createdAt"))
                        )
                .fetch();

        //페이징 추가
        //- 게시글 전체 개수
        long total = boardDataRepository.count(andBuilder);
        //- 하단쪽 페이지 개수 ( 장비에 따라 다름)
        int ranges = utils.isMobile() ? board.getPageCountMobile() : board.getPageCountPc();

        Pagination pagination = new Pagination(page, (int)total, ranges, limit, request);

        return new ListData<>(items, pagination);

    }

    //게시글 추가 정보처리
    public void addBoardData(BoardData boardData){
        String gid = boardData.getGid();

        //완료된 파일정보 가져오기
        List<FileInfo> editorFiles = fileInfoService.getListDone(gid, "editor"); //에디터 이미지파일
        List<FileInfo> attachFiles = fileInfoService.getListDone(gid, "attach"); //첨부파일
        //추가
        boardData.setEditorFiles(editorFiles);
        boardData.setAttachFiles(attachFiles);
    }
}
