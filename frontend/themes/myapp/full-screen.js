window.openFullscreen = function() {
	if(document.fullscreenElement!=null || document.webkitFullscreenElement != null || document.msFullscreenElement != null) {
		if (document.exitFullscreen) {
    			document.exitFullscreen();
  		} else if (document.webkitExitFullscreen) { 
    			document.webkitExitFullscreen();
  		} else if (document.msExitFullscreen) { 
    			document.msExitFullscreen();
  		}
	} else {
		if (document.documentElement.requestFullscreen) {
    			document.documentElement.requestFullscreen();
  		} else if (document.documentElement.webkitRequestFullscreen) {
    			document.documentElement.webkitRequestFullscreen();
  		} else if (document.documentElement.msRequestFullscreen) {
    			document.documentElement.msRequestFullscreen();
 		}
	}
}