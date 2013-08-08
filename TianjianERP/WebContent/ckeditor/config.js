/*
Copyright (c) 2003-2012, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

CKEDITOR.editorConfig = function( config )
{
	 config.skin = 'office2003';
	 config.height = document.body.clientHeight -120 ;    
	 config.extraPlugins = 'input,listView'; 
	 
	 config.toolbar_Full = [
	       ['Source'],
	       ['Cut','Copy','Paste','PasteText','PasteFromWord','-','Print', 'SpellChecker', 'Scayt'],
	       ['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
	       '/',
	       ['Bold','Italic','Underline','Strike','-','Subscript','Superscript'],
	        ['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
	        ['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
	        ['Link','Unlink','Anchor'],
	       ['Image','Table','HorizontalRule','Smiley','SpecialChar'],
	       '/',
	        ['Styles','Format','Font','FontSize'],
	        ['TextColor','BGColor']
	    ];
	 
	 config.enterMode = CKEDITOR.ENTER_BR; //可选：CKEDITOR.ENTER_BR或CKEDITOR.ENTER_DIV
};
