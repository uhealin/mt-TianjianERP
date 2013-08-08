
var matech_listView_win ;
CKEDITOR.plugins.add('listView',
{
	init : function( editor )
	{
		
		var pluginName = 'listView';
       // CKEDITOR.dialog.add(pluginName, this.path + 'dialogs/input.js');
        editor.addCommand(pluginName, {
        	exec:function(editor) {
        		if(!matech_listView_win)
        			matech_listView_win = new Ext.matech.form.listViewDialog({editor:editor});
        		matech_listView_win.clear();
        		matech_listView_win.show() ;
        	}
        });
        

		if ( editor.contextMenu )
		{
			editor.contextMenu.addListener( function( element )
				{
					if ( element.is('input') ) {
						//new inputDialog();
					} 
						
				});
		}

		editor.on( 'doubleclick', function( evt ){
			 var element = evt.data.element;  
			  
             if ( element.is('input') )  {
            	 var value = element.getAttribute( 'value' ) ;
            	 var ext = element.getAttribute( 'matech_ext' ) ;
            	 if(value == "{列表控件}" || (ext.indexOf("ext_type=sublist;") > -1)) {
            		 if(!matech_listView_win) {
            			 matech_listView_win = new Ext.matech.form.listViewDialog({editor:editor});
            		 }
            		 var attribute = element.getAttribute("matech_ext");
            		 matech_listView_win.clear();
            		 matech_listView_win.parse(attribute) ;
            		 matech_listView_win.setElement(element) ;
            		 matech_listView_win.show() ;	
            	 }
             }
            	
		});
	},

	afterInit : function( editor ){
		
	}
} );


