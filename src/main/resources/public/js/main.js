let markdownElements = document.querySelectorAll('[md]');


markdownElements.forEach((item, index) => {
    let md = item.innerText;

    item.innerHTML = marked(md, {sanitize: false})
    item.style["white-space"] = "normal"
});