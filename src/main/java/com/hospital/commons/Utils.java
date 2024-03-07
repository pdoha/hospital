package com.hospital.commons;

import com.hospital.admin.config.controllers.BasicConfig;
import com.hospital.file.service.FileInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Component
@RequiredArgsConstructor
public class Utils {

    private final HttpServletRequest request;
    private final HttpSession session;
    private final FileInfoService fileInfoService;

    //메세지 번들
    //객체를 만들지 않아도 클래스가 로드될때 실행되게 하기 위해서 static 사용
    private static final ResourceBundle commonsBundle;
    private static final ResourceBundle validationsBundle;
    private static final ResourceBundle errorsBundle;

    //메세지 코드에 대한 번들 가져오기
    //상황에 따라 맞는 다른 메세지를 가져올 수 있다
    static{
        commonsBundle = ResourceBundle.getBundle("messages.commons");
        validationsBundle = ResourceBundle.getBundle("messages.validations");
        errorsBundle = ResourceBundle.getBundle("messages.errors");
    }

    public boolean isMobile(){
        //모바일 수동 전환 모드 체크
        String device = (String) session.getAttribute("device");
        if(StringUtils.hasText(device)){
            return device.equals("MOBILE");
        }
        //요청헤더 : User-Agent 패턴을 가지고 모바일인지 아닌지 체크
        //getHeader를 통해가져온다
        String ua = request.getHeader("User-Agent");

        //패턴
        String pattern = ".*(iPhone|iPod|iPad|BlackBerry|Android|Windows CE|LG|MOT|SAMSUNG|SonyEricsson).*";

        //위 패턴이있으면 모바일로 인지
        return ua.matches(pattern);



    }

    public String tpl(String path){
        String prefix = isMobile() ? "mobile/" : "front/";

        return prefix + path;
    }

    //메세지 번들 null값에 대한 검증
    //있으면 타입을 그대로 넣고 없으면  validations;
    public static String getMessage(String code, String type){
        type = StringUtils.hasText(type) ? type : "validations";

        ResourceBundle bundle = null;
        if( type.equals("commons")){
            bundle = commonsBundle;
        } else if(type.equals("errors")){
            bundle = errorsBundle;
        } else{
            bundle = validationsBundle;
        }

        //키워드( 키값) 은 코드로 조회
        return bundle.getString(code);
    }

    //유효성 검사에 있는거 가져오기
    public static String getMessage(String code){
        return getMessage(code, null);
    }

    //줄개행 문자가 있으면 br태그로 바꿔주는 편의기능
    // -> \n 또는 \r\n -> <br>
    public String nl2br(String str){
        str = str.replaceAll("\\n", "<br>")
                .replaceAll("\\r", "");
        return str;

    }

    //썸네일 이미지 사이즈 설정
    public List<int[]> getThumbSize(){
        BasicConfig config = (BasicConfig) request.getAttribute("siteConfig"); //설정가져와서
        String thumbSzie = config.getThumbSize(); //썸네일 사이즈 설정만 가져와서
        String[] thumbSize = thumbSzie.split("\\n"); //줄바꿈 제거

        List<int[]> data = Arrays.stream(thumbSize).map(this::toConvert).toList();

        return data;
    }

    // \r 제거하고 대문자 x 자르고, 인트배열로 반환하는 편의 메서드
    public int[] toConvert(String size){
        //앞뒤에 공백이 있을경우 제거 ( 공백있으면 반환안됨)
        size = size.trim();
        try { //만약에 값이 있다고하더라도 알파벳같은 숫자로 안바뀌는 문자열일때 null 발생가능성이 있으므로 try, catch
            int[] data = Arrays.stream(size.replaceAll("\\r", "") //커서 다음행 제거
                            .toUpperCase().split("X")) //x를 대문자로 바꿔서 잘라주고
                            .filter(s -> !s.isBlank()) //공백일때는 건너뛰고 -> 처리하지않고
                            //공백아니면 변환메서드 이용해서 int배열로 반환
                            .mapToInt(Integer::parseInt).toArray(); //문자 -> 정수로 바꿔서 배열로 변환

            return data;
        } catch (Exception e) {
            return null;
        }
    }

    //썸네일 -> 이미지태그로 바로 출력해주는 기능

    //className이 포함된 경우
    public String printThumb(long seq, int width, int height, String className){
        String[] data = fileInfoService.getThumb(seq, width, height);
        if(data != null){//데이터가 있으면 이미지태그 출력
            String cls = StringUtils.hasText(className) ? " class='" + className + "'" : "";
            String image = String.format("<img src='%s'%s>", data[1], cls);
            return image;
        }


        return "";

    }

    //className 없는경우
    public String printThumb(long seq, int width, int height){
        return printThumb(seq, width, height, null);

    }



}
