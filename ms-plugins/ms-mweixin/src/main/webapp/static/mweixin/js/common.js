//Quill富文本添加A链接支持
var Link = Quill.import('formats/link');
class FileBlot extends Link {  // 继承Link Blot
    static create(value) {
        let node = undefined
        if (value&&!value.href){  // 适应原本的Link Blot
            node = super.create(value);
        }
        else{  // 自定义Link Blot
            node = super.create(value.href);
            if(value.download){
                node.setAttribute('download', true);  // 左键点击即下载
            }
            node.innerText = value.innerText;
        }
        node.removeAttribute('target');
        return node;
    }
}
FileBlot.blotName = 'link';
FileBlot.tagName = 'a';
Quill.register(FileBlot);