let markdownElements = document.querySelectorAll('[md]');


markdownElements.forEach((item, index) => {
    let md = item.innerHTML;
    item.innerHTML = marked(md, {sanitize: false})
});