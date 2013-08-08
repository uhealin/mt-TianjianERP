 

var   isSaved   =   true; 
var input;
function protect(thisforms){
	
document.body.onbeforeunload=function ff(){return f()}
document.body.onclick=function getinfo(){return save()}

 input   =   document.getElementsByName(thisforms)[0].getElementsByTagName("INPUT"); 

for(var   i=0;i <input.length;   i++){ 
input[i].attachEvent("onchange",func); 
} 
}

function   func(){ 

        isSaved   =   false 
} 


function save(){

	if(window.event.srcElement.type == "submit"||window.event.srcElement.value=="修改密码"){
		isSaved = true;
	}
		
}

function   f() 
{ 

if(!isSaved) 
{ 
return  "您输入的信息还没保存，离开会使信息丢失！"; 
} 
} 

function   func(){ 
     isSaved   =   false 
   
}



