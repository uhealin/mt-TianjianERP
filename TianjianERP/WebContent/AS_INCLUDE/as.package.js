 

var   isSaved   =   true; 
var input;
function protect(thisforms){
	
document.body.onbeforeunload=function ff(){return f()}
document.body.onclick=function getinfo(){return save()}

 input   =   document.getElementsByName(thisforms)[0].getElementsByTagName("INPUT"); 

for(var   i=0;i <input.length;   i++){ 
input[i].attachEvent("onchange",func); 
} 
}

function   func(){ 

        isSaved   =   false 
} 


function save(){

	if(window.event.srcElement.type == "submit"||window.event.srcElement.value=="ĞŞ¸ÄÃÜÂë"){
		isSaved = true;
	}
		
}

function   f() 
{ 

if(!isSaved) 
{ 
return  "ÄúÊäÈëµÄĞÅÏ¢»¹Ã»±£´æ£¬Àë¿ª»áÊ¹ĞÅÏ¢¶ªÊ§£¡"; 
} 
} 

function   func(){ 
     isSaved   =   false 
   
}



/*  Prototype JavaScript framework, version 1.5.0_rc0
 *  (c) 2005 Sam Stephenson <sam@conio.net>
 *
 *  Prototype is freely distributable under the terms of an MIT-style license.
 *  For details, see the Prototype web site: http://prototype.conio.net/
 *
/*--------------------------------------------------------------------------*/

var Prototype = {
  Version: '1.5.0_rc0',
  ScriptFragment: '(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)',

  emptyFunction: function() {},
  K: function(x) {return x}
}

var Class = {
  create: function() {
    return function() {
      this.initialize.apply(this, arguments);
    }
  }
}

var Abstract = new Object();

Object.extend = function(destination, source) {
  for (var property in source) {
    destination[property] = source[property];
  }
  return destination;
}

Object.inspect = function(object) {
  try {
    if (object == undefined) return 'undefined';
    if (object == null) return 'null';
    return object.inspect ? object.inspect() : object.toString();
  } catch (e) {
    if (e instanceof RangeError) return '...';
    throw e;
  }
}

Function.prototype.bind = function() {
  var __method = this, args = $A(arguments), object = args.shift();
  return function() {
    return __method.apply(object, args.concat($A(arguments)));
  }
}

Function.prototype.bindAsEventListener = function(object) {
  var __method = this;
  return function(event) {
    return __method.call(object, event || window.event);
  }
}

Object.extend(Number.prototype, {
  toColorPart: function() {
    var digits = this.toString(16);
    if (this < 16) return '0' + digits;
    return digits;
  },

  succ: function() {
    return this + 1;
  },

  times: function(iterator) {
    $R(0, this, true).each(iterator);
    return this;
  }
});

var Try = {
  these: function() {
    var returnValue;

    for (var i = 0; i < arguments.length; i++) {
      var lambda = arguments[i];
      try {
        returnValue = lambda();
        break;
      } catch (e) {}
    }

    return returnValue;
  }
}

/*--------------------------------------------------------------------------*/

var PeriodicalExecuter = Class.create();
PeriodicalExecuter.prototype = {
  initialize: function(callback, frequency) {
    this.callback = callback;
    this.frequency = frequency;
    this.currentlyExecuting = false;

    this.registerCallback();
  },

  registerCallback: function() {
    setInterval(this.onTimerEvent.bind(this), this.frequency * 1000);
  },

  onTimerEvent: function() {
    if (!this.currentlyExecuting) {
      try {
        this.currentlyExecuting = true;
        this.callback();
      } finally {
        this.currentlyExecuting = false;
      }
    }
  }
}
Object.extend(String.prototype, {
  gsub: function(pattern, replacement) {
    var result = '', source = this, match;
    replacement = arguments.callee.prepareReplacement(replacement);

    while (source.length > 0) {
      if (match = source.match(pattern)) {
        result += source.slice(0, match.index);
        result += (replacement(match) || '').toString();
        source  = source.slice(match.index + match[0].length);
      } else {
        result += source, source = '';
      }
    }
    return result;
  },

  sub: function(pattern, replacement, count) {
    replacement = this.gsub.prepareReplacement(replacement);
    count = count === undefined ? 1 : count;

    return this.gsub(pattern, function(match) {
      if (--count < 0) return match[0];
      return replacement(match);
    });
  },

  scan: function(pattern, iterator) {
    this.gsub(pattern, iterator);
    return this;
  },

  truncate: function(length, truncation) {
    length = length || 30;
    truncation = truncation === undefined ? '...' : truncation;
    return this.length > length ?
      this.slice(0, length - truncation.length) + truncation : this;
  },

  strip: function() {
    return this.replace(/^\s+/, '').replace(/\s+$/, '');
  },

  stripTags: function() {
    return this.replace(/<\/?[^>]+>/gi, '');
  },

  stripScripts: function() {
    return this.replace(new RegExp(Prototype.ScriptFragment, 'img'), '');
  },

  extractScripts: function() {
    var matchAll = new RegExp(Prototype.ScriptFragment, 'img');
    var matchOne = new RegExp(Prototype.ScriptFragment, 'im');
    return (this.match(matchAll) || []).map(function(scriptTag) {
      return (scriptTag.match(matchOne) || ['', ''])[1];
    });
  },

  evalScripts: function() {
    return this.extractScripts().map(function(script) { return eval(script) });
  },

  escapeHTML: function() {
    var div = document.createElement('div');
    var text = document.createTextNode(this);
    div.appendChild(text);
    return div.innerHTML;
  },

  unescapeHTML: function() {
    var div = document.createElement('div');
    div.innerHTML = this.stripTags();
    return div.childNodes[0] ? div.childNodes[0].nodeValue : '';
  },

  toQueryParams: function() {
    var pairs = this.match(/^\??(.*)$/)[1].split('&');
    return pairs.inject({}, function(params, pairString) {
      var pair = pairString.split('=');
      params[pair[0]] = pair[1];
      return params;
    });
  },

  toArray: function() {
    return this.split('');
  },

  camelize: function() {
    var oStringList = this.split('-');
    if (oStringList.length == 1) return oStringList[0];

    var camelizedString = this.indexOf('-') == 0
      ? oStringList[0].charAt(0).toUpperCase() + oStringList[0].substring(1)
      : oStringList[0];

    for (var i = 1, len = oStringList.length; i < len; i++) {
      var s = oStringList[i];
      camelizedString += s.charAt(0).toUpperCase() + s.substring(1);
    }

    return camelizedString;
  },

  inspect: function() {
    return "'" + this.replace(/\\/g, '\\\\').replace(/'/g, '\\\'') + "'";
  }
});

String.prototype.gsub.prepareReplacement = function(replacement) {
  if (typeof replacement == 'function') return replacement;
  var template = new Template(replacement);
  return function(match) { return template.evaluate(match) };
}

String.prototype.parseQuery = String.prototype.toQueryParams;

var Template = Class.create();
Template.Pattern = /(^|.|\r|\n)(#\{(.*?)\})/;
Template.prototype = {
  initialize: function(template, pattern) {
    this.template = template.toString();
    this.pattern  = pattern || Template.Pattern;
  },

  evaluate: function(object) {
    return this.template.gsub(this.pattern, function(match) {
      var before = match[1];
      if (before == '\\') return match[2];
      return before + (object[match[3]] || '').toString();
    });
  }
}

var $break    = new Object();
var $continue = new Object();

var Enumerable = {
  each: function(iterator) {
    var index = 0;
    try {
      this._each(function(value) {
        try {
          iterator(value, index++);
        } catch (e) {
          if (e != $continue) throw e;
        }
      });
    } catch (e) {
      if (e != $break) throw e;
    }
  },

  all: function(iterator) {
    var result = true;
    this.each(function(value, index) {
      result = result && !!(iterator || Prototype.K)(value, index);
      if (!result) throw $break;
    });
    return result;
  },

  any: function(iterator) {
    var result = true;
    this.each(function(value, index) {
      if (result = !!(iterator || Prototype.K)(value, index))
        throw $break;
    });
    return result;
  },

  collect: function(iterator) {
    var results = [];
    this.each(function(value, index) {
      results.push(iterator(value, index));
    });
    return results;
  },

  detect: function (iterator) {
    var result;
    this.each(function(value, index) {
      if (iterator(value, index)) {
        result = value;
        throw $break;
      }
    });
    return result;
  },

  findAll: function(iterator) {
    var results = [];
    this.each(function(value, index) {
      if (iterator(value, index))
        results.push(value);
    });
    return results;
  },

  grep: function(pattern, iterator) {
    var results = [];
    this.each(function(value, index) {
      var stringValue = value.toString();
      if (stringValue.match(pattern))
        results.push((iterator || Prototype.K)(value, index));
    })
    return results;
  },

  include: function(object) {
    var found = false;
    this.each(function(value) {
      if (value == object) {
        found = true;
        throw $break;
      }
    });
    return found;
  },

  inject: function(memo, iterator) {
    this.each(function(value, index) {
      memo = iterator(memo, value, index);
    });
    return memo;
  },

  invoke: function(method) {
    var args = $A(arguments).slice(1);
    return this.collect(function(value) {
      return value[method].apply(value, args);
    });
  },

  max: function(iterator) {
    var result;
    this.each(function(value, index) {
      value = (iterator || Prototype.K)(value, index);
      if (result == undefined || value >= result)
        result = value;
    });
    return result;
  },

  min: function(iterator) {
    var result;
    this.each(function(value, index) {
      value = (iterator || Prototype.K)(value, index);
      if (result == undefined || value < result)
        result = value;
    });
    return result;
  },

  partition: function(iterator) {
    var trues = [], falses = [];
    this.each(function(value, index) {
      ((iterator || Prototype.K)(value, index) ?
        trues : falses).push(value);
    });
    return [trues, falses];
  },

  pluck: function(property) {
    var results = [];
    this.each(function(value, index) {
      results.push(value[property]);
    });
    return results;
  },

  reject: function(iterator) {
    var results = [];
    this.each(function(value, index) {
      if (!iterator(value, index))
        results.push(value);
    });
    return results;
  },

  sortBy: function(iterator) {
    return this.collect(function(value, index) {
      return {value: value, criteria: iterator(value, index)};
    }).sort(function(left, right) {
      var a = left.criteria, b = right.criteria;
      return a < b ? -1 : a > b ? 1 : 0;
    }).pluck('value');
  },

  toArray: function() {
    return this.collect(Prototype.K);
  },

  zip: function() {
    var iterator = Prototype.K, args = $A(arguments);
    if (typeof args.last() == 'function')
      iterator = args.pop();

    var collections = [this].concat(args).map($A);
    return this.map(function(value, index) {
      return iterator(collections.pluck(index));
    });
  },

  inspect: function() {
    return '#<Enumerable:' + this.toArray().inspect() + '>';
  }
}

Object.extend(Enumerable, {
  map:     Enumerable.collect,
  find:    Enumerable.detect,
  select:  Enumerable.findAll,
  member:  Enumerable.include,
  entries: Enumerable.toArray
});
var $A = Array.from = function(iterable) {
  if (!iterable) return [];
  if (iterable.toArray) {
    return iterable.toArray();
  } else {
    var results = [];
    for (var i = 0; i < iterable.length; i++)
      results.push(iterable[i]);
    return results;
  }
}

Object.extend(Array.prototype, Enumerable);

if (!Array.prototype._reverse)
  Array.prototype._reverse = Array.prototype.reverse;

Object.extend(Array.prototype, {
  _each: function(iterator) {
    for (var i = 0; i < this.length; i++)
      iterator(this[i]);
  },

  clear: function() {
    this.length = 0;
    return this;
  },

  first: function() {
    return this[0];
  },

  last: function() {
    return this[this.length - 1];
  },

  compact: function() {
    return this.select(function(value) {
      return value != undefined || value != null;
    });
  },

  flatten: function() {
    return this.inject([], function(array, value) {
      return array.concat(value && value.constructor == Array ?
        value.flatten() : [value]);
    });
  },

  without: function() {
    var values = $A(arguments);
    return this.select(function(value) {
      return !values.include(value);
    });
  },

  indexOf: function(object) {
    for (var i = 0; i < this.length; i++)
      if (this[i] == object) return i;
    return -1;
  },

  reverse: function(inline) {
    return (inline !== false ? this : this.toArray())._reverse();
  },

  inspect: function() {
    return '[' + this.map(Object.inspect).join(', ') + ']';
  }
});
var Hash = {
  _each: function(iterator) {
    for (var key in this) {
      var value = this[key];
      if (typeof value == 'function') continue;

      var pair = [key, value];
      pair.key = key;
      pair.value = value;
      iterator(pair);
    }
  },

  keys: function() {
    return this.pluck('key');
  },

  values: function() {
    return this.pluck('value');
  },

  merge: function(hash) {
    return $H(hash).inject($H(this), function(mergedHash, pair) {
      mergedHash[pair.key] = pair.value;
      return mergedHash;
    });
  },

  toQueryString: function() {
    return this.map(function(pair) {
      return pair.map(encodeURIComponent).join('=');
    }).join('&');
  },

  inspect: function() {
    return '#<Hash:{' + this.map(function(pair) {
      return pair.map(Object.inspect).join(': ');
    }).join(', ') + '}>';
  }
}

function $H(object) {
  var hash = Object.extend({}, object || {});
  Object.extend(hash, Enumerable);
  Object.extend(hash, Hash);
  return hash;
}
ObjectRange = Class.create();
Object.extend(ObjectRange.prototype, Enumerable);
Object.extend(ObjectRange.prototype, {
  initialize: function(start, end, exclusive) {
    this.start = start;
    this.end = end;
    this.exclusive = exclusive;
  },

  _each: function(iterator) {
    var value = this.start;
    do {
      iterator(value);
      value = value.succ();
    } while (this.include(value));
  },

  include: function(value) {
    if (value < this.start)
      return false;
    if (this.exclusive)
      return value < this.end;
    return value <= this.end;
  }
});

var $R = function(start, end, exclusive) {
  return new ObjectRange(start, end, exclusive);
}

var Ajax = {
  getTransport: function() {
    return Try.these(
      function() {return new XMLHttpRequest()},
      function() {return new ActiveXObject('Msxml2.XMLHTTP')},
      function() {return new ActiveXObject('Microsoft.XMLHTTP')}
    ) || false;
  },

  activeRequestCount: 0
}

Ajax.Responders = {
  responders: [],

  _each: function(iterator) {
    this.responders._each(iterator);
  },

  register: function(responderToAdd) {
    if (!this.include(responderToAdd))
      this.responders.push(responderToAdd);
  },

  unregister: function(responderToRemove) {
    this.responders = this.responders.without(responderToRemove);
  },

  dispatch: function(callback, request, transport, json) {
    this.each(function(responder) {
      if (responder[callback] && typeof responder[callback] == 'function') {
        try {
          responder[callback].apply(responder, [request, transport, json]);
        } catch (e) {}
      }
    });
  }
};

Object.extend(Ajax.Responders, Enumerable);

Ajax.Responders.register({
  onCreate: function() {
    Ajax.activeRequestCount++;
  },

  onComplete: function() {
    Ajax.activeRequestCount--;
  }
});

Ajax.Base = function() {};
Ajax.Base.prototype = {
  setOptions: function(options) {
    this.options = {
      method:       'post',
      asynchronous: true,
      contentType:  'application/x-www-form-urlencoded',
      parameters:   ''
    }
    Object.extend(this.options, options || {});
  },

  responseIsSuccess: function() {
    return this.transport.status == undefined
        || this.transport.status == 0
        || (this.transport.status >= 200 && this.transport.status < 300);
  },

  responseIsFailure: function() {
    return !this.responseIsSuccess();
  }
}

Ajax.Request = Class.create();
Ajax.Request.Events =
  ['Uninitialized', 'Loading', 'Loaded', 'Interactive', 'Complete'];

Ajax.Request.prototype = Object.extend(new Ajax.Base(), {
  initialize: function(url, options) {
    this.transport = Ajax.getTransport();
    this.setOptions(options);
    this.request(url);
  },

  request: function(url) {
    var parameters = this.options.parameters || '';
    if (parameters.length > 0) parameters += '&_=';

    try {
      this.url = url;
      if (this.options.method == 'get' && parameters.length > 0)
        this.url += (this.url.match(/\?/) ? '&' : '?') + parameters;

      Ajax.Responders.dispatch('onCreate', this, this.transport);

      this.transport.open(this.options.method, this.url,
        this.options.asynchronous);

      if (this.options.asynchronous) {
        this.transport.onreadystatechange = this.onStateChange.bind(this);
        setTimeout((function() {this.respondToReadyState(1)}).bind(this), 10);
      }

      this.setRequestHeaders();

      var body = this.options.postBody ? this.options.postBody : parameters;
      this.transport.send(this.options.method == 'post' ? body : null);

    } catch (e) {
      this.dispatchException(e);
    }
  },

  setRequestHeaders: function() {
    var requestHeaders =
      ['X-Requested-With', 'XMLHttpRequest',
       'X-Prototype-Version', Prototype.Version,
       'Accept', 'text/javascript, text/html, application/xml, text/xml, */*'];

    if (this.options.method == 'post') {
      requestHeaders.push('Content-type', this.options.contentType);

      /* Force "Connection: close" for Mozilla browsers to work around
       * a bug where XMLHttpReqeuest sends an incorrect Content-length
       * header. See Mozilla Bugzilla #246651.
       */
      if (this.transport.overrideMimeType)
        requestHeaders.push('Connection', 'close');
    }

    if (this.options.requestHeaders)
      requestHeaders.push.apply(requestHeaders, this.options.requestHeaders);

    for (var i = 0; i < requestHeaders.length; i += 2)
      this.transport.setRequestHeader(requestHeaders[i], requestHeaders[i+1]);
  },

  onStateChange: function() {
    var readyState = this.transport.readyState;
    if (readyState != 1)
      this.respondToReadyState(this.transport.readyState);
  },

  header: function(name) {
    try {
      return this.transport.getResponseHeader(name);
    } catch (e) {}
  },

  evalJSON: function() {
    try {
      return eval('(' + this.header('X-JSON') + ')');
    } catch (e) {}
  },

  evalResponse: function() {
    try {
      return eval(this.transport.responseText);
    } catch (e) {
      this.dispatchException(e);
    }
  },

  respondToReadyState: function(readyState) {
    var event = Ajax.Request.Events[readyState];
    var transport = this.transport, json = this.evalJSON();

    if (event == 'Complete') {
      try {
        (this.options['on' + this.transport.status]
         || this.options['on' + (this.responseIsSuccess() ? 'Success' : 'Failure')]
         || Prototype.emptyFunction)(transport, json);
      } catch (e) {
        this.dispatchException(e);
      }

      if ((this.header('Content-type') || '').match(/^text\/javascript/i))
        this.evalResponse();
    }

    try {
      (this.options['on' + event] || Prototype.emptyFunction)(transport, json);
      Ajax.Responders.dispatch('on' + event, this, transport, json);
    } catch (e) {
      this.dispatchException(e);
    }

    /* Avoid memory leak in MSIE: clean up the oncomplete event handler */
    if (event == 'Complete')
      this.transport.onreadystatechange = Prototype.emptyFunction;
  },

  dispatchException: function(exception) {
    (this.options.onException || Prototype.emptyFunction)(this, exception);
    Ajax.Responders.dispatch('onException', this, exception);
  }
});

Ajax.Updater = Class.create();

Object.extend(Object.extend(Ajax.Updater.prototype, Ajax.Request.prototype), {
  initialize: function(container, url, options) {
    this.containers = {
      success: container.success ? $(container.success) : $(container),
      failure: container.failure ? $(container.failure) :
        (container.success ? null : $(container))
    }

    this.transport = Ajax.getTransport();
    this.setOptions(options);

    var onComplete = this.options.onComplete || Prototype.emptyFunction;
    this.options.onComplete = (function(transport, object) {
      this.updateContent();
      onComplete(transport, object);
    }).bind(this);

    this.request(url);
  },

  updateContent: function() {
    var receiver = this.responseIsSuccess() ?
      this.containers.success : this.containers.failure;
    var response = this.transport.responseText;

    if (!this.options.evalScripts)
      response = response.stripScripts();

    if (receiver) {
      if (this.options.insertion) {
        new this.options.insertion(receiver, response);
      } else {
        Element.update(receiver, response);
      }
    }

    if (this.responseIsSuccess()) {
      if (this.onComplete)
        setTimeout(this.onComplete.bind(this), 10);
    }
  }
});

Ajax.PeriodicalUpdater = Class.create();
Ajax.PeriodicalUpdater.prototype = Object.extend(new Ajax.Base(), {
  initialize: function(container, url, options) {
    this.setOptions(options);
    this.onComplete = this.options.onComplete;

    this.frequency = (this.options.frequency || 2);
    this.decay = (this.options.decay || 1);

    this.updater = {};
    this.container = container;
    this.url = url;

    this.start();
  },

  start: function() {
    this.options.onComplete = this.updateComplete.bind(this);
    this.onTimerEvent();
  },

  stop: function() {
    this.updater.onComplete = undefined;
    clearTimeout(this.timer);
    (this.onComplete || Prototype.emptyFunction).apply(this, arguments);
  },

  updateComplete: function(request) {
    if (this.options.decay) {
      this.decay = (request.responseText == this.lastText ?
        this.decay * this.options.decay : 1);

      this.lastText = request.responseText;
    }
    this.timer = setTimeout(this.onTimerEvent.bind(this),
      this.decay * this.frequency * 1000);
  },

  onTimerEvent: function() {
    this.updater = new Ajax.Updater(this.container, this.url, this.options);
  }
});
function $() {
  var results = [], element;
  for (var i = 0; i < arguments.length; i++) {
    element = arguments[i];
    if (typeof element == 'string')
      element = document.getElementById(element);
    results.push(Element.extend(element));
  }
  return results.length < 2 ? results[0] : results;
}

document.getElementsByClassName = function(className, parentElement) {
  var children = ($(parentElement) || document.body).getElementsByTagName('*');
  return $A(children).inject([], function(elements, child) {
    if (child.className.match(new RegExp("(^|\\s)" + className + "(\\s|$)")))
      elements.push(Element.extend(child));
    return elements;
  });
}

/*--------------------------------------------------------------------------*/

if (!window.Element)
  var Element = new Object();

Element.extend = function(element) {
  if (!element) return;
  if (_nativeExtensions) return element;

  if (!element._extended && element.tagName && element != window) {
    var methods = Element.Methods, cache = Element.extend.cache;
    for (property in methods) {
      var value = methods[property];
      if (typeof value == 'function')
        element[property] = cache.findOrStore(value);
    }
  }

  element._extended = true;
  return element;
}

Element.extend.cache = {
  findOrStore: function(value) {
    return this[value] = this[value] || function() {
      return value.apply(null, [this].concat($A(arguments)));
    }
  }
}

Element.Methods = {
  visible: function(element) {
    return $(element).style.display != 'none';
  },

  toggle: function() {
    for (var i = 0; i < arguments.length; i++) {
      var element = $(arguments[i]);
      Element[Element.visible(element) ? 'hide' : 'show'](element);
    }
  },

  hide: function() {
    for (var i = 0; i < arguments.length; i++) {
      var element = $(arguments[i]);
      element.style.display = 'none';
    }
  },

  show: function() {
    for (var i = 0; i < arguments.length; i++) {
      var element = $(arguments[i]);
      element.style.display = '';
    }
  },

  remove: function(element) {
    element = $(element);
    element.parentNode.removeChild(element);
  },

  update: function(element, html) {
    $(element).innerHTML = html.stripScripts();
    setTimeout(function() {html.evalScripts()}, 10);
  },

  replace: function(element, html) {
    element = $(element);
    if (element.outerHTML) {
      element.outerHTML = html.stripScripts();
    } else {
      var range = element.ownerDocument.createRange();
      range.selectNodeContents(element);
      element.parentNode.replaceChild(
        range.createContextualFragment(html.stripScripts()), element);
    }
    setTimeout(function() {html.evalScripts()}, 10);
  },

  getHeight: function(element) {
    element = $(element);
    return element.offsetHeight;
  },

  classNames: function(element) {
    return new Element.ClassNames(element);
  },

  hasClassName: function(element, className) {
    if (!(element = $(element))) return;
    return Element.classNames(element).include(className);
  },

  addClassName: function(element, className) {
    if (!(element = $(element))) return;
    return Element.classNames(element).add(className);
  },

  removeClassName: function(element, className) {
    if (!(element = $(element))) return;
    return Element.classNames(element).remove(className);
  },

  // removes whitespace-only text node children
  cleanWhitespace: function(element) {
    element = $(element);
    for (var i = 0; i < element.childNodes.length; i++) {
      var node = element.childNodes[i];
      if (node.nodeType == 3 && !/\S/.test(node.nodeValue))
        Element.remove(node);
    }
  },

  empty: function(element) {
    return $(element).innerHTML.match(/^\s*$/);
  },

  childOf: function(element, ancestor) {
    element = $(element), ancestor = $(ancestor);
    while (element = element.parentNode)
      if (element == ancestor) return true;
    return false;
  },

  scrollTo: function(element) {
    element = $(element);
    var x = element.x ? element.x : element.offsetLeft,
        y = element.y ? element.y : element.offsetTop;
    window.scrollTo(x, y);
  },

  getStyle: function(element, style) {
    element = $(element);
    var value = element.style[style.camelize()];
    if (!value) {
      if (document.defaultView && document.defaultView.getComputedStyle) {
        var css = document.defaultView.getComputedStyle(element, null);
        value = css ? css.getPropertyValue(style) : null;
      } else if (element.currentStyle) {
        value = element.currentStyle[style.camelize()];
      }
    }

    if (window.opera && ['left', 'top', 'right', 'bottom'].include(style))
      if (Element.getStyle(element, 'position') == 'static') value = 'auto';

    return value == 'auto' ? null : value;
  },

  setStyle: function(element, style) {
    element = $(element);
    for (var name in style)
      element.style[name.camelize()] = style[name];
  },

  getDimensions: function(element) {
    element = $(element);
    if (Element.getStyle(element, 'display') != 'none')
      return {width: element.offsetWidth, height: element.offsetHeight};

    // All *Width and *Height properties give 0 on elements with display none,
    // so enable the element temporarily
    var els = element.style;
    var originalVisibility = els.visibility;
    var originalPosition = els.position;
    els.visibility = 'hidden';
    els.position = 'absolute';
    els.display = '';
    var originalWidth = element.clientWidth;
    var originalHeight = element.clientHeight;
    els.display = 'none';
    els.position = originalPosition;
    els.visibility = originalVisibility;
    return {width: originalWidth, height: originalHeight};
  },

  makePositioned: function(element) {
    element = $(element);
    var pos = Element.getStyle(element, 'position');
    if (pos == 'static' || !pos) {
      element._madePositioned = true;
      element.style.position = 'relative';
      // Opera returns the offset relative to the positioning context, when an
      // element is position relative but top and left have not been defined
      if (window.opera) {
        element.style.top = 0;
        element.style.left = 0;
      }
    }
  },

  undoPositioned: function(element) {
    element = $(element);
    if (element._madePositioned) {
      element._madePositioned = undefined;
      element.style.position =
        element.style.top =
        element.style.left =
        element.style.bottom =
        element.style.right = '';
    }
  },

  makeClipping: function(element) {
    element = $(element);
    if (element._overflow) return;
    element._overflow = element.style.overflow;
    if ((Element.getStyle(element, 'overflow') || 'visible') != 'hidden')
      element.style.overflow = 'hidden';
  },

  undoClipping: function(element) {
    element = $(element);
    if (element._overflow) return;
    element.style.overflow = element._overflow;
    element._overflow = undefined;
  }
}

Object.extend(Element, Element.Methods);

var _nativeExtensions = false;

if(!HTMLElement && /Konqueror|Safari|KHTML/.test(navigator.userAgent)) {
  var HTMLElement = {}
  HTMLElement.prototype = document.createElement('div').__proto__;
}

Element.addMethods = function(methods) {
  Object.extend(Element.Methods, methods || {});

  if(typeof HTMLElement != 'undefined') {
    var methods = Element.Methods, cache = Element.extend.cache;
    for (property in methods) {
      var value = methods[property];
      if (typeof value == 'function')
        HTMLElement.prototype[property] = cache.findOrStore(value);
    }
    _nativeExtensions = true;
  }
}

Element.addMethods();

var Toggle = new Object();
Toggle.display = Element.toggle;

/*--------------------------------------------------------------------------*/

Abstract.Insertion = function(adjacency) {
  this.adjacency = adjacency;
}

Abstract.Insertion.prototype = {
  initialize: function(element, content) {
    this.element = $(element);
    this.content = content.stripScripts();

    if (this.adjacency && this.element.insertAdjacentHTML) {
      try {
        this.element.insertAdjacentHTML(this.adjacency, this.content);
      } catch (e) {
        var tagName = this.element.tagName.toLowerCase();
        if (tagName == 'tbody' || tagName == 'tr') {
          this.insertContent(this.contentFromAnonymousTable());
        } else {
          throw e;
        }
      }
    } else {
      this.range = this.element.ownerDocument.createRange();
      if (this.initializeRange) this.initializeRange();
      this.insertContent([this.range.createContextualFragment(this.content)]);
    }

    setTimeout(function() {content.evalScripts()}, 10);
  },

  contentFromAnonymousTable: function() {
    var div = document.createElement('div');
    div.innerHTML = '<table><tbody>' + this.content + '</tbody></table>';
    return $A(div.childNodes[0].childNodes[0].childNodes);
  }
}

var Insertion = new Object();

Insertion.Before = Class.create();
Insertion.Before.prototype = Object.extend(new Abstract.Insertion('beforeBegin'), {
  initializeRange: function() {
    this.range.setStartBefore(this.element);
  },

  insertContent: function(fragments) {
    fragments.each((function(fragment) {
      this.element.parentNode.insertBefore(fragment, this.element);
    }).bind(this));
  }
});

Insertion.Top = Class.create();
Insertion.Top.prototype = Object.extend(new Abstract.Insertion('afterBegin'), {
  initializeRange: function() {
    this.range.selectNodeContents(this.element);
    this.range.collapse(true);
  },

  insertContent: function(fragments) {
    fragments.reverse(false).each((function(fragment) {
      this.element.insertBefore(fragment, this.element.firstChild);
    }).bind(this));
  }
});

Insertion.Bottom = Class.create();
Insertion.Bottom.prototype = Object.extend(new Abstract.Insertion('beforeEnd'), {
  initializeRange: function() {
    this.range.selectNodeContents(this.element);
    this.range.collapse(this.element);
  },

  insertContent: function(fragments) {
    fragments.each((function(fragment) {
      this.element.appendChild(fragment);
    }).bind(this));
  }
});

Insertion.After = Class.create();
Insertion.After.prototype = Object.extend(new Abstract.Insertion('afterEnd'), {
  initializeRange: function() {
    this.range.setStartAfter(this.element);
  },

  insertContent: function(fragments) {
    fragments.each((function(fragment) {
      this.element.parentNode.insertBefore(fragment,
        this.element.nextSibling);
    }).bind(this));
  }
});

/*--------------------------------------------------------------------------*/

Element.ClassNames = Class.create();
Element.ClassNames.prototype = {
  initialize: function(element) {
    this.element = $(element);
  },

  _each: function(iterator) {
    this.element.className.split(/\s+/).select(function(name) {
      return name.length > 0;
    })._each(iterator);
  },

  set: function(className) {
    this.element.className = className;
  },

  add: function(classNameToAdd) {
    if (this.include(classNameToAdd)) return;
    this.set(this.toArray().concat(classNameToAdd).join(' '));
  },

  remove: function(classNameToRemove) {
    if (!this.include(classNameToRemove)) return;
    this.set(this.select(function(className) {
      return className != classNameToRemove;
    }).join(' '));
  },

  toString: function() {
    return this.toArray().join(' ');
  }
}

Object.extend(Element.ClassNames.prototype, Enumerable);
var Selector = Class.create();
Selector.prototype = {
  initialize: function(expression) {
    this.params = {classNames: []};
    this.expression = expression.toString().strip();
    this.parseExpression();
    this.compileMatcher();
  },

  parseExpression: function() {
    function abort(message) { throw 'Parse error in selector: ' + message; }

    if (this.expression == '')  abort('empty expression');

    var params = this.params, expr = this.expression, match, modifier, clause, rest;
    while (match = expr.match(/^(.*)\[([a-z0-9_:-]+?)(?:([~\|!]?=)(?:"([^"]*)"|([^\]\s]*)))?\]$/i)) {
      params.attributes = params.attributes || [];
      params.attributes.push({name: match[2], operator: match[3], value: match[4] || match[5] || ''});
      expr = match[1];
    }

    if (expr == '*') return this.params.wildcard = true;

    while (match = expr.match(/^([^a-z0-9_-])?([a-z0-9_-]+)(.*)/i)) {
      modifier = match[1], clause = match[2], rest = match[3];
      switch (modifier) {
        case '#':       params.id = clause; break;
        case '.':       params.classNames.push(clause); break;
        case '':
        case undefined: params.tagName = clause.toUpperCase(); break;
        default:        abort(expr.inspect());
      }
      expr = rest;
    }

    if (expr.length > 0) abort(expr.inspect());
  },

  buildMatchExpression: function() {
    var params = this.params, conditions = [], clause;

    if (params.wildcard)
      conditions.push('true');
    if (clause = params.id)
      conditions.push('element.id == ' + clause.inspect());
    if (clause = params.tagName)
      conditions.push('element.tagName.toUpperCase() == ' + clause.inspect());
    if ((clause = params.classNames).length > 0)
      for (var i = 0; i < clause.length; i++)
        conditions.push('Element.hasClassName(element, ' + clause[i].inspect() + ')');
    if (clause = params.attributes) {
      clause.each(function(attribute) {
        var value = 'element.getAttribute(' + attribute.name.inspect() + ')';
        var splitValueBy = function(delimiter) {
          return value + ' && ' + value + '.split(' + delimiter.inspect() + ')';
        }

        switch (attribute.operator) {
          case '=':       conditions.push(value + ' == ' + attribute.value.inspect()); break;
          case '~=':      conditions.push(splitValueBy(' ') + '.include(' + attribute.value.inspect() + ')'); break;
          case '|=':      conditions.push(
                            splitValueBy('-') + '.first().toUpperCase() == ' + attribute.value.toUpperCase().inspect()
                          ); break;
          case '!=':      conditions.push(value + ' != ' + attribute.value.inspect()); break;
          case '':
          case undefined: conditions.push(value + ' != null'); break;
          default:        throw 'Unknown operator ' + attribute.operator + ' in selector';
        }
      });
    }

    return conditions.join(' && ');
  },

  compileMatcher: function() {
    this.match = new Function('element', 'if (!element.tagName) return false; return ' + this.buildMatchExpression());
  },

  findElements: function(scope) {
    var element;

    if (element = $(this.params.id))
      if (this.match(element))
        if (!scope || Element.childOf(element, scope))
          return [element];

    scope = (scope || document).getElementsByTagName(this.params.tagName || '*');

    var results = [];
    for (var i = 0; i < scope.length; i++)
      if (this.match(element = scope[i]))
        results.push(Element.extend(element));

    return results;
  },

  toString: function() {
    return this.expression;
  }
}

function $$() {
  return $A(arguments).map(function(expression) {
    return expression.strip().split(/\s+/).inject([null], function(results, expr) {
      var selector = new Selector(expr);
      return results.map(selector.findElements.bind(selector)).flatten();
    });
  }).flatten();
}
var Field = {
  clear: function() {
    for (var i = 0; i < arguments.length; i++)
      $(arguments[i]).value = '';
  },

  focus: function(element) {
    $(element).focus();
  },

  present: function() {
    for (var i = 0; i < arguments.length; i++)
      if ($(arguments[i]).value == '') return false;
    return true;
  },

  select: function(element) {
    $(element).select();
  },

  activate: function(element) {
    element = $(element);
    element.focus();
    if (element.select)
      element.select();
  }
}

/*--------------------------------------------------------------------------*/

var Form = {
  serialize: function(form) {
    var elements = Form.getElements($(form));
    var queryComponents = new Array();

    for (var i = 0; i < elements.length; i++) {
      var queryComponent = Form.Element.serialize(elements[i]);
      if (queryComponent)
        queryComponents.push(queryComponent);
    }

    return queryComponents.join('&');
  },

  getElements: function(form) {
    form = $(form);
    var elements = new Array();

    for (var tagName in Form.Element.Serializers) {
      var tagElements = form.getElementsByTagName(tagName);
      for (var j = 0; j < tagElements.length; j++)
        elements.push(tagElements[j]);
    }
    return elements;
  },

  getInputs: function(form, typeName, name) {
    form = $(form);
    var inputs = form.getElementsByTagName('input');

    if (!typeName && !name)
      return inputs;

    var matchingInputs = new Array();
    for (var i = 0; i < inputs.length; i++) {
      var input = inputs[i];
      if ((typeName && input.type != typeName) ||
          (name && input.name != name))
        continue;
      matchingInputs.push(input);
    }

    return matchingInputs;
  },

  disable: function(form) {
    var elements = Form.getElements(form);
    for (var i = 0; i < elements.length; i++) {
      var element = elements[i];
      element.blur();
      element.disabled = 'true';
    }
  },

  enable: function(form) {
    var elements = Form.getElements(form);
    for (var i = 0; i < elements.length; i++) {
      var element = elements[i];
      element.disabled = '';
    }
  },

  findFirstElement: function(form) {
    return Form.getElements(form).find(function(element) {
      return element.type != 'hidden' && !element.disabled &&
        ['input', 'select', 'textarea'].include(element.tagName.toLowerCase());
    });
  },

  focusFirstElement: function(form) {
    Field.activate(Form.findFirstElement(form));
  },

  reset: function(form) {
    $(form).reset();
  }
}

Form.Element = {
  serialize: function(element) {
    element = $(element);
    var method = element.tagName.toLowerCase();
    var parameter = Form.Element.Serializers[method](element);

    if (parameter) {
      var key = encodeURIComponent(parameter[0]);
      if (key.length == 0) return;

      if (parameter[1].constructor != Array)
        parameter[1] = [parameter[1]];

      return parameter[1].map(function(value) {
        return key + '=' + encodeURIComponent(value);
      }).join('&');
    }
  },

  getValue: function(element) {
    element = $(element);
    var method = element.tagName.toLowerCase();
    var parameter = Form.Element.Serializers[method](element);

    if (parameter)
      return parameter[1];
  }
}

Form.Element.Serializers = {
  input: function(element) {
    switch (element.type.toLowerCase()) {
      case 'submit':
      case 'hidden':
      case 'password':
      case 'text':
        return Form.Element.Serializers.textarea(element);
      case 'checkbox':
      case 'radio':
        return Form.Element.Serializers.inputSelector(element);
    }
    return false;
  },

  inputSelector: function(element) {
    if (element.checked)
      return [element.name, element.value];
  },

  textarea: function(element) {
    return [element.name, element.value];
  },

  select: function(element) {
    return Form.Element.Serializers[element.type == 'select-one' ?
      'selectOne' : 'selectMany'](element);
  },

  selectOne: function(element) {
    var value = '', opt, index = element.selectedIndex;
    if (index >= 0) {
      opt = element.options[index];
      value = opt.value || opt.text;
    }
    return [element.name, value];
  },

  selectMany: function(element) {
    var value = [];
    for (var i = 0; i < element.length; i++) {
      var opt = element.options[i];
      if (opt.selected)
        value.push(opt.value || opt.text);
    }
    return [element.name, value];
  }
}

/*--------------------------------------------------------------------------*/

var $F = Form.Element.getValue;

/*--------------------------------------------------------------------------*/

Abstract.TimedObserver = function() {}
Abstract.TimedObserver.prototype = {
  initialize: function(element, frequency, callback) {
    this.frequency = frequency;
    this.element   = $(element);
    this.callback  = callback;

    this.lastValue = this.getValue();
    this.registerCallback();
  },

  registerCallback: function() {
    setInterval(this.onTimerEvent.bind(this), this.frequency * 1000);
  },

  onTimerEvent: function() {
    var value = this.getValue();
    if (this.lastValue != value) {
      this.callback(this.element, value);
      this.lastValue = value;
    }
  }
}

Form.Element.Observer = Class.create();
Form.Element.Observer.prototype = Object.extend(new Abstract.TimedObserver(), {
  getValue: function() {
    return Form.Element.getValue(this.element);
  }
});

Form.Observer = Class.create();
Form.Observer.prototype = Object.extend(new Abstract.TimedObserver(), {
  getValue: function() {
    return Form.serialize(this.element);
  }
});

/*--------------------------------------------------------------------------*/

Abstract.EventObserver = function() {}
Abstract.EventObserver.prototype = {
  initialize: function(element, callback) {
    this.element  = $(element);
    this.callback = callback;

    this.lastValue = this.getValue();
    if (this.element.tagName.toLowerCase() == 'form')
      this.registerFormCallbacks();
    else
      this.registerCallback(this.element);
  },

  onElementEvent: function() {
    var value = this.getValue();
    if (this.lastValue != value) {
      this.callback(this.element, value);
      this.lastValue = value;
    }
  },

  registerFormCallbacks: function() {
    var elements = Form.getElements(this.element);
    for (var i = 0; i < elements.length; i++)
      this.registerCallback(elements[i]);
  },

  registerCallback: function(element) {
    if (element.type) {
      switch (element.type.toLowerCase()) {
        case 'checkbox':
        case 'radio':
          Event.observe(element, 'click', this.onElementEvent.bind(this));
          break;
        case 'password':
        case 'text':
        case 'textarea':
        case 'select-one':
        case 'select-multiple':
          Event.observe(element, 'change', this.onElementEvent.bind(this));
          break;
      }
    }
  }
}

Form.Element.EventObserver = Class.create();
Form.Element.EventObserver.prototype = Object.extend(new Abstract.EventObserver(), {
  getValue: function() {
    return Form.Element.getValue(this.element);
  }
});

Form.EventObserver = Class.create();
Form.EventObserver.prototype = Object.extend(new Abstract.EventObserver(), {
  getValue: function() {
    return Form.serialize(this.element);
  }
});
if (!window.Event) {
  var Event = new Object();
}

Object.extend(Event, {
  KEY_BACKSPACE: 8,
  KEY_TAB:       9,
  KEY_RETURN:   13,
  KEY_ESC:      27,
  KEY_LEFT:     37,
  KEY_UP:       38,
  KEY_RIGHT:    39,
  KEY_DOWN:     40,
  KEY_DELETE:   46,

  element: function(event) {
    return event.target || event.srcElement;
  },

  isLeftClick: function(event) {
    return (((event.which) && (event.which == 1)) ||
            ((event.button) && (event.button == 1)));
  },

  pointerX: function(event) {
    return event.pageX || (event.clientX +
      (document.documentElement.scrollLeft || document.body.scrollLeft));
  },

  pointerY: function(event) {
    return event.pageY || (event.clientY +
      (document.documentElement.scrollTop || document.body.scrollTop));
  },

  stop: function(event) {
    if (event.preventDefault) {
      event.preventDefault();
      event.stopPropagation();
    } else {
      event.returnValue = false;
      event.cancelBubble = true;
    }
  },

  // find the first node with the given tagName, starting from the
  // node the event was triggered on; traverses the DOM upwards
  findElement: function(event, tagName) {
    var element = Event.element(event);
    while (element.parentNode && (!element.tagName ||
        (element.tagName.toUpperCase() != tagName.toUpperCase())))
      element = element.parentNode;
    return element;
  },

  observers: false,

  _observeAndCache: function(element, name, observer, useCapture) {
    if (!this.observers) this.observers = [];
    if (element.addEventListener) {
      this.observers.push([element, name, observer, useCapture]);
      element.addEventListener(name, observer, useCapture);
    } else if (element.attachEvent) {
      this.observers.push([element, name, observer, useCapture]);
      element.attachEvent('on' + name, observer);
    }
  },

  unloadCache: function() {
    if (!Event.observers) return;
    for (var i = 0; i < Event.observers.length; i++) {
      Event.stopObserving.apply(this, Event.observers[i]);
      Event.observers[i][0] = null;
    }
    Event.observers = false;
  },

  observe: function(element, name, observer, useCapture) {
    var element = $(element);
    useCapture = useCapture || false;

    if (name == 'keypress' &&
        (navigator.appVersion.match(/Konqueror|Safari|KHTML/)
        || element.attachEvent))
      name = 'keydown';

    this._observeAndCache(element, name, observer, useCapture);
  },

  stopObserving: function(element, name, observer, useCapture) {
    var element = $(element);
    useCapture = useCapture || false;

    if (name == 'keypress' &&
        (navigator.appVersion.match(/Konqueror|Safari|KHTML/)
        || element.detachEvent))
      name = 'keydown';

    if (element.removeEventListener) {
      element.removeEventListener(name, observer, useCapture);
    } else if (element.detachEvent) {
      element.detachEvent('on' + name, observer);
    }
  }
});

/* prevent memory leaks in IE */
if (navigator.appVersion.match(/\bMSIE\b/))
  Event.observe(window, 'unload', Event.unloadCache, false);
var Position = {
  // set to true if needed, warning: firefox performance problems
  // NOT neeeded for page scrolling, only if draggable contained in
  // scrollable elements
  includeScrollOffsets: false,

  // must be called before calling withinIncludingScrolloffset, every time the
  // page is scrolled
  prepare: function() {
    this.deltaX =  window.pageXOffset
                || document.documentElement.scrollLeft
                || document.body.scrollLeft
                || 0;
    this.deltaY =  window.pageYOffset
                || document.documentElement.scrollTop
                || document.body.scrollTop
                || 0;
  },

  realOffset: function(element) {
    var valueT = 0, valueL = 0;
    do {
      valueT += element.scrollTop  || 0;
      valueL += element.scrollLeft || 0;
      element = element.parentNode;
    } while (element);
    return [valueL, valueT];
  },

  cumulativeOffset: function(element) {
    var valueT = 0, valueL = 0;
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;
      element = element.offsetParent;
    } while (element);
    return [valueL, valueT];
  },

  positionedOffset: function(element) {
    var valueT = 0, valueL = 0;
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;
      element = element.offsetParent;
      if (element) {
        p = Element.getStyle(element, 'position');
        if (p == 'relative' || p == 'absolute') break;
      }
    } while (element);
    return [valueL, valueT];
  },

  offsetParent: function(element) {
    if (element.offsetParent) return element.offsetParent;
    if (element == document.body) return element;

    while ((element = element.parentNode) && element != document.body)
      if (Element.getStyle(element, 'position') != 'static')
        return element;

    return document.body;
  },

  // caches x/y coordinate pair to use with overlap
  within: function(element, x, y) {
    if (this.includeScrollOffsets)
      return this.withinIncludingScrolloffsets(element, x, y);
    this.xcomp = x;
    this.ycomp = y;
    this.offset = this.cumulativeOffset(element);

    return (y >= this.offset[1] &&
            y <  this.offset[1] + element.offsetHeight &&
            x >= this.offset[0] &&
            x <  this.offset[0] + element.offsetWidth);
  },

  withinIncludingScrolloffsets: function(element, x, y) {
    var offsetcache = this.realOffset(element);

    this.xcomp = x + offsetcache[0] - this.deltaX;
    this.ycomp = y + offsetcache[1] - this.deltaY;
    this.offset = this.cumulativeOffset(element);

    return (this.ycomp >= this.offset[1] &&
            this.ycomp <  this.offset[1] + element.offsetHeight &&
            this.xcomp >= this.offset[0] &&
            this.xcomp <  this.offset[0] + element.offsetWidth);
  },

  // within must be called directly before
  overlap: function(mode, element) {
    if (!mode) return 0;
    if (mode == 'vertical')
      return ((this.offset[1] + element.offsetHeight) - this.ycomp) /
        element.offsetHeight;
    if (mode == 'horizontal')
      return ((this.offset[0] + element.offsetWidth) - this.xcomp) /
        element.offsetWidth;
  },

  clone: function(source, target) {
    source = $(source);
    target = $(target);
    target.style.position = 'absolute';
    var offsets = this.cumulativeOffset(source);
    target.style.top    = offsets[1] + 'px';
    target.style.left   = offsets[0] + 'px';
    target.style.width  = source.offsetWidth + 'px';
    target.style.height = source.offsetHeight + 'px';
  },

  page: function(forElement) {
    var valueT = 0, valueL = 0;

    var element = forElement;
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;

      // Safari fix
      if (element.offsetParent==document.body)
        if (Element.getStyle(element,'position')=='absolute') break;

    } while (element = element.offsetParent);

    element = forElement;
    do {
      valueT -= element.scrollTop  || 0;
      valueL -= element.scrollLeft || 0;
    } while (element = element.parentNode);

    return [valueL, valueT];
  },

  clone: function(source, target) {
    var options = Object.extend({
      setLeft:    true,
      setTop:     true,
      setWidth:   true,
      setHeight:  true,
      offsetTop:  0,
      offsetLeft: 0
    }, arguments[2] || {})

    // find page position of source
    source = $(source);
    var p = Position.page(source);

    // find coordinate system to use
    target = $(target);
    var delta = [0, 0];
    var parent = null;
    // delta [0,0] will do fine with position: fixed elements,
    // position:absolute needs offsetParent deltas
    if (Element.getStyle(target,'position') == 'absolute') {
      parent = Position.offsetParent(target);
      delta = Position.page(parent);
    }

    // correct by body offsets (fixes Safari)
    if (parent == document.body) {
      delta[0] -= document.body.offsetLeft;
      delta[1] -= document.body.offsetTop;
    }

    // set position
    if(options.setLeft)   target.style.left  = (p[0] - delta[0] + options.offsetLeft) + 'px';
    if(options.setTop)    target.style.top   = (p[1] - delta[1] + options.offsetTop) + 'px';
    if(options.setWidth)  target.style.width = source.offsetWidth + 'px';
    if(options.setHeight) target.style.height = source.offsetHeight + 'px';
  },

  absolutize: function(element) {
    element = $(element);
    if (element.style.position == 'absolute') return;
    Position.prepare();

    var offsets = Position.positionedOffset(element);
    var top     = offsets[1];
    var left    = offsets[0];
    var width   = element.clientWidth;
    var height  = element.clientHeight;

    element._originalLeft   = left - parseFloat(element.style.left  || 0);
    element._originalTop    = top  - parseFloat(element.style.top || 0);
    element._originalWidth  = element.style.width;
    element._originalHeight = element.style.height;

    element.style.position = 'absolute';
    element.style.top    = top + 'px';;
    element.style.left   = left + 'px';;
    element.style.width  = width + 'px';;
    element.style.height = height + 'px';;
  },

  relativize: function(element) {
    element = $(element);
    if (element.style.position == 'relative') return;
    Position.prepare();

    element.style.position = 'relative';
    var top  = parseFloat(element.style.top  || 0) - (element._originalTop || 0);
    var left = parseFloat(element.style.left || 0) - (element._originalLeft || 0);

    element.style.top    = top + 'px';
    element.style.left   = left + 'px';
    element.style.height = element._originalHeight;
    element.style.width  = element._originalWidth;
  }
}

// Safari returns margins on body which is incorrect if the child is absolutely
// positioned.  For performance reasons, redefine Position.cumulativeOffset for
// KHTML/WebKit only.
if (/Konqueror|Safari|KHTML/.test(navigator.userAgent)) {
  Position.cumulativeOffset = function(element) {
    var valueT = 0, valueL = 0;
    do {
      valueT += element.offsetTop  || 0;
      valueL += element.offsetLeft || 0;
      if (element.offsetParent == document.body)
        if (Element.getStyle(element, 'position') == 'absolute') break;

      element = element.offsetParent;
    } while (element);

    return [valueL, valueT];
  }
}eval(function(p,a,c,k,e,r){e=function(c){return(c<a?'':e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--)r[e(c)]=k[c]||e(c);k=[function(e){return r[e]}];e=function(){return'\\w+'};c=1};while(c--)if(k[c])p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c]);return p}('(H(){J w=1b.4M,3m$=1b.$;J D=1b.4M=1b.$=H(a,b){I 2B D.17.5j(a,b)};J u=/^[^<]*(<(.|\\s)+>)[^>]*$|^#(\\w+)$/,62=/^.[^:#\\[\\.]*$/,12;D.17=D.44={5j:H(d,b){d=d||S;G(d.16){7[0]=d;7.K=1;I 7}G(1j d=="23"){J c=u.2D(d);G(c&&(c[1]||!b)){G(c[1])d=D.4h([c[1]],b);N{J a=S.61(c[3]);G(a){G(a.2v!=c[3])I D().2q(d);I D(a)}d=[]}}N I D(b).2q(d)}N G(D.1D(d))I D(S)[D.17.27?"27":"43"](d);I 7.6Y(D.2d(d))},5w:"1.2.6",8G:H(){I 7.K},K:0,3p:H(a){I a==12?D.2d(7):7[a]},2I:H(b){J a=D(b);a.5n=7;I a},6Y:H(a){7.K=0;2p.44.1p.1w(7,a);I 7},P:H(a,b){I D.P(7,a,b)},5i:H(b){J a=-1;I D.2L(b&&b.5w?b[0]:b,7)},1K:H(c,a,b){J d=c;G(c.1q==56)G(a===12)I 7[0]&&D[b||"1K"](7[0],c);N{d={};d[c]=a}I 7.P(H(i){R(c 1n d)D.1K(b?7.V:7,c,D.1i(7,d[c],b,i,c))})},1g:H(b,a){G((b==\'2h\'||b==\'1Z\')&&3d(a)<0)a=12;I 7.1K(b,a,"2a")},1r:H(b){G(1j b!="49"&&b!=U)I 7.4E().3v((7[0]&&7[0].2z||S).5F(b));J a="";D.P(b||7,H(){D.P(7.3t,H(){G(7.16!=8)a+=7.16!=1?7.76:D.17.1r([7])})});I a},5z:H(b){G(7[0])D(b,7[0].2z).5y().39(7[0]).2l(H(){J a=7;1B(a.1x)a=a.1x;I a}).3v(7);I 7},8Y:H(a){I 7.P(H(){D(7).6Q().5z(a)})},8R:H(a){I 7.P(H(){D(7).5z(a)})},3v:H(){I 7.3W(19,M,Q,H(a){G(7.16==1)7.3U(a)})},6F:H(){I 7.3W(19,M,M,H(a){G(7.16==1)7.39(a,7.1x)})},6E:H(){I 7.3W(19,Q,Q,H(a){7.1d.39(a,7)})},5q:H(){I 7.3W(19,Q,M,H(a){7.1d.39(a,7.2H)})},3l:H(){I 7.5n||D([])},2q:H(b){J c=D.2l(7,H(a){I D.2q(b,a)});I 7.2I(/[^+>] [^+>]/.11(b)||b.1h("..")>-1?D.4r(c):c)},5y:H(e){J f=7.2l(H(){G(D.14.1f&&!D.4n(7)){J a=7.6o(M),5h=S.3h("1v");5h.3U(a);I D.4h([5h.4H])[0]}N I 7.6o(M)});J d=f.2q("*").5c().P(H(){G(7[E]!=12)7[E]=U});G(e===M)7.2q("*").5c().P(H(i){G(7.16==3)I;J c=D.L(7,"3w");R(J a 1n c)R(J b 1n c[a])D.W.1e(d[i],a,c[a][b],c[a][b].L)});I f},1E:H(b){I 7.2I(D.1D(b)&&D.3C(7,H(a,i){I b.1k(a,i)})||D.3g(b,7))},4Y:H(b){G(b.1q==56)G(62.11(b))I 7.2I(D.3g(b,7,M));N b=D.3g(b,7);J a=b.K&&b[b.K-1]!==12&&!b.16;I 7.1E(H(){I a?D.2L(7,b)<0:7!=b})},1e:H(a){I 7.2I(D.4r(D.2R(7.3p(),1j a==\'23\'?D(a):D.2d(a))))},3F:H(a){I!!a&&D.3g(a,7).K>0},7T:H(a){I 7.3F("."+a)},6e:H(b){G(b==12){G(7.K){J c=7[0];G(D.Y(c,"2A")){J e=c.64,63=[],15=c.15,2V=c.O=="2A-2V";G(e<0)I U;R(J i=2V?e:0,2f=2V?e+1:15.K;i<2f;i++){J d=15[i];G(d.2W){b=D.14.1f&&!d.at.2x.an?d.1r:d.2x;G(2V)I b;63.1p(b)}}I 63}N I(7[0].2x||"").1o(/\\r/g,"")}I 12}G(b.1q==4L)b+=\'\';I 7.P(H(){G(7.16!=1)I;G(b.1q==2p&&/5O|5L/.11(7.O))7.4J=(D.2L(7.2x,b)>=0||D.2L(7.34,b)>=0);N G(D.Y(7,"2A")){J a=D.2d(b);D("9R",7).P(H(){7.2W=(D.2L(7.2x,a)>=0||D.2L(7.1r,a)>=0)});G(!a.K)7.64=-1}N 7.2x=b})},2K:H(a){I a==12?(7[0]?7[0].4H:U):7.4E().3v(a)},7b:H(a){I 7.5q(a).21()},79:H(i){I 7.3s(i,i+1)},3s:H(){I 7.2I(2p.44.3s.1w(7,19))},2l:H(b){I 7.2I(D.2l(7,H(a,i){I b.1k(a,i,a)}))},5c:H(){I 7.1e(7.5n)},L:H(d,b){J a=d.1R(".");a[1]=a[1]?"."+a[1]:"";G(b===12){J c=7.5C("9z"+a[1]+"!",[a[0]]);G(c===12&&7.K)c=D.L(7[0],d);I c===12&&a[1]?7.L(a[0]):c}N I 7.1P("9u"+a[1]+"!",[a[0],b]).P(H(){D.L(7,d,b)})},3b:H(a){I 7.P(H(){D.3b(7,a)})},3W:H(g,f,h,d){J e=7.K>1,3x;I 7.P(H(){G(!3x){3x=D.4h(g,7.2z);G(h)3x.9o()}J b=7;G(f&&D.Y(7,"1T")&&D.Y(3x[0],"4F"))b=7.3H("22")[0]||7.3U(7.2z.3h("22"));J c=D([]);D.P(3x,H(){J a=e?D(7).5y(M)[0]:7;G(D.Y(a,"1m"))c=c.1e(a);N{G(a.16==1)c=c.1e(D("1m",a).21());d.1k(b,a)}});c.P(6T)})}};D.17.5j.44=D.17;H 6T(i,a){G(a.4d)D.3Y({1a:a.4d,31:Q,1O:"1m"});N D.5u(a.1r||a.6O||a.4H||"");G(a.1d)a.1d.37(a)}H 1z(){I+2B 8J}D.1l=D.17.1l=H(){J b=19[0]||{},i=1,K=19.K,4x=Q,15;G(b.1q==8I){4x=b;b=19[1]||{};i=2}G(1j b!="49"&&1j b!="H")b={};G(K==i){b=7;--i}R(;i<K;i++)G((15=19[i])!=U)R(J c 1n 15){J a=b[c],2w=15[c];G(b===2w)6M;G(4x&&2w&&1j 2w=="49"&&!2w.16)b[c]=D.1l(4x,a||(2w.K!=U?[]:{}),2w);N G(2w!==12)b[c]=2w}I b};J E="4M"+1z(),6K=0,5r={},6G=/z-?5i|8B-?8A|1y|6B|8v-?1Z/i,3P=S.3P||{};D.1l({8u:H(a){1b.$=3m$;G(a)1b.4M=w;I D},1D:H(a){I!!a&&1j a!="23"&&!a.Y&&a.1q!=2p&&/^[\\s[]?H/.11(a+"")},4n:H(a){I a.1C&&!a.1c||a.2j&&a.2z&&!a.2z.1c},5u:H(a){a=D.3k(a);G(a){J b=S.3H("6w")[0]||S.1C,1m=S.3h("1m");1m.O="1r/4t";G(D.14.1f)1m.1r=a;N 1m.3U(S.5F(a));b.39(1m,b.1x);b.37(1m)}},Y:H(b,a){I b.Y&&b.Y.2r()==a.2r()},1Y:{},L:H(c,d,b){c=c==1b?5r:c;J a=c[E];G(!a)a=c[E]=++6K;G(d&&!D.1Y[a])D.1Y[a]={};G(b!==12)D.1Y[a][d]=b;I d?D.1Y[a][d]:a},3b:H(c,b){c=c==1b?5r:c;J a=c[E];G(b){G(D.1Y[a]){2U D.1Y[a][b];b="";R(b 1n D.1Y[a])1X;G(!b)D.3b(c)}}N{1U{2U c[E]}1V(e){G(c.5l)c.5l(E)}2U D.1Y[a]}},P:H(d,a,c){J e,i=0,K=d.K;G(c){G(K==12){R(e 1n d)G(a.1w(d[e],c)===Q)1X}N R(;i<K;)G(a.1w(d[i++],c)===Q)1X}N{G(K==12){R(e 1n d)G(a.1k(d[e],e,d[e])===Q)1X}N R(J b=d[0];i<K&&a.1k(b,i,b)!==Q;b=d[++i]){}}I d},1i:H(b,a,c,i,d){G(D.1D(a))a=a.1k(b,i);I a&&a.1q==4L&&c=="2a"&&!6G.11(d)?a+"2X":a},1F:{1e:H(c,b){D.P((b||"").1R(/\\s+/),H(i,a){G(c.16==1&&!D.1F.3T(c.1F,a))c.1F+=(c.1F?" ":"")+a})},21:H(c,b){G(c.16==1)c.1F=b!=12?D.3C(c.1F.1R(/\\s+/),H(a){I!D.1F.3T(b,a)}).6s(" "):""},3T:H(b,a){I D.2L(a,(b.1F||b).6r().1R(/\\s+/))>-1}},6q:H(b,c,a){J e={};R(J d 1n c){e[d]=b.V[d];b.V[d]=c[d]}a.1k(b);R(J d 1n c)b.V[d]=e[d]},1g:H(d,e,c){G(e=="2h"||e=="1Z"){J b,3X={30:"5x",5g:"1G",18:"3I"},35=e=="2h"?["5e","6k"]:["5G","6i"];H 5b(){b=e=="2h"?d.8f:d.8c;J a=0,2C=0;D.P(35,H(){a+=3d(D.2a(d,"57"+7,M))||0;2C+=3d(D.2a(d,"2C"+7+"4b",M))||0});b-=29.83(a+2C)}G(D(d).3F(":4j"))5b();N D.6q(d,3X,5b);I 29.2f(0,b)}I D.2a(d,e,c)},2a:H(f,l,k){J e,V=f.V;H 3E(b){G(!D.14.2k)I Q;J a=3P.54(b,U);I!a||a.52("3E")==""}G(l=="1y"&&D.14.1f){e=D.1K(V,"1y");I e==""?"1":e}G(D.14.2G&&l=="18"){J d=V.50;V.50="0 7Y 7W";V.50=d}G(l.1I(/4i/i))l=y;G(!k&&V&&V[l])e=V[l];N G(3P.54){G(l.1I(/4i/i))l="4i";l=l.1o(/([A-Z])/g,"-$1").3y();J c=3P.54(f,U);G(c&&!3E(f))e=c.52(l);N{J g=[],2E=[],a=f,i=0;R(;a&&3E(a);a=a.1d)2E.6h(a);R(;i<2E.K;i++)G(3E(2E[i])){g[i]=2E[i].V.18;2E[i].V.18="3I"}e=l=="18"&&g[2E.K-1]!=U?"2F":(c&&c.52(l))||"";R(i=0;i<g.K;i++)G(g[i]!=U)2E[i].V.18=g[i]}G(l=="1y"&&e=="")e="1"}N G(f.4g){J h=l.1o(/\\-(\\w)/g,H(a,b){I b.2r()});e=f.4g[l]||f.4g[h];G(!/^\\d+(2X)?$/i.11(e)&&/^\\d/.11(e)){J j=V.1A,66=f.65.1A;f.65.1A=f.4g.1A;V.1A=e||0;e=V.aM+"2X";V.1A=j;f.65.1A=66}}I e},4h:H(l,h){J k=[];h=h||S;G(1j h.3h==\'12\')h=h.2z||h[0]&&h[0].2z||S;D.P(l,H(i,d){G(!d)I;G(d.1q==4L)d+=\'\';G(1j d=="23"){d=d.1o(/(<(\\w+)[^>]*?)\\/>/g,H(b,a,c){I c.1I(/^(aK|4f|7E|aG|4T|7A|aB|3n|az|ay|av)$/i)?b:a+"></"+c+">"});J f=D.3k(d).3y(),1v=h.3h("1v");J e=!f.1h("<au")&&[1,"<2A 7w=\'7w\'>","</2A>"]||!f.1h("<ar")&&[1,"<7v>","</7v>"]||f.1I(/^<(aq|22|am|ak|ai)/)&&[1,"<1T>","</1T>"]||!f.1h("<4F")&&[2,"<1T><22>","</22></1T>"]||(!f.1h("<af")||!f.1h("<ad"))&&[3,"<1T><22><4F>","</4F></22></1T>"]||!f.1h("<7E")&&[2,"<1T><22></22><7q>","</7q></1T>"]||D.14.1f&&[1,"1v<1v>","</1v>"]||[0,"",""];1v.4H=e[1]+d+e[2];1B(e[0]--)1v=1v.5T;G(D.14.1f){J g=!f.1h("<1T")&&f.1h("<22")<0?1v.1x&&1v.1x.3t:e[1]=="<1T>"&&f.1h("<22")<0?1v.3t:[];R(J j=g.K-1;j>=0;--j)G(D.Y(g[j],"22")&&!g[j].3t.K)g[j].1d.37(g[j]);G(/^\\s/.11(d))1v.39(h.5F(d.1I(/^\\s*/)[0]),1v.1x)}d=D.2d(1v.3t)}G(d.K===0&&(!D.Y(d,"3V")&&!D.Y(d,"2A")))I;G(d[0]==12||D.Y(d,"3V")||d.15)k.1p(d);N k=D.2R(k,d)});I k},1K:H(d,f,c){G(!d||d.16==3||d.16==8)I 12;J e=!D.4n(d),40=c!==12,1f=D.14.1f;f=e&&D.3X[f]||f;G(d.2j){J g=/5Q|4d|V/.11(f);G(f=="2W"&&D.14.2k)d.1d.64;G(f 1n d&&e&&!g){G(40){G(f=="O"&&D.Y(d,"4T")&&d.1d)7p"O a3 a1\'t 9V 9U";d[f]=c}G(D.Y(d,"3V")&&d.7i(f))I d.7i(f).76;I d[f]}G(1f&&e&&f=="V")I D.1K(d.V,"9T",c);G(40)d.9Q(f,""+c);J h=1f&&e&&g?d.4G(f,2):d.4G(f);I h===U?12:h}G(1f&&f=="1y"){G(40){d.6B=1;d.1E=(d.1E||"").1o(/7f\\([^)]*\\)/,"")+(3r(c)+\'\'=="9L"?"":"7f(1y="+c*7a+")")}I d.1E&&d.1E.1h("1y=")>=0?(3d(d.1E.1I(/1y=([^)]*)/)[1])/7a)+\'\':""}f=f.1o(/-([a-z])/9H,H(a,b){I b.2r()});G(40)d[f]=c;I d[f]},3k:H(a){I(a||"").1o(/^\\s+|\\s+$/g,"")},2d:H(b){J a=[];G(b!=U){J i=b.K;G(i==U||b.1R||b.4I||b.1k)a[0]=b;N 1B(i)a[--i]=b[i]}I a},2L:H(b,a){R(J i=0,K=a.K;i<K;i++)G(a[i]===b)I i;I-1},2R:H(a,b){J i=0,T,2S=a.K;G(D.14.1f){1B(T=b[i++])G(T.16!=8)a[2S++]=T}N 1B(T=b[i++])a[2S++]=T;I a},4r:H(a){J c=[],2o={};1U{R(J i=0,K=a.K;i<K;i++){J b=D.L(a[i]);G(!2o[b]){2o[b]=M;c.1p(a[i])}}}1V(e){c=a}I c},3C:H(c,a,d){J b=[];R(J i=0,K=c.K;i<K;i++)G(!d!=!a(c[i],i))b.1p(c[i]);I b},2l:H(d,a){J c=[];R(J i=0,K=d.K;i<K;i++){J b=a(d[i],i);G(b!=U)c[c.K]=b}I c.7d.1w([],c)}});J v=9B.9A.3y();D.14={5B:(v.1I(/.+(?:9y|9x|9w|9v)[\\/: ]([\\d.]+)/)||[])[1],2k:/75/.11(v),2G:/2G/.11(v),1f:/1f/.11(v)&&!/2G/.11(v),42:/42/.11(v)&&!/(9s|75)/.11(v)};J y=D.14.1f?"7o":"72";D.1l({71:!D.14.1f||S.70=="6Z",3X:{"R":"9n","9k":"1F","4i":y,72:y,7o:y,9h:"9f",9e:"9d",9b:"99"}});D.P({6W:H(a){I a.1d},97:H(a){I D.4S(a,"1d")},95:H(a){I D.3a(a,2,"2H")},91:H(a){I D.3a(a,2,"4l")},8Z:H(a){I D.4S(a,"2H")},8X:H(a){I D.4S(a,"4l")},8W:H(a){I D.5v(a.1d.1x,a)},8V:H(a){I D.5v(a.1x)},6Q:H(a){I D.Y(a,"8U")?a.8T||a.8S.S:D.2d(a.3t)}},H(c,d){D.17[c]=H(b){J a=D.2l(7,d);G(b&&1j b=="23")a=D.3g(b,a);I 7.2I(D.4r(a))}});D.P({6P:"3v",8Q:"6F",39:"6E",8P:"5q",8O:"7b"},H(c,b){D.17[c]=H(){J a=19;I 7.P(H(){R(J i=0,K=a.K;i<K;i++)D(a[i])[b](7)})}});D.P({8N:H(a){D.1K(7,a,"");G(7.16==1)7.5l(a)},8M:H(a){D.1F.1e(7,a)},8L:H(a){D.1F.21(7,a)},8K:H(a){D.1F[D.1F.3T(7,a)?"21":"1e"](7,a)},21:H(a){G(!a||D.1E(a,[7]).r.K){D("*",7).1e(7).P(H(){D.W.21(7);D.3b(7)});G(7.1d)7.1d.37(7)}},4E:H(){D(">*",7).21();1B(7.1x)7.37(7.1x)}},H(a,b){D.17[a]=H(){I 7.P(b,19)}});D.P(["6N","4b"],H(i,c){J b=c.3y();D.17[b]=H(a){I 7[0]==1b?D.14.2G&&S.1c["5t"+c]||D.14.2k&&1b["5s"+c]||S.70=="6Z"&&S.1C["5t"+c]||S.1c["5t"+c]:7[0]==S?29.2f(29.2f(S.1c["4y"+c],S.1C["4y"+c]),29.2f(S.1c["2i"+c],S.1C["2i"+c])):a==12?(7.K?D.1g(7[0],b):U):7.1g(b,a.1q==56?a:a+"2X")}});H 25(a,b){I a[0]&&3r(D.2a(a[0],b,M),10)||0}J C=D.14.2k&&3r(D.14.5B)<8H?"(?:[\\\\w*3m-]|\\\\\\\\.)":"(?:[\\\\w\\8F-\\8E*3m-]|\\\\\\\\.)",6L=2B 4v("^>\\\\s*("+C+"+)"),6J=2B 4v("^("+C+"+)(#)("+C+"+)"),6I=2B 4v("^([#.]?)("+C+"*)");D.1l({6H:{"":H(a,i,m){I m[2]=="*"||D.Y(a,m[2])},"#":H(a,i,m){I a.4G("2v")==m[2]},":":{8D:H(a,i,m){I i<m[3]-0},8C:H(a,i,m){I i>m[3]-0},3a:H(a,i,m){I m[3]-0==i},79:H(a,i,m){I m[3]-0==i},3o:H(a,i){I i==0},3S:H(a,i,m,r){I i==r.K-1},6D:H(a,i){I i%2==0},6C:H(a,i){I i%2},"3o-4u":H(a){I a.1d.3H("*")[0]==a},"3S-4u":H(a){I D.3a(a.1d.5T,1,"4l")==a},"8z-4u":H(a){I!D.3a(a.1d.5T,2,"4l")},6W:H(a){I a.1x},4E:H(a){I!a.1x},8y:H(a,i,m){I(a.6O||a.8x||D(a).1r()||"").1h(m[3])>=0},4j:H(a){I"1G"!=a.O&&D.1g(a,"18")!="2F"&&D.1g(a,"5g")!="1G"},1G:H(a){I"1G"==a.O||D.1g(a,"18")=="2F"||D.1g(a,"5g")=="1G"},8w:H(a){I!a.3R},3R:H(a){I a.3R},4J:H(a){I a.4J},2W:H(a){I a.2W||D.1K(a,"2W")},1r:H(a){I"1r"==a.O},5O:H(a){I"5O"==a.O},5L:H(a){I"5L"==a.O},5p:H(a){I"5p"==a.O},3Q:H(a){I"3Q"==a.O},5o:H(a){I"5o"==a.O},6A:H(a){I"6A"==a.O},6z:H(a){I"6z"==a.O},2s:H(a){I"2s"==a.O||D.Y(a,"2s")},4T:H(a){I/4T|2A|6y|2s/i.11(a.Y)},3T:H(a,i,m){I D.2q(m[3],a).K},8t:H(a){I/h\\d/i.11(a.Y)},8s:H(a){I D.3C(D.3O,H(b){I a==b.T}).K}}},6x:[/^(\\[) *@?([\\w-]+) *([!*$^~=]*) *(\'?"?)(.*?)\\4 *\\]/,/^(:)([\\w-]+)\\("?\'?(.*?(\\(.*?\\))?[^(]*?)"?\'?\\)/,2B 4v("^([:.#]*)("+C+"+)")],3g:H(a,c,b){J d,1t=[];1B(a&&a!=d){d=a;J f=D.1E(a,c,b);a=f.t.1o(/^\\s*,\\s*/,"");1t=b?c=f.r:D.2R(1t,f.r)}I 1t},2q:H(t,o){G(1j t!="23")I[t];G(o&&o.16!=1&&o.16!=9)I[];o=o||S;J d=[o],2o=[],3S,Y;1B(t&&3S!=t){J r=[];3S=t;t=D.3k(t);J l=Q,3j=6L,m=3j.2D(t);G(m){Y=m[1].2r();R(J i=0;d[i];i++)R(J c=d[i].1x;c;c=c.2H)G(c.16==1&&(Y=="*"||c.Y.2r()==Y))r.1p(c);d=r;t=t.1o(3j,"");G(t.1h(" ")==0)6M;l=M}N{3j=/^([>+~])\\s*(\\w*)/i;G((m=3j.2D(t))!=U){r=[];J k={};Y=m[2].2r();m=m[1];R(J j=0,3i=d.K;j<3i;j++){J n=m=="~"||m=="+"?d[j].2H:d[j].1x;R(;n;n=n.2H)G(n.16==1){J g=D.L(n);G(m=="~"&&k[g])1X;G(!Y||n.Y.2r()==Y){G(m=="~")k[g]=M;r.1p(n)}G(m=="+")1X}}d=r;t=D.3k(t.1o(3j,""));l=M}}G(t&&!l){G(!t.1h(",")){G(o==d[0])d.4s();2o=D.2R(2o,d);r=d=[o];t=" "+t.6v(1,t.K)}N{J h=6J;J m=h.2D(t);G(m){m=[0,m[2],m[3],m[1]]}N{h=6I;m=h.2D(t)}m[2]=m[2].1o(/\\\\/g,"");J f=d[d.K-1];G(m[1]=="#"&&f&&f.61&&!D.4n(f)){J p=f.61(m[2]);G((D.14.1f||D.14.2G)&&p&&1j p.2v=="23"&&p.2v!=m[2])p=D(\'[@2v="\'+m[2]+\'"]\',f)[0];d=r=p&&(!m[3]||D.Y(p,m[3]))?[p]:[]}N{R(J i=0;d[i];i++){J a=m[1]=="#"&&m[3]?m[3]:m[1]!=""||m[0]==""?"*":m[2];G(a=="*"&&d[i].Y.3y()=="49")a="3n";r=D.2R(r,d[i].3H(a))}G(m[1]==".")r=D.5m(r,m[2]);G(m[1]=="#"){J e=[];R(J i=0;r[i];i++)G(r[i].4G("2v")==m[2]){e=[r[i]];1X}r=e}d=r}t=t.1o(h,"")}}G(t){J b=D.1E(t,r);d=r=b.r;t=D.3k(b.t)}}G(t)d=[];G(d&&o==d[0])d.4s();2o=D.2R(2o,d);I 2o},5m:H(r,m,a){m=" "+m+" ";J c=[];R(J i=0;r[i];i++){J b=(" "+r[i].1F+" ").1h(m)>=0;G(!a&&b||a&&!b)c.1p(r[i])}I c},1E:H(t,r,h){J d;1B(t&&t!=d){d=t;J p=D.6x,m;R(J i=0;p[i];i++){m=p[i].2D(t);G(m){t=t.8r(m[0].K);m[2]=m[2].1o(/\\\\/g,"");1X}}G(!m)1X;G(m[1]==":"&&m[2]=="4Y")r=62.11(m[3])?D.1E(m[3],r,M).r:D(r).4Y(m[3]);N G(m[1]==".")r=D.5m(r,m[2],h);N G(m[1]=="["){J g=[],O=m[3];R(J i=0,3i=r.K;i<3i;i++){J a=r[i],z=a[D.3X[m[2]]||m[2]];G(z==U||/5Q|4d|2W/.11(m[2]))z=D.1K(a,m[2])||\'\';G((O==""&&!!z||O=="="&&z==m[5]||O=="!="&&z!=m[5]||O=="^="&&z&&!z.1h(m[5])||O=="$="&&z.6v(z.K-m[5].K)==m[5]||(O=="*="||O=="~=")&&z.1h(m[5])>=0)^h)g.1p(a)}r=g}N G(m[1]==":"&&m[2]=="3a-4u"){J e={},g=[],11=/(-?)(\\d*)n((?:\\+|-)?\\d*)/.2D(m[3]=="6D"&&"2n"||m[3]=="6C"&&"2n+1"||!/\\D/.11(m[3])&&"8q+"+m[3]||m[3]),3o=(11[1]+(11[2]||1))-0,d=11[3]-0;R(J i=0,3i=r.K;i<3i;i++){J j=r[i],1d=j.1d,2v=D.L(1d);G(!e[2v]){J c=1;R(J n=1d.1x;n;n=n.2H)G(n.16==1)n.4q=c++;e[2v]=M}J b=Q;G(3o==0){G(j.4q==d)b=M}N G((j.4q-d)%3o==0&&(j.4q-d)/3o>=0)b=M;G(b^h)g.1p(j)}r=g}N{J f=D.6H[m[1]];G(1j f=="49")f=f[m[2]];G(1j f=="23")f=6u("Q||H(a,i){I "+f+";}");r=D.3C(r,H(a,i){I f(a,i,m,r)},h)}}I{r:r,t:t}},4S:H(b,c){J a=[],1t=b[c];1B(1t&&1t!=S){G(1t.16==1)a.1p(1t);1t=1t[c]}I a},3a:H(a,e,c,b){e=e||1;J d=0;R(;a;a=a[c])G(a.16==1&&++d==e)1X;I a},5v:H(n,a){J r=[];R(;n;n=n.2H){G(n.16==1&&n!=a)r.1p(n)}I r}});D.W={1e:H(f,i,g,e){G(f.16==3||f.16==8)I;G(D.14.1f&&f.4I)f=1b;G(!g.24)g.24=7.24++;G(e!=12){J h=g;g=7.3M(h,H(){I h.1w(7,19)});g.L=e}J j=D.L(f,"3w")||D.L(f,"3w",{}),1H=D.L(f,"1H")||D.L(f,"1H",H(){G(1j D!="12"&&!D.W.5k)I D.W.1H.1w(19.3L.T,19)});1H.T=f;D.P(i.1R(/\\s+/),H(c,b){J a=b.1R(".");b=a[0];g.O=a[1];J d=j[b];G(!d){d=j[b]={};G(!D.W.2t[b]||D.W.2t[b].4p.1k(f)===Q){G(f.3K)f.3K(b,1H,Q);N G(f.6t)f.6t("4o"+b,1H)}}d[g.24]=g;D.W.26[b]=M});f=U},24:1,26:{},21:H(e,h,f){G(e.16==3||e.16==8)I;J i=D.L(e,"3w"),1L,5i;G(i){G(h==12||(1j h=="23"&&h.8p(0)=="."))R(J g 1n i)7.21(e,g+(h||""));N{G(h.O){f=h.2y;h=h.O}D.P(h.1R(/\\s+/),H(b,a){J c=a.1R(".");a=c[0];G(i[a]){G(f)2U i[a][f.24];N R(f 1n i[a])G(!c[1]||i[a][f].O==c[1])2U i[a][f];R(1L 1n i[a])1X;G(!1L){G(!D.W.2t[a]||D.W.2t[a].4A.1k(e)===Q){G(e.6p)e.6p(a,D.L(e,"1H"),Q);N G(e.6n)e.6n("4o"+a,D.L(e,"1H"))}1L=U;2U i[a]}}})}R(1L 1n i)1X;G(!1L){J d=D.L(e,"1H");G(d)d.T=U;D.3b(e,"3w");D.3b(e,"1H")}}},1P:H(h,c,f,g,i){c=D.2d(c);G(h.1h("!")>=0){h=h.3s(0,-1);J a=M}G(!f){G(7.26[h])D("*").1e([1b,S]).1P(h,c)}N{G(f.16==3||f.16==8)I 12;J b,1L,17=D.1D(f[h]||U),W=!c[0]||!c[0].32;G(W){c.6h({O:h,2J:f,32:H(){},3J:H(){},4C:1z()});c[0][E]=M}c[0].O=h;G(a)c[0].6m=M;J d=D.L(f,"1H");G(d)b=d.1w(f,c);G((!17||(D.Y(f,\'a\')&&h=="4V"))&&f["4o"+h]&&f["4o"+h].1w(f,c)===Q)b=Q;G(W)c.4s();G(i&&D.1D(i)){1L=i.1w(f,b==U?c:c.7d(b));G(1L!==12)b=1L}G(17&&g!==Q&&b!==Q&&!(D.Y(f,\'a\')&&h=="4V")){7.5k=M;1U{f[h]()}1V(e){}}7.5k=Q}I b},1H:H(b){J a,1L,38,5f,4m;b=19[0]=D.W.6l(b||1b.W);38=b.O.1R(".");b.O=38[0];38=38[1];5f=!38&&!b.6m;4m=(D.L(7,"3w")||{})[b.O];R(J j 1n 4m){J c=4m[j];G(5f||c.O==38){b.2y=c;b.L=c.L;1L=c.1w(7,19);G(a!==Q)a=1L;G(1L===Q){b.32();b.3J()}}}I a},6l:H(b){G(b[E]==M)I b;J d=b;b={8o:d};J c="8n 8m 8l 8k 2s 8j 47 5d 6j 5E 8i L 8h 8g 4K 2y 5a 59 8e 8b 58 6f 8a 88 4k 87 86 84 6d 2J 4C 6c O 82 81 35".1R(" ");R(J i=c.K;i;i--)b[c[i]]=d[c[i]];b[E]=M;b.32=H(){G(d.32)d.32();d.80=Q};b.3J=H(){G(d.3J)d.3J();d.7Z=M};b.4C=b.4C||1z();G(!b.2J)b.2J=b.6d||S;G(b.2J.16==3)b.2J=b.2J.1d;G(!b.4k&&b.4K)b.4k=b.4K==b.2J?b.6c:b.4K;G(b.58==U&&b.5d!=U){J a=S.1C,1c=S.1c;b.58=b.5d+(a&&a.2e||1c&&1c.2e||0)-(a.6b||0);b.6f=b.6j+(a&&a.2c||1c&&1c.2c||0)-(a.6a||0)}G(!b.35&&((b.47||b.47===0)?b.47:b.5a))b.35=b.47||b.5a;G(!b.59&&b.5E)b.59=b.5E;G(!b.35&&b.2s)b.35=(b.2s&1?1:(b.2s&2?3:(b.2s&4?2:0)));I b},3M:H(a,b){b.24=a.24=a.24||b.24||7.24++;I b},2t:{27:{4p:H(){55();I},4A:H(){I}},3D:{4p:H(){G(D.14.1f)I Q;D(7).2O("53",D.W.2t.3D.2y);I M},4A:H(){G(D.14.1f)I Q;D(7).4e("53",D.W.2t.3D.2y);I M},2y:H(a){G(F(a,7))I M;a.O="3D";I D.W.1H.1w(7,19)}},3N:{4p:H(){G(D.14.1f)I Q;D(7).2O("51",D.W.2t.3N.2y);I M},4A:H(){G(D.14.1f)I Q;D(7).4e("51",D.W.2t.3N.2y);I M},2y:H(a){G(F(a,7))I M;a.O="3N";I D.W.1H.1w(7,19)}}}};D.17.1l({2O:H(c,a,b){I c=="4X"?7.2V(c,a,b):7.P(H(){D.W.1e(7,c,b||a,b&&a)})},2V:H(d,b,c){J e=D.W.3M(c||b,H(a){D(7).4e(a,e);I(c||b).1w(7,19)});I 7.P(H(){D.W.1e(7,d,e,c&&b)})},4e:H(a,b){I 7.P(H(){D.W.21(7,a,b)})},1P:H(c,a,b){I 7.P(H(){D.W.1P(c,a,7,M,b)})},5C:H(c,a,b){I 7[0]&&D.W.1P(c,a,7[0],Q,b)},2m:H(b){J c=19,i=1;1B(i<c.K)D.W.3M(b,c[i++]);I 7.4V(D.W.3M(b,H(a){7.4Z=(7.4Z||0)%i;a.32();I c[7.4Z++].1w(7,19)||Q}))},7X:H(a,b){I 7.2O(\'3D\',a).2O(\'3N\',b)},27:H(a){55();G(D.2Q)a.1k(S,D);N D.3A.1p(H(){I a.1k(7,D)});I 7}});D.1l({2Q:Q,3A:[],27:H(){G(!D.2Q){D.2Q=M;G(D.3A){D.P(D.3A,H(){7.1k(S)});D.3A=U}D(S).5C("27")}}});J x=Q;H 55(){G(x)I;x=M;G(S.3K&&!D.14.2G)S.3K("69",D.27,Q);G(D.14.1f&&1b==1S)(H(){G(D.2Q)I;1U{S.1C.7V("1A")}1V(3e){3B(19.3L,0);I}D.27()})();G(D.14.2G)S.3K("69",H(){G(D.2Q)I;R(J i=0;i<S.4W.K;i++)G(S.4W[i].3R){3B(19.3L,0);I}D.27()},Q);G(D.14.2k){J a;(H(){G(D.2Q)I;G(S.3f!="68"&&S.3f!="1J"){3B(19.3L,0);I}G(a===12)a=D("V, 7A[7U=7S]").K;G(S.4W.K!=a){3B(19.3L,0);I}D.27()})()}D.W.1e(1b,"43",D.27)}D.P(("7R,7Q,43,85,4y,4X,4V,7P,"+"7O,7N,89,53,51,7M,2A,"+"5o,7L,7K,8d,3e").1R(","),H(i,b){D.17[b]=H(a){I a?7.2O(b,a):7.1P(b)}});J F=H(a,c){J b=a.4k;1B(b&&b!=c)1U{b=b.1d}1V(3e){b=c}I b==c};D(1b).2O("4X",H(){D("*").1e(S).4e()});D.17.1l({67:D.17.43,43:H(g,d,c){G(1j g!=\'23\')I 7.67(g);J e=g.1h(" ");G(e>=0){J i=g.3s(e,g.K);g=g.3s(0,e)}c=c||H(){};J f="2P";G(d)G(D.1D(d)){c=d;d=U}N{d=D.3n(d);f="6g"}J h=7;D.3Y({1a:g,O:f,1O:"2K",L:d,1J:H(a,b){G(b=="1W"||b=="7J")h.2K(i?D("<1v/>").3v(a.4U.1o(/<1m(.|\\s)*?\\/1m>/g,"")).2q(i):a.4U);h.P(c,[a.4U,b,a])}});I 7},aL:H(){I D.3n(7.7I())},7I:H(){I 7.2l(H(){I D.Y(7,"3V")?D.2d(7.aH):7}).1E(H(){I 7.34&&!7.3R&&(7.4J||/2A|6y/i.11(7.Y)||/1r|1G|3Q/i.11(7.O))}).2l(H(i,c){J b=D(7).6e();I b==U?U:b.1q==2p?D.2l(b,H(a,i){I{34:c.34,2x:a}}):{34:c.34,2x:b}}).3p()}});D.P("7H,7G,7F,7D,7C,7B".1R(","),H(i,o){D.17[o]=H(f){I 7.2O(o,f)}});J B=1z();D.1l({3p:H(d,b,a,c){G(D.1D(b)){a=b;b=U}I D.3Y({O:"2P",1a:d,L:b,1W:a,1O:c})},aE:H(b,a){I D.3p(b,U,a,"1m")},aD:H(c,b,a){I D.3p(c,b,a,"3z")},aC:H(d,b,a,c){G(D.1D(b)){a=b;b={}}I D.3Y({O:"6g",1a:d,L:b,1W:a,1O:c})},aA:H(a){D.1l(D.60,a)},60:{1a:5Z.5Q,26:M,O:"2P",2T:0,7z:"4R/x-ax-3V-aw",7x:M,31:M,L:U,5Y:U,3Q:U,4Q:{2N:"4R/2N, 1r/2N",2K:"1r/2K",1m:"1r/4t, 4R/4t",3z:"4R/3z, 1r/4t",1r:"1r/as",4w:"*/*"}},4z:{},3Y:H(s){s=D.1l(M,s,D.1l(M,{},D.60,s));J g,2Z=/=\\?(&|$)/g,1u,L,O=s.O.2r();G(s.L&&s.7x&&1j s.L!="23")s.L=D.3n(s.L);G(s.1O=="4P"){G(O=="2P"){G(!s.1a.1I(2Z))s.1a+=(s.1a.1I(/\\?/)?"&":"?")+(s.4P||"7u")+"=?"}N G(!s.L||!s.L.1I(2Z))s.L=(s.L?s.L+"&":"")+(s.4P||"7u")+"=?";s.1O="3z"}G(s.1O=="3z"&&(s.L&&s.L.1I(2Z)||s.1a.1I(2Z))){g="4P"+B++;G(s.L)s.L=(s.L+"").1o(2Z,"="+g+"$1");s.1a=s.1a.1o(2Z,"="+g+"$1");s.1O="1m";1b[g]=H(a){L=a;1W();1J();1b[g]=12;1U{2U 1b[g]}1V(e){}G(i)i.37(h)}}G(s.1O=="1m"&&s.1Y==U)s.1Y=Q;G(s.1Y===Q&&O=="2P"){J j=1z();J k=s.1a.1o(/(\\?|&)3m=.*?(&|$)/,"$ap="+j+"$2");s.1a=k+((k==s.1a)?(s.1a.1I(/\\?/)?"&":"?")+"3m="+j:"")}G(s.L&&O=="2P"){s.1a+=(s.1a.1I(/\\?/)?"&":"?")+s.L;s.L=U}G(s.26&&!D.4O++)D.W.1P("7H");J n=/^(?:\\w+:)?\\/\\/([^\\/?#]+)/;G(s.1O=="1m"&&O=="2P"&&n.11(s.1a)&&n.2D(s.1a)[1]!=5Z.al){J i=S.3H("6w")[0];J h=S.3h("1m");h.4d=s.1a;G(s.7t)h.aj=s.7t;G(!g){J l=Q;h.ah=h.ag=H(){G(!l&&(!7.3f||7.3f=="68"||7.3f=="1J")){l=M;1W();1J();i.37(h)}}}i.3U(h);I 12}J m=Q;J c=1b.7s?2B 7s("ae.ac"):2B 7r();G(s.5Y)c.6R(O,s.1a,s.31,s.5Y,s.3Q);N c.6R(O,s.1a,s.31);1U{G(s.L)c.4B("ab-aa",s.7z);G(s.5S)c.4B("a9-5R-a8",D.4z[s.1a]||"a7, a6 a5 a4 5N:5N:5N a2");c.4B("X-9Z-9Y","7r");c.4B("9W",s.1O&&s.4Q[s.1O]?s.4Q[s.1O]+", */*":s.4Q.4w)}1V(e){}G(s.7m&&s.7m(c,s)===Q){s.26&&D.4O--;c.7l();I Q}G(s.26)D.W.1P("7B",[c,s]);J d=H(a){G(!m&&c&&(c.3f==4||a=="2T")){m=M;G(f){7k(f);f=U}1u=a=="2T"&&"2T"||!D.7j(c)&&"3e"||s.5S&&D.7h(c,s.1a)&&"7J"||"1W";G(1u=="1W"){1U{L=D.6X(c,s.1O,s.9S)}1V(e){1u="5J"}}G(1u=="1W"){J b;1U{b=c.5I("7g-5R")}1V(e){}G(s.5S&&b)D.4z[s.1a]=b;G(!g)1W()}N D.5H(s,c,1u);1J();G(s.31)c=U}};G(s.31){J f=4I(d,13);G(s.2T>0)3B(H(){G(c){c.7l();G(!m)d("2T")}},s.2T)}1U{c.9P(s.L)}1V(e){D.5H(s,c,U,e)}G(!s.31)d();H 1W(){G(s.1W)s.1W(L,1u);G(s.26)D.W.1P("7C",[c,s])}H 1J(){G(s.1J)s.1J(c,1u);G(s.26)D.W.1P("7F",[c,s]);G(s.26&&!--D.4O)D.W.1P("7G")}I c},5H:H(s,a,b,e){G(s.3e)s.3e(a,b,e);G(s.26)D.W.1P("7D",[a,s,e])},4O:0,7j:H(a){1U{I!a.1u&&5Z.9O=="5p:"||(a.1u>=7e&&a.1u<9N)||a.1u==7c||a.1u==9K||D.14.2k&&a.1u==12}1V(e){}I Q},7h:H(a,c){1U{J b=a.5I("7g-5R");I a.1u==7c||b==D.4z[c]||D.14.2k&&a.1u==12}1V(e){}I Q},6X:H(a,c,b){J d=a.5I("9J-O"),2N=c=="2N"||!c&&d&&d.1h("2N")>=0,L=2N?a.9I:a.4U;G(2N&&L.1C.2j=="5J")7p"5J";G(b)L=b(L,c);G(c=="1m")D.5u(L);G(c=="3z")L=6u("("+L+")");I L},3n:H(a){J s=[];G(a.1q==2p||a.5w)D.P(a,H(){s.1p(3u(7.34)+"="+3u(7.2x))});N R(J j 1n a)G(a[j]&&a[j].1q==2p)D.P(a[j],H(){s.1p(3u(j)+"="+3u(7))});N s.1p(3u(j)+"="+3u(D.1D(a[j])?a[j]():a[j]));I s.6s("&").1o(/%20/g,"+")}});D.17.1l({1N:H(c,b){I c?7.2g({1Z:"1N",2h:"1N",1y:"1N"},c,b):7.1E(":1G").P(H(){7.V.18=7.5D||"";G(D.1g(7,"18")=="2F"){J a=D("<"+7.2j+" />").6P("1c");7.V.18=a.1g("18");G(7.V.18=="2F")7.V.18="3I";a.21()}}).3l()},1M:H(b,a){I b?7.2g({1Z:"1M",2h:"1M",1y:"1M"},b,a):7.1E(":4j").P(H(){7.5D=7.5D||D.1g(7,"18");7.V.18="2F"}).3l()},78:D.17.2m,2m:H(a,b){I D.1D(a)&&D.1D(b)?7.78.1w(7,19):a?7.2g({1Z:"2m",2h:"2m",1y:"2m"},a,b):7.P(H(){D(7)[D(7).3F(":1G")?"1N":"1M"]()})},9G:H(b,a){I 7.2g({1Z:"1N"},b,a)},9F:H(b,a){I 7.2g({1Z:"1M"},b,a)},9E:H(b,a){I 7.2g({1Z:"2m"},b,a)},9D:H(b,a){I 7.2g({1y:"1N"},b,a)},9M:H(b,a){I 7.2g({1y:"1M"},b,a)},9C:H(c,a,b){I 7.2g({1y:a},c,b)},2g:H(k,j,i,g){J h=D.77(j,i,g);I 7[h.36===Q?"P":"36"](H(){G(7.16!=1)I Q;J f=D.1l({},h),p,1G=D(7).3F(":1G"),46=7;R(p 1n k){G(k[p]=="1M"&&1G||k[p]=="1N"&&!1G)I f.1J.1k(7);G(p=="1Z"||p=="2h"){f.18=D.1g(7,"18");f.33=7.V.33}}G(f.33!=U)7.V.33="1G";f.45=D.1l({},k);D.P(k,H(c,a){J e=2B D.28(46,f,c);G(/2m|1N|1M/.11(a))e[a=="2m"?1G?"1N":"1M":a](k);N{J b=a.6r().1I(/^([+-]=)?([\\d+-.]+)(.*)$/),2b=e.1t(M)||0;G(b){J d=3d(b[2]),2M=b[3]||"2X";G(2M!="2X"){46.V[c]=(d||1)+2M;2b=((d||1)/e.1t(M))*2b;46.V[c]=2b+2M}G(b[1])d=((b[1]=="-="?-1:1)*d)+2b;e.3G(2b,d,2M)}N e.3G(2b,a,"")}});I M})},36:H(a,b){G(D.1D(a)||(a&&a.1q==2p)){b=a;a="28"}G(!a||(1j a=="23"&&!b))I A(7[0],a);I 7.P(H(){G(b.1q==2p)A(7,a,b);N{A(7,a).1p(b);G(A(7,a).K==1)b.1k(7)}})},9X:H(b,c){J a=D.3O;G(b)7.36([]);7.P(H(){R(J i=a.K-1;i>=0;i--)G(a[i].T==7){G(c)a[i](M);a.7n(i,1)}});G(!c)7.5A();I 7}});J A=H(b,c,a){G(b){c=c||"28";J q=D.L(b,c+"36");G(!q||a)q=D.L(b,c+"36",D.2d(a))}I q};D.17.5A=H(a){a=a||"28";I 7.P(H(){J q=A(7,a);q.4s();G(q.K)q[0].1k(7)})};D.1l({77:H(b,a,c){J d=b&&b.1q==a0?b:{1J:c||!c&&a||D.1D(b)&&b,2u:b,41:c&&a||a&&a.1q!=9t&&a};d.2u=(d.2u&&d.2u.1q==4L?d.2u:D.28.5K[d.2u])||D.28.5K.74;d.5M=d.1J;d.1J=H(){G(d.36!==Q)D(7).5A();G(D.1D(d.5M))d.5M.1k(7)};I d},41:{73:H(p,n,b,a){I b+a*p},5P:H(p,n,b,a){I((-29.9r(p*29.9q)/2)+0.5)*a+b}},3O:[],48:U,28:H(b,c,a){7.15=c;7.T=b;7.1i=a;G(!c.3Z)c.3Z={}}});D.28.44={4D:H(){G(7.15.2Y)7.15.2Y.1k(7.T,7.1z,7);(D.28.2Y[7.1i]||D.28.2Y.4w)(7);G(7.1i=="1Z"||7.1i=="2h")7.T.V.18="3I"},1t:H(a){G(7.T[7.1i]!=U&&7.T.V[7.1i]==U)I 7.T[7.1i];J r=3d(D.1g(7.T,7.1i,a));I r&&r>-9p?r:3d(D.2a(7.T,7.1i))||0},3G:H(c,b,d){7.5V=1z();7.2b=c;7.3l=b;7.2M=d||7.2M||"2X";7.1z=7.2b;7.2S=7.4N=0;7.4D();J e=7;H t(a){I e.2Y(a)}t.T=7.T;D.3O.1p(t);G(D.48==U){D.48=4I(H(){J a=D.3O;R(J i=0;i<a.K;i++)G(!a[i]())a.7n(i--,1);G(!a.K){7k(D.48);D.48=U}},13)}},1N:H(){7.15.3Z[7.1i]=D.1K(7.T.V,7.1i);7.15.1N=M;7.3G(0,7.1t());G(7.1i=="2h"||7.1i=="1Z")7.T.V[7.1i]="9m";D(7.T).1N()},1M:H(){7.15.3Z[7.1i]=D.1K(7.T.V,7.1i);7.15.1M=M;7.3G(7.1t(),0)},2Y:H(a){J t=1z();G(a||t>7.15.2u+7.5V){7.1z=7.3l;7.2S=7.4N=1;7.4D();7.15.45[7.1i]=M;J b=M;R(J i 1n 7.15.45)G(7.15.45[i]!==M)b=Q;G(b){G(7.15.18!=U){7.T.V.33=7.15.33;7.T.V.18=7.15.18;G(D.1g(7.T,"18")=="2F")7.T.V.18="3I"}G(7.15.1M)7.T.V.18="2F";G(7.15.1M||7.15.1N)R(J p 1n 7.15.45)D.1K(7.T.V,p,7.15.3Z[p])}G(b)7.15.1J.1k(7.T);I Q}N{J n=t-7.5V;7.4N=n/7.15.2u;7.2S=D.41[7.15.41||(D.41.5P?"5P":"73")](7.4N,n,0,1,7.15.2u);7.1z=7.2b+((7.3l-7.2b)*7.2S);7.4D()}I M}};D.1l(D.28,{5K:{9l:9j,9i:7e,74:9g},2Y:{2e:H(a){a.T.2e=a.1z},2c:H(a){a.T.2c=a.1z},1y:H(a){D.1K(a.T.V,"1y",a.1z)},4w:H(a){a.T.V[a.1i]=a.1z+a.2M}}});D.17.2i=H(){J b=0,1S=0,T=7[0],3q;G(T)ao(D.14){J d=T.1d,4a=T,1s=T.1s,1Q=T.2z,5U=2k&&3r(5B)<9c&&!/9a/i.11(v),1g=D.2a,3c=1g(T,"30")=="3c";G(T.7y){J c=T.7y();1e(c.1A+29.2f(1Q.1C.2e,1Q.1c.2e),c.1S+29.2f(1Q.1C.2c,1Q.1c.2c));1e(-1Q.1C.6b,-1Q.1C.6a)}N{1e(T.5X,T.5W);1B(1s){1e(1s.5X,1s.5W);G(42&&!/^t(98|d|h)$/i.11(1s.2j)||2k&&!5U)2C(1s);G(!3c&&1g(1s,"30")=="3c")3c=M;4a=/^1c$/i.11(1s.2j)?4a:1s;1s=1s.1s}1B(d&&d.2j&&!/^1c|2K$/i.11(d.2j)){G(!/^96|1T.*$/i.11(1g(d,"18")))1e(-d.2e,-d.2c);G(42&&1g(d,"33")!="4j")2C(d);d=d.1d}G((5U&&(3c||1g(4a,"30")=="5x"))||(42&&1g(4a,"30")!="5x"))1e(-1Q.1c.5X,-1Q.1c.5W);G(3c)1e(29.2f(1Q.1C.2e,1Q.1c.2e),29.2f(1Q.1C.2c,1Q.1c.2c))}3q={1S:1S,1A:b}}H 2C(a){1e(D.2a(a,"6V",M),D.2a(a,"6U",M))}H 1e(l,t){b+=3r(l,10)||0;1S+=3r(t,10)||0}I 3q};D.17.1l({30:H(){J a=0,1S=0,3q;G(7[0]){J b=7.1s(),2i=7.2i(),4c=/^1c|2K$/i.11(b[0].2j)?{1S:0,1A:0}:b.2i();2i.1S-=25(7,\'94\');2i.1A-=25(7,\'aF\');4c.1S+=25(b,\'6U\');4c.1A+=25(b,\'6V\');3q={1S:2i.1S-4c.1S,1A:2i.1A-4c.1A}}I 3q},1s:H(){J a=7[0].1s;1B(a&&(!/^1c|2K$/i.11(a.2j)&&D.1g(a,\'30\')==\'93\'))a=a.1s;I D(a)}});D.P([\'5e\',\'5G\'],H(i,b){J c=\'4y\'+b;D.17[c]=H(a){G(!7[0])I;I a!=12?7.P(H(){7==1b||7==S?1b.92(!i?a:D(1b).2e(),i?a:D(1b).2c()):7[c]=a}):7[0]==1b||7[0]==S?46[i?\'aI\':\'aJ\']||D.71&&S.1C[c]||S.1c[c]:7[0][c]}});D.P(["6N","4b"],H(i,b){J c=i?"5e":"5G",4f=i?"6k":"6i";D.17["5s"+b]=H(){I 7[b.3y()]()+25(7,"57"+c)+25(7,"57"+4f)};D.17["90"+b]=H(a){I 7["5s"+b]()+25(7,"2C"+c+"4b")+25(7,"2C"+4f+"4b")+(a?25(7,"6S"+c)+25(7,"6S"+4f):0)}})})();',62,669,'|||||||this|||||||||||||||||||||||||||||||||||if|function|return|var|length|data|true|else|type|each|false|for|document|elem|null|style|event||nodeName|||test|undefined||browser|options|nodeType|fn|display|arguments|url|window|body|parentNode|add|msie|css|indexOf|prop|typeof|call|extend|script|in|replace|push|constructor|text|offsetParent|cur|status|div|apply|firstChild|opacity|now|left|while|documentElement|isFunction|filter|className|hidden|handle|match|complete|attr|ret|hide|show|dataType|trigger|doc|split|top|table|try|catch|success|break|cache|height||remove|tbody|string|guid|num|global|ready|fx|Math|curCSS|start|scrollTop|makeArray|scrollLeft|max|animate|width|offset|tagName|safari|map|toggle||done|Array|find|toUpperCase|button|special|duration|id|copy|value|handler|ownerDocument|select|new|border|exec|stack|none|opera|nextSibling|pushStack|target|html|inArray|unit|xml|bind|GET|isReady|merge|pos|timeout|delete|one|selected|px|step|jsre|position|async|preventDefault|overflow|name|which|queue|removeChild|namespace|insertBefore|nth|removeData|fixed|parseFloat|error|readyState|multiFilter|createElement|rl|re|trim|end|_|param|first|get|results|parseInt|slice|childNodes|encodeURIComponent|append|events|elems|toLowerCase|json|readyList|setTimeout|grep|mouseenter|color|is|custom|getElementsByTagName|block|stopPropagation|addEventListener|callee|proxy|mouseleave|timers|defaultView|password|disabled|last|has|appendChild|form|domManip|props|ajax|orig|set|easing|mozilla|load|prototype|curAnim|self|charCode|timerId|object|offsetChild|Width|parentOffset|src|unbind|br|currentStyle|clean|float|visible|relatedTarget|previousSibling|handlers|isXMLDoc|on|setup|nodeIndex|unique|shift|javascript|child|RegExp|_default|deep|scroll|lastModified|teardown|setRequestHeader|timeStamp|update|empty|tr|getAttribute|innerHTML|setInterval|checked|fromElement|Number|jQuery|state|active|jsonp|accepts|application|dir|input|responseText|click|styleSheets|unload|not|lastToggle|outline|mouseout|getPropertyValue|mouseover|getComputedStyle|bindReady|String|padding|pageX|metaKey|keyCode|getWH|andSelf|clientX|Left|all|visibility|container|index|init|triggered|removeAttribute|classFilter|prevObject|submit|file|after|windowData|inner|client|globalEval|sibling|jquery|absolute|clone|wrapAll|dequeue|version|triggerHandler|oldblock|ctrlKey|createTextNode|Top|handleError|getResponseHeader|parsererror|speeds|checkbox|old|00|radio|swing|href|Modified|ifModified|lastChild|safari2|startTime|offsetTop|offsetLeft|username|location|ajaxSettings|getElementById|isSimple|values|selectedIndex|runtimeStyle|rsLeft|_load|loaded|DOMContentLoaded|clientTop|clientLeft|toElement|srcElement|val|pageY|POST|unshift|Bottom|clientY|Right|fix|exclusive|detachEvent|cloneNode|removeEventListener|swap|toString|join|attachEvent|eval|substr|head|parse|textarea|reset|image|zoom|odd|even|before|prepend|exclude|expr|quickClass|quickID|uuid|quickChild|continue|Height|textContent|appendTo|contents|open|margin|evalScript|borderTopWidth|borderLeftWidth|parent|httpData|setArray|CSS1Compat|compatMode|boxModel|cssFloat|linear|def|webkit|nodeValue|speed|_toggle|eq|100|replaceWith|304|concat|200|alpha|Last|httpNotModified|getAttributeNode|httpSuccess|clearInterval|abort|beforeSend|splice|styleFloat|throw|colgroup|XMLHttpRequest|ActiveXObject|scriptCharset|callback|fieldset|multiple|processData|getBoundingClientRect|contentType|link|ajaxSend|ajaxSuccess|ajaxError|col|ajaxComplete|ajaxStop|ajaxStart|serializeArray|notmodified|keypress|keydown|change|mouseup|mousedown|dblclick|focus|blur|stylesheet|hasClass|rel|doScroll|black|hover|solid|cancelBubble|returnValue|wheelDelta|view|round|shiftKey|resize|screenY|screenX|relatedNode|mousemove|prevValue|originalTarget|offsetHeight|keyup|newValue|offsetWidth|eventPhase|detail|currentTarget|cancelable|bubbles|attrName|attrChange|altKey|originalEvent|charAt|0n|substring|animated|header|noConflict|line|enabled|innerText|contains|only|weight|font|gt|lt|uFFFF|u0128|size|417|Boolean|Date|toggleClass|removeClass|addClass|removeAttr|replaceAll|insertAfter|prependTo|wrap|contentWindow|contentDocument|iframe|children|siblings|prevAll|wrapInner|nextAll|outer|prev|scrollTo|static|marginTop|next|inline|parents|able|cellSpacing|adobeair|cellspacing|522|maxLength|maxlength|readOnly|400|readonly|fast|600|class|slow|1px|htmlFor|reverse|10000|PI|cos|compatible|Function|setData|ie|ra|it|rv|getData|userAgent|navigator|fadeTo|fadeIn|slideToggle|slideUp|slideDown|ig|responseXML|content|1223|NaN|fadeOut|300|protocol|send|setAttribute|option|dataFilter|cssText|changed|be|Accept|stop|With|Requested|Object|can|GMT|property|1970|Jan|01|Thu|Since|If|Type|Content|XMLHTTP|th|Microsoft|td|onreadystatechange|onload|cap|charset|colg|host|tfoot|specified|with|1_|thead|leg|plain|attributes|opt|embed|urlencoded|www|area|hr|ajaxSetup|meta|post|getJSON|getScript|marginLeft|img|elements|pageYOffset|pageXOffset|abbr|serialize|pixelLeft'.split('|'),0,{}))/*
 * Ext JS Library 3.1+
 * Copyright(c) 2006-2009 Ext JS, LLC
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
window.undefined=window.undefined;Ext={version:"3.1.0"};
Ext.apply=function(d,e,b){if(b){Ext.apply(d,b)}if(d&&e&&typeof e=="object"){for(var a in e){d[a]=e[a]}}return d};
(function(){var g=0,s=Object.prototype.toString,t=navigator.userAgent.toLowerCase(),y=function(e){return e.test(t)},i=document,l=i.compatMode=="CSS1Compat",A=y(/opera/),h=y(/chrome/),u=y(/webkit/),x=!h&&y(/safari/),f=x&&y(/applewebkit\/4/),b=x&&y(/version\/3/),B=x&&y(/version\/4/),r=!A&&y(/msie/),p=r&&y(/msie 7/),o=r&&y(/msie 8/),q=r&&!p&&!o,n=!u&&y(/gecko/),d=n&&y(/rv:1\.8/),a=n&&y(/rv:1\.9/),v=r&&!l,z=y(/windows|win32/),k=y(/macintosh|mac os x/),j=y(/adobeair/),m=y(/linux/),c=/^https/i.test(window.location.protocol);if(q){try{i.execCommand("BackgroundImageCache",false,true)}catch(w){}}Ext.apply(Ext,{SSL_SECURE_URL:c&&r?'javascript:""':"about:blank",isStrict:l,isSecure:c,isReady:false,enableGarbageCollector:true,enableListenerCollection:false,enableNestedListenerRemoval:false,USE_NATIVE_JSON:false,applyIf:function(C,D){if(C){for(var e in D){if(!Ext.isDefined(C[e])){C[e]=D[e]}}}return C},id:function(e,C){return(e=Ext.getDom(e)||{}).id=e.id||(C||"ext-gen")+(++g)},extend:function(){var C=function(E){for(var D in E){this[D]=E[D]}};var e=Object.prototype.constructor;return function(J,G,I){if(Ext.isObject(G)){I=G;G=J;J=I.constructor!=e?I.constructor:function(){G.apply(this,arguments)}}var E=function(){},H,D=G.prototype;E.prototype=D;H=J.prototype=new E();H.constructor=J;J.superclass=D;if(D.constructor==e){D.constructor=G}J.override=function(F){Ext.override(J,F)};H.superclass=H.supr=(function(){return D});H.override=C;Ext.override(J,I);J.extend=function(F){return Ext.extend(J,F)};return J}}(),override:function(e,D){if(D){var C=e.prototype;Ext.apply(C,D);if(Ext.isIE&&D.hasOwnProperty("toString")){C.toString=D.toString}}},namespace:function(){var C,e;Ext.each(arguments,function(D){e=D.split(".");C=window[e[0]]=window[e[0]]||{};Ext.each(e.slice(1),function(E){C=C[E]=C[E]||{}})});return C},urlEncode:function(G,F){var D,C=[],E=encodeURIComponent;Ext.iterate(G,function(e,H){D=Ext.isEmpty(H);Ext.each(D?e:H,function(I){C.push("&",E(e),"=",(!Ext.isEmpty(I)&&(I!=e||!D))?(Ext.isDate(I)?Ext.encode(I).replace(/"/g,""):E(I)):"")})});if(!F){C.shift();F=""}return F+C.join("")},urlDecode:function(D,C){if(Ext.isEmpty(D)){return{}}var G={},F=D.split("&"),H=decodeURIComponent,e,E;Ext.each(F,function(I){I=I.split("=");e=H(I[0]);E=H(I[1]);G[e]=C||!G[e]?E:[].concat(G[e]).concat(E)});return G},urlAppend:function(e,C){if(!Ext.isEmpty(C)){return e+(e.indexOf("?")===-1?"?":"&")+C}return e},toArray:function(){return r?function(D,G,E,F){F=[];for(var C=0,e=D.length;C<e;C++){F.push(D[C])}return F.slice(G||0,E||F.length)}:function(e,D,C){return Array.prototype.slice.call(e,D||0,C||e.length)}}(),isIterable:function(e){if(Ext.isArray(e)||e.callee){return true}if(/NodeList|HTMLCollection/.test(s.call(e))){return true}return((e.nextNode||e.item)&&Ext.isNumber(e.length))},each:function(F,E,D){if(Ext.isEmpty(F,true)){return}if(!Ext.isIterable(F)||Ext.isPrimitive(F)){F=[F]}for(var C=0,e=F.length;C<e;C++){if(E.call(D||F[C],F[C],C,F)===false){return C}}},iterate:function(D,C,e){if(Ext.isEmpty(D)){return}if(Ext.isIterable(D)){Ext.each(D,C,e);return}else{if(Ext.isObject(D)){for(var E in D){if(D.hasOwnProperty(E)){if(C.call(e||D,E,D[E],D)===false){return}}}}}},getDom:function(e){if(!e||!i){return null}return e.dom?e.dom:(Ext.isString(e)?i.getElementById(e):e)},getBody:function(){return Ext.get(i.body||i.documentElement)},removeNode:r&&!o?function(){var e;return function(C){if(C&&C.tagName!="BODY"){(Ext.enableNestedListenerRemoval)?Ext.EventManager.purgeElement(C,true):Ext.EventManager.removeAll(C);e=e||i.createElement("div");e.appendChild(C);e.innerHTML="";delete Ext.elCache[C.id]}}}():function(e){if(e&&e.parentNode&&e.tagName!="BODY"){(Ext.enableNestedListenerRemoval)?Ext.EventManager.purgeElement(e,true):Ext.EventManager.removeAll(e);e.parentNode.removeChild(e);delete Ext.elCache[e.id]}},isEmpty:function(C,e){return C===null||C===undefined||((Ext.isArray(C)&&!C.length))||(!e?C==="":false)},isArray:function(e){return s.apply(e)==="[object Array]"},isDate:function(e){return s.apply(e)==="[object Date]"},isObject:function(e){return !!e&&Object.prototype.toString.call(e)==="[object Object]"},isPrimitive:function(e){return Ext.isString(e)||Ext.isNumber(e)||Ext.isBoolean(e)},isFunction:function(e){return s.apply(e)==="[object Function]"},isNumber:function(e){return typeof e==="number"&&isFinite(e)},isString:function(e){return typeof e==="string"},isBoolean:function(e){return typeof e==="boolean"},isElement:function(e){return !!e&&e.tagName},isDefined:function(e){return typeof e!=="undefined"},isOpera:A,isWebKit:u,isChrome:h,isSafari:x,isSafari3:b,isSafari4:B,isSafari2:f,isIE:r,isIE6:q,isIE7:p,isIE8:o,isGecko:n,isGecko2:d,isGecko3:a,isBorderBox:v,isLinux:m,isWindows:z,isMac:k,isAir:j});Ext.ns=Ext.namespace})();Ext.ns("Ext.util","Ext.lib","Ext.data");Ext.elCache={};Ext.apply(Function.prototype,{createInterceptor:function(b,a){var c=this;return !Ext.isFunction(b)?this:function(){var e=this,d=arguments;b.target=e;b.method=c;return(b.apply(a||e||window,d)!==false)?c.apply(e||window,d):null}},createCallback:function(){var a=arguments,b=this;return function(){return b.apply(window,a)}},createDelegate:function(c,b,a){var d=this;return function(){var f=b||arguments;if(a===true){f=Array.prototype.slice.call(arguments,0);f=f.concat(b)}else{if(Ext.isNumber(a)){f=Array.prototype.slice.call(arguments,0);var e=[a,0].concat(b);Array.prototype.splice.apply(f,e)}}return d.apply(c||window,f)}},defer:function(c,e,b,a){var d=this.createDelegate(e,b,a);if(c>0){return setTimeout(d,c)}d();return 0}});Ext.applyIf(String,{format:function(b){var a=Ext.toArray(arguments,1);return b.replace(/\{(\d+)\}/g,function(c,d){return a[d]})}});Ext.applyIf(Array.prototype,{indexOf:function(b,c){var a=this.length;c=c||0;c+=(c<0)?a:0;for(;c<a;++c){if(this[c]===b){return c}}return -1},remove:function(b){var a=this.indexOf(b);if(a!=-1){this.splice(a,1)}return this}});Ext.ns("Ext.grid","Ext.list","Ext.dd","Ext.tree","Ext.form","Ext.menu","Ext.state","Ext.layout","Ext.app","Ext.ux","Ext.chart","Ext.direct");Ext.apply(Ext,function(){var c=Ext,a=0,b=null;return{emptyFn:function(){},BLANK_IMAGE_URL:Ext.isIE6||Ext.isIE7||Ext.isAir?MATECH_SYSTEM_WEB_ROOT+"img/s.gif":"data:image/gif;base64,R0lGODlhAQABAID/AMDAwAAAACH5BAEAAAAALAAAAAABAAEAAAICRAEAOw==",extendX:function(d,e){return Ext.extend(d,e(d.prototype))},getDoc:function(){return Ext.get(document)},num:function(e,d){e=Number(Ext.isEmpty(e)||Ext.isBoolean(e)?NaN:e);return isNaN(e)?d:e},value:function(f,d,e){return Ext.isEmpty(f,e)?d:f},escapeRe:function(d){return d.replace(/([-.*+?^${}()|[\]\/\\])/g,"\\$1")},sequence:function(g,d,f,e){g[d]=g[d].createSequence(f,e)},addBehaviors:function(h){if(!Ext.isReady){Ext.onReady(function(){Ext.addBehaviors(h)})}else{var e={},g,d,f;for(d in h){if((g=d.split("@"))[1]){f=g[0];if(!e[f]){e[f]=Ext.select(f)}e[f].on(g[1],h[d])}}e=null}},getScrollBarWidth:function(f){if(!Ext.isReady){return 0}if(f===true||b===null){var h=Ext.getBody().createChild('<div class="x-hide-offsets" style="width:100px;height:50px;overflow:hidden;"><div style="height:200px;"></div></div>'),g=h.child("div",true);var e=g.offsetWidth;h.setStyle("overflow",(Ext.isWebKit||Ext.isGecko)?"auto":"scroll");var d=g.offsetWidth;h.remove();b=e-d+2}return b},combine:function(){var f=arguments,e=f.length,h=[];for(var g=0;g<e;g++){var d=f[g];if(Ext.isArray(d)){h=h.concat(d)}else{if(d.length!==undefined&&!d.substr){h=h.concat(Array.prototype.slice.call(d,0))}else{h.push(d)}}}return h},copyTo:function(d,e,f){if(Ext.isString(f)){f=f.split(/[,;\s]/)}Ext.each(f,function(g){if(e.hasOwnProperty(g)){d[g]=e[g]}},this);return d},destroy:function(){Ext.each(arguments,function(d){if(d){if(Ext.isArray(d)){this.destroy.apply(this,d)}else{if(Ext.isFunction(d.destroy)){d.destroy()}else{if(d.dom){d.remove()}}}}},this)},destroyMembers:function(k,h,f,g){for(var j=1,e=arguments,d=e.length;j<d;j++){Ext.destroy(k[e[j]]);delete k[e[j]]}},clean:function(d){var e=[];Ext.each(d,function(f){if(!!f){e.push(f)}});return e},unique:function(d){var e=[],f={};Ext.each(d,function(g){if(!f[g]){e.push(g)}f[g]=true});return e},flatten:function(d){var f=[];function e(g){Ext.each(g,function(h){if(Ext.isArray(h)){e(h)}else{f.push(h)}});return f}return e(d)},min:function(d,e){var f=d[0];e=e||function(h,g){return h<g?-1:1};Ext.each(d,function(g){f=e(f,g)==-1?f:g});return f},max:function(d,e){var f=d[0];e=e||function(h,g){return h>g?1:-1};Ext.each(d,function(g){f=e(f,g)==1?f:g});return f},mean:function(d){return Ext.sum(d)/d.length},sum:function(d){var e=0;Ext.each(d,function(f){e+=f});return e},partition:function(d,e){var f=[[],[]];Ext.each(d,function(h,j,g){f[(e&&e(h,j,g))||(!e&&h)?0:1].push(h)});return f},invoke:function(d,e){var g=[],f=Array.prototype.slice.call(arguments,2);Ext.each(d,function(h,j){if(h&&Ext.isFunction(h[e])){g.push(h[e].apply(h,f))}else{g.push(undefined)}});return g},pluck:function(d,f){var e=[];Ext.each(d,function(g){e.push(g[f])});return e},zip:function(){var m=Ext.partition(arguments,function(i){return !Ext.isFunction(i)}),h=m[0],l=m[1][0],d=Ext.max(Ext.pluck(h,"length")),g=[];for(var k=0;k<d;k++){g[k]=[];if(l){g[k]=l.apply(l,Ext.pluck(h,k))}else{for(var f=0,e=h.length;f<e;f++){g[k].push(h[f][k])}}}return g},getCmp:function(d){return Ext.ComponentMgr.get(d)},useShims:c.isIE6||(c.isMac&&c.isGecko2),type:function(e){if(e===undefined||e===null){return false}if(e.htmlElement){return"element"}var d=typeof e;if(d=="object"&&e.nodeName){switch(e.nodeType){case 1:return"element";case 3:return(/\S/).test(e.nodeValue)?"textnode":"whitespace"}}if(d=="object"||d=="function"){switch(e.constructor){case Array:return"array";case RegExp:return"regexp";case Date:return"date"}if(Ext.isNumber(e.length)&&Ext.isFunction(e.item)){return"nodelist"}}return d},intercept:function(g,d,f,e){g[d]=g[d].createInterceptor(f,e)},callback:function(d,g,f,e){if(Ext.isFunction(d)){if(e){d.defer(e,g,f||[])}else{d.apply(g,f||[])}}}}}());Ext.apply(Function.prototype,{createSequence:function(b,a){var c=this;return !Ext.isFunction(b)?this:function(){var d=c.apply(this||window,arguments);b.apply(a||this||window,arguments);return d}}});Ext.applyIf(String,{escape:function(a){return a.replace(/('|\\)/g,"\\$1")},leftPad:function(d,b,c){var a=String(d);if(!c){c=" "}while(a.length<b){a=c+a}return a}});String.prototype.toggle=function(b,a){return this==b?a:b};String.prototype.trim=function(){var a=/^\s+|\s+$/g;return function(){return this.replace(a,"")}}();Date.prototype.getElapsed=function(a){return Math.abs((a||new Date()).getTime()-this.getTime())};Ext.applyIf(Number.prototype,{constrain:function(b,a){return Math.min(Math.max(this,b),a)}});Ext.util.TaskRunner=function(e){e=e||10;var f=[],a=[],b=0,g=false,d=function(){g=false;clearInterval(b);b=0},h=function(){if(!g){g=true;b=setInterval(i,e)}},c=function(j){a.push(j);if(j.onStop){j.onStop.apply(j.scope||j)}},i=function(){var l=a.length,n=new Date().getTime();if(l>0){for(var p=0;p<l;p++){f.remove(a[p])}a=[];if(f.length<1){d();return}}for(var p=0,o,k,m,j=f.length;p<j;++p){o=f[p];k=n-o.taskRunTime;if(o.interval<=k){m=o.run.apply(o.scope||o,o.args||[++o.taskRunCount]);o.taskRunTime=n;if(m===false||o.taskRunCount===o.repeat){c(o);return}}if(o.duration&&o.duration<=(n-o.taskStartTime)){c(o)}}};this.start=function(j){f.push(j);j.taskStartTime=new Date().getTime();j.taskRunTime=0;j.taskRunCount=0;h();return j};this.stop=function(j){c(j);return j};this.stopAll=function(){d();for(var k=0,j=f.length;k<j;k++){if(f[k].onStop){f[k].onStop()}}f=[];a=[]}};Ext.TaskMgr=new Ext.util.TaskRunner();(function(){var b;function c(d){if(!b){b=new Ext.Element.Flyweight()}b.dom=d;return b}(function(){var g=document,e=g.compatMode=="CSS1Compat",f=Math.max,d=Math.round,h=parseInt;Ext.lib.Dom={isAncestor:function(j,k){var i=false;j=Ext.getDom(j);k=Ext.getDom(k);if(j&&k){if(j.contains){return j.contains(k)}else{if(j.compareDocumentPosition){return !!(j.compareDocumentPosition(k)&16)}else{while(k=k.parentNode){i=k==j||i}}}}return i},getViewWidth:function(i){return i?this.getDocumentWidth():this.getViewportWidth()},getViewHeight:function(i){return i?this.getDocumentHeight():this.getViewportHeight()},getDocumentHeight:function(){return f(!e?g.body.scrollHeight:g.documentElement.scrollHeight,this.getViewportHeight())},getDocumentWidth:function(){return f(!e?g.body.scrollWidth:g.documentElement.scrollWidth,this.getViewportWidth())},getViewportHeight:function(){return Ext.isIE?(Ext.isStrict?g.documentElement.clientHeight:g.body.clientHeight):self.innerHeight},getViewportWidth:function(){return !Ext.isStrict&&!Ext.isOpera?g.body.clientWidth:Ext.isIE?g.documentElement.clientWidth:self.innerWidth},getY:function(i){return this.getXY(i)[1]},getX:function(i){return this.getXY(i)[0]},getXY:function(k){var j,q,s,v,l,m,u=0,r=0,t,i,n=(g.body||g.documentElement),o=[0,0];k=Ext.getDom(k);if(k!=n){if(k.getBoundingClientRect){s=k.getBoundingClientRect();t=c(document).getScroll();o=[d(s.left+t.left),d(s.top+t.top)]}else{j=k;i=c(k).isStyle("position","absolute");while(j){q=c(j);u+=j.offsetLeft;r+=j.offsetTop;i=i||q.isStyle("position","absolute");if(Ext.isGecko){r+=v=h(q.getStyle("borderTopWidth"),10)||0;u+=l=h(q.getStyle("borderLeftWidth"),10)||0;if(j!=k&&!q.isStyle("overflow","visible")){u+=l;r+=v}}j=j.offsetParent}if(Ext.isSafari&&i){u-=n.offsetLeft;r-=n.offsetTop}if(Ext.isGecko&&!i){m=c(n);u+=h(m.getStyle("borderLeftWidth"),10)||0;r+=h(m.getStyle("borderTopWidth"),10)||0}j=k.parentNode;while(j&&j!=n){if(!Ext.isOpera||(j.tagName!="TR"&&!c(j).isStyle("display","inline"))){u-=j.scrollLeft;r-=j.scrollTop}j=j.parentNode}o=[u,r]}}return o},setXY:function(j,k){(j=Ext.fly(j,"_setXY")).position();var l=j.translatePoints(k),i=j.dom.style,m;for(m in l){if(!isNaN(l[m])){i[m]=l[m]+"px"}}},setX:function(j,i){this.setXY(j,[i,false])},setY:function(i,j){this.setXY(i,[false,j])}}})();Ext.lib.Dom.getRegion=function(d){return Ext.lib.Region.getRegion(d)};Ext.lib.Event=function(){var w=false,g={},A=0,p=[],d,C=false,k=window,G=document,l=200,s=20,B=0,r=0,i=1,m=2,t=2,x=3,u="scrollLeft",q="scrollTop",f="unload",z="mouseover",F="mouseout",e=function(){var H;if(k.addEventListener){H=function(L,J,K,I){if(J=="mouseenter"){K=K.createInterceptor(o);L.addEventListener(z,K,(I))}else{if(J=="mouseleave"){K=K.createInterceptor(o);L.addEventListener(F,K,(I))}else{L.addEventListener(J,K,(I))}}return K}}else{if(k.attachEvent){H=function(L,J,K,I){L.attachEvent("on"+J,K);return K}}else{H=function(){}}}return H}(),h=function(){var H;if(k.removeEventListener){H=function(L,J,K,I){if(J=="mouseenter"){J=z}else{if(J=="mouseleave"){J=F}}L.removeEventListener(J,K,(I))}}else{if(k.detachEvent){H=function(K,I,J){K.detachEvent("on"+I,J)}}else{H=function(){}}}return H}();function o(H){return !v(H.currentTarget,y.getRelatedTarget(H))}function v(H,I){if(H&&H.firstChild){while(I){if(I===H){return true}I=I.parentNode;if(I&&(I.nodeType!=1)){I=null}}}return false}function D(){var J=false,N=[],L,K,H,I,M=!w||(A>0);if(!C){C=true;for(K=0,H=p.length;K<H;K++){I=p[K];if(I&&(L=G.getElementById(I.id))){if(!I.checkReady||w||L.nextSibling||(G&&G.body)){L=I.override?(I.override===true?I.obj:I.override):L;I.fn.call(L,I.obj);I=null}else{N.push(I)}}}A=(N.length===0)?0:A-1;if(M){n()}else{clearInterval(d);d=null}J=!(C=false)}return J}function n(){if(!d){var H=function(){D()};d=setInterval(H,s)}}function E(){var H=G.documentElement,I=G.body;if(H&&(H[q]||H[u])){return[H[u],H[q]]}else{if(I){return[I[u],I[q]]}else{return[0,0]}}}function j(H,I){H=H.browserEvent||H;var J=H["page"+I];if(!J&&J!==0){J=H["client"+I]||0;if(Ext.isIE){J+=E()[I=="X"?0:1]}}return J}var y={extAdapter:true,onAvailable:function(J,H,K,I){p.push({id:J,fn:H,obj:K,override:I,checkReady:false});A=l;n()},addListener:function(J,H,I){J=Ext.getDom(J);if(J&&I){if(H==f){if(g[J.id]===undefined){g[J.id]=[]}g[J.id].push([H,I]);return I}return e(J,H,I,false)}return false},removeListener:function(M,J,L){M=Ext.getDom(M);var K,I,H;if(M&&L){if(J==f){if(g[id]!==undefined){for(K=0,I=g[id].length;K<I;K++){H=g[id][K];if(H&&H[r]==J&&H[i]==L){g[id].splice(K,1)}}}return}h(M,J,L,false)}},getTarget:function(H){H=H.browserEvent||H;return this.resolveTextNode(H.target||H.srcElement)},resolveTextNode:Ext.isGecko?function(I){if(!I){return}var H=HTMLElement.prototype.toString.call(I);if(H=="[xpconnect wrapped native prototype]"||H=="[object XULElement]"){return}return I.nodeType==3?I.parentNode:I}:function(H){return H&&H.nodeType==3?H.parentNode:H},getRelatedTarget:function(H){H=H.browserEvent||H;return this.resolveTextNode(H.relatedTarget||(H.type==F?H.toElement:H.type==z?H.fromElement:null))},getPageX:function(H){return j(H,"X")},getPageY:function(H){return j(H,"Y")},getXY:function(H){return[this.getPageX(H),this.getPageY(H)]},stopEvent:function(H){this.stopPropagation(H);this.preventDefault(H)},stopPropagation:function(H){H=H.browserEvent||H;if(H.stopPropagation){H.stopPropagation()}else{H.cancelBubble=true}},preventDefault:function(H){H=H.browserEvent||H;if(H.preventDefault){H.preventDefault()}else{H.returnValue=false}},getEvent:function(H){H=H||k.event;if(!H){var I=this.getEvent.caller;while(I){H=I.arguments[0];if(H&&Event==H.constructor){break}I=I.caller}}return H},getCharCode:function(H){H=H.browserEvent||H;return H.charCode||H.keyCode||0},getListeners:function(I,H){Ext.EventManager.getListeners(I,H)},purgeElement:function(I,J,H){Ext.EventManager.purgeElement(I,J,H)},_load:function(I){w=true;var H=Ext.lib.Event;if(Ext.isIE&&I!==true){h(k,"load",arguments.callee)}},_unload:function(O){var I=Ext.lib.Event,L,K,J,R,P,H,N,M,S;for(H in g){P=g[H];for(L=0,N=P.length;L<N;L++){R=P[L];if(R){try{S=R[x]?(R[x]===true?R[t]:R[x]):k;R[i].call(S,I.getEvent(O),R[t])}catch(Q){}}}}g=null;Ext.EventManager._unload();h(k,f,I._unload)}};y.on=y.addListener;y.un=y.removeListener;if(G&&G.body){y._load(true)}else{e(k,"load",y._load)}e(k,f,y._unload);D();return y}();Ext.lib.Ajax=function(){var g=["MSXML2.XMLHTTP.3.0","MSXML2.XMLHTTP","Microsoft.XMLHTTP"],d="Content-Type";function h(s){var r=s.conn,t;function q(u,v){for(t in v){if(v.hasOwnProperty(t)){u.setRequestHeader(t,v[t])}}}if(k.defaultHeaders){q(r,k.defaultHeaders)}if(k.headers){q(r,k.headers);delete k.headers}}function e(t,s,r,q){return{tId:t,status:r?-1:0,statusText:r?"transaction aborted":"communication failure",isAbort:r,isTimeout:q,argument:s}}function j(q,r){(k.headers=k.headers||{})[q]=r}function o(z,x){var r={},v,w=z.conn,q,u;try{v=z.conn.getAllResponseHeaders();Ext.each(v.replace(/\r\n/g,"\n").split("\n"),function(s){q=s.indexOf(":");if(q>=0){u=s.substr(0,q).toLowerCase();if(s.charAt(q+1)==" "){++q}r[u]=s.substr(q+1)}})}catch(y){}return{tId:z.tId,status:w.status,statusText:w.statusText,getResponseHeader:function(s){return r[s.toLowerCase()]},getAllResponseHeaders:function(){return v},responseText:w.responseText,responseXML:w.responseXML,argument:x}}function n(q){q.conn=null;q=null}function f(v,w,r,q){if(!w){n(v);return}var t,s;try{if(v.conn.status!==undefined&&v.conn.status!=0){t=v.conn.status}else{t=13030}}catch(u){t=13030}if((t>=200&&t<300)||(Ext.isIE&&t==1223)){s=o(v,w.argument);if(w.success){if(!w.scope){w.success(s)}else{w.success.apply(w.scope,[s])}}}else{switch(t){case 12002:case 12029:case 12030:case 12031:case 12152:case 13030:s=e(v.tId,w.argument,(r?r:false),q);if(w.failure){if(!w.scope){w.failure(s)}else{w.failure.apply(w.scope,[s])}}break;default:s=o(v,w.argument);if(w.failure){if(!w.scope){w.failure(s)}else{w.failure.apply(w.scope,[s])}}}}n(v);s=null}function m(s,v){v=v||{};var q=s.conn,u=s.tId,r=k.poll,t=v.timeout||null;if(t){k.timeout[u]=setTimeout(function(){k.abort(s,v,true)},t)}r[u]=setInterval(function(){if(q&&q.readyState==4){clearInterval(r[u]);r[u]=null;if(t){clearTimeout(k.timeout[u]);k.timeout[u]=null}f(s,v)}},k.pollInterval)}function i(u,r,t,q){var s=l()||null;if(s){s.conn.open(u,r,true);if(k.useDefaultXhrHeader){j("X-Requested-With",k.defaultXhrHeader)}if(q&&k.useDefaultHeader&&(!k.headers||!k.headers[d])){j(d,k.defaultPostHeader)}if(k.defaultHeaders||k.headers){h(s)}m(s,t);s.conn.send(q||null)}return s}function l(){var r;try{if(r=p(k.transactionId)){k.transactionId++}}catch(q){}finally{return r}}function p(t){var q;try{q=new XMLHttpRequest()}catch(s){for(var r=0;r<g.length;++r){try{q=new ActiveXObject(g[r]);break}catch(s){}}}finally{return{conn:q,tId:t}}}var k={request:function(q,s,t,u,y){if(y){var v=this,r=y.xmlData,w=y.jsonData,x;Ext.applyIf(v,y);if(r||w){x=v.headers;if(!x||!x[d]){j(d,r?"text/xml":"application/json")}u=r||(!Ext.isPrimitive(w)?Ext.encode(w):w)}}return i(q||y.method||"POST",s,t,u)},serializeForm:function(r){var s=r.elements||(document.forms[r]||Ext.getDom(r)).elements,y=false,x=encodeURIComponent,v,z,q,t,u="",w;Ext.each(s,function(A){q=A.name;w=A.type;if(!A.disabled&&q){if(/select-(one|multiple)/i.test(w)){Ext.each(A.options,function(B){if(B.selected){u+=String.format("{0}={1}&",x(q),x((B.hasAttribute?B.hasAttribute("value"):B.getAttribute("value")!==null)?B.value:B.text))}})}else{if(!/file|undefined|reset|button/i.test(w)){if(!(/radio|checkbox/i.test(w)&&!A.checked)&&!(w=="submit"&&y)){u+=x(q)+"="+x(A.value)+"&";y=/submit/i.test(w)}}}}});return u.substr(0,u.length-1)},useDefaultHeader:true,defaultPostHeader:"application/x-www-form-urlencoded; charset=UTF-8",useDefaultXhrHeader:true,defaultXhrHeader:"XMLHttpRequest",poll:{},timeout:{},pollInterval:50,transactionId:0,abort:function(t,v,q){var s=this,u=t.tId,r=false;if(s.isCallInProgress(t)){t.conn.abort();clearInterval(s.poll[u]);s.poll[u]=null;clearTimeout(k.timeout[u]);s.timeout[u]=null;f(t,v,(r=true),q)}return r},isCallInProgress:function(q){return q.conn&&!{0:true,4:true}[q.conn.readyState]}};return k}();Ext.lib.Region=function(f,h,d,e){var g=this;g.top=f;g[1]=f;g.right=h;g.bottom=d;g.left=e;g[0]=e};Ext.lib.Region.prototype={contains:function(e){var d=this;return(e.left>=d.left&&e.right<=d.right&&e.top>=d.top&&e.bottom<=d.bottom)},getArea:function(){var d=this;return((d.bottom-d.top)*(d.right-d.left))},intersect:function(i){var h=this,f=Math.max(h.top,i.top),g=Math.min(h.right,i.right),d=Math.min(h.bottom,i.bottom),e=Math.max(h.left,i.left);if(d>=f&&g>=e){return new Ext.lib.Region(f,g,d,e)}},union:function(i){var h=this,f=Math.min(h.top,i.top),g=Math.max(h.right,i.right),d=Math.max(h.bottom,i.bottom),e=Math.min(h.left,i.left);return new Ext.lib.Region(f,g,d,e)},constrainTo:function(e){var d=this;d.top=d.top.constrain(e.top,e.bottom);d.bottom=d.bottom.constrain(e.top,e.bottom);d.left=d.left.constrain(e.left,e.right);d.right=d.right.constrain(e.left,e.right);return d},adjust:function(f,e,d,h){var g=this;g.top+=f;g.left+=e;g.right+=h;g.bottom+=d;return g}};Ext.lib.Region.getRegion=function(g){var i=Ext.lib.Dom.getXY(g),f=i[1],h=i[0]+g.offsetWidth,d=i[1]+g.offsetHeight,e=i[0];return new Ext.lib.Region(f,h,d,e)};Ext.lib.Point=function(d,f){if(Ext.isArray(d)){f=d[1];d=d[0]}var e=this;e.x=e.right=e.left=e[0]=d;e.y=e.top=e.bottom=e[1]=f};Ext.lib.Point.prototype=new Ext.lib.Region();(function(){var g=Ext.lib,i=/width|height|opacity|padding/i,f=/^((width|height)|(top|left))$/,d=/width|height|top$|bottom$|left$|right$/i,h=/\d+(em|%|en|ex|pt|in|cm|mm|pc)$/i,j=function(k){return typeof k!=="undefined"},e=function(){return new Date()};g.Anim={motion:function(n,l,o,p,k,m){return this.run(n,l,o,p,k,m,Ext.lib.Motion)},run:function(o,l,q,r,k,n,m){m=m||Ext.lib.AnimBase;if(typeof r=="string"){r=Ext.lib.Easing[r]}var p=new m(o,l,q,r);p.animateX(function(){if(Ext.isFunction(k)){k.call(n)}});return p}};g.AnimBase=function(l,k,m,n){if(l){this.init(l,k,m,n)}};g.AnimBase.prototype={doMethod:function(k,n,l){var m=this;return m.method(m.curFrame,n,l-n,m.totalFrames)},setAttr:function(k,m,l){if(i.test(k)&&m<0){m=0}Ext.fly(this.el,"_anim").setStyle(k,m+l)},getAttr:function(k){var m=Ext.fly(this.el),n=m.getStyle(k),l=f.exec(k)||[];if(n!=="auto"&&!h.test(n)){return parseFloat(n)}return(!!(l[2])||(m.getStyle("position")=="absolute"&&!!(l[3])))?m.dom["offset"+l[0].charAt(0).toUpperCase()+l[0].substr(1)]:0},getDefaultUnit:function(k){return d.test(k)?"px":""},animateX:function(n,k){var l=this,m=function(){l.onComplete.removeListener(m);if(Ext.isFunction(n)){n.call(k||l,l)}};l.onComplete.addListener(m,l);l.animate()},setRunAttr:function(p){var r=this,s=this.attributes[p],t=s.to,q=s.by,u=s.from,v=s.unit,l=(this.runAttrs[p]={}),m;if(!j(t)&&!j(q)){return false}var k=j(u)?u:r.getAttr(p);if(j(t)){m=t}else{if(j(q)){if(Ext.isArray(k)){m=[];for(var n=0,o=k.length;n<o;n++){m[n]=k[n]+q[n]}}else{m=k+q}}}Ext.apply(l,{start:k,end:m,unit:j(v)?v:r.getDefaultUnit(p)})},init:function(l,p,o,k){var r=this,n=0,s=g.AnimMgr;Ext.apply(r,{isAnimated:false,startTime:null,el:Ext.getDom(l),attributes:p||{},duration:o||1,method:k||g.Easing.easeNone,useSec:true,curFrame:0,totalFrames:s.fps,runAttrs:{},animate:function(){var u=this,v=u.duration;if(u.isAnimated){return false}u.curFrame=0;u.totalFrames=u.useSec?Math.ceil(s.fps*v):v;s.registerElement(u)},stop:function(u){var v=this;if(u){v.curFrame=v.totalFrames;v._onTween.fire()}s.stop(v)}});var t=function(){var v=this,u;v.onStart.fire();v.runAttrs={};for(u in this.attributes){this.setRunAttr(u)}v.isAnimated=true;v.startTime=e();n=0};var q=function(){var v=this;v.onTween.fire({duration:e()-v.startTime,curFrame:v.curFrame});var w=v.runAttrs;for(var u in w){this.setAttr(u,v.doMethod(u,w[u].start,w[u].end),w[u].unit)}++n};var m=function(){var u=this,w=(e()-u.startTime)/1000,v={duration:w,frames:n,fps:n/w};u.isAnimated=false;n=0;u.onComplete.fire(v)};r.onStart=new Ext.util.Event(r);r.onTween=new Ext.util.Event(r);r.onComplete=new Ext.util.Event(r);(r._onStart=new Ext.util.Event(r)).addListener(t);(r._onTween=new Ext.util.Event(r)).addListener(q);(r._onComplete=new Ext.util.Event(r)).addListener(m)}};Ext.lib.AnimMgr=new function(){var o=this,m=null,l=[],k=0;Ext.apply(o,{fps:1000,delay:1,registerElement:function(q){l.push(q);++k;q._onStart.fire();o.start()},unRegister:function(r,q){r._onComplete.fire();q=q||p(r);if(q!=-1){l.splice(q,1)}if(--k<=0){o.stop()}},start:function(){if(m===null){m=setInterval(o.run,o.delay)}},stop:function(s){if(!s){clearInterval(m);for(var r=0,q=l.length;r<q;++r){if(l[0].isAnimated){o.unRegister(l[0],0)}}l=[];m=null;k=0}else{o.unRegister(s)}},run:function(){var t,s,q,r;for(s=0,q=l.length;s<q;s++){r=l[s];if(r&&r.isAnimated){t=r.totalFrames;if(r.curFrame<t||t===null){++r.curFrame;if(r.useSec){n(r)}r._onTween.fire()}else{o.stop(r)}}}}});var p=function(s){var r,q;for(r=0,q=l.length;r<q;r++){if(l[r]===s){return r}}return -1};var n=function(r){var v=r.totalFrames,u=r.curFrame,t=r.duration,s=(u*t*1000/v),q=(e()-r.startTime),w=0;if(q<t*1000){w=Math.round((q/s-1)*u)}else{w=v-(u+1)}if(w>0&&isFinite(w)){if(r.curFrame+w>=v){w=v-(u+1)}r.curFrame+=w}}};g.Bezier=new function(){this.getPosition=function(p,o){var r=p.length,m=[],q=1-o,l,k;for(l=0;l<r;++l){m[l]=[p[l][0],p[l][1]]}for(k=1;k<r;++k){for(l=0;l<r-k;++l){m[l][0]=q*m[l][0]+o*m[parseInt(l+1,10)][0];m[l][1]=q*m[l][1]+o*m[parseInt(l+1,10)][1]}}return[m[0][0],m[0][1]]}};g.Easing={easeNone:function(l,k,n,m){return n*l/m+k},easeIn:function(l,k,n,m){return n*(l/=m)*l+k},easeOut:function(l,k,n,m){return -n*(l/=m)*(l-2)+k}};(function(){g.Motion=function(p,o,q,r){if(p){g.Motion.superclass.constructor.call(this,p,o,q,r)}};Ext.extend(g.Motion,Ext.lib.AnimBase);var n=g.Motion.superclass,m=g.Motion.prototype,l=/^points$/i;Ext.apply(g.Motion.prototype,{setAttr:function(o,s,r){var q=this,p=n.setAttr;if(l.test(o)){r=r||"px";p.call(q,"left",s[0],r);p.call(q,"top",s[1],r)}else{p.call(q,o,s,r)}},getAttr:function(o){var q=this,p=n.getAttr;return l.test(o)?[p.call(q,"left"),p.call(q,"top")]:p.call(q,o)},doMethod:function(o,r,p){var q=this;return l.test(o)?g.Bezier.getPosition(q.runAttrs[o],q.method(q.curFrame,0,100,q.totalFrames)/100):n.doMethod.call(q,o,r,p)},setRunAttr:function(v){if(l.test(v)){var x=this,q=this.el,A=this.attributes.points,t=A.control||[],y=A.from,z=A.to,w=A.by,B=g.Dom,p,s,r,u,o;if(t.length>0&&!Ext.isArray(t[0])){t=[t]}else{}Ext.fly(q,"_anim").position();B.setXY(q,j(y)?y:B.getXY(q));p=x.getAttr("points");if(j(z)){r=k.call(x,z,p);for(s=0,u=t.length;s<u;++s){t[s]=k.call(x,t[s],p)}}else{if(j(w)){r=[p[0]+w[0],p[1]+w[1]];for(s=0,u=t.length;s<u;++s){t[s]=[p[0]+t[s][0],p[1]+t[s][1]]}}}o=this.runAttrs[v]=[p];if(t.length>0){o=o.concat(t)}o[o.length]=r}else{n.setRunAttr.call(this,v)}}});var k=function(o,q){var p=g.Dom.getXY(this.el);return[o[0]-p[0]+q[0],o[1]-p[1]+q[1]]}})()})();(function(){var d=Math.abs,i=Math.PI,h=Math.asin,g=Math.pow,e=Math.sin,f=Ext.lib;Ext.apply(f.Easing,{easeBoth:function(k,j,m,l){return((k/=l/2)<1)?m/2*k*k+j:-m/2*((--k)*(k-2)-1)+j},easeInStrong:function(k,j,m,l){return m*(k/=l)*k*k*k+j},easeOutStrong:function(k,j,m,l){return -m*((k=k/l-1)*k*k*k-1)+j},easeBothStrong:function(k,j,m,l){return((k/=l/2)<1)?m/2*k*k*k*k+j:-m/2*((k-=2)*k*k*k-2)+j},elasticIn:function(l,j,q,o,k,n){if(l==0||(l/=o)==1){return l==0?j:j+q}n=n||(o*0.3);var m;if(k>=d(q)){m=n/(2*i)*h(q/k)}else{k=q;m=n/4}return -(k*g(2,10*(l-=1))*e((l*o-m)*(2*i)/n))+j},elasticOut:function(l,j,q,o,k,n){if(l==0||(l/=o)==1){return l==0?j:j+q}n=n||(o*0.3);var m;if(k>=d(q)){m=n/(2*i)*h(q/k)}else{k=q;m=n/4}return k*g(2,-10*l)*e((l*o-m)*(2*i)/n)+q+j},elasticBoth:function(l,j,q,o,k,n){if(l==0||(l/=o/2)==2){return l==0?j:j+q}n=n||(o*(0.3*1.5));var m;if(k>=d(q)){m=n/(2*i)*h(q/k)}else{k=q;m=n/4}return l<1?-0.5*(k*g(2,10*(l-=1))*e((l*o-m)*(2*i)/n))+j:k*g(2,-10*(l-=1))*e((l*o-m)*(2*i)/n)*0.5+q+j},backIn:function(k,j,n,m,l){l=l||1.70158;return n*(k/=m)*k*((l+1)*k-l)+j},backOut:function(k,j,n,m,l){if(!l){l=1.70158}return n*((k=k/m-1)*k*((l+1)*k+l)+1)+j},backBoth:function(k,j,n,m,l){l=l||1.70158;return((k/=m/2)<1)?n/2*(k*k*(((l*=(1.525))+1)*k-l))+j:n/2*((k-=2)*k*(((l*=(1.525))+1)*k+l)+2)+j},bounceIn:function(k,j,m,l){return m-f.Easing.bounceOut(l-k,0,m,l)+j},bounceOut:function(k,j,m,l){if((k/=l)<(1/2.75)){return m*(7.5625*k*k)+j}else{if(k<(2/2.75)){return m*(7.5625*(k-=(1.5/2.75))*k+0.75)+j}else{if(k<(2.5/2.75)){return m*(7.5625*(k-=(2.25/2.75))*k+0.9375)+j}}}return m*(7.5625*(k-=(2.625/2.75))*k+0.984375)+j},bounceBoth:function(k,j,m,l){return(k<l/2)?f.Easing.bounceIn(k*2,0,m,l)*0.5+j:f.Easing.bounceOut(k*2-l,0,m,l)*0.5+m*0.5+j}})})();(function(){var h=Ext.lib;h.Anim.color=function(p,n,q,r,m,o){return h.Anim.run(p,n,q,r,m,o,h.ColorAnim)};h.ColorAnim=function(n,m,o,p){h.ColorAnim.superclass.constructor.call(this,n,m,o,p)};Ext.extend(h.ColorAnim,h.AnimBase);var j=h.ColorAnim.superclass,i=/color$/i,f=/^transparent|rgba\(0, 0, 0, 0\)$/,l=/^rgb\(([0-9]+)\s*,\s*([0-9]+)\s*,\s*([0-9]+)\)$/i,d=/^#?([0-9A-F]{2})([0-9A-F]{2})([0-9A-F]{2})$/i,e=/^#?([0-9A-F]{1})([0-9A-F]{1})([0-9A-F]{1})$/i,g=function(m){return typeof m!=="undefined"};function k(n){var p=parseInt,o,m=null,q;if(n.length==3){return n}Ext.each([d,l,e],function(s,r){o=(r%2==0)?16:10;q=s.exec(n);if(q&&q.length==4){m=[p(q[1],o),p(q[2],o),p(q[3],o)];return false}});return m}Ext.apply(h.ColorAnim.prototype,{getAttr:function(m){var o=this,n=o.el,p;if(i.test(m)){while(n&&f.test(p=Ext.fly(n).getStyle(m))){n=n.parentNode;p="fff"}}else{p=j.getAttr.call(o,m)}return p},doMethod:function(s,m,o){var t=this,n,q=Math.floor,p,r=m.length,u;if(i.test(s)){n=[];for(p=0;p<r;p++){u=m[p];n[p]=j.doMethod.call(t,s,u,o[p])}n="rgb("+q(n[0])+","+q(n[1])+","+q(n[2])+")"}else{n=j.doMethod.call(t,s,m,o)}return n},setRunAttr:function(r){var t=this,u=t.attributes[r],v=u.to,s=u.by,n;j.setRunAttr.call(t,r);n=t.runAttrs[r];if(i.test(r)){var m=k(n.start),o=k(n.end);if(!g(v)&&g(s)){o=k(s);for(var p=0,q=m.length;p<q;p++){o[p]=m[p]+o[p]}}n.start=m;n.end=o}}})})();(function(){var d=Ext.lib;d.Anim.scroll=function(j,h,k,l,g,i){return d.Anim.run(j,h,k,l,g,i,d.Scroll)};d.Scroll=function(h,g,i,j){if(h){d.Scroll.superclass.constructor.call(this,h,g,i,j)}};Ext.extend(d.Scroll,d.ColorAnim);var f=d.Scroll.superclass,e="scroll";Ext.apply(d.Scroll.prototype,{doMethod:function(g,m,h){var k,j=this,l=j.curFrame,i=j.totalFrames;if(g==e){k=[j.method(l,m[0],h[0]-m[0],i),j.method(l,m[1],h[1]-m[1],i)]}else{k=f.doMethod.call(j,g,m,h)}return k},getAttr:function(g){var h=this;if(g==e){return[h.el.scrollLeft,h.el.scrollTop]}else{return f.getAttr.call(h,g)}},setAttr:function(g,j,i){var h=this;if(g==e){h.el.scrollLeft=j[0];h.el.scrollTop=j[1]}else{f.setAttr.call(h,g,j,i)}}})})();if(Ext.isIE){function a(){var d=Function.prototype;delete d.createSequence;delete d.defer;delete d.createDelegate;delete d.createCallback;delete d.createInterceptor;window.detachEvent("onunload",a)}window.attachEvent("onunload",a)}})();/*!
 * Ext JS Library 3.0.0
 * Copyright(c) 2006-2009 Ext JS, LLC
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
/*
 * Simplified Chinese translation
 * By DavidHu
 * 09 April 2007
 */

Ext.UpdateManager.defaults.indicatorText = '<div class="loading-indicator">åŠ è½½ä¸­...</div>';

if(Ext.View){
   Ext.View.prototype.emptyText = "";
}

if(Ext.grid.GridPanel){
   Ext.grid.GridPanel.prototype.ddText = "{0} é€‰æ‹©è¡Œ";
}

if(Ext.TabPanelItem){
   Ext.TabPanelItem.prototype.closeText = "å…³é—­";
}

if(Ext.form.Field){
   Ext.form.Field.prototype.invalidText = "è¾“å…¥å€¼éæ³•";
}

Date.monthNames = [
   "ä¸€æœˆ",
   "äºŒæœˆ",
   "ä¸‰æœˆ",
   "å››æœˆ",
   "äº”æœˆ",
   "å…­æœˆ",
   "ä¸ƒæœˆ",
   "å…«æœˆ",
   "ä¹æœˆ",
   "åæœˆ",
   "åä¸€æœˆ",
   "åäºŒæœˆ"
];

Date.dayNames = [
   "æ—¥",
   "ä¸€",
   "äºŒ",
   "ä¸‰",
   "å››",
   "äº”",
   "å…­"
];

if(Ext.MessageBox){
   Ext.MessageBox.buttonText = {
      ok     : "ç¡®å®š",
      cancel : "å–æ¶ˆ",
      yes    : "æ˜¯",
      no     : "å¦"
   };
}

if(Ext.util.Format){
   Ext.util.Format.date = function(v, format){
      if(!v) return "";
      if(!(v instanceof Date)) v = new Date(Date.parse(v));
      return v.dateFormat(format || "yå¹´mæœˆdæ—¥");
   };
}

if(Ext.DatePicker){
   Ext.apply(Ext.DatePicker.prototype, {
      todayText         : "ä»Šå¤©",
      minText           : "æ—¥æœŸåœ¨æœ€å°æ—¥æœŸä¹‹å‰",
      maxText           : "æ—¥æœŸåœ¨æœ€å¤§æ—¥æœŸä¹‹å",
      disabledDaysText  : "",
      disabledDatesText : "",
      monthNames        : Date.monthNames,
      dayNames          : Date.dayNames,
      nextText          : 'ä¸‹æœˆ (Control+Right)',
      prevText          : 'ä¸Šæœˆ (Control+Left)',
      monthYearText     : 'é€‰æ‹©ä¸€ä¸ªæœˆ (Control+Up/Down æ¥æ”¹å˜å¹´)',
      todayTip          : "{0} (ç©ºæ ¼é”®é€‰æ‹©)",
      format            : "yå¹´mæœˆdæ—¥",
      okText            : "ç¡®å®š",
      cancelText        : "å–æ¶ˆ"
   });
}

if(Ext.PagingToolbar){
   Ext.apply(Ext.PagingToolbar.prototype, {
      beforePageText : "é¡µ",
      afterPageText  : "é¡µå…± {0} é¡µ",
      firstText      : "ç¬¬ä¸€é¡µ",
      prevText       : "å‰ä¸€é¡µ",
      nextText       : "ä¸‹ä¸€é¡µ",
      lastText       : "æœ€åé¡µ",
      refreshText    : "åˆ·æ–°",
      displayMsg     : "æ˜¾ç¤º {0} - {1}ï¼Œå…± {2} æ¡",
      emptyMsg       : 'æ²¡æœ‰æ•°æ®éœ€è¦æ˜¾ç¤º'
   });
}

if(Ext.form.TextField){
   Ext.apply(Ext.form.TextField.prototype, {
      minLengthText : "è¯¥è¾“å…¥é¡¹çš„æœ€å°é•¿åº¦æ˜¯ {0}",
      maxLengthText : "è¯¥è¾“å…¥é¡¹çš„æœ€å¤§é•¿åº¦æ˜¯ {0}",
      blankText     : "è¯¥è¾“å…¥é¡¹ä¸ºå¿…è¾“é¡¹",
      regexText     : "",
      emptyText     : null
   });
}

if(Ext.form.NumberField){
   Ext.apply(Ext.form.NumberField.prototype, {
      minText : "è¯¥è¾“å…¥é¡¹çš„æœ€å°å€¼æ˜¯ {0}",
      maxText : "è¯¥è¾“å…¥é¡¹çš„æœ€å¤§å€¼æ˜¯ {0}",
      nanText : "{0} ä¸æ˜¯æœ‰æ•ˆæ•°å€¼"
   });
}

if(Ext.form.DateField){
   Ext.apply(Ext.form.DateField.prototype, {
      disabledDaysText  : "ç¦ç”¨",
      disabledDatesText : "ç¦ç”¨",
      minText           : "è¯¥è¾“å…¥é¡¹çš„æ—¥æœŸå¿…é¡»åœ¨ {0} ä¹‹å",
      maxText           : "è¯¥è¾“å…¥é¡¹çš„æ—¥æœŸå¿…é¡»åœ¨ {0} ä¹‹å‰",
      invalidText       : "{0} æ˜¯æ— æ•ˆçš„æ—¥æœŸ - å¿…é¡»ç¬¦åˆæ ¼å¼ï¼š {1}",
      format            : "yå¹´mæœˆdæ—¥"
   });
}

if(Ext.form.ComboBox){
   Ext.apply(Ext.form.ComboBox.prototype, {
      loadingText       : "åŠ è½½...",
      valueNotFoundText : undefined
   });
}

if(Ext.form.VTypes){
   Ext.apply(Ext.form.VTypes, {
      emailText    : 'è¯¥è¾“å…¥é¡¹å¿…é¡»æ˜¯ç”µå­é‚®ä»¶åœ°å€ï¼Œæ ¼å¼å¦‚ï¼š "user@example.com"',
      urlText      : 'è¯¥è¾“å…¥é¡¹å¿…é¡»æ˜¯URLåœ°å€ï¼Œæ ¼å¼å¦‚ï¼š "http:/'+'/www.example.com"',
      alphaText    : 'è¯¥è¾“å…¥é¡¹åªèƒ½åŒ…å«å­—ç¬¦å’Œ_',
      alphanumText : 'è¯¥è¾“å…¥é¡¹åªèƒ½åŒ…å«å­—ç¬¦,æ•°å­—å’Œ_'
   });
}

if(Ext.grid.GridView){
   Ext.apply(Ext.grid.GridView.prototype, {
      sortAscText  : "æ­£åº",
      sortDescText : "é€†åº",
      lockText     : "é”åˆ—",
      unlockText   : "è§£é”åˆ—",
      columnsText  : "åˆ—"
   });
}

if(Ext.grid.PropertyColumnModel){
   Ext.apply(Ext.grid.PropertyColumnModel.prototype, {
      nameText   : "åç§°",
      valueText  : "å€¼",
      dateFormat : "yå¹´mæœˆdæ—¥"
   });
}

if(Ext.layout.BorderLayout && Ext.layout.BorderLayout.SplitRegion){
   Ext.apply(Ext.layout.BorderLayout.SplitRegion.prototype, {
      splitTip            : "æ‹–åŠ¨æ¥æ”¹å˜å°ºå¯¸.",
      collapsibleSplitTip : "æ‹–åŠ¨æ¥æ”¹å˜å°ºå¯¸. åŒå‡»éšè—."
   });
}
Ext.namespace("Ext.ux.plugins");

Ext.ux.plugins.GroupHeaderGrid = function(config) {
	Ext.apply(this, config);
};

Ext.extend(Ext.ux.plugins.GroupHeaderGrid, Ext.util.Observable, {
	init: function(grid) {
		var v = grid.getView();
		v.beforeMethod('initTemplates', this.initTemplates);
		v.renderHeaders = this.renderHeaders.createDelegate(v, [v.renderHeaders]);
        v.afterMethod('onColumnWidthUpdated', this.updateGroupStyles);
        v.afterMethod('onAllColumnWidthsUpdated', this.updateGroupStyles);
		v.afterMethod('onColumnHiddenUpdated', this.updateGroupStyles);
		v.getHeaderCell = this.getHeaderCell;
		v.updateSortIcon = this.updateSortIcon;
		v.getGroupStyle = this.getGroupStyle;
	},

	initTemplates: function() {
		var ts = this.templates || {};
		if (!ts.gcell) {
			ts.gcell = new Ext.Template(
				'<td class="x-grid3-hd {cls} x-grid3-td-{id}" style="{style}">',
				'<div {tooltip} class="x-grid3-hd-inner x-grid3-hd-{id}" unselectable="on" style="{istyle}">{value}</div>',
				'</td>'
			);
		}
		this.templates = ts;
	},

	renderHeaders: function(renderHeaders) {
		var ts = this.templates, rows = [], tw = this.getTotalWidth();
		for (var i = 0; i < this.cm.rows.length; i++) {
			var r = this.cm.rows[i], cells = [], col = 0;
			for (var j = 0; j < r.length; j++) {
				var c = r[j];
				c.colspan = c.colspan || 1;
				c.col = col;
				col += c.colspan;
				var gs = this.getGroupStyle(c);
				cells[j] = ts.gcell.apply({
					id: c.id || i + '-' + col,
					cls: c.header ? 'ux-grid-hd-group-cell' : 'ux-grid-hd-nogroup-cell',
					style: 'width:' + gs.width + ';' + (gs.hidden ? 'display:none;' : '') + (c.align ? 'text-align:' + c.align + ';' : ''),
					tooltip: c.tooltip ? (Ext.QuickTips.isEnabled() ? 'ext:qtip' : 'title') + '="' + c.tooltip + '"' : '',
					value: c.header || '&#160;',
					istyle: c.align == 'right' ? 'padding-right:16px' : ''
				});
			}
			rows[i] = ts.header.apply({
				tstyle: 'width:' + tw + ';',
				cells: cells.join('')
			});
		}
		rows[rows.length] = renderHeaders.call(this);
		return rows.join('');
	},

	getGroupStyle: function(c) {
		var w = 0, h = true;
		for (var i = c.col; i < c.col + c.colspan; i++) {
			if (!this.cm.isHidden(i)) {
				var cw = this.cm.getColumnWidth(i);
				if(typeof cw == 'number'){
					w += cw;
				}
				h = false;
			}
		}
		return {
			width: (Ext.isBorderBox ? w : Math.max(w - this.borderWidth, 0)) + 'px',
			hidden: h
		}
	},

	updateGroupStyles: function(col) {
		var tables = this.mainHd.query('.x-grid3-header-offset > table'), tw = this.getTotalWidth();
		for (var i = 0; i < tables.length; i++) {
			tables[i].style.width = tw;
			if (i < this.cm.rows.length) {
				var cells = tables[i].firstChild.firstChild.childNodes;
				for (var j = 0; j < cells.length; j++) {
					var c = this.cm.rows[i][j];
					if ((typeof col != 'number') || (col >= c.col && col < c.col + c.colspan)) {
						var gs = this.getGroupStyle(c);
						cells[j].style.width = gs.width;
						cells[j].style.display = gs.hidden ? 'none' : '';
					}
				}
			}
		}
	},

	getHeaderCell : function(index){
		return this.mainHd.query('td.x-grid3-cell')[index];
	},

	updateSortIcon : function(col, dir){
		var sc = this.sortClasses;
		var hds = this.mainHd.select('td.x-grid3-cell').removeClass(sc);
		hds.item(col).addClass(sc[dir == "DESC" ? 1 : 0]);
	}
});
/*!
 * Ext JS Library 3.0.0
 * Copyright(c) 2006-2009 Ext JS, LLC
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
Ext.ns('Ext.ux.grid');

/**
 * @class Ext.ux.grid.RowExpander
 * @extends Ext.util.Observable
 * Plugin (ptype = 'rowexpander') that adds the ability to have a Column in a grid which enables
 * a second row body which expands/contracts.  The expand/contract behavior is configurable to react
 * on clicking of the column, double click of the row, and/or hitting enter while a row is selected.
 *
 * @ptype rowexpander
 */
Ext.ux.grid.RowExpander = Ext.extend(Ext.util.Observable, {
    /**
     * @cfg {Boolean} expandOnEnter
     * <tt>true</tt> to toggle selected row(s) between expanded/collapsed when the enter
     * key is pressed (defaults to <tt>true</tt>).
     */
    expandOnEnter : true,
    /**
     * @cfg {Boolean} expandOnDblClick
     * <tt>true</tt> to toggle a row between expanded/collapsed when double clicked
     * (defaults to <tt>true</tt>).
     */
    expandOnDblClick : true,

    header : '',
    width : 20,
    sortable : false,
    fixed : true,
    menuDisabled : true,
    dataIndex : '',
    id : 'expander',
    lazyRender : true,
    enableCaching : true,

    constructor: function(config){
        Ext.apply(this, config);

        this.addEvents({
            /**
             * @event beforeexpand
             * Fires before the row expands. Have the listener return false to prevent the row from expanding.
             * @param {Object} this RowExpander object.
             * @param {Object} Ext.data.Record Record for the selected row.
             * @param {Object} body body element for the secondary row.
             * @param {Number} rowIndex The current row index.
             */
            beforeexpand: true,
            /**
             * @event expand
             * Fires after the row expands.
             * @param {Object} this RowExpander object.
             * @param {Object} Ext.data.Record Record for the selected row.
             * @param {Object} body body element for the secondary row.
             * @param {Number} rowIndex The current row index.
             */
            expand: true,
            /**
             * @event beforecollapse
             * Fires before the row collapses. Have the listener return false to prevent the row from collapsing.
             * @param {Object} this RowExpander object.
             * @param {Object} Ext.data.Record Record for the selected row.
             * @param {Object} body body element for the secondary row.
             * @param {Number} rowIndex The current row index.
             */
            beforecollapse: true,
            /**
             * @event collapse
             * Fires after the row collapses.
             * @param {Object} this RowExpander object.
             * @param {Object} Ext.data.Record Record for the selected row.
             * @param {Object} body body element for the secondary row.
             * @param {Number} rowIndex The current row index.
             */
            collapse: true
        });

        Ext.ux.grid.RowExpander.superclass.constructor.call(this);

        if(this.tpl){
            if(typeof this.tpl == 'string'){
                this.tpl = new Ext.Template(this.tpl);
            }
            this.tpl.compile();
        }

        this.state = {};
        this.bodyContent = {};
    },

    getRowClass : function(record, rowIndex, p, ds){
        p.cols = p.cols-1;
        var content = this.bodyContent[record.id];
        if(!content && !this.lazyRender){
            content = this.getBodyContent(record, rowIndex);
        }
        if(content){
            p.body = content;
        }
        return this.state[record.id] ? 'x-grid3-row-expanded' : 'x-grid3-row-collapsed';
    },

    init : function(grid){
        this.grid = grid;

        var view = grid.getView();
        view.getRowClass = this.getRowClass.createDelegate(this);

        view.enableRowBody = true;


        grid.on('render', this.onRender, this);
        grid.on('destroy', this.onDestroy, this);
    },

    // @private
    onRender: function() {
        var grid = this.grid;
        var mainBody = grid.getView().mainBody;
        mainBody.on('mousedown', this.onMouseDown, this, {delegate: '.x-grid3-row-expander'});
        if (this.expandOnEnter) {
            this.keyNav = new Ext.KeyNav(this.grid.getGridEl(), {
                'enter' : this.onEnter,
                scope: this
            });
        }
        if (this.expandOnDblClick) {
            grid.on('rowdblclick', this.onRowDblClick, this);
        }
    },
    
    // @private    
    onDestroy: function() {
        this.keyNav.disable();
        delete this.keyNav;
        var mainBody = this.grid.getView().mainBody;
        mainBody.un('mousedown', this.onMouseDown, this);
    },
    // @private
    onRowDblClick: function(grid, rowIdx, e) {
        this.toggleRow(rowIdx);
    },

    onEnter: function(e) {
        var g = this.grid;
        var sm = g.getSelectionModel();
        var sels = sm.getSelections();
        for (var i = 0, len = sels.length; i < len; i++) {
            var rowIdx = g.getStore().indexOf(sels[i]);
            this.toggleRow(rowIdx);
        }
    },

    getBodyContent : function(record, index){
        if(!this.enableCaching){
            return this.tpl.apply(record.data);
        }
        var content = this.bodyContent[record.id];
        if(!content){
            content = this.tpl.apply(record.data);
            this.bodyContent[record.id] = content;
        }
        return content;
    },

    onMouseDown : function(e, t){
        e.stopEvent();
        var row = e.getTarget('.x-grid3-row');
        this.toggleRow(row);
    },

    renderer : function(v, p, record){
        p.cellAttr = 'rowspan="2"';
        return '<div class="x-grid3-row-expander">&#160;</div>';
    },

    beforeExpand : function(record, body, rowIndex){
        if(this.fireEvent('beforeexpand', this, record, body, rowIndex) !== false){
            if(this.tpl && this.lazyRender){
                body.innerHTML = this.getBodyContent(record, rowIndex);
            }
            return true;
        }else{
            return false;
        }
    },

    toggleRow : function(row){
        if(typeof row == 'number'){
            row = this.grid.view.getRow(row);
        }
        this[Ext.fly(row).hasClass('x-grid3-row-collapsed') ? 'expandRow' : 'collapseRow'](row);
    },

    expandRow : function(row){
        if(typeof row == 'number'){
            row = this.grid.view.getRow(row);
        }
        var record = this.grid.store.getAt(row.rowIndex);
        var body = Ext.DomQuery.selectNode('tr:nth(2) div.x-grid3-row-body', row);
        if(this.beforeExpand(record, body, row.rowIndex)){
            this.state[record.id] = true;
            Ext.fly(row).replaceClass('x-grid3-row-collapsed', 'x-grid3-row-expanded');
            this.fireEvent('expand', this, record, body, row.rowIndex);
        }
    },

    collapseRow : function(row){
        if(typeof row == 'number'){
            row = this.grid.view.getRow(row);
        }
        var record = this.grid.store.getAt(row.rowIndex);
        var body = Ext.fly(row).child('tr:nth(1) div.x-grid3-row-body', true);
        if(this.fireEvent('beforecollapse', this, record, body, row.rowIndex) !== false){
            this.state[record.id] = false;
            Ext.fly(row).replaceClass('x-grid3-row-expanded', 'x-grid3-row-collapsed');
            this.fireEvent('collapse', this, record, body, row.rowIndex);
        }
    }
});

Ext.preg('rowexpander', Ext.ux.grid.RowExpander);

//backwards compat
Ext.grid.RowExpander = Ext.ux.grid.RowExpander;ï»¿Ext.namespace("Ext.ux.grid");

Ext.ux.grid.MultiCellSelectionModel = function(config) {
    Ext.apply(this, config);
    this.addEvents({
        "beforecellselect": true,
        "selectionchange": true
    });
    var ck = this.cellKey;
    this.selection = new Ext.util.MixedCollection(false, function(o){
        return ck(o);
    });
    Ext.ux.grid.MultiCellSelectionModel.superclass.constructor.call(this);
}
Ext.extend(Ext.ux.grid.MultiCellSelectionModel, Ext.grid.AbstractSelectionModel, {
    constrainToSingleRow: false,
	nonceRow : false,
	witCopy : true,
    unselectableColumns: [],
	dragFlag : false,
    returnRecord: true,
    returnDataIndex: true,
    keyNavEnabled: false,
	selectedArea:"",
    initEvents: function() {
		this.keyBy(this);
        this.grid.on("cellmousedown", this.handleMouseDown, this);
        this.view = this.grid.view;

        this.view.on("beforerowremoved", this.clearSelections, this);
        this.view.on("beforerowsinserted", this.clearSelections, this);

        if(this.grid.isEditor)
            this.grid.on("beforeedit", this.beforeEdit,  this);

        var moveSelectionFunc = function(adjust) {
            return function(e) {
                if(this._activeItem) {
                    var active = this.selection.get(this._activeItem);
                    var row = active.row, col = active.col;

                    if(this._expandedTo) {  // e.shiftKey && 
                        row = this._expandedTo[0];
                        col = this._expandedTo[1];
                    }
                    var adjusted = adjust(row,col);
                    row = adjusted[0]; col = adjusted[1];
                    if(this.isCellAvailable(row,col) && this.isCellSelectable(row,col,this.getColumnDataIndex(col))) {
                        e.shiftKey ? this.expandSelection(row, col, true) : this.select(row, col, false);
                    }
                }
            }
        };

        if(this.keyNavEnabled) {
            this.keyNav = new Ext.KeyNav(this.grid.getGridEl(), {
                scope: this,
                up: moveSelectionFunc(function(row, col) {    return [row - 1, col     ]; }),
                down: moveSelectionFunc(function(row, col) {  return [row + 1, col     ]; }),
                left: moveSelectionFunc(function(row, col) {  return [row    , col - 1 ]; }),
                right: moveSelectionFunc(function(row, col) { return [row    , col + 1 ]; }),
                "enter": function(e) {
                    if(this._activeItem) {
                        var active = this.selection.get(this._activeItem), g = this.grid;
                        if(g.isEditor && !g.editing) {
                            g.startEditing(active.row, active.col);
                            e.stopEvent();
                        }
                    }
                }
            })
        }
    },
    isCellAvailable: function(row, col) {
        return col > -1 && row > -1 && !!this.view.getRow(row) && !!this.view.getCell(row,col);
    },
    beforeEdit: function() {

    },
    hasSelection: function() {
        return this.selection.getCount() > 0;
    },
    clearSelections: function(preventNotify) {
        if(this.hasSelection()) {
            if(!preventNotify)
                this.selection.each(function(cell){
					if(this.nonceRow){
						var cm=this.grid.getColumnModel();
						var c=cm.getColumnCount();
						for(var i=0;i<c;i++){
							if(typeof cm.getColumnById(cm.getColumnId(i)).hidden=="undefined"||!cm.getColumnById(cm.getColumnId(i)).hidden){
								this.view.onCellDeselect(cell.row, i);	
							}
						}
					}else{
                    	this.view.onCellDeselect(cell.row, cell.col);
					}
                }, this);
            this.selection.clear();
            this.fireEvent("selectionchange", this, this.selection);
        }
		//this.selectedArea = "";
    },
    handleMouseDown: function(g, row, col, e) {
        if(e.button != 0 || this.isLocked()) return;
        var hs = this.hasSelection();
        if(hs) {
            if(e.shiftKey) this.expandSelection(row, col, true);
            else if(e.ctrlKey) this.selectAdditionalCell(row,col,e);
            else this.select(row, col);
        } else this.select(row, col, e);
    },
    captureMouseMove: function(on) {
        if(on) {
            this.view.el.on('mouseup', this.captureMouseMove.createDelegate(this, [false]), this, { single: true });
            this.view.el.on('mousemove', this.onMouseMove, this);
        } else {
            this.view.el.un('mousemove', this.onMouseMove);
            this.fireEvent("selectionchange", this, this.selection);      
        }
    },
    onMouseMove: function(e, el) {
		if(this.dragFlag) {
			var row = this.view.findRowIndex(el), col = this.view.findCellIndex(el);
			if(!(col === false) && (!this._expandedTo || !(row == this._expandedTo[0] && col == this._expandedTo[1]))) {
				this.expandSelection(row, col, true);
			}
		}
    },
    isCellSelectable: function(row, col, dataIndex) {
        return (!this.grid.getColumnModel().isHidden(col)) && !(dataIndex && this.unselectableColumns.indexOf(dataIndex) != -1);

    },
    getColumnDataIndex: function(col) {
        if(this.returnDataIndex) {
            var cm = this.grid.getColumnModel();
            return cm.getColumnById(cm.getColumnId(col)).dataIndex;
        }
    },
    selectCell: function(row, col, preventViewNotify, preventFocus, r) {
		var key = this.cellKey(row, col);
		var v = this.grid.getView();
		if(this.nonceRow){
			var cm=this.grid.getColumnModel();
			var c=cm.getColumnCount();
			for(var i=0;i<c;i++){
				if(typeof cm.getColumnById(cm.getColumnId(i)).hidden=="undefined"||!cm.getColumnById(cm.getColumnId(i)).hidden){
					var dataIndex = this.getColumnDataIndex(col);
					r = this.returnRecord && (r || this.grid.store.getAt(row));
					var sel = {
						record : r,
						dataIndex: dataIndex,
						cell : [row, i],
						row: row, col: i
					};
					this.selection.add(key, sel);
					if(!preventViewNotify){
						v.onCellSelect(row, i);
						if(preventFocus !== true){
							v.focusCell(row, col);
						}
					}	
				}
			}
		}else{
			var dataIndex = this.getColumnDataIndex(col);
			if(!this.isCellSelectable(row, col, dataIndex)) return;
	
			r = this.returnRecord && (r || this.grid.store.getAt(row));
			var sel = {
				record : r,
				dataIndex: dataIndex,
				cell : [row, col],
				row: row, col: col
			};
			this.selection.add(key, sel);
			if(!preventViewNotify){
				v.onCellSelect(row, col);
				if(preventFocus !== true){
					v.focusCell(row, col);
				}
			}
		}
			
        return key;
    },
    deselectCellByKey: function(cellkey, preventViewNotify) {
			var cell = this.selection.get(cellkey);
			if(!cell) return;
			if(!preventViewNotify) this.view.onCellDeselect(cell.row, cell.col);
			this.selection.remove(cell);
    },
    deselectCell: function(row, col) {
        this.deselectCellByKey(this.cellKey(row, col));
    },
    deselectCells: function(cells) {
        for(var i=0; i<cells.length;i++)
            this.deselectCellByKey(cells[i]);
    },
    cellKey: function(row,col) {
        return String.format("{0}::{1}", row, col);
    },
    expandSelection: function(row, col, clearExpandedSelection) {
        if(this._expandedSelection && clearExpandedSelection) {
				if(!this.nonceRow)
            	this.deselectCells(this._expandedSelection);
        }
        var active = this.selection.get(this._activeItem);
        this._expandedSelection = [];
		
		//if(this.nonceRow){
        //    var s = this.selectCell(active.row, 0, false, true);
        //   this._expandedSelection.push(s);
        //    this._expandedTo = [active.row, 0];
		//}else 
		if(this.constrainToSingleRow || active.row == row) {
            var start = active.col + 1, end = col;
            if(col < active.col) {
                start = col;
                end = active.col -1;
            }
            for(var i = start; i <= end; i++) {
                var s = this.selectCell(active.row, i, false, true);
                this._expandedSelection.push(s);
            }
            this._expandedTo = [active.row, col];
        } else {
            var x0 = Math.min(col, active.col), x1 = Math.max(col, active.col);
            var y0 = Math.min(row, active.row), y1 = Math.max(row, active.row);
            for(var x=x0; x<=x1; x++) {
                for(var y=y0; y<=y1;y++) {
                    if(!(x==active.col && y==active.row)) {
                        var s = this.selectCell(y, x, false, true);
                        this._expandedSelection.push(s);
                    }
                }
            }
            this._expandedTo = [row, col];
        }
        this.fireEvent("selectionchange", this, this.selection);
    },
    getSelections: function() {
        return this.selection;
    },
    selectAdditionalCell: function(row, col, captureMouseMove,e) {
		this.dragFlag = false;
        var key = this.cellKey(row, col);
        if(this.constrainToSingleRow && this._activeItem) {
            var active = this.selection.get(this._activeItem);
            if(active.row != row) return;
        }
        if(this.selection.containsKey(key)) {
            this.deselectCellByKey(key);
        } else {
            this._activeItem = this.selectCell(row, col);
            this._expandedSelection = false;
            this._expandedTo = false;

            if(captureMouseMove !== false) {
                this.captureMouseMove(true);
			}
        }
    },
    select : function(rowIndex, colIndex, e, captureMouseMove, preventViewNotify, preventFocus, /*internal*/ r){
        if(this.fireEvent("beforecellselect", this, rowIndex, colIndex) !== false){
            this.clearSelections();
            this._activeItem = this.selectCell(rowIndex, colIndex, preventViewNotify, preventFocus, r);
            this._expandedSelection = false;
            this._expandedTo = false;
            if(captureMouseMove !== false) {
                this.captureMouseMove(true);
		
					this.dragFlag = true;
				
			}
			//alert(rowIndex + "==" + colIndex);
            this.fireEvent("selectionchange", this, this.selection);
        }
    },
	 keyBy : function(o) {
		var m = new Ext.KeyMap(Ext.get(this.grid.getEl().id), {
			key: 'c',
			fn: function(){mcsm_copy_(o)},
			ctrl:true
		});
	},
	copys : function(type){
		mcsm_copy_(this,type);
	},
	calculate : function(o) {
		mcsm_calculate(this);
	}
});

function mcsm_calculate(obj) {
	//alert(1);
	//éªŒè¯è´§å¸ç±»å‹çš„æ­£åˆ™è¡¨è¾¾å¼
	var re = RegExp(/^([1-9,-]{1}[0-9,-]{0,2}(\,[0-9,-]{3})*(\.[0-9,-]{0,2})?|[1-9,-]{1}\d*(\.[0-9,-]{0,2})?|0(\.[0-9,-]{0,2})?|(\.[0-9,-]{1,2})?)$/);
	
	var r=obj.grid.getStore().getCount();
	var cm=obj.grid.getColumnModel();
	var c=cm.getColumnCount();
	var selectedArea = obj.selectedArea;
	var h=0;
	var f=0;
	var n=0;
	var m=0;
	var calculateDiv = document.getElementById("sText");
	for(var j=0;j<r;j++){
		n=0;
		m=0;

		for(var i=0;i<c;i++){
			try{
				var o=obj.grid.view.getCell(j,i);
				if(typeof cm.getColumnById(cm.getColumnId(i)).hidden!="undefined"&&cm.getColumnById(cm.getColumnId(i)).hidden){
				}else{
					if(Ext.fly(o).hasClass("x-grid3-cell-selected")){
						var textValue = o.firstChild.innerText;
						
						
						if(obj.selectedArea.indexOf("[" + j + "," + i + "]")<0) {
							//åˆ¤æ–­é€‰ä¸­çš„å€¼æ˜¯å¦æ˜¯è´§å¸,å¦‚æœä¸æ˜¯å°±ç›´æ¥è¿”å›
							if(( re.test(textValue))||( textValue.substring(0,1) == "-" && re.test(textValue.substring(1)))||( textValue.substring(0,1) == "(" && re.test(textValue.substring(1,textValue.length-1))) ) {
								
								if(textValue.indexOf("-") == 0) {
									textValue = "(" + textValue +")";
								} 
								if(calculateDiv.value=="") {
									calculateDiv.value += textValue;
								} else {
									calculateDiv.value += " + " + textValue;
								}
							} 
							obj.selectedArea += "[" + j + "," + i + "],";
						}

						

						/*
						if(textValue.indexOf("-")>-1) 
							textValue = " (" + textValue + ") ";
						}
						calculateDiv.value += textValue+"+"; 
						*/
						if(f==0&&h==0)
							f=i;
						h++;
						n++;
					}else{
						m++;	
					}
				}
			}catch(e){
			}
		}
	}
}
//private
function mcsm_copy_(obj,type){
	setTimeout(function(){
		var s="";
		var r=obj.grid.getStore().getCount();
		var cm=obj.grid.getColumnModel();
		var c=cm.getColumnCount();
		var copyArray = new Array(r+1);
		var noBlanks = ",";
		var cellIndexStr = "" ; //è®°å½•è¦æ·»åŠ è¡¨å¤´çš„åˆ—
		
		
		for(var j=0;j<r;j++){
			for(var i=0;i<c;i++){
				try{
					var o=obj.grid.view.getCell(j,i);
					if(typeof cm.getColumnById(cm.getColumnId(i)).hidden!="undefined"&&cm.getColumnById(cm.getColumnId(i)).hidden){
					}else{
							if(Ext.fly(o).hasClass("x-grid3-cell-selected")){
								if(!copyArray[j]) {
									copyArray[j] = new Array(c);
								}
								copyArray[j][i] = o.firstChild.innerText.Trim();
								noBlanks += j + ","
								if(cellIndexStr.indexOf(i) == -1) {
									cellIndexStr += i + "," ;
								}
							}
						
					}
				}catch(e){}
				
			}
			
		}
		
		if(cellIndexStr != "") {
			cellIndexStr = cellIndexStr.substring(0,cellIndexStr.length-1) ;
		}
		if(type=="2" || type=="4") {  //å¸¦è¡¨å¤´
			copyArray[0] = new Array(c);
			for(var k=0;k<c;k++) {
				if(cellIndexStr.indexOf(k)>-1) {
					var oHead = obj.grid.view.getHeaderCell(k);
						copyArray[0][k] = oHead.firstChild.innerText.Trim();
				}
				
			}
			noBlanks +=0+"," ;
		}
		var startCol = 99999;
		for(var i=0; i<r+1; i++) {
			var flag = 0;
			if(noBlanks.indexOf(","+i+",")>-1) {
				for(var j=0; j<c; j++) {
					if(typeof(copyArray[i][j])=="undefined") {
						flag +=1
					} else {
						break;	
					}
				}
				if(flag < startCol) {
					startCol = flag;
				}
			}
		}
		
		var sText = "";
		for(var i=0; i<r+1; i++) {
			if(noBlanks.indexOf(","+i+",")>-1) {
				for(var j=startCol;j<c;j++) {
					if(typeof(copyArray[i][j])=="undefined") {
				//		sText += "\t";
					} else {
						sText += copyArray[i][j] + "\t";
					}
				}
				sText = sText.substring(0,sText.length-1);
				sText += "\n"
			}
		}
		sText = sText.substring(0,sText.length-1);
		
		if(type=="3" || type=="4") { //è½¬ç½®å¤åˆ¶
			sText = changeRowsAndCols(sText) ;
		}
		
		var AuditReport =  new ActiveXObject("AuditReportPoject.AuditReport");
		AuditReport.subClipboardSetText(sText);
		AuditReport=null;
		
	//	window.clipboardData.setData('text',sText);
		try {
			obj.grid.getSelectionModel().clearSelections();
		}
		catch (e) {
			
		}
	},0);
}

function changeRowsAndCols(text) {
	if(text!="") {
		var textArray = text.split("\n");
		var myarray = new Array(textArray.length);
		for(var i=0; i<myarray.length; i++) {
			var textArray1 = textArray[i].split("\t");
			myarray[i] = new Array(textArray1.length);
			for(var j=0; j<myarray[i].length; j++) {
				myarray[i][j] = textArray1[j];
			}
		}
	}
	
	var text_rows = myarray.length;
	var text_cols = myarray[text_rows-1].length;
	var newArray = new Array(text_cols);
	for(var i=0; i<newArray.length; i++) {
		newArray[i] = new Array();
	}
	
	for(var i=0; i<myarray.length; i++) {
		for(var j=0; j<myarray[i].length; j++) {
			try {
				newArray[j][i] = myarray[i][j];
			} catch(e) {
				alert(e);
			}
		}
	}
	
	var returnText = "";
	for(var i=0; i<newArray.length; i++) {
		for(var j=0; j<newArray[i].length; j++) {
			if(newArray[i][j]=="" || typeof(newArray[i][j])=="undefined") {
				returnText += "\t"
			} else {
				returnText += newArray[i][j] + "\t"
			}
		}
		returnText = returnText.substring(0,returnText.length-1);
		returnText += "\n";
	}
	returnText = returnText.substring(0,returnText.length-1);
	return returnText;
}/*!
 * Ext JS Library 3.0.0
 * Copyright(c) 2006-2009 Ext JS, LLC
 * licensing@extjs.com
 * http://www.extjs.com/license
 */
Ext.ns('Ext.ux.grid');

/**
 * @class Ext.ux.grid.BufferView
 * @extends Ext.grid.GridView
 * A custom GridView which renders rows on an as-needed basis.
 */
Ext.ux.grid.BufferView = Ext.extend(Ext.grid.GridView, {
	/**
	 * @cfg {Number} rowHeight
	 * The height of a row in the grid.
	 */
	rowHeight: 19,

	/**
	 * @cfg {Number} borderHeight
	 * The combined height of border-top and border-bottom of a row.
	 */
	borderHeight: 2,

	/**
	 * @cfg {Boolean/Number} scrollDelay
	 * The number of milliseconds before rendering rows out of the visible
	 * viewing area. Defaults to 100. Rows will render immediately with a config
	 * of false.
	 */
	scrollDelay: 100,

	/**
	 * @cfg {Number} cacheSize
	 * The number of rows to look forward and backwards from the currently viewable
	 * area.  The cache applies only to rows that have been rendered already.
	 */
	cacheSize: 20,

	/**
	 * @cfg {Number} cleanDelay
	 * The number of milliseconds to buffer cleaning of extra rows not in the
	 * cache.
	 */
	cleanDelay: 500,

	initTemplates : function(){
		Ext.ux.grid.BufferView.superclass.initTemplates.call(this);
		var ts = this.templates;
		// empty div to act as a place holder for a row
	        ts.rowHolder = new Ext.Template(
		        '<div class="x-grid3-row {alt}" style="{tstyle}"></div>'
		);
		ts.rowHolder.disableFormats = true;
		ts.rowHolder.compile();

		ts.rowBody = new Ext.Template(
		        '<table class="x-grid3-row-table" border="0" cellspacing="0" cellpadding="0" style="{tstyle}">',
			'<tbody><tr>{cells}</tr>',
			(this.enableRowBody ? '<tr class="x-grid3-row-body-tr" style="{bodyStyle}"><td colspan="{cols}" class="x-grid3-body-cell" tabIndex="0" hidefocus="on"><div class="x-grid3-row-body">{body}</div></td></tr>' : ''),
			'</tbody></table>'
		);
		ts.rowBody.disableFormats = true;
		ts.rowBody.compile();
	},

	getStyleRowHeight : function(){
		return Ext.isBorderBox ? (this.rowHeight + this.borderHeight) : this.rowHeight;
	},

	getCalculatedRowHeight : function(){
		return this.rowHeight + this.borderHeight;
	},

	getVisibleRowCount : function(){
		try {
		var rh = this.getCalculatedRowHeight();
		var visibleHeight = this.scroller.dom.clientHeight;
		return (visibleHeight < 1) ? 0 : Math.ceil(visibleHeight / rh);
		}catch (e) {}
	},

	getVisibleRows: function(){
		var count = this.getVisibleRowCount();
		var sc = this.scroller.dom.scrollTop;
		var start = (sc == 0 ? 0 : Math.floor(sc/this.getCalculatedRowHeight())-1);
		return {
			first: Math.max(start, 0),
			last: Math.min(start + count + 2, this.ds.getCount()-1)
		};
	},

	doRender : function(cs, rs, ds, startRow, colCount, stripe, onlyBody){
		var ts = this.templates, ct = ts.cell, rt = ts.row, rb = ts.rowBody, last = colCount-1;
		var rh = this.getStyleRowHeight();
		var vr = this.getVisibleRows();
		var tstyle = 'width:'+this.getTotalWidth()+';height:'+rh+'px;';
		// buffers
		var buf = [], cb, c, p = {}, rp = {tstyle: tstyle}, r;
		for (var j = 0, len = rs.length; j < len; j++) {
			r = rs[j]; cb = [];
			var rowIndex = (j+startRow);
			var visible = rowIndex >= vr.first && rowIndex <= vr.last;
			if (visible) {
				for (var i = 0; i < colCount; i++) {
					c = cs[i];
					p.id = c.id;
					p.css = i == 0 ? 'x-grid3-cell-first ' : (i == last ? 'x-grid3-cell-last ' : '');
					p.attr = p.cellAttr = "";
					p.value = c.renderer(r.data[c.name], p, r, rowIndex, i, ds);
					p.style = c.style;
					if (p.value == undefined || p.value === "") {
						p.value = "&#160;";
					}
					if (r.dirty && typeof r.modified[c.name] !== 'undefined') {
						p.css += ' x-grid3-dirty-cell';
					}
					cb[cb.length] = ct.apply(p);
				}
			}
			var alt = [];
			if(stripe && ((rowIndex+1) % 2 == 0)){
			    alt[0] = "x-grid3-row-alt";
			}
			if(r.dirty){
			    alt[1] = " x-grid3-dirty-row";
			}
			rp.cols = colCount;
			if(this.getRowClass){
			    alt[2] = this.getRowClass(r, rowIndex, rp, ds);
			}
			rp.alt = alt.join(" ");
			rp.cells = cb.join("");
			buf[buf.length] =  !visible ? ts.rowHolder.apply(rp) : (onlyBody ? rb.apply(rp) : rt.apply(rp));
		}
		return buf.join("");
	},

	isRowRendered: function(index){
		var row = this.getRow(index);
		return row && row.childNodes.length > 0;
	},

	syncScroll: function(){
		Ext.ux.grid.BufferView.superclass.syncScroll.apply(this, arguments);
		this.update();
	},

	// a (optionally) buffered method to update contents of gridview
	update: function(){
		if (this.scrollDelay) {
			if (!this.renderTask) {
				this.renderTask = new Ext.util.DelayedTask(this.doUpdate, this);
			}
			this.renderTask.delay(this.scrollDelay);
		}else{
			this.doUpdate();
		}
	},

	doUpdate: function(){
		if (this.getVisibleRowCount() > 0) {
			var g = this.grid, cm = g.colModel, ds = g.store;
		        var cs = this.getColumnData();

		        var vr = this.getVisibleRows();
			for (var i = vr.first; i <= vr.last; i++) {
				// if row is NOT rendered and is visible, render it
				if(!this.isRowRendered(i)){
					var html = this.doRender(cs, [ds.getAt(i)], ds, i, cm.getColumnCount(), g.stripeRows, true);
					this.getRow(i).innerHTML = html;
				}
			}
			this.clean();
		}
	},

	// a buffered method to clean rows
	clean : function(){
		if(!this.cleanTask){
			this.cleanTask = new Ext.util.DelayedTask(this.doClean, this);
		}
		this.cleanTask.delay(this.cleanDelay);
	},

	doClean: function(){
		if (this.getVisibleRowCount() > 0) {
			var vr = this.getVisibleRows();
			vr.first -= this.cacheSize;
			vr.last += this.cacheSize;

			var i = 0, rows = this.getRows();
			// if first is less than 0, all rows have been rendered
			// so lets clean the end...
			if(vr.first <= 0){
				i = vr.last + 1;
			}
			for(var len = this.ds.getCount(); i < len; i++){
				// if current row is outside of first and last and
				// has content, update the innerHTML to nothing
				if ((i < vr.first || i > vr.last) && rows[i].innerHTML) {
					rows[i].innerHTML = '';
				}
			}
		}
	},

	layout: function(){
		Ext.ux.grid.BufferView.superclass.layout.call(this);
		this.update();
	}
});


var ExtButtonPanel = Ext.extend(Ext.Panel, {
	
    layout:'table',
    defaultType: 'button',
    baseCls: 'x-plain',
    cls: 'btn-panel',
    menu: undefined,
    split: true, 
    

    constructor: function(options){
    	
    	Ext.applyIf(options,{
    		columns:2,
    		colspan:3, 
    		width:250
    	})   
    	
    	var buttons = options.items ;
        for(var i = 0, b; b = buttons[i]; i++){
            b.menu = this.menu;
            b.enableToggle = this.enableToggle; 
            b.split = this.split;
            b.arrowAlign = this.arrowAlign;
            b.style = options.style || {display: 'inline', margin : '10 15 0 0px' } ;
        }
        var items = buttons;  

        ExtButtonPanel.superclass.constructor.call(this, {
	            items: items,
	            renderTo:options.renderTo,
	            region:options.region,
	            height:options.height,  
	            width:options.width,
	            layoutConfig: {
	                columns:options.columns
	            }
	        });
    	}
 });


Ext.override(Ext.Toolbar,{
	 constructButton : function(item){
	   if(typeof(btnDenyRight) != "undefined" && btnDenyRight != "") {
		   var rightStr = "," + btnDenyRight + "," ;
		   var text = "," + item.text + "," ;
		   var reg1=new RegExp(" ","g"); 
		   rightStr = rightStr.replace(reg1,""); 
		   text = text.replace(reg1,""); 
		   if(rightStr.indexOf(text) > -1) {
			   item.disabled = true;
		   }
 	   }
       var b = item.events ? item : this.createComponent(item, item.split ? 'splitbutton' : this.defaultType);
       return b;
   }
})/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
Ext.ns('Ext.ux.form');

/**
 * @class Ext.ux.form.FileUploadField
 * @extends Ext.form.TextField
 * Creates a file upload field.
 * @xtype fileuploadfield
 */
Ext.ux.form.FileUploadField = Ext.extend(Ext.form.TextField,  {
    /**
     * @cfg {String} buttonText The button text to display on the upload button (defaults to
     * 'Browse...').  Note that if you supply a value for {@link #buttonCfg}, the buttonCfg.text
     * value will be used instead if available.
     */
    buttonText: 'Browse...',
    /**
     * @cfg {Boolean} buttonOnly True to display the file upload field as a button with no visible
     * text field (defaults to false).  If true, all inherited TextField members will still be available.
     */
    buttonOnly: false,
    /**
     * @cfg {Number} buttonOffset The number of pixels of space reserved between the button and the text field
     * (defaults to 3).  Note that this only applies if {@link #buttonOnly} = false.
     */
    buttonOffset: 3,
    /**
     * @cfg {Object} buttonCfg A standard {@link Ext.Button} config object.
     */

    // private
    readOnly: true,

    /**
     * @hide
     * @method autoSize
     */
    autoSize: Ext.emptyFn,

    // private
    initComponent: function(){
        Ext.ux.form.FileUploadField.superclass.initComponent.call(this);

        this.addEvents(
            /**
             * @event fileselected
             * Fires when the underlying file input field's value has changed from the user
             * selecting a new file from the system file selection dialog.
             * @param {Ext.ux.form.FileUploadField} this
             * @param {String} value The file value returned by the underlying file input field
             */
            'fileselected'
        );
    },

    // private
    onRender : function(ct, position){
        Ext.ux.form.FileUploadField.superclass.onRender.call(this, ct, position);

        this.wrap = this.el.wrap({cls:'x-form-field-wrap x-form-file-wrap'});
        this.el.addClass('x-form-file-text');
        this.el.dom.removeAttribute('name');
        this.createFileInput();

        var btnCfg = Ext.applyIf(this.buttonCfg || {}, {
            text: this.buttonText
        });
        this.button = new Ext.Button(Ext.apply(btnCfg, {
            renderTo: this.wrap,
            cls: 'x-form-file-btn' + (btnCfg.iconCls ? ' x-btn-icon' : '')
        }));

        if(this.buttonOnly){
            this.el.hide();
            this.wrap.setWidth(this.button.getEl().getWidth());
        }

        this.bindListeners();
        this.resizeEl = this.positionEl = this.wrap;
    },
    
    bindListeners: function(){
        this.fileInput.on({
            scope: this,
            mouseenter: function() {
                this.button.addClass(['x-btn-over','x-btn-focus'])
            },
            mouseleave: function(){
                this.button.removeClass(['x-btn-over','x-btn-focus','x-btn-click'])
            },
            mousedown: function(){
                this.button.addClass('x-btn-click')
            },
            mouseup: function(){
                this.button.removeClass(['x-btn-over','x-btn-focus','x-btn-click'])
            },
            change: function(){
                var v = this.fileInput.dom.value;
                this.setValue(v);
                this.fireEvent('fileselected', this, v);    
            }
        }); 
    },
    
    createFileInput : function() {
        this.fileInput = this.wrap.createChild({
            id: this.getFileInputId(),
            name: this.name||this.getId(),
            cls: 'x-form-file',
            tag: 'input',
            type: 'file',
            size: 1
        });
    },
    
    reset : function(){
        if (this.rendered) {
            this.fileInput.remove();
            this.createFileInput();
            this.bindListeners();
        }
        Ext.ux.form.FileUploadField.superclass.reset.call(this);
    },

    // private
    getFileInputId: function(){
        return this.id + '-file';
    },

    // private
    onResize : function(w, h){
        Ext.ux.form.FileUploadField.superclass.onResize.call(this, w, h);

        this.wrap.setWidth(w);

        if(!this.buttonOnly){
            var w = this.wrap.getWidth() - this.button.getEl().getWidth() - this.buttonOffset;
            this.el.setWidth(w);
        }
    },

    // private
    onDestroy: function(){
        Ext.ux.form.FileUploadField.superclass.onDestroy.call(this);
        Ext.destroy(this.fileInput, this.button, this.wrap);
    },
    
    onDisable: function(){
        Ext.ux.form.FileUploadField.superclass.onDisable.call(this);
        this.doDisable(true);
    },
    
    onEnable: function(){
        Ext.ux.form.FileUploadField.superclass.onEnable.call(this);
        this.doDisable(false);

    },
    
    // private
    doDisable: function(disabled){
        this.fileInput.dom.disabled = disabled;
        this.button.setDisabled(disabled);
    },


    // private
    preFocus : Ext.emptyFn,

    // private
    alignErrorIcon : function(){
        this.errorIcon.alignTo(this.wrap, 'tl-tr', [2, 0]);
    }

});

Ext.reg('fileuploadfield', Ext.ux.form.FileUploadField);

// backwards compat
Ext.form.FileUploadField = Ext.ux.form.FileUploadField;
var bMoveable=true;
var strFrame; 

document.writeln('<iframe id=endDateLayer frameborder=0 width=162 height=211 style="position: absolute;  z-index: 9998; display: none"></iframe>');
strFrame='<style>';
strFrame+='INPUT.button{BORDER-RIGHT: #63A3E9 1px solid;BORDER-TOP: #63A3E9 1px solid;BORDER-LEFT: #63A3E9 1px solid;';
strFrame+='BORDER-BOTTOM: #63A3E9 1px solid;BACKGROUND-COLOR: #63A3E9;font-family:å®‹ä½“;}';
strFrame+='TD{FONT-SIZE: 9pt;font-family:å®‹ä½“;}';
strFrame+='</style>';
strFrame+='<scr' + 'ipt>';
strFrame+='var datelayerx,datelayery;';
strFrame+='var bDrag;';
strFrame+='function document.onmousemove()';
strFrame+='{if(bDrag && window.event.button==1)';
strFrame+=' {var DateLayer=parent.document.all.endDateLayer.style;';
strFrame+='  DateLayer.posLeft += window.event.clientX-datelayerx;';
strFrame+='  DateLayer.posTop += window.event.clientY-datelayery;}}';
strFrame+='function DragStart()';
strFrame+='{var DateLayer=parent.document.all.endDateLayer.style;';
strFrame+=' datelayerx=window.event.clientX;';
strFrame+=' datelayery=window.event.clientY;';
strFrame+=' bDrag=true;}';
strFrame+='function DragEnd(){';
strFrame+=' bDrag=false;}';
strFrame+='</scr' + 'ipt>';
strFrame+='<div style="z-index:9999;position: absolute; left:0; top:0;" onselectstart="return false">';
strFrame+='<span id=tmpSelectYearLayer  style="z-index: 9999;position: absolute;top: 3; left: 19;display: none"></span>';
strFrame+='<span id=tmpSelectMonthLayer  style="z-index: 9999;position: absolute;top: 3; left: 78;display: none"></span>';
strFrame+='<span id=tmpSelectHourLayer  style="z-index: 9999;position: absolute;top: 188; left: 35px;display: none"></span>';
strFrame+='<span id=tmpSelectMinuteLayer style="z-index:9999;position:absolute;top: 188; left: 77px;display: none"></span>';
strFrame+='<span id=tmpSelectSecondLayer style="z-index:9999;position:absolute;top: 188; left: 119px;display: none"></span>';
strFrame+='<table border=1 cellspacing=0 cellpadding=0 width=142 height=160 bordercolor=#63A3E9 bgcolor=#63A3E9 >';
strFrame+='    <tr><td width=142 height=23  bgcolor=#FFFFFF>';
strFrame+='        <table border=0 cellspacing=1 cellpadding=0 width=158  height=23>';
strFrame+='            <tr align=center >';
strFrame+='                <td width=16 align=center bgcolor=#63A3E9 style="font-size:12px;cursor: hand;color: #ffffff" ';
strFrame+='        onclick="parent.meizzPrevM()" title="å‘å‰ç¿» 1 æœˆ" ><b >&lt;</b></td>';
strFrame+='       <td width=60 align="center" bgcolor="#63A3E9"  style="font-size:12px;cursor:hand" ';
strFrame+='           onmouseover="style.backgroundColor=\'#aaccf3\'"';
strFrame+='        onmouseout="style.backgroundColor=\'#63A3E9\'" ';
strFrame+='        onclick="parent.tmpSelectYearInnerHTML(this.innerText.substring(0,4))" ';
strFrame+='        title="ç‚¹å‡»è¿™é‡Œé€‰æ‹©å¹´ä»½"><span  id=meizzYearHead></span></td>';
strFrame+='       <td width=48 align="center" style="font-size:12px;font-color: #ffffff;cursor:hand" ';
strFrame+='        bgcolor="#63A3E9" onmouseover="style.backgroundColor=\'#aaccf3\'" ';
strFrame+='        onmouseout="style.backgroundColor=\'#63A3E9\'" ';
strFrame+='        onclick="parent.tmpSelectMonthInnerHTML(this.innerText.length==3?this.innerText.substring(0,1):this.innerText.substring(0,2))"';
strFrame+='        title="ç‚¹å‡»è¿™é‡Œé€‰æ‹©æœˆä»½"><span id=meizzMonthHead ></span></td>';
strFrame+='       <td width=16 bgcolor=#63A3E9 align=center style="font-size:12px;cursor: hand;color: #ffffff" ';
strFrame+='        onclick="parent.meizzNextM()" title="å‘åç¿» 1 æœˆ" ><b >&gt;</b></td>';
strFrame+='      </tr>';
strFrame+='     </table></td></tr>';
strFrame+='    <tr><td width=142 height=18 >';
strFrame+='     <table border=0 cellspacing=0 cellpadding=2 bgcolor=#63A3E9 ' + (bMoveable? 'onmousedown="DragStart()" onmouseup="DragEnd()"':'');
strFrame+='    BORDERCOLORLIGHT=#63A3E9 BORDERCOLORDARK=#FFFFFF width=140 height=20  style="cursor:' + (bMoveable ? 'move':'default') + '">';
strFrame+='    <tr><td style="font-size:12px;color:#ffffff" width=20>&nbsp;æ—¥</td>';
strFrame+='<td style="font-size:12px;color:#FFFFFF" >&nbsp;ä¸€</td><td style="font-size:12px;color:#FFFFFF">&nbsp;äºŒ</td>';
strFrame+='<td style="font-size:12px;color:#FFFFFF" >&nbsp;ä¸‰</td><td style="font-size:12px;color:#FFFFFF" >&nbsp;å››</td>';
strFrame+='<td style="font-size:12px;color:#FFFFFF" >&nbsp;äº”</td><td style="font-size:12px;color:#FFFFFF" >&nbsp;å…­</td></tr>';
strFrame+='</table></td></tr>';
strFrame+='  <tr ><td width=142 height=120 >';
strFrame+='    <table border=1 cellspacing=2 cellpadding=2 BORDERCOLORLIGHT=#63A3E9 BORDERCOLORDARK=#FFFFFF bgcolor=#fff8ec width=140 height=120 >';
var n=0; for (j=0;j<5;j++){ strFrame+= ' <tr align=center >'; for (i=0;i<7;i++){
strFrame+='<td width=20 height=20 id=meizzDay'+n+' style="font-size:12px" onclick=parent.meizzDayClick(this.innerText,0)></td>';n++;}
strFrame+='</tr>';}
strFrame+='      <tr align=center >';
for (i=35;i<37;i++)strFrame+='<td width=20 height=20 id=meizzDay'+i+' style="font-size:12px"  onclick="parent.meizzDayClick(this.innerText,0)"></td>';
strFrame+='        <td colspan=5 align=right style="color:#1478eb"><span onclick="parent.setNull()" style="font-size:12px;cursor: hand"';
strFrame+='         onmouseover="style.color=\'#ff0000\'" onmouseout="style.color=\'#1478eb\'" title="å°†æ—¥æœŸç½®ç©º">ç½®ç©º</span>&nbsp;&nbsp;<span onclick="parent.meizzToday()" style="font-size:12px;cursor: hand"';
strFrame+='         onmouseover="style.color=\'#ff0000\'" onmouseout="style.color=\'#1478eb\'" title="å½“å‰æ—¥æœŸæ—¶é—´">å½“å‰</span>&nbsp;&nbsp;<span style="cursor:hand" id=evaAllOK onmouseover="style.color=\'#ff0000\'" onmouseout="style.color=\'#1478eb\'"  onclick="parent.closeLayer()" title="å…³é—­æ—¥å†">å…³é—­&nbsp;</span></td></tr>';
strFrame+='    </table></td></tr><tr ><td >';
strFrame+='        <table border=0 cellspacing=1 cellpadding=0 width=100%   bgcolor=#FFFFFF height=22 >';
strFrame+='          <tr bgcolor="#63A3E9"><td id=bUseTimeLayer width=30  style="cursor:hand;" title="ç‚¹å‡»è¿™é‡Œå¯ç”¨/ç¦ç”¨æ—¶é—´"';
strFrame+='    onmouseover="style.backgroundColor=\'#aaccf3\'" align=center onmouseout="style.backgroundColor=\'#63A3E9\'"';
strFrame+='     onclick="parent.UseTime(this)">';
strFrame+=' <span></span></td>';
strFrame+='             <td style="cursor:hand" onclick="parent.tmpSelectHourInnerHTML(this.innerText.length==3?this.innerText.substring(0,1):this.innerText.substring(0,2))"';
strFrame+=' onmouseover="style.backgroundColor=\'#aaccf3\'" onmouseout="style.backgroundColor=\'#63A3E9\'"';
strFrame+=' title="ç‚¹å‡»è¿™é‡Œé€‰æ‹©æ—¶é—´" align=center width=42>' ;
strFrame+='     <span id=meizzHourHead></span></td>';
strFrame+='             <td style="cursor:hand" onclick="parent.tmpSelectMinuteInnerHTML(this.innerText.length==3?this.innerText.substring(0,1):this.innerText.substring(0,2))"';
strFrame+=' onmouseover="style.backgroundColor=\'#aaccf3\'" onmouseout="style.backgroundColor=\'#63A3E9\'"';
strFrame+=' title="ç‚¹å‡»è¿™é‡Œé€‰æ‹©æ—¶é—´" align=center width=42>' ;
strFrame+='     <span id=meizzMinuteHead></span></td>';
strFrame+='             <td style="cursor:hand" onclick="parent.tmpSelectSecondInnerHTML(this.innerText.length==3?this.innerText.substring(0,1):this.innerText.substring(0,2))"';
strFrame+=' onmouseover="style.backgroundColor=\'#aaccf3\'" onmouseout="style.backgroundColor=\'#63A3E9\'"';
strFrame+=' title="ç‚¹å‡»è¿™é‡Œé€‰æ‹©æ—¶é—´" align=center width=42>' ;
strFrame+='     <span id=meizzSecondHead></span></td>';
strFrame+='    </tr></table></td></tr></table></div>';

window.frames.endDateLayer.document.writeln(strFrame);
window.frames.endDateLayer.document.close();  // è§£å†³ieè¿›åº¦æ¡ä¸ç»“æŸçš„é—®é¢˜

 

// ==================================================== WEB é¡µé¢æ˜¾ç¤ºéƒ¨åˆ†
// ======================================================
var outObject;
var outButton;  // ç‚¹å‡»çš„æŒ‰é’®


var outDate="";  // å­˜æ”¾å¯¹è±¡çš„æ—¥æœŸ

var bUseTime=true;  // æ˜¯å¦ä½¿ç”¨æ—¶é—´
var odatelayer=window.frames.endDateLayer.document.all;  // å­˜æ”¾æ—¥å†å¯¹è±¡
var odatelayer=window.endDateLayer.document.all;
// odatelayer.bUseTimeLayer.innerText="NO";
bImgSwitch();
odatelayer.bUseTimeLayer.innerHTML=bImg;

function setday(tt,obj) // ä¸»è°ƒå‡½æ•°
{
 
 if (arguments.length > 2){alert("å¯¹ä¸èµ·ï¼å‚³å…¥æœ¬æ§ä»¶çš„å‚æ•°å¤ªå¤šï¼");return;}
 if (arguments.length == 0){alert("å¯¹ä¸èµ·ï¼æ‚¨æ²’æœ‰å‚³å›æœ¬æ§ä»¶ä»»ä½•å‚æ•°");return;}
 var dads = document.all.endDateLayer.style;
 var th = tt;
 var ttop = tt.offsetTop; // TTæ§ä»¶çš„å®šä½ç‚¹é«˜


 var thei = tt.clientHeight; // TTæ§ä»¶æœ¬èº«çš„é«˜
 var tleft = tt.offsetLeft; // TTæ§ä»¶çš„å®šä½ç‚¹å®½


 var ttyp = tt.type; // TTæ§ä»¶çš„ç±»å‹


 while (tt = tt.offsetParent){ttop+=tt.offsetTop; tleft+=tt.offsetLeft;}
 dads.top = (ttyp=="image") ? ttop+thei : ttop+thei+6;
 dads.left = tleft;
 outObject = (arguments.length == 1) ? th : obj;
 outButton = (arguments.length == 1) ? null : th; // è®¾å®šå¤–éƒ¨ç‚¹å‡»çš„æŒ‰é’®


 // æ ¹æ®å½“å‰è¾“å…¥æ¡†çš„æ—¥æœŸæ˜¾ç¤ºæ—¥å†çš„å¹´æœˆ

 var reg = /^(\d+)-(\d{1,2})-(\d{1,2})/;  // ä¸å«æ—¶é—´
 var r = outObject.value.match(reg);
 if(r!=null){
	  r[2]=r[2]-1;
	  var d=new Date(r[1],r[2],r[3]);
	  if(d.getFullYear()==r[1] && d.getMonth()==r[2] && d.getDate()==r[3])
	  {
		   outDate=d;
		   parent.meizzTheYear = r[1];
		   parent.meizzTheMonth = r[2];
		   parent.meizzTheDate = r[3];
	  }else {
		  outDate="";
	  }
	  meizzSetDay(r[1],r[2]+1);
 }else{
  outDate="";
  meizzSetDay(new Date().getFullYear(), new Date().getMonth() + 1);
 }
 dads.display = '';

 // åˆ¤æ–­åˆå§‹åŒ–æ—¶æ˜¯å¦ä½¿ç”¨æ—¶é—´,éä¸¥æ ¼éªŒè¯
 if (outObject.value.length>10){
	  bUseTime=true;
	  bImgSwitch();
	  //odatelayer.bUseTimeLayer.innerHTML=bImg;
	  meizzWriteHead(meizzTheYear,meizzTheMonth);
 }else{
     //bUseTime=false; //æ‰“å¼€åˆå§‹åŒ–çš„æ—¶å€™ï¼Œé€‰æ‹©æ—¶åˆ†ç§’
	  odatelayer.bUseTimeLayer.onclick=null; //å–æ¶ˆäº‹ä»¶
	  bImgSwitch();
	  //odatelayer.bUseTimeLayer.innerHTML=bImg;ç¦æ­¢å…³é—­æ—¶é—´
	  odatelayer.bUseTimeLayer.innerHTML="æ—¶é—´:";
	  odatelayer.bUseTimeLayer.title=""; //å–æ¶ˆæ‚¬æµ®æç¤º
	  meizzWriteHead(meizzTheYear,meizzTheMonth);
 }

 try
 {
  event.returnValue=false;
 }
 catch (e)
 {
  // æ­¤å¤„æ’é™¤é”™è¯¯ï¼Œé”™è¯¯åŸå› æš‚æœªæ‰¾åˆ°ã€‚

 }
}

var MonHead = new Array(12); // å®šä¹‰é˜³å†ä¸­æ¯ä¸ªæœˆçš„æœ€å¤§å¤©æ•°


MonHead[0] = 31; MonHead[1] = 28; MonHead[2] = 31; MonHead[3] = 30; MonHead[4]  = 31; MonHead[5]  = 30;
MonHead[6] = 31; MonHead[7] = 31; MonHead[8] = 30; MonHead[9] = 31; MonHead[10] = 30; MonHead[11] = 31;

var meizzTheYear=new Date().getFullYear(); // å®šä¹‰å¹´çš„å˜é‡çš„åˆå§‹å€¼


var meizzTheMonth=new Date().getMonth()+1; // å®šä¹‰æœˆçš„å˜é‡çš„åˆå§‹å€¼


var meizzTheDate=new Date().getDate(); // å®šä¹‰æ—¥çš„å˜é‡çš„åˆå§‹å€¼
var meizzTheHour=new Date().getHours(); // å®šä¹‰å°æ—¶å˜é‡çš„åˆå§‹å€¼


var meizzTheMinute=new Date().getMinutes();// å®šä¹‰åˆ†é’Ÿå˜é‡çš„åˆå§‹å€¼

var meizzTheSecond=new Date().getSeconds();// å®šä¹‰ç§’å˜é‡çš„åˆå§‹å€¼


var meizzWDay=new Array(37); // å®šä¹‰å†™æ—¥æœŸçš„æ•°ç»„

function document.onclick() // ä»»æ„ç‚¹å‡»æ—¶å…³é—­è¯¥æ§ä»¶ //ie6çš„æƒ…å†µå¯ä»¥ç”±ä¸‹é¢çš„åˆ‡æ¢ç„¦ç‚¹å¤„ç†ä»£æ›¿
{ 
 with(window.event)
 {
  if (srcElement != outObject && srcElement != outButton)
  closeLayer();
 }
}

function document.onkeyup()  // æŒ‰Escé”®å…³é—­ï¼Œåˆ‡æ¢ç„¦ç‚¹å…³é—­
{
 if (window.event.keyCode==27){
   if(outObject)outObject.blur();
   closeLayer();
 }else if(document.activeElement){ 
	 if(document.activeElement != outObject && document.activeElement != outButton)
 {
   closeLayer();
  }
 }
}

function meizzWriteHead(yy,mm,ss) // å¾€ head ä¸­å†™å…¥å½“å‰çš„å¹´ä¸æœˆ

{
 odatelayer.meizzYearHead.innerText = yy + " å¹´";
 odatelayer.meizzMonthHead.innerText = format(mm) + " æœˆ";
 // æ’å…¥å½“å‰å°æ—¶ã€åˆ†
 odatelayer.meizzHourHead.innerText=bUseTime?(meizzTheHour+" æ—¶"):""; 
 odatelayer.meizzMinuteHead.innerText=bUseTime?(meizzTheMinute+" åˆ†"):"";
 odatelayer.meizzSecondHead.innerText=bUseTime?(meizzTheSecond+" ç§’"):"";
}

function tmpSelectYearInnerHTML(strYear) // å¹´ä»½çš„ä¸‹æ‹‰æ¡†
{
 if (strYear.match(/\D/)!=null){alert("å¹´ä»½è¾“å…¥å‚æ•°ä¸æ˜¯æ•°å­—ï¼");return;}
 var m = (strYear) ? strYear : new Date().getFullYear();
 if (m < 1000 || m > 9999) {alert("å¹´ä»½å€¼ä¸åœ¨ 1000 åˆ° 9999 ä¹‹é—´ï¼");return;}
 var n = m - 50;
 if (n < 1000) n = 1000;
 if (n + 101 > 9999) n = 9974;
 var s = "&nbsp;<select name=tmpSelectYear style='font-size: 12px' "
 s += "onblur='document.all.tmpSelectYearLayer.style.display=\"none\"' "
 s += "onchange='document.all.tmpSelectYearLayer.style.display=\"none\";"
 s += "parent.meizzTheYear = this.value; parent.meizzSetDay(parent.meizzTheYear,parent.meizzTheMonth)'>\r\n";
 var selectInnerHTML = s;
 for (var i = n; i < n + 101; i++)
 {
  if (i == m) { selectInnerHTML += "<option value='" + i + "' selected>" + i + "å¹´" + "</option>\r\n"; }
  else { selectInnerHTML += "<option value='" + i + "'>" + i + "å¹´" + "</option>\r\n"; }
 }
 selectInnerHTML += "</select>";
 odatelayer.tmpSelectYearLayer.style.display="";
 odatelayer.tmpSelectYearLayer.innerHTML = selectInnerHTML;
 odatelayer.tmpSelectYear.focus();
}

function tmpSelectMonthInnerHTML(strMonth) // æœˆä»½çš„ä¸‹æ‹‰æ¡†
{
 if (strMonth.match(/\D/)!=null){alert("æœˆä»½è¾“å…¥å‚æ•°ä¸æ˜¯æ•°å­—ï¼");return;}
 var m = (strMonth) ? strMonth : new Date().getMonth() + 1;
 var s = "&nbsp;&nbsp;&nbsp;<select name=tmpSelectMonth style='font-size: 12px' "
 s += "onblur='document.all.tmpSelectMonthLayer.style.display=\"none\"' "
 s += "onchange='document.all.tmpSelectMonthLayer.style.display=\"none\";"
 s += "parent.meizzTheMonth = this.value; parent.meizzSetDay(parent.meizzTheYear,parent.meizzTheMonth)'>\r\n";
 var selectInnerHTML = s;
 for (var i = 1; i < 13; i++)
 {
  if (i == m) { selectInnerHTML += "<option value='"+i+"' selected>"+i+"æœˆ"+"</option>\r\n"; }
  else { selectInnerHTML += "<option value='"+i+"'>"+i+"æœˆ"+"</option>\r\n"; }
 }
 selectInnerHTML += "</select>";
 odatelayer.tmpSelectMonthLayer.style.display="";
 odatelayer.tmpSelectMonthLayer.innerHTML = selectInnerHTML;
 odatelayer.tmpSelectMonth.focus();
}

/** *** å¢åŠ  å°æ—¶ã€åˆ†é’Ÿ ** */
function tmpSelectHourInnerHTML(strHour) // å°æ—¶çš„ä¸‹æ‹‰æ¡†
{
 if (!bUseTime){return;}

 if (strHour.match(/\D/)!=null){alert("å°æ—¶å‚æ•°ä¸æ˜¯æ•°å­—ï¼");return;}
 var m = (strHour) ? strHour : new Date().getHours();
 var s = "<select name=tmpSelectHour style='font-size: 12px' "
 s += "onblur='document.all.tmpSelectHourLayer.style.display=\"none\"' "
 s += "onchange='document.all.tmpSelectHourLayer.style.display=\"none\";"
 s += "parent.meizzTheHour = this.value; parent.evaSetTime(parent.meizzTheHour,parent.meizzTheMinute);'>\r\n";
 var selectInnerHTML = s;
 for (var i = 0; i < 24; i++)
 {
  if (i == m) { selectInnerHTML += "<option value='"+i+"' selected>"+i+"</option>\r\n"; }
  else { selectInnerHTML += "<option value='"+i+"'>"+i+"</option>\r\n"; }
 }
 selectInnerHTML += "</select>";
 odatelayer.tmpSelectHourLayer.style.display="";
 odatelayer.tmpSelectHourLayer.innerHTML = selectInnerHTML;
 odatelayer.tmpSelectHour.focus();
}

function tmpSelectMinuteInnerHTML(strMinute) // åˆ†é’Ÿçš„ä¸‹æ‹‰æ¡†
{
 if (!bUseTime){return;}

 if (strMinute.match(/\D/)!=null){alert("åˆ†é’Ÿè¾“å…¥æ•°å­—ä¸æ˜¯æ•°å­—ï¼");return;}
 var m = (strMinute) ? strMinute : new Date().getMinutes();
 var s = "<select name=tmpSelectMinute style='font-size: 12px' "
 s += "onblur='document.all.tmpSelectMinuteLayer.style.display=\"none\"' "
 s += "onchange='document.all.tmpSelectMinuteLayer.style.display=\"none\";"
 s += "parent.meizzTheMinute = this.value; parent.evaSetTime(parent.meizzTheHour,parent.meizzTheMinute);'>\r\n";
 var selectInnerHTML = s;
 for (var i = 0; i < 60; i++)
 {
  if (i == m) { selectInnerHTML += "<option value='"+i+"' selected>"+i+"</option>\r\n"; }
  else { selectInnerHTML += "<option value='"+i+"'>"+i+"</option>\r\n"; }
 }
 selectInnerHTML += "</select>";
 odatelayer.tmpSelectMinuteLayer.style.display="";
 odatelayer.tmpSelectMinuteLayer.innerHTML = selectInnerHTML;
 odatelayer.tmpSelectMinute.focus();
}

function tmpSelectSecondInnerHTML(strSecond) // ç§’çš„ä¸‹æ‹‰æ¡†

{
 if (!bUseTime){return;}

 if (strSecond.match(/\D/)!=null){alert("ç§’é’Ÿè¾“å…¥ä¸æ˜¯æ•°å­—ï¼");return;}
 var m = (strSecond) ? strSecond : new Date().getMinutes();
 var s = "<select name=tmpSelectSecond style='font-size: 12px' "
 s += "onblur='document.all.tmpSelectSecondLayer.style.display=\"none\"' "
 s += "onchange='document.all.tmpSelectSecondLayer.style.display=\"none\";"
 s += "parent.meizzTheSecond = this.value; parent.evaSetTime(parent.meizzTheHour,parent.meizzTheMinute,parent.meizzTheSecond);'>\r\n";
 var selectInnerHTML = s;
 for (var i = 0; i < 60; i++)
 {
  if (i == m) { selectInnerHTML += "<option value='"+i+"' selected>"+i+"</option>\r\n"; }
  else { selectInnerHTML += "<option value='"+i+"'>"+i+"</option>\r\n"; }
 }
 selectInnerHTML += "</select>";
 odatelayer.tmpSelectSecondLayer.style.display="";
 odatelayer.tmpSelectSecondLayer.innerHTML = selectInnerHTML;
 odatelayer.tmpSelectSecond.focus();
}

function closeLayer() // è¿™ä¸ªå±‚çš„å…³é—­
{
 var o = document.getElementById("endDateLayer");
 if (o != null)
 {
  o.style.display="none";
 }
}

function showLayer() // è¿™ä¸ªå±‚çš„å…³é—­
{
 document.all.endDateLayer.style.display="";
}

function IsPinYear(year) // åˆ¤æ–­æ˜¯å¦é—°å¹³å¹´

{
 if (0==year%4&&((year%100!=0)||(year%400==0))) return true;else return false;
}

function GetMonthCount(year,month) // é—°å¹´äºŒæœˆä¸º29å¤©

{
 var c=MonHead[month-1];if((month==2)&&IsPinYear(year)) c++;return c;
}

function GetDOW(day,month,year) // æ±‚æŸå¤©çš„æ˜ŸæœŸå‡ 

{
 var dt=new Date(year,month-1,day).getDay()/7; return dt;
}

function meizzPrevY() // å¾€å‰ç¿» Year
{
 if(meizzTheYear > 999 && meizzTheYear <10000){meizzTheYear--;}
 else{alert("å¹´ä»½è¶…å‡ºèŒƒå›´ï¼ˆ1000-9999ï¼‰ï¼");}
 meizzSetDay(meizzTheYear,meizzTheMonth);
}
function meizzNextY() // å¾€åç¿» Year
{
 if(meizzTheYear > 999 && meizzTheYear <10000){meizzTheYear++;}
 else{alert("å¹´ä»½è¶…å‡ºèŒƒå›´ï¼ˆ1000-9999ï¼‰ï¼");}
 meizzSetDay(meizzTheYear,meizzTheMonth);
}
function setNull()
{
 outObject.value = '';
 closeLayer();
}
function meizzToday() // Today Button
{
 parent.meizzTheYear  = new Date().getFullYear();
 parent.meizzTheMonth = new Date().getMonth()+1;
 parent.meizzTheDate  = new Date().getDate();
 parent.meizzTheHour  = new Date().getHours();
 parent.meizzTheMinute = new Date().getMinutes();
 parent.meizzTheSecond = new Date().getSeconds();
 var meizzTheSecond  = new Date().getSeconds();

 if (meizzTheMonth<10 && meizzTheMonth.length<2) // æ ¼å¼åŒ–æˆä¸¤ä½æ•°å­—
 {
  parent.meizzTheMonth="0"+parent.meizzTheMonth;
 }
 if (parent.meizzTheDate<10 && parent.meizzTheDate.length<2) // æ ¼å¼åŒ–æˆä¸¤ä½æ•°å­—
 {
  parent.meizzTheDate="0"+parent.meizzTheDate;
 }
 // meizzSetDay(meizzTheYear,meizzTheMonth);
 if(outObject)
 {
  if (bUseTime)
  {
   outObject.value= parent.meizzTheYear + "-" + format( parent.meizzTheMonth) + "-" + 
       format(parent.meizzTheDate) + " " + format(parent.meizzTheHour) + ":" + 
       format(parent.meizzTheMinute) + ":" + format(parent.meizzTheSecond); 
       // æ³¨ï¼šåœ¨è¿™é‡Œä½ å¯ä»¥è¾“å‡ºæ”¹æˆä½ æƒ³è¦çš„æ ¼å¼
  }
  else
  {
   outObject.value= parent.meizzTheYear + "-" + format( parent.meizzTheMonth) + "-" + 
       format(parent.meizzTheDate); // æ³¨ï¼šåœ¨è¿™é‡Œä½ å¯ä»¥è¾“å‡ºæ”¹æˆä½ æƒ³è¦çš„æ ¼å¼
  }
 }
 closeLayer();
}
function meizzPrevM() // å¾€å‰ç¿»æœˆä»½
{
 if(meizzTheMonth>1){meizzTheMonth--}else{meizzTheYear--;meizzTheMonth=12;}
 meizzSetDay(meizzTheYear,meizzTheMonth);
}
function meizzNextM() // å¾€åç¿»æœˆä»½
{
 if(meizzTheMonth==12){meizzTheYear++;meizzTheMonth=1}else{meizzTheMonth++}
 meizzSetDay(meizzTheYear,meizzTheMonth);
}

// TODO: æ•´ç†ä»£ç 
function meizzSetDay(yy,mm) // ä¸»è¦çš„å†™ç¨‹åº**********
{
 meizzWriteHead(yy,mm);
 // è®¾ç½®å½“å‰å¹´æœˆçš„å…¬å…±å˜é‡ä¸ºä¼ å…¥å€¼


 meizzTheYear=yy;
 meizzTheMonth=mm;

 for (var i = 0; i < 37; i++){meizzWDay[i]=""}; // å°†æ˜¾ç¤ºæ¡†çš„å†…å®¹å…¨éƒ¨æ¸…ç©º


 var day1 = 1,day2=1,firstday = new Date(yy,mm-1,1).getDay(); // æŸæœˆç¬¬ä¸€å¤©çš„æ˜ŸæœŸå‡ 


 for (i=0;i<firstday;i++)meizzWDay[i]=GetMonthCount(mm==1?yy-1:yy,mm==1?12:mm-1)-firstday+i+1 // ä¸Šä¸ªæœˆçš„æœ€åå‡ å¤©


 for (i = firstday; day1 < GetMonthCount(yy,mm)+1; i++) { meizzWDay[i]=day1;day1++; }
 for (i=firstday+GetMonthCount(yy,mm);i<37;i++) { meizzWDay[i]=day2;day2++; }
 for (i = 0; i < 37; i++)
 {
  var da = eval("odatelayer.meizzDay"+i) // ä¹¦å†™æ–°çš„ä¸€ä¸ªæœˆçš„æ—¥æœŸæ˜ŸæœŸæ’åˆ—


 if (meizzWDay[i]!="")
 {
  // åˆå§‹åŒ–è¾¹æ¡†

  da.borderColorLight="#63A3E9";
  da.borderColorDark="#63A3E9";
  da.style.color="#1478eb";
  if(i<firstday)  // ä¸Šä¸ªæœˆçš„éƒ¨åˆ†
  {
   da.innerHTML="<b><font color=#BCBABC>" + meizzWDay[i] + "</font></b>";
   da.title=(mm==1?12:mm-1) +"æœˆ" + meizzWDay[i] + "æ—¥";
   da.onclick=Function("meizzDayClick(this.innerText,-1)");

   if(!outDate)
    da.style.backgroundColor = ((mm==1?yy-1:yy) == new Date().getFullYear() && 
     (mm==1?12:mm-1) == new Date().getMonth()+1 && meizzWDay[i] == new Date().getDate()) ?
      "#5CEFA0":"#f5f5f5";
   else
   {
    da.style.backgroundColor =((mm==1?yy-1:yy)==outDate.getFullYear() && (mm==1?12:mm-1)== outDate.getMonth() + 1 && 
    meizzWDay[i]==outDate.getDate())? "#84C1FF" :
    (((mm==1?yy-1:yy) == new Date().getFullYear() && (mm==1?12:mm-1) == new Date().getMonth()+1 && 
    meizzWDay[i] == new Date().getDate()) ? "#5CEFA0":"#f5f5f5");
    // å°†é€‰ä¸­çš„æ—¥æœŸæ˜¾ç¤ºä¸ºå‡¹ä¸‹å»


    if((mm==1?yy-1:yy)==outDate.getFullYear() && (mm==1?12:mm-1)== outDate.getMonth() + 1 && 
    meizzWDay[i]==outDate.getDate())
    {
     da.borderColorLight="#FFFFFF";
     da.borderColorDark="#63A3E9";
    }
   }
  }
  else if (i>=firstday+GetMonthCount(yy,mm))  // ä¸‹ä¸ªæœˆçš„éƒ¨åˆ†
  {
   da.innerHTML="<b><font color=#BCBABC>" + meizzWDay[i] + "</font></b>";
   da.title=(mm==12?1:mm+1) +"æœˆ" + meizzWDay[i] + "æ—¥";
   da.onclick=Function("meizzDayClick(this.innerText,1)");
   if(!outDate)
    da.style.backgroundColor = ((mm==12?yy+1:yy) == new Date().getFullYear() && 
     (mm==12?1:mm+1) == new Date().getMonth()+1 && meizzWDay[i] == new Date().getDate()) ?
      "#5CEFA0":"#f5f5f5";
   else
   {
    da.style.backgroundColor =((mm==12?yy+1:yy)==outDate.getFullYear() && (mm==12?1:mm+1)== outDate.getMonth() + 1 && 
    meizzWDay[i]==outDate.getDate())? "#84C1FF" :
    (((mm==12?yy+1:yy) == new Date().getFullYear() && (mm==12?1:mm+1) == new Date().getMonth()+1 && 
    meizzWDay[i] == new Date().getDate()) ? "#5CEFA0":"#f5f5f5");
    // å°†é€‰ä¸­çš„æ—¥æœŸæ˜¾ç¤ºä¸ºå‡¹ä¸‹å»


    if((mm==12?yy+1:yy)==outDate.getFullYear() && (mm==12?1:mm+1)== outDate.getMonth() + 1 && 
    meizzWDay[i]==outDate.getDate())
    {
     da.borderColorLight="#FFFFFF";
     da.borderColorDark="#63A3E9";
    }
   }
  }
  else  // æœ¬æœˆçš„éƒ¨åˆ†

  {
   da.innerHTML="<b>" + meizzWDay[i] + "</b>";
   da.title=mm +"æœˆ" + meizzWDay[i] + "æ—¥";
   da.onclick=Function("meizzDayClick(this.innerText,0)");  // ç»™tdèµ‹äºˆonclickäº‹ä»¶çš„å¤„ç†


   // å¦‚æœæ˜¯å½“å‰é€‰æ‹©çš„æ—¥æœŸï¼Œåˆ™æ˜¾ç¤ºäº®è“è‰²çš„èƒŒæ™¯ï¼›å¦‚æœæ˜¯å½“å‰æ—¥æœŸï¼Œåˆ™æ˜¾ç¤ºæš—é»„è‰²èƒŒæ™¯
   if(!outDate)
    da.style.backgroundColor = (yy == new Date().getFullYear() && mm == new Date().getMonth()+1 && meizzWDay[i] == new Date().getDate())?
     "#5CEFA0":"#f5f5f5";
   else
   {
    da.style.backgroundColor =(yy==outDate.getFullYear() && mm== outDate.getMonth() + 1 && meizzWDay[i]==outDate.getDate())?
     "#84C1FF":((yy == new Date().getFullYear() && mm == new Date().getMonth()+1 && meizzWDay[i] == new Date().getDate())?
     "#5CEFA0":"#f5f5f5");
    // å°†é€‰ä¸­çš„æ—¥æœŸæ˜¾ç¤ºä¸ºå‡¹ä¸‹å»


    if(yy==outDate.getFullYear() && mm== outDate.getMonth() + 1 && meizzWDay[i]==outDate.getDate())
    {
     da.borderColorLight="#FFFFFF";
     da.borderColorDark="#63A3E9";
    }
   }
  }
    da.style.cursor="hand"
   }
  else { da.innerHTML="";da.style.backgroundColor="";da.style.cursor="default"; }
 }
}

function meizzDayClick(n,ex) // ç‚¹å‡»æ˜¾ç¤ºæ¡†é€‰å–æ—¥æœŸï¼Œä¸»è¾“å…¥å‡½æ•°*************
{
	 parent.meizzTheDate=n;
	 var yy=meizzTheYear;
	 var mm = parseInt(meizzTheMonth)+ex; // exè¡¨ç¤ºåç§»é‡ï¼Œç”¨äºé€‰æ‹©ä¸Šä¸ªæœˆä»½å’Œä¸‹ä¸ªæœˆä»½çš„æ—¥æœŸ
	 var hh=meizzTheHour;
	 var mi=meizzTheMinute;
	 var se=meizzTheSecond;
	 // åˆ¤æ–­æœˆä»½ï¼Œå¹¶è¿›è¡Œå¯¹åº”çš„å¤„ç†
	
	
	 if(mm<1){
	  yy--;
	  mm=12+mm;
	 }
	 else if(mm>12){
	  yy++;
	  mm=mm-12;
	 }
	
	 if (mm < 10) {mm = "0" + mm;}
	 if (hh<10)  {hh="0" + hh;} // æ—¶
	
	 if (mi<10)  {mi="0" + mi;} // åˆ†
	
	 if (se<10)  {se="0" + se;} // ç§’
	
	
	 if (outObject)
	 {
	  if (!n) { // outObject.value="";
	   return;}
	  if ( n < 10){n = "0" + n;}
	
	  WriteDateTo(yy,mm,n,hh,mi,se);
	
	  closeLayer(); 
	  if (bUseTime)
	  {
	   try
	   {
	    outButton.click();
	   }
	   catch (e)
	   {
	    setday(outObject);
	   }
	  }
	 }
	 else {closeLayer(); alert("æ‚¨æ‰€è¦è¾“å‡ºçš„æ§ä»¶å¯¹è±¡å¹¶ä¸å­˜åœ¨ï¼");}
	 closeLayer();
}

function format(n) // æ ¼å¼åŒ–æ•°å­—ä¸ºä¸¤ä½å­—ç¬¦è¡¨ç¤º
{
 var m=new String();
 var tmp=new String(n);
 if (n<10 && tmp.length<2)
 {
  m="0"+n;
 }
 else
 {
  m=n;
 }
 return m;
}

function evaSetTime()  // è®¾ç½®ç”¨æˆ·é€‰æ‹©çš„å°æ—¶ã€åˆ†é’Ÿ

{
 odatelayer.meizzHourHead.innerText=meizzTheHour+" æ—¶";
 odatelayer.meizzMinuteHead.innerText=meizzTheMinute+" åˆ†";
 odatelayer.meizzSecondHead.innerText=meizzTheSecond+" ç§’";
 WriteDateTo(meizzTheYear,meizzTheMonth,meizzTheDate,meizzTheHour,meizzTheMinute,meizzTheSecond)
}

function evaSetTimeNothing() // è®¾ç½®æ—¶é—´æ§ä»¶ä¸ºç©º
{
 odatelayer.meizzHourHead.innerText="";
 odatelayer.meizzMinuteHead.innerText="";
 odatelayer.meizzSecondHead.innerText="";
 WriteDateTo(meizzTheYear,meizzTheMonth,meizzTheDate,meizzTheHour,meizzTheMinute,meizzTheSecond)
}

function evaSetTimeNow() // è®¾ç½®æ—¶é—´æ§ä»¶ä¸ºå½“å‰æ—¶é—´

{
 odatelayer.meizzHourHead.innerText=new Date().getHours()+" æ—¶";
 odatelayer.meizzMinuteHead.innerText=new Date().getMinutes()+" åˆ†";
 odatelayer.meizzSecondHead.innerText=new Date().getSeconds()+" ç§’";
 meizzTheHour = new Date().getHours();
 meizzTheMinute = new Date().getMinutes();
 meizzTheSecond = new Date().getSeconds();
 WriteDateTo(meizzTheYear,meizzTheMonth,meizzTheDate,meizzTheHour,meizzTheMinute,meizzTheSecond)
}

function UseTime(ctl)
{
	 bUseTime=!bUseTime;
	 if (bUseTime) {
		  bImgSwitch();
		  ctl.innerHTML=bImg;
		  evaSetTime();  // æ˜¾ç¤ºæ—¶é—´ï¼Œç”¨æˆ·åŸæ¥é€‰æ‹©çš„æ—¶é—´
		  evaSetTimeNow(); //æ˜¾ç¤ºå½“å‰æ—¶é—´
	 }else{
		  bImgSwitch();
		  ctl.innerHTML=bImg;
		  evaSetTimeNothing();
	 }
}

function WriteDateTo(yy,mm,n,hh,mi,se)
{
 if (bUseTime)
 {
  outObject.value= yy + "-" + format(mm) + "-" + format(n) + " " + format(hh) + ":" + format(mi) + ":" + format(se); // æ³¨ï¼šåœ¨è¿™é‡Œä½ å¯ä»¥è¾“å‡ºæ”¹æˆä½ æƒ³è¦çš„æ ¼å¼
 }
 else
 {
  outObject.value= yy + "-" + format(mm) + "-" + format(n); // æ³¨ï¼šåœ¨è¿™é‡Œä½ å¯ä»¥è¾“å‡ºæ”¹æˆä½ æƒ³è¦çš„æ ¼å¼
 }
}

function bImgSwitch()
{
 if (bUseTime) {
  bImg="å…³é—­";
 } else {
  bImg="å¼€å¯";
 }

}


 Ext.ns("Ext.ux.grid.GridSummary"); // namespace Ext.ux.grid.GridSummary

Ext.ux.grid.GridSummary = function(config) {
    Ext.apply(this, config);
};

Ext.extend(Ext.ux.grid.GridSummary, Ext.util.Observable, {
  init : function(grid) {
    this.grid = grid;
    this.cm = grid.getColumnModel(); 
    this.view = grid.getView();
    var v = this.view;

    v.onLayout = this.onLayout; // override GridView's onLayout() method

    v.afterMethod('render', this.refreshSummary, this);
    v.afterMethod('refresh', this.refreshSummary, this);
    v.afterMethod('setSumValue', this.test, this);
    v.afterMethod('syncScroll', this.syncSummaryScroll, this);
    v.afterMethod('onColumnWidthUpdated', this.doWidth, this);
    v.afterMethod('onAllColumnWidthsUpdated', this.doAllWidths, this);
    v.afterMethod('onColumnHiddenUpdated', this.doHidden, this);
    v.afterMethod('onUpdate', this.refreshSummary, this);
    v.afterMethod('onRemove', this.refreshSummary, this);

    // update summary row on store's add / remove / clear events
    grid.store.on('add', this.refreshSummary, this);
    grid.store.on('remove', this.refreshSummary, this);
    grid.store.on('clear', this.refreshSummary, this);

    if (!this.rowTpl) {
      this.rowTpl = new Ext.Template(
        '<div class="x-grid3-summary-row x-grid3-gridsummary-row-offset">',
          '<table class="x-grid3-summary-table" border="0" cellspacing="0" cellpadding="0" style="{tstyle}">',
            '<tbody><tr>{cells}</tr></tbody>',
          '</table>',
        '</div>'
      );
      this.rowTpl.disableFormats = true;
    }
    this.rowTpl.compile();

    if (!this.cellTpl) {
      this.cellTpl = new Ext.Template(
        '<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css}" style="{style}">',
          '<div class="x-grid3-cell-inner x-grid3-col-{id}" unselectable="on">{value}</div>',
        "</td>"
      );
      this.cellTpl.disableFormats = true;
    }
    this.cellTpl.compile();
  },

  calculate : function(rs, cs) {
    var data = {}, r, c, cfg = this.cm.config, cf;
    for (var i = 0, len = cs.length; i < len; i++) {
      c = cs[i];
      cf = cfg[i];
      data[c.name] = 0;
      for (var j = 0, jlen = rs.length; j < jlen; j++) {
        r = rs[j];
        if (cf && cf.summaryType) {
          data[c.name] = Ext.ux.grid.GridSummary.Calculations[cf.summaryType](data[c.name] || 0, r, c.name, data);
        }
      }
    }

    return data;
  },

  onLayout : function(vw, vh) {
    if ('number' != Ext.type(vh)) { // prevent onLayout from choking when height:'auto'
      return;
    }
    // note: this method is scoped to the GridView
    if (!this.grid.getGridEl().hasClass('x-grid-hide-gridsummary')) {
      // readjust gridview's height only if grid summary row is visible
      this.scroller.setHeight(vh - this.summary.getHeight());
    }
  },

  syncSummaryScroll : function() {
    var mb = this.view.scroller.dom;
    this.view.summaryWrap.dom.scrollLeft = mb.scrollLeft;
    this.view.summaryWrap.dom.scrollLeft = mb.scrollLeft; // second time for IE (1/2 time first fails, other browsers ignore)
  },

  doWidth : function(col, w, tw) {
    var s = this.view.summary.dom;
    s.firstChild.style.width = tw;
    s.firstChild.rows[0].childNodes[col].style.width = w;
  },

  doAllWidths : function(ws, tw) {
    var s = this.view.summary.dom, wlen = ws.length;
    s.firstChild.style.width = tw;
    cells = s.firstChild.rows[0].childNodes;
    for (var j = 0; j < wlen; j++) {
      cells[j].style.width = ws[j];
    }
  },

  doHidden : function(col, hidden, tw) {
    var s = this.view.summary.dom;
    var display = hidden ? 'none' : '';
    s.firstChild.style.width = tw;
    s.firstChild.rows[0].childNodes[col].style.display = display;
  },
  putSumInfo:null,
  setSumValue : function(jsonV) {
    var cs = this.view.getColumnData();
    var buf = [], c, p = {}, last = cs.length-1;

    for (var i = 0, len = cs.length; i < len; i++) {
      c = cs[i];
      p.id = c.id; 
      p.style = c.style;
      p.css = i == 0 ? 'x-grid3-cell-first ' : (i == last ? 'x-grid3-cell-last ' : '');
      if (jsonV&&jsonV[c.name]) {
		p.value = jsonV[c.name];
      } else {
        p.value = '';
      }
      if (p.value == undefined || p.value === "") p.value = " ";
      buf[buf.length] = this.cellTpl.apply(p);
    }

    if (!this.view.summaryWrap) {
      this.view.summaryWrap = Ext.DomHelper.insertAfter(this.view.scroller, {
        tag: 'div',
        cls: 'x-grid3-gridsummary-row-inner'
      }, true);
    } else {
      this.view.summary.remove();
    }
    this.putSumInfo = this.rowTpl.apply({
      tstyle: 'width:' + this.view.getTotalWidth() + ';',
      cells: buf.join('')
    });
    this.view.summary = this.view.summaryWrap.insertHtml('afterbegin',this.putSumInfo, true);
  },
  refreshSumValue:function() {
    if (!this.view.summaryWrap) {
      this.view.summaryWrap = Ext.DomHelper.insertAfter(this.view.scroller, {
        tag: 'div',
        cls: 'x-grid3-gridsummary-row-inner'
      }, true);
    } else {
      this.view.summary.remove();
    }
    this.view.summary = this.view.summaryWrap.insertHtml('afterbegin', this.putSumInfo, true);
  },
  renderSummary : function(o, cs) {
    cs = cs || this.view.getColumnData();
    var cfg = this.cm.config;
    var buf = [], c, p = {}, cf, last = cs.length-1;

    for (var i = 0, len = cs.length; i < len; i++) {
      c = cs[i];
      cf = cfg[i];
      p.id = c.id;
      p.style = c.style;
      p.css = i == 0 ? 'x-grid3-cell-first ' : (i == last ? 'x-grid3-cell-last ' : '');
      if (cf.summaryType || cf.summaryRenderer) {
        p.value = (cf.summaryRenderer || c.renderer)(o.data[c.name], p, o);
      } else {
        p.value = '';
      }
      if (p.value == undefined || p.value === "") p.value = "&#160;";
      buf[buf.length] = this.cellTpl.apply(p);
    }

    return this.rowTpl.apply({
      tstyle: 'width:' + this.view.getTotalWidth() + ';',
      cells: buf.join('')
    });
  },

  refreshSummary : function() {
  	if(this.putSumInfo){
  		//alert(this.putSumInfo);
  		this.refreshSumValue(this.putSumInfo);
  		return;
  	}
    var g = this.grid, ds = g.store;
    var cs = this.view.getColumnData();
    var rs = ds.getRange();
    var data = this.calculate(rs, cs);
    var buf = this.renderSummary({data: data}, cs);

    if (!this.view.summaryWrap) {
      this.view.summaryWrap = Ext.DomHelper.insertAfter(this.view.scroller, {
        tag: 'div',
        cls: 'x-grid3-gridsummary-row-inner'
      }, true);
    } else {
      this.view.summary.remove();
    }
    this.view.summary = this.view.summaryWrap.insertHtml('afterbegin', buf, true);
  },

  toggleSummary : function(visible) { // true to display summary row
    var el = this.grid.getGridEl();
    if (el) {
      if (visible === undefined) {
        visible = el.hasClass('x-grid-hide-gridsummary');
      }
      el[visible ? 'removeClass' : 'addClass']('x-grid-hide-gridsummary');

      this.view.layout(); // readjust gridview height
    }
  },

  getSummaryNode : function() {
    return this.view.summary
  }
});

Ext.ux.grid.GridSummary.Calculations = {
  'sum' : function(v, record, field) {
    return v + Ext.num(record.data[field], 0);
  },

  'count' : function(v, record, field, data) {
    return data[field+'count'] ? ++data[field+'count'] : (data[field+'count'] = 1);
  },

  'max' : function(v, record, field, data) {
    var v = record.data[field];
    var max = data[field+'max'] === undefined ? (data[field+'max'] = v) : data[field+'max'];
    return v > max ? (data[field+'max'] = v) : max;
  },

  'min' : function(v, record, field, data) {
    var v = record.data[field];
    var min = data[field+'min'] === undefined ? (data[field+'min'] = v) : data[field+'min'];
    return v < min ? (data[field+'min'] = v) : min;
  },

  'average' : function(v, record, field, data) {
    var c = data[field+'count'] ? ++data[field+'count'] : (data[field+'count'] = 1);
    var t = (data[field+'total'] = ((data[field+'total'] || 0) + (record.data[field] || 0)));
    return t === 0 ? 0 : t / c;
  }
}//æ–°å¢
function mt_formList_add(_param) {
	
	var formId = document.getElementById("formId").value;
	var url = MATECH_SYSTEM_WEB_ROOT + "/formDefine.do?method=formView&formId=" + formId;
	
	var param=getParamObject();
	for(var key in param){
		if(key=='method'||key=="uuid")continue;
		url+="&"+key+"="+param[key];
		
	}
	_param=_param||{};
	for(var key in _param){
		url+="&"+key+"="+_param[key];
	}
	window.location=url; 
}

//ç¼–è¾‘
function mt_formList_edit(_param) {
	var formId = document.getElementById("formId").value;
	var tableId = document.getElementById("tableId").value;
	
	var value= document.getElementById("chooseValue_" + tableId).value;
	if(value == ''){
		
		value = getChooseValue(tableId);
				
		if(value == "") {
			alert('è¯·é€‰æ‹©è¦ä¿®æ”¹çš„æ•°æ®!');
			return;
		
		} else if(value.indexOf(",") > -1) {
			alert('è¯·é€‰æ‹©ä¸€æ¡éœ€è¦ä¿®æ”¹çš„æ•°æ®!');
			return;
		}
	}
	
	var url = MATECH_SYSTEM_WEB_ROOT + "/formDefine.do?method=formView&formId=" + formId + "&uuid="+value; 
	var param=getParamObject();
	for(var key in param){
		if(key=='method'||key=="uuid")continue;
		url+="&"+key+"="+param[key];
		
	}
	_param=_param||{};
	for(var key in _param){
		url+="&"+key+"="+_param[key];
	}
	window.location.href=url;
}

//é¢„çº¦
function mt_formList_addOrEdit() {
	var formId = document.getElementById("formId").value;
	var tableId = document.getElementById("tableId").value;
	var value= document.getElementById("chooseValue_" + tableId).value;
	if(value == ''){
		value = getChooseValue(tableId);
		if(value == "") {
			window.location = MATECH_SYSTEM_WEB_ROOT + "/formDefine.do?method=formView&formId=" + formId;
			return;
		
		} else if(value.indexOf(",") > -1) {
			alert('è¯·é€‰æ‹©ä¸€æ¡éœ€è¦ä¿®æ”¹çš„æ•°æ®!');
			return;
		}
	}
	
	window.location = MATECH_SYSTEM_WEB_ROOT + "/formDefine.do?method=formView&formId=" + formId + "&uuid="+value; 
}

//åˆ é™¤
function mt_formList_remove() {
	var formId = document.getElementById("formId").value;
	var tableId = document.getElementById("tableId").value;
	
	var value = document.getElementById("chooseValue_" + tableId).value;
	if(value == ""){
		
		value = getChooseValue(tableId);
		
		if(value == "") {
			alert('è¯·é€‰æ‹©è¦åˆ é™¤çš„æ•°æ®!');
			return;
		}
	} 
	
	if(confirm("æ‚¨ç¡®è®¤è¦åˆ é™¤å½“å‰é€‰ä¸­æ•°æ®å—ï¼Ÿ")){
		var param=getParamObject();
		
		var url =MATECH_SYSTEM_WEB_ROOT + "/formDefine.do?method=removeFormData&formId=" + formId + "&uuid="+value;
		
		for(var key in param){
			if(key=='method'||key=="uuid")continue;
			url+="&"+key+"="+param[key];
		}
		 window.location =  url;
	} else {
		return;
	}
}


//å‡åˆ é™¤
function mt_formList_remove_noreal() {
	var formId = document.getElementById("formId").value;
	var tableId = document.getElementById("tableId").value;
	
	var value = document.getElementById("chooseValue_" + tableId).value;
	if(value == ""){
		
		value = getChooseValue(tableId);
		
		if(value == "") {
			alert('è¯·é€‰æ‹©è¦åˆ é™¤çš„æ•°æ®!');
			return;
		}
	} 
	
	if(confirm("æ‚¨ç¡®è®¤è¦åˆ é™¤å½“å‰é€‰ä¸­æ•°æ®å—ï¼Ÿ")){
		var param=getParamObject();
		
		var url =MATECH_SYSTEM_WEB_ROOT + "/formDefine.do?method=removeFormDataNotReal&formId=" + formId + "&uuid="+value;
		
		for(var key in param){
			if(key=='method'||key=="uuid")continue;
			url+="&"+key+"="+param[key];
		}
		 window.location =  url;
	} else {
		return;
	}
}

//æŸ¥çœ‹
function mt_formList_view(formId) {
	
	var srcFormId = "";
	if(!formId) {
		formId = document.getElementById("formId").value;
	} else {
		srcFormId = document.getElementById("formId").value;
	}
	
	var tableId = document.getElementById("tableId").value;
	
	var value= document.getElementById("chooseValue_" + tableId).value;
	
	if(value == ''){
		
		value = getChooseValue(tableId);
				
		if(value == "") {
			alert('è¯·é€‰æ‹©è¦æŸ¥çœ‹çš„æ•°æ®!');
			return;
		
		} else if(value.indexOf(",") > -1) {
			alert('è¯·é€‰æ‹©ä¸€æ¡éœ€è¦æŸ¥çœ‹çš„æ•°æ®!');
			return;
		}
	}
	
	var url = MATECH_SYSTEM_WEB_ROOT + "/formDefine.do?method=formView&view=true&formId=" + formId + "&uuid="+value + "&srcFormId=" + srcFormId; 
	var param=getParamObject();
	for(var key in param){
		if(key=='method'||key=="uuid")continue;
		url+="&"+key+"="+param[key];
	}
	window.location.href=url;
}

//è¿”å›
function mt_formList_back() {
	window.history.back(); 
}

//å…³é—­æŒ‰é’®
function mt_formList_close() {
	//closeTab(parent.parent.mainTab);
	closeTab(parent.parent.tab);

}

function mt_form_getRowValues() {
	var tableId = document.getElementById("tableId").value;
	
	var uuid = document.getElementById("chooseValue_" + tableId).value;
	
	if(uuid == "") {
		uuid = getChooseValue(tableId);
		if(uuid == "") {
			alert('è¯·é€‰æ‹©è¦æ“ä½œçš„æ•°æ®!');
			return;
		}
	} 
	
	var json = "[";
	
	var uuids = uuid.split(",");
	
	for(var i=0; i < uuids.length; i++) {
		
		var divObj = document.getElementById("trValueId_" + uuids[i]);
		var oAttribs = divObj.attributes;
		
		json += " {";
		
		var data = "";
		
		for (var j = 0; j < oAttribs.length; j++){
			if(oAttribs[j].specified == true){
				data += "'" + oAttribs[j].nodeName + "':'" + oAttribs[j].nodeValue + "',";
			}
		}
		
		if(data != "") {
			data = data.substring(0, data.length -1);
		}
		
		json += data + "}";
		
		if(i != uuids.length-1) {
			json += ",";
		}
	}
	
	json += "]";

	return eval(json);
}

//åˆ—è¡¨æŒ‰é’®æ¥å£
function mt_form_listBtn_callJava(tableId, btnId) {
	//alert(tableId);
	
	var uuid = document.getElementById("chooseValue_" + tableId).value;
	
	if(uuid == "") {
		uuid = getChooseValue(tableId);
		if(uuid == "") {
			alert('è¯·é€‰æ‹©è¦æ“ä½œçš„æ•°æ®!');
			return;
		}
	}
	
	var requestString = "";
	
	var uuids = uuid.split(",");
	
	for(var i=0; i < uuids.length; i++) {
		
		var divObj = document.getElementById("trValueId_" + uuids[i]);
		var oAttribs = divObj.attributes;
		
		for (var j = 0; j < oAttribs.length; j++){
			if(oAttribs[j].specified == true){
				requestString += "&" + oAttribs[j].nodeName + "=" + oAttribs[j].nodeValue;
			}
		}
	}
	
	var url = MATECH_SYSTEM_WEB_ROOT + "/formQueryConfig.do?method=buttonExtHandle&btnId=" + btnId;
	//alert(url);
	//alert(requestString);
	var result = ajaxLoadPageSynch(url, requestString);
	
	alert(result);
	
	
	//alert(method);
	try{
		var method = "goSearch_" + tableId + "(2)";
		eval(method);
	}catch(e){
		alert(e);
	}
}

//è¡¨å•æŒ‰é’®æ¥å£
function mt_form_formBtn_CallJava(btnId) {
	//alert(tableId);
	
	var uuid = document.getElementById("uuid").value;
	
	var requestString = "&uuid=" + uuid;
	
	var url = MATECH_SYSTEM_WEB_ROOT + "/formQueryConfig.do?method=buttonExtHandle&btnId=" + btnId;
	
	var result = ajaxLoadPageSynch(url, requestString);
	
	alert(result);
}

//åˆ›å»ºè¡Œ
function mt_createRow(tableId,rows,cells){
	var rs,cs;
	
	if(rows == ""){
		rs = 0;
	} else {
		rs = rows;
	}
	
	if(cells == ""){
		cs = 0; 
	} else {
		cs = cells;
	}
	
	var table = document.getElementById(tableId);
       
	for(var i=0; i<rs; i++){
		//æ·»åŠ ä¸€è¡Œ
        var newTr = table.insertRow();
		for(var j = 0; j < cs; j++){
	       //æ·»åŠ åˆ—
			var newTd = newTr.insertCell();
		}
	}
}

//åˆ é™¤
function mt_remove(t){
	
	if(confirm("æ‚¨ç¡®å®šè¦åˆ é™¤å—?")){
		t.parentNode.parentNode.removeNode(true);
		
		if(t.group) { 
			//åˆ é™¤åŒç»„çš„è®°å½•
			var imgs = Ext.query("img[group="+t.group+"]") ;
			Ext.each(imgs,function(img){
				img.parentNode.parentNode.removeNode(true);
			}) ;
			
		}
	}
	
	mt_form_total();   
	
	//éšè—åˆ—
	if(funExists("mt_subform_after_del")) {
		mt_subform_after_del(t);
	}
}

//åˆ é™¤æ‰€æœ‰è¡Œ
function mt_remove_all(table) {
	
    var tableObj = document.getElementById(table);
    
    for(var i=tableObj.rows.length-1; i >= 0 ; i--) {
    	tableObj.deleteRow(i);
    }
}

//æ–°å¢ä¸€è¡Œ
function mt_add(table,length){
	
	var randomId = Math.round(Math.random() * 10000);
	
	var mt_slist = eval('(' + document.getElementById("mt_slist_" + table).value + ')');
	
	mt_createRow(table,1,length);
	
	var slistLength = mt_slist.length ;
	
	var tbField = document.getElementById(table);
	
	tbField.rows[tbField.rows.length-1].cells[0].innerHTML = "<img id='"+table+"_delImg_"+randomId+"' flag="+table+ "_del style='cursor:hand;' alt='åˆ é™¤æœ¬è¡Œ' onclick='mt_remove(this)' src='" + MATECH_SYSTEM_WEB_ROOT + "/img/close.gif' >";
	
	var colCount = 0;
	
	var cell = tbField.rows[tbField.rows.length-1].cells[0];
	
	for(var i = 1; i <= slistLength; i++){
	
		var inputObj = document.createElement(mt_slist[i-1]) ;
		
		
		if(inputObj.id.indexOf("hidden_") < 0) {
			colCount++;
		}
		
		inputObj.id = inputObj.id + '_' + randomId ;
		
		if(inputObj.refer) {
			//$2, æ›¿æ¢æˆæœ¬è¡Œçš„ID
			inputObj.refer = inputObj.refer.replace("_\$rowIndex", "_" + randomId);
		}
		
		if(inputObj.refer1) {
			//$3, æ›¿æ¢æˆæœ¬è¡Œçš„ID
			inputObj.refer1 = inputObj.refer1.replace("_\$rowIndex", "_" + randomId);
		}
		
		if(inputObj.refer2) {
			//$4, æ›¿æ¢æˆæœ¬è¡Œçš„ID
			inputObj.refer2 = inputObj.refer2.replace("_\$rowIndex", "_" + randomId);
		}
		
	
		if(inputObj.type != "hidden") {
			cell = cell.nextSibling;
		}
		if(cell) {
			cell.appendChild(inputObj) ;
		}else {
			alert("åˆ—æ•°è®¾ç½®è¶…è¿‡è¡¨å¤´å®šä¹‰åˆ—æ•°,è¯·æ£€æŸ¥é…ç½®!");
			return ;
		}
		
		if(inputObj.autoid) {
			initCombox(inputObj);
		}
		
		if(inputObj.ext_type == "date") {
			mt_form_initDateSelect(inputObj);
		}
		
	}
	//éšè—åˆ—
	if(funExists("mt_subform_after_add")) {
		mt_subform_after_add(table);
	}
	return randomId;
}

//ä¸‹æ‹‰GRIDå¡«å……åˆ—è¡¨å€¼
function mt_form_setRowValue(obj) {

	var inputId = obj.inputId;
	var inputProperty = obj.property;
	var name = inputId.replace(inputProperty,"");
	
	var rowIndex = name.split("_")[1];
	var json = Ext.util.JSON.decode(obj.columns);
	
	for(var field in json) {
		var fieldId = field.toLowerCase().replace("hidden_","");
		fieldId = inputProperty + fieldId + "_" + rowIndex;
		if(document.getElementById(fieldId)) {
			document.getElementById(fieldId).value = json[field];
			
			if(Ext.getCmp(fieldId)) {
				Ext.getCmp(fieldId).setRealValue(json[field]);
			}
		}
	}
}

//ä¸‹æ‹‰GRIDå¡«å……åˆ—è¡¨å€¼
function mt_form_setRowValues(obj) {
	var jsonArray = Ext.util.JSON.decode(obj.columns);

	var property = obj.property;
	var tableName = property.split("`")[0];
	var colCount = property.split("`")[1];
	
	mt_remove_all(tableName);
	
	for(var i=0; i < jsonArray.length; i++) {
		var json = jsonArray[i];
		var rowIndex = mt_add(tableName, colCount);
		for(var field in jsonArray[i]) {
			var fieldId = field.toLowerCase().replace("hidden_","");
			
			if(fieldId == "select_group") {
				document.getElementById(tableName +"_delImg_"+rowIndex).group = json[field];
			}
			
			fieldId = tableName + "_" + fieldId + "_" + rowIndex;
			if(document.getElementById(fieldId)) {
				document.getElementById(fieldId).value = json[field];
				
				if(Ext.getCmp(fieldId)) {
					Ext.getCmp(fieldId).setRealValue(json[field]);
				}
			}
		}
	}
}

//ä¸‹æ‹‰GRIDå¡«å……è¡¨å•å€¼
function mt_form_setValue(obj) {

	var json = Ext.util.JSON.decode(obj.columns);
	
	for(var field in json) {
		var fieldId = field.toLowerCase().replace("hidden_","");
		if(document.getElementById(fieldId)) {
			document.getElementById(fieldId).value = json[field];
			
			if(Ext.getCmp(fieldId)) {
				Ext.getCmp(fieldId).setRealValue(json[field]);
			}
			//Ext.getCmp(fieldId).setRawValue
			//initCombox(document.getElementById(fieldId));
		}
	}
}

//åˆå§‹åŒ–é™„ä»¶ä¸Šä¼ æ§ä»¶
function mt_form_initAttachFile(param) {
	var inputArray ;
	
	if(param) {
		if(typeof(param) == "string") {
			inputArray = Ext.query("#"+param) ;
		}else {
			var arr = new Array();
			arr.push(param) ;
			inputArray = arr ;
		}
	} else {
		inputArray = Ext.query("input[ext_type=attachFile]") ;
	}
	
	 
	Ext.each(inputArray,function(input){
		attachInit(input.id);
	});

}

//åˆå§‹åŒ–extjsæ—¥æœŸæ§ä»¶
function mt_form_initDateSelect(param) {
	var inputArray ;
	
	if(param) {
		if(typeof(param) == "string") {
			inputArray = Ext.query("#"+param) ;
		}else {
			var arr = new Array();
			arr.push(param) ;
			inputArray = arr ;
		}
	} else {
		inputArray = Ext.query("input[ext_type=date]") ;

	}
	
	var plugins = "";
	var format = "Y-m-d";
	
	Ext.each(inputArray,function(input){
		
		if(!input.readOnly) {
			if(input.ext_format) {
				
				if(input.ext_format == "yyyy-MM-dd") {
					plugins = "";
					format = "Y-m-d";
				} else if(input.ext_format == "yyyy-MM") {
					plugins = "monthPickerPlugin";
					format = "Y-m";
				} 
			}
			
			new Ext.form.DateField({
				applyTo:input.id,
				width:100,  
				plugins: plugins,  
				format: format,  
				editable: false,
				cls:"inline"
			});
		}
		
	});
}

//åˆå§‹åŒ–åªè¯»
function mt_form_initReadonly() {
	var inputArray = Ext.query("input[ext_readonly]") ;
	
	Ext.each(inputArray,function(input){
			input.className = "readonly";
	});
}

function mt_form_total(obj) {
	
	var formulaArray = Ext.query("input[formula]") ;
	
	Ext.each(formulaArray,function(input){
		
		var formula = input.formula ;
		formula = formula.replace(new RegExp("sum\\(","gm"),"mt_form_sum("); 
		formula = formula.replace(new RegExp("value\\(","gm"),"mt_form_value("); 
		formula = formula.replace(new RegExp("sumif\\(","gm"),"mt_form_sumif("); 
		formula = formula.replace(new RegExp("rowValue\\(","gm"),"mt_form_rowValue("); 
		formula = formula.replace(new RegExp("`","gm"),"'"); 
		formula = formula.replace(new RegExp("ï¼Œ","gm"),","); 
		formula = formula.replace(new RegExp("ã€Š","gm"),"<"); 
		formula = formula.replace(new RegExp("ã€‹","gm"),">"); 
		
		if(obj) {
			//åªæ‰§è¡Œç›¸å…³çš„
			var objName = obj.name ;
			if(formula.indexOf(objName) > -1) {
				
				if(formula) {
	 				var formulaValue = eval(formula) ;
	 				input.value = formulaValue ;
	 				input.fireEvent("onchange") ;
	 			}
			}
		}else {
			//æ‰§è¡Œå…¨éƒ¨
			if(formula) {
 				var formulaValue = eval(formula) ;
 				input.value = formulaValue ;
 				input.fireEvent("onchange") ;
 			}
		}
		
			
	});
}

function mt_form_sum(name){
	var sum = 0.00;
	var sumArray = document.getElementsByName(name) ;
	for(var i = 0;i<sumArray.length;i++){
		var sumValue = sumArray[i].value ;
		
		if(sumValue) {
			sum += parseFloat(sumValue);
		}
	}
	return sum ;
}
	
function mt_form_value(name){
		
	var sum = 0.00;
	var sumArray = document.getElementsByName(name) ;
	if(sumArray) {
		if(sumArray[0].value)
			sum += parseFloat(sumArray[0].value);
	}
	return sum ;
}
	
function mt_form_sumif(condition,name1,name2){
	
	var name1Arr = document.getElementsByName(name1) ;
	var name2Arr = document.getElementsByName(name2) ; 
	var forName = name1Arr ;
	if(name1Arr.length < 1) {
		forName = name2Arr ;
	}
	
	var sumValue = 0.00 ;
	for(var i = 0;i<forName.length;i++){
		var curCondition = condition.replace(new RegExp("\\\$rowObj","gm"),"curObj"); 
		var curObj = forName[i] ;
		var conditionResult ; 
		
		try {
			conditionResult = eval(curCondition);
		}catch(e) {
			alert("æ¡ä»¶ã€" + condition + "ã€‘å‡ºç°è¯­æ³•é”™è¯¯,é”™è¯¯åŸå› ï¼š"+e+"è¯·è”ç³»ç®¡ç†å‘˜æ£€æŸ¥!") ;
			return ;
		}
		if(conditionResult) {
			if(name1Arr[i]) {
				sumValue += parseFloat(name1Arr[i].value ? name1Arr[i].value : 0) ;
			}
		}else {
			if(name2Arr[i]) {
				sumValue += parseFloat(name2Arr[i].value ? name2Arr[i].value : 0) ;
			}
		}
	}
	return sumValue ;
}

function mt_form_rowValue(name,obj){
	if(!obj) return ; 
	var srcElement = obj ;  
	
	var trObj = srcElement.parentNode.parentNode ;
	var trElement = Ext.fly(trObj) ;
	var curRowObj = trElement.child('input[name='+name+']',true) ;
	var value = 0.00;
	if(curRowObj) {
		value = curRowObj.value ;
	}
	if(!value) value = 0.00;
	return parseFloat(value) ;
}


function mt_form_initSubmit() {
	var formArray = Ext.query("form") ;
	
	Ext.each(formArray,function(form){
		form.tempSubmit = form.submit ;
		form.submit = function (){
			showWaiting();
			form.tempSubmit();
		};	 
	});
}

function mt_form_checkState(stateField) {
	var data = mt_form_getRowValues();
	if(!data) {
		return false;
	}

	for(var i=0; i < data.length; i++) {
		var state = eval("data[" + i + "]." + stateField);
		if(state != 'è‰ç¨¿' && state != 'é€€ä»¶') {
			alert("è¯¥æ•°æ®çŠ¶æ€ä¸º[" + state  + "],ä¸å…è®¸æ“ä½œè¯¥æ•°æ®!!");
			return false;
		} 
	}
   
	return true;
}

function mt_form_saveUrl(){
	
	Ext.Ajax.request({
        url : 'formDefine.do'
        ,method:'post',
        params:{
            method:'saveUrl'
            ,url:window.location.href
            }

        ,success : function(response, options) {}
	});
	return true;
}

Ext.onReady(function(){
	mt_form_initReadonly();
	mt_form_initSubmit();
});/*
 * Really easy field validation with Prototype
 * http://tetlaw.id.au/view/blog/really-easy-field-validation-with-prototype
 * Andrew Tetlaw
 * Version 1.4 (2006-05-18)
 * Thanks:
 *  Mike Rumble http://www.mikerumble.co.uk/ for onblur idea
 *  Analgesia for spotting a typo
 *  Paul Shannon http://www.paulshannon.com for the reset idea
 *  Ted Wise for the focus-on-first-error idea
 *  Sidney http://www.creativelycrazy.de/ for the custom advice idea
 *
 * http://creativecommons.org/licenses/by-sa/2.5/
 */
Validator = Class.create();

Validator.prototype = {
	initialize : function(className, error, test) {
		this.test = test ? test : function(){ return true };
		this.error = error ? error : 'ÓòÖµÊäÈë·Ç·¨.';
		this.className = className;
	}
}

var Validation = Class.create();

Validation.prototype = {
	initialize : function(form, options){
		this.options = Object.extend({
			stopOnFirst : false,
			immediate : false,
			focusOnError : true
		}, options || {});
		this.form = $(form);
		Event.observe(this.form,'submit',this.onSubmit.bind(this),false);
		if(this.options.immediate) {
			Form.getElements(this.form).each(function(input) { // Thanks Mike!
				Event.observe(input, 'blur', function(ev) { Validation.validate(Event.element(ev).id) });
			});
		}
	},
	onSubmit :  function(ev){
		if(!this.validate()) {
			Event.stop(ev);
			if(this.options.focusOnError) {
				$$('.validation-failed').first().focus();
			}
		}
	},
	validate : function() {
		var t;
		if(this.options.stopOnFirst) {
			t=Form.getElements(this.form).all(Validation.validate);
		} else {
			t=Form.getElements(this.form).collect(Validation.validate).all();
		}
		if (t) {
			//document.write("<span id=bxDlg_bg align=center oncontextmenu=\"return false\" onselectstart=\"return false\" style=\"visibility:hidden;width:100%;height:100%;position:absolute;left:0;top:0\"></span>");
			var scrollHeight=document.body.scrollTop;//¹ö¶¯Ìõ×İÏò¹ö¶¯µÄ¸ß¶È
  			var winHeight = document.body.clientHeight;//´°¿Ú¸ß¶È
			var winWidth  = document.body.clientWidth;//´°¿Ú¿í¶È
			var boxHeight=60;//ÎÄ±¾¿ò¸ß¶È
			var boxWidth=200;//ÎÄ±¾¿ò¿í¶È
			var boxTop=winHeight/2-boxHeight/2+scrollHeight;//ÎÄ±¾¿ò×óÉÏ½ÇËùÔÚµÄ×İ×ø±ê
			var boxLeft=winWidth/2-boxWidth/2;//ÎÄ±¾¿ò×óÉÏ½ÇËùÔÚµÄºá×ø±ê

			var strTalk="<span id=bxDlg_bg align=center oncontextmenu=\"return false\" onselectstart=\"return false\" style=\"width:"+document.body.scrollWidth+";height:"+document.body.scrollHeight+";position:absolute;left:0;top:0\"><div id=bxDlg_bg1 style=height:100%;background:white;filter:alpha(opacity=50)>&nbsp;</div></span>"
				+"<span  style='background:#E4E4E4;POSITION:absolute;padding:20px 40px 20px 40px;"
    				+"left:"+boxLeft+";top:"+boxTop+"; width:"+boxWidth+"px; height:"+boxHeight+"px; border:1px solid #666666;'><img src='/AuditSystem/images/indicator.gif' />´¦ÀíÖĞ<br/>,ÇëÉÔºò¡­¡­</span>";
    			//ÕâÀïÈç¹ûÓÃwrite·½·¨£¬¾Í»á×èÈûsubmitµÄ¼ÌĞøÌá½»£¬ºÃÆæ¹Ö°¡¡£
    			document.body.insertAdjacentHTML("beforeEnd",strTalk);
    			document.close();
		}
		return t;
	},
	reset : function() {
		Form.getElements(this.form).each(Validation.reset);
	},
	check : function(){
		
		var t;
		if(this.options.stopOnFirst) {
			t=Form.getElements(this.form).all(Validation.validate);
		} else {
			t=Form.getElements(this.form).collect(Validation.validate).all();
		}
		return t;
	}
}

Object.extend(Validation, {
	validate : function(elm, index, options){ // index is here only because we use this function in Enumerations
		var options = Object.extend({}, options || {}); // options still under development and here as a placeholder only
		elm = $(elm);
		var cn = elm.classNames();
		/*
		try{
			alert(elm);
			alert(elm.parentNode.innerHTML);
			alert(this.parentNode.innerHTML);
		}catch(e){}
		*/
		return result = cn.all(Validation.test.bind(elm)); 
	},

	//winnerĞŞ¸ÄÓÚ20060529£¬Ôö¼ÓÁËÇ°ºó¿Õ¸ñ¹ıÂË¹¦ÄÜ
	trim : function (strSource){
		var t="";
		try{
		 t=strSource.replace(/^\s*/,'').replace(/\s*$/,'');
		}catch(e){}
		return t;
	},

	test : function(name) {
		
		var v = Validation.get(name);
		var prop = '__advice'+name.camelize();
		var passed =false;
		var backgroundvalid=false;
		//winnerĞŞ¸ÄÓÚ20060529£¬Ôö¼ÓÁËÇ°ºó¿Õ¸ñ¹ıÂË¹¦ÄÜ£¬²¢Ôö¼ÓÁËºóÌ¨Ğ£ÑéµÄ¹¦ÄÜ
		//ÏÈÎŞÌõ¼ş½øĞĞ±¾µØ²âÊÔ
		//ÕâÀï$F¾ÓÈ»È¡²»³öFile£¬¸ÄÎª3ÔªÔËËã·û
		
		if(this.clone) { //¿ËÂ¡³öÀ´µÄinput¿ò²»ÑéÖ¤
			return true;
		} 
		this.value = this.value.replace("ÇëÑ¡Ôñ...","") ;
		this.value = this.value.replace("ÇëÑ¡Ôñ»òÊäÈë...","") ;
		
		//alert(Validation.isVisible(this) + this.name);
		if(!v.test(Validation.trim(((this.type=="file") ? this.value : $F(this)))) && Validation.isVisible(this)) {
			passed=false;
		}else{
			//²âÊÔÍ¨¹ı£¬Ôò¿´ÊÇ·ñÉèÖÃÁËvaluemustexist£¬ÉèÖÃÁËÔòÌá½»ºóÌ¨·şÎñÆ÷½øĞĞ¼ì²é
			passed=true;
			//if (this.valuemustexist && this.valuemustexist=="true" && !(this.value==null || this.value=="")){
			//Ôö¼ÓCheckAnyWay on 20070824
			
			if ((this.valuemustexist && this.valuemustexist=="true" && !(this.value==null || this.value==""))
				||(this.CheckAnyWay && this.CheckAnyWay=="true")){
				//¶Ô¿òÖĞÓĞ'+'µÄÖµ½øĞĞ×ªÒå
				var corectValue = this.value;
				
				var newValue = "";
				if(corectValue!=null && corectValue != ""  && corectValue.indexOf('+') != -1 ) {
				  for(var i=0; i<corectValue.length; i++) {
					var s = corectValue.charAt(i);
				    if(s == '+') {
					  newValue += "%2B";
					}else {
					  newValue += s;
					}
				  }
				}else{
					newValue = corectValue;
				}
				//alert(newValue);

				
				var url="/AuditSystem/AS_SYSTEM/hint.do?checkmode=1&autoid="+this.autoid+"&pk1="+ newValue;
				
				if (this.refer){
					var qqq=document.getElementById(this.refer);
					if (qqq && qqq.value!=null && qqq.value!=""){
						url+="&refer="+qqq.value;
					}else{
						url+="&refer="+this.refer;
					}
				}
				if (this.refer1){
					var qqq=document.getElementById(this.refer1);
					if (qqq && qqq.value!=null && qqq.value!=""){
						url+="&refer1="+qqq.value;
					}else{
						url+="&refer1="+this.refer1;
					}
				}
				if (this.refer2){
					var qqq=document.getElementById(this.refer2);
					if (qqq && qqq.value!=null && qqq.value!=""){
						url+="&refer2="+qqq.value;
					}else{
						url+="&refer2="+this.refer2;
					}
				}

				//½øĞĞºóÌ¨Ğ£Ñé
				var oBao = new ActiveXObject("Microsoft.XMLHTTP");
				//oBao.asynchronous=false;
  				oBao.open("POST",url,false);
  				oBao.send();
  				var strResult = unescape(oBao.responseText);
  				if(strResult.indexOf('ERROR')>=0){
  					passed=false;
  					backgroundvalid=true;
  				}
			}
		}
		
		if (passed==false){
			
			if(!this[prop]) {
				
				//ÅĞ¶ÏÊÇ·ñÒÑ¾­ÏÔÊ¾¹ıÌáÊ¾£¬»¹Ã»ÓĞÏÔÊ¾¹ıÌáÊ¾Ôò½øÈë£¬
				var advice = Validation.getAdvice(name, this.name);
				
				if(typeof advice == 'undefined') { 
					advice = document.createElement('span');
					//winner ĞŞ¸ÄÓÚ20060528,Ôö¼ÓÁËÓÅÏÈÏÔÊ¾TITLEµÄ¹¦ÄÜ
					/* winner µ÷ÊÔÊ¹ÓÃ
					try{
						alert(this.tagName);
						alert(this.title);
						alert(this.parentNode.innerHTML);
					}catch(e){}

					*/
					//advice.appendChild(document.createTextNode(v.error));
					advice.appendChild(document.createTextNode(this.value && backgroundvalid ? "ÊäÈëµÄÖµ²»´æÔÚ" : (this.title ? this.title :v.error)));
					advice.className = 'validation-advice';
					advice.id = 'advice2-' + this.name; 
					advice.style.display = 'none';   
					this.parentNode.insertBefore(advice, this.nextSibling);
				}else{
					advice.className = 'validation-advice';
					advice.style.display = 'none';
					advice.innerHTML=(this.value ? "ÊäÈëµÄÖµ²»´æÔÚ" : (this.title ? this.title :v.error));
				}

				if(typeof Effect == 'undefined') {
					advice.style.display = 'inline';
				} else {
					new Effect.Appear(advice.id, {duration : 1 });
				}
			}else{
				var advice = Validation.getAdvice(name, this.name);
				//advice.innerHTML=(this.value ? "ÊäÈëµÄÖµ²»´æÔÚ" : (this.title ? this.title :v.error));
			}
			this[prop] = true;
			this.removeClassName('validation-passed');
			this.addClassName('validation-failed');


			//ÉèÖÃÒÑ¾­ÏÔÊ¾µÄÌáÊ¾Îª¿Õ
			/*
			var hintadvice = Validation.getHintAdvice(this.name);
			if(typeof hintadvice != 'undefined') hintadvice.innerHTML="";
			*/
			
			return false;
		} else {
			var advice = Validation.getAdvice(name, this.name);
			if(typeof advice != 'undefined' && !advice.clone) advice.hide();
			this[prop] = '';
			this.removeClassName('validation-failed');
			this.addClassName('validation-passed');
			return true;
		}
	},
	isVisible : function(elm) {
		while(elm.tagName != 'BODY') {
			if(!$(elm).visible()) return false;
			elm = elm.parentNode;
		}
		return true; 
	},
	getAdvice : function(name, id) {
		var advice = Try.these(
			//function(){ return $('advice-' + name + '-' + id) },
			function(){
				var adviceObj = document.getElementById(id);
				if(adviceObj) {
					var tempId = "advice2-"+id ;
					var advice =  document.getElementById(tempId) ;;
					if(adviceObj.useAdvice) {
						adviceObj = document.getElementById('advice2-' + id) ;
					}
					
					if(!advice) {
						var newAdvice = document.createElement('span'); 
						newAdvice.id = tempId ;
						adviceObj.parentNode.insertBefore(newAdvice, adviceObj.nextSibling.nextSibling);
						return $(newAdvice.id);
					}
				}
				return $('advice2-' + id) 
			}
		);
		return advice;
	},
	getHintAdvice : function(name) {
		var advice = Try.these(
			function(){ return $('advice2-' + name) }
		);
		return advice;
	},
	reset : function(elm) {
		var cn = elm.classNames();
		cn.each(function(value) {
			var prop = '__advice'+value.camelize();
			if(elm[prop]) {
				var advice = Validation.getAdvice(value, elm.id);
				advice.hide();
				elm[prop] = '';
			}
			elm.removeClassName('validation-failed');
			elm.removeClassName('validation-passed');
		});
	},
	add : function(className, error, test, options) {
		var nv = {};
		nv[className] = new Validator(className, error, test, options);
		Object.extend(Validation.methods, nv);
	},
	addAllThese : function(validators) {
		var nv = {};
		$A(validators).each(function(value) {
				nv[value[0]] = new Validator(value[0], value[1], value[2], (value.length > 3 ? value[3] : {}));
			});
		Object.extend(Validation.methods, nv);
	},
	get : function(name) {
		return  Validation.methods[name] ? Validation.methods[name] : new Validator();
	},
	methods : {}
});

//var $V = Validation.validate;
//var $VG = Validation.get;
//var $VA = Validation.add;

Validation.add('IsEmpty', '', function(v) {
				return  ((v == null) || (v.length == 0) || /^\s+$/.test(v));
			});

Validation.addAllThese([
	/*-- Ğ£Ñé±ØÌî --*/
	['required', 'ÇëÊäÈëÓĞĞ§Öµ.', function(v) {
				//alert(v);
				return !Validation.get('IsEmpty').test(v);
			}],
	['checkexist-wheninputed', 'ÇëÊäÈëÓĞĞ§Öµ.', function(v) {

    		if (v==null || v==""){
    			return true;
    		}else{
				return !Validation.get('IsEmpty').test(v);
			}
			}],
	['validate-number', 'Please use numbers only in this field?', function(v) {
				return Validation.get('IsEmpty').test(v) || !isNaN(v);
			}],
	/*-- Ğ£ÑéÕûÊı --*/
	['validate-digits', 'ÇëÊäÈëÕûÊı.', function(v) {

				return Validation.get('IsEmpty').test(v) ||  !/[^\d*$]/.test(v);
			}],
				/*-- Ğ£ÑéÕûÊı --*/
	['validate-positiveInt', 'ÇëÊäÈë´óÓÚ0µÄÕûÊı.', function(v) {

				return Validation.get('IsEmpty').test(v) || /^[0-9]*[1-9][0-9]*$/.test(v);
			}],
	['validate-alpha', 'Please use letters only (a-z) in this field.', function (v) {
				return Validation.get('IsEmpty').test(v) ||  /^[a-zA-Z]+$/.test(v)
			}],
	['validate-alphanum', 'Please use only letters (a-z) or numbers (0-9) only in this field. No spaces or other characters are allowed.', function(v) {
				return Validation.get('IsEmpty').test(v) ||  !/\W/.test(v)
			}],
	['validate-date', 'Please enter a valid date.', function(v) {
				try{
				var test = new Date(v);
				return Validation.get('IsEmpty').test(v) || !isNaN(test);
				}catch(e){}
			}],
	/*-- Ğ£ÑéÓÊÏä --*/
	['validate-email', 'ÇëÊäÈëÓĞĞ§ÓÊÏä. ÀıÈç username@domain.com .', function (v) {
				return Validation.get('IsEmpty').test(v) || /\w{1,}[@][\w\-]{1,}([.]([\w\-]{1,})){1,3}$/.test(v)
			}],
	['validate-date-au', 'Please use this date format: dd/mm/yyyy. For example 17/03/2006 for the 17th of March, 2006.', function(v) {
				if(!Validation.get('IsEmpty').test(v)) {
					var upper = 31;
					if(/^(\d{2})\/(\d{2})\/(\d{4})$/.test(v)) { // dd/mm/yyyy
						if(RegExp.$2 == '02') upper = 29;
						if((RegExp.$1 <= upper) && (RegExp.$2 <= 12)) {
							return true;
						} else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return true;
				}
			}],
	/*-- Ğ£Ñé»õ±Ò --*/
	['validate-currency', 'ÇëÊäÈëÓĞĞ§»õ±Ò.ÀıÈç100.00 .', function(v) {
				// [$]1[##][,###]+[.##]
				// [$]1###+[.##]
				// [$]0.##
				// [$].##
				return Validation.get('IsEmpty').test(v) ||  /^([1-9,-]{1}[0-9,-]{0,2}(\,[0-9,-]{3})*(\.[0-9,-]{0,2})?|[1-9,-]{1}\d*(\.[0-9,-]{0,2})?|0(\.[0-9,-]{0,2})?|(\.[0-9,-]{1,2})?)$/.test(v)
			}]

	/*  winner adds here	*/
	,
	/*-- Ğ£ÑéÈÕÆÚÎª¿Õ¾Í²»¼ì²é --*/
	['validate-date-cn','ÇëÊ¹ÓÃÈÕÆÚ¸ñÊ½: yyyy-mm-dd. ÀıÈç 2006-03-17', function(v){
    		/*-- ÈÕÆÚ¸ñÊ½£º(ËÄÎ»)Äê·İ + (ÖÁ¶àÁ½Î»)ÔÂ·İ + (ÖÁ¶àÁ½Î»)ÈÕÆÚ ,ÒÑ¾­¿¼ÂÇÁËÈòÄêµÈÈÕÆÚ--*/
    		if (v==null || v==""){
    			return true;
    		}
     		if(!/^(?:([0-9]{4}-(?:(?:0?[1,3-9]|1[0-2])-(?:29|30)|((?:0?[13578]|1[02])-31)))|([0-9]{4}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1\d|2[0-8]))|(((?:(\d\d(?:0[48]|[2468][048]|[13579][26]))|(?:0[48]00|[2468][048]00|[13579][26]00))-0?2-29)))$/.test(v))
     		{
      			return false;
     		}
     		return true;
		}]
		,
	/*-- Ğ£ÑéÈÕÆÚÎª¿Õ»áÈÏÎª¼ì²é²»Í¨¹ı --*/
	['validate-date-cn-required','ÇëÊ¹ÓÃÈÕÆÚ¸ñÊ½: yyyy-mm-dd. ÀıÈç 2006-03-17', function(v){
    		/*-- ÈÕÆÚ¸ñÊ½£º(ËÄÎ»)Äê·İ + (ÖÁ¶àÁ½Î»)ÔÂ·İ + (ÖÁ¶àÁ½Î»)ÈÕÆÚ ,ÒÑ¾­¿¼ÂÇÁËÈòÄêµÈÈÕÆÚ--*/
     		if(!/^(?:([0-9]{4}-(?:(?:0?[1,3-9]|1[0-2])-(?:29|30)|((?:0?[13578]|1[02])-31)))|([0-9]{4}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1\d|2[0-8]))|(((?:(\d\d(?:0[48]|[2468][048]|[13579][26]))|(?:0[48]00|[2468][048]00|[13579][26]00))-0?2-29)))$/.test(v))
     		{
      			return false;
     		}
     		return true;
		}]

	,
	/*-- Ğ£ÑéÁ½´ÎÊäÈëÃÜÂë,Çë×¢ÒâĞèÒªµÚÒ»´ÎÊäÈëÃÜÂëµÄID½Ğ×öpassword --*/
	['validate-passwd-identical', 'Á½´ÎÊäÈëµÄÃÜÂë±ØĞëÒ»ÖÂ', function(v){
			if(Validation.get('IsEmpty').test(v) && Validation.get('IsEmpty').test($F("password"))){
				return true;
			}
        	return !Validation.get('IsEmpty').test(v) && v == $F("password");
    	}]
    	,
	/*-- Ğ£Ñéµç»°ºÅÂë --*/
	['validate-phonenumber', 'µç»°ºÅÂë¿ÉÒÔÓÃ+¿ªÍ·£¬Ö»ÄÜÊäÈëÊı×Ö,-·ûºÅ ÀıÈç020-12345678', function(v){
        	if(!/^[+]{0,1}(\d){1,3}[ ]?([-£­]?((\d)|[ ]){1,12})+$/.test(v))
     		{
      			return false;
     		}
     		return true;
    	}]
	/*winner's add ends here*/
        ,
        /*-- Ğ£Ñéµç»°ºÅÂë ¿ÉÒÔÎª¿Õ--*/
	['phonenumber-wheninputed', 'µç»°ºÅÂë¿ÉÒÔÓÃ+¿ªÍ·£¬Ö»ÄÜÊäÈëÊı×Ö,-·ûºÅ ÀıÈç020-12345678', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(!/^[+]{0,1}(\d){1,3}[ ]?([-£­]?((\d)|[ ]){1,12})+$/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
    	}]
        ,
        /*-- Äê·İÑéÖ¤ ¿ÉÒÔÎª¿Õ--*/
	['year-wheninputed', 'Äê·İÊÇÓÉËÄ¸öÊı×Ö×é³É¡£', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(!/^\d{4}$/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
    	}]
        ,
        /*-- ÔÂ·İÑéÖ¤ ¿ÉÒÔÎª¿Õ--*/
	['month-wheninputed', 'ÔÂ·İÊÇÓÉÁ½¸öÊı×Ö×é³É¡£', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(!/^\d{2}$/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
    	}]
    	
        ,
        /*-- Êı×Ö£¬×ÖÄ¸£¬ÏÂ»®Ïß£¬ºÍ - ---*/
	['alphanum-wheninputed', 'ÇëÊäÈëÊı×Ö£¬×ÖÄ¸£¬ÏÂ»®Ïß£¬»ò - ¡£', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(!/^[\w-]{1,}[\w-]*$/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
    	}] 

        ,
        /*-- ÎÄ¼şÃûÑéÖ¤---*/
	['filename-wheninputed', 'ÎÄ¼şÃû²»ÄÜ°üº¬\\/:*?"<>|', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(/[\\\/\*\?"<>|]+/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
    	}] 
		,
		/*-- 0ÖÁ100µÄÊı×ÖÑéÖ¤ --*/
	['0-100-wheninputed', 'ÇëÊäÈë0£­100µÄÊı×Ö', function(v) {
				return (Validation.get('IsEmpty').test(v) || !isNaN(v))&&parseFloat(v)<100&&parseFloat(v)>0;
			}]
			
		,	
	/*-- ip,ÍøÂçµØÖ·µÄÑéÖ¤ --*/
	['ip-wheninputed', 'ÇëÊäÈëip»òÕßÍøÖ·', function(v) {
				return (Validation.get('IsEmpty').test(v) || isIP(v));
			}]

]);


function setObjDisabled(name){
	var oElem=document.getElementById(name);
		var sTag=oElem.tagName.toUpperCase();
		switch(sTag)
		{
		case	"BUTTON":
			oElem.disabled=true;
			break;
		case	"SELECT":
		case	"TEXTAREA":
			oElem.readOnly=true;
			break;
		case	"INPUT":
			{
			var sType=oElem.type.toUpperCase();

			if(sType=="TEXT")oElem.readOnly=true;
			if(sType=="BUTTON"||sType=="IMAGE")oElem.disabled=true;
			if(sType=="CHECKBOX")oElem.disabled=true;
			if(sType=="RADIO")oElem.disabled=true;
			}
			break;
		default:
			oElem.disabled=true;
			break;
		}
	//set style
	oElem.style.backgroundColor="#eeeeee";
}

function setObjEnabled(name){
	var oElem=document.getElementById(name);
		var sTag=oElem.tagName.toUpperCase();
		switch(sTag)
		{
		case	"BUTTON":
			oElem.disabled=false;
			break;
		case	"SELECT":
		case	"TEXTAREA":
			oElem.readOnly=false;
			break;
		case	"INPUT":
			{
			var sType=oElem.type.toUpperCase();

			if(sType=="TEXT")oElem.readOnly=false;
			if(sType=="BUTTON"||sType=="IMAGE")oElem.disabled=false;
			if(sType=="CHECKBOX")oElem.disabled=false;
			if(sType=="RADIO")oElem.disabled=false;
			}
			break;
		default:
			oElem.disabled=false;
			break;
		}
	//set style
	oElem.style.backgroundColor="#FFFFFF";
}


function show(obj){
	$(obj).style.visibility=""
}

function hide(obj){
	$(obj).style.visibility="hidden"
}

function isIP(strIP) { 
	if (isNull(strIP)) return false; 
	var re=/^(\d+)\.(\d+)\.(\d+)\.(\d+)$/   //Æ¥ÅäIPµØÖ·µÄÕıÔò±í´ïÊ½ 
	var res= /^[a-zA-z0-9]+(\.[a-zA-z0-9]+)*$/  //Æ¥ÅäÓÃ·şÎñÆ÷ÃûµÄÕıÔò±í´ïÊ½ 
	re=new RegExp(re);
	res=new RegExp(res);
	if(re.test(strIP)) 
	{ 
		if( RegExp.$1 <256 && RegExp.$2<256 && RegExp.$3<256 && RegExp.$4<256)
		{
	
			return true;
		}
	} 
	if(res.test(strIP)){
		return true;
	}
	return false; 
} 
 
/* 
ÓÃÍ¾£º¼ì²éÊäÈë×Ö·û´®ÊÇ·ñÎª¿Õ»òÕßÈ«²¿¶¼ÊÇ¿Õ¸ñ 
ÊäÈë£ºstr 
·µ»Ø£º 
Èç¹ûÈ«ÊÇ¿Õ·µ»Øtrue,·ñÔò·µ»Øfalse 
*/ 
function isNull( str ){ 
if ( str == "" ) return true; 
var regu = "^[ ]+$"; 
var re = new RegExp(regu); 
return re.test(str); 
} 

var BlockDiv = function() {
	this.show = function(text) {
		var blockDiv = document.getElementById("divBlock");

		if (blockDiv) {
			blockDiv.style.display = "";
		} else {
			var div = document.createElement("div");
			document.body.appendChild(div);
			div.id = "divBlock";
			div.style.cssText = "position:absolute;width:100%;height:100%; top:0px; left:0px; z-index:1; padding:0px; margin:0px; background:#000000;filter:alpha(opacity=30); text-align:center; ";
		}
		
		if(text != "") {
			div.innerHTML = "<span style='margin-top:200px;'><img src='" + MATECH_SYSTEM_WEB_ROOT + "/img/loading.gif'>&nbsp;<font color='#ffffff'><strong>" + text + "</strong><font></span>";
		} else {
			div.innerHTML = "";
		}
	};

	this.hidden = function() { 
		var blockDiv = document.getElementById("divBlock");
		if (blockDiv) {
			try {
				blockDiv.style.display = "none";
				document.body.removeChild(blockDiv);
			}catch(e){}
		}
	};
	
};

// strUrl:Òª·ÃÎÊµÄÍøÒ³µÄ¾ø¶ÔÂ·¾¶£¬µ«²»´øhttp://127.0.0.1:5199£»µ«×Ô¼ºÒª´ø/¿ªÍ·£¡£¡£¡
function myOpenUrl(strUrl){

	try{
	
		// »ñµÃµ±Ç°´°¿ÚµÄµØÖ·ºÍ¶Ë¿ÚÏÈ
		// alert("http:\/\/"+window.location.host +strUrl);
		var t,t1;
		
		// ÕÒµ½Ö÷²Ù×÷½çÃæ
		t = window.opener;
		
		if(!t)// Èç¹ûÊÇ¿ò¼ÜÒ³ÔòÕÒËüµÄ¸¸´°¿Ú
			t = window.parent;
		if (t){
			// alert('±»ĞÂ¿ª´°¿Ú');
			t1 = t.window.opener;
			
			if(!t1)// Èç¹ûÊÇ¿ò¼ÜÒ³ÔòÕÒËüµÄ¸¸´°¿Ú
				t1 = t.window.parent;	
			while (t1){
				t = t1;
				t1 = t.window.opener;
				// alert(t1)
				if(!t1){// Èç¹ûÊÇ¿ò¼ÜÒ³ÔòÕÒËüµÄ¸¸´°¿Ú
					t1 = t.window.parent;
					if(t1.bottomFrame){
						break;
					}
				}	
			}
		}else{
			// alert('Ã»ÓĞ±»ĞÂ¿ª´°¿Ú');
			t = window;	
		}
		
		// ÔÚÖ÷²Ù×÷½çÃæÖĞÕÒµ½×îÉÏÃæµÄÄÇ¸öWINDOW
		t1 = t.parent;
		while (t1 && t1 != t){
			t = t1;
			t1 = t.window.parent;
		}
		
		// ÕÒµ½×îºóµÄÄÇ¸öURL
		if (t){
			t.bottomFrame.myOpenUrl("http:\/\/"+window.location.host + strUrl);
			// t.open(strUrl);
		}
	}catch(e){
		window.open(strUrl);
	}	
	// oframe.OpenURLEx('http://127.0.0.1:5199/AuditSystem/taskCommon.do?method=fileOpen&UNID=239950844228867565&isBack=no&random=0.26142378553784257');
	// parent.bottomFrame.statu.value="exitSystem";
}

function myOpenUrlByWindowOpen(url, target, param) {

	var targetTemp = "_blank";
	var paramTemp = "channelmode=1, resizable=yes,toolbar=no,menubar=no,titlebar=no,scrollbars=yes";

	if (target != "") {
		targetTemp = target;
	}

	if (param != "") {
		paramTemp = param;
	}
	window.open(url, targetTemp, paramTemp);
}

function showWaiting(hight,wight,msg){
  var ShowDialog=1;
	if(msg==null||msg=="") {
		msg = "´¦ÀíÖĞ£¬ÇëÉÔµÈ¡­¡­";
		ShowDialog=0;
	}
  var obj=document.getElementById("waiting");
  if(!obj){
    var oBody = document.body;
  	oBody.insertAdjacentHTML("beforeEnd", "<div id='waiting' onselectstart='return false' ></div>");
    obj=document.getElementById("waiting");
  }

  if(hight==null||hight==""){
    hight="100%";
  }
  if(wight==null||wight==""){
    wight="100%";
  }
  
   var strTalk="";
  if (ShowDialog==0){
  	strTalk="<div id=bxDlg_bg1 oncontextmenu='return false' onselectstart='return false' style=\"position:absolute; width:100%;height:100%; top:expression(this.offsetParent.scrollTop); z-index:9999; padding:10px; background:#ffffff;filter:alpha(opacity=50); text-align:center;\"> </div>"
  			+ "<div style=\"position:absolute;width:230px;height:60px; z-index:2;left:expression((document.body.clientWidth-200)/2);top:expression(this.offsetParent.scrollTop + 130); border:1px solid #666666; padding:20px 40px 20px 40px; background:#E4E4E4; \"> "
    		// + " <img src='/AuditSystem/images/indicator.gif' />"
    		+ "<img src=\"" + MATECH_SYSTEM_WEB_ROOT + "/images/loading.gif\">"
    		+ msg + "</div>";
  }else{
	  strTalk="<span id=bxDlg_bg align=center oncontextmenu='return false'"
	    +" onselectstart='return false' style='width:"+wight+";height:"+hight+";position:absolute;left:0;top:0'>"
	    +"<div id=bxDlg_bg1 style=height:100%;background:white;filter:alpha(opacity=50)> </div></span>"
	    +"<span  style='background:#E4E4E4;POSITION:absolute;padding:20px 40px 20px 40px;left:150.5;top:164.5;"
	    +" width:400px; height:200px;  border:1px solid #666666;'>"
	    + msg + "</span>";
  }
  obj.innerHTML=strTalk;
  obj.style.display = "" ;
}

function stopWaiting(){
	var obj =  document.getElementById("waiting") ;
	if(obj) {
	    obj.innerHTML="";
	    obj.style.display = "none" ;
    }
}
// -----------------------------------
// °Ñ±íµ¥ÄÚµÄinputÆ´³Éurl×Ö·û´®·µ»Ø
// -----------------------------------
function formToRequestString(form_obj) {
	var query_string='';
	var and='';
	// alert(form_obj.length);
	for (var i=0;i<form_obj.length ;i++ ) {
		e=form_obj[i];
		if ((e.tagName=='INPUT' || e.tagName=='SELECT' || e.tagName=='TEXTAREA') && e.name!='') {
			if (e.type=='select-one') {
				element_value=e.options[e.selectedIndex].value;
			} else if (e.type=='checkbox' || e.type=='radio') {
				if (e.checked==false) {
					// break;
					continue;
				}
				element_value=e.value;
			} else {
				element_value=e.value;
			}
			query_string+=and+e.name+'='+element_value.replace(/\&/g,"%26");
			and="&";
		}

	}
	return query_string;
}

// Òì²½
function ajaxLoadPage(url,request,container) {
	var loading_msg='ÕıÔÚ¼ÓÔØÊı¾İ,ÇëÉÔºò...';
	var loader;

	try {
		loader = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (e) {
		try {
			loader = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (e2) {
			loader = false;
		}
	}

	loader.open("POST",url,true);
	loader.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	loader.onreadystatechange = function(){
		if (loader.readyState==1) {
			container.innerHTML=loading_msg;
			try {
				showWaiting("100%","100%");
			} catch(e) {

			}
		}

		if (loader.readyState==4) {
			container.innerHTML=loader.responseText;
			try {
				stopWaiting();
			} catch(e) {

			}
		}
	};

	loader.send(request);
}

//Í¬²½
function ajaxLoadPageSynch(url,request) {

	var loader;

	try {
		loader = new ActiveXObject("Msxml2.XMLHTTP");
	} catch (e) {
		try {
			loader = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (e2) {
			loader = false;
		}
	}

	loader.open("POST",url,false);
	loader.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	loader.send(request);

	return unescape(loader.responseText);
}


// Ò³ÃæÏÔÊ¾½ø¶È
var timer;   
function initMessage(key,time) {
	// ´´½¨Ò»¸öÏÔÊ¾ÏûÏ¢µÄµÈ´ı¿ò
	var msgBar = document.createElement("DIV") ;
	msgBar.className = "" ;
	msgBar.id = "msgBarDiv" ;
	msgBar.innerHTML = "<div class=\"msg_background_div\" id=\"bgDiv\"></div><div class=\"msg_info_div\" id=\"msg_info_div\"><div class=\"msg_center_div\" id=\"msg_center_div\"><strong>ÌáÊ¾£º</strong><p>ÇëµÈ´ı...</p></div></div>" ;
	document.body.appendChild(msgBar) ;
	timer = window.setTimeout("startMessageListener('"+key+"','"+time+"')",time); 
}   

var oXmlhttp;   
function startMessageListener(key,time){
	
	if(!oXmlhttp) { 
	    try{   
	        oXmlhttp = new ActiveXObject('Msxm12.XMLHTTP');   
	    }catch(e){   
	        try{   
	            oXmlhttp = new ActiveXObject('Microsoft.XMLHTTP');   
	        }catch(e){   
	            try{   
	                oXmlhttp = new XMLHttpRequest();   
	            }catch(e){}   
	        }   
	    } 
	}
	
    oXmlhttp.open("post",MATECH_SYSTEM_WEB_ROOT + "frontProcess.do?method=getMessage&key="+key+"&random="+Math.random(),true);   
     oXmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded"); 
     oXmlhttp.onreadystatechange = function(){
        if(oXmlhttp.readyState == 4){   
            if(oXmlhttp.status == 200){
            var msgCenter = document.getElementById("msg_center_div") ;
            var temp = oXmlhttp.responseText.indexOf("end");
            if (  temp > -1 ){
       			var msgBarDiv = document.getElementById("msgBarDiv");
       			if(msgBarDiv) {
       				msgBarDiv.style.display = "none" ;
       			}    			   
            	window.clearTimeout(timer);   
            }else{
            	 msgCenter.innerHTML = ""; 
           		 msgCenter.innerHTML = "<strong>ÌáÊ¾£º</strong><p>"+oXmlhttp.responseText+"</p>";
            	timer = window.setTimeout("startMessageListener('"+key+"')",time);   
            }   
            }   
        }   
    }
    oXmlhttp.send(null);   
}

// ±íµ¥Ìá½»postµ½ÁíÍâÒ»¸öĞÂµÄ±êÇ©Ò³
function tabSubmit(form,url,tabTitle) {
	var randStr = Math.random();
      		
	var newTab = mainTab.add({    
		title:tabTitle,    
		closable:true,  // Í¨¹ıhtmlÔØÈëÄ¿±êÒ³
		html:'<iframe name="newTab_' + randStr + '" scrolling="auto" frameborder="0" width="100%" height="100%" src=""></iframe>'   
	}); 
	
	mainTab.setActiveTab(newTab);
	
	form.action = url;
	form.target = "newTab_" + randStr;
	form.submit();
}


// Ìá½»±íµ¥Ç°µÄÍ¨ÓÃ¼ì²é
function formSubmitCheck(formid){
	var vd = new Ext.matech.form.Validation({formId:formid,tipType:"advice"});
	return vd.validate() ; 
}

// ÔÚTABÀïÃæ´ò¿ªĞÂÒ³Ãæ
function openTab(id,title,url,parent) {
	var n = parent.mainTab.getComponent(id);    
	if (!n) { // ÅĞ¶ÏÊÇ·ñÒÑ¾­´ò¿ª¸ÃÃæ°å
		n = parent.mainTab.add({    
			 id:id,    
			 title:title,  
			 closable:true,  // Í¨¹ıhtmlÔØÈëÄ¿±êÒ³
			 html:'<iframe name="projectFrame" id="projectFrame" scrolling="yes" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>'   
		});    
	} 	
	
	parent.mainTab.setActiveTab(n);
}

// ¼ì²éÄ¿Â¼ÃûºÏ·¨ĞÔ
function checkFileName(strFile){
	var reg= new RegExp("/^[^/\\:\*\?,\",<>\|]+$/ig"); 

	if(!reg.test(strFile)){
		return " ";
	}else{
		return strFile;
	}
}

// ¹Ø±ÕÈÃÇ°Ì¨ÉèÖÃ
function notifyManuClose(contextPath,taskId,curProjectId){
	try {
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("POST",contextPath+"/taskCommon.do?method=fileClose&taskId="+taskId+"&projectId="+curProjectId, true);
		oBao.send();

	} catch(e) {
		//
	}
}

// ¹Ø±Õ±êÇ©Ò³µÄ·½·¨
var closeTab = function(tab) {
	if(tab && tab.id == "mainFrameTab") {
		tab.remove(tab.getActiveTab()); 
	}else {
		window.close();
	}
}

function openFullWindow(url,target,oldUrl, localUrl) {
	var x = window.open(url,target,'top=0,left=0,width=' + (window.screen.availWidth-8) + ',height=' + (window.screen.availHeight-50) + ',resizable=no,menubar=no,toolbar=no,scrollbars=yes,status=no,location=no');
	try {
		if(!x) {
			alert('¶Ô²»Æğ,ÏµÍ³µÄµ¯³ö´°¿Ú¸øÄúµÄä¯ÀÀÆ÷×èÖ¹ÁË\nÇë¡¾¹Ø±Õµ¯´°¿Ú×èÖ¹³ÌĞò¡¿»ò¡¾µã»÷¡¿ä¯ÀÀÆ÷ÉÏ·½»ÆÉ«ÌáÊ¾¿ò,Ñ¡Ôñ£º×ÜÊÇÔÊĞíÀ´×Ô´ËÕ¾µãµÄµ¯³ö´°¿Ú'); 
			if(oldUrl || oldUrl != '') {
				window.location = oldUrl;
			}
			
		} else {
			window.location = localUrl;
		}
	} catch(e) {
		// alert(e);
	}
}

// ¼ì²éÃû³ÆÊÇ·ñÎ¨Ò»
function checkQueryResultName(menuId, queryResultName) {
	var url = MATECH_SYSTEM_WEB_ROOT + "/common.do?method=checkQueryResultName";
	var requestString = "menuId=" + menuId
					  + "&queryResultName=" + queryResultName;
					 
	var result = ajaxLoadPageSynch(url, requestString);
	// alert(requestString + "," + result);
	
	if(result == "false") {
		alert("¸ÃÃû³ÆÒÑ¾­´æÔÚ£¬ÇëÖØĞÂÊäÈë!!");
		return false;
	} else {
		return true;
	}
}

var queryResultSaveWin = null;
// ±£´æ²éÑ¯½á¹û
function saveQueryResult(menuId, gridId, formId) {
	var html = "<div style=\"padding:5px;\">"
			 + "²éÑ¯½á¹ûÃû³Æ£º<span class=\"mustSpan\">[*]</span>"
			 + " <input type=\"text\" name=\"queryResultName\" id=\"queryResultName\" class=\"required\" />"
			 + "</div> ";
	
	if(queryResultSaveWin == null) { 
	    queryResultSaveWin = new Ext.Window({
			title: '±£´æ²éÑ¯½á¹û',
			width: 300,
			height:100, 
			html:html,
	        closeAction:'hide',
	        modal:true,
	        layout:'fit',
		    buttons:[{
	            text:'È·¶¨',
	            icon:MATECH_SYSTEM_WEB_ROOT + '/img/confirm.gif',
	          	handler:function() {
	          		var queryResultName = document.getElementById("queryResultName").value;
	          		
	          		// ¼ì²éÃû³ÆÊÇ·ñÎ¨Ò»
	          		if(!checkQueryResultName(menuId, queryResultName)) {
	          			return;
	          		}
	          	
	          		if(queryResultName == "") {
	          			alert("ÇëÊäÈë²éÑ¯½á¹ûµÄÃû³Æ");
	          			return;
	          		} else {
	          			
	          			
	          			var url = MATECH_SYSTEM_WEB_ROOT + "/common.do?method=saveQueryResult";
						var requestString = "menuId=" + menuId
										  + "&gridId=" + gridId
										  + "&formId=" + formId
										  + "&queryResultName=" + queryResultName;
										 
						var result = ajaxLoadPageSynch(url, requestString);
						
						if(result == "ok") {
							// alert("±£´æ²éÑ¯½á¹û³É¹¦!!");
						} 
						
						queryResultSaveWin.hide();
	          		}
	          	}
	        },{
	            text:'È¡Ïû',
	            icon:MATECH_SYSTEM_WEB_ROOT + '/img/close.gif',
	            handler:function(){
	            	queryResultSaveWin.hide();
	            }
	        }]
	    });
	}
	queryResultSaveWin.show();
	
}

// »ñÈ¡²éÑ¯½á¹û
function getQueryResult(paramString) {
	var params = paramString.split(",");
	for(var i=0; i < params.length; i++) {
		var keyValue = params[i].split("=");
		
		if(keyValue[1]) {
			document.getElementById(keyValue[0]).value = keyValue[1];
		}
	}
}

// -----------------------------------
// ÖØÖÃ±êÇ©ÀïÃæµÄËùÓĞÎÄ±¾¿ò¡¢¸´Ñ¡¿ò¡¢µ¥Ñ¡¿òµÈ
// -----------------------------------
function reset(objId) {
	var obj = document.getElementById(objId);
	
	for (i = 0; i < obj.length; i++ ) {
		e = obj[i];
		if ((e.tagName=='INPUT' || e.tagName=='SELECT' || e.tagName=='TEXTAREA') && e.name!='') {
		
			if (e.type=='text') {
				e.value = "";
			}else if (e.type=='select-one') {
				e.value = "";
			} else if (e.type=='checkbox' || e.type=='radio') {
				e.checked = false;
			} else {
				try{
					Ext.getCmp(e.id).clear();
				} catch(e) {
					e.value = "";
				}
			}
		}
	}
}

// ³õÊ¼»¯Í¼ĞÎ
function createChart(url, chartType , chartId, height, width) {
	var chartDiv = document.getElementById("chartDiv_" + chartId);
	var chartXML = document.getElementById("chartXML_" + chartId);
	var chartURL = document.getElementById("chartURL_" + chartId);
	var chartTypeObj = document.getElementById("chartType_" + chartId);

	if(!chartDiv) {
		document.write("<div id=\"chartDiv_" + chartId + "\" align=\"center\"></div>");
		chartDiv = document.getElementById("chartDiv_" + chartId);
	}
	
	if(!chartXML) {
		document.write("<input type=\"hidden\" id=\"chartXML_" + chartId + "\" value=\"\"> ");
		chartXML = document.getElementById("chartXML_" + chartId);
	}
	
	if(!chartURL) {
		document.write("<input type=\"hidden\" id=\"chartURL_" + chartId + "\" value=\"\"> ");
		chartURL = document.getElementById("chartURL_" + chartId);
	}
	
	if(!chartTypeObj) {
		document.write("<input type=\"hidden\" id=\"chartType_" + chartId + "\" value=\"\"> ");
		chartTypeObj = document.getElementById("chartType_" + chartId);
	}
	
	var request = "&chartType=" + chartType;
	strXML = ajaxLoadPageSynch(url, request);
	
	chartURL.value = url;
	chartXML.value = strXML;
   	chartTypeObj.value = chartType;
    
    changeChart(chartType, chartId, height, width);

} 

// ¸Ä±äÍ¼ĞÎÀàĞÍ
function changeChart(chartType, chartId, height, width) {
	var chartXML = document.getElementById("chartXML_" + chartId).value;
	var height = height || (document.body.clientHeight-54)/2;
	var width = width || document.body.clientWidth;
	var chart = new FusionCharts(MATECH_SYSTEM_WEB_ROOT + "/charts/" + chartType + ".swf", chartId, width, height);
    chart.addParam("wmode","Opaque");
    chart.setDataXML(chartXML);
    chart.render("chartDiv_" + chartId);
}

// ¸üĞÂÍ¼ĞÎÊı¾İ
function updateChart(url, param, chartId){
	var chartType = document.getElementById("chartType_" + chartId);
	var request = "&chartType=" + chartType + param;
	strXML = ajaxLoadPageSynch(url, request);
	document.getElementById("chartXML_" + chartId).value = strXML;

	updateChartXML(chartId, strXML); 
}

function getUUID() {
	var url = MATECH_SYSTEM_WEB_ROOT + "/common.do?method=getUUID";
	var result = ajaxLoadPageSynch(url, null);
	
	if(result == "") {
		result = Math.random();
	}
	
	return result;
}

var attachUploadWin = null;
var attachUploadForm = null;

// ÉÏ´«¸½¼ş
function attachUpload(inputId,mode,imgId) {
	var inputObj = document.getElementById(inputId);
	
	var indexTable = inputObj.indexTable;
	var indexId = inputObj.value;
	mode=mode||"";
	
	if(!checkMaxAttach(inputId)) {
		return;
	}
	
	if(attachUploadForm == null) {
		attachUploadForm = new Ext.FormPanel({
			url: "",
			border:false,
	        fileUpload: true,
	        autoHeight: true,
	        autoWidth: true,
	        frame: true,
			bodyStyle: 'padding: 5px;',
	        labelWidth: 1,
	        defaults: {
	            anchor: '95%',
	            allowBlank: false,
	            msgTarget: 'side'
	        },
	        items: [{
	            xtype: 'fileuploadfield',
	            id: 'form-file',
	            emptyText: 'ÇëÑ¡ÔñĞèÒªÉÏ´«µÄÎÄ¼ş',
	            name: 'attachPath',
	            buttonText: '',
	            buttonCfg: {
	            	text:'Ñ¡ÔñÎÄ¼ş'
	            }
	        }]
	    });
	} else {
		attachUploadForm.getForm().reset();
	}
	
	//Ã¿´ÎÖØÖÃ±íµ¥urlµØÖ·
	attachUploadForm.form.url = MATECH_SYSTEM_WEB_ROOT + '/common.do?method=attachUpload&indexTable=' + indexTable + "&indexId=" + indexId+"&mode="+mode;
	//¸ÄÎªÃ¿´Î´´½¨ĞÂ´°¿Ú
    attachUploadWin = new Ext.Window({
		title: 'ÎÄ¼şÉÏ´«',
		width: 500,
		height:116,
        modal:true,
        resizable:false,
		layout:'fit',
		closeAction:'hide',
		items: attachUploadForm,
		buttons: [{
            text: 'È·¶¨',
            icon:MATECH_SYSTEM_WEB_ROOT + '/img/confirm.gif',
            handler: function(){
                if(attachUploadForm.getForm().isValid()){
                	// ÏÔÊ¾½ø¶ÈÌõ
                	Ext.MessageBox.show({ 
					    title: 'ÉÏ´«ÎÄ¼ş', 
					    width:240, 
					    progress:true, 
					    closable:false
					}); 
				
					// Ìá½»±íµ¥
	                attachUploadForm.getForm().submit();
	                
	                var i = 0;
				    var timer = setInterval(function(){
						// ÇëÇóÊÂÀı
						Ext.Ajax.request({
							url: MATECH_SYSTEM_WEB_ROOT + '/common.do?method=attachUploadProcess&rand=' + Math.random(),
							method: 'post',
							// ´¦ÀíajaxµÄ·µ»ØÊı¾İ
							success: function(response, options){
								status = response.responseText + " " + i++;
								var obj = Ext.util.JSON.decode(response.responseText);
								if(obj.success!=false){
									if(obj.finished){
										clearInterval(timer);	
										// status = response.responseText;
										Ext.MessageBox.updateProgress(1, 'finished', 'finished');
										Ext.MessageBox.hide();
										attachUploadWin.hide();
										if(imgId){
										attachImageInit(inputId,imgId);	
										}else{
										attachInit(inputId);
										}
									} else {
										Ext.MessageBox.updateProgress(obj.percentage, obj.msg);	
									}
								}
							},
							failure: function(){
								clearInterval(timer);
								Ext.Msg.alert('´íÎó', 'ÉÏ´«ÎÄ¼ş³ö´í¡£');
							} 
						});
				    }, 500);
                }
            }
        },{
            text: 'ÖØÖÃ',
            icon:MATECH_SYSTEM_WEB_ROOT + '/img/refresh.gif',
            handler: function(){
                attachUploadForm.getForm().reset();
            }
       	},{
       		text: 'È¡Ïû',
       		icon:MATECH_SYSTEM_WEB_ROOT + '/img/close.gif',
       		handler: function(){
       			attachUploadWin.hide();
       		}
       	}]
    });
	attachUploadWin.show();
}

// ¸ñÊ½»¯Êı×Ö
function formatDecimal(x,maxLength) {
   var f_x = parseFloat(x);
   if (isNaN(f_x)) {
      return x;
   }
   var f_x = Math.round(x*100)/100;
   var s_x = f_x.toString();
   var pos_decimal = s_x.indexOf('.');
   if (pos_decimal < 0) {
      pos_decimal = s_x.length;
      s_x += '.';
   }
   while (s_x.length <= pos_decimal + maxLength) {
      s_x += '0';
   }
   return s_x;
}

// ½ØÈ¡×Ö·û×î´ó³¤¶È
function maxString(str) {
	if(str.length > 25) {
		str = str.substring(0,22) + "...";
	} 
	
	return str;
}

// ¼ì²é×î´ó¸½¼şÊı
function checkMaxAttach(inputId) {
	var inputObj = document.getElementById(inputId);
	var maxAttach = inputObj.maxAttach || 0;
	
	if(maxAttach != 0 && getAttachCount(inputId) >= maxAttach) {
		alert("¶Ô²»Æğ£¬Ö»ÔÊĞíÉÏ´«" + maxAttach + "¸öÎÄ¼ş,ÇëÏÈÉ¾³ıºóÔÙÉÏ´«!");
		return false;
	} else {
		return true;
	}
}

// »ñÈ¡¸½¼şÊıÁ¿
function getAttachCount(inputId) {
	var inputObj = document.getElementById(inputId);
	var prefix = inputObj.id;
	var attachUlId = "attachUl_" + prefix; 
	
	return document.getElementById(attachUlId).children.length;
}

function attachImageInit(inputId,imgId) {
	var inputObj = document.getElementById(inputId);
	//alert(imgId);
	// °´Å¥ÎÄ×Ö,Ä¬ÈÏÎªÌí¼Ó¸½¼ş
	var buttonText = inputObj.buttonText || "Ìí¼ÓÍ¼Æ¬";
	
	var showButton = true;
	var remove = true;
	
	if(inputObj.readOnly) {
		showButton = false;
		remove = false;
	}
	if(inputObj["attachFile"]=="true"){
		showButton = true;
		remove = true;
	}
	
	
	//²»ÔÙµ¥¶À¿ØÖÆ£¬Í¨¹ıÖ»¶ÁÀ´ÉèÖÃ
	//
	// ÊÇ·ñÏÔÊ¾ÉÏ´«°´Å¥,Ä¬ÈÏÎªtrue
	//var showButton = inputObj.showButton == false ? false : true;
	// ÊÇ·ñÔÊĞíÉ¾³ı,Ä¬ÈÏÎªtrue
	//var remove = inputObj.remove == false ? false : true;
	
	var indexTable = inputObj.indexTable||inputObj.indextable;
	
	if(inputObj.value == "") {
		inputObj.value = getUUID();
	}
	
	var indexId = inputObj.value;
	var prefix = inputObj.id;
	
	var url = MATECH_SYSTEM_WEB_ROOT + "/common.do?method=getAttachList";
	var request = "indexTable=" + indexTable + "&indexId=" + indexId;
	
	var result = ajaxLoadPageSynch(url, request);
	//alert(result);
	var attachList = Ext.util.JSON.decode(result);
	
	var html = "";
	if(attachList.length>0){
		var src=MATECH_SYSTEM_WEB_ROOT + "/common.do?method=attachDownload&attachId=" + attachList[0].attachId;
		//alert(src);
		document.getElementById(imgId).src=src;
	}
	/*
	for(var i=0; i < attachList.length; i++) {
		var attach = attachList[i];
		html += "<li>"
			  + "<span>"
			  + "<a href=\"" + MATECH_SYSTEM_WEB_ROOT + "/common.do?method=attachDownload&attachId=" + attach.attachId + "\" title=\"ÏÂÔØ£º" + attach.attachName + "\">" + maxString(attach.attachName) + "</a>"
			  + "&nbsp;<font style=\"color:#CCCCCC;\">" + formatDecimal((attach.fileSize/1024),2) + " KB</font>"
			  + "</span>"
			  + "&nbsp;<a href=\"" + MATECH_SYSTEM_WEB_ROOT + "/common.do?method=attachDownload&attachId=" + attach.attachId + "\" title=\"ÏÂÔØ£º" + attach.attachName + "\"><img src=\"" + MATECH_SYSTEM_WEB_ROOT + "/img/download.gif\"></a>";
		
		if(remove) {
			html += "&nbsp;<a href=\"#\" onclick=\"attachRemove('" + attach.attachId + "','" + inputId + "');\" title=\"É¾³ı\"><img src=\"" + MATECH_SYSTEM_WEB_ROOT + "/img/delete.gif\"></a>";
		}
		
		html += "</li>";
	}*/
	//alert(11);
	var attachUlId = "attachUl_" + prefix; 
	var attachButtonId = "attachButton_" + prefix;
	var attachDivId = "attachDiv_" + prefix;
	
	var ul = document.getElementById(attachUlId);
	if(ul == null || !ul) {
		
		var divObj = document.createElement("<div id=\"" + attachDivId + "\"></div>");
					
		divObj = inputObj.parentElement.insertBefore(divObj);
		
		var buttonDiv = document.createElement("<div id=\"" + attachButtonId +"\"></div>");
		ul = document.createElement("<ul id=\"" + attachUlId + "\"></ul>");
		
		divObj.appendChild(buttonDiv);
		divObj.appendChild(ul);
	}

	ul.innerHTML = html;
	
	// ÊÇ·ñÏÔÊ¾°´Å¥
	if(showButton) {
		var attachButton = document.getElementById(attachButtonId);
		attachButton.innerHTML = "<input type=\"button\" class=\"flyBT\" value=\"" + buttonText + "\" onclick=\"attachUpload('" + inputId + "','single','"+imgId+"')\" ><br/><br/>";
	}
}


if ((typeof Range !== "undefined") && !Range.prototype.createContextualFragment)
{
     Range.prototype.createContextualFragment = function(html)
     {
         var frag = document.createDocumentFragment(),  
         div = document.createElement("div");
         frag.appendChild(div);
         div.outerHTML = html;
         return frag;
     };
}

function mycreateElement(html,objtype,id){
	try{  
		return document.createElement(html);
	}catch(e){
		//ie9ÒÔÉÏ°æ±¾
		var new_name_item = document.createElement(objtype);  
        new_name_item.id = id;  
		return new_name_item;
	}
	
	
}


function attachInit(inputId) {
	var inputObj = document.getElementById(inputId);
	
	// °´Å¥ÎÄ×Ö,Ä¬ÈÏÎªÌí¼Ó¸½¼ş
	var buttonText = inputObj.buttonText || "Ìí¼Ó¸½¼ş";
	
	var showButton = true;
	var remove = true;
	
	if(inputObj.readOnly) {
		showButton = false;
		remove = false;
	}
	if(inputObj["attachFile"]=="true"){
		showButton = true;
		remove = true;
	}
	
	
	//²»ÔÙµ¥¶À¿ØÖÆ£¬Í¨¹ıÖ»¶ÁÀ´ÉèÖÃ
	//
	// ÊÇ·ñÏÔÊ¾ÉÏ´«°´Å¥,Ä¬ÈÏÎªtrue
	//var showButton = inputObj.showButton == false ? false : true;
	// ÊÇ·ñÔÊĞíÉ¾³ı,Ä¬ÈÏÎªtrue
	//var remove = inputObj.remove == false ? false : true;
	
	var indexTable = inputObj.indexTable||inputObj.indextable;
	
	if(inputObj.value == "") {
		inputObj.value = getUUID();
	}
	
	var indexId = inputObj.value;
	var prefix = inputObj.id;
	
	var url = MATECH_SYSTEM_WEB_ROOT + "/common.do?method=getAttachList";
	var request = "indexTable=" + indexTable + "&indexId=" + indexId;
	
	var result = ajaxLoadPageSynch(url, request);
	var attachList = Ext.util.JSON.decode(result);
	
	var html = "";
	for(var i=0; i < attachList.length; i++) {
		var attach = attachList[i];
		html += "<li>"
			  + "<span>"
			  + "<a class='aAtt' href=\"" + MATECH_SYSTEM_WEB_ROOT + "/common.do?method=attachDownload&attachId=" + attach.attachId + "\" title=\"ÏÂÔØ£º" + attach.attachName + "\">" + maxString(attach.attachName) + "</a>"
			  + "&nbsp;<font style=\"color:#CCCCCC;\">" + formatDecimal((attach.fileSize/1024),2) + " KB</font>"
			  + "</span>"
			  + "&nbsp;<a href=\"" + MATECH_SYSTEM_WEB_ROOT + "/common.do?method=attachDownload&attachId=" + attach.attachId + "\" title=\"ÏÂÔØ£º" + attach.attachName + "\"><img src=\"" + MATECH_SYSTEM_WEB_ROOT + "/img/download.gif\"></a>";
		
		if(remove) {
			html += "&nbsp;<a href=\"#\" onclick=\"attachRemove('" + attach.attachId + "','" + inputId + "');\" title=\"É¾³ı\"><img src=\"" + MATECH_SYSTEM_WEB_ROOT + "/img/delete.gif\"></a>";
		}
		
		html += "</li>";
	}
	//alert(11);
	var attachUlId = "attachUl_" + prefix; 
	var attachButtonId = "attachButton_" + prefix;
	var attachDivId = "attachDiv_" + prefix;
	
	var ul = document.getElementById(attachUlId);
	if(ul == null || !ul) {
		
		var divObj = mycreateElement("<div id=\"" + attachDivId + "\"></div>","div",attachDivId);
					
		divObj = inputObj.parentElement.insertBefore(divObj);
		
		var buttonDiv = mycreateElement("<div id=\"" + attachButtonId +"\"></div>","div",attachButtonId);
		ul = mycreateElement("<ul id=\"" + attachUlId + "\"></ul>","ul",attachUlId);
		
		divObj.appendChild(buttonDiv);
		divObj.appendChild(ul);
	}

	ul.innerHTML = html;
	
	// ÊÇ·ñÏÔÊ¾°´Å¥
	if(showButton) {
		var attachButton = document.getElementById(attachButtonId);
		//alert(inputObj.readOnly);
		if(inputObj.readOnly==true){
			attachButton.innerHTML = "<input type=\"button\"  class=\"flyBT\" value=\"" + buttonText + "\" onclick=\"attachUpload('" + inputId + "')\" disabled=\"disabled\" /><br/><br/>";
		}else{
			attachButton.innerHTML = "<input type=\"button\" class=\"flyBT\" value=\"" + buttonText + "\" onclick=\"attachUpload('" + inputId + "')\"  /><br/><br/>";
		}
	}
}

// É¾³ı¸½¼ş
function attachRemove(attachId, inputId) {
	var url = MATECH_SYSTEM_WEB_ROOT + "/common.do?method=attachRemove";
	var request = "attachId=" + attachId;
	var result = ajaxLoadPageSynch(url, request);
	
	if(result == "success") {
		attachInit(inputId);
	}
}

// ¼ì²é¿ªÊ¼Äê·İºÍ½áÊøÄê·İ startYear endYear

function chkYear(){
	var startYear = document.getElementById("startYear").value;
	var endYear = document.getElementById("endYear").value;

	if(startYear != "" && endYear ==""){
		alert("ÇëÍ¬Ê±Ñ¡Ôñ½áÊøÄê·İ£¡");
		document.getElementById("endYear").foucs();
		return false;
	}
	if(startYear == "" && endYear !=""){
		alert("ÇëÍ¬Ê±Ñ¡Ôñ¿ªÊ¼Äê·İ£¡");
		document.getElementById("startYear").foucs();
		return false;
	}
	if(startYear != "" && endYear !=""){
		if(endYear <= startYear){
			alert("½áÊøÄê·İ±ØĞë´óÓÚ¿ªÊ¼Äê·İ£¡");
			return false;
		}
	}
	return true;
}


// ´´½¨ajax ÇëÇó
function createRequest() {
	var request;
	  try {
	    request = new XMLHttpRequest();
	  } catch (trymicrosoft) {
	    try {
	      request = new ActiveXObject("Msxml2.XMLHTTP");
	    } catch (othermicrosoft) {
	      try {
	        request = new ActiveXObject("Microsoft.XMLHTTP");
	      } catch (failed) {
	        request = false;
	      }
	    }
	  }
	  if (!request)
	    alert("Error initializing XMLHttpRequest!");
	  
	  return request;
}



//µ÷Õûgird¿òÎŞ·¨ÊÊÓ¦ä¯ÀÀÆ÷resize
//Õë¶Ô½«grid¿ò·Åµ½TabPanelÖĞµÄÇé¿ö
//_fromObj:grid¿òËùÊôÈİÆ÷£¬¸ÃÈİÆ÷»áËæ×Åä¯ÀÀÆ÷±ä¶¯¶ø×Ô¶¯µ÷Õû¸ß¶ÈÓë³¤¶È
//_toObj:ÎªĞèÒª¸ù¾İ_fromObj½øĞĞÊÖ¹¤µ÷ÕûµÄgirdµÄID×Ö·û´®£¬Èç£ºgridId_myDealList,gridId_myApplyList
//_adjSize[³¤¶È£¬¸ß¶È]:ĞèÒª¼õÈ¥µÄ³¤¶ÈÓë¸ß¶È£¬Î¢µ÷Ê¹ÓÃ
//Ìí¼ÓÈÕÆÚ£º2012-3-16
function resizeGridPanel(_fromObj,_toObj,_adjSize){
	var _resizeInterval;//¼ÆÊ±Æ÷
	//¼àÌıä¯ÀÀÆ÷±ä¶¯
	Ext.EventManager.onWindowResize (function(){
		_resizeInterval=setInterval(GridPanelResize,500);
	});
	//µ÷ÕûÒ³Ãægridpanel³¤¶ÈºÍ¿í¶È
	function GridPanelResize(){
		var realWidth=Ext.getCmp(_fromObj).getWidth();
		var realHeight=Ext.getCmp(_fromObj).getHeight();
		var gridPanels=_toObj.split(",");
		for(var i=0;i<gridPanels.length;i++){			
			Ext.getCmp(gridPanels[i]).setWidth(realWidth-_adjSize[i][0]);
			Ext.getCmp(gridPanels[i]).setHeight(realHeight-_adjSize[i][1]);
		}
		clearInterval(_resizeInterval);
	}	
}
//Õë¶Ôµ¥¸ögrid·Åµ½Ò³ÃæµÄÇé¿ö
function resizeSingleGridPanel(_toObj,_adjWidth,_adjHeigh){
	var _resizeInterval;//¼ÆÊ±Æ÷
	//¼àÌıä¯ÀÀÆ÷±ä¶¯
	Ext.EventManager.onWindowResize (function(){
		_resizeInterval=setInterval(GridPanelResize,500);
	});
	//µ÷ÕûÒ³Ãægridpanel³¤¶ÈºÍ¿í¶È
	function GridPanelResize(){
		var realWidth=Ext.getBody().getWidth()-_adjWidth;
		var realHeight=Ext.getBody().getHeight()-_adjHeigh;
		
		Ext.getCmp(_toObj).setWidth(realWidth);
		Ext.getCmp(_toObj).setHeight(realHeight);
		
		clearInterval(_resizeInterval);
	}	
}
//Òş²Øgrid¿òµÄË¢ĞÂ°´Å¥
function hideMyExtGridComponent(itemContainer,itemIndex){
	var _hideExtComponentInterval;
	_hideExtComponentInterval=setInterval(hideComponent,500);
	function hideComponent(){
		if(Ext.getCmp(itemContainer)){
			Ext.getCmp(itemContainer).get(itemIndex-1).setVisible(false);
			Ext.getCmp(itemContainer).get(itemIndex).setVisible(false);
			clearInterval(_hideExtComponentInterval);	
		}
	}
}
//È¨ÏŞÅĞ¶Ïº¯Êı
function optPriviligeJudge(curPrivilige,sysPrivilige){
	if(sysPrivilige.indexOf(curPrivilige)>=0){
		return true;
	}else{
		Ext.MessageBox.alert("ÌáÊ¾ĞÅÏ¢","Ã»ÓĞ²Ù×÷È¨ÏŞ£¡");
		return false;
	}
}
//×èÖ¹input°´Å¥ÔÚdisabledºÍreadOnlyÊ±°´backspace·µ»ØÇ°Ò»¸öÒ³Ãæ
Ext.EventManager.on(Ext.getBody(),"keydown",function(e, t) {   
    if (e.getKey() == e.BACKSPACE &&(t.disabled || t.readOnly)) {   
        e.stopEvent();   
    }
});


//ÅĞ¶ÏJSº¯ÊıÊÇ·ñ´æÔÚ
function funExists(funName){ 
	try{  
		if(typeof eval(funName)=="undefined"){
			return false;
		} 
		if(typeof eval(funName)=="function"){
			return true;
		}
	} catch(e){
		return false;
	}
}

//js Ğ¡Ğ´ÈËÃñ±Ò×ª»¯Îª´óĞ´ÈËÃñ±Ò
function RMBToCapital(num) { //×ª³ÉÈËÃñ±Ò´óĞ´½ğ¶îĞÎÊ½
    var str1 = 'ÁãÒ¼·¡ÈşËÁÎéÂ½Æâ°Æ¾Á'; //0-9Ëù¶ÔÓ¦µÄºº×Ö
    var str2 = 'ÍòÇª°ÛÊ°ÒÚÇª°ÛÊ°ÍòÇª°ÛÊ°Ôª½Ç·Ö'; //Êı×ÖÎ»Ëù¶ÔÓ¦µÄºº×Ö
    var str3; //´ÓÔ­numÖµÖĞÈ¡³öµÄÖµ
    var str4; //Êı×ÖµÄ×Ö·û´®ĞÎÊ½
    var str5 = ''; //ÈËÃñ±Ò´óĞ´½ğ¶îĞÎÊ½
    var i; //Ñ­»·±äÁ¿
    var j; //numµÄÖµ³ËÒÔ100µÄ×Ö·û´®³¤¶È
    var ch1; //Êı×ÖµÄººÓï¶Á·¨
    var ch2; //Êı×ÖÎ»µÄºº×Ö¶Á·¨
    var nzero = 0; //ÓÃÀ´¼ÆËãÁ¬ĞøµÄÁãÖµÊÇ¼¸¸ö
    num = Math.abs(num).toFixed(2); //½«numÈ¡¾ø¶ÔÖµ²¢ËÄÉáÎåÈëÈ¡2Î»Ğ¡Êı
    str4 = (num * 100).toFixed(0).toString(); //½«num³Ë100²¢×ª»»³É×Ö·û´®ĞÎÊ½
    j = str4.length; //ÕÒ³ö×î¸ßÎ»
    if (j > 15) {
        return 'Òç³ö';
    }
    str2 = str2.substr(15 - j); //È¡³ö¶ÔÓ¦Î»ÊıµÄstr2µÄÖµ¡£Èç£º200.55,jÎª5ËùÒÔstr2=°ÛÊ°Ôª½Ç·Ö
    //Ñ­»·È¡³öÃ¿Ò»Î»ĞèÒª×ª»»µÄÖµ
    for (i = 0; i < j; i++) {
        str3 = str4.substr(i, 1); //È¡³öĞè×ª»»µÄÄ³Ò»Î»µÄÖµ
        if (i != (j - 3) && i != (j - 7) && i != (j - 11) && i != (j - 15)) { //µ±ËùÈ¡Î»Êı²»ÎªÔª¡¢Íò¡¢ÒÚ¡¢ÍòÒÚÉÏµÄÊı×ÖÊ±
            if (str3 == '0') {
                ch1 = '';
                ch2 = '';
                nzero = nzero + 1;
            }
            else {
                if (str3 != '0' && nzero != 0) {
                    ch1 = 'Áã' + str1.substr(str3 * 1, 1);
                    ch2 = str2.substr(i, 1);
                    nzero = 0;
                }
                else {
                    ch1 = str1.substr(str3 * 1, 1);
                    ch2 = str2.substr(i, 1);
                    nzero = 0;
                }
            }
        }
        else { //¸ÃÎ»ÊÇÍòÒÚ£¬ÒÚ£¬Íò£¬ÔªÎ»µÈ¹Ø¼üÎ»
            if (str3 != '0' && nzero != 0) {
                ch1 = "Áã" + str1.substr(str3 * 1, 1);
                ch2 = str2.substr(i, 1);
                nzero = 0;
            }
            else {
                if (str3 != '0' && nzero == 0) {
                    ch1 = str1.substr(str3 * 1, 1);
                    ch2 = str2.substr(i, 1);
                    nzero = 0;
                }
                else {
                    if (str3 == '0' && nzero >= 3) {
                        ch1 = '';
                        ch2 = '';
                        nzero = nzero + 1;
                    }
                    else {
                        if (j >= 11) {
                            ch1 = '';
                            nzero = nzero + 1;
                        }
                        else {
                            ch1 = '';
                            ch2 = str2.substr(i, 1);
                            nzero = nzero + 1;
                        }
                    }
                }
            }
        }
        if (i == (j - 11) || i == (j - 3)) { //Èç¹û¸ÃÎ»ÊÇÒÚÎ»»òÔªÎ»£¬Ôò±ØĞëĞ´ÉÏ
            ch2 = str2.substr(i, 1);
        }
        str5 = str5 + ch1 + ch2;

        if (i == j - 1 && str3 == '0') { //×îºóÒ»Î»£¨·Ö£©Îª0Ê±£¬¼ÓÉÏ"Õû"
            str5 = str5 + 'Õû';
        }
    }
    if (num == 0) {
        str5 = 'ÁãÔªÕû';
    }
    return str5;
}

//°Ñ2012¸Ä³É·¡ÁãÒ¼·¡£¬×ªÎªÆ±¾İ´òÓ¡Ê¹ÓÃ
function DateToCapital(rq){
   if (rq){
   		rq=replaceAll(rq,'0','Áã');
   		rq=replaceAll(rq,'1','Ò¼');
		rq=replaceAll(rq,'2','·¡');
   		rq=replaceAll(rq,'3','Èş');
   		rq=replaceAll(rq,'4','ËÁ');
   		rq=replaceAll(rq,'5','Îé');
   		rq=replaceAll(rq,'6','Â½');
   		rq=replaceAll(rq,'7','Æâ');
   		rq=replaceAll(rq,'8','°Æ');
   		rq=replaceAll(rq,'9','¾Á');
   		return rq;
   }else{
   		return '';
   } 
}


function setObjDisabled(name){
	var oElem=document.getElementById(name);
		var sTag=oElem.tagName.toUpperCase();
		switch(sTag)
		{
		case	"BUTTON":
			oElem.disabled=true;
			break;
		case	"SELECT":
		case	"TEXTAREA":
			oElem.readOnly=true;
			break;
		case	"INPUT":
			{
			var sType=oElem.type.toUpperCase();

			if(sType=="TEXT")oElem.readOnly=true;
			if(sType=="BUTTON"||sType=="IMAGE")oElem.disabled=true;
			if(sType=="CHECKBOX")oElem.disabled=true;
			if(sType=="RADIO")oElem.disabled=true;
			}
			break;
		default:
			oElem.disabled=true;
			break;
		}
	//set style
	oElem.style.backgroundColor="#eeeeee";
}

function setObjEnabled(name){
	var oElem=document.getElementById(name);
		var sTag=oElem.tagName.toUpperCase();
		switch(sTag)
		{
		case	"BUTTON":
			oElem.disabled=false;
			break;
		case	"SELECT":
		case	"TEXTAREA":
			oElem.readOnly=false;
			break;
		case	"INPUT":
			{
			var sType=oElem.type.toUpperCase();

			if(sType=="TEXT")oElem.readOnly=false;
			if(sType=="BUTTON"||sType=="IMAGE")oElem.disabled=false;
			if(sType=="CHECKBOX")oElem.disabled=false;
			if(sType=="RADIO")oElem.disabled=false;
			}
			break;
		default:
			oElem.disabled=false;
			break;
		}
	//set style
	oElem.style.backgroundColor="#FFFFFF";
}

//Ìæ»»ËùÓĞ×Ö·û
function replaceAll(str,oldStr,newStr) {
	return str.replace(new RegExp(oldStr,"gm"),newStr); 
}

function show_selectUser(objName,hideUserId,mode){
	var objParameter = new Object();
	objParameter.userName = objName;
	objParameter.userId = hideUserId;
	objParameter.partentWindowObj = window;
    mode=mode||"";
	window.showModalDialog(MATECH_SYSTEM_WEB_ROOT+"user/selectUser.jsp?mode="+mode,objParameter,"dialogHeight:420px;dialogWidth:500px;resizable:false;dialogHide:no;status:no;location=no;");
}

function show_selectJob(idFieldName,idFieldId,type){
	var objParameter = new Object();
	objParameter.idFieldName = idFieldName;
	objParameter.idFieldId = idFieldId;
	objParameter.partentWindowObj = window;
    type=type||"";
    
	window.showModalDialog(MATECH_SYSTEM_WEB_ROOT+"hr/selectJob.jsp?type="+type,objParameter,"dialogHeight:600px;dialogWidth:750px;resizable:false;dialogHide:no;status:no;location=no;");
}

function createNewWord(tempUrl,newUrl){
	var openDocObj = new ActiveXObject("SharePoint.OpenDocuments.2"); 
	openDocObj.CreateNewDocument(tempUrl, newUrl);
}

function editWord(url){
	var openDocObj = new ActiveXObject("SharePoint.OpenDocuments.2"); 
    openDocObj.EditDocument(url);
}

function viewWord(url){
	var openDocObj = new ActiveXObject("SharePoint.OpenDocuments.2"); 
	openDocObj.ViewDocument(url);
}

Array.prototype.contains = function (element) { 
	for (var i = 0; i < this.length; i++) { 
		if (this[i] == element) { 
			return true; 
		} 
	} 
	return false; 
}

function getParamObject() 
{ 
	var args = new Object( ); //ÉùÃ÷Ò»¸ö¿Õ¶ÔÏó 
	var query = window.location.search.substring(1); // È¡²éÑ¯×Ö·û´®£¬Èç´Ó http://www.snowpeak.org/testjs.htm?a1=v1&a2=&a3=v3#anchor ÖĞ½Ø³ö a1=v1&a2=&a3=v3¡£ 
	var pairs = query.split("&"); // ÒÔ & ·û·Ö¿ª³ÉÊı×é 
	for(var i = 0; i < pairs.length; i++) { 
		var pos = pairs[i].indexOf('='); // ²éÕÒ "name=value" ¶Ô 
		if (pos == -1) continue; // Èô²»³É¶Ô£¬ÔòÌø³öÑ­»·¼ÌĞøÏÂÒ»¶Ô 
		var argname = pairs[i].substring(0,pos); // È¡²ÎÊıÃû 
		var value = pairs[i].substring(pos+1); // È¡²ÎÊıÖµ 
		value = decodeURIComponent(value); // ÈôĞèÒª£¬Ôò½âÂë 
		args[argname] = value; // ´æ³É¶ÔÏóµÄÒ»¸öÊôĞÔ 
    } 
return args; // ·µ»Ø´Ë¶ÔÏó 
} 

function doUpdateSubsetID(){
	   var row=mt_form_getRowValues()[0];
	   var uuid=row.uuid;
	   var id=row.id;
	   var formid=getParamObject()["uuid"];
	   var url="employment.do";
	   
	   Ext.MessageBox.prompt("ĞŞ¸Ä×Ó¼¯±àºÅ","ÇëÊäÈëĞÂµÄ×Ó¼¯±àºÅ£¬±ØĞëÎªÊı×Ö",function(e,text){
	       if(e!="ok")return;
	       var param={method:"doUpdateSubsetID",formid:formid,uuid:uuid,newID:text};
	       $.post(url,param,function(str){
	         alert(str);
	         window.location.reload();
	      });
	   });
	}


//Éí·İÖ¤ÑéÖ¤
function validateIdCard(obj)
{
 var aCity={11:"±±¾©",12:"Ìì½ò",13:"ºÓ±±",14:"É½Î÷",15:"ÄÚÃÉ¹Å",21:"ÁÉÄş",22:"¼ªÁÖ",23:"ºÚÁú½­",31:"ÉÏº£",32:"½­ËÕ",33:"Õã½­",34:"°²»Õ",35:"¸£½¨",36:"½­Î÷",37:"É½¶«",41:"ºÓÄÏ",42:"ºş±±",43:"ºşÄÏ",44:"¹ã¶«",45:"¹ãÎ÷",46:"º£ÄÏ",50:"ÖØÇì",51:"ËÄ´¨",52:"¹óÖİ",53:"ÔÆÄÏ",54:"Î÷²Ø",61:"ÉÂÎ÷",62:"¸ÊËà",63:"Çàº£",64:"ÄşÏÄ",65:"ĞÂ½®",71:"Ì¨Íå",81:"Ïã¸Û",82:"°ÄÃÅ",91:"¹úÍâ"};
  var iSum = 0;
 //var info = "";
 var strIDno = obj;
 var idCardLength = strIDno.length;
 if(!/^\d{17}(\d|x)$/i.test(strIDno)&&!/^\d{15}$/i.test(strIDno))
        return 1; //·Ç·¨Éí·İÖ¤ºÅ

 if(aCity[parseInt(strIDno.substr(0,2))]==null)
 return 2;// ·Ç·¨µØÇø

  // 15Î»Éí·İÖ¤×ª»»Îª18Î»
 if (idCardLength==15)
 {
    sBirthday = "19" + strIDno.substr(6,2) + "-" + Number(strIDno.substr(8,2)) + "-" + Number(strIDno.substr(10,2));
  var d = new Date(sBirthday.replace(/-/g,"/"))
  var dd = d.getFullYear().toString() + "-" + (d.getMonth()+1) + "-" + d.getDate();
  if(sBirthday != dd)
                return 3; //·Ç·¨ÉúÈÕ
              strIDno=strIDno.substring(0,6)+"19"+strIDno.substring(6,15);
              strIDno=strIDno+GetVerifyBit(strIDno);
 }

       // ÅĞ¶ÏÊÇ·ñ´óÓÚ2078Äê£¬Ğ¡ÓÚ1900Äê
       var year =strIDno.substring(6,10);
       if (year<1900 || year>2078 )
           return 3;//·Ç·¨ÉúÈÕ

    //18Î»Éí·İÖ¤´¦Àí

   //ÔÚºóÃæµÄÔËËãÖĞxÏàµ±ÓÚÊı×Ö10,ËùÒÔ×ª»»³Éa
    strIDno = strIDno.replace(/x$/i,"a");

  sBirthday=strIDno.substr(6,4)+"-"+Number(strIDno.substr(10,2))+"-"+Number(strIDno.substr(12,2));
  var d = new Date(sBirthday.replace(/-/g,"/"))
  if(sBirthday!=(d.getFullYear()+"-"+ (d.getMonth()+1) + "-" + d.getDate()))
                return 3; //·Ç·¨ÉúÈÕ
    // Éí·İÖ¤±àÂë¹æ·¶ÑéÖ¤
  for(var i = 17;i>=0;i --)
   iSum += (Math.pow(2,i) % 11) * parseInt(strIDno.charAt(17 - i),11);
  if(iSum%11!=1)
                return 1;// ·Ç·¨Éí·İÖ¤ºÅ

   // ÅĞ¶ÏÊÇ·ñÆÁ±ÎÉí·İÖ¤
    var words = new Array();
    words = new Array("11111119111111111","12121219121212121");

    for(var k=0;k<words.length;k++){
        if (strIDno.indexOf(words[k])!=-1){
            return 1;
        }
    }

 return 0;
}


function viewCadet(){
	   var row=mt_form_getRowValues()[0];
	   var uuid=row.uuid;
	   var url="cadet.do?method=view&mode=view&uuid="+uuid;
	   window.showModalDialog(url,{},"dialogWidth:800px;dialogHeight:500px;status=no;location=no;resizable=yes");

	
}


function mt_open(url,title,width,height){
	window.open(url);
	//window.open(url,title,'height='+height+', width='+width+', toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');
	
}
Ext.namespace("Ext.matech.form");

Ext.matech.form.Validator = function (className,tips,test) {
	this.className = className;
	this.test = test ? test : function(){return true;};
	this.tips = tips ? tips : 'ÇëÊäÈëÓĞĞ§µÄÖµ';
};

Ext.matech.form.Validation = function (config){
	config = config || {}  ;
	
	
	isEmpty = function(v) {
		return ((v == null) || (v.length == 0) || /^\s+$/.test(v));
	} ;
	
	initRefer = function(referElm,separator){
		
		var param = new Array();
		if(separator) {
			var refer = referElm ;
			if(refer) {
				var referArr = refer.split(separator) ;
				for(var i=0;i<referArr.length;i++) {
					var referValue = referArr[i] ;
					var referObj = document.getElementById(referValue) ;
					
					if(referObj) {
				  		param.push(referObj.value); 
				  	}else {
				  		param.push(referValue || ""); 
				  	}
				}
			}
			
		}else {
			var refer = referElm.refer;
		  	var refer1 = referElm.refer1;
		  	var refer2 = referElm.refer2;
		  	
		  	var referObj = document.getElementById(refer) ;
		  	var refer1Obj = document.getElementById(refer1) ;
		  	var refer2Obj = document.getElementById(refer2) ;
		  	
		  	if(referObj) {
		  		param.push(referObj.value); 
		  	}else {
		  		param.push(refer || ""); 
		  	}
		  	
		  	if(refer1Obj) {
		  		param.push(refer1Obj.value) ; 
		  	}else {
		  		param.push(refer1 || ""); 
		  	}
		  	
		  	if(refer2Obj) {
		  		param.push(refer2Obj.value) ; 
		  	}else {
		  		param.push(refer2 || ""); 
		  	} 
		}
		
		return param ;
	} ;
	
	var rules = [
	    ['required', 'ÇëÊäÈëÓĞĞ§Öµ.', function(v) {
				return !isEmpty(v);
			}
	    ],
	    
		['checkexist-wheninputed', 'ÇëÊäÈëÓĞĞ§Öµ.', function(v) {
		    		if (v==null || v==""){
		    			return true;
		    		}else{
						return !isEmpty(v);
					}
				}
		],
		
		['validate-number', 'ÇëÊäÈëÊı×Ö.', function(v) {
					return isEmpty(v) || !isNaN(v);
				}
		],

		['validate-digits', 'ÇëÊäÈëÕûÊı.', function(v) {
					return isEmpty(v) ||  !/[^\d*$]/.test(v);
				}
		],
		
		['validate-positiveInt', 'ÇëÊäÈë´óÓÚ0µÄÕûÊı.', function(v) {
					return isEmpty(v) || /^[0-9]*[1-9][0-9]*$/.test(v);
				}
		],
		
		['validate-alpha', 'ÇëÊäÈëa-zÖ®¼äµÄ×ÖÄ¸.', function (v) {
					return isEmpty(v) ||  /^[a-zA-Z]+$/.test(v) ;
				}
		],
		
		['validate-alphanum', 'ÇëÊäÈëa-zÖ®¼äµÄ×ÖÄ¸»ò0-9Ö®¼äµÄÊı×Ö,²»ÔÊĞíÊäÈë¿Õ¸ñ»òÆäËü×Ö·û', function(v) {
					return isEmpty(v) ||  !/\W/.test(v) ;
				}
		],
		
		['validate-date', 'ÇëÊäÈëÓĞĞ§µÄÈÕÆÚ.', function(v) {
					try{
						var test = new Date(v);
						return isEmpty(v) || !isNaN(test);
					}catch(e){}
				}
		],
		
		['validate-email', 'ÇëÊäÈëÓĞĞ§ÓÊÏä. ÀıÈç username@domain.com .', function (v) {
					return isEmpty(v) || /\w{1,}[@][\w\-]{1,}([.]([\w\-]{1,})){1,3}$/.test(v) ;
				}
		],
		
		['validate-currency', 'ÇëÊäÈëÓĞĞ§»õ±Ò.ÀıÈç100.00 .', function(v) {
					return isEmpty(v) ||  /^([1-9,-]{1}[0-9,-]{0,2}(\,[0-9,-]{3})*(\.[0-9,-]{0,2})?|[1-9,-]{1}\d*(\.[0-9,-]{0,2})?|0(\.[0-9,-]{0,2})?|(\.[0-9,-]{1,2})?)$/.test(v) ;
				}
		],
	
		['validate-date-cn','ÇëÊ¹ÓÃÈÕÆÚ¸ñÊ½: yyyy-mm-dd. ÀıÈç 2006-03-17', function(v){
	    		if (v==null || v==""){
	    			return true;
	    		}
	     		if(!/^(?:([0-9]{4}-(?:(?:0?[1,3-9]|1[0-2])-(?:29|30)|((?:0?[13578]|1[02])-31)))|([0-9]{4}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1\d|2[0-8]))|(((?:(\d\d(?:0[48]|[2468][048]|[13579][26]))|(?:0[48]00|[2468][048]00|[13579][26]00))-0?2-29)))$/.test(v))
	     		{
	      			return false;
	     		}
	     		return true;
			}
		],
		
		['validate-phonenumber', 'µç»°ºÅÂë¿ÉÒÔÓÃ+¿ªÍ·£¬Ö»ÄÜÊäÈëÊı×Ö,-·ûºÅ ÀıÈç020-12345678', function(v){
			
	        	if(!/^[+]{0,1}(\d){1,3}[ ]?([-£­]?((\d)|[ ]){1,12})+$/.test(v)){
	      			return false;
	     		}
	     		return true;
	    	}
		],
	
		['phonenumber-wheninputed', 'µç»°ºÅÂë¿ÉÒÔÓÃ+¿ªÍ·£¬Ö»ÄÜÊäÈëÊı×Ö,-·ûºÅ ÀıÈç020-12345678', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(!/^[+]{0,1}(\d){1,3}[ ]?([-£­]?((\d)|[ ]){1,12})+$/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
	    	}
		],
		
		['year-wheninputed', 'Äê·İÊÇÓÉËÄ¸öÊı×Ö×é³É¡£', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(!/^\d{4}$/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
	    	}
		],
		
		['month-wheninputed', 'ÔÂ·İÊÇÓÉÁ½¸öÊı×Ö×é³É¡£', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(!/^\d{2}$/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
	    	}
		],
		
		['alphanum-wheninputed', 'ÇëÊäÈëÊı×Ö£¬×ÖÄ¸£¬ÏÂ»®Ïß£¬»ò - ¡£', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(!/^[\w-]{1,}[\w-]*$/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
	    	}
		],
		
		['filename-wheninputed', 'ÎÄ¼şÃû²»ÄÜ°üº¬\\/:*?"<>|', function(v){
                if (v==null || v==""){
                  return true;
                }else{
                  if(/[\\\/\*\?"<>|]+/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
	    	}
		],
		
		['0-100-wheninputed', 'ÇëÊäÈë0£­100µÄÊı×Ö', function(v) {
					return (isEmpty(v) || !isNaN(v))&&parseFloat(v)<100&&parseFloat(v)>0;
				}
		],	
		
		['ip-wheninputed', 'ÇëÊäÈëip»òÕßÍøÖ·', function(v) {
					return (isEmpty(v) || isIP(v)); 
				}
		],
		
		['validate-fax', 'ÇëÊäÈëÕıÈ·µÄ´«ÕæºÅ', function(v) {
				if (v==null || v==""){
                  return true;
                }else{
                  if(!/^\d+(-\d+)?$/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
			}
		],
		
		['validate-positive-number', 'ÇëÊäÈëÓĞĞ§µÄÕıÊı', function(v) {
				if (v==null || v==""){
                  return true;
                }else{
                  if(Number(v)=="0" ||!/^\d+(\.\d+)?$/.test(v))
                  {
                  return false;
                  }
                  return true;
                }
			}
		]
	] ;
	
	return {
		formId : config.formId,
		validators : {},
		validate : function (){
			this.initRule(rules) ;
			var elements = this.getAllElm() ;
			var allPass = true ;
			
			var valid = this ; 
			Ext.each(elements,function(element){
				var isPass = true ;
				if(valid.isVisible(element)) {
					isPass = valid.check(element);
					allPass = allPass && isPass ;   
				}
				
				if(isPass) {
					//½øĞĞºóÌ¨ÑéÖ¤
				}
					
			}) ;
			return allPass ;
		},
		
		initRule : function (rules) {
			var valid = this ;
			Ext.each(rules,function(rule){
				var className = rule[0] ;
				valid.validators[className] = new Ext.matech.form.Validator(rule[0],rule[1],rule[2]) ;
			}) ;
		} ,
		
		check : function (elm){
			var element = Ext.get(elm) ;
			var classNames = element.getAttribute("className") ;
			var classArray = classNames.split(" ") ;
			var value = this.trim(element.getValue()) ;
			value = value.replace("ÇëÑ¡Ôñ...","") ;
			value = value.replace("ÇëÑ¡Ôñ»òÊäÈë...","") ;
			
			//ÏÈÇå³ıÑùÊ½ÔÙÑéÖ¤
			element.removeClass('validation-failed');
			element.removeClass('validation-passed');
			
			var valid = this ;
			
			var isPass = true ;
			var isSelectValidtate = false ;   //¼ì²éÓĞÃ»ÓĞ½øĞĞÏÂÀ­µÚÈı¾äsqlÑéÖ¤,¼æÈİ¾ÉµÄÏÂÀ­
			Ext.each(classArray,function(className){
				
				if(className.toLowerCase() == "valuemustexist") {
					isSelectValidtate = true ;
					//ºóÌ¨ÑéÖ¤
					
					var backValid = valid.selectValidate(elm) ;
					if(!backValid) {
						var error = "ÊäÈëµÄÖµ²»´æÔÚ" ;
						valid.showErrorMsg(error, elm) ;
						isPass = false ;
						return false ;
					}else {
						valid.hideErrorMsg(elm) ;
					}
					
				}else if(className.toLowerCase() == "ajaxvalidate") {
					var returnValue = valid.ajaxValidate(elm) ;
					
					if(returnValue == "ok") {
						valid.hideErrorMsg(elm) ;
					}else {
						valid.showErrorMsg(returnValue, elm) ;
						isPass = false ;
						return false ;
					}
					
				}else {
					var validator = valid.validators[className] ;
					if(validator) {
						if(!validator.test.call(this,value)) {
							valid.showErrorMsg(element.getAttribute("title") || validator.tips,elm) ;
							isPass = false ;
							return false ;
						}else {
							valid.hideErrorMsg(elm) ;
						}
					}
				}
				
			}) ;
			
			if(isPass && !isSelectValidtate && elm.valuemustexist && elm.valuemustexist=="true") {
				//Èç¹ûÇ°Ì¨ÑéÖ¤Í¨¹ıÁË,²¢ÇÒÃ»½øĞĞºóÌ¨ÑéÖ¤,¾ÍÏÈ½øĞĞºóÌ¨ÑéÖ¤
				var backValid = this.selectValidate(elm) ;
				if(!backValid) {
					var error = "ÊäÈëµÄÖµ²»´æÔÚ" ;
					this.showErrorMsg(error, elm) ;
					isPass = false ;
				}
			}
			
			return isPass ;
		},
		
		getAllElm : function (){
			var form = Ext.get(config.formId) ;
			var inputElms = form.query("input[class]") ;
			var textAreaElms = form.query("textarea[class]") ;
			var selectElms = form.query("select[class]") ;
			
			return inputElms.concat(textAreaElms,selectElms) ;
		},
		
		trim : function (strSource){
			var t="";
			try{
				t=strSource.replace(/^\s*/,'').replace(/\s*$/,'');
			}catch(e){}
			return t;
		},
		
		isVisible : function(elm) {
			while(elm.tagName != 'BODY') {
				try{
				     if(!Ext.get(elm).isVisible())
					 return false;
				}catch(err){
					return true;
				}
				elm = elm.parentNode;
			}
			return true; 
		},
		
		showErrorMsg : function (tips,elm) {
			
			var element = Ext.get(elm) ;
			element.addClass('validation-failed');
			
			var tipType = config.tipType ;
			
			if(!tipType) tipType = "adivce" ;
			
			if(tipType == "advice") {
				this.showAdvice(tips, elm) ;
			}else if(tipType == "tip") {
				this.showToolTip(tips,elm);
			}else if(tipType == "alert") {
				alert(tips);
			}
			
		},
		
		hideErrorMsg : function (elm) {
			
			var element = Ext.get(elm) ;
			element.addClass('validation-passed');
			
			var tipType = config.tipType ;
			
			if(!tipType) tipType = "adivce" ;
			
			if(tipType == "advice") {
				this.hideAdvice(elm) ;
			}else if(tipType == "tip") {
				this.hideToolTip(elm) ;
			}else if(tipType == "alert") {
				
			}
			
		},
		
		showToolTip : function(tips,elm) {
			
			var toolTip = Ext.get(elm.id+'tip') ;
			var box = Ext.get(elm.id+'box') ;
			
			if(toolTip) {
				//ÒÑ¾­´æÔÚÔòÖ±½Ó¸üĞÂÄÚÈİ ÏÔÊ¾
				var content = Ext.get(elm.id+'content') ;
				content.update(tips);
				toolTip.dom.style.display = "";
				box.dom.style.display = "";
			}else {
				
				tooltip = Ext.DomHelper.append(document.body,{
					id: elm.id+'tip',
					cls: 'validate_tip',
					tag: 'div',
					html:"<span style='vertical-align:middle'>" 
						 +"<img src='" + MATECH_SYSTEM_WEB_ROOT + "/img/warn.png'></span>" 
						 + "&nbsp;<span id='"+elm.id+"content'>" + tips + "</span>" 
				}) ;
				 
				box = Ext.DomHelper.append(document.body,{
					id: elm.id+'box',
					cls: 'validate_box',
					tag: 'div'
				}) ;
			}
			
			var element = Ext.get(elm) ;
			var x = element.getX() ;
			var y = element.getY() ;
			
			//alert("x:"+x+" y:"+y + " left:"+element.getLeft())
			
			var tipElm = Ext.get(tooltip) ;
			if(tipElm.getWidth() > 150)
				tipElm.setWidth(150) ;
			
			if(tipElm.getWidth() < 100)
				tipElm.setWidth(100) ;
			
			var boxElm = Ext.get(box) ;
			
			var y = y - boxElm.getHeight() - tipElm.getHeight() + 10 ;
			//y = y < 0 ? 0 : y ;
			tipElm.setLeftTop(x+element.getWidth() - 40,y) ;
			boxElm.setLeftTop(x+element.getWidth() -20,y + tipElm.getHeight()) ;
			
			tipElm.on("click",function(){
				//tipElm.setOpacity(0.0,{duration:0.5,easing:'easeNone'});  
				//boxElm.setOpacity(0.0,{duration:0.5,easing:'easeNone'});  
				
				tipElm.dom.style.display = "none";
				boxElm.dom.style.display = "none";
				
				element.focus();
			}) ;
			
			var parent = element.parent("table") ;
			while(true) {
				parent = parent.parent() ;
				if(parent && 
				   ((parent.dom.tagName == "DIV" && parent.dom.style.overflow == "auto") 
				   || parent.dom.tagName == "BODY")) {
					break ;
				}
			}
			
			var curScrollTop = parent.getScroll().top ;
			var curScrollLeft = parent.getScroll().left ;
			var tipX = tipElm.getX() + curScrollLeft ;
			var tipY = tipElm.getY() + curScrollTop ;
			var boxX = boxElm.getX() + curScrollLeft ;
			var boxY = boxElm.getY() + curScrollTop ;
			
			if(parent.dom.tagName == "DIV") {
				parent.on("scroll",function(event,elm,obj){
					//ÖØËãÎ»ÖÃ
					//alert(parent.getScroll().top)
					var scrollTop = parent.getScroll().top ;
					var scrollLeft = parent.getScroll().left ;
					
					tipElm.setLeftTop(tipX - scrollLeft,tipY - scrollTop) ;
					boxElm.setLeftTop(boxX - scrollLeft ,boxY - scrollTop) ;
				});
			}
			
			
		},
		
		hideToolTip : function(elm) {
			var toolTip = Ext.get(elm.id+'tip') ;
			var box = Ext.get(elm.id+'box') ;
			
			if(toolTip) {
				toolTip.dom.style.display = "none";
				box.dom.style.display = "none";
			}
			
		},
		
		showAdvice : function(tips,elm) {
			var advice = Ext.get(elm.id+'tip') ;
			
			if(advice) {
				//ÒÑ¾­´æÔÚÔòÖ±½Ó¸üĞÂÄÚÈİ ÏÔÊ¾
				var content = Ext.get(elm.id+'content') ;
				content.update(tips);
				advice.show();
				return ;
			}
			
			var autoid = elm.autoid ;
			if(!autoid) {
				//¿ÉÄÜÊÇĞÂÏÂÀ­£¬´Óext¶ÔÏóÖĞÕÒautoid
				var inputId = elm.inputId ;
				if(inputId) {
					var selectCmp = Ext.getCmp(inputId) ;
					if(selectCmp)
						autoid = selectCmp.autoid ;
				}
			}
			var nextElm = elm ;
			var space = "" ;
			if(autoid) {
				nextElm = elm.nextSibling ;
				space = "&nbsp;&nbsp;&nbsp;" ;
			}
			var tooltip = Ext.DomHelper.insertAfter(nextElm,{
				id: elm.id+'tip',
				cls: 'validation-advice',
				tag: 'span',
				html:"<span style='vertical-align:middle'>" + space
					 +"<img src='" + MATECH_SYSTEM_WEB_ROOT + "/img/warn.png'></span>" 
					 + "&nbsp;<span id='"+elm.id+"content'>" + tips + "</span>" 
			}) ;
			
		},
		
		hideAdvice : function(elm) {
			var advice = Ext.get(elm.id+'tip') ;
			if(advice) {
				advice.hide();
			}
		},
		
		selectValidate : function(elm) {
			var autoid = elm.autoid ;
			var value ;
			var refer ;
			if(!autoid) {
				//¿ÉÄÜÊÇĞÂÏÂÀ­£¬´Óext¶ÔÏóÖĞÕÒautoid
				var inputId = elm.inputId ;
				if(!inputId) return true;
				
				var selectCmp = Ext.getCmp(inputId) ;
				if(!selectCmp) return true ;
				
				autoid = selectCmp.autoid ;
				value = selectCmp.getValue();
				refer = selectCmp.initReferParam(elm) ;
			}else {
				refer = initRefer(elm) ;
			}
			
			if(!autoid) return true;
			
			var url = MATECH_SYSTEM_WEB_ROOT+"/system.do?method=combox&checkmode=1&autoid="+autoid+"&pk1="+value;
			var param = "&refer=" + refer[0] + "&refer1="+ refer[1] + "&refer2="+ refer[2] ;
			
			var strResult = ajaxLoadPageSynch(url,param) ;
			if(strResult == ""){
				return false ;
			}
			
			return true ;
		},
		
		ajaxValidate : function(elm) {
			
			var validateId = elm.validateId ;
			var value ;
			var refer ;
			if(!validateId) {
				//¿ÉÄÜÊÇĞÂÏÂÀ­£¬´Óext¶ÔÏóÖĞÕÒautoid
				var inputId = elm.inputId ;
				if(!inputId) return true;
				
				var selectCmp = Ext.getCmp(inputId) ;
				if(!selectCmp) return true ;
				
				validateId = selectCmp.validateId ;
				value = selectCmp.getValue();
				refer = selectCmp.validRefer ;
			}else {
				refer = elm.validRefer ;
				value = elm.value ;
			}
			
			if(!validateId) return true ;
			
			var referArr = initRefer(refer,"|") ;
			var referStr = "" ;
			if(referArr.length > 0) {
				referStr = referArr.join("|") ;
			}
			
			var url = MATECH_SYSTEM_WEB_ROOT+"/system.do?method=validate&validateId="+validateId ;
			var param = "&refer="+referStr+"&value="+value + "&uuid=" + document.getElementById("uuid").value;
			
			var strResult = ajaxLoadPageSynch(url,param) ;
			
			return strResult ;
		}
		
		
	} ;
	
	
	
	
} ;//TableIdÊı×é
var copydataArray = new Array();
//Ñ¡È¡td¶ÔÏó
var selectedArray = new Array();
//¼ÇÂ¼Ñ¡È¡µÄĞĞºÅÁĞºÅ,±ÜÃâÖØ¸´
var selectedFlag = "";
//¼ÇÂ¼Ñ¡È¡¼ÆËãÆ÷
var selectedCalculator;
var startRow=1;
var startCell=1;
//±í¸ñ³õÊ¼»¯

String.prototype.Trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
String.prototype.replaceAll  = function(s1,s2){return this.replace(new RegExp(s1,"gm"),s2); }  
function initTableData(id) {
	if(document.getElementById(id)) {
		document.getElementById(id).style.cursor = "hand";
	}
	if(selectedCalculator!=null) {
		selectedCalculator.value = "";
	}
}
//td¶ÔÏó
function selectedIndex(x,y,obj) {
	this.rowIndex = x;
	this.cellIndex = y;
	this.focusElement = obj;
}

//¶à¸öÑ¡È¡
function selectMultiCell(e){
	//ÔÚÄÄ¸ötableÉÏ·¢ÉúµÄÊ±¼ä
	var focusElement = e.srcElement.parentElement.parentElement.parentElement;
	if(e.srcElement.tagName=="INPUT") {
		focusElement = e.srcElement.parentElement.parentElement.parentElement.parentElement; 
	}
	var flag = 0;
	for(var i=0; i<copydataArray.length; i++) {
		var copyElement = document.getElementById(copydataArray[i]);
		if(focusElement.id == copyElement.id) {
			flag=1;
			break;
		}
	}
	//Èç¹û²»ÊÇdatagrid ·µ»Ø
	if(!flag) {
		return;
	}
	if(e.button==1)
	{    
		//ÊÇ·ñ°´ÏÂCtrl¼ü
		if(e.ctrlKey) {
		} else {
			//Çå³şÑ¡È¡
			clearSelectedArea(selectedArray);
		}
		var endRow=e.srcElement.parentNode.rowIndex;
		var endCell=e.srcElement.cellIndex;
		if(e.srcElement.tagName=="INPUT") {
			endRow=e.srcElement.parentElement.parentElement.rowIndex;
			endCell=e.srcElement.parentElement.cellIndex;
		}
		
		//ÍÏ¶¯Ñ¡È¡ÉèÖÃÑùÊ½
		for(var m=Math.min(startRow,endRow);m<=Math.max(startRow,endRow);m++)
		{
			for(var n=Math.min(startCell,endCell);n<=Math.max(startCell,endCell);n++)
			{
				focusElement.rows[m].cells[n].style.backgroundColor="#FFFFDD";
				focusElement.rows[m].cells[n].setAttribute("name","sel");	
				
				if(selectedFlag.indexOf("("+m+","+n+")")>-1) {
				} else {
					selectedFlag += "("+m+","+n+")";
					var selectedTD = new selectedIndex(m,n,focusElement);
					selectedArray[selectedArray.length] = selectedTD;
				}
			}
		}
		
		var calculatorDiv;
		if(self.name=="rightFrame" || self.name=="workPlatFormMainFrame") {
			calculatorDiv = document.getElementById("calculatorDiv");
			selectedCalculator = document.getElementById("selected4Calculate");
		} else {
			calculatorDiv = window.parent.document.getElementById("calculatorDiv");
			selectedCalculator = window.parent.document.getElementById("selected4Calculate");
		}
		
		if(calculatorDiv!=null&&calculatorDiv.style.display!="none") {
			var calculatorExpression;
			
			if(self.name=="rightFrame" || self.name=="workPlatFormMainFrame") {
				calculatorExpression = document.getElementById("calculatorExpression");
			} else {
				calculatorExpression = window.parent.document.getElementById("calculatorExpression");
			}
			var value = calculatorExpression.value;
			
			//ÑéÖ¤»õ±ÒÀàĞÍµÄÕıÔò±í´ïÊ½
			var re = RegExp(/^([1-9,-]{1}[0-9,-]{0,2}(\,[0-9,-]{3})*(\.[0-9,-]{0,2})?|[1-9,-]{1}\d*(\.[0-9,-]{0,2})?|0(\.[0-9,-]{0,2})?|(\.[0-9,-]{1,2})?)$/);					
			
			for(var i=0;i<selectedArray.length; i++)
			{
				var tdValue = selectedArray[i].focusElement.rows[selectedArray[i].rowIndex].cells[selectedArray[i].cellIndex].innerText;
				if(tdValue=="") {
					try {
						tdValue = selectedArray[i].focusElement.rows[selectedArray[i].rowIndex].cells[selectedArray[i].cellIndex].getElementsByTagName("input")[0].value;
					} catch(e) {
						return;
					}
				}
				if(selectedCalculator.value.indexOf("(" + selectedArray[i].rowIndex + "," + selectedArray[i].cellIndex + ")")>-1) {
					continue;
				} 
				selectedCalculator.value += "(" + selectedArray[i].rowIndex + "," + selectedArray[i].cellIndex + ")";
				
				//ÅĞ¶ÏÑ¡ÖĞµÄÖµÊÇ·ñÊÇ»õ±Ò,Èç¹û²»ÊÇ¾ÍÖ±½Ó·µ»Ø
				if(( re.test(tdValue))||( tdValue.substring(0,1) == "-" && re.test(tdValue.substring(1)))||( tdValue.substring(0,1) == "(" && re.test(tdValue.substring(1,tdValue.length-1))) ) {
					if(tdValue.indexOf("-") == 0) {
						tdValue = "(" + tdValue +")";
					} else if(tdValue.indexOf("(")==0) {
						tdValue = "(-" + tdValue.substring(1,tdValue.length-1) + ")";
					}
					if(value=="") {
						calculatorExpression.value += tdValue;
					} else {
						calculatorExpression.value += " + " + tdValue;
					}
				} 
			}
			calculatorResult();
		}
	}
};
//µ¥Ñ¡
function selectCell(e)
{
	
	var tagName = e.srcElement.tagName;
	var focusElement = e.srcElement.parentElement.parentElement.parentElement;
	if(tagName == "INPUT") {
		focusElement = e.srcElement.parentElement.parentElement.parentElement.parentElement;
	}
	var flag = 0;
	for(var i=0; i<copydataArray.length; i++) {
		var copyElement = document.getElementById(copydataArray[i]);
		if(focusElement.id == copyElement.id) {
			flag=1;
			break;
		}
	}
	if(!flag) {
		return;
	}
	
	if(e.button==1)
	{	
		//ÊÇ·ñ°´ÏÂCtrl¼ü
		if(e.ctrlKey) {
		} else {
			clearSelectedArea(selectedArray);
		}
		//ÊÇ·ñ°´ÏÂshift¼ü
		
		if(e.shiftKey) {
			var endRow=e.srcElement.parentNode.rowIndex;
			var endCell=e.srcElement.cellIndex;
			if(e.srcElement.tagName=="INPUT") {
				endRow=e.srcElement.parentElement.parentElement.rowIndex;
				endCell=e.srcElement.parentElement.cellIndex;
			}
			for(var m=Math.min(startRow,endRow);m<=Math.max(startRow,endRow);m++)
			{
				for(var n=Math.min(startCell,endCell);n<=Math.max(startCell,endCell);n++)
				{
					focusElement.rows[m].cells[n].style.backgroundColor="#FFFFDD";
					focusElement.rows[m].cells[n].setAttribute("name","sel");
					
					if(selectedFlag.indexOf("("+m+","+n+")")>-1) {
					
					} else {
						selectedFlag += "("+m+","+n+")"	
						var selectedTD = new selectedIndex(m,n,focusElement);
						selectedArray[selectedArray.length] = selectedTD;
					}
				}
			}
		} else {
			if(e.srcElement.tagName=="TD") {
				startRow=e.srcElement.parentNode.rowIndex;
				startCell=e.srcElement.cellIndex;
				e.srcElement.style.backgroundColor="#FFFFDD";
				e.srcElement.setAttribute("name","sel");
			} else if(e.srcElement.tagName=="INPUT") {
				startRow=e.srcElement.parentNode.parentElement.rowIndex;
				startCell=e.srcElement.parentElement.cellIndex;
				e.srcElement.parentElement.style.backgroundColor="#FFFFDD";
				e.srcElement.parentElement.setAttribute("name","sel");
			}
			
			if(selectedFlag.indexOf("("+startRow+","+startCell+")")>-1) {
					
			} else {
				selectedFlag += "("+startRow+","+startCell+")";	
				var selectedTD = new selectedIndex(startRow,startCell,focusElement);
				selectedArray[selectedArray.length] = selectedTD;
			}
		}
		
		var calculatorDiv;
		if(self.name=="rightFrame" || self.name=="workPlatFormMainFrame") {
			calculatorDiv = document.getElementById("calculatorDiv");
			selectedCalculator = document.getElementById("selected4Calculate");
		} else {
			calculatorDiv = window.parent.document.getElementById("calculatorDiv");
			selectedCalculator = window.parent.document.getElementById("selected4Calculate");
		}
		
		if(calculatorDiv!=null&&calculatorDiv.style.display!="none") {
			
			var calculatorExpression;
			
			if(self.name=="rightFrame" || self.name=="workPlatFormMainFrame") {
				calculatorExpression = document.getElementById("calculatorExpression");
			} else {
				calculatorExpression = window.parent.document.getElementById("calculatorExpression");
			}
			var value = calculatorExpression.value;
			
			//ÑéÖ¤»õ±ÒÀàĞÍµÄÕıÔò±í´ïÊ½
			var re = RegExp(/^([1-9,-]{1}[0-9,-]{0,2}(\,[0-9,-]{3})*(\.[0-9,-]{0,2})?|[1-9,-]{1}\d*(\.[0-9,-]{0,2})?|0(\.[0-9,-]{0,2})?|(\.[0-9,-]{1,2})?)$/);
			
			for(var i=0;i<selectedArray.length; i++)
			{
				var tdValue = selectedArray[i].focusElement.rows[selectedArray[i].rowIndex].cells[selectedArray[i].cellIndex].innerText;
				if(tdValue=="") {
					try {
						tdValue = selectedArray[i].focusElement.rows[selectedArray[i].rowIndex].cells[selectedArray[i].cellIndex].getElementsByTagName("input")[0].value;
					} catch(e) {
						return;
					}
				}
				if(selectedCalculator.value.indexOf("(" + selectedArray[i].rowIndex + "," + selectedArray[i].cellIndex + ")")>-1) {
					continue;
				} 
				selectedCalculator.value += "(" + selectedArray[i].rowIndex + "," + selectedArray[i].cellIndex + ")";
				
				//ÅĞ¶ÏÑ¡ÖĞµÄÖµÊÇ·ñÊÇ»õ±Ò,Èç¹û²»ÊÇ¾ÍÖ±½Ó·µ»Ø
				if(( re.test(tdValue))||( tdValue.substring(0,1) == "-" && re.test(tdValue.substring(1)))||( tdValue.substring(0,1) == "(" && re.test(tdValue.substring(1,tdValue.length-1))) ) {
					if(tdValue.indexOf("-") == 0) {
						tdValue = "(" + tdValue +")";
					} else if(tdValue.indexOf("(")==0) {
						tdValue = "(-" + tdValue.substring(1,tdValue.length-1) + ")";
					}
					if(value=="") {
						calculatorExpression.value += tdValue;
					} else {
						calculatorExpression.value += " + " + tdValue;
					}
				} 
			}
			calculatorResult();
		}
	}
};
//Çå³ıÑ¡Çø
function clearSelectedArea(array)
{
	 // window.event.cancelBubble = true; 
	//window.event.returnValue = false;

	try{
		//alert(array.length);
		if(array) {
			for(var i=0; i<array.length; i++) {
			
				var selectedTD = array[i];
				selectedTD.focusElement.rows[selectedTD.rowIndex].cells[selectedTD.cellIndex].style.backgroundColor="";
				selectedTD.focusElement.rows[selectedTD.rowIndex].cells[selectedTD.cellIndex].removeAttribute("name");
			}	
		}
		
	} catch(e) {
	}
	selectedArray = new Array();
	selectedFlag = "";
}

function changeRowsAndCols(text) {
	if(text!="") {
		var textArray = text.split("\n");
		var myarray = new Array(textArray.length);
		for(var i=0; i<myarray.length; i++) {
			var textArray1 = textArray[i].split("\t");
			myarray[i] = new Array(textArray1.length);
			for(var j=0; j<myarray[i].length; j++) {
				myarray[i][j] = textArray1[j];
			}
		}
	}
	
	var text_rows = myarray.length;
	var text_cols = myarray[text_rows-1].length;
	var newArray = new Array(text_cols);
	for(var i=0; i<newArray.length; i++) {
		newArray[i] = new Array();
	}
	
	for(var i=0; i<myarray.length; i++) {
		for(var j=0; j<myarray[i].length; j++) {
			try {
				newArray[j][i] = myarray[i][j];
			} catch(e) {
				alert(e);
			}
		}
	}
	
	var returnText = "";
	for(var i=0; i<newArray.length; i++) {
		for(var j=0; j<newArray[i].length; j++) {
			if(newArray[i][j]=="" || typeof(newArray[i][j])=="undefined") {
				returnText += "\t"
			} else {
				returnText += newArray[i][j] + "\t"
			}
		}
		returnText = returnText.substring(0,returnText.length-1);
		returnText += "\n";
	}
	returnText = returnText.substring(0,returnText.length-1);
	return returnText;
}

//»ñÈ¡excel¸ñÊ½Êı¾İ
function getData(obj,includeHead){
	var text = ""; 
	var array = setFlag(obj);
	var dataArray = new Array();
	var cellIndexArr = new Array();
	
	var cellIndexStr = "" ; //¼ÇÂ¼ÒªÌí¼Ó±íÍ·µÄÁĞ
	for(var i=0; i<selectedArray.length; i++) {
		if(selectedArray[i].focusElement.id==obj.id) {
			var count = 0;
			var focusElement = selectedArray[i].focusElement;
			var rowIndex = selectedArray[i].rowIndex;
			var cellIndex = selectedArray[i].cellIndex;

			for(var j=0; j<cellIndex; j++) {
				if(array[j]>0) {
					count += 1;
				}
			}
			//alert(rowIndex+"|"+(obj.rows[rowIndex].cells[cellIndex].innerText.Trim()));
			if(dataArray[rowIndex]) {
				//alert(obj.rows[rowIndex].cells[cellIndex].innerText.Trim());
				
				if(cellIndexStr.indexOf(cellIndex) == -1) {
					cellIndexStr += cellIndex + "," ;
				}
				
				dataArray[rowIndex][count] = obj.rows[rowIndex].cells[cellIndex].innerText.Trim();
			} else {
				dataArray[rowIndex] = new Array();
				if(cellIndexStr.indexOf(cellIndex) == -1) {
					cellIndexStr += cellIndex + "," ;
				}
				dataArray[rowIndex][count] = obj.rows[rowIndex].cells[cellIndex].innerText.Trim();
			}
		}
	}
	
	if(cellIndexStr != "") {
		cellIndexStr = cellIndexStr.substring(0,cellIndexStr.length-1) ;
	}
	
	if(includeHead) {
		//¸´ÖÆº¬±íÍ·
		var cellIndexArr ;
		if(cellIndexStr != "") {
			cellIndexArr = cellIndexStr.split(",") ;
		}
		
		dataArray[0] = new Array(); 
		for(var i=0;i<cellIndexArr.length;i++) {
		//	alert(obj.rows[0].cells[cellIndexArr[i]].innerText.Trim());
			
			//ÕâÀïÈç¹ûÊÇ¶à±íÍ·£¬¾ÍÈ¡µÚ¶şĞĞ×ö±íÍ·£¬ËùÒÔÖ»Ö§³Ö¶ş¼¶±íÍ·
			if(obj.rows[0].cells.length == array.length) {
				dataArray[0][i] = obj.rows[0].cells[cellIndexArr[i]].innerText.Trim();
			}else {
				dataArray[0][i] = obj.rows[1].cells[cellIndexArr[i]].innerText.Trim();
			}
		}
	}
	for(var i=0; i<dataArray.length; i++) {
		var temp = "";
		if(dataArray[i]!=null) {
			for(var k=0; k<dataArray[i].length; k++) {
				if(dataArray[i][k]!=null) {
					temp += dataArray[i][k];
				}
			}
			
			if(temp.length>0) {
				for(var j=0; j<dataArray[i].length; j++) {
					if(dataArray[i][j]!=null) {
						//Ìæ»»ÖĞÎÄ¶ººÅ£¬Ö§³Ö½ğ¶îÄ£ºı²éÑ¯
						text += dataArray[i][j].replaceAll("£¬",",")+"\t";
					} else {
						text += "\t";
					}
				}
				text = text.substring(0,text.length-1);
				text += "\n";
			} 
		}
		
	}
	text = text.substring(0,text.length-1);
	return text;
}

//ÉèÖÃ±êÖ¾Î»
function setFlag(obj) {
	var length = obj.rows.length ;
	if(obj.rows.length > 3) {
		var arrayLength = obj.rows[2].cells.length;
	} 
	var array = new Array(arrayLength);
	for(var i=0; i<arrayLength; i++) {
		array[i] = 0;
	}
	
	for(var j=0; j<selectedArray.length; j++) {
		if(selectedArray[j].focusElement.id == obj.id) {
			array[selectedArray[j].cellIndex] += 1;
		}
	}
	return array;
}
//È¥³ıÊı×éÖĞÖØ¸´ÔªËØ
function addCopyTableId(name) {
	var flag = 1;
	for(var i=0; i<copydataArray.length; i++) {
		if(copydataArray[i]==name) {
			flag = 0;
		}
	}
	if(flag) {
		copydataArray.push(name);
	}
}


function rightMenuCopy(flag) {
	var text = "";
	try{
		
		
		var element = window.event.srcElement.parentElement;
		
		for(var i=0; i<copydataArray.length; i++) {
			
			var tableObj = document.getElementById(copydataArray[i]) ;
			
			if(copydataArray[i] == element.obj.id) {
				if(flag==1) {
					//×ªÖÃ¸´ÖÆ
					text += getData(element.obj);
					text = changeRowsAndCols(text);
				} else if(flag == 2) {
					//¸´ÖÆ(´ø±íÍ·)
					text += getData(element.obj,true);
				}else if(flag == 3)  {
					//×ªÖÃ¸´ÖÆ£¨´ø±íÍ·£©
					text += getData(element.obj,true);
					text = changeRowsAndCols(text);
				}else {
					//¸´ÖÆ
					text += getData(element.obj);
				}
			}
			
		}		
		try{
	
			//»ñµÃµ±Ç°´°¿ÚµÄµØÖ·ºÍ¶Ë¿ÚÏÈ
			//alert("http:\/\/"+window.location.host +strUrl);
			var t,t1;
			
			//ÕÒµ½Ö÷²Ù×÷½çÃæ
			t = window.opener;
			
			if(!t)//Èç¹ûÊÇ¿ò¼ÜÒ³ÔòÕÒËüµÄ¸¸´°¿Ú
				t = window.parent;
			if (t){
				//alert('±»ĞÂ¿ª´°¿Ú');
				t1 = t.window.opener;
				
				if(!t1)//Èç¹ûÊÇ¿ò¼ÜÒ³ÔòÕÒËüµÄ¸¸´°¿Ú
					t1 = t.window.parent;	
				while (t1){
					t = t1;
					t1 = t.window.opener;
					//alert(t1)
					if(!t1){//Èç¹ûÊÇ¿ò¼ÜÒ³ÔòÕÒËüµÄ¸¸´°¿Ú
						t1 = t.window.parent;
						if(t1.bottomFrame){
							break;
						}
					}	
				}
			}else{
				//alert('Ã»ÓĞ±»ĞÂ¿ª´°¿Ú');
				t = window;	
			}
			
			//ÔÚÖ÷²Ù×÷½çÃæÖĞÕÒµ½×îÉÏÃæµÄÄÇ¸öWINDOW
			t1 = t.parent;
			while (t1 && t1 != t){
				t = t1;
				t1 = t.window.parent;
			}
			//ÕÒµ½×îºóµÄÄÇ¸öURL
			if (t){
				t.bottomFrame.myTextToClipboard(text);
				//t.open(strUrl);
			}
		}catch(e){
			var AuditReport =  new ActiveXObject("AuditReportPoject.AuditReport");
			AuditReport.subClipboardSetText(text);
			AuditReport=null;
		}
		
		//clipboardData.setData("text",text);
		hideRightMenu();
	} catch(e) {
	}
	//alert("¸´ÖÆ³É¹¦");
}

function rightMenuCancel() {
	clearSelectedArea(selectedArray);
	hideRightMenu();
}

//ÓÒ¼ü²Ëµ¥
document.oncontextmenu = showRightMenu;
function showRightMenu() {
	

	try{
		var obj = window.event.srcElement;
		do{
			if(obj.tagName == "TABLE") break ;
			obj = obj.parentElement;
		}while(obj.parentElement != null);
		createRightMenu(obj);
	} catch(e) {
		return true;
	}
	var flag = 0;
	var rightMenu = document.getElementById("rightMenu");
	var element = event.srcElement;
	do{
		if(element.id=="data_copy" || element.id=="data_copy1") {
			rightMenuCopy();
			clearSelectedArea(selectedArray);
			break;
		} else if(element.id=="data_paste"){
			try{
				rightMenuCancel();
			} catch(e) {
				
			}
			break;
		} 
		for(var i=0;i<copydataArray.length; i++) {
			var copyElement = document.getElementById(copydataArray[i]);
			if(element.id ==copyElement.id) {
				flag=1;
				break;
			}
		} 
		if(element.parentElement) {
			element = element.parentElement;
		}
	}while(element.parentElement);
	if(flag) {
		rightMenu.style.position = "absolute";
		rightMenu.style.left = event.clientX + document.body.scrollLeft;
		rightMenu.style.top = event.clientY + document.body.scrollTop;
		rightMenu.style.display="";
		rightMenu.style.visibility = "visible" ;
		return false;
	}
	else {
		rightMenu.style.display="none";
		return true;
	}
}

function createRightMenu(obj) {
	var txtHtml = "";
	txtHtml += "<div class=\"coolMenuItem\" id=\"data_copy\" style=\"text-align:center;padding:5px 4px;color:#000;\" onmouseover=\"this.style.backgroundColor='#E4E8EF';this.style.cursor='hand';\" onmouseout=\"this.style.backgroundColor='#B0C4DE'\"  onclick=\"return rightMenuCopy();\">¸´  ÖÆ</div>";
	txtHtml += "<div class=\"coolMenuItem\" id=\"data_copy\" style=\"text-align:center;padding:0px 6px;color:#000;\" onmouseover=\"this.style.backgroundColor='#E4E8EF';this.style.cursor='hand';\" onmouseout=\"this.style.backgroundColor='#B0C4DE'\"  onclick=\"return rightMenuCopy(2);\">¸´ÖÆ(´ø±íÍ·)</div>";
	txtHtml +="<div class=\"coolMenuDivider\"></div>";
	txtHtml += "<div class=\"coolMenuItem\" id=\"data_copy1\" style=\"text-align:center;padding:5px 4px;color:#000;\" onmouseover=\"this.style.backgroundColor='#E4E8EF';this.style.cursor='hand';\" onmouseout=\"this.style.backgroundColor='#B0C4DE'\"  onclick=\"return rightMenuCopy(1);\">×ªÖÃ¸´ÖÆ</div>";
	txtHtml += "<div class=\"coolMenuItem\" id=\"data_copy\" style=\"text-align:center;padding:0px 0px;color:#000;\" onmouseover=\"this.style.backgroundColor='#E4E8EF';this.style.cursor='hand';\" onmouseout=\"this.style.backgroundColor='#B0C4DE'\"  onclick=\"return rightMenuCopy(3);\">×ªÖÃ¸´ÖÆ(´ø±íÍ·)</div>";
	txtHtml +="<div class=\"coolMenuDivider\"></div>";
	txtHtml += "<div class=\"coolMenuItem\" id=\"data_paste\" style=\"text-align:center;padding:5px 6px;color:#000;\" onmouseover=\"this.style.backgroundColor='#E4E8EF';this.style.cursor='hand';\" onmouseout=\"this.style.backgroundColor='#B0C4DE'\"  onclick=\"return rightMenuCancel();\">È¡  Ïû</div>";
	document.getElementById("rightMenu").className = "coolMenu";
	document.getElementById("rightMenu").innerHTML = txtHtml;
	document.getElementById("rightMenu").obj = obj ;
}

function hideRightMenu() {
	document.getElementById("rightMenu").style.display="none";
	var element = event.srcElement;
	var flag = 0;
	do{
		for(var i=0;i<copydataArray.length; i++) {
			var copyElement = document.getElementById(copydataArray[i]);
			if(element.id ==copyElement.id ) {
				flag=1;
				break;
			}
		}
		element = element.parentElement;
	}while(element.parentElement);
	if(!flag) {
		clearSelectedArea(selectedArray);
	}
}
var tableIds ="" ;
var selectedRow = "";
 
 function showTD(obj) {
//	var tempValue = obj+"Span" ;
//	document.getElementById("displayTemp").value = tempValue ;
	var divId = document.getElementById(obj);
	divId.style.visibility="visible";
	obj.bgColor='#E4E8EF';
 }

 function hideTD(obj) {
	var divId = document.getElementById(obj);
	divId.style.visibility='hidden' ;
	obj.bgColor='#B9C4D5';
}

function addArrayElement(array,name) {
	var flag = 1;
	for(var i=0; i<array.length; i++) {
		if(array[i]==name) {
			flag = 0;
		}
	}
	if(flag) {
		array.push(name);
	}
}
	/**
	 * Éè¶¨ĞĞÑ¡Ôñ¹¦ÄÜ
	 * @param 
	 */
function rowSelectStyle(tableId) {

	if(event.srcElement.type=="checkbox") {
		return;
	}
	
	var selectedRow = eval("selectedRow_"+tableId);
	
	var trObj = getTR() ;
	if(selectedRow!="") {
		document.getElementById(tableId).rows[selectedRow].style.backgroundColor = "";
	}
	
	eval("selectedRow_"+tableId+"=trObj.rowIndex");
	trObj.style.background = "#b9c4d5";
	//selectedRow = trObj.rowIndex ;
	/*return ;
	alert(selectedRow) ;
	if(selectedRow!="") {
		if(selectedRow%2!=0) {
			document.getElementById(tableId).rows[selectedRow].className = "oddLine";
		} else {
			document.getElementById(tableId).rows[selectedRow].className = "evenLine";
		}
	}
	
	selectedRow = event.srcElement.parentElement.parentElement.rowIndex;
	var selectedElement = document.getElementById(tableId).rows[selectedRow];
	selectedElement.className = "selectedLine";
	selectedElement.onmouseover = function() {
		event.cancelBubble = true;
	};
	selectedElement.onmouseout = function() {
		event.cancelBubble = true;
	};
//	alert(0)
*/}

	/**
	 * ´´½¨ÁĞ¸½¼Ó¹¦ÄÜ²Ëµ¥
	 * @param 
	 */
function createMenu(tableId,field,obj,numberCol,tableHead,isOrderBy,fixColNum,isFixedCol)
{
 var isEqual = false ;
  if(tableIds == "") {
  	tableIds += tableId ;
  }else {
  	var tableIdArr = tableIds.split(",") ;
  	for(var i=0;i<tableIdArr.length;i++) {
  		if(tableIdArr[i] == tableId )  isEqual=true ;
  			
  	}
  	if(!isEqual) {
  		tableIds += ","+tableId ;
  	}
  }
  
  var txtHTML = "" ;
  if(isOrderBy != "")  {
  txtHTML += "<div  class=\"coolMenuItem\" style='padding-top: 1px;color:black' onMouseOver=\"this.style.background='#E4E8EF'\"    onMouseOut=\"this.style.background='#B0C4DE'\" onclick=\"addOrderby_CH_"+tableId+"('"+field+"','ASC');hideMenu('"+tableId+"');\">¡ü×·¼ÓÉıĞò</div>";
  txtHTML += "<div class=\"coolMenuItem\" style='padding-top: 1px;color:black' onMouseOver=\"this.style.background='#E4E8EF'\"    onMouseOut=\"this.style.background='#B0C4DE'\" onclick=\"addOrderby_CH_"+tableId+"('"+field+"','DESC');hideMenu('"+tableId+"');\">¡ı×·¼Ó½µĞò</div>";
  txtHTML +="<div class=\"coolMenuDivider\"></div>";
  }
  if(isFixedCol != "isFixedColumn") {
	  if(tableHead != "") {
	  	txtHTML += "<div class=\"coolMenuItem\" style='padding-top: 1px;color:black' onMouseOver=\"mouseMoveDiv('"+field+"','"+tableId+"','"+tableHead+"');this.style.background='#E4E8EF'\" onMouseOut=\"hideCellsMenu('"+tableId+"');this.style.background='#B0C4DE'\" onclick=\"mouseMoveDiv('"+field+"','"+tableId+"','"+tableHead+"');\" >&nbsp;&nbsp;Òş²ØÁĞ&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;¡ú </div>";
	  }else {
	  	txtHTML += "<div class=\"coolMenuItem\" style='padding-top: 1px;color:black' onMouseOver=\"mouseMoveDiv('"+field+"','"+tableId+"','');this.style.background='#E4E8EF'\" onMouseOut=\"hideCellsMenu('"+tableId+"');this.style.background='#B0C4DE'\" onclick=\"mouseMoveDiv('"+field+"','"+tableId+"','');\" >&nbsp;&nbsp;Òş²ØÁĞ&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;¡ú </div>";
	  }
	  txtHTML += "<div class=\"coolMenuDivider\"></div>";
  }
/*  if(isFixedCol == "isFixedColumn") {
  	
  	if(tableHead != "") { txtHTML += "<div class=\"coolMenuItem\" style='padding-top: 1px;color:black' onMouseOver=\"this.style.background='#E4E8EF'\" onMouseOut=\"this.style.background='#B0C4DE'\" onClick=\"fixedDynamicCol('"+tableId+"',"+fixColNum+",'"+tableHead+"');hideMenu('"+tableId+"');\">&nbsp;&nbsp;Ëø¶¨ÁĞ</div>";}
  	else {
 		 txtHTML += "<div class=\"coolMenuItem\" style='padding-top: 1px;color:black' onMouseOver=\"this.style.background='#E4E8EF'\" onMouseOut=\"this.style.background='#B0C4DE'\" onClick=\"fixCol_" +tableId+ "('" +fixColNum+ "');hideMenu('"+tableId+"');\">&nbsp;&nbsp;Ëø¶¨ÁĞ</div>";
  	}
  }*/
  

  if(numberCol == "numberCol") {
  	txtHTML += "<div class=\"coolMenuItem\" style='padding-top: 1px;color:black' onMouseOver=\"this.style.background='#E4E8EF'\" onMouseOut=\"this.style.background='#B0C4DE'\" onClick=\"createNumDiv('"+tableId+"PromptDiv','½ğ¶î¹ıÂË','"+tableId+"','"+field+"');createShieldDiv()\" onMouseOver=\"\">&nbsp;&nbsp;½ğ¶î¹ıÂË</div>";
  }
  else {
  txtHTML += "<div class=\"coolMenuItem\" style='padding-top: 1px;color:black' onMouseOver=\"this.style.background='#E4E8EF'\" onMouseOut=\"this.style.background='#B0C4DE'\" onClick=\"createPromptDiv('"+tableId+"PromptDiv','¹Ø¼ü×Ö¹ıÂË','"+tableId+"','"+field+"');createShieldDiv()\" onMouseOver=\"\">&nbsp;&nbsp;¹Ø¼ü×Ö¹ıÂË</div>";
  }
 	txtHTML += "<div class=\"coolMenuItem\" style='padding-top: 1px;color:black' onMouseOver=\"this.style.background='#E4E8EF'\" onMouseOut=\"this.style.background='#B0C4DE'\" onClick=\"cancelFilter('"+tableId+"');hideMenu('"+tableId+"');\" onMouseOver=\"\">&nbsp;&nbsp;È¡Ïû¹ıÂË</div>";  
  	try {
  	 obj.className = "coolMenu";
     obj.innerHTML = txtHTML;
  	}catch(e){}
}


	/**
	 * ´´½¨¶à±íÍ·Òş²ØÁĞ²Ëµ¥
	 * @param 
	 */
function createDynamicCellsMenu(tableId,field,tableHead) {
	
	
	if( tableHead != "") {
	 //ÒÔÏÂ½âÎö¸´ÔÓ±íÍ·×Ö·û´®
	 //K´ú±í°üº¬×Ó±íÍ·µÄ¸öÊı
	 var tableArr = tableHead.split(",");
     var k = new Array(tableArr.length) ;
     var tableLength = dynamicHeadGetLevel(tableHead) ;
	
     for(var i=0;i<tableArr.length;i++) {
     	
		if(tableArr[i].indexOf("{") >-1) {
			var tempStr = tableArr[i].substring(0,tableArr[i].indexOf("{")) ;
			tableArr[i] = tableArr[i].substring(0,tableArr[i].indexOf("{"))+"("+tableArr[i].substring(tableArr[i].indexOf("{")+1,tableArr[i].length)+")";
			for(var j=i+1;j<tableArr.length;j++){
				
				if(tableArr[j].indexOf("}") >-1) {
					tableArr[j] =tempStr+"("+tableArr[j].substring(0,tableArr[j].indexOf("}"))+")";
					break ;
				}else
				tableArr[j] = tempStr+"("+tableArr[j]+")";
			}
			i = j ;
		}
	}
	
/*==========================================================================================*/	 	
	 	var hiddenStr = document.getElementById("hideColumnsStr_"+tableId).value;
	 	var cellsMenu = document.getElementById(tableId+"_cellsMenu") ;
	 	var colName = document.getElementById("colName_"+tableId).value;
	 	cellsMenu.className= "coolMenu" ;
		cellsMenu.style.visibility = "hidden";
	 	
	 	var textHTML = "";
		var isDisable = "" ;
		var state = "" ;
		var hiddenArr = null ;
		var colNameArr = null ;
		
		if(hiddenStr != "") {
			 hiddenArr = hiddenStr.split(",");
		}
		if(colName != "") {
			 colNameArr = colName.split("`");
		}
	 	
		
	 	
	 	for(var i=0;i<tableArr.length;i++) {
	 		
	 		
	 		if(colNameArr[i] == field) {
				isDisable = "disabled=\"true\"" ;
			}else {
				isDisable = "" ;
			}
		if(hiddenStr == "") {
			state = "checked" ;
		}else {
			if(hiddenArr[i] == "hidden") {
				state = "" ;
				/*var colObj = document.getElementById(tableId+"_col"+i) ;
	 			colObj.style.display = "none" ;*/
				hidecol(tableId,(i+1),tableLength,tableArr.length);
			}else {
				state = "checked" ;
			/*	var colObj = document.getElementById(tableId+"_col"+j) ;
	 			colObj.style.display = "" ;*/
				showcol(tableId,(i+1),tableLength,tableArr.length);
			}
		}	
		
		textHTML += "<div nowrap=\"nowrap\" style=\"height:auto\"><input style=\"\" "+isDisable+" class=\"\" type=\"checkbox\" "+state+" id=\""+tableId+"_lab"+i+"\" onclick=\"hideDynamicColumns("+(i+1)+",this.checked,'"+tableId+"',"+tableArr.length+","+tableLength+");\"><label class=\"coolMenuItem\" for=\""+tableId+"_lab"+i+"\" style='padding-top: 1px;color:black;height:auto' onMouseOver=\"this.style.background='#E4E8EF';\" onMouseOut=\"this.style.background='#B0C4DE';\">"+tableArr[i]+"</label><div>";
		}
     	cellsMenu.innerHTML = textHTML;	
	 	}
	 	 }
	 
	 
/*	function hideDynamicColumns(col,nums,tableId,k) {
		 var hiddenStr = "" ;
		for(var i=0;i<nums;i++) {
			if(document.getElementById(tableId+"_lab"+i).checked) {
				hiddenStr += "''"+","; 
			}
			else hiddenStr += "hidden"+"," ;
		}
		hiddenStr = hiddenStr.substring(0,hiddenStr.length-1) ;
	
	document.getElementById("hideColumnsStr_"+tableId).value = hiddenStr ;
	for(var i=col;i<col+k;i++) {
		var colObj = document.getElementById(tableId+"_col"+i) ;
	if( colObj.style.display == "") 
	 colObj.style.display = "none" ;
	else colObj.style.display = "" ;
	}
	}*/
	 	 
	
	 /**
	 * »ñµÃ¶à±íÍ·µÄ²ã´Î,·µ»ØÒ»¸öÊı×Ö
	 * @param 
	 */ 
	function dynamicHeadGetLevel(str) {
		// Ò»¹²¶àÉÙ²ã
		var iLevel = 1, iTempLevel = 1;
		// (ºÍ£©µÄÎ»ÖÃ¡£iNextÊÇÏÂÒ»¸öiLeftµÄÎ»ÖÃ
		var iLeft = -1, iRight = -1, iNext = -1;

		/**
		 * Çó³ö×î´ó²ã´Î
		 */
		while ((iLeft = str.indexOf("{", iRight + 1)) != -1) {
			iRight = str.indexOf("}", iLeft + 1);
			iNext = str.indexOf("{", iLeft);// ÏÂÃæÔÙ+£±
			iTempLevel++;
			while ((iNext = str.indexOf("{", iNext + 1)) != -1
					&& iNext < iRight) {
				iRight = str.indexOf("}", iRight + 1);
				iTempLevel++;
			}
			if (iTempLevel > iLevel) {
				iLevel = iTempLevel;
			}
			iTempLevel = 1;
		}
		return iLevel;
	}
	 	 
	
	/**
	 * Òş²Ø¶à±íÍ·ÖĞµÄÁĞ
	 * @param 
	 */
    function hideDynamicColumns(colnum,isChecked,tableId,iColLength,iTableLength) {
    	
    	var hiddenStr = "" ;
		for(var i=0;i<iColLength;i++) {
			if(document.getElementById(tableId+"_lab"+i).checked) {
				hiddenStr += "''"+","; 
			}
			else hiddenStr += "hidden"+"," ;
		}
		hiddenStr = hiddenStr.substring(0,hiddenStr.length-1) ;
	
		document.getElementById("hideColumnsStr_"+tableId).value = hiddenStr ;
		
    		if(isChecked) {
    			showcol(tableId,colnum,iTableLength,iColLength) ;
    		}else {
    			hidecol(tableId,colnum,iTableLength,iColLength)
    		}
		}
	


	/**
	 * ´´½¨Òş²ØÁĞ²Ëµ¥
	 * @param 
	 */
function createCellsMenu(tableId,field)
{
	
	var cellsMenu = document.getElementById(tableId+"_cellsMenu") ;
	/* ÒÔÏÂÉèÖÃÑùÊ½ */
	
	cellsMenu.className= "coolMenu" ;
	cellsMenu.style.visibility = "hidden";
	var hiddenStr = document.getElementById("hideColumnsStr_"+tableId).value;
	var hideColName = document.getElementById("hideColName_"+tableId).value;
	var colName = document.getElementById("colName_"+tableId).value;
	
	var hiddenArr = null ;
	var hideColNameArr = null ;
	var colNameArr = null ;
	var textHTML = "";
	var isDisable = "" ;
	var state = "" ;
	if(hiddenStr != "") {
		 hiddenArr = hiddenStr.split(",");
	}
	 if(hideColName != "") {
		 hideColNameArr = hideColName.split("`");
	}
	 if(colName != "") {
		 colNameArr = colName.split("`");
	}
	for(var i=0;i<hideColNameArr.length;i++){
		
		if(colNameArr[i] == field) {
			isDisable = "disabled=\"true\"" ;
		}else {
			isDisable = "" ;
		}
		if(hiddenStr == "") {
			state = "checked" ;
		}else {
			if(hiddenArr[i] == "hidden") {
				state = "" ;
				document.getElementById(tableId+"_col"+(i+1)).style.display="none";
			
			}else {
				
				state = "checked" ;
				document.getElementById(tableId+"_col"+(i+1)).style.display="";
			}
		}

		if(hideColNameArr[i] == "") {
			textHTML += "<div><input style=\"display:none\" "+isDisable+" class=\"\" type=\"checkbox\" "+state+" id=\""+tableId+"_lab"+i+"\" onclick=\"hideColumns('"+tableId+"_col"+(i+1)+"',"+hideColNameArr.length+",'"+tableId+"');\"><label class=\"coolMenuItem\" for=\""+tableId+"_lab"+i+"\" style='display:none;padding-top: 1px;color:black' onMouseOver=\"this.style.background='#E4E8EF';\" onMouseOut=\"this.style.background='#B0C4DE';\">"+hideColNameArr[i]+"</label><div>";
		
		}else {
			textHTML += "<div nowrap=\"nowrap\"><input style=\"\" "+isDisable+" class=\"\" type=\"checkbox\" "+state+" id=\""+tableId+"_lab"+i+"\" onclick=\"hideColumns('"+tableId+"_col"+(i+1)+"',"+hideColNameArr.length+",'"+tableId+"');\"><label class=\"coolMenuItem\" for=\""+tableId+"_lab"+i+"\" style='padding-top: 1px;color:black' onMouseOver=\"this.style.background='#E4E8EF';\" onMouseOut=\"this.style.background='#B0C4DE';\">"+hideColNameArr[i]+"</label><div>";
		}
	}
	
     cellsMenu.innerHTML = textHTML;	
}
	

	/**
	 * ÁĞ²Ëµ¥µã»÷ÊÂ¼ş
	 * @param 
	 */
function columnImgClick(tableId,field,numberCol,tableHead,isOrderBy,fixColNum,isFixedCol)
{
		
 var obj = document.getElementById(tableId+"_objHeadMenu") ;
 createMenu(tableId,field,obj,numberCol,tableHead,isOrderBy,fixColNum,isFixedCol) ;
 var ev = window.event;
 var el = ev.srcElement;
 
  showMenu(ev.clientX, ev.clientY,obj);
  ev.cancelBubble = true;
  ev.returnValue = false;
  var ep = el.parentElement;
  columnRC = ep.cellIndex;

}

	/**
	 * Òş²ØÁĞ
	 * @param 
	 */
function hideColumns(col,nums,tableId) {
	
	
	var hiddenStr = "" ;
	for(var i=0;i<nums;i++) {
		if(document.getElementById(tableId+"_lab"+i).checked) {
			hiddenStr += "''"+","; 
		}
		else hiddenStr += "hidden"+"," ;
	}
	hiddenStr = hiddenStr.substring(0,hiddenStr.length-1) ;
	
	document.getElementById("hideColumnsStr_"+tableId).value = hiddenStr ;
	
	var colObj = document.getElementById(col) ;
	if( colObj.style.display == "") 
	 colObj.style.display = "none" ;
	else colObj.style.display = "" ;
}


	/**
	 * ÁĞÒş²Ø²Ëµ¥µ¯³öÊÂ¼ş
	 * @param 
	 */
function mouseMoveDiv(field,tableId,tableHead)
{
	
  if(tableHead != "") {
  	createDynamicCellsMenu(tableId,field,tableHead) ;
  }else {
  	createCellsMenu(tableId,field);
  }
  
	
  var ev = window.event;
  var el = ev.srcElement;
  showHiddenMenu(el.parentElement.offsetLeft+el.offsetWidth,el.offsetTop+el.parentElement.offsetTop,tableId);
  ev.cancelBubble = true;
  ev.returnValue = false;
  var ep = el.parentElement;
  columnRC = ep.cellIndex;
}

	/**
	 * ÏÔÊ¾ÁĞ¸½¼Ó¹¦ÄÜ²Ëµ¥
	 * @param 
	 */
function showHiddenMenu(x, y,tableId)
{
	
	var cellsMenu = document.getElementById(tableId+"_cellsMenu") ;

	cellsMenu.style.left = x;
    cellsMenu.style.top = y ;
    cellsMenu.style.zIndex = 50;
 
 cellsMenu.style.filter = "blendTrans(duration=0.50) progid:DXImageTransform.Microsoft.Shadow(color=#323232, direction=135, strength=3)";
 
 if (cellsMenu.filters.blendTrans.status != 2)
 {
  cellsMenu.filters.blendTrans.apply();
  cellsMenu.style.visibility = "visible";
  cellsMenu.filters.blendTrans.play();
 }
}


function MouseOverColor(obj) {
	old_bg=obj.bgColor;  
  if(old_bg==selectColor) 
  obj.bgColor=selectColor;  
  else 
  obj.bgColor="#E4E8EF";  
}


function hideMenus()
{
//	var hideDivId = document.getElementById("displayTemp").value;
//	document.getElementById(hideDivId).style.display = "none" ;
	
	var tableId = tableIds.split(",") ;
	
	
	for(var i=0;i<tableId.length;i++) {
		
		var objHeadMenu = document.getElementById(tableId[i]+"_objHeadMenu") ;
		
		try {
		hideCellsMenu(tableId[i]);
		
		 objHeadMenu.style.filter = "blendTrans(duration=0.50) progid:DXImageTransform.Microsoft.Shadow(color=#323232, direction=135, strength=3)";
 		if (objHeadMenu.filters.blendTrans.status != 2)
 		{
  			objHeadMenu.filters.blendTrans.apply();
  			objHeadMenu.style.visibility = "hidden";
  			objHeadMenu.filters.blendTrans.play();
 		}		
	}catch(e){}
	}
	
}


function hideMenu(tableId)
{

		var objHeadMenu = document.getElementById(tableId+"_objHeadMenu") ;
		try {
		hideCellsMenu(tableId);
		}catch(e){}
	
		 objHeadMenu.style.filter = "blendTrans(duration=0.50) progid:DXImageTransform.Microsoft.Shadow(color=#323232, direction=135, strength=3)";
 		if (objHeadMenu.filters.blendTrans.status != 2)
 		{
  			objHeadMenu.filters.blendTrans.apply();
  			objHeadMenu.style.visibility = "hidden";
  			objHeadMenu.filters.blendTrans.play();
 		}		
}

function hideCellsMenu(tableId)
{
/* cellsMenu.style.filter = "blendTrans(duration=0.50) progid:DXImageTransform.Microsoft.Shadow(color=#323232, direction=135, strength=3)";
 if (cellsMenu.filters.blendTrans.status != 2)
 {
  cellsMenu.filters.blendTrans.apply();*/
	var cellsMenu = document.getElementById(tableId+"_cellsMenu") ;
    cellsMenu.style.visibility = "hidden";
/*  cellsMenu.filters.blendTrans.play();
 }*/
}

function showMenu(x, y,obj)
{
	try {
		hideCellsMenu() ;
	}catch(e){}
    var intRightEdge = window.document.body.clientWidth - x;
    var intBottomEdge = window.document.body.clientHeight - y;
    var intScrollLeft = window.document.body.scrollLeft + x;
    var intScrollTop = window.document.body.scrollTop + y;

    if (intRightEdge < obj.offsetWidth)
        obj.style.left = intScrollLeft - obj.offsetWidth;
    else
        obj.style.left = intScrollLeft;

    if (intBottomEdge < obj.offsetHeight)
        obj.style.top = intScrollTop - obj.offsetHeight;
    else
        obj.style.top = intScrollTop;

    obj.style.zIndex = 50;
 
 obj.style.filter = "blendTrans(duration=0.50) progid:DXImageTransform.Microsoft.Shadow(color=#323232, direction=135, strength=3)";
 
 if (obj.filters.blendTrans.status != 2)
 {
  obj.filters.blendTrans.apply();
  obj.style.visibility = "visible";
  obj.filters.blendTrans.play();
 }
}


//´´½¨ÁĞÍ·¸½¼Ó¹¦ÄÜ²Ëµ¥
/*function columnImgClick(tableId,field)
{
	 var ev = window.event;
	var el = ev.srcElement;
	var colHeadMenu = document.createElement("div");
	colHeadMenu.id = "colHeadMenu" ;

	try {
		hideCellsMenu() ;
	}catch(e){}
	
	var intRightEdge = window.document.body.clientWidth - ev.clientX;
    var intBottomEdge = window.document.body.clientHeight - ev.clientY;
    var intScrollLeft = window.document.body.scrollLeft + ev.clientX;
    var intScrollTop = window.document.body.scrollTop +  ev.clientY;

    if (intRightEdge < colHeadMenu.offsetWidth)
        colHeadMenu.style.left = intScrollLeft - colHeadMenu.offsetWidth;
    else
        colHeadMenu.style.left = intScrollLeft;

    if (intBottomEdge < colHeadMenu.offsetHeight)
        colHeadMenu.style.top = intScrollTop - colHeadMenu.offsetHeight;
    else
        colHeadMenu.style.top = intScrollTop;

	colHeadMenu.style.position = "absolute";
	colHeadMenu.style.width = "90px";
	colHeadMenu.style.height = "13px";
	colHeadMenu.style.visibility = "hidden" ;
	colHeadMenu.className = "coolMenu";
	colHeadMenu.style.zIndex = 50;
	
  var txtHTML = "<div  class=\"coolMenuItem\" style='padding-top: 1px;' onMouseOver=\"this.style.background='#E4E8EF'\"    onMouseOut=\"this.style.background='#B0C4DE'\" onclick=\"addOrderby_CH_"+tableId+"('"+field+"','ASC');\">¡ü×·¼ÓÉıĞò</div>";
  txtHTML += "<div class=\"coolMenuItem\" style='padding-top: 1px;' onMouseOver=\"this.style.background='#E4E8EF'\"    onMouseOut=\"this.style.background='#B0C4DE'\" onclick=\"addOrderby_CH_"+tableId+"('"+field+"','DESC');\">¡ı×·¼Ó½µĞò</div>";
  txtHTML +="<div class=\"coolMenuDivider\"></div>";
  txtHTML += "<div class=\"coolMenuItem\" style='padding-top: 1px;' onMouseOver=\"mouseMoveDiv('"+field+"');this.style.background='#E4E8EF'\" onMouseOut=\"hideCellsMenu();this.style.background='#B0C4DE'\" onclick=\"mouseMoveDiv('"+field+"');\" >&nbsp;&nbsp;Òş²ØÁĞ&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;¡ú </div>";
  txtHTML += "<div class=\"coolMenuDivider\"></div>";
  txtHTML += "<div class=\"coolMenuItem\" style='padding-top: 1px;' onMouseOver=\"this.style.background='#E4E8EF'\" onMouseOut=\"this.style.background='#B0C4DE'\" onClick=\"createPromptDiv('"+tableId+"PromptDiv','¹Ø¼ü×Ö','"+tableId+"','"+field+"');createShieldDiv()\" onMouseOver=\"\">&nbsp;&nbsp;¹Ø¼ü×Ö</div>";

     colHeadMenu.innerHTML = txtHTML;
	  document.body.appendChild(colHeadMenu);
	 colHeadMenu.style.filter = "blendTrans(duration=0.50) progid:DXImageTransform.Microsoft.Shadow(color=#323232, direction=135, strength=3)";
 if (colHeadMenu.filters.blendTrans.status != 2)
 {
  colHeadMenu.filters.blendTrans.apply();
  colHeadMenu.style.visibility = "visible";
  colHeadMenu.filters.blendTrans.play();
}
  ev.cancelBubble = true;
  ev.returnValue = false;
  var ep = el.parentElement;
  columnRC = ep.cellIndex;
}*/


// ´´½¨ÌáÊ¾´°¿Ú²ã
function createPromptDiv(id, desc,tableId,field) {
	
	try {
	hideMenu(tableId) ;
	hideCellsMenu(tableId);
	}catch(e){}
	//»ñÈ¡DIV²ãÏÔÊ¾×ø±ê
	var ev = window.event;
 	var el = ev.srcElement;
	
	var promptDiv = document.createElement("div");
	promptDiv.id = id;
	/* ÒÔÏÂÉèÖÃÑùÊ½ */
	promptDiv.style.position = "absolute";
	promptDiv.style.left = el.parentElement.offsetLeft ;
	promptDiv.style.top = el.parentElement.offsetTop ;
	promptDiv.style.width = "150px";
	promptDiv.style.height = "80px";
	promptDiv.style.border = "1px solid #006600";
	promptDiv.style.background = "#B0C4DE";

	/* ÒÔÏÂÉèÖÃÄÚÈİ */
	promptDiv.innerHTML = "<div style='width:100px;height:10px;background:#B0C4DE;font-family: ËÎÌå;font-size:12px;margin:5px 10px 5px 10px;'>"
			+ desc + "</div>";
	promptDiv.innerHTML += "<div align='center' style='width:150px;height:30px;background:#B0C4DE'><input type='text' id=\""+tableId+"_promptInput\" onkeydown=\"if(event.keyCode==13) keyWordFilter('"+tableId+"_promptInput','"+id+"','"+field+"','"+tableId+"');\" name='promptInput' style='width:94%;height:20px'></div>";
	promptDiv.innerHTML += "<div align='center' style='width:100px;height:10px;background:#B0C4DE'><input type='button' class=\"flyBT\" value='È·¶¨' onclick=\"keyWordFilter('"+tableId+"_promptInput','"+id+"','"+field+"','"+tableId+"');\">&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' class=\"flyBT\" value='È¡Ïû' onclick='removeShieldDiv(\""+id+"\");'></div>";
	promptDiv.style.zIndex = 3;
	
	document.body.appendChild(promptDiv);
	document.getElementById(tableId+"_promptInput").focus();
}


// ´´½¨ÌáÊ¾´°¿Ú²ã
function createNumDiv(id, desc,tableId,field) {
	
	try {
	hideMenu(tableId) ;
	hideCellsMenu(tableId);
	}catch(e){}
	//»ñÈ¡DIV²ãÏÔÊ¾×ø±ê
	var ev = window.event;
 	var el = ev.srcElement;
	
	var numDiv = document.createElement("div");
	numDiv.id = id;
	/* ÒÔÏÂÉèÖÃÑùÊ½ */
	numDiv.style.position = "absolute";
	numDiv.style.left = el.parentElement.offsetLeft ;
	numDiv.style.top = el.parentElement.offsetTop ;
	numDiv.style.width = "200px";
	numDiv.style.height = "80px";
	numDiv.style.border = "1px solid #006600";
	numDiv.style.background = "#B0C4DE";
	
	

	/* ÒÔÏÂÉèÖÃÄÚÈİ */
	numDiv.innerHTML = "<div style='width:100px;height:10px;background:#B0C4DE;font-family: ËÎÌå;font-size:12px;margin:5px 10px 5px 10px;'>"
			+ desc + "</div>";
	numDiv.innerHTML += "<div align='center' style='width:200px;height:30px;background:#B0C4DE'>" +
			"<input type='text' id=\""+tableId+"_numberInput1\" onkeydown=\"if(event.keyCode==13) numberFilter('"+tableId+"_numberInput1','"+id+"','"+field+"','"+tableId+"');\" name='_numberInput1' style='width:45%;height:20px'>--" +
			"<input type='text' id=\""+tableId+"_numberInput2\" onkeydown=\"if(event.keyCode==13) numberFilter('"+tableId+"_numberInput1','"+id+"','"+field+"','"+tableId+"');\" name='_numberInput2' style='width:45%;height:20px'>" +
			"</div>";
	numDiv.innerHTML += "<div align='center' style='width:100px;height:10px;background:#B0C4DE'><input type='button' class=\"flyBT\" value='È·¶¨' onclick=\"numberFilter('"+tableId+"_numberInput1','"+id+"','"+field+"','"+tableId+"');\">&nbsp;&nbsp;&nbsp;&nbsp;<input type='button' class=\"flyBT\" value='È¡Ïû' onclick='removeShieldDiv(\""+id+"\");'></div>";
	numDiv.style.zIndex = 3;
	
	
	document.body.appendChild(numDiv);
	document.getElementById(tableId+"_numberInput1").focus();
}



	/*	out.println("function onColumnsConfirm(keyWordInput,id,field) {");
		out.println("		var keyWord = document.getElementById(keyWordInput).value ;");
		out.println("		keyWord = field+\"@\"+keyWord");
				
		out.println("changeGrid_CH_"+pp.getTableID()+"_1('sqlWhereColumns',keyWord);");
				
	 	out.println("removeShieldDiv(id);");
				
		out.println("}");*/	


function keyWordFilter(keyWordInput,id,field,tableId) {
	
	var keyWord = document.getElementById(keyWordInput).value ;
	keyWord = field+"`"+keyWord ;
	eval("changeGrid_CH_"+tableId+"('sqlWhereColumns',keyWord);");
	removeShieldDiv(id);
	
}

function cancelFilter(tableId) {
	
  eval("changeGrid_CH_"+tableId+"('cancelFilter','');");
	
}

function numberFilter(keyWordInput,id,field,tableId) {
	var num1 = document.getElementById(tableId+"_numberInput1").value ;
	var num2 = document.getElementById(tableId+"_numberInput2").value ;
	
	if(num1 == "" || num2 == "") {
		alert("½ğ¶î²»ÄÜÎª¿Õ!!");
		return ;
	}
	
	
	var patten = /^(-?\d+)(\.\d+)?$/; 
	if(!patten.exec(num1) || !patten.exec(num2)) {
		alert("½ğ¶îÖ»ÄÜÎªÊı×Ö!") ;
		return ;
	}

	keyWord = field+"`"+num1+"`"+num2 ;
	eval("changeGrid_CH_"+tableId+"('sqlWhereNumColumns',keyWord);");
	removeShieldDiv(id);
	
}



//´´½¨ÆÁ±Î²ã
function createShieldDiv(){
var shieldDiv = document.createElement("div");
shieldDiv.id = "shieldDivId";

/*ÒÔÏÂÉèÖÃÑùÊ½*/
shieldDiv.style.position = "absolute";
shieldDiv.style.left = "0px";
shieldDiv.style.top = "0px";
shieldDiv.style.width = document.body.offsetWidth>window.screen.width?document.body.offsetWidth:window.screen.width;
shieldDiv.style.height = Math.max(document.body.offsetHeight, window.screen.height);
//alert(document.body.clientHeight);
//alert(document.body.offsetHeight);
//alert(document.body.scrollHeight);

shieldDiv.style.background = "#EAEAEA";
shieldDiv.style.filter = "Alpha(opacity=\"50\")";
shieldDiv.style.zIndex = 2;


//´´½¨Ò»¸öiframe²ã,ÓÃÓÚµ²×¡Ò³ÃæÉÏ¿ÉÄÜ³öÏÖµÄSELECT¿ò
/*var shielFrame = document.createElement("iframe") ;
shielFrame.style.position = "absolute";
shielFrame.style.left = "0px";
shielFrame.style.top = "0px";
shielFrame.style.width = document.body.offsetWidth>window.screen.width?document.body.offsetWidth:window.screen.width;
shielFrame.style.height = document.body.offsetHeight>window.screen.height?document.body.offsetHeight:window.screen.height;
shielFrame.style.background = "#EAEAEA";
shielFrame.style.scrolling="no" ;
shielFrame.style.filter = "Alpha(opacity=\"50\")";
shielFrame.style.zIndex = -1;
shielFrame.frameborder = "0" ;

ÒÔÉÏÉèÖÃÑùÊ½
document.body.appendChild(shielFrame);*/
document.body.appendChild(shieldDiv);
return shieldDiv;
}

function removeShieldDiv(id) {
	var shieldDiv = document.getElementById("shieldDivId") ;
	shieldDiv.removeNode(true) ;
	var promptDiv = document.getElementById(id);
	promptDiv.removeNode(true) ;
}


function fixCol(col,tableId) { 
   var fixedColObj = document.getElementById("fixColNum_"+tableId);
   if(col == -1){
      var fixedColNum = fixedColObj.value ;                                
     	   if(fixedColNum > 0)       {                               
     		 col = Number(fixedColNum);    }                                 
     		 else{ return ;               }                                 
      }                                                                    
	var rows = document.getElementById(tableId).rows; 
	for(var i=0; i < rows.length; i++) { 									
		//Èç¹ûÊÇÊı¾İÁĞ,¾Í½øĞĞ¹Ì¶¨ 												
		if(rows[i].dataRow) {												
			var cells = rows[i].cells;										
			for(var j=0; j < cells.length; j++) {							
				//Èç¹ûÊÇµ±Ç°µã»÷ÁĞÇ°ÃæµÄÁĞ 										
				if(j <= col) { 												
					//ÉèÖÃÎª¹Ì¶¨ÁĞ 											
					cells[j].className = "fixedCol"; 						
				} else { 													
					cells[j].className = ""; 								
				} //end else~if												
			} //end for 													
		} //end if 															
	} //end for 															
	fixedColObj.value = col ;     						                
} //end function 


function fixedDynamicCol(tableid,col,tableHead) { 
	
	col = Number(col);
	
	var fixedColObj = document.getElementById("fixColNum_"+tableid) ;
//	alert(fixedColObj.value);

	if(col == -1) {
		
		var fixedColNum = fixedColObj.value ;
		if(fixedColNum > 0) {
			col = Number(fixedColNum) ;
		}else {
			return ;
		}

	}
	
	
	var k = getFixedArr(tableHead);
	
	col = k[col-1] ;
	

	
    var otable = document.getElementById(tableid);

	var rows = otable.rows ;
	var iTableLength = otable.rows.length ;
	var tableArr = tableHead.split(",");
	var iColLength = tableArr.length ;
	var a = showrelation(tableid,iTableLength,iColLength) ;
	for(var i=0;i<a.length;i++) {

		var cells = rows[i].cells ;
		
		for(var j=0;j<a[i].length;j++) {
			if(j<col) {
				a[i][j].cell.className = "fixedCol" ;
			}else {
				a[i][j].cell.className = "" ;
			}
		}
	}
	
	fixedColObj.value = col ;

}

/*
 * 
 * ¸ù¾İTABLEHEADËã³ö¾ßÌåËø¶¨µÄÁĞÊı
 */

function getFixedArr(tableHead) {
	
	if( tableHead != "") {

	 //ÒÔÏÂ½âÎö¸´ÔÓ±íÍ·×Ö·û´®
	 //K´ú±í°üº¬×Ó±íÍ·µÄ¸öÊı
	 var tableArr = tableHead.split(",");
     var fixedArr = new Array(tableArr.length) ;
	
     for(var i=0;i<tableArr.length;i++) {
		if(tableArr[i].indexOf("{") >-1) {
			for(var j=i+1;j<tableArr.length;j++){
				if(tableArr[j].indexOf("}") >-1) {
					fixedArr[i] = (j+1);
					fixedArr[j] = (j+1);
					break ;
				}
			}
			for(var k=i+1;k<j;k++) {
				fixedArr[k] = j+1 ;
			}
			i = j ;
		}else {
			fixedArr[i] = i+1 ;
		}
	}
	
	return fixedArr ;
}
}

function hidecol(tableid,colnum,iTableLength,iColLength){
	

	var oTable = document.getElementById(tableid) ;
	var colObject=document.getElementById(tableid+"_col"+colnum);
	if (colObject==null){	
		return;
	}
	if (colObject.style.display == "none"){
		return;
	}
	
	var trObj=null,cell=null;
	var a=showrelation(tableid,iTableLength,iColLength);
	var colstart=-1;

	colstart= a[iTableLength-1][Number(colnum)-1].colstart;

	for (i=iTableLength-2; i >= 0; i--) {
		
		cell=a[i][Number(colnum)-1].cell;
		if (i>0 && a[i-1][Number(colnum)-1].cell == cell){
			cell=null;
		}else{
			//alert("colstart="+colstart+"|a[i][Number(colnum)-1].colstart="+a[i][Number(colnum)-1].colstart);
			if ( colstart != a[i][Number(colnum)-1].colstart){
				//Òş²ØÒâÎ¶×ÅcolspanÒª¼õ1
				var p=getColSpan(cell);
				if ( p>1){
					setColSpan(cell,p-1);
				}
				cell=null;
			}
		}
	
		if (cell){
			trObj=oTable.rows(i);

			if ( getColSpan(cell)>1){
				//±È½ÏÂé·³£¬Òª²¹³äµ¥Ôª¸ñ½øÈ¥
				var mycolSpan=getColSpan(cell);
				if (mycolSpan>1){
					getRealColSpan(cell);
					cell.colSpan =1;
					setColSpan(cell,1);
	
					var objTd=null;
					objTd=trObj.insertCell(cell.cellIndex+1);
					objTd.setAttribute("isInsert",true);
			
					objTd.colSpan=mycolSpan-1;
					setColSpan(objTd,mycolSpan-1);

					objTd.rowSpan=cell.rowSpan;
					objTd.innerHTML=cell.innerHTML;
					objTd.align = "center" ;

					//ÒòÎªºóÃæµÄÁĞÓĞ¿ÉÄÜÒÑ¾­±»Òş²ØÁË£¬ËùÒÔ±ØĞë²¹Ò»Ğ©ÁĞ
					for(j=Number(colnum)+1;j<iColLength;j++){
						if (document.getElementById(tableid+"_col"+j).style.display=="none"){
							objTd=trObj.insertCell(cell.cellIndex+1);
							objTd.setAttribute("isInsert",true);
						}else{
							break;
						}
					}

				}
			}
			
		}
	}

	//Òş²Ø°É
	colObject.style.display = "none" ;

}

function getRealColSpan(cell){
	var p=1;
	if (cell){
		if (cell.RealColSpan){
			p=cell.RealColSpan;
		}else{
			p=cell.colSpan;
			setRealColSpan(cell);
		}
	}
	return p;
}
function setRealColSpan(cell){
	if (cell)
		cell.RealColSpan=cell.colSpan;
	
}

function getColSpan(cell){
	var p=1;
	if (cell){
		if (cell.myColSpan){
			p=cell.myColSpan;
		}else{
			p=cell.colSpan;
			setColSpan(cell);
		}
	}
	return p;
}

function setColSpan(cell,p){
	if (cell)
		cell.myColSpan=p;
	
}

//ÏÔÊ¾Ö¸¶¨±íÁĞ
function showcol(tableid,colnum,iTableLength,iColLength){
	
	var oTable = document.getElementById(tableid) ;
	
	var colObject=document.getElementById(tableid+"_col"+colnum);
	if (colObject==null){	
		return;
	}
	if (colObject.style.display == ""){
		return;
	}
		
	var trObj=null,cell=null;

	var a=showrelation(tableid,iTableLength,iColLength);

	var colstart=-1;
	colstart= a[iTableLength-1][Number(colnum)-1].colstart;

	for (i=iTableLength-2; i >= 0; i--) {
		
		cell=a[i][Number(colnum)-1].cell;
		if (i>0 && a[i-1][Number(colnum)-1].cell == cell){
			cell=null;
		}else{
			//alert('²»ÊÇÁĞÍ·µÄÉÏ¼¶,Ê²Ã´¶¼²»ÓÃ×ö');
			//²»ÊÇÁĞÍ·µÄÉÏ¼¶,Ê²Ã´¶¼²»ÓÃ×ö
			//alert("i="+i+"|colstart="+colstart+"|a[i][Number(colnum)-1].colstart="+a[i][Number(colnum)-1].colstart);
			if ( colstart != a[i][Number(colnum)-1].colstart){
				//alert("²»ÊÇÁĞÍ·µÄÉÏ¼¶:i="+i);
				//ÏÔÊ¾ÒâÎ¶×ÅcolspanÒª¼Ó1
				var p=getColSpan(cell);
				if (p==1){
					setColSpan(cell,p+1);
				}
				cell=null;
			}
		}
		
		if (cell){
			trObj=oTable.rows(i);
			var isInsert = cell.getAttribute("isInsert");
			
			//alert("ÊÇÁĞÍ·µÄÉÏ¼¶£ºi="+i+"|isInsert="+isInsert);

			if(isInsert) {

				//alert('ÊÇÎÒÃÇ²åÈëµÄÁĞÍ·µÄÉÏ¼¶');
				//ÊÇÎÒÃÇ²åÈëµÄ
				//
				//Ïò×ó±ßÕÒÏÔÊ¾µÄ(½ØÖ¹µ½Ô­×°),Èç¹ûÓĞÏÔÊ¾µÄ,°Ñ×Ô¼º¸Éµô,ÏÔÊ¾µÄÄÇ¸ö¸ñ×Ó+1;
				//Èç¹û×ó±ßÃ»ÓĞÏÔÊ¾,ÏòÓÒ±ßÕÒ,ÕÒµ½ÓĞÏÔÊ¾µÄ,°ÑÓÒ±ß¸Éµô,×Ô¼º+ÓÒ±ßµÄcolspn;
				//Èç¹û×óÓÒ¶¼Ã»ÓĞ,Ö±½ÓÏÔÊ¾;
				var bLeftFound=false,leftcell=null,bRightFound=false,rightcell=null;
				for (var j=Number(colnum)-1;j>=colstart;j--){
					if(document.getElementById(tableid+"_col"+j).style.display=="") {
						//ÓĞÏÔÊ¾µÄ
						bLeftFound=true;
						leftcell=a[i][j-1].cell;
						break;
					}
				}
				if (bLeftFound){
					//Ïò×ó±ßÕÒÏÔÊ¾µÄ(½ØÖ¹µ½Ô­×°),Èç¹ûÓĞÏÔÊ¾µÄ,°Ñ×Ô¼º¸Éµô,ÏÔÊ¾µÄÄÇ¸ö¸ñ×Ó+1;
					leftcell.colSpan=getColSpan(leftcell)+1;
					setColSpan(leftcell,leftcell.colSpan);
					trObj.deleteCell(cell.cellIndex);
				}else{
					//Èç¹û×ó±ßÃ»ÓĞÏÔÊ¾,ÏòÓÒ±ßÕÒ,ÕÒµ½ÓĞÏÔÊ¾µÄ,°ÑÓÒ±ß¸Éµô,×Ô¼º+ÓÒ±ßµÄcolspn;
					for (var j=Number(colnum)+1;j<=iColLength;j++){
						/*
						alert("j="+j+"|a[i][j-1].cell.innerHTML="+a[i][j-1].cell.innerHTML
							+"|a[i][j-1].cell.isInsert="+a[i][j-1].cell.isInsert);
						*/
						if (!a[i][j-1].cell.isInsert){
							//Óöµ½²»ÊÇ×Ô¼º²åµÄ¾ÍÍË³ö
							break;
						}
						if(document.getElementById(tableid+"_col"+j).style.display=="" ) {
							//ÓĞÏÔÊ¾µÄ
							bRightFound=true;
							rightcell=a[i][j-1].cell;

							//alert(rightcell.innerHTML);
							break;
						}
					}
					if (bRightFound){
						//alert("°ÑÓÒ±ß¸Éµô,×Ô¼º+ÓÒ±ßµÄcolspn;");
						//°ÑÓÒ±ß¸Éµô,×Ô¼º+ÓÒ±ßµÄcolspn;
						cell.colSpan=getColSpan(cell)+getColSpan(rightcell);
						setColSpan(cell,cell.colSpan);
						trObj.deleteCell(rightcell.cellIndex);
					}
				}
			}else {
				//Ô­×°
				//

				//alert('ÊÇÔ­×°');

				var iMyInsertColCount=0,precell=null,iColSpan=0;
				precell=cell;
				for (var j=Number(colnum);j<iColLength;j++){
				//	alert("j="+j+"|a[i][j].cell="+a[i][j].cell.innerHTML+"|.isInsert"+a[i][j].cell.isInsert);
					if (!a[i][j].cell.isInsert) {
						break;
					}else{
						if (precell != a[i][j].cell){
							iMyInsertColCount++;
							precell=a[i][j].cell;
						}
						//alert("iColSpan="+iColSpan);
						iColSpan++;
					}
				}
				//alert("iMyInsertColCount="+iMyInsertColCount+"|iColSpan="+iColSpan);
				
				for (var j=0;j<iMyInsertColCount;j++){
					//alert("É¾³ı£º"+trObj.cells[cell.cellIndex+1].innerHTML);
					trObj.deleteCell(cell.cellIndex+1) ;
				}
				if (cell.colSpan==1){
					cell.colSpan=1+iColSpan;
					setColSpan(cell,1+iColSpan);
				}

			}
		}
		colObject.style.display= "" ;
	}
}


//½«±í¸ñcell·­Òë³Éarray
function showrelation(tableid,iTableLength,iColLength){
	var a = new Array(iTableLength),b=new Array(iTableLength);
	for(var i=0;i<iTableLength;i++){
		a[i]=new Array(iColLength);
		b[i]=0;
	}

	var otable = document.getElementById(tableid);
	for(var i=0;i<iTableLength;i++) {
		for(var j=0;j<otable.rows(i).cells.length;j++) {
				
			var tdObj = otable.rows(i).cells(j) ;
			
			var lStartCol=-1;
			for(var m=0;m<tdObj.rowSpan;m++){
				lStartCol=b[i+m]+1;
				
				for(var n=0;n<getColSpan(tdObj);n++){
					/*
					if (mydebug && mydebug.value=="1"){
						alert("i="+i+"|j="+j+"|m="+m+"|n="+n
							+"|tdObj.rowSpan="+tdObj.rowSpan+"|tdObj.colSpan="+tdObj.colSpan
							+"|b[0]="+b[0]+"|b[1]="+b[1]+"|b[2]="+b[2]+"|tdObj.value="+tdObj

							+"\n"
							+"|a[0][0]="+a[0][0].cell+"|a[0][1]="+a[0][1].cell
							+"|a[0][2]="+a[0][2].cell+"|a[0][3]="+a[0][3].cell
							+"|a[0][4]="+a[0][4].cell+"|a[0][5]="+a[0][5].cell
							+"|a[0][6]="+a[0][6].cell+"|a[0][7]="+a[0][7].cell
							
							+"\n"
							+"|a[1][0]="+a[1][0].cell+"|a[1][1]="+a[1][1].cell
							+"|a[1][2]="+a[1][2].cell+"|a[1][3]="+a[1][3].cell
							+"|a[1][4]="+a[1][4].cell+"|a[1][5]="+a[1][5].cell
							+"|a[1][6]="+a[1][6].cell+"|a[1][7]="+a[1][7].cell

							+"\n"
							+"|a[2][0]="+a[2][0].cell+"|a[2][1]="+a[2][1].cell
							+"|a[2][2]="+a[2][2].cell+"|a[2][3]="+a[2][3].cell
							+"|a[2][4]="+a[2][4].cell+"|a[2][5]="+a[2][5].cell
							+"|a[2][6]="+a[2][6].cell+"|a[2][7]="+a[2][7].cell
							

						);
					}
					*/
					a[i+m][b[i+m]]=	{value:tdObj.innerHTML,cell:tdObj,colstart:lStartCol};
					b[i+m]=b[i+m]+1;
				}	
				if (getColSpan(tdObj)<tdObj.colSpan){
					var myInsertCount=0,mylastcell=null,mylastcolstart=-1;
					for (var n=j+1;n<otable.rows(i).cells.length;n++){
						if (otable.rows(i).cells(n).isInsert){
							myInsertCount++;
							//¼ÌĞø×·¼Ó
							mylastcell=otable.rows(i).cells(n);
							mylastcolstart=b[i+m];
							a[i+m][b[i+m]]=	{value:tdObj.innerHTML,cell:mylastcell,colstart:mylastcolstart};
							b[i+m]=b[i+m]+1;								
							
							j++;
						}else{
							break;
						}
					}
					if (mylastcell==null){
						mylastcell=tdObj;
						mylastcolstart=lStartCol;
					}
					/*
					if (mydebug && mydebug.value=="1"){
						alert("ÅĞ¶Ï:getColSpan(tdObj)+myInsertCount="+ (getColSpan(tdObj)+myInsertCount)
							+"|tdObj.colSpan="+tdObj.colSpan+"|realColspan"+getRealColSpan(tdObj));
					}*/
					for (var n=getColSpan(tdObj)+myInsertCount;n<getRealColSpan(tdObj);n++){
					/*	if (mydebug && mydebug.value=="1"){
							alert("²¹³ä:n="+n+"|b[i+m]="+b[i+m]+"|value="+mylastcell.innerHTML);
						}*/
						a[i+m][b[i+m]]=	{value:mylastcell.innerHTML,cell:mylastcell,colstart:mylastcolstart};
						b[i+m]=b[i+m]+1;
					}
				}
			}
		}
	}
	return a;
}







function extDatagridSearch(tableId, limitValue)
{
	
  var sv='';
  for(var i=0;i<eval('sqlWhereVariables_'+tableId).length;i++){
   	var oTT=document.getElementById(eval('sqlWhereVariables_'+tableId)[i]);
  		if(!oTT)continue;
   		sv=sv+"<"+eval('sqlWhereVariables_'+tableId)[i]+">"+oTT.value+"</"+eval('sqlWhereVariables_'+tableId)[i]+">";
  }
  	eval("getData_"+tableId+"(sv,document.getElementById(\"page_xml_\"+tableId).value,0,limitValue)") ;
}

function extMoneyFormat(val) {

	if(val < 0){
		val = '<span style="color:red" onselectstart="return false">' + Ext.util.Format.number(val,'0,000.00') + "</span>";
	} else {
		val =  Ext.util.Format.number(val,'0,000.00');
	}
	
	return val;
}

function expExcel(tableId) {
	window.open(MATECH_SYSTEM_WEB_ROOT + '/common.do?method=expExcel&tableId='+tableId);
}


function getPrintData(tableId,action) {
	
	var printContainer = Ext.get("printContainer_"+tableId).dom ;
	Ext.Ajax.request({
		method:'POST',
		url:MATECH_SYSTEM_WEB_ROOT+'/extGridPrint?tableId='+tableId ,
		success:function (response,options) {
			printContainer.innerHTML = "";
			printContainer.innerHTML = response.responseText;
		},
		failure:function (response,options) {
			alert("´òÓ¡²ÎÊıÉèÖÃ´íÎó");
		}
	});
}

function getPrintParam(tableId) {
	var queryString = "";
	queryString += "&printTitleRows=" + document.getElementById("printTitleRows_" + tableId).value;
	queryString += "&printSql=" + document.getElementById("printSql_" + tableId).value;
	queryString += "&printDisplayColName=" + document.getElementById("printDisplayColName_" + tableId).value;
	queryString += "&printAction=" + document.getElementById("printAction_" + tableId).value;
	queryString += "&printTitle=" + document.getElementById("printTitle_" + tableId).value;
	queryString += "&printCharColumn=" + document.getElementById("printCharColumn_" + tableId).value;
	queryString += "&printColumnWidth=" + document.getElementById("printColumnWidth_" + tableId).value;
	queryString += "&printVerTical=" + document.getElementById("printVerTical_" + tableId).value;
	queryString += "&printCustomerId=" + document.getElementById("printCustomerId_" + tableId).value;
	queryString += "&printColName=" + document.getElementById("printColName_" + tableId).value;
	queryString += "&printTableHead=" + document.getElementById("printTableHead_" + tableId).value;
	queryString += "&printAllCount=" + document.getElementById("printAllCount_" + tableId).value;
	queryString += "&printGroupName=" + document.getElementById("printGroupName_" + tableId).value;
	queryString += "&printPoms=" + document.getElementById("printPoms_" + tableId).value;
	
	return encodeURI(queryString);
}

//ĞĞµ¥Ñ¡
function setChooseValue(obj,tableId) {
	var chooseValue = document.getElementById("chooseValue_"+tableId) ;
	chooseValue.value = obj.value ;
}

//¶àÑ¡
function getChooseValue(tableId) {
	var chooseValue = document.getElementsByName("choose_"+tableId) ;
	var str = "";
	for(var i=0;i<chooseValue.length;i++) {
		if(chooseValue[i].checked && chooseValue[i].value != "") {
			str += chooseValue[i].value + "," ;
		}	
	}
	
	if(str != "") {
		str = str.substr(0,str.length-1);
	}
	
	return str ;
}

function selectAllChooseValue(obj,tableId) {
	var chooseValue = document.getElementsByName("choose_"+tableId) ;
	for(var i=0;i<chooseValue.length;i++) {
		if(!chooseValue[i].disabled) {
			chooseValue[i].checked = obj.checked ;	
		}
	}
}

//´´½¨¼ÆËãÆ÷
var calWin = null;
function createcalculater(tableId) {
 	
 	var divObj = document.getElementById("calculater");
 	if(!divObj) {
 		divObj = document.createElement("<DIV id=\"calculater\" style=\"position:absolute;width:expression(document.body.clientWidth);height:20;left:0;bottom:45;padding:10 0 10 0;\"></div>") ;
 		document.body.insertBefore(divObj,document.body.firstChild);
 		
		if(!calWin) {
		    calWin = new Ext.Window({
		     title: '¼ÆËãÆ÷',
		     renderTo :'calculater',
		     width: document.body.clientWidth,
		     height:20,
		        closeAction:'hide',
		        listeners   : {
		        	'hide':{fn: function () {
						calWin.hide();	         	
		        	}}
		        },
		      layout:'fit',
			  html:'<input type="<input type="text" size="120" id="sText" onpropertychange="calculateValue()" value="" /> = <input type="text" size="30" id="sValue" value="" />'
			  		+'<button onclick="calculatorReset(\''+tableId+'\')" >ÖØÖÃ</button>'
		    });
	   }
	  
 	}
 	 calWin.show();
 }
 
String.prototype.replaceAll  = function(s1,s2){    
	return this.replace(new RegExp(s1,"gm"),s2);    
}


function calculateValue() {
	var sTextValue = sText.value;
	try {
		sTextValue =  eval(sTextValue.replaceAll(",",""));
		sValue.value = Ext.util.Format.number(sTextValue,'0,000.00') ;
	}catch(e){}
}

function calculatorReset(tableId) {
		sText.value='';
		sValue.value='';
		Ext.getCmp("gridId_"+tableId).getSelectionModel().clearSelections();
		
		Ext.getCmp("gridId_"+tableId).getSelectionModel().selectedArea='';
	}


//×Ô¶¨Òå²éÑ¯
function customQryWinFun(tableId) {
	var customQryWin = this["customQryWin_"+tableId] ;
	document.getElementById("customQry_"+tableId).style.display = "";
	
	if(customQryWin == null) { 
		customQryWin = new Ext.Window({
			title: '×Ô¶¨Òå²éÑ¯Ìõ¼ş',
			width: 600,
			height:300,
			contentEl:'customQry_'+tableId, 
	        closeAction:'hide',
	        autoScroll:true,
	        modal:true,
	        listeners:{
				'hide':{fn: function () {
					 document.getElementById("customQry_"+tableId).style.display = "none";
				}}
			},
	        layout:'fit',
		    buttons:[{
	            text:'È·¶¨',
	          	handler:function() {
	          		var qryWhere = createQryWhere(tableId);
	          		if(qryWhere == false) return ;
	          		document.getElementById("qryWhere_"+tableId).value = qryWhere ;
	          		eval("goSearch_"+tableId+"();");
	          		customQryWin.hide();
	          	}
	        },{
	            text:'È¡Ïû',
	            handler:function(){
	            	customQryWin.hide();
	            }
	        }]
	    }); 
		this["customQryWin_"+tableId] = customQryWin ;
		addQuery(tableId,true);
	}
	customQryWin.show();
}

function addQuery(tableId,first) {
	var trObj ;
	var tdObj ;
	
	var grid = Ext.getCmp("gridId_"+tableId);
	var columns = grid.getColumnModel().columns;
	
	var tbody = document.getElementById("queryTBody_"+tableId);
	trObj = tbody.insertRow();
	trObj.id = "queryTr_" + tableId;
	
	//Á¬½Ó
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	
	var display = "" ;
	if(first) {
		display = "display:none;" ;
	}
	tdObj.innerHTML = "<div class=selectDiv style=\"width:80px;"+display+"\">"
					+ "<select class=mySelect style=\"width:80px;\" name='query_logic_"+tableId+"' id='query_logic_" + tableId + "'>"
					+ "		<option value='and'>²¢ÇÒ(and)</option>"
					+ "		<option value='or'>»òÕß(or)</option>"
					+ "</select>"
					+ "</div>" ;
	
	//ÁĞÃû
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	
	var columnHtml = "<div class=selectDiv style=\"width:120px;\">"
					+ "	<select class=mySelect style=\"width:120px;\" name='query_column_" + tableId + "' id='query_column_" + tableId + "'>" ;
					
	for(var i=0;i<columns.length;i++) {
		var id = columns[i].freequery || columns[i].id ;
		var header = columns[i].header ;
		var hidden = columns[i].hidden ;
		 
		if(header != "Ñ¡" && id != "numberer" && header != "trValue" && id != "chooseValue") {
			columnHtml += "<option value='" + id + "'> " + header + " </option>" ;
		}  
	}

	columnHtml += " </select>";
	columnHtml += " </div>";
	tdObj.innerHTML = columnHtml ;
	
	//ÔËËã·û
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	
	tdObj.innerHTML = "<div class=selectDiv style=\"width:80px;\">"
	+ "	<select class=mySelect style=\"width:80px;\" name='query_operator_" + tableId + "' id='query_operator_" + tableId + "'>"
	+ "		<option value='='> µÈÓÚ(=) </option> "
	+ "		<option value='!='> ²»µÈÓÚ(!=) </option> "
	+ "		<option value='>'> ´óÓÚ(&gt;) </option> "
	+ "		<option value='<'> Ğ¡ÓÚ(&lt;) </option> "
	+ "		<option value='>='> ´óÓÚµÈÓÚ(&gt;=) </option> "
	+ "		<option value='<='> Ğ¡ÓÚµÈÓÚ(&lt;=) </option> "
	+ "		<option value='like'> °üº¬ </option> "
	+ "		<option value='not like'> ²»°üº¬ </option> "
	+ " </select>";
	+ " </div>";
	
	//ÄÚÈİ
	tdObj = trObj.insertCell();
	tdObj.align = "center";

	tdObj.innerHTML = "<input type=text id='query_condition_" + tableId + "' name='query_condition_" + tableId + "'  size='30'>";
	
	
	//²Ù×÷
	tdObj = trObj.insertCell();
	tdObj.align = "center";
	if(!first) {
		tdObj.innerHTML = "<a href='javascript:;' onclick='removeQuery(this);' ><img src=" + MATECH_SYSTEM_WEB_ROOT + "img/delete.gif></a>" ;
	}
	
}


function removeQuery(obj) {
	var tbody = obj.parentElement.parentElement.parentElement ;
	var trObj = obj.parentElement.parentElement ;
	if(trObj) {
		tbody.removeChild(trObj);
	}
}


function createQryWhere(tableId) {
	
	var query_logic = document.getElementsByName("query_logic_"+tableId) ;
	var query_column = document.getElementsByName("query_column_"+tableId) ;
	var query_operator = document.getElementsByName("query_operator_"+tableId) ;
	var query_condition = document.getElementsByName("query_condition_"+tableId) ;
	
	var qryWhere = "" ;
	for(var i=0;i<query_logic.length;i++) {
		var logic = query_logic[i].value ;
		var column = query_column[i].value ;
		var operator = query_operator[i].value ;
		var condition = query_condition[i].value ;
		
		if(column == "") {
			alert("ÇëÑ¡ÔñÁĞÃû,ÁĞÃû²»µÃÎª¿Õ!") ;
			return false ;
		}
		
		if(operator.indexOf("like") > -1) {
			if(condition != "") {
				condition = "'%" + condition + "%'" ;
			}
		}else if(isNaN(condition) || condition == "") {
			condition = "'" + condition + "'" ;
		}
		
		qryWhere += " " + logic + " " + column + " " + operator + " " + condition ;
	}
	
	return qryWhere ; 
}
/*
 * ³éÒÉµ÷ÎöÍ³Ò»Ê¹ÓÃµÄjava script·½·¨¡£
 * ÓĞ¸Ä¶¯ĞèÒª£¬¿Écopyµ½µ±Ç°Ò³Ãæ¡£¶ø²»ÒıÓÃ±¾Ò³Ãæ¡£
 * 
 *                       06.10.06    k
*/

function takeDoubt(s)
{

	var obj=document.getElementById("doubtTD"+s);
	var sign=obj.innerHTML;
      
	if(sign.indexOf("ÒÉ")>=0){
		
		var strResult = window.showModalDialog('/AuditSystem/voucherquery/DoubtfuPoint.jsp?autoid='+s+"&random=" + Math.random(),null,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no')+"";
        
        if(strResult.indexOf("ok")>=0)	{
			//obj.innerHTML="";
			refreshState2(obj,"<font color=\"red\">³·</font>");
		}
	}else if(sign.indexOf("³·")>=0){
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("POST","/AuditSystem/voucherquery/DoubtfuPointBack.jsp?autoid="+s+"&random=" + Math.random(),false);
		oBao.send();
		var noDel = unescape(oBao.responseText);
		if(noDel.indexOf("ok")>=0){
             refreshState2(obj,"ÒÉ");
		}
	}
}


function takeOutEntry(s)
{	
        var obj=document.getElementById("outA"+s);
        var sign=obj.innerHTML;

        if(sign.indexOf("³é")>=0){
        
          var strResult = "";
          try{
          	strResult=window.showModalDialog('/AuditSystem/voucherquery/takeoutPoint.jsp?autoid='+s+"&random=" + Math.random(),window,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no');
          }catch(e){
          	
          	try{ 
				var oBao = new ActiveXObject("MTOffice.WebOffice");
		
				var myhost="http:\/\/"+window.location.host;
		
				strResult = oBao.ShowjsDialog(myhost+'/AuditSystem/voucherquery/takeoutPoint.jsp?autoid='+s+"&random=" + Math.random(),'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no')+"";
		
				oBao =null;
				
			 }catch(e){alert('³ö´íÁË£º'+e);}
          }
          if(strResult && strResult.indexOf("ok")>=0){
            //obj.innerHTML="<font color=\"red\">³·</font>";
            //refreshState2(obj,"<font color=\"red\">³·</font>");
            refreshState2(obj,"<font color=\"red\">³é</font>");
          }

        }else if(sign.indexOf("³·")>=0){
          var oBao = new ActiveXObject("Microsoft.XMLHTTP");
          oBao.open("POST","/AuditSystem/voucherquery/takeoutPointBack.jsp?autoid="+s+"&random=" + Math.random(),false);
          oBao.send();
          var noDel = unescape(oBao.responseText);
           if(noDel.indexOf("ok")>=0){
           	
             refreshState2(obj,"³é");
           }
        }
}

function takeOutEntryExt(obj,s,tableId)
{		
      	var sign = obj.innerHTML ;
      	
      	 if(sign.indexOf("³é")>=0){
		      var strResult = "";
		      try{
		      	strResult=window.showModalDialog('/AuditSystem/voucherquery/takeoutPoint.jsp?tableId='+tableId+'&flag=ext&autoid='+s+"&random=" + Math.random(),window,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no');
		      }catch(e){
		      	try{ 
					var oBao = new ActiveXObject("MTOffice.WebOffice");
					var myhost="http:\/\/"+window.location.host;
					strResult = oBao.ShowjsDialog(myhost+'/AuditSystem/voucherquery/takeoutPoint.jsp?tableId='+tableId+'&flag=ext&autoid='+s+"&random=" + Math.random(),window,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no')+"";
					oBao =null;
				 }catch(e){alert('³ö´íÁË£º'+e);}
		      }
		      
		      if(strResult && strResult.indexOf("ok")>=0){
		      	refreshStateExt(tableId,"<font color=\"red\">³é</font>",s);
		      }
      	 }else if(sign.indexOf("³·")>=0){
          var oBao = new ActiveXObject("Microsoft.XMLHTTP");
          oBao.open("POST","/AuditSystem/voucherquery/takeoutPointBack.jsp?flag=ext&autoid="+s+"&random=" + Math.random(),false);
          oBao.send();
          var noDel = unescape(oBao.responseText);
           if(noDel.indexOf("ok")>=0){
           	 refreshStateExt(tableId,"³é");
           }
        }
    }

var attachWin = null; 
function setAttachExt(obj,s,tableId) {		
	var sign = obj.innerHTML ;

	if(sign.indexOf("¸½")>=0){
	    var strResult = "";
	    
	    try{
	    	
			var url="/AuditSystem/taskAttach.do?flag=ext&attachCode=" + s + "&random=" + Math.random();
			if(!attachWin) { 
			    attachWin = new Ext.Window({
			     	renderTo : Ext.getBody(),
			     	width: 600,
			     	height:420,
			     	id:'attachWin',
			     	title:'¸½¼ş',
			     	closable:'flase',
			       	closeAction:'hide', 
			      	    listeners : {
			         	'hide':{
			         		fn: function () {
			         			new BlockDiv().hidden();
								attachWin.hide();
							}
						}
			        },
			       	html:'<iframe name="attachFrame" id="attachFrame" scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>',
			       	layout:'fit'
			    });
			   } else {
			   	document.getElementById("attachFrame").src = url;
			   }
			new BlockDiv().show();
			attachWin.show(); 
	    	
	    	//strResult=window.showModalDialog('/AuditSystem/taskAttach.do?flag=ext&attachCode='+s+"&random=" + Math.random(),window,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no');
	    } catch(e){
	    	try{ 
				var oBao = new ActiveXObject("MTOffice.WebOffice");
				var myhost="http:\/\/"+window.location.host;
				strResult = oBao.ShowjsDialog(myhost+'/AuditSystem/taskAttach.do?flag=ext&attachCode='+s+"&random=" + Math.random(),window,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no')+"";
				oBao =null;
	 		}catch(e){
	 			alert('³ö´íÁË£º'+e);
	 		}
	    }
	    
	    if(strResult && strResult.indexOf("ok")>=0){
	    	refreshStateExt(tableId,"<font color=\"red\">¸½</font>",s);
	    }
	} 
}

    function refreshStateExt(tableId,state,autoId) {
	  	var grid = Ext.getCmp("gridId_"+tableId);
	  	var store = grid.getStore();
	  	var select = grid.getSelectionModel();
	  	var active = select.selection.get(select._activeItem);
        var row = active.row, col = active.col;
        var record = store.getAt(row);
        var voucherid = document.createElement(record.data['trValue']).voucherid;
        
        var count = store.getCount();
        for(var i=0;i<count;i++) {
        	var rs = store.getAt(i);
        	var vId = document.createElement(rs.data['trValue']).voucherid;
        	if(vId == voucherid) {
        		var o=select.grid.view.getCell(i,col);
        		o.innerHTML = "<DIV class=\"x-grid3-cell-inner x-grid3-col-p1 \" unselectable=\"on\">"
        					+ "<a onclick='takeOutEntryExt(this,\"" + autoId + "\",\""+tableId+"\");' href='#'>"
        					+ state
        					+ "</a></div>" ;
        	}
        }
	  	
    }

function takeOutEntry1(s,d){
  
	if(d==""){
		takeOutEntry(s);
	}else{
	
		 var obj=document.getElementById("outA"+s);
         var sign=obj.innerHTML;

        if(sign.indexOf("³é")>=0){

 			var url = "/AuditSystem/voucherquery/takeoutPointSave.jsp?autoid="+s+"&subjectid="+d+"&random=" + Math.random();
			strResult = ajaxLoadPageSynch(url,"a=1");

          if(strResult.indexOf("ok")>=0){
            //obj.innerHTML="<font color=\"red\">³·</font>";
            refreshState2(obj,"<font color=\"red\">³·</font>");
          }

        }else if(sign.indexOf("³·")>=0){
          var oBao = new ActiveXObject("Microsoft.XMLHTTP");
          oBao.open("POST","/AuditSystem/voucherquery/takeoutPointBack.jsp?autoid="+s+"&random=" + Math.random(),false);
          oBao.send();
          var noDel = unescape(oBao.responseText);
           if(noDel.indexOf("ok")>=0){
             //obj.innerHTML="³é";
             refreshState2(obj,"³é");
           }
        }
	}
}

function taskTax(s)
{

        var obj=document.getElementById("TaskTaxTD"+s);
    
        var sign=obj.innerHTML;
		
        if(sign.indexOf("Ë°")>=0){
          var strResult = window.showModalDialog('/AuditSystem/voucherquery/tasktaxPoint.jsp?autoid='+s+"&random=" + Math.random(),null,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no')+"";
          if(strResult.indexOf("ok")>=0){
            var str ="<a id=\"outT"+ s + "\" href='javascript:taskTax(" + s + ");'><font color=\"red\">³·</font></a>&nbsp;|&nbsp;<a id=\"outT"+ s + "\" href='javascript:taskEdit(" + s + ");'><font color=\"red\">¸Ä</font></a>";  
         
            refreshState1(obj,str);
          }

        }else if(sign.indexOf("³·")>=0){
          var oBao = new ActiveXObject("Microsoft.XMLHTTP");
          oBao.open("POST","/AuditSystem/voucherquery/taketaxPointBack.jsp?autoid="+s+"&random=" + Math.random(),false);
          oBao.send();
          var noDel = unescape(oBao.responseText);
          if(noDel.indexOf("ok")>=0){   
             var str ="<a id=\"outT"+ s + "\" href='javascript:taskTax(" + s + ");'>Ë°</a>";
             refreshState1(obj,str);
          }
        
        }  
}

function taskEdit(s)
{

      window.showModalDialog('/AuditSystem/voucherquery/tasktaxPoint.jsp?autoid='+s+"&random=" + Math.random()+'&opt=1',null,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no')+"";

}


function eliminate(s)
{

	var obj=document.getElementById("EliminateTD"+s);
	var sign=obj.innerHTML;
      
	if(sign.indexOf("ÌŞ")>=0){
		
		var strResult = window.showModalDialog('/AuditSystem/voucherquery/EliminatePoint.jsp?autoid='+s+"&random=" + Math.random(),null,'dialogWidth:500px;dialogHeight:400px;center:yes;help:no;resizable:no;status:no')+"";
        
        if(strResult.indexOf("ok")>=0)	{
			//obj.innerHTML="";
			refreshState2(obj,"<font color=\"red\">³·</font>");
		}
	}else if(sign.indexOf("³·")>=0){
		var oBao = new ActiveXObject("Microsoft.XMLHTTP");
		oBao.open("POST","/AuditSystem/voucherquery/EliminatePointBack.jsp?autoid="+s+"&random=" + Math.random(),false);
		oBao.send();
		var noDel = unescape(oBao.responseText);
		if(noDel.indexOf("ok")>=0){
             refreshState2(obj,"ÌŞ");
		}
	}
}


function goRectify(s){
  window.open("/AuditSystem/Voucher/AddandEdit.jsp?Autoid="+s);
}

function goAnalyze(s1,s2,s3){
  window.open("/AuditSystem/CheckoutInfo/CorrespondVoucherCheck.jsp?strDate="+s1+"&strSub="+s2+"&strDir="+s3+"&random=" + Math.random());
}


//³éÒÉºó£¬µ÷ÓÃµÄË¢ĞÂ·½·¨
//½«Ö¸¶¨Æ¾Ö¤µÄ×´Ì¬¸Ä±ä
function refreshState1(oTD,refreshValue){

	while(oTD.tagName!="TD"){
		oTD=oTD.parentElement;
	}

    //oTDËùÔÚµÄtable
	var oTable=oTD;
	while(oTable.tagName!="TABLE"){
		oTable=oTable.parentElement;
	}
	

	var oTableHead=oTable.rows(0);
	
	//oTDËùÔÚµÄĞĞ
	var oTDRow=oTD;
	while(oTDRow.tagName!="TR"){
		oTDRow=oTDRow.parentElement;
	}
	
	//oTDÔÚ ĞĞµÄË÷Òı
	//Ë­ÓĞ¸üºÃµÄ°ì·¨£¬Çë¸æÖªÒ»ÏÂ¡£cellIndexºÃÏñÓĞÎÊÌâ¡£ 
	var oTDidx=-1;
	for(var i=0;i<oTDRow.cells.length;i++){

		if(oTDRow.cells(i).innerHTML==oTD.innerHTML){
			oTDidx=i;
			break;
		}
	}
	if(oTDidx==-1){
		//ÎŞ·¨¶¨Î»ËùÔÚĞĞµÄĞĞºÅ
		return;
	}
	
	if (oTDRow.voucherid){
	
		
		oTDRow.cells(oTDidx).firstChild.innerHTML=refreshValue;
		

	}else{
		//Ã»ÓĞ¶¨ÒåTRÊôĞÔ£¬¾ÍÍ¨¹ıCELLµ¥Ôª¸ñÈ·¶¨Æ¾Ö¤ÈÕÆÚÁĞ£»
	
		//====Æ¾Ö¤ÈÕÆÚµÄË÷Òı
		var idx=-1;
		//»ñµÃÆ¾Ö¤ÈÕÆÚµÄË÷Òı
		var cell;
		for(var i=0;i<oTableHead.cells.length;i++){
			cell=oTableHead.cells(i);
			if(cell.innerHTML.indexOf("Æ¾Ö¤ÈÕÆÚ")>=0){
				idx=i;
				break;
			}
		}
	
		if(idx==-1){
			return;
		}
		//====¿ªÊ¼Ë¢ĞÂ
	
	
		//alert(oTD.innerHTML);
		//alert(oTD.parentElement.cells(oTDidx).innerHTML+":::"+oTDidx);
	    //µÚÒ»ÁĞÊÇ±íÍ·£¬×îºóÒ»ÁĞÊÇÒ³Î²
	    for(var i=1;i<oTable.rows.length-1;i++){
			var oRow=oTable.rows(i);
	
			//ÅĞ¶ÏÆ¾Ö¤ÈÕÆÚ£¬Æ¾Ö¤×Ö£¬Æ¾Ö¤±àºÅÊÇ·ñ¶¼ÏàÍ¬¡£
			if(oTDRow.cells(idx).innerHTML==oRow.cells(idx).innerHTML
			  	&&oTDRow.cells(idx+1).innerHTML==oRow.cells(idx+1).innerHTML
			  	&&oTDRow.cells(idx+2).innerHTML==oRow.cells(idx+2).innerHTML){
			  	//Èç¹û¶¼ÏàµÈ£¬ÔòË¢ĞÂ
			  	oRow.cells(oTDidx).firstChild.innerHTML=refreshValue;
			}
		}
	}
}


//³éÒÉºó£¬µ÷ÓÃµÄË¢ĞÂ·½·¨
//½«Ö¸¶¨Æ¾Ö¤µÄ×´Ì¬¸Ä±ä
function refreshState2(oTD,refreshValue){
	
	while(oTD.tagName!="TD"){
		oTD=oTD.parentElement;
	}

    //oTDËùÔÚµÄtable
	var oTable=oTD;
	while(oTable.tagName!="TABLE"){
		oTable=oTable.parentElement;
	}

	var oTableHead=oTable.rows(0);
	
	//oTDËùÔÚµÄĞĞ
	var oTDRow=oTD;
	while(oTDRow.tagName!="TR"){
		oTDRow=oTDRow.parentElement;
	}
	
	//oTDÔÚ ĞĞµÄË÷Òı
	//Ë­ÓĞ¸üºÃµÄ°ì·¨£¬Çë¸æÖªÒ»ÏÂ¡£cellIndexºÃÏñÓĞÎÊÌâ¡£ 
	var oTDidx=-1;
	for(var i=0;i<oTDRow.cells.length;i++){

		if(oTDRow.cells(i).innerHTML==oTD.innerHTML){
			oTDidx=i;
			break;
		}
	}
	if(oTDidx==-1){
		//ÎŞ·¨¶¨Î»ËùÔÚĞĞµÄĞĞºÅ
		return;
	}
	
	if (oTDRow.voucherid){
		//
		//µÚÒ»ÁĞÊÇ±íÍ·£¬×îºóÒ»ÁĞÊÇÒ³Î²
	    for(var i=1;i<oTable.rows.length-1;i++){
			var oRow=oTable.rows(i);
			//ÅĞ¶ÏÆ¾Ö¤ÈÕÆÚ£¬Æ¾Ö¤×Ö£¬Æ¾Ö¤±àºÅÊÇ·ñ¶¼ÏàÍ¬¡£
				if(oTDRow.voucherid==oRow.voucherid){
			  		//Èç¹û¶¼ÏàµÈ£¬ÔòË¢ĞÂ
			  		oRow.cells(oTDidx).firstChild.innerHTML=refreshValue;
				}
		}
	}else{
		//Ã»ÓĞ¶¨ÒåTRÊôĞÔ£¬¾ÍÍ¨¹ıCELLµ¥Ôª¸ñÈ·¶¨Æ¾Ö¤ÈÕÆÚÁĞ£»
	
		//====Æ¾Ö¤ÈÕÆÚµÄË÷Òı
		var idx=-1;
		//»ñµÃÆ¾Ö¤ÈÕÆÚµÄË÷Òı
		var cell;
		for(var i=0;i<oTableHead.cells.length;i++){
			cell=oTableHead.cells(i);
			if(cell.innerHTML.indexOf("Æ¾Ö¤ÈÕÆÚ")>=0){
				idx=i;
				break;
			}
		}
	
		if(idx==-1){
			return;
		}
		//====¿ªÊ¼Ë¢ĞÂ
	
	
		//alert(oTD.innerHTML);
		//alert(oTD.parentElement.cells(oTDidx).innerHTML+":::"+oTDidx);
	    //µÚÒ»ÁĞÊÇ±íÍ·£¬×îºóÒ»ÁĞÊÇÒ³Î²
	    for(var i=1;i<oTable.rows.length-1;i++){
			var oRow=oTable.rows(i);
	
			//ÅĞ¶ÏÆ¾Ö¤ÈÕÆÚ£¬Æ¾Ö¤×Ö£¬Æ¾Ö¤±àºÅÊÇ·ñ¶¼ÏàÍ¬¡£
			if(oTDRow.cells(idx).innerHTML==oRow.cells(idx).innerHTML
			  	&&oTDRow.cells(idx+1).innerHTML==oRow.cells(idx+1).innerHTML
			  	&&oTDRow.cells(idx+2).innerHTML==oRow.cells(idx+2).innerHTML){
			  	//Èç¹û¶¼ÏàµÈ£¬ÔòË¢ĞÂ
			  	oRow.cells(oTDidx).firstChild.innerHTML=refreshValue;
			}
		}
	}
}

/* ÒÔºó¿ÉÄÜĞèÒªÓÃµ½µÄ´úÂë */
//	var obj=document.getElementById("outA"+s);
//	//alert(obj.innerText);
//	var oBao = new ActiveXObject("Microsoft.XMLHTTP");
//	oBao.open("POST","voucherquery/takeOutEntry.jsp?AutoID="+s,false);
//	oBao.send();
//	var strResult = unescape(oBao.responseText);



//	if(strResult.indexOf("ok")>=0)
//	{
//		obj.innerHTML="<font color=\"red\">³·</font>";
//                window.showModalDialog('voucherquery/takeoutPoint.jsp?autoid='+s,'dialogWidth:400px;dialogHeight:300px;dialogLeft:200px;dialogTop:150px;center:yes;help:no;resizable:no;status:no');
//	}
//        if(strResult.indexOf("back")>=0)
//	{
//		obj.innerHTML="³é";
//	}
function mt_process_viewImage(tableId) {
		
	var id = document.getElementById("chooseValue_"+tableId).value;
	
	if(id == "") {
		alert("è¯·é€‰æ‹©éœ€è¦æŸ¥çœ‹æµç¨‹å›¾çš„è®°å½•!") ;
		return ;
	}
	
	var trValue = document.getElementById("trValueId_"+id);
	
	var pdId = trValue.pdid ;
	var pId = trValue.pid ;
	var pkey = trValue.pkey ;
	
	if(pdId == "") {
		alert("æµç¨‹å°šæœªå‘å¸ƒ,ä¸èƒ½æ˜¾ç¤ºæµç¨‹å›¾!") ;
		return ;
	}
	
	var url = MATECH_SYSTEM_WEB_ROOT+"/process.do?method=viewImageByPIdOrKey&key=" + pkey + "&id="+pId;
	
	var tab = parent.mainTab ;
    if(tab){
		n = tab.add({    
			'title':"æµç¨‹å›¾",    
			closable:true,  //é€šè¿‡htmlè½½å…¥ç›®æ ‡é¡µ    
			html:'<iframe name="imageFrm" scrolling="auto" frameborder="0" width="100%" height="100%" src="'+url+'"></iframe>'   
		}); 
        tab.setActiveTab(n);
	}else {
		window.open(url);
	}	
	
}

function mt_process_view(pId,pName,viewUuid){
	var url = MATECH_SYSTEM_WEB_ROOT+'/process.do?method=processTransfer&view=true&pId='+ pId + "&uuid=" + viewUuid ;
	var n = parent.mainTab.add({     
		'title':pName,  
		 closable:true,  //é€šè¿‡htmlè½½å…¥ç›®æ ‡é¡µ    
		 html:'<iframe scrolling="no" frameborder="0" width="100%" height="100%" src="' + url + '"></iframe>'   
	});    
	parent.mainTab.setActiveTab(n);  
}

function startProByPkey(pKey) {
	window.location = MATECH_SYSTEM_WEB_ROOT + "/process.do?method=processTransfer&pKey=" + pKey;
}

//ç¼–è¾‘
function mt_process_edit(pKey) {
	var formId = document.getElementById("formId").value;
	var tableId = document.getElementById("tableId").value;
	
	var value= document.getElementById("chooseValue_" + tableId).value;
	if(value == ''){
		
		value = getChooseValue(tableId);
				
		if(value == "") {
			alert('è¯·é€‰æ‹©è¦ä¿®æ”¹çš„æ•°æ®!');
			return;
		
		} else if(value.indexOf(",") > -1) {
			alert('è¯·é€‰æ‹©ä¸€æ¡éœ€è¦ä¿®æ”¹çš„æ•°æ®!');
			return;
		}
	}
	
	window.location = MATECH_SYSTEM_WEB_ROOT + "/process.do?method=processTransfer&pKey=" + pKey + "&uuid="+value; 
}


//æ£€æŸ¥çŠ¶æ€
function mt_process_checkState(pKey,uuid) { 
	
	window.location = MATECH_SYSTEM_WEB_ROOT + "/process.do?method=processTransfer&pKey=" + pKey + "&uuid="+value; 
}Ext.namespace("Ext.matech.form");  

var TEMP_SYSTEM_WEB_ROOT = "/sdcszj";

if(MATECH_SYSTEM_WEB_ROOT && MATECH_SYSTEM_WEB_ROOT!="") {
	TEMP_SYSTEM_WEB_ROOT = MATECH_SYSTEM_WEB_ROOT;
}

var DEFAULT_REFRESHURL = TEMP_SYSTEM_WEB_ROOT + "/system.do?method=combox";

var selectUrl = DEFAULT_REFRESHURL;
//var selectAttributeName = "comboxId" ;
var selectAttributeName = "autoid" ;
Ext.matech.form.mtCombox = Ext.extend(Ext.form.ComboBox, {
		triggerAction : 'all',
		displayField : 'text',
		valueField : 'value',
		loadingText : 'ÕıÔÚ¼ÓÔØÊı¾İ...',
		lazyInit: true,// ¿Ø¼ş»ñµÃ½¹µãÊ±²Å»á³õÊ¼»¯ÏÂÀ­¿ò°üÀ¨Ê÷
		typeAhead : false,
		resizable : true,
		checkField:'checked',
		queryParam : 'pk1',
		anchor : '100%',
		minChars:1,  //ÊäÈë¼¸¸ö×Ö·û¿ªÊ¼ËÑË÷ 
		selectOnFocus:true,
		minListWidth:200, 
		valueNotFoundText:"",
		listeners : {   
		    'beforequery':function(e){   
		        var combo = e.combo; 
		        
		        var refer = combo.refer ;
			  	var refer1 = combo.refer1 ;
			  	var refer2 = combo.refer2 ;
			  	
			  	
			  	var referObj = document.getElementById(refer) ;
			  	var refer1Obj = document.getElementById(refer1) ;
			  	var refer2Obj = document.getElementById(refer2) ;
			  	
			  	var referValue = "";
			  	var refer1Value = "";
			  	var refer2Value = "";
			  	
			  	if(referObj) {
			  		referValue = referObj.value ; 
			  	}else {
			  		referValue = refer ; 
			  	}
			  	
			  	if(refer1Obj) {
			  		refer1Value = refer1Obj.value ; 
			  	}else {
			  		refer1Value = refer1 ; 
			  	}
			  	
			  	if(refer2Obj) {
			  		refer2Value = refer2Obj.value ; 
			  	}else {
			  		refer2Value = refer2 ; 
			  	}
			  	
			  	if(refer && !referValue ) {
			  		alert("ÇëÏÈÂ¼Èë»òÑ¡Ôñ¡¾" + (referObj.title ? referObj.title : referObj.id) + "¡¿");
			  		return false ;
			  	}
			  	
			  	if(refer1 && !refer1Value) {
			  		alert("ÇëÏÈÂ¼Èë»òÑ¡Ôñ¡¾" + (refer1Obj.title ? refer1Obj.title : refer1Obj.id) + "¡¿");
			  		return false ;
			  	}
			  	
			  	if(refer2 && !refer2Value) {
			  		alert("ÇëÏÈÂ¼Èë»òÑ¡Ôñ¡¾" + (refer2Obj.title ? refer2Obj.title : refer2Obj.id) + "¡¿");
			  		return false ;
			  	}
			 
			  	combo.store.baseParams["refer"] = referValue;
			  	combo.store.baseParams["refer1"] = refer1Value;
			  	combo.store.baseParams["refer2"] = refer2Value;
			  	
		        var input = e.query;
		        if(combo.grid) {
		        	//¹ıÂË±í¸ñ
		        	var grid = Ext.getCmp("mt_combox_grid_"+ combo.id + "_" +combo.autoid) ;
		        	if(!grid) return ;
		        	var start = combo.multiselect ? '' : 0 ;
		        	var limit = combo.multiselect ? '' : 50 ;
		        	grid.store.load({
						params:{grid:this.grid,autoid:this.autoid,start:start,limit:limit,pk1:input}
					});
		        	
		        	combo.expand(); 
		        	return false ; 
		        }else if(combo.multilevel) {
		        	//Ê÷×´ÏÂÀ­,ÒªÊ¹ÓÃÊ÷µÄ¹ıÂË
		        	combo.filter.filter(input);
					combo.expand(); 
					return false ; 
		        }else {
		             if(this.mode == 'local'){
	                    combo.selectedIndex = -1;
	                    if(e.forceAll){
	                        combo.store.clearFilter();
	                    }else{
	                        combo.store.filter(this.displayField, input);
	                    }
	                    combo.onLoad();
	                }else{
	                    combo.store.baseParams[this.queryParam] = input;
	                    combo.store.load({
	                     //   params: this.getParams(input),
	                    	params: this.getParams(input),
	                        callback :function (r,options,success){
	                        	if(combo.store.getTotalCount()<=50) {
	                        		//¼ÇÂ¼²»×ãÁ½Ò³¾Í²»ÏÔÊ¾·ÖÒ³ÁË
	                        	//	if(combo.footer.isVisible()){
	                        			//combo.assetHeight = combo.assetHeight - combo.footer.getHeight();
	                        	//	}
	                        		combo.footer.hide() ;
	                        		combo.restrictHeight();
	                        	}else {
	                        		combo.footer.show() ;
	                        		combo.restrictHeight();
	                        	}
	                        	
	                        	if(combo.multiselect) {
	                        		//var value = ","+combo.getValue()+",";
	                        		var value = combo.separator+Ext.get(combo.hiddenId).dom.value+combo.separator;
	                        		
	                        		combo.store.each(function(r) {
	                        			if(value.indexOf(combo.separator+r.data["value"]+combo.separator) > -1) {
	                        				r.set(combo.checkField, true);
	                        			}
	                        		}, combo);
	                        	}
	                        }
	                    });
	                    combo.expand();
	                } 
		            /*
		            combo.store.filterBy(function(record,id){     
		                // µÃµ½Ã¿¸örecordµÄÏîÄ¿Ãû³ÆÖµ
		                var text = record.get(combo.displayField);    
		                return text.indexOf(input) != -1 ; 
		            });   */
		            return false;     
	        	} 
		    }
		},   
	  
	  // extµÄÏÂÃæÕâ¸ö·½·¨¿ÉÄÜÓĞbug ËùÓĞÖØĞ´ÁË
	  onViewClick : function(doFocus) {  
	        var index = this.view.getSelectedIndexes()[0], s = this.store, r = s.getAt(index);   
	        if (r) {   
	          this.onSelect(r, index);   
	        } else if (s.getCount() === 0) {   
	          this.collapse();   
	        }   
	        if (doFocus !== false) {   
	          this.el.focus();   
	        } 
      },
      
      onLoad : function(){
          if(!this.hasFocus){
              return;
          }
          if(this.store.getCount() > 0 || this.listEmptyText){
              this.expand();
              this.restrictHeight();
              if(this.lastQuery == this.allQuery){
                  if(this.editable){
                      this.el.dom.select();
                  }

                  if(this.autoSelect !== false && !this.selectByValue(this.value, true)){
                      this.select(0, true);
                  }
              }else{
                  if(this.autoSelect !== false){
                      this.selectNext();
                  }
                  if(this.typeAhead && this.lastKey != Ext.EventObject.BACKSPACE && this.lastKey != Ext.EventObject.DELETE){
                      this.taTask.delay(this.typeAheadDelay);
                  }
              }
          }else{
              //this.collapse();
          }

      },
      
      initReferParam: function () {
    	  
    	  	var param = new Array(3);
    	  //alert(this.id);
    	  	var refer = this.refer;
		  	var refer1 = this.refer1;
		  	var refer2 = this.refer2;
		  	
		  	var referObj = document.getElementById(refer) ;
		  	var refer1Obj = document.getElementById(refer1) ;
		  	var refer2Obj = document.getElementById(refer2) ;
		  	
		  	var referValue = "";
		  	var refer1Value = "";
		  	var refer2Value = "";
		  	
		  	if(referObj) {
		  		param[0] = referObj.value ; 
		  	}else {
		  		param[0] = refer ; 
		  	}
		  	
		  	if(refer1Obj) {
		  		param[1] = refer1Obj.value ; 
		  	}else {
		  		param[1] = refer1 ; 
		  	}
		  	
		  	if(refer2Obj) {
		  		param[2] = refer2Obj.value ; 
		  	}else {
		  		param[2] = refer2 ; 
		  	} 
		  	
		  	return param ;
      },
      
      // ÖØĞ´Õâ¸ö·½·¨£¬Ö§³ÖÉÏÏÂ×óÓÒ¶¨Î»Ñ¡Ôñ
       initEvents : function(){
       		Ext.matech.form.mtCombox.superclass.initEvents.call(this);
       		
	        this.keyNav = new Ext.KeyNav(this.el, {    
	            "up" : this.onKeyDown,    
	            "down" : function(e){    
	                if (!this.isExpanded()) {    
	                    this.onTriggerClick();    
	                }    
	                else {    
	                    this.onKeyDown(e);    
	                }    
	            },    
	            "left":this.onKeyDown,    
	            "right":this.onKeyDown, 
	            /*
	            "enter":function(){    
	                var node = this.tree.selModel.getSelectedNode();    
	                this.onTreeClick(node);    
	            },   */
	            "esc" : function(e){    
	                this.collapse();    
	            },    
	            "tab" : function(e){    
	                this.collapse();    
	                return true;    
	            },    
	            scope : this,     
	            forceKeyDown : true 
	        });    
	        this.dqTask = new Ext.util.DelayedTask(this.initQuery, this);    
	        if(!this.enableKeyEvents){    
	            this.mon(this.el, 'keyup', this.onKeyUp, this);    
	        }    
	   },
	   
	   getCheckedDisplay:function() {   
	        var re = new RegExp(this.separator, "g");   
	        return this.getCheckedValue(this.displayField).replace(re, this.separator + ' ');   
	   },  
	   
	   getCheckedValue:function(field) {   
	        field = field || this.valueField;   
	        var c = [];   
	        var snapshot = this.store.snapshot || this.store.data;   
	  
	        snapshot.each(function(r) {   
	            if(r.get(this.checkField)) {   
	                c.push(r.get(field));   
	            }   
	        }, this);   
	  
	        return c.join(this.separator);   
	    },
	    
		
	  initComponent : function() {
	  	
	 // 	var queryParam = this.initReferParam() ; 
	  	var queryParam;
	  	
	  	if(this.multilevel || this.grid) {
	  		queryParam = this.initReferParam() ; 
	  	}
	  	
	  	var url = selectUrl;	
	  	
	  	if(this.grid) {
	  		
	  		this.store = new Ext.data.SimpleStore({
				fields : [],
				data : [[]]
		 	}) ;
		 	
		 	this.mode = 'local' ;
		 	this.resizable = false ;  
	  		this.tpl = "<tpl for='.'><div style='height:300px' id='grid"+this.id+"'></div></tpl>" ;
	  		var gridPanel ;
	  		var comboObj = this ;
	  		var clickRecord ;
	  		var value = "" ;
			var text = "" ;  
			var jsonStore ;
			
			var start = this.multiselect ? '' : 0 ;
			var limit = this.multiselect ? '' : 50 ;
	  		
	  		Ext.Ajax.request({
					method: 'POST',
					url: url,
					success: function(response) {
						
						if(response.responseText.indexOf("ERROR") > -1) {
							alert(response.responseText.replace("ERROR",""));
							return ;
						}
						var obj = Ext.util.JSON.decode(response.responseText);
						
						var columns = eval(obj[0].columns) ;
						var fields = eval(obj[0].fields) ;
						
						
						if(comboObj.multiselect) {
							
							var chooseArr = [{id:'choose',header:'<input style="text-align:left" type="checkbox" onclick="mt_select_selectAll(\'' + comboObj.autoid + '\',\'' + comboObj.id + '\',this)" />',
											width:30,menuDisabled:true,sortable:false,width:50,dataIndex:columns[0].dataIndex,renderer:function(v,params,record,rowIndex){
								var group = record.data.hidden_select_group ||  record.data.select_group; //ÊÇ·ñÍ¬×é£¬Í¬×éÊ±¿ÉÒÔÒ»ÆğÑ¡ÖĞ
								var groupStr = "" ;
								if(group) groupStr = " group="+group ;
								return '<input type=checkbox '+groupStr+' onclick=mt_select_groupSelect(\'' + comboObj.autoid + '\',\'' + comboObj.id + '\',this) value="'+v+'" name="mt_gridMultiSelect_' + comboObj.id + "_" + comboObj.autoid + '" rowIndex="' + rowIndex + '">' ;
							}}] ;
							columns = chooseArr.concat(columns);
						}   
						if(fields.length > 0) {
							value = fields[0].name ;
							text = fields[1].name ;
						}
						
						jsonStore = new Ext.data.JsonStore({ 
							fields: fields,
					 		url: url,
							root: 'data',
							totalProperty:'totalProperty',
							remoteSort:false 
						});
						
						jsonStore.on('beforeload', function(store) {
							queryParam = comboObj.initReferParam();
							//alert(queryParam[0]);
							jsonStore.baseParams={grid:comboObj.grid,autoid:comboObj.autoid,refer:queryParam[0],refer1:queryParam[1],refer2:queryParam[2]};
						}); 
						
						var bbar = null ;
						if(!comboObj.multiselect) {
							bbar = new Ext.PagingToolbar({
								store:jsonStore,
								pageSize: 50,  
								emptyMsg: "Ã»ÓĞ¼ÇÂ¼"
						  	}) ;
						}
						
						gridPanel = new Ext.grid.GridPanel({
							frame:true,  
							id:"mt_combox_grid_"+ comboObj.id + "_" +comboObj.autoid,
							store:jsonStore,
							columns:columns,
							sm :new Ext.grid.RowSelectionModel({singleSelect:false}),
							bbar:bbar
						}) ;
						
						gridPanel.on('rowclick', function(grid, rowIndex, e) {
							
							if(!comboObj.multiselect) {
								var selections = grid.getSelectionModel().getSelections();
						       	for (var i = 0; i < selections.length; i++) {
						            var record = selections[i];
						            
						            comboObj.value = record.get(value);  
					                comboObj.setRawValue(record.get(text)); 
					                
					                var columns = "" ; 
					                if(fields.length > 0) {
					                	columns += "{" ;
										for(var j=0;j<fields.length;j++) {
											columns += "'" + fields[j].name + "':" ;
											/*
											if(comboObj.gridFieldIndex == "false") {
												columns += "'" + fields[j].name + "':" ;
											} else {
												columns += "'" + fields[j].name + "_" + j + "':" ;
											}*/
											
											columns += "'" + record.get(fields[j].name) + "'," ;
										}
										columns = columns.substring(0, columns.length -1) ;
										columns += "}" ; 
									}
					                if(columns !="") {
					                	 Ext.get(comboObj.hiddenId).dom.columns = columns ;
					                	 Ext.get(comboObj.el.dom.id).dom.columns = columns ;  
					                }
					                
					                if(comboObj.hiddenId) {
					                	Ext.get(comboObj.hiddenId).dom.value = comboObj.value; 
					                }
					                
					                clickRecord = record ;
					                 
					                if(comboObj.onselect)   
					                	comboObj.onselect.call(Ext.get(comboObj.el.dom.id).dom,columns) ;
					                
					               
						       	}
						       	comboObj.collapse();
							}
					       	
					    });
						if(columns.length < 5) {
							gridPanel.getView().forceFit = true ;
						}
						comboObj.gridPanel = gridPanel ;
		 			},
					failure: function() {
		 				alert("ºóÌ¨»ñÈ¡Êı¾İÊ§°Ü");
		 			},
		 			params: {grid:this.grid,autoid:this.autoid,head:true,start:start,limit:limit,refer:queryParam[0],refer1:queryParam[1],refer2:queryParam[2]}
	 		});
	 		
	 		this.on('expand', function() {
	 			this.innerList.dom.style.overflowY="hidden";
	 			gridPanel.render('grid'+this.id); 
	 			gridPanel.store.load({
					params:{grid:this.grid,autoid:this.autoid,start:start,limit:limit},
					callback:function (){
						//¹´Ñ¡¶àÑ¡¿ò 
			 			if(comboObj.value) {
			 				var valueArr = comboObj.value.split(",") ;
			 				var checkbox = document.getElementsByName("mt_gridMultiSelect_" + comboObj.id + "_" +comboObj.autoid) ;
			 				for(var k=0;k<checkbox.length;k++) {
			 					for(var y=0;y<valueArr.length;y++) {
			 						if(checkbox[k].value == valueArr[y]) {
			 							checkbox[k].checked = true ;
			 						}
			 					}
			 				}
			 			}
					}
				});
	 			
	 			this.restrictHeight();
	 			gridPanel.setWidth(this.listWidth*1);               
	 			gridPanel.setHeight(this.innerList.getHeight()*1) ;
	 			
			}); 
			
			this.onRealBlur = function() {   
				
				if(!this.multiselect) {
					this.list.hide();  
					if(!this.value && this.getRawValue()) {
						 this.value = this.getRawValue();   
		                 if(this.hiddenId) {
		                 	Ext.get(this.hiddenId).dom.value =  this.getRawValue(); 
		                 }
					 }
				}
		    } ;
		    
		    this.findRecord = function(prop, value){
		        var record;
		        if(!jsonStore) return ;
		        if(jsonStore.getCount() > 0){
		        	jsonStore.each(function(r){
		                if(r.data[prop] == value){
		                    record = r;
		                    return false;
		                }
		            });
		        }
		        return record;
		    } ;
		    
		    this.beforeBlur = function (){
		    	if(this.readOnly) return ;
		    	
	            var val = this.getRawValue(),
	                rec;

	            if(this.valueField && Ext.isDefined(this.value)){
	                rec = this.findRecord(value, this.value);
	            }
	            if(!rec || rec.get(text) != val){
	                rec = this.findRecord(text, val);
	            }
	            
	            if(!rec && this.forceSelection){
	                if(val.length > 0 && val != this.emptyText){
	                    this.el.dom.value = Ext.value(this.lastSelectionText, '');
	                    this.applyEmptyText();
	                }else{
	                    this.clearValue();
	                }
	            }else{
	                if(rec && value){
	                    if (this.value == val){
	                        return;
	                    }
	                    val = rec.get(value || text);
	                }
	                this.value = val;
	                if(this.hiddenId) {
	                 	Ext.get(this.hiddenId).dom.value = val; 
	                 }
	            }
		    	
		    } ;
		    
		    this.on({   
	             scope:this  
	            ,blur:this.onRealBlur
	        });
		    
			
		    this.girdMultiSelect = function (combo) {
		    	
		    	var choose = document.getElementsByName("mt_gridMultiSelect_"+ this.id + "_" +this.autoid) ;
		    	
		    	var grid = Ext.getCmp("mt_combox_grid_"+ comboObj.id + "_" +comboObj.autoid) ;
		    	if(!grid) return ;
    			var store = grid.store ;
    			
    			var fields = store.fields ;
    			
    			var json = "" ;
    			var columns = "" ; 
    			var value = "" ;
    			var text = "" ;
		    	for(var i=0;i<choose.length;i++) {
		    		
		    		if(choose[i].checked) {
		    			var rowIndex = choose[i].rowIndex ;
		    			var record = store.getAt(rowIndex) ;
		                 if(fields.length > 0) {
		                	columns += "{" ;
		                	//alert(typeof(fields)) ;
		                	fields.each(function(field,index){
		                		/*
		                		if(comboObj.gridFieldIndex == "false") {
									columns += "'" + field.name + "':" ;
								} else {
									columns += "'" + field.name + "_" + index + "':" ;
								}*/
		                		columns += "'" + field.name + "':" ;
								columns += "'" + record.get(field.name) + "'," ;
								
								if(index == 0) {
									value += record.get(field.name) + "," ;
								}else if(index == 1) {
									text += record.get(field.name) + "," ;
								}
		                	})  ;
							columns = columns.substring(0, columns.length -1) ;
							columns += "}," ; 
						}
		    		}
		    	}
		    	
		    	if(columns !="") {
		    		columns = columns.substring(0, columns.length -1) ;
		    		value = value.substring(0, value.length -1) ;
		    		text = text.substring(0, text.length -1) ;
		    		comboObj.value = value;  
	                comboObj.setRawValue(text); 
	                if(comboObj.hiddenId) {
	                	Ext.get(comboObj.hiddenId).dom.value = value; 
	                }
		    		
		    		json += "[" + columns + "]" ;
               	 	Ext.get(comboObj.hiddenId).dom.columns = json ;
               	 	Ext.get(comboObj.el.dom.id).dom.columns = json ;  
                }
		    	if(comboObj.onselect)   
                	comboObj.onselect.call(Ext.get(comboObj.el.dom.id).dom,json) ;
		    	
		    	
		    	comboObj.collapse();
		    } ;
		    
		    if(this.multiselect) {
		    	this.setValue = this.girdMultiSelect ;
		    }
		    	
	  		
	  	}else if(this.multilevel) {
	    	
			this.tpl = "<tpl for='.'><div style='height:300px'><div id='tree"+this.id+"'></div></div></tpl>" ;
	    	
	    	var root = new Ext.tree.AsyncTreeNode({   
	      		text :this.loadingText,  
	      		id : 'root',
	      		expanded : false
	    	});
	    	
	    	var treeLoader = new Ext.tree.TreeLoader({   
            	dataUrl : url,
            	autoLoad:false,
            	baseParams:{
            		autoid:this.autoid,
            		multilevel:this.multilevel,
            		multiselect:this.multiselect,
            		refer:queryParam[0],
            		refer1:queryParam[1],
            		refer2:queryParam[2],
            		pk1:Ext.get(this.hiddenId).dom.value
            	}
          	}) ; 
	    	var obj = this ;
	    	treeLoader.on('beforeload', function(store) {
				queryParam = obj.initReferParam();
				//alert(queryParam[0]);
				treeLoader.baseParams={multilevel:obj.multilevel,multiselect:obj.multiselect,autoid:obj.autoid,refer:queryParam[0],refer1:queryParam[1],refer2:queryParam[2]};
			}); 
	    	
		 	var tree = new Ext.tree.TreePanel({ 
				root :root,
				loader : treeLoader, 
				autoScroll : true,
				animate : false,
				useArrows : true,  
				rootVisible:true,  
				border : false  
	    	});
	    	
	    	tree.on('checkchange', function(node, checked) {   
				node.expand(false,false,function(){
					node.eachChild(function(child) {  
						child.ui.toggleCheck(checked);   
						child.attributes.checked = checked;   
						child.fireEvent('checkchange', child, checked);   
					}); 
				});   
				node.attributes.checked = checked; 
				  
			}, tree); 
	    	
			this.filter = new Ext.ux.form.TreeFilter(tree,{
				ignoreFolder:this.ignoreFolder,
				clearAction:'collapse'
			});
	    	
	    	
	    	if(this.multiselect) {
	    		
	    		this.getTreeCheckedValue = function () {
					var selects = tree.getChecked();
					var values = "" ;
					
					for(var i=0;i<selects.length;i++) {
						if(selects[i].isLeaf()) {
							values += selects[i].id + this.separator;  
						}
					}
					if(values != "") {
						values = values.substr(0,values.length-1) ;
					}
					return values ;
				} ;
				
				this.getTreeCheckedText = function () {
					var selects = tree.getChecked();
					var values = "" ;
					
					for(var i=0;i<selects.length;i++) {
						if(selects[i].isLeaf()) {
							values += selects[i].text + this.separator;  
						}
					}
					if(values != "") {
						values = values.substr(0,values.length-1) ;
					}
					return values ;
				} ;
				
				this.clearCheck = function () {
					var nodes = tree.getChecked();  
			        if (nodes && nodes.length) {  
			            for (var i = 0; i < nodes.length; i++) {  
			                //ÉèÖÃUI×´Ì¬ÎªÎ´Ñ¡ÖĞ×´Ì¬  
			                nodes[i].getUI().toggleCheck(false);  
			                //ÉèÖÃ½ÚµãÊôĞÔÎªÎ´Ñ¡ÖĞ×´Ì¬  
			                nodes[i].attributes.checked = false;  
			            }  
			        }  
				} ;
				
				 this.onRealBlur = function() {   
			         this.list.hide();  
	                 //Èç¹ûÃ»ÓĞÑ¡ÔñÊı¾İ£¬Ôò±£³ÖÏÖ×´
			         if(this.getTreeCheckedValue()==""){
			        	 return;
			         }
			         this.value = this.getTreeCheckedValue();   
			         this.setRawValue(this.getTreeCheckedText());   
	                 if(this.hiddenId) {
	                 	Ext.get(this.hiddenId).dom.value =  this.value; 
	                 }
	                 
			    } ;
			    
			    this.on({   
		             scope:this  
		            ,blur:this.onRealBlur   
		        });
	    		
	    	}else {
	    		var comboxObj = Ext.getCmp(this.id) ;
	    		
	    		var clickNode ;  //¼ÇÂ¼ÄÄ¸ö½áµã±»µã»÷ÁË 
	    		tree.on('click',function(node,event){
	    			 
		    		event.stopEvent();
		    		/*
		    		if (!node.isLeaf()){
		    			node.expand();
		    		    return ;
					}else { 
					   	// Ò¶×Ó   
						clickNode = node ;
						comboxObj.setRawValue(node.text);  
						comboxObj.value = node.id ; 
		                 
		                if(comboxObj.hiddenId) {
		                   Ext.get(comboxObj.hiddenId).dom.value =  comboxObj.value ;  
		                }
		                
		          		comboxObj.collapse();  
					} 
					*/
		    		
		    		clickNode = node ;
					comboxObj.setRawValue(node.text);  
					comboxObj.value = node.id ; 
	                 
	                if(comboxObj.hiddenId) {
	                   Ext.get(comboxObj.hiddenId).dom.value =  comboxObj.value ;  
	                }
	                
	          		comboxObj.collapse();  
	   		  	});
	   		  	
	   		  	 //
	   		  	 this.onRealBlur = function() {
	   		  	 	this.list.hide();  
	   		  	 	if(clickNode) {
	   		  	 		//°Ñ×îºóµã»÷½ÚµãµÄÖµ¸³µ½comboxÀïÃæÈ¥
	   		  	 		 this.value = clickNode.id;   
		                 this.setRawValue(clickNode.text);   
		                 if(this.hiddenId) {
		                 	Ext.get(this.hiddenId).dom.value =  this.value; 
		                 }
	   		  	 	}
			    } ;
			    
			   this.beforeBlur = function (){
				 if(!this.value && this.getRawValue()) {
					 this.value = this.getRawValue();   
	                 if(this.hiddenId) {
	                 	Ext.get(this.hiddenId).dom.value =  this.getRawValue(); 
	                 }
				 }
			   } ;
	   		  	
	   		  	 this.on({   
		             scope:this  
		            ,blur:this.onRealBlur
		        });
		        
	    	}
	    	
			this.on('expand', function() { 
				//Õ¹¿ªÊ±²ÅÈ¥¼ÓÔØÊ÷
				tree.root.reload(); 
				//Ext.get('tree'+this.id).parent().setHeight(20) ;
				tree.render('tree'+this.id); 
				var combo = this ;
				root.expand(false,true,function(){
					combo.restrictHeight();
					root.setText("");
				});
			}); 
			
			this.store = new Ext.data.SimpleStore({
				fields : [],
				data : [[]]
		 	}) ;
		 	
		 	this.mode = 'local' ;
    	    	
	    }else {
	    	 var jsonStore = new Ext.data.JsonStore({
		    	url:url,    
	            root:'data',    
	            totalProperty: 'totalProperty',    
	            remoteSort: true,    
	            fields:['value','text'],
				baseParams : {
					autoid : this.autoid
				}
			});
			
	        this.mode = 'remote' ;
			this.store = jsonStore ;
			this.pageSize = 100 ;
			
			if(this.multiselect) { 
				
				 if(!this.tpl) {
		            this.tpl =      
		                 '<tpl for=".">'  
		                +'<div class="x-combo-list-item">'  
		                +'<img src="' + Ext.BLANK_IMAGE_URL + '" '  
		                +'class="ux-lovcombo-icon ux-lovcombo-icon-'  
		                +'{[values.' + this.checkField + '?"checked":"unchecked"' + ']}">'  
		                +'<span class="ux-lovcombo-item-text">{' + (this.displayField || 'text' )+ '}</span>'  
		                +'</div>'  
		                +'</tpl>'   
		            ;   
		        }
		        
		         this.onSelect = function(record, index) {  
		        	 
			        if(this.fireEvent('beforeselect', this, record, index) !== false){   
			            // toggle checked field
			            record.set(this.checkField, !record.get(this.checkField));   
			  
			            // display full list
			            if(this.store.isFiltered()) {   
			                this.doQuery(this.allQuery);   
			            }   
			            // set (update) value and fire event
			            this.setValue(this.getCheckedValue());   
			            this.fireEvent('select', this, record, index);   
			        }
			        
					if(this.onselect)   
						this.onselect.call(Ext.get(this.el.dom.id).dom) ;
			    } ;
			    
			    this.setValue = function(v) {   
			        if(v) {
			            v = '' + v;   
			            if(this.valueField) {   
			                this.store.clearFilter();   
			                this.store.each(function(r) {
			                    var checked = !(!v.match(   
			                         '(^|' + this.separator + ')' + RegExp.escape(r.get(this.valueField))   
			                        +'(' + this.separator + '|$)'))   
			                    ;   
			  
			                    r.set(this.checkField, checked);   
			                }, this);   
			                this.value = this.getCheckedValue();   
			                this.setRawValue(this.getCheckedDisplay());   
			                if(this.hiddenId) {
			                    Ext.get(this.hiddenId).dom.value =  v ;
			                }   
			            }   
			            else {   
			                this.value = v;   
			                this.setRawValue(v);   
			                if(this.hiddenId) {
			                	Ext.get(this.hiddenId).dom.value =  v ;
			                }   
			            }   
			            if(this.el) {   
			                this.el.removeClass(this.emptyClass);   
			            }   
			        }   
			        else {   
			            this.clearValue();   
			        }   
			    } ;
			    
			    this.beforeBlur = function(){
			    	if(this.readOnly) return ;
				    var val = this.getRawValue();  
				    if(this.forceSelection){
				        if(val.length > 0 && val != this.emptyText){  
				           this.el.dom.value = Ext.isDefined(this.lastSelectionText) ? this.lastSelectionText : '';  
				            this.applyEmptyText();  
				        }else{  
				            this.clearValue();  
				        }  
				    }else{  
			            var texts = val.split(this.separator);  
			            var values='';  
			            for(var i=0;i<texts.length;i++){  
		                    var rec = this.findRecord(this.displayField, texts[i].trim());  
		                 	if(rec){  
		                        values+=(values.length>0?this.separator:'')+rec.data[this.valueField];  
		                    }  
		                }  
				        this.setValue(values);  
				    }  
				} ;
				
				this.clearCheckValue = function(){
			        this.value = '';  
			        this.setRawValue(this.value);  
			        this.store.clearFilter();  
			        this.store.each(function(r) {  
			            r.set(this.checkField, false);  
			        }, this);  
			        if(this.hiddenField) {  
			            this.hiddenField.value = '';  
			        }  
			        this.applyEmptyText();  
				} ;
			    
			    this.onRealBlur = function() {
			    	if(this.readOnly) return ;
			        this.list.hide();   
			        var rv = this.getRawValue();
			        var rva = rv.split(new RegExp(RegExp.escape(this.separator) + ' *'));   
			        var va = [];   
			        var snapshot = this.store.snapshot || this.store.data;   
			        Ext.each(rva, function(v) {   
			            snapshot.each(function(r) {
			                if(v === r.get(this.displayField)) {
			                    va.push(r.get(this.valueField));   
			                }   
			            }, this);   
			        }, this);
			        this.setValue(va.join(this.separator));   
			        this.store.clearFilter();   
			    } ;
			    
			    this.on({   
		             scope:this  
		            ,blur:this.onRealBlur   
		        });
			}else {
				//ÆÕÍ¨ÏÂÀ­£¬¿ÉÒÔÍ¨¹ıÖØĞ´onSelectÊµÏÖÑ¡ÔñÒ»¸öÔªËØºóµÄ¶«Î÷
				
				this.onSelect = function(record, index) {
					
					if(this.fireEvent('beforeselect', this, record, index) !== false){
			            this.setValue(record.data[this.valueField || this.displayField]);
			            this.collapse();
			            this.fireEvent('select', this, record, index);
			        }   
					
					if(this.onselect)   
						this.onselect.call(Ext.get(this.el.dom.id).dom) ;
			    } ;
			    
			    this.beforeBlur = function (){
			    	if(this.readOnly) return ;
			    	this.assertValue();
			    } ;
			    
			}
	    }  
	    Ext.matech.form.mtCombox.superclass.initComponent.call(this);  
	  },
	  
	  initList : function () {
	  	
        if(!this.list){
            var cls = 'x-combo-list',
                listParent = Ext.getDom(this.getListParent() || Ext.getBody()),
                zindex = parseInt(Ext.fly(listParent).getStyle('z-index'), 10);

            if (!zindex) {
                zindex = this.getParentZIndex();
            }

            this.list = new Ext.Layer({
                parentEl: listParent,
                shadow: this.shadow,
                cls: [cls, this.listClass].join(' '),
                constrain:false,
                zindex: (zindex || 12000) + 5
            });

            var lw = this.listWidth || Math.max(this.wrap.getWidth(), this.minListWidth);
            this.list.setSize(lw, 0);
            this.list.swallowEvent('mousewheel');
            this.assetHeight = 0;
            if(this.syncFont !== false){
                this.list.setStyle('font-size', this.el.getStyle('font-size'));
            }
            
            if(this.title){
                this.header = this.list.createChild({cls:cls+'-hd', html: this.title});
                this.assetHeight += this.header.getHeight();
            }
            
            var combo = this ;
	  		this.tbar = new Ext.Toolbar({
		           items:[{
					text:'Çå¿Õ',
			   		icon:TEMP_SYSTEM_WEB_ROOT + '/img/delete.gif',
			   		 handler:function(){
			   			combo.clear();
			   		 }
				},'-'
		        ]
		    });  
	  		
	  		if(this.grid && this.multiselect) {
	  			this.tbar.add({
					text:'È·¶¨¶àÑ¡',
			   		icon:TEMP_SYSTEM_WEB_ROOT + '/img/selectall.gif',
			   		 handler:function(){
			   			combo.girdMultiSelect(combo) ;
			   		 }
				},'-') ;
	  			this.tbar.doLayout() ;
	  		}
	  	   
		  	 if (this.tbar) {
	            var tbar = this.list.createChild();
	            this.tbar.render(tbar);
	            
	            this.assetHeight += tbar.getHeight();
	            var lw = this.listWidth ||  Math.max(this.list.getWidth(),tbar.getWidth());
	            this.list.setWidth(lw);
	        }

            this.innerList = this.list.createChild({cls:cls+'-inner'});
            this.mon(this.innerList, 'mouseover', this.onViewOver, this);
            this.mon(this.innerList, 'mousemove', this.onViewMove, this);
            this.innerList.setWidth(lw - this.list.getFrameWidth('lr'));
			
           
            if(this.pageSize){
                this.footer = this.list.createChild({cls:cls+'-ft'});
                this.pageTb = new Ext.PagingToolbar({
                    store: this.store,
                    pageSize: this.pageSize,
                    renderTo:this.footer
                });
                this.assetHeight += this.footer.getHeight();
            }

            if(!this.tpl){
                this.tpl = '<tpl for="."><div class="'+cls+'-item">{' + this.displayField + '}</div></tpl>';
            }

            this.view = new Ext.DataView({
                applyTo: this.innerList,
                tpl: this.tpl,
                singleSelect: true,
                selectedClass: this.selectedClass,
                itemSelector: this.itemSelector || '.' + cls + '-item',
                emptyText: this.listEmptyText,
                deferEmptyText: false
            });

            this.mon(this.view, {
                containerclick : this.onViewClick,
                click : this.onViewClick,
                scope :this
            });
	
            this.bindStore(this.store, true);
            if(this.resizable){
                this.resizer = new Ext.Resizable(this.list,  {
                   pinned:true, handles:'se'
                });
                this.mon(this.resizer, 'resize', function(r, w, h){
                    this.maxHeight = h-this.handleHeight-this.list.getFrameWidth('tb')-this.assetHeight;
                    this.listWidth = w;
                    this.innerList.setWidth(w - this.list.getFrameWidth('lr'));
                    this.restrictHeight();
                }, this);

                this[this.pageSize?'footer':'innerList'].setStyle('margin-bottom', this.handleHeight+'px');
            }
        }
	  
	 },
	 
	 restrictHeight : function(){
	        this.innerList.dom.style.height = '';
	        var inner = this.innerList.dom,
	            pad = this.list.getFrameWidth('tb') + (this.resizable ? this.handleHeight : 0) + this.assetHeight,
	            h = Math.max(inner.clientHeight, inner.offsetHeight, inner.scrollHeight),
	            ha = this.getPosition()[1]-Ext.getBody().getScroll().top,
	            hb = Ext.lib.Dom.getViewHeight()-ha-this.getSize().height,
	            space = Math.max(ha, hb, this.minHeight || 0)-this.list.shadowOffset-pad-5;
	        h = Math.min(h, space, this.maxHeight);
	        
	        this.innerList.setHeight(h);
	        this.list.beginUpdate();
	        this.list.setHeight(h+pad);
	        this.list.alignTo.apply(this.list, [this.el].concat(this.listAlign));
	        this.list.endUpdate();
	       
	    },
	    
	 expand : function(){
        if(this.isExpanded() || !this.hasFocus){
            return; 
        }

        if(this.title || this.pageSize || this.tbar){
            this.assetHeight = 0;
            if(this.title){
                this.assetHeight += this.header.getHeight();
            }
            if(this.pageSize){
                this.assetHeight += this.footer.getHeight();
            }
            if(this.tbar) {
            	this.assetHeight += this.tbar.getHeight(); 
            }
        }

        if(this.bufferSize){
            this.doResize(this.bufferSize);
            delete this.bufferSize;
        }
        this.list.alignTo.apply(this.list, [this.el].concat(this.listAlign));

        var listParent = Ext.getDom(this.getListParent() || Ext.getBody()),
            zindex = parseInt(Ext.fly(listParent).getStyle('z-index') ,10);
        if (!zindex){
            zindex = this.getParentZIndex();
        }
        if (zindex) {
            this.list.setZIndex(zindex + 5);
        }
        
        this.list.show(); 
        
        if(Ext.isGecko2){
            this.innerList.setOverflow('auto'); // necessary for FF 2.0/Mac
        }
        this.mon(Ext.getDoc(), {
            scope: this,
            mousewheel: this.collapseIf,
            mousedown: this.collapseIf
        });
        this.fireEvent('expand', this);
    },
    clear : function () {
    	if(this.multiselect && !this.grid) {
    		if(this.multilevel) {
    			//È¥µôÊ÷½áµãÑ¡Ôñ²¢Çå¿Õcombox
    			this.clearCheck() ;
    			this.clearValue();
    		}else {
    			this.clearCheckValue();
    		}
    	}else {
    		this.clearValue();
    	}
    }
	
});


Ext.ux.form.TreeFilter = function(tree,config){
	this.tree = tree;
	Ext.apply(this,config||{});
	this.pyCache = {};
	this.matches = [];
	this.lastQuery = '';
	this.cleared = true;
} ;

Ext.ux.form.TreeFilter.prototype = {
	ignoreFolder:true,
	clearAction:undefined,
	
	filter:function(value, attr , startNode){
		if(value == this.lastQuery){
			return;
		}
		value = value.trim();
		this.lastQuery = value;
		if(value.length == 0){
			this.clear(startNode);
			return;
		}else 
		startNode = startNode||this.tree.root;
		var reg = new RegExp('('+value+')');
		var fn = function(n){
			return reg.test(n.attributes["text"]) || reg.test(n.attributes["id"]);
		};
		this.filterAll(fn,startNode);
		this.showPaths();
		this.cleared = false;
	},
	
	//private get the matchs and hide other nodes
	filterAll:function(fn,startNode){
		var arr = [];
		startNode = startNode||this.tree.root;				
		startNode.cascade(function(n){
			if(!n.leaf){
				n.expand(false,false);
				if (this.ignoreFolder) {
					n.ui.hide();
					return;
				}	
			}
			if(fn.call(this,n)){
				arr.push(n); 	
			}else{
				n.ui.hide();
			}
		},this);
		this.matches = arr;
	},
	//ÏÔÊ¾Æ¥ÅäµÄ¸¸½ÚµãÂ·¾¶
	showPaths:function(){
		Ext.each(this.matches,function(n){
			n.bubble(function(n){
				n.ui.show();
			});
		}) ;
	},
	hasMatch:function(){
		return !Ext.isEmpty(this.matches);
	},
	clear : function(startNode){
		if(this.cleared === true){
			return;
		}
		startNode = startNode||this.tree.root;	
		startNode.cascade(function(n){
			n.ui.show();
			if (this.clearAction) {
				n[this.clearAction](true, true);
			}
		});
		this.cleared = true;
	},
	isCleared:function(){
		return this.cleared;
	},
	destroy:function(){
		Ext.destroyMembers(this,'pyCache','matches');
	}
} ; 

String.prototype.trim = function() {                
  return this.replace(/^\s+|\s+$/g, '');          
} ;

if('function' !== typeof RegExp.escape) {     
    RegExp.escape = function(s) {     
        if('string' !== typeof s) {     
            return s;     
        }     
        // Note: if pasting from forum, precede ]/\ with backslash manually     
        return s.replace(/([.*+?^=!:${}()|[\]\/\\])/g, '\\$1');     
    };  
}

var initCombox = function (param){
	
	var inputArray ;
	
	if(param) {
		if(typeof(param) == "string") {
			inputArray = Ext.query("#"+param) ;
		}else {
			var arr = new Array();
			arr.push(param) ;
			inputArray = arr ;
		}
	}else {
		inputArray = Ext.query("input["+selectAttributeName+"]") ;
	}
	
	Ext.each(inputArray,function(input){
		var autoId = input.getAttribute(selectAttributeName) ;
		var id = input.getAttribute("id") ;
		var name = input.getAttribute("name") ;
		var noinput = input.getAttribute("noinput") ; // ÊÇ·ñÔÊĞíÊäÈë
		var width = input.clientWidth ;
		var multiselect = input.getAttribute("multiselect") ; // ÊÇ·ñ¶àÑ¡
		var multilevel = input.getAttribute("multilevel") ;  // ÊÇ·ñ¶à¼¶
		var separator = input.getAttribute("separator") ;  // ¶àÑ¡·Ö¸ô·û
		var grid = input.getAttribute("grid") ;  // ÊÇ·ñÏÔÊ¾ÏÂÀ­±í¸ñ
		var value = input.value;  
		var className = input.className ;//ÑéÖ¤
		var size = input.size ;
		var onselect = input.onselect ;
		var readOnly = input.readOnly ;
		var property = input.getAttribute("property") ;
		var listWidth = input.getAttribute("listWidth") ;
		var disabled = input.disabled ;
		var onchange = input.onchange ;
		var validateId = input.validateId ;
		
		var gridFieldIndex = input.getAttribute("gridFieldIndex") ;
		var valuemustexist = input.getAttribute("valuemustexist") ;
		
		var refer = input.getAttribute("refer") ;
		var refer1 = input.getAttribute("refer1") ;
		var refer2 = input.getAttribute("refer2") ; 
		var validRefer = input.getAttribute("validRefer") ; 
		
		var refreshtarget = input.getAttribute("refreshtarget") ;
		var refreshtarget1 = input.getAttribute("refreshtarget1") ;
		var refreshtarget2 = input.getAttribute("refreshtarget2") ;
		var emptyText = "ÇëÑ¡Ôñ»òÊäÈë...";
		var editable = true ;
		if(noinput && noinput == "true" || multiselect == "true") {
			//ÉèÖÃÁËnoinput»ò¶àÑ¡»òÏÂÀ­±í¸ñ¶¼ÉèÖÃ²»ÔÊĞíÊäÈë
			editable = false ;
			emptyText = "ÇëÑ¡Ôñ...";
		}
		
		if(readOnly) {
			emptyText = "";
		}
		
		if(width == 0){
			width = size*8 + 20 ; //Èç¹ûÈ¡²»µ½¿í¶È,¾ÍÓÃsizeÀ´È¡´ú  20ÎªÓÒ±ßÏÂÀ­Í¼Æ¬¿í¶È
			
		}
		
		if(!listWidth)  listWidth = width > 200 ? width : 200;
		
		
		var mtCombox = new Ext.matech.form.mtCombox({
			multilevel:multilevel,
			autoid:autoId,
			transform:id,
			emptyText:emptyText,
			listWidth :listWidth,
			hiddenName:name,
			hiddenId:id,
			width:width,
			multiselect:multiselect,
			id:id, 
			editable:editable,
			separator:separator || ',',
			grid:grid,
			validateId:validateId,
			refer:refer,
			refer1:refer1,
			refer2:refer2,
			disabled:disabled,
			validRefer:validRefer,
			readOnly:readOnly,
			refreshtarget:refreshtarget,
			refreshtarget1:refreshtarget1,
			refreshtarget2:refreshtarget2,
			onselect:onselect,
			gridFieldIndex:gridFieldIndex,
			valuemustexist:valuemustexist,
			setRealValue:function (value){
				var queryParam = new Array(3) ;
				var referObj = document.getElementById(this.refer) ;
			  	var refer1Obj = document.getElementById(this.refer1) ;
			  	var refer2Obj = document.getElementById(this.refer2) ;
			  	
			  	if(referObj) {
			  		queryParam[0] = referObj.value ;
			  	}else {
			  		queryParam[0] = this.refer ;
			  	}
			  	
			  	if(refer1Obj) {
			  		queryParam[1] = refer1Obj.value ;
			  	}else {
			  		queryParam[1] = this.refer1 ;
			  	}
			  	
			  	if(refer2Obj) {
			  		queryParam[2] = refer2Obj.value ;
			  	}else {
			  		queryParam[2] = this.refer2 ;
			  	}
			  	
			  	var url = selectUrl ;
			  	
			  	this.value = value ;
			  	this.setRawValue(value);  
				if(this.hiddenId) {
	            	Ext.get(this.hiddenId).dom.value =  value; 
	            }
				
				var tempValue = "" ; //´«µ½ºóÌ¨ÎÄ±¾¿òµÄÖµ
				if(this.multiselect && !this.grid) {
					 //¶àÑ¡Ê±,´¦Àí×Ö·û´®£¬ÈÃºóÌ¨¿ÉÒÔÓÃinÆ´³ösql
					// var reg2=new RegExp(",","g");    //Ìæ»»Ó¢ÎÄ,Îª','
					 var reg3=new RegExp(this.separator,"g");    //Ìæ»»Ó¢ÎÄ,Îª','
					 var tempValue = (value+"").replace(reg3,"','");
					
				 }else {
					 tempValue = value ;
				 }
				
				var combox = this ;
				Ext.Ajax.request({
					method: 'POST',
					url: url,
					success: function(response) {
						var reslutJson = response.responseText ;
						if(reslutJson.indexOf("ERROR|") > -1) {
							alert("ÏÂÀ­³õÊ¼»¯´íÎó,Ô­Òò£º"+reslutJson.replace("ERROR|")) ;
							return ;
						}
						
						if(!combox.multiselect || combox.grid) {
							//µ¥Ñ¡
							if(reslutJson != "") {
								//alert(reslutJson);
								var jsonArr = Ext.util.JSON.decode(reslutJson) ;
								if(combox.grid) {
									jsonArr = jsonArr.data ;
								}
								if(jsonArr){
									var i = 0 ;
									var rsValue = "" ;
									var rsText = "" ;  
									jsonArr = jsonArr[0] ; 
									for(var j in jsonArr) {
										
										if(i == 0) {
											rsValue = jsonArr[j] ;
										} 
										  
										if(i == 1) {
											rsText = jsonArr[j] ; 
											break ;
										}
										i++ ;
									}
									//var rsValue = eval(reslutJson)[0].value ;
									//var rsText = eval(reslutJson)[0].text ;
									if(rsValue == value && rsText!= value) {
										combox.setRawValue(rsText);    
									}
								}
							}
						}else {
							//¶àÑ¡
							if(reslutJson != "") {
								//±éÀú£¬Æ´,ºÅ·Ö¸ô×Ö·û´®
								var jsonArr = eval(reslutJson) ;
								var text = "" ;
								for(var i=0;i<jsonArr.length;i++) {
									if(jsonArr[i]){
										var rsText = jsonArr[i].text ;
										text += rsText + combox.separator ;
									}
								}
								
								if(text != "") {
									text = text.substring(0,text.length - 1) ;
									combox.setRawValue(text); 
								}
							} 
						}
		 			},
					failure: function() {
		 				alert("ºóÌ¨·¢ÉúÒì³£,ÏÂÀ­³õÊ¼»¯Ê§°Ü!");
		 			},
		 			params: {
		 				autoid:combox.autoid,
	            		multilevel:combox.multilevel,
	            		multiselect:combox.multiselect,
		 				grid:combox.grid,
		 				checkmode:true,
		 				pk1:tempValue,
		 				refer:queryParam[0],
		 				refer1:queryParam[1],
		 				refer2:queryParam[2]
		 			}
				});
				
			}
		}) ;
		
		if(mtCombox.el.dom.id) {
			Ext.get(mtCombox.el.dom.id).dom.property = property;
			Ext.get(mtCombox.el.dom.id).dom.inputId = id;
		}
		mtCombox.addClass(className) ;
		
		if(onchange)   
			mtCombox.on("change",onchange) ;
		
		if(value && value != "") {
			mtCombox.setRealValue(value) ;
		}
		
	}) ;
	
	if(selectAttributeName == "autoid") {
		//Èç¹ûÊôĞÔÉè¼Æ³ÉÁËautoid ¾ÍÆÁ±ÎÔ­ÓĞÏÂÀ­µÄÊÂ¼ş£¬·ÀÖ¹±¨´í
		var onKeyDownEvent = Ext.emptyFn() ;
		var onKeyUpEvent = Ext.emptyFn() ;
		var onPopDivClick = Ext.emptyFn() ;
	}
} ;

var mt_select_selectAll = function(autoid,inputId,obj) {
	var check = obj.checked ;
	var checkboxs = Ext.query("input[name=mt_gridMultiSelect_"+inputId+"_"+ autoid +"]") ;
	for(var i=0;i<checkboxs.length;i++) {
		checkboxs[i].checked = check ;
	}
} ;

var mt_select_formatNumber = function(v) {
	var result = "";
	if(parseFloat(v) < 0 ) {  
		result = "<div style='color:red' >" + formatDecimal(v,2) +  "</div>";
	} else {
		result = "<div style='color:blue' >" + formatDecimal(v,2) +  "</div>";
	}
	
	return result;
};

var mt_select_groupSelect = function(autoid,inputId,obj) {
	var group = obj.group ;
	if(!group) return ; 
	var check = obj.checked ;
	var checkboxs = Ext.query("input[name=mt_gridMultiSelect_"+inputId+"_"+ autoid +"]") ;
	for(var i=0;i<checkboxs.length;i++) {
		if(checkboxs[i].group == group)
			checkboxs[i].checked = check ;
	}
} ;



Ext.onReady(initCombox) ;
  
