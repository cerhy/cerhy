﻿//初始化页面元素
$(function(){
	$(".xueke-content-detail").mouseup(function(e){
		var selectedText ;
		if(window.getSelection) {
			selectedText = window.getSelection().toString();
		}else if(document.selection && document.selection.createRange) {
			selectedText = document.selection.createRange().text;
		}
		if(selectedText){
			$("#icon").css({
				"left" : e.clientX+1,
				"top" : e.pageY-30
				
			}).fadeIn(300);
		}else {
			$("#icon").css({
				"left" : e.clientX+1,
				"top" : e.pageY-30
				
			}).fadeIn(300);
			//$("#icon").hide();
		}
	});
	$("#icon").hover(function(){
		$(this).children().removeClass("tipsIcon");
	}
	,
	function(){
		$(this).children().addClass("tipsIcon");
	}).click(function() {
		$("#icon").hide();
		addPostil();
	});
});
//添加批注
function addPostil() {
	//IE支持的range对象
	var ie_range ;
	//其他浏览器的range对象
	var other_range ;
	if(window.getSelection) {
		other_range = window.getSelection().getRangeAt(0);
	}
	else if(document.selection && document.selection.createRange) {
		ie_range = document.selection.createRange();
	}
	art.dialog({
		id:'inputDialog', 
		title:'添加批注', 
		content:'<textarea id="postil" rows="10" cols="30"></textarea>',
		lock:true
	
	}
	, function(){
		var value = document.getElementById("postil").value;
		var postilUserId = document.getElementById("postilUserId").value;
		var postilUserName = document.getElementById("postilUserName").value;
		if(!value){
			art.dialog({
				content:'批注内容不能为空！', time: 1
			});
			return false;
		}
		if(other_range) {
			/*
			//IE之外的浏览器，如果在选择内容包含其他标签的一部分的时候会报异常
			var mark = document.createElement("ins");
			mark.setAttribute("comment", value);
			mark.className = "postil";
			mark.id=new Date().getTime();
			other_range.surroundContents(mark);
			*/
			var selected = other_range.extractContents().textContent;
			var text = "[ins id='"+(new Date().getTime())+"' comment='"+value+"']"+selected+"[/ins]";
			var textNode = document.createTextNode(text);
			other_range.insertNode(textNode);
			var content = $(".xueke-content-detail").html();
			var reg = /\[ins id='(\d*)' comment='([\w\W]*)']([\w\W]*)\[\/ins]/gi;
			reg.test(content);
			var id = RegExp.$1,
			comment = RegExp.$2,
			c = RegExp.$3;
			var reHtml = "<ins title=批注人:"+postilUserName+"批注内容:"+comment+" id='"+id+"' comment='"+comment+"' postilUserId='"+postilUserId+"' class='postil' >"+c+"</ins>";
			content = content.replace(reg, reHtml);
			$(".xueke-content-detail").html(content);
		}else if(ie_range) {
			ie_range.pasteHTML("<ins comment='"+value+"' class='postil' id='"+new Date().getTime()+"'>"+ie_range.htmlText+"</ins>");
			ie_range=null;
		}
		loader();
	});
}
//解析批注
function loader(){
	var $list = $(".list");
	$list.children().remove();
	var postilUserId = document.getElementById("postilUserId").value;
	$.each($(".xueke-content-detail ins"), function(a, b){
		var content = $(b).attr("comment");
		var userId = $(b).attr("postilUserId");
		if(postilUserId==userId){
		var $postil = $("<div forid='"+$(b).get(0).id+"'>"+content+"<span onClick='removePostil(this)'>　　x</span></div>");
		$postil.hover(function(){
			$(this).css("border-color", "red");
			$("#"+$(this).attr("forid")+"").removeClass().addClass("postilFocus");
		}
		,
		function(){
			$(this).css("border-color", "#ddd");
			$("#"+$(this).attr("forid")+"").removeClass().addClass("postil");
		});
		$(b).hover(function(){
			$(this).removeClass().addClass("postilFocus");
			$("div[forid='"+$(this).get(0).id+"']").css("border-color", "red");
			//$("em[id='"+$(this).get(0).id+"']").animate({opacity: "show", top: "-75"}, "slow"); 
			//$("div[forid='"+$(this).get(0).id+"']").css("display", "inline");
		}
		,
		function(){
			$(this).removeClass().addClass("postil");
			$("div[forid='"+$(this).get(0).id+"']").css("border-color", "#ddd");
			//$("em[id='"+$(this).get(0).id+"']").animate({opacity: "hide", top: "-85"}, "fast");  
			//$("div[forid='"+$(this).get(0).id+"']").css("display", "none");
		});
		$list.append($postil);
		$("#saves").val($(".xueke-content-detail").html().trim());
		}else{
			$(this).removeClass("postil");
			$(this).css("text-decoration", "none");
			$(this).attr("title","");
		}
	});
}
//删除批注
function removePostil(arg){
	var $div = $(arg).parent();
	var id = $div.attr("forid");
	var $source = $("#"+id);
	var text = $source.after($source.html());
	$source.remove();
	loader();
}


//解析批注
function loader2(){
	var $list = $(".list");
	var postilUserId = document.getElementById("postilUserId").value;
	$list.children().remove();
	$.each($(".xueke-content-detail ins"), function(a, b){
		var content = $(b).attr("comment");
		var userId = $(b).attr("postilUserId");
		if(postilUserId==userId){
			var $postil = $("<div forid='"+$(b).get(0).id+"'>"+content+"<span onClick='removePostil(this)'>　　x</span></div>");
			$postil.hover(function(){
				$(this).css("border-color", "red");
				$("#"+$(this).attr("forid")+"").removeClass().addClass("postilFocus");
			}
			,
			function(){
				$(this).css("border-color", "#ddd");
				$("#"+$(this).attr("forid")+"").removeClass().addClass("postil");
			});
			$(b).hover(function(){
				$(this).removeClass().addClass("postilFocus");
				$("div[forid='"+$(this).get(0).id+"']").css("border-color", "red");
			}
			,
			function(){
				$(this).removeClass().addClass("postil");
				$("div[forid='"+$(this).get(0).id+"']").css("border-color", "#ddd");
			});
			$list.append($postil);
			$("#saves").val($(".xueke-content-detail").html().trim());
		}else{
			$(this).removeClass("postil");
			$(this).css("text-decoration", "none");
			$(this).attr("title","");
		}
	});
}




function savePostil(userId,contentId){
	if($(".xueke-content-detail").html().trim()!=""){
		$.ajax({
			url: "${base}/blog/savePostil.jspx?contentId="+contentId+"&txt"+$(".xueke-content-detail").html().trim(),
			type: "POST",
			dataType:"json",
			data: "",
			success: function (data) {
				if(data.status>0){
					alert("批注保存成功!");
				}else{
					alert("批注保存失败!");
				}
			}
		});
	}else{
		alert("文章没有进行批注不需要保存!");
	}
}