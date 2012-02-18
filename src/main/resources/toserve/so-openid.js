var providers_large = {
	stackexchange : {
		name : "Stack_Exchange",
		url : "https://openid.stackexchange.com/",
		x : -1,
		y : -518
	},
	google : {
		name : "Google",
		url : "https://www.google.com/accounts/o8/id",
		x : -1,
		y : -1
	},
	yahoo : {
		name : "Yahoo",
		url : "http://yahoo.com/",
		x : -1,
		y : -63
	},
	myopenid : {
		name : "MyOpenID",
		url : "http://myopenid.com/",
		x : -1,
		y : -187
	},
	facebook : {
		name : "Facebook",
		oauth_version : "2.0",
		oauth_server : "https://graph.facebook.com/oauth/authorize",
		x : -1,
		y : -456
	}
};
var providers_small_large_equivalents = {
	stackexchange : {
		name : "Stack_Exchange",
		url : "https://openid.stackexchange.com/",
		x : -27,
		y : -352
	},
	google : {
		name : "Google",
		url : "https://www.google.com/accounts/o8/id",
		x : -27,
		y : -274
	},
	yahoo : {
		name : "Yahoo",
		url : "http://yahoo.com/",
		x : -27,
		y : -300
	},
	myopenid : {
		name : "MyOpenID",
		url : "http://myopenid.com/",
		x : -27,
		y : -326
	}
};
var providers_small = {
	livejournal : {
		name : "LiveJournal",
		label : "Enter your LiveJournal username",
		url : "http://{username}.livejournal.com/",
		x : -1,
		y : -352
	},
	wordpress : {
		name : "Wordpress",
		label : "Enter your Wordpress.com username",
		url : "http://{username}.wordpress.com/",
		x : -1,
		y : -404
	},
	blogger : {
		name : "Blogger",
		label : "Enter your Blogger account",
		url : "http://{username}.blogspot.com/",
		x : -1,
		y : -249
	},
	verisign : {
		name : "Verisign",
		label : "Enter your Verisign username",
		url : "http://{username}.pip.verisignlabs.com/",
		x : -1,
		y : -378
	},
	claimid : {
		name : "ClaimID",
		label : "Enter your ClaimID username",
		url : "http://claimid.com/{username}",
		x : -1,
		y : -326
	},
	clickpass : {
		name : "ClickPass",
		label : "Enter your ClickPass username",
		url : "http://clickpass.com/public/{username}",
		x : -1,
		y : -274
	},
	google_profile : {
		name : "Google_Profile",
		label : "Enter your Google Profile username",
		url : "https://profiles.google.com/{username}",
		x : -1,
		y : -300
	},
	aol : {
		name : "AOL",
		label : "Enter your AOL screenname",
		url : "http://openid.aol.com/{username}",
		x : -27,
		y : -249
	}
};
var providers_custom = {
	launchpad : {
		name : "Launchpad",
		label : "Enter your Launchpad username",
		url : "https://launchpad.net/~{username}",
		x : -1,
		y : -1
	},
	steam : {
		name : "Steam",
		url : "http://steamcommunity.com/openid",
		x : -1,
		y : -63
	}
};
var providers = $.extend({}, providers_large, providers_small);
window.stringify = window.JSON ? JSON.stringify : function(b) {
	if (typeof b == "string") {
		return '"'
				+ b.replace(/\\/g, "\\\\").replace(/"/g, '\\"').replace(/\n/g,
						"\\n").replace(/\r/g, "\\r") + '"'
	} else {
		if (typeof b == "number") {
			return b.toString()
		} else {
			if (b == null) {
				return "null"
			} else {
				if (b == undefined) {
					return "undefined"
				} else {
					if (b.length != undefined) {
						return "[" + $.map(b, stringify).join(",") + "]"
					}
				}
			}
		}
	}
	var a = "{";
	for ( var c in b) {
		a = a + stringify(c) + ":" + stringify(b[c]) + ","
	}
	if (a.length > 1) {
		a = a.substr(0, a.length - 1)
	}
	return a + "}"
};
var openid = {
	use_affiliate_post : false,
	facebook_app_id : undefined,
	facebook_login_params : {
		scope : "email"
	},
	localKey : "login-prefs",
	prefs : {},
	img_path : null,
	img_path_custom : "http://sstatic.net/Img/openid/openid-logos-custom.png?v=3",
	input_id : null,
	provider_url : null,
	suppress_hash_switch : false,
	switchAllToSmall : function(a) {
		$.extend(providers_small_large_equivalents, providers_small);
		delete providers_small_large_equivalents.google_profile;
		providers_small = providers_small_large_equivalents;
		providers_large = a
	},
	getPrefs : function() {
		if (!!window.localStorage) {
			var a = window.localStorage.getItem(this.localKey);
			if (a) {
				this.prefs = $.parseJSON(a)
			}
		}
		return this.prefs
	},
	setPref : function(a, b) {
		if (!!window.localStorage) {
			this.prefs[a] = b;
			window.localStorage.setItem(this.localKey, stringify(this.prefs))
		}
	},
	init : function(n, f, g, l) {
		openid.img_path = g;
		openid.use_affiliate_post = l;
		$("body").css("cursor", "default");
		$("#openid_submit").css("cursor", "default");
		$("input[type=submit]").removeAttr("disabled");
		this.input_id = n;
		$("#openid_choice").show();
		if (f) {
			var d = f.split(",");
			for ( var c = 0; c < d.length; c++) {
				var k = d[c];
				var h = providers_custom[k];
				if (!h) {
					StackExchange.debug.log('unknown custom openid provider "'
							+ k + '"');
					continue
				}
				if (providers[k]) {
					StackExchange.debug.log('openid provider "' + k
							+ '" already in list');
					continue
				}
				providers_large[k] = h;
				providers[k] = h;
				h.isCustom = true
			}
		}
		var a = $("#openid_btns");
		for (id in providers_large) {
			a.append(this.getBoxHTML(providers_large[id], "large"))
		}
		a.append("<br>");
		var m = $("#more-openid-options");
		for (id in providers_small) {
			m.append(this.getBoxHTML(providers_small[id], "small"))
		}
		$("#openid_form").submit(this.submitx);
		var j = $("#show-more-options").appendTo(a).hide();
		var b = $("#more-options-link").click(function() {
			b.detach();
			j.show("fast");
			openid.setPref("showMoreOpenIdOptions", true)
		});
		this.prefs = openid.getPrefs();
		if (this.prefs.showMoreOpenIdOptions) {
			b.detach();
			j.show()
		}
		if (this.prefs.provider) {
			this.signin(this.prefs.provider, true);
			if (this.prefs.openid) {
				if (this.prefs.username) {
					$("#openid_username").val(this.prefs.username)
				}
				$("#" + this.input_id).val(this.prefs.openid)
			} else {
				if (this.prefs.oauth_server) {
					$("#oauth_version").val(this.prefs.oauth_version);
					$("#oauth_server").val(this.prefs.oauth_server)
				}
			}
		}
		var e = window.location.hash;
		if (e && e != "#") {
			switch (e) {
			case "#log-in":
				openid.loadSignin();
				break;
			case "#create-account":
				openid.loadSignup();
				break
			}
		}
		$(window).bind("hashchange", function() {
			if (suppress_hash_switch) {
				suppress_hash_switch = false;
				return
			}
			var i = window.location.hash;
			if (!i || i == "#") {
				window.location.reload();
				return
			}
			switch (i) {
			case "#log-in":
				openid.loadSignin();
				break;
			case "#create-account":
				openid.loadSignup();
				break
			}
		})
	},
	getBoxHTML : function(f, c) {
		var b = "background: #fff url("
				+ (f.isCustom ? this.img_path_custom : this.img_path)
				+ "); background-position: " + f.x + "px " + f.y + "px";
		var e = f.name.toLowerCase();
		var a = e + " openid_" + c + "_btn";
		if (f.custom_style) {
			b = f.custom_style;
			a = e
		}
		var d = '<a title="log in with ' + f.name.replace("_", " ")
				+ '" href="javascript:openid.signin(\'' + e + '\');" style="'
				+ b + '" class="' + a + '"></a>';
		return d
	},
	facebookLogin : function(a, b, c) {
		if (c) {
			return
		}
		$("." + b).css("cursor", "wait");
		if (openid.facebook_app_id) {
			openid.doJsFacebookLogin()
		} else {
			this.setOAuthInfo(a.oauth_version, a.oauth_server);
			if (!c) {
				$("#openid_form").submit()
			}
		}
	},
	loadFacebookApi : function(a, b, d) {
		openid.facebook_app_id = a;
		openid.facebook_login_params.scope = b;
		$("body").append($('<div id="fb-root"></div>'));
		$("body")
				.append(
						$('<script src="http://connect.facebook.net/en_US/all.js"></script>'));
		var c;
		c = setInterval(function() {
			if (!!window.FB && !!window.FB.init) {
				clearInterval(c);
				window.FB.init({
					appId : a,
					status : false,
					cookie : false,
					xfbml : false,
					oauth : true
				});
				d()
			} else {
				return
			}
		}, 50)
	},
	doJsFacebookLogin : function(a) {
		window.FB.login(function(b) {
			if (b.authResponse) {
				var c = "/users/oauth/facebook/js?accessToken="
						+ encodeURI(b.authResponse.accessToken);
				if (a) {
					c += "&returnUrl=" + encodeURI(a)
				}
				window.location = c
			}
		}, openid.facebook_login_params)
	},
	signin : function(c, b) {
		openid.setPref("provider", c);
		if (c == "stack_exchange") {
			if (b) {
				this.highlight(c);
				openid.setOpenIdUrl("")
			} else {
				openid.loadSignin();
				openid.setOpenIdUrl("")
			}
			return
		}
		var a = providers[c];
		if (!a) {
			return
		}
		if (c == "facebook") {
			this.facebookLogin(a, c, b);
			return
		}
		this.highlight(c);
		if (c == "openid") {
			$("#openid_input_area").hide();
			this.setOpenIdUrl("");
			$("#" + this.input_id).focus();
			return
		}
		if (a.label) {
			this.useInputBox(a);
			this.provider_url = a.url
		} else {
			if (!b) {
				$("." + c).css("cursor", "wait")
			}
			this.setOpenIdUrl(a.url);
			this.provider_url = null;
			if (!b) {
				$("#openid_form").submit()
			}
		}
	},
	submitx : function() {
		var a = $("#openid_username").val();
		openid.setPref("username", a);
		openid.setPref("openid", $("#" + this.input_id).val());
		if (a == "") {
			return true
		}
		$("body").css("cursor", "wait");
		$("#openid_submit").css("cursor", "wait");
		$("input[type=submit]", this).attr("disabled", "disabled");
		var b = openid.provider_url;
		if (b) {
			b = b.replace("{username}", a);
			openid.setPref("openid", b);
			openid.setOpenIdUrl(b)
		}
		return true
	},
	setOpenIdUrl : function(a) {
		$("#" + this.input_id).val(a);
		$("#oauth_version").val("");
		$("#oauth_server").val("");
		openid.setPref("oauth_version", "");
		openid.setPref("oauth_server", "");
		openid.setPref("openid", a)
	},
	setOAuthInfo : function(b, a) {
		$("#oauth_version").val(b);
		$("#oauth_server").val(a);
		$("#" + this.input_id).val("");
		openid.setPref("oauth_version", b);
		openid.setPref("oauth_server", a);
		openid.setPref("openid", "")
	},
	highlight : function(a) {
		$("#openid_highlight a").unwrap();
		$("." + a).wrap('<div id="openid_highlight"></div>')
	},
	useInputBox : function(a) {
		$("#openid_provider_label").html(a.label);
		$("#openid_input_area").show();
		$("#openid_username").val("").focus();
		$("#more-options-link").detach();
		$("#show-more-options").show("fast")
	},
	setPageTitle : function(a) {
		suppress_hash_switch = true;
		$(".subheader").find("h1").text(a);
		window.location.hash = a.toLowerCase().replace(" ", "-")
	},
	addSignupSidebar : function() {
		var b = $("#sidebar").empty().css("width", "320px");
		$("#mainbar").css("width", "625px");
		var c = $(
				'<div class="module newuser"><h4>How to Log In</h4>    <p>Once you create your Stack Exchange account you can use it to log in to any <a href="http://stackexchange.com/sites">Stack Exchange site</a>.</p>    <p>To log in, click the \'Log in with Stack Exchange\' button.</p> </div>')
				.appendTo(b);
		var a = $("<div></div>").css("height", "80px").appendTo(c);
		$(this.getBoxHTML(providers_large.stackexchange, "large")).attr("href",
				"#log-in").click(function() {
			openid.loadSignin()
		}).appendTo(a)
	},
	loadSignup : function() {
		$(
				"#openid_choice, #simple-openid-selector, #forgot-password, #affiliate-signin-iframe")
				.hide();
		openid.setPageTitle("Create Account");
		var c = $("#affiliate-signup");
		c.empty();
		var a = $("#openid_form");
		var d = $('<div id="loading-signup"></div>');
		a.prepend(d);
		StackExchange.helpers.addSpinner(d);
		if (window.postMessage) {
			var b = function(f) {
				if (f.data == "loaded") {
					$("#affiliate-iframe").show();
					d.detach()
				}
			};
			if (window.addEventListener) {
				window.addEventListener("message", b, false)
			} else {
				window.attachEvent("onmessage", b)
			}
		}
		$
				.ajax({
					url : "/users/signup",
					data : null,
					success : function(e) {
						c.empty();
						var f = $('<iframe id="affiliate-iframe" style="width:100%;height:480px" src="'
								+ e + '"></iframe>');
						c.append(f);
						openid.addSignupSidebar();
						if (window.postMessage) {
							$(f).hide()
						} else {
							d.detach()
						}
					},
					error : function() {
						c.empty();
						c.innerText("Failed to load affiliate form");
						d.detach()
					},
					type : (openid.use_affiliate_post ? "POST" : "GET")
				})
	},
	loadSignin : function() {
		$("#openid_choice, #simple-openid-selector").hide();
		var d = $("#forgot-password");
		d.show();
		$("#sidebar").hide();
		$("#affiliate-iframe").detach();
		$("#affiliate-signup").find("p").first().text("Don't have an account?");
		var e = $("#openid_choice");
		var a = e.parent();
		e.detach();
		openid.setPageTitle("Log In");
		var b = $('<div id="loading-signup"></div>');
		a.prepend(b);
		StackExchange.helpers.addSpinner(b);
		var c = $("#forgot-password").find("a");
		c.attr("href", "https://openid.stackexchange.com/account/recovery");
		if (window.postMessage) {
			var f = function(g) {
				if (g.data == "signin-loaded") {
					$("#loading-signup .ajax-loader").detach();
					$("#affiliate-signin-iframe").show()
				}
			};
			if (window.addEventListener) {
				window.addEventListener("message", f, false)
			} else {
				window.attachEvent("onmessage", f)
			}
		}
		$
				.ajax({
					url : "/users/signin",
					data : null,
					success : function(g, i, j) {
						if (!window.postMessage) {
							b.empty()
						}
						var h = $('<iframe id="affiliate-signin-iframe" style="width:100%;height:165px" src="'
								+ g + '"></iframe>');
						b.prepend(h);
						if (window.postMessage) {
							$(h).hide()
						}
					},
					error : function(g, h, i) {
						b.empty();
						b.innerText("Failed to load affiliate form")
					},
					type : (openid.use_affiliate_post ? "POST" : "GET")
				})
	}
};