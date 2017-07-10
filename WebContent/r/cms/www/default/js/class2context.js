/* class2context.js
creator: Arnau
July 2016
version: v0.1


usage:
class2context(
  'className',
  "title",
  [["option1", "functionoption1()"], ["option 2","functionoption2()"]]
);
*/




//creates the div where the context menus will place
//document.body.innerHTML+="<div id='contextMenus'></div>";
//

function class2context(classgiv, title, options){
    classgiv=JSON.parse(JSON.stringify(classgiv));
    if(document.getElementById("context"+classgiv))
    {
        //toastr.success("alreadyExist");
    }else{
        var aux="";
        aux+="<div id='context"+classgiv+"' class='contextMenu'>";
        aux+="  <ul class='c2c-dropdown c2c-border' style='display:block;position:static;margin-bottom:5px;'>";
        aux+="<div class='contextTitle'>" + title + "</div>";
        for(var i=0; i<options.length; i++)
        {
        	if(classgiv=='class3'){
        		if(i==0){
        			aux+="    <a id='sigout' onmousedown='"+options[i][1]+"; ContextMenus();' href='javascript:void(0);'>"+options[i][0]+"</a>";
        		}
        	}else{
        		
        		if(i==0){
        			aux+="    <a id='createEle' onmousedown='"+options[i][1]+"; ContextMenus();' href='javascript:void(0);'>"+options[i][0]+"</a>";
        			
        		}else if(i==1){
        			
        			aux+="    <a id='editEle' onmousedown='"+options[i][1]+"; ContextMenus();' href='javascript:void(0);'>"+options[i][0]+"</a>";
        		}else if(i==2){
        			
        			aux+="    <a id='delEle' onmousedown='"+options[i][1]+"; ContextMenus();' href='javascript:void(0);'>"+options[i][0]+"</a>";
        		}
        	}

        }
        aux+="  </ul>";
        aux+="</div>";
        document.getElementById('contextMenus').innerHTML+=aux; //adds the div context menu into the context menus div place
    }

    for(var i=0; i<document.getElementsByClassName(classgiv).length; i++)
    {
        classgiv=JSON.parse(JSON.stringify(classgiv));
        if(classgiv=='class3'){
        	document.getElementsByClassName(classgiv)[i].addEventListener("contextmenu", function(e){
        		// Avoid the real one
        		e.preventDefault();
        		e.stopPropagation();
        		var tar = e.target;
        		$("#sigout").attr("onmousedown","quitGroup("+tar.id+");ContextMenus();");
        		setTimeout(function(){
        			document.getElementById('context'+classgiv).style.display='block';
        			document.getElementById('context'+classgiv).style.left=e.pageX + "px";
        			document.getElementById('context'+classgiv).style.top=e.pageY + "px";
        		}, 150);
        	});
        }else{
        	document.getElementsByClassName(classgiv)[i].addEventListener("contextmenu", function(e){
        		// Avoid the real one
        		e.preventDefault();
        		e.stopPropagation();
        		var tar = e.target;
        		$("#createEle").attr("onmousedown","createChannel("+tar.id+");ContextMenus();");
        		$("#editEle").attr("onmousedown","editChannel("+tar.id+");ContextMenus();");
        		$("#delEle").attr("onmousedown","delChannel("+tar.id+");ContextMenus();");
        		setTimeout(function(){
        			document.getElementById('context'+classgiv).style.display='block';
        			document.getElementById('context'+classgiv).style.left=e.pageX + "px";
        			document.getElementById('context'+classgiv).style.top=e.pageY + "px";
        		}, 150);
        	});
        }
    }

    document.body.addEventListener("mousedown", function(e){
        // Avoid the real one
        //e.preventDefault();
        if(document.getElementById('context'+classgiv))
        {
            setTimeout(function(){
                document.getElementById('context'+classgiv).style.display='none';
            }, 70);
            //ContextMenus();

        }
        return false;
    }, false);

}
