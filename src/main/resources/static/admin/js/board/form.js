window.addEventListener("DOMContentLoaded", function(){
    const { loadEditor } = commonLib;


        loadEditor("html_top")
            .then(editor => window.editor1 = editor) // 반환값 editor값을 전역에서 조회할 수 있게
            .catch(err => console.error(err)); //err가 나오면 콘솔창에 에러 출력
        loadEditor("html_bottom")
           .then(editor => window.editor2 = editor) // 반환값 editor값을 전역에서 조회할 수 있게
           .catch(err => console.error(err));

        //에디터 이미지 본문 추가 이벤트
        const insertImages = document.getElementsByClassName("insert_image");
        for(const el of insertImages){
            el.addEventListener("click", function(){
                const parentId = this.parentElement.parentElement.id;
                //상단 하단 구분 (부모의 부모의 아이디로)
                const editor = parentId.indexOf("html_bottom") == -1 ? editor1 : editor2;
                const url = this.dataset.url;

                insertImage(editor, url);
            })
        }


});

//파일 업로드 후 후속 처리 함수
function callbackFileUpload(files){
    //파일이 없거나 파일길이야 0이면 종료
    if( !files || files.length == 0){
        return;
    }
    //템플릿 가져오기
    const tpl = document.getElementById("editor_tpl").innerHTML;

    //html = 텍스트 형태 이기때문에 돔으로 바꿔서
    const domParser = new DOMParser();
    //타겟 2가지 (올라가는 파일 위치 2군데)
    const targetTop = document.getElementById("uploaded_files_html_top");
    const targetBottom = document.getElementById("uploaded_files_html_bottom");

    //location값에 따라서 editor1 , editor2 에디터가 바뀜
    //- html-top -> editor1  /  html-bottom -> editor2
    for ( const file of files){
        //location값이 html_bottom이면 editor2이고
        const editor = file.location == 'html_bottom' ? editor2 : editor1;
        const target = file.location == 'html_bottom' ? targetBottom : targetTop;
        //애디터 추가할때 원본이미지 그대로 넣는다
        //정해져있는 API : insertImage
        //소스항목에 주소를 입력
        insertImage(editor, file.fileUrl); //에디터에 이미지 추가

        //템플릿 데이터 치환
        //템플릿 치환코드 부분 교체
        //명칭만 입력하면 데이터가 교체
        let html = tpl;  //const는 상수 이기때문에 바뀌지않아서 let 씀
        html = html.replace(/\[seq\]/g, file.seq) //html 치환
                    .replace(/\[fileName\]/g, file.fileName)
                    .replace(/\[imageUrl\]/g, file.fileUrl);

        //치환한거 dom으로 바꾸자 (html에 넣자)
        const dom = domParser.parseFromString(html, "text/html");
        //가져오기
        const fileBox = dom.querySelector(".file_tpl_box");
        target.appendChild(fileBox);

        const el = fileBox.querySelector(".insert_image")
        if(el){
            el.addEventListener("click", () => insertImage(editor, file.fileUrl));
        }

    }

}

//에디처 본문 이미지 첨부
function insertImage(editor, source){
    editor.execute('insertImage', { source });
}

//에디터 이미지 삭제
function callbackFileDelete(seq) {
    const el = document.getElementById(`file_${seq}`);
    el.parentElement.removeChild(el);

    const pattern = new RegExp(`.*(<figure.*><img.*src=['"]?/uploads/\\d{1,}/${seq}\\.\\D{3,}['"]?.*></figure>).*`, 'igm');

    const result1 = pattern.exec(editor1.getData());
    if (result1) {
        editor1.setData(editor1.getData().replace(result1[1].replace('\\"', '"'), ''));
    }

    const result2 = pattern.exec(editor2.getData());
    if (result2) {
         editor2.setData(editor2.getData().replace(result2[1].replace('\\"', '"'), ''));
    }
}
