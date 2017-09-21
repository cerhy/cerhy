/**
 * @license Copyright (c) 2003-2017, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 */
CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	// config.language = 'fr';
	 config.language = 'zh-cn'; // 配置语言  
	    config.width = 'auto'; // 宽度  
	    config.height = '600px'; // 高度  
	    config.uiColor = '#f8f8f8';
	    config.toolbar = 'MyToolbar';// 工具栏风格（基础'Basic'、全能'Full'、自定义）
	    //自定义的工具栏    
	    config.toolbar_MyToolbar =  
	    [  
	        ['Source','-','Preview','Print'],  
	        ['Cut','Copy','Paste','PasteText','PasteFromWord','-','Scayt'],  
	        ['Undo','Redo','-','Find','Replace','-','SelectAll','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'], 
	        [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','TextColor','BGColor','-','RemoveFormat'],
	        ['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
	        ['Image','Flash','Table','HorizontalRule','Smiley','SpecialChar'],  
	        ['Link','Unlink','Anchor','Iframe'],
	        ['Maximize','autoformat','-'],
	        ['Styles','Format','Font','FontSize','lineheight']
	    ];  
	    config.extraPlugins += (config.extraPlugins ? ',lineheight' : 'lineheight');
		config.font_names = '隶书/隶书;宋体/SimSun;新宋体/NSimSun;黑体/SimHei;楷体/KaiTi;微软雅黑/Microsoft YaHei;'+  
	    '楷体_GB2312/KaiTi_GB2312;仿宋/FangSong;仿宋_GB2312/FangSong_GB2312;华文细黑/STXihei;华文楷体/STKaiti;华文宋体/STSong;华文中宋/STZhongsong;'+  
	    '华文仿宋/STFangsong;华文彩云/STCaiyun;华文琥珀/STHupo;华文隶书/STLiti;华文行楷/STXingkai;华文新魏/STXinwei;'+  
	    '方正舒体/FZShuTi;方正姚体/FZYaoti;细明体/MingLiU;新细明体/PMingLiU;微软正黑/Microsoft JhengHei;'+  
	    'Arial Black/Arial Black;'+ config.font_names;
		config.pasteFromWordRemoveFontStyles = false;
		config.pasteFromWordRemoveStyles = false;
		config.allowedContent=true;
};
CKEDITOR.on( 'instanceReady', function( ev ) { with (ev.editor.dataProcessor.writer) {
	setRules("p", {indent : true, breakBeforeOpen : true, breakAfterOpen : false,breakBeforeClose : false,  breakAfterClose : true} );
	setRules("div", {indent : true, breakBeforeOpen : true, breakAfterOpen : false,breakBeforeClose : false,  breakAfterClose : true} );
	}
}); 