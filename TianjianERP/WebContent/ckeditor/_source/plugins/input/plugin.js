/*
Copyright (c) 2003-2012, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

/**
 * @file Forms Plugin
 */
var matech_input_win ;
CKEDITOR.plugins.add( 'input',
{
	init : function( editor )
	{
		
		var pluginName = 'input';
       // CKEDITOR.dialog.add(pluginName, this.path + 'dialogs/input.js');
        editor.addCommand(pluginName, {
        	exec:function(editor) {
        		if(!matech_input_win)
        			matech_input_win = new Ext.matech.form.inputDialog({editor:editor});
        		matech_input_win.clear();
        		matech_input_win.show() ;
        	}
        });
        

		if ( editor.contextMenu )
		{
			editor.contextMenu.addListener( function( element )
				{
					if ( element.is('input') ) {
						//new inputDialog();
						//alert(345543);
					} 
						
				});
		}

		editor.on( 'doubleclick', function( evt ){
			 var element = evt.data.element;  
			  
             if ( element.is('input') )  {
            	 var type = element.getAttribute( 'type' ) ;
            	 var value = element.getAttribute( 'value' ) ;
            	 var ext = element.getAttribute( 'matech_ext' ) ;
            	 if(type == "text" && value != "{列表控件}" && (ext.indexOf("ext_type=sublist;") == -1)) {
            		 if(!matech_input_win) {
            			 matech_input_win = new Ext.matech.form.inputDialog({editor:editor});
            		 }
            		 var attribute = element.getAttribute("matech_ext");
            		 matech_input_win.clear();
            		 matech_input_win.parse(attribute) ;
            		 matech_input_win.setElement(element) ;
            		 matech_input_win.show() ;	
            	 }
             }
            	
		});
	},

	afterInit : function( editor ){
		
	}
} );


