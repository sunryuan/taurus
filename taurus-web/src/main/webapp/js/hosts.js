$(function () {
	var log = function(s){
        window.console && console.log(s);
    };
    $('.nav-tabs a:first').tab('show');
    $('a[data-toggle="tab"]').on('show', function (e) {
        log(e);
    });
    $('a[data-toggle="tab"]').on('shown', function (e) {
        log(e.target); // activated tab
        log(e.relatedTarget); // previous tab
    });
});