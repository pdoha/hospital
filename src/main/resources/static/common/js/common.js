var commonLib = commonLib || {};

/**
* ajax 요청,응답 편의 함수

method : 요청 방식 - GET, POST, PUT, PATCH, DELETE ...
url : 요청 URL
params : 요청 데이터(POST, PUT, PATCH ... )
responseType :  json : javascript 객체로 변환
*/
commonLib.ajaxLoad = function(method, url, params, responseType) {
    //메서드 없으면 get 기본 메서드,
    method = method || "GET";

    //요청데이터 params이 없을때 null값
    params = params || null;

    const token = document.querySelector("meta[name='_csrf']").content;
    const tokenHeader = document.querySelector("meta[name='_csrf_header']").content;
    //요청 promise 비동기 순차 실행 함수
    //resolve : 성공시 데이터 넘김
    //reject : 실패시 데이터 넘김
    return new Promise((resolve, reject) => {
        const xhr = new XMLHttpRequest();

        xhr.open(method, url);
        xhr.setRequestHeader(tokenHeader, token);
        xhr.send(params); // 요청 body에 실릴 데이터 키=값&키=값& .... FormData 객체 (POST, PATCH, PUT)

        //상태가 바뀔때마다 호출
        xhr.onreadystatechange = function() {
            //요청 성공시 && 상태가 응답이 완료된 상태일때
            if (xhr.status == 200 && xhr.readyState == XMLHttpRequest.DONE) {
                //타입이 있고 && 타입이 json과 같을때 변환 JSON.parse
                const resData = (responseType && responseType.toLowerCase() == 'json') ?
                JSON.parse(xhr.responseText) : xhr.responseText;

                resolve(resData); // 성공시 응답 데이터
            }
        };

        xhr.onabort = function(err) {
            reject(err); // 중단 시
        };

        xhr.onerror = function(err) {
            reject(err); // 요청 또는 응답시 오류 발생
        };
    });
};

/**
* 위지윅 에디터 로드

*
*/ //특정아이디를 가지고 에디터 생성
commonLib.loadEditor = function(id, height) { //매개변수로 id, 높이
    if (!id) { //아이디가 없을때는
        return; //실행하지 않음
    }
    //height가 없을 때는 기본값 450
    height = height || 450;

    // ClassicEditor객체가 만들어져서 에디터를 만들수있음

    return ClassicEditor.create(document.getElementById(id), {
    //반환값 요소선택(id),ClassicEditor의 2번째 매개변수가 에디터의 설정값, 많지만 height만 넣어줬음
        height
    });
}

//이메일 인증 메일보내기 및 검증 함수 추가
//var commonLib = commonLib || {};

/**
* 이메일 인증 메일 보내기
*
* @param email : 인증할 이메일
*/
commonLib.sendEmailVerify = function(email) {
    const { ajaxLoad } = commonLib;

    const url = `/api/email/verify?email=${email}`;

    ajaxLoad("GET", url, null, "json")
        .then(data => {
            if (typeof callbackEmailVerify == 'function') { // 이메일 승인 코드 메일 전송 완료 후 처리 콜백
                callbackEmailVerify(data);
            }
        })
        .catch(err => console.error(err));
};

/**
* 인증 메일 코드 검증 처리
*
*/
commonLib.sendEmailVerifyCheck = function(authNum) {
    const { ajaxLoad } = commonLib;
    const url = `/api/email/auth_check?authNum=${authNum}`;

    ajaxLoad("GET", url, null, "json")
        .then(data => {
            if (typeof callbackEmailVerifyCheck == 'function') { // 인증 메일 코드 검증 요청 완료 후 처리 콜백
                callbackEmailVerifyCheck(data);
            }
        })
        .catch(err => console.error(err));
};