
window.addEventListener("DOMContentLoaded", function(){
    if(typeof ClassicEditor == 'function'){ //에디터 사용하면
        //에디터 로드 (비구조할당)
        const { loadEditor } = commonLib;

        loadEditor("content", 450)
            .then(editor => window.editor = editor);

    }
});

/*이미지 본문 추가 이벤트 처리 S*/
const insertimages = document.getElementsByClassName("insert_image");
for(const el of insertimages){
    el.addEventListener("click", (e) => insertImage(e.currentTarget.dataset.url));
}
/*이미지 본문 추가 이벤트 처리 E*/

//에디터에 이미지 추가
function insertImage(source){
    editor.execute('insertImage', { source })
}

//파일업로드 후 후속처리
function callbackFileUpload(files){
    if(!files || files.length == 0){
        return;
    }
    const imageUrls = [];

    const editorTpl = document.getElementById("editor_tpl").innerHTML;
    const attachTpl = document.getElementById("attach_tpl").innerHTML;

    //location값에 따라 에디터로 첨부될지 파일로 첨부될지 결정된다
    const editorFiles = document.getElementById("editor_files");
    const attachFiles = document.getElementById("attach_files");

    //dom객체로 변환
    const domParser = new DOMParser();

    for(const file of files){
        const location = file.location;
        let html = location == 'editor' ? editorTpl : attachTpl;
        const targetEl = location == 'editor' ? editorFiles : attachFiles;

        if(location == 'editor'){
            imageUrls.push(file.fileUrl); //이미지 전체 한꺼번에 추가
        }

        //가공해서 ( 치환 작업)
        html = html.replace(/\[seq\]/g, file.seq)
            .replace(/\[fileName\]/g, file.fileName)
            .replace(/\[imageUrl\]/g, file.fileUrl);

        //document 객체로 변환
        const dom = domParser.parseFromString(html, "text/html");
        //가져와서
        const fileBox = dom.querySelector(".file_tpl_box");
        //치환
        targetEl.appendChild(fileBox);

        //에디터 본문 추가
        const insertImageEl = fileBox.querySelector(".insert_image");
        //존재할때만 이벤트 발생
        if(insertImageEl) insertImageEl.addEventListener("click", () => insertImage(file.fileUrl));

    }

    if(imageUrls.length > 0) insertImage(imageUrls);



}

//파일삭제 후 후속처리
function callbackFileDelete(seq){
    const fileBox = document.getElementById(`file_${seq}`);
    fileBox.parentElement.removeChild(fileBox);
}