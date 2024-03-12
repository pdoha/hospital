var commonLib = commonLib || {};
//파일 업로드
commonLib.fileManager = {
    //파일 업로드 처리
    upload(files){
        try{
            if(!files || files.length == 0){
                    throw new Error("업로드할 파일을 선택하세요.");
            }

            //서버에 올리기전에 필요한 파일정보 가져오기
            const gidEl = document.querySelector("[name='gid']");
            //요소가 없거나, 값이없는경우
            if(!gidEl || !gidEl.value.trim()) {
                throw new Error("gid가 누락되었습니다.");

            //1. 양식 만들기!!
            const gid = gidEl.value.trim();
            //생성자 객체 생성 (메모리상의 양식데이터를 만들기위한)
            const formData = new FormData(); //기본 Content-Type: multipart/form-data
            formData.append("gid", gid); //항목 추가 (key & value)

            //파일 복수개 => 반복
            for(const file of files){
                formData.append("file", file);
            }

            //ajaxLoad 함수 (속성명)만 분리 - 비구조할당
            const {ajaxLoad} = commonLib;
            //post 방식 / 요청 주소 / 요청데이터 formData / 응답은 JSON
            ajaxLoad("POST", "/api/file", formData, "json")
                .then(res => { //요청 성공시 promise방식은 then으로 받음

                    if(res && res.success){ //파일 업로드 성공시
                        if(typeof parent.callbackFileUpload == 'function'){
                            parent.callbackFileUpload(res.data); //파일 업로드후 페이지마다 후속처리를 다르게하기 위한 콜백함수
                        } else { //파일 업로드 실패시 메세지
                            if(res) alert(res.message);
                        }
                    }

                })
                .catch(err => console.error(err)); //요청 실패시
 &

            //2. 서버에 전송!!

        } catch (err){
            alert(err.message);
            console.error(err);
        }
    }
};

//이벤트 처리
//DOM이 로딩된 후에 이벤트 생성 가능
 window.addEventListener("DOMContentLoaded", function(){
    //클래스명으로 선택
    const uploadFiles = document.getElementsByClassName("upload_files");

    //input 태그 만들기 (메모리상에 만드는거라서 노출 x)
    const fileEl = document.createElement("input");
    //타입을 file 로 바꾸기
    fileEl.type = "file";
    file.multiple = true; //여러개 파일을 선택 가능하게 설정

    //파일업로드 버튼 누르면 파일탐색기 열기
    for(const el of uploadFiles){
        el.addEventListener("click", function(){
            fileEl.click(); //파일 탐색기 생성

        });
    }

    //파일 선택시 이벤트 처리
    fileEl.addEventListener("change", function(e)){
        commonLib.fileManager.upload(e.target.files);

    });

 });


}