//파일 업로드 후 후속 처리 함수
//@files : 업로드 한 파일 정보 목록
function callbackFileUpload(files){
    //파일이 업로드되면
    if(!files || files.length == 0){
        return;
    }

    //템플릿 치환
    //단일파일이니까 0번째 파일 하나만 가져온다
    const file = files[0];
    //템플릿데이터 가져오기 ( 내부 html 가져오기)
    let html = document.getElementById("image1_tpl").innerHTML;

    //thumbsUrl 있으면 마지막꺼 꺼내서 넣고 없으면 fileUrl 넣는다
    const imageUrl = file.thumbsUrl.length > 0 ? file.thumbsUrl.pop() : file.fileUrl;
    const seq = file.seq;

    //치환
    html = html.replace(/\[seq\]/g, seq)
                .replace(/\[imageUrl\]/g, imageUrl);

    //document객체로 변환 domParser
    const domParser = new DOMParser();
    const dom = domParser.parseFromString(html, "text/html");

    //요소가져오기 (해당 이미지)
    const imageTplEl = dom.querySelector(".image1_tpl_box");

    //타켓 가져오기 (선택)
    //올라온 이미지를 회원가입폼 프로필 쪽에 붙여넣는것
    const profileImage = document.getElementById("profile_image");
    //기존 html 비우고
    profileImage.innerHTML = "";

    profileImage.appendChild(imageTplEl);

}

//파일 삭제후 후속처리 함수
//@seq : 파일 등록 번호
function callbackFileDelete(seq){
    const fileEl = document.getElementById(`file_${seq}`); //선택
    //부모요소
    fileEl.parentElement.removeChild(fileEl); //제거
}