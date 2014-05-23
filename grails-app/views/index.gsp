<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Welcome to Social Login</title>
	</head>
	<body>

		<div id="page-body" role="main">
			<h1>Welcome to Grails</h1>
			<p>Congratulations, you have successfully started your first Grails application! At the moment
			   this is the default page, feel free to modify it to either redirect to a controller or display whatever
			   content you may choose. Below is a list of controllers that are currently deployed in this application,
			   click on each to execute its default action:</p>
		</div>

    <div class="row">
        <br/>
        <br/>
        <br/>
        <br/>
        <br/>
    </div>
    <div class="row">
        <div class="col-xs-12 col-md-3">

            <g:link class="btn btn-default btn-lg" controller="signIn" action="connectFacebook"><img src="${resource(dir: 'images/icons',file: 'facebook.png')}"> Connect Facebook</g:link>

        </div>

        <div class="col-xs-12 col-md-3">

            <g:link class="btn btn-default btn-lg" controller="signIn" action="connectGoogle"><img src="${resource(dir: 'images/icons',file: 'google.png')}"> Connect Google</g:link>
        </div>

        <div class="col-xs-12 col-md-3">

            <g:link class="btn btn-default btn-lg" controller="signIn" action="connectLinkedIn"><img src="${resource(dir: 'images/icons',file: 'linkedin.png')}"> Connect LinkedIn</g:link>

        </div>

        <div class="col-xs-6 col-md-3">
            <g:link class="btn btn-default btn-lg" controller="signIn" action="connectTwitter"> <img src="${resource(dir: 'images/icons',file: 'twitter.png')}"> Connect Twitter</g:link>

        </div>
    </div>

    <div class="row">
        <br/>
        <br/>
      <h4 style="text-align: center"> - OR - </h4>
        <br/>
        <br/>
    </div>
    <div class="row">
        <div class="col-xs-12 col-md-4">
        </div>

        <div class="col-xs-12 col-md-4" style="text-align: center">
            <g:link class="btn btn-primary btn-lg" controller="signIn" action="signup">SignUp via Email</g:link>

        </div>

        <div class="col-xs-12 col-md-4">
        </div>
    </div>
	</body>
</html>
