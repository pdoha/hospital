package com.hospital.commons;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class Pagination {
    private int page; //현재 페이지
    private int total; //전체 레코드 개수
    private int ranges; //페이지 구간 개수
    private int limit; //1페이지 당 레코드 개수

    private int firstRangePage; //구간별 첫 페이지
    private int lastRangePage; //구간별 마지막 페이지

    private int prevRangePage; //이전 구간 첫 페이지 번호
    private int nextRangePage; //다음 구간 첫 페이지 번호

    private int totalPages; //전체 페이지 개수
    private String baseURL; //페이징 쿼리스트링 기본 URL

    public Pagination(int page, int total, int ranges, int limit, HttpServletRequest request) {
        //음수가 들어오면 0이하이면 1값으로 대체 ( Utils 편의기능)
        page = Utils.onlyPositiveNumber(page, 1);
        total = Utils.onlyPositiveNumber(total,0);
        ranges = Utils.onlyPositiveNumber(ranges,10);
        limit = Utils.onlyPositiveNumber(limit,20);

        //전체 페이지 개수
        int totalPages = (int)Math.ceil(total / (double)limit);

        //구간 번호
        int rangeCnt = (page - 1) / ranges;
        int firstRangePage = rangeCnt * ranges + 1;
        int lastRangePage = firstRangePage + ranges - 1;
        lastRangePage = lastRangePage > totalPages ? totalPages : lastRangePage;

        //이전 구간 첫 페이지
        if( rangeCnt > 0){
            prevRangePage = firstRangePage - ranges;
        }

        //다음 구간 첫 페이지
        //마지막 구간 번호
        int lastRangeCnt = (totalPages - 1 ) / ranges;
        if(rangeCnt < lastRangeCnt) { //마지막 구간이 아닌 경우 다음 구간 첫 페이지 계산
            nextRangePage = firstRangePage + ranges;
        }

        //쿼리스트링 값 유지 처리
        //쿼리스트링 값중에 page만 제외하고 다시 조합
        String baseURL = "?";
        if(request != null){
            //가져와서
            String queryString = request.getQueryString(); //가져와서 새로만든다

            if(StringUtils.hasText(queryString)){
                //제거
                queryString = queryString.replace("?", ""); //앞에 ? 제거

                baseURL += Arrays.stream(queryString.split("&")) //&로 쪼개서
                        .filter(s -> !s.contains("page=")) //page=가 포함되어있는 데이터는 제거하고
                        .collect(Collectors.joining("&")); //다시 &로 묶어준다
                baseURL = baseURL.length() > 1 ? baseURL += "&" : baseURL;

            }
            this.baseURL = baseURL;
        }

        this.page = page;
        this.total = total;
        this.ranges = ranges;
        this.limit = limit;
        this.firstRangePage = firstRangePage;
        this.lastRangePage = lastRangePage;
        this.totalPages = totalPages;
        this.baseURL = baseURL;
    }

    //요청데이터를 쓰지 않는 경우 생성자 추가
    public Pagination(int page, int total, int ranges, int limit){
        this(page, total, ranges, limit, null);
    }

    //첫번째구간부터 마지막 구간까지 반복하면서 가공해서 스트림 배열로 만들어서 리스트형태로 반환
    public List<String[]> getPages(){
        //0 : 페이지 번호 - 정수니까 문자로 바꿔야지 String.valueOf(p)
        // 1: 페이지 URL - 링크 (주소) - "?page=" + p
        return IntStream.rangeClosed(firstRangePage, lastRangePage)
                .mapToObj(p -> new String[] {String.valueOf(p), baseURL + "page=" + p}) //스트림배열로 변환 mapToObj
                .toList();
    }
}
