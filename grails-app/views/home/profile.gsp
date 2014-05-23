<%@ page import="com.social.SocialProfileType;" %>
<!DOCTYPE html>
<html>
<head>
    <title>Profile</title>
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


    <div class="row">

        <g:form controller="home" action="updateProfile">
            <h3>Manage Profile</h3>

            <table class="table table-bordered table-bordered">
                <g:if test="${flash.message}">
                <tr>
                    <td colspan="2" style="text-align: center"><span class="alert alert-success">${flash.message}</span></td>
                </tr>
                </g:if>


                <tr>
                    <td>Name:</td>
                    <td><g:textField name="name" value="${user.name}"/></td>
                </tr>
                <tr>
                    <td>Email:</td>
                    <td><g:textField name="username" value="${user.username}"/></td>
                </tr>
                <tr>
                    <td>Update:</td>
                    <td><g:submitButton name="Update Info" value="Update Info" class="btn btn-success"/></td>
                </tr>
            </table>
        </g:form>
    </div>
</div>
</body>
</html>
