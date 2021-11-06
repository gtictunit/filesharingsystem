//For example we are defining menu in JSON format. You can also define it on Ul list. See on documentation.
var menu = [ {
	name : 'create',
	title : 'create button',
	fun : function() {
		alert('i am add button')
	}
}, {
	name : 'update',
	title : 'update button',
	fun : function() {
		alert('i am update button')
	}
}, {
	name : 'delete',
	title : 'create button',
	fun : function() {
		alert('i am add button')
	}
} ];

var menux = [ {
	name : 'create',
	title : 'create button',
	fun : function() {
		alert('i am add button')
	}
}, {
	name : 'update',
	title : 'update button',
	fun : function() {
		alert('i am update button')
	}
} ];

// Calling context menu
$('#folderx').contextMenu(menu);
$('#filex').contextMenu(menux);