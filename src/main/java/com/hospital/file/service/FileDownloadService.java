package com.hospital.file.service;

import com.hospital.file.entities.FileInfo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@RequiredArgsConstructor
public class FileDownloadService {
    private final FileInfoService infoService;
    private final HttpServletResponse response;

    public void download(Long seq){
        //파일 번호로 파일정보 조회
        FileInfo data = infoService.get(seq);
        String filePath = data.getFilePath();


        //원래 파일 이름과 파일 실제 서버 경로를 불러와서 실제 서버쪽에서 출력
        String fileName = data.getFileName();


        //파일명 2바이트 인코딩으로 변경 (윈도우 한글깨짐 방지)
        try {
            fileName = new String(data.getFileName().getBytes(), "ISO8859_1");
        } catch (UnsupportedEncodingException e) {

        }

        //파일데이터를 파일경로 (filePath)를 이용해서 읽어오기
        File file = new File(filePath);
        try(FileInputStream fis = new FileInputStream(file);
            //버퍼에 담자
            BufferedInputStream bis = new BufferedInputStream(fis)){
            //응답 body에 출력
            OutputStream out = response.getOutputStream();

            //헤더에 정의
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setHeader("Content-Type", "application/octet-stream"); //범용적으로 쓰기위해 바이널코드넣어줌
            response.setIntHeader("Expires", 0); //만료시간 x
            response.setHeader("Cache-Control", "must-revalidate"); //캐싱해도 기존데이터 갱신
            response.setHeader("Pragma", "public"); //옛 브라우저용 캐싱 설정
            response.setHeader("Content-Length", String.valueOf(file.length())); //파일용량
            //바디에 출력
            while(bis.available() > 0){ //버퍼에 파일정보가 있으면
                out.write(bis.read()); //바디에 출력
            }
            out.flush();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
