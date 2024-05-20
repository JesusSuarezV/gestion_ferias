const menu =document.querySelector('.menu')

fetch('../views/menu.html')
.then(res=>res.text())
.then(data=>{
    menu.innerHTML=data
    const parser = new DOMParser()
    const doc = parser.parseFromString(data, 'text/html')
    eval(doc.querySelector('script').textContent)
})

const nav =document.querySelector('.header')

fetch('../views/navbar.html')
.then(res=>res.text())
.then(data=>{
    nav.innerHTML=data
    const parser = new DOMParser()
    const doc = parser.parseFromString(data, 'text/html')
})