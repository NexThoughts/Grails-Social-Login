<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Grails"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${resource(dir: 'images', file: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}">
		%{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">--}%
		%{--<link rel="stylesheet" href="${resource(dir: 'css', file: 'mobile.css')}" type="text/css">--}%
		<g:layoutHead/>
        <r:require module="bootstrap"/>
        <r:layoutResources />
	</head>
	<body>
    <div class="container">
        <nav class="navbar navbar-default" role="navigation">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
            <sec:ifLoggedIn>
                <a class="navbar-brand" href="${createLink(controller: 'home',action: 'index')}">
                    Social App
                </a>
            </sec:ifLoggedIn>
            <sec:ifNotLoggedIn>
                <a class="navbar-brand" href="${createLink(uri: '/')}">
                    Social App
                </a>
            </sec:ifNotLoggedIn>
        </div>
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav navbar-right">
                    <sec:ifNotLoggedIn>
                        <li><a href="${createLink(controller: 'signIn',action: 'signup')}">Sign Up</a></li>
                    </sec:ifNotLoggedIn>

                    <sec:ifLoggedIn>
                        <li><a href="${createLink(controller: 'logout',action: 'index')}"><sec:username/> Logout</a></li>
                    </sec:ifLoggedIn>
                    <sec:ifNotLoggedIn>
                        <li><a href="${createLink(controller: 'login',action: 'auth')}">Login</a></li>
                    </sec:ifNotLoggedIn>
                </ul>
            </div><!-- /.navbar-collapse -->
        </nav>

        <g:layoutBody/>
        <div class="footer" role="contentinfo"></div>
        <div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
        <g:javascript library="application"/>
        <r:layoutResources />
    </div>

    </body>
</html>
