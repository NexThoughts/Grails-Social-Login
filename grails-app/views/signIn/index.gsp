<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Welcome to Social Login</title>
	</head>
	<body>

		<div id="page-body" role="main">
			<h1>Create Account</h1>
		</div>

    <div class="row">
        <br/>
    </div>
    <div class="row">
        <g:hasErrors bean="${signUpVO}">
            <ul class="errors" role="alert">
                <g:eachError bean="${signUpVO}" var="error">
                    <li style="color: red" <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message error="${error}"/></li>
                </g:eachError>
            </ul>
        </g:hasErrors>
        <g:form controller="signIn" action="createUser">
        <table class="table table-bordered">
            <tr>
                <td>Name</td>
                <td><g:textField name="name" value="${signUpVO.name}"/></td>
            </tr>
            <tr>
                <td>Email</td>
                <td><g:textField name="email" value="${signUpVO.email}"/></td>
            </tr>
            <tr>
                <td>Password</td>
                <td><g:passwordField name="password" value=""/></td>
            </tr>
            <tr>
                <td>Confirm Password</td>
                <td><g:passwordField name="confirmPassword" value=""/></td>
            </tr>
            <tr>
                <td></td>
                <td><g:submitButton name="Submit" value="Create Account" class="btn btn-primary"/></td>
            </tr>

        </table>
        </g:form>
    </div>
	</body>
</html>
