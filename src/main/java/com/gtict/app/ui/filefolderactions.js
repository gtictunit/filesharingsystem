//For example we are defining menu in JSON format. You can also define it on Ul list. See on documentation.
var menu = [ {
	name : 'create',
	img : 'images/create.png',
	title : 'create button',
	fun : function() {
		alert('i am add button')
	}
}, {
	name : 'update',
	img : 'images/update.png',
	title : 'update button',
	fun : function() {
		alert('i am update button')
	}
}, {
	name : 'delete',
	img : 'images/delete.png',
	title : 'create button',
	fun : function() {
		alert('i am add button')
	}
} ];

var menux = [ {
	name : 'create',
	img : 'images/create.png',
	title : 'create button',
	fun : function() {
		alert('i am add button')
	}
}, {
	name : 'update',
	img : 'images/update.png',
	title : 'update button',
	fun : function() {
		alert('i am update button')
	}
}, {
	name : 'delete',
	img : 'images/delete.png',
	title : 'create button',
	fun : function() {
		alert('i am add button')
	}
} ];

// Calling context menu
$('.folderx').contextMenu(menu);
$('.file').contextMenu(menux);