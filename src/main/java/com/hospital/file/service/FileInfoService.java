package com.hospital.file.service;

import com.hospital.configs.FileProperties;
import com.hospital.file.FileInfoRepository.FileInfoRepository;
import com.hospital.file.entities.FileInfo;
import com.hospital.file.entities.QFileInfo;
import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import static org.springframework.data.domain.Sort.Order.asc;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class FileInfoService {
    private final FileProperties fileProperties;
    private final FileInfoRepository repository;
    private final HttpServletRequest request;


    //단일 조회 - 파일번호 seq
    public FileInfo get(Long seq){
        FileInfo fileInfo = repository.findById(seq)
                .orElseThrow(FileNotFoundException::new);

        //파일 추가 정보
        addFileInfo(fileInfo);

        return fileInfo;
    }

    //파일목록 조회
    // mode - ALL : 기본값 - 완료 + 미완료 모두 조회
    //     - DONE : 완료된 파일
    //      - UNDONE : 미완료된 파일
    public List<FileInfo> getList(String gid, String location, String mode){
        QFileInfo fileInfo = QFileInfo.fileInfo; //조건식 비교하기 위해 q형태 객체 가져온다
        //있으면 모드값 없으면 기본값 ALL
        mode = StringUtils.hasText(mode) ? mode : "ALL";

        //여러 조건식 사용 BooleanBuilder 사용
        BooleanBuilder builder = new BooleanBuilder();
        //gid는 필수이므로 무조건 넣어준다
        builder.and(fileInfo.gid.eq(gid));

        //location은 값이 있을때만 조건식을 선별적으로 넣는다
        if(StringUtils.hasText(location)){
            builder.and(fileInfo.location.eq(location));
        }

        //mode값에 따라 완료 , 미완료 2가지 형태 파일


        // UNDONE 일때 false
        if(!mode.equals("ALL")){ //ALL이 아닐때 조건식
            builder.and(fileInfo.done.eq(mode.equals("DONE")));  // DONE 일때 true
        }

        List<FileInfo> items = (List<FileInfo>) repository.findAll(builder, Sort.by(asc("createAt")));
        items.forEach(this::addFileInfo);
        return items;
    }

    //getList 전체파일 조회
    public List<FileInfo> getList(String gid){
        return getList(gid, null, "ALL"); //전체 조회 (완료 + 미완료 파일)

    }

    public List<FileInfo> getList(String gid, String location){
        return getList(gid, location, "ALL"); //location으로 같이 조회했을때 전체 조회
    }

    //완료된 파일만 조회
    public List<FileInfo> getListDone(String gid){
        return getList(gid, null, "DONE"); //완료된 파일만 조회
    }

    public List<FileInfo> getListDone(String gid, String location){
        return getList(gid, location, "DONE"); //완료된 파일만 조회
    }

    //파일 추가 정보 처리
    // - 파일 서버 경로 (filePath)
    // - 파일 URL (fileUrl)

    public void addFileInfo(FileInfo fileInfo) {
        long seq = fileInfo.getSeq();
        long dir = seq % 10L;
        String fileName = seq + fileInfo.getExtension();

        //파일경로 , URL
        //서버에 올라가는 실제 파일 경로 filePath
        //브라우저창에서 접근할 수 있는 url를 가공해서 넣음
        String filePath = fileProperties.getPath() + dir + "/" + fileName;
        String fileUrl = request.getContextPath() + fileProperties.getUrl() + dir + "/" + fileName;

        fileInfo.setFilePath(filePath);
        fileInfo.setFileUrl(fileUrl);

        //썸네일 경로 URL (해당파일의 썸네일 디렉토리)

        //가져온 경로를 list에 담자
        List<String> thumbsPath = new ArrayList<>();
        List<String> thumbsUrl = new ArrayList<>();

        //공통경로 맨아래 메서드를 만들었음

        String thumbDir = getThumbDir(seq);
        String thumbUrl = getThumbUrl(seq);

        //썸네일 경로를 가지고 조회 ( list ( ):경로에 있는 모든 파일을 가져온다)
        File _thumDir = new File(thumbDir);

        //디렉토리가 있을때만 파일을 가져온다
        if (_thumDir.exists()) { //경로가 존재하면
            //파일 목록을 전부 가져오기
            for (String thumbFileName : _thumDir.list()) { // list ( ):경로에 있는 모든 파일을 가져온다
                thumbsPath.add(thumbDir + "/" + thumbFileName);
                thumbsUrl.add(thumbUrl + "/" + thumbFileName);
            }

            //썸네일 만들기
            fileInfo.setThumbsPath(thumbsPath);
            fileInfo.setThumbsUrl(thumbsUrl);


        }
    }
        //파일별 특정 사이즈 썸네일 조회

    public String[] getThumb(long seq, int width, int height){
            //파일정보
        FileInfo fileInfo = get(seq);
        String fileType = fileInfo.getFileType(); //파이형식이 이미지인지 체크
        if(fileType.indexOf("image/") == -1){
            return null; //이미지가 아니면 건너뛴다
        }

            //파일이름 가져오기
        String fileName = seq + fileInfo.getExtension();

        String thumbDir = getThumbDir(seq);
        File _thumbDir = new File(thumbDir); //없을 경우 새로 만든다
        if(!_thumbDir.exists()){
            _thumbDir.mkdirs();

        }
        String thumbPath = String.format("%s/%d_%d_%s", thumbDir, width, height, fileName);
        File _thumbPath = new File(thumbPath);
        if(!_thumbPath.exists()) { //썸네일 이미지가 없는경우 만든다
            try {
                Thumbnails.of(new File(fileInfo.getFilePath()))
                        .size(width, height)
                        .toFile(_thumbPath);
            } catch (IOException e){
                e.printStackTrace();
            }
        }

            //url 만들기
        String thumbUrl = String.format("%s/%d_%d_%s", fileInfo.getThumbsUrl());
                    //경로, url
        return new String[] { thumbPath, thumbUrl};

    }

    public String getThumbDir(long seq){
        String thumbDirCommon = "thumbs/" + (seq % 10) + "/" + seq; //공통경로 따로뺌
        return fileProperties.getPath() + thumbDirCommon;

    }

    public String getThumbUrl(long seq){
        String thumbDirCommon = "thumbs/" + (seq % 10) + "/" + seq; //공통경로 따로뺌
        return fileProperties.getUrl() + thumbDirCommon;
    }
}
