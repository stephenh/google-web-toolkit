var plugin = document.getElementById('pluginEmbed');
var disabledIcon = 'gwt32-gray.png';
var enabledIcon = 'gwt32.png';


function getHostFromUrl(url) {
  var hostname = '';
  var idx = url.indexOf('://');
  if (idx >= 0) {
    idx += 3;
    hostname = url.substring(idx);
  }
  idx = hostname.indexOf('/');
  if (idx >= 0) {
    hostname = hostname.substring(0,idx);
  }
  idx = hostname.indexOf('@');
  if( idx >= 0)
  {
    hostname = hostname.substring(idx+1);
  }
  idx = hostname.indexOf(':');
  if (idx >= 0) {
    hostname = hostname.substring(0,idx);
  }
  return hostname;
}

function getCodeServerFromUrl(url) {
  var idx = url.indexOf('?');
  if (idx < 0) {
    return '';
  }
  url = url.substring(idx+1);
  idx = url.indexOf('gwt.codesvr=');
  if( idx < 0 ) {
    return '';
  }
  url = url.substring(idx+12);
  var colon = url.indexOf(':');
  var amp   = url.indexOf('&');
  if( amp < 0 || colon < amp ) {
    amp = colon;
  }
  return amp < 0 ? url : url.substring(0,amp);
}

function devModeTabListener(tabId, changeInfo, tab) {
  var search = tab.url.slice(tab.url.indexOf('?'));
  if (search.indexOf('gwt.codesvr=') >= 0 || search.indexOf('gwt.hosted=') >= 0) {
    var permission = plugin.getHostPermission(tab.url);
    var host = getHostFromUrl(tab.url);
    var code = getCodeServerFromUrl(tab.url);
    var popup = 'page_action.html';
    var icon = null;
    console.log("got permission " + permission + " for host " + host + '/ code ' + code);

    var idObject = {};
    plugin.testJsIdentity( idObject, idObject );

    if (permission == 'include') {
      icon = enabledIcon;
    } else if (permission == 'exclude') {
      icon = disabledIcon;
    } else if (permission == 'unknown') {
      icon = disabledIcon;
    }
    popup += "?permission=" + permission + "&host=" + host + "&codeserver=" + code;
    chrome.pageAction.setIcon({'tabId' : tabId, 'path' : icon});
    chrome.pageAction.setPopup({'tabId' : tabId, 'popup' : popup});
    chrome.pageAction.show(tabId);

    var hostEntries = window.localStorage.getItem('GWT_DEV_HOSTENTRY') || [];
    console.log("loading hostentries: " + hostEntries);
    plugin.loadHostEntries.apply(plugin, JSON.parse(hostEntries));
  } else {
    chrome.pageAction.hide(tabId);
  }
};

chrome.tabs.onUpdated.addListener(devModeTabListener);
