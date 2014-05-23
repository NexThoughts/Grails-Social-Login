<%@ page import="com.social.SocialProfileType;" %>
<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
    <meta name="layout" content="main">
</head>

<body>
<div class="container">
    <div class="row">
        <div class="col-xs-12 col-md-3">
            <g:link class="btn btn-default btn-lg" controller="signIn" action="connectFacebook"><img src="${resource(dir: 'images/icons',file: 'facebook.png')}"> ${socialProfileList.any{it.type==SocialProfileType.FACEBOOK} ? 'Update':'Connect'} Facebook</g:link>
        </div>

        <div class="col-xs-12 col-md-3">
            <g:link class="btn btn-default btn-lg" controller="signIn" action="connectGoogle"><img src="${resource(dir: 'images/icons',file: 'google.png')}"> ${socialProfileList.any{it.type==SocialProfileType.GOOGLE} ? 'Update':'Connect'} Google</g:link>
        </div>

        <div class="col-xs-12 col-md-3">
            <g:link class="btn btn-default btn-lg" controller="signIn" action="connectLinkedIn"><img src="${resource(dir: 'images/icons',file: 'linkedin.png')}"> ${socialProfileList.any{it.type==SocialProfileType.LINKED_IN} ? 'Update':'Connect'} LinkedIn</g:link>
        </div>

        <div class="col-xs-6 col-md-3">
            <g:link class="btn btn-default btn-lg" controller="signIn" action="index"> <img src="${resource(dir: 'images/icons',file: 'twitter.png')}"> ${socialProfileList.any{it.type==SocialProfileType.TWITTER} ? 'Update':'Connect'} Twitter</g:link>
        </div>
    </div>
     <br/>
     <br/>
    <div class="row">
        <div class="col-xs-12 col-md-3">
            <ul class="list-group">
                <g:form controller="home" action="profile">
                    <li class="list-group-item"><h3>Edit Account</h3></li>
                    <li class="list-group-item">
                    <g:submitButton name="Edit Account" value="Edit Account" class="btn btn-success"/>
                    </li>
                </g:form>
            </ul>
        </div>
    </div>
</div>
</body>
</html>
