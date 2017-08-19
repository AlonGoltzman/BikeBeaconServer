$(document).ready(function() {
    $('.dropdown').on('show.bs.dropdown', function () {
    	   $.ajax({
				  method: "POST",
				  url: "../api/notifications",
				})
             .done(function(data) {	
             	alert(data);
             });
    });
});