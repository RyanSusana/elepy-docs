


let markdownElements = document.querySelectorAll('[md]');


markdownElements.forEach((item, index)=>{
    let md = item.innerHTML;
    console.log(md)
    item.innerHTML = marked(md, {sanitize: false})
});