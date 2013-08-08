/*
Copyright (c) 2003-2012, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

CKEDITOR.dialog.add( 'textfield', function( editor )
{
	var autoAttributes =
	{
		value : 1,
		size : 1,
		maxLength : 1
	};

	var acceptedTypes =
	{
		text : 1,
		password : 1
	};

	return {
		title : editor.lang.textfield.title,
		minWidth : 650,
		minHeight : 300,
		onShow : function()
		{
			delete this.textField;

			var element = this.getParentEditor().getSelection().getSelectedElement();
			if ( element && element.getName() == "input" &&
					( acceptedTypes[ element.getAttribute( 'type' ) ] || !element.getAttribute( 'type' ) ) )
			{
				alert();
				this.textField = element;
				this.setupContent( element );
			}
		},
		onOk : function()
		{
			var editor,
				element = this.textField,
				isInsertMode = !element;

			if ( isInsertMode )
			{
				editor = this.getParentEditor();
				element = editor.document.createElement( 'input' );
				element.setAttribute( 'type', 'text' );
			}
			
			if ( isInsertMode ) 
				editor.insertElement( element );
			this.commitContent( { element : element } );
		},
		onLoad : function()
		{
			var autoSetup = function( element )
			{
				var value = element.hasAttribute( this.id ) && element.getAttribute( this.id );
				this.setValue( value || '' );
			};

			var autoCommit = function( data )
			{
				var element = data.element;
				var value = this.getValue();

				if ( value )
					element.setAttribute( this.id, value );
				else
					element.removeAttribute( this.id );
			};

			this.foreach( function( contentObj )
				{
					if ( autoAttributes[ contentObj.id ] )
					{
						contentObj.setup = autoSetup;
						contentObj.commit = autoCommit;
					}
				} );
		},
		contents : [
			{
				id : 'info',
				label : editor.lang.textfield.title,
				title : editor.lang.textfield.title,
				
				elements : [
					{
						type : 'hbox',
						widths : [ '50%', '50%' ],
						children :
						[
							{
								id : '_cke_saved_name',
								type : 'text',
								label : editor.lang.textfield.name,
								'default' : '',
								accessKey : 'N',
								setup : function( element )
								{
									this.setValue(
											element.data( 'cke-saved-name' ) ||
											element.getAttribute( 'name' ) ||
											'' );
								},
								commit : function( data )
								{
									var element = data.element;

									if ( this.getValue() )
										element.data( 'cke-saved-name', this.getValue() );
									else
									{
										element.data( 'cke-saved-name', false );
										element.removeAttribute( 'name' );
									}
								}
							},
							{
								id : 'value',
								type : 'text',
								label : editor.lang.textfield.value,
								'default' : '',
								accessKey : 'V'
							}
						]
					},
					{
						type : 'hbox',
						widths : [ '50%', '50%' ],
						children :
						[
							{
								id : 'size',
								type : 'text',
								label : editor.lang.textfield.charWidth,
								'default' : '',
								accessKey : 'C',
								style : 'width:50px',
								validate : CKEDITOR.dialog.validate.integer( editor.lang.common.validateNumberFailed )
							},
							{
								id : 'maxLength',
								type : 'text',
								label : editor.lang.textfield.maxChars,
								'default' : '',
								accessKey : 'M',
								style : 'width:50px',
								validate : CKEDITOR.dialog.validate.integer( editor.lang.common.validateNumberFailed )
							}
						],
						onLoad : function()
						{
							// Repaint the style for IE7 (#6068)
							if ( CKEDITOR.env.ie7Compat )
								this.getElement().setStyle( 'zoom', '100%' );
						}
					},
					{
						type : 'hbox',
						widths : [ '50%', '50%' ],
						children :
						[
							{
								id : 'textType',
								type : 'select',
								label : '文本框类型',
								'default' : 'text',
								accessKey : 'Z',
								style : 'width:150px',
								items :
								[
									['文本框', 'text' ],
									['密码', 'password' ],
									['单选', 'singleSelect'],
									['多选', 'multiSelect'],
									['隐藏域', 'hidden']
								],
								setup : function( element ){
									this.setValue(
										element.data('textType') ||
										element.getAttribute( 'name' ) ||
									'' );
								},
								commit : function(data){  
									var element = data.element;
									if (this.getValue()) {
										element.data('textType',this.getValue());
										return "ext_type="+this.getValue() ;
									}else{
										element.data('textType',false);
										element.removeAttribute('name');
										return "" ;
									}
								},
								change : function (element){
									alert("on change!!!") ;
								}
							},
							{
								id : 'validate',
								type : 'text',
								label :'验证',
								'default' : '',
								accessKey : 'M',
								style : 'width:150px',
								setup : function( element )
								{
									this.setValue(
										element.data('validate') ||
										element.getAttribute( 'name' ) ||
									'' );
								},
								commit : function(data){
									var element = data.element;
									if (this.getValue()) {
										element.data('validate',this.getValue());
										return "ext_validate="+this.getValue() ;
									}else{
										element.data('validate',false);
										element.removeAttribute('name');
										return "" ;
									}
								}
							}
						],
						onLoad : function()
						{
							// Repaint the style for IE7 (#6068)
							if ( CKEDITOR.env.ie7Compat )
								this.getElement().setStyle( 'zoom', '100%' );
						}
					}
					/*
					,
					{
						type : 'hbox',
						widths : [ '50%', '50%' ],
						children :
						[
							{
								id : 'selectId',
								type : 'text',
								label : '下拉',
								'default' : '',
								autoid:24,
								accessKey : 'Z',
								style : 'width:150px',
								setup : function( element ){
									this.setValue(
										element.data('selectId') ||
										element.getAttribute( 'name' ) ||
									'' );
								},
								commit : function(data){  
									var element = data.element;
									if (this.getValue()) {
										element.data('selectId',this.getValue());
										return "ext_type="+this.getValue() ;
									}else{
										element.data('selectId',false);
										element.removeAttribute('name');
										return "" ;
									}
								},
								change : function (element){
									alert("on change!!!") ;
								}
							}
						],
						onLoad : function()
						{
							// Repaint the style for IE7 (#6068)
							if ( CKEDITOR.env.ie7Compat )
								this.getElement().setStyle( 'zoom', '100%' );
						}
					}
					*/
				]
			}
		]
	};
});
