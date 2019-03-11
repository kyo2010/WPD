function cfm(msg,url){
  if (confirm(msg)){
    document.location=url;
  }       
}

function w3_open() {
  document.getElementById("main").style.marginLeft = "250px";
  document.getElementById("mySidebar").style.width = "250px";
  document.getElementById("mySidebar").style.display = "block";
  document.getElementById("openNav").style.display = 'none';
}
function w3_close() {
  document.getElementById("main").style.marginLeft = "0%";
  document.getElementById("mySidebar").style.display = "none";
  document.getElementById("openNav").style.display = "inline-block";
}

