/* smartui v2.2.2 - 2009-2014 clickntap.com */

window.log = function(message) {
	if (this.console) {
		console.log(message);
	}
};

window.debug = function(message) {
	if (this.console) {
		console.debug(message);
	}
};

window.err = function(message) {
	if (this.console) {
		console.error(message);
	}
};

var uiParams = {};
var uiService = '';
var uiSkip = false;
var uiResponseBusy = false;
var uiResponseTime = 0;
var uiBoundaryOpenTag = '[boundary]';
var uiBoundaryCloseTag = '[/boundary]';
var uiPreRequestCallback = function(params) {
};
var uiPostRequestCallback = function(params, millis) {
};
var uiErrorCallback = function(error) {
};
var uiWaitOnCallback = function(params, service) {
};
var uiWaitOffCallback = function(params, service) {
};
var uiLogRequestCallback = function(params, service) {
};
var uiRequestQueue = new Array();
var uiSyncTimer = 0;
var uiSupportDefaultApp = 'web';
var uiSupportSuffix = '-support.app';
var uiInit = function() {
	uiPostRequestCallback({}, 0);
};
var uiResponse = function(text) {
	boundaries = text.split(uiBoundaryOpenTag);
	for (i in boundaries) {
		if (i > 0) {
			command = boundaries[i].split(uiBoundaryCloseTag);
			if (command[0] == 'script') {
				eval(command[1]);
			} else {
				info = command[0].split(':');
				if (info[0] == 'inner') {
					$('#' + info[1]).html(command[1]);
				}
			}
		}
	}
}

function uiDebug() {
	uiPreRequestCallback = function(params) {
		log('uiPreRequest: ' + JSON.stringify(params))
	};
	uiPostRequestCallback = function(params, elapsed) {
		log('uiPostRequest: ' + elapsed + ' millis')
	};
	uiErrorCallback = function(error) {
		log('uiError: ' + JSON.stringify(error));
	};
	uiWaitOnCallback = function(params, service) {
		log('uiWaitOn');
	};
	uiWaitOffCallback = function(params, service) {
		log('uiWaitOff');
	};
	uiLog();
}

function uiLog() {
	uiLogRequestCallback = function(url, params) {
		log('url: ' + url + ", params: " + JSON.stringify(params));
	};
}

function closeResponse() {
	uiResponseTime = new Date().getTime() - uiResponseTime;
	if (!uiSkip) {
		uiPostRequestCallback(uiParams, uiResponseTime)
		uiWaitOffCallback(uiParams, uiService);
	}
	uiResponseBusy = false;
}

function ui(params) {
	uis(params, uiSupportDefaultApp);
}

function uiProcessNextInQueue() {
	if (uiResponseBusy) {
		setTimeout(uiProcessNextInQueue, 100);
	} else {
		if (uiRequestQueue.length > 0) {
			request = uiRequestQueue.pop();
			uiRequest(request.params, request.service);
			setTimeout(uiProcessNextInQueue, 100);
		}
	}
}

function uis(params, service) {
	uiSync = false;
	for (i in params) {
		if (params[i] && params[i].name == 'uiSync') {
			uiSync = true;
			params[i].name = false;
		}
	}
	if (params.uiSync) {
		uiSync = true;
		params.uiSync = false;
	}
	if (uiSync) {
		clearTimeout(uiSyncTimer);
		uiSyncTimer = setTimeout(function() {
			uis(params, service)
		}, 300);
		return;
	}
	if (!uiResponseBusy && uiRequestQueue.length == 0) {
		uiRequest(params, service);
	} else {
		skip = false;
		for (i = 0; i < uiRequestQueue.length; i++) {
			request = uiRequestQueue[i];
			if (JSON.stringify(request.params) == JSON.stringify(params) && request.service == service) {
				skip = true;
				break;
			}
		}
		if (!skip) {
			uiRequestQueue.unshift({
				params : params,
				service : service
			});
			setTimeout(uiProcessNextInQueue, 100);
		}
	}
}

function uiRequest(params, service) {
	uiSkip = false;
	for (i in params) {
		if (params[i] && params[i].name === 'uiSkip')
			uiSkip = true;
	}
	if (params.uiSkip) {
		uiSkip = true;
	}
	uiResponseBusy = true;
	uiResponseTime = new Date().getTime();
	if (!uiSkip) {
		uiWaitOnCallback(params, service);
		uiPreRequestCallback(params);
	}
	uiParams = params;
	uiService = service;
	if (params.uiFunction) {
		setTimeout(params.uiFunction, 100);
		closeResponse();
	} else {
		uiLogRequestCallback(service + uiSupportSuffix, params);
		$.ajax({
			url : service + uiSupportSuffix,
			type : 'POST',
			data : params,
			dataType : 'text',
			success : function(data) {
				uiResponse(data);
				closeResponse();
			},
			error : function(error) {
				uiErrorCallback(error);
				closeResponse();
			}
		});
	}
}

function uif(form) {
	return uisf(form, uiSupportDefaultApp);
}

function uisf(form, service, event) {
	uis($(form).serializeArray(), service);
	return false;
}

if (navigator.userAgent.match(/IEMobile\/10\.0/)) {
	var msViewportStyle = document.createElement('style')
	msViewportStyle.appendChild(document.createTextNode('@-ms-viewport{width:auto!important}'))
	document.querySelector('head').appendChild(msViewportStyle)
}

$(function() {
	var nua = navigator.userAgent
	var isAndroid = (nua.indexOf('Mozilla/5.0') > -1 && nua.indexOf('Android ') > -1 && nua.indexOf('AppleWebKit') > -1 && nua.indexOf('Chrome') === -1)
	if (isAndroid) {
		$('select.form-control').removeClass('form-control').css('width', '100%')
	}
})