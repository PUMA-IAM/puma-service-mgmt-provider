<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<jsp:include page="../includes/header.jsp" />

<div class="container">
	<div class="row">
		<div class="col-md-12">
			<h1 class="page-header">Overview</h1>
		</div>
	</div>
	<c:choose>
		<c:when test="${empty tenants}">None</c:when>
		<c:otherwise>
			<table class="table table-hover">
				<thead>
					<tr>
						<th>Name</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="tenant" items="${tenants}" varStatus="status">
						<tr>
							<td><c:out value="${tenant.name}" /></td>
							<td><a class="btn btn-primary btn-sm"
								href="<c:url value="/tenants/${tenant.id}"/>"><span
									class="glyphicon glyphicon-chevron-right"></span> View details</a>
								<a class="btn btn-danger btn-sm"
								href="<c:url value="/tenants/${tenant.id}/delete"/>"><span
									class="glyphicon glyphicon-chevron-right"></span> Delete</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:otherwise>
	</c:choose>

	<div class="row">
		<div class="col-md-12">
			<h1 class="page-header">Create new tenant</h1>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<form class="form-horizontal" role="form" method="post"
				action="<c:url value="/tenants/create-impl"/>">
				<div class="form-group">
					<label for="input-name" class="col-sm-2 control-label">Name</label>
					<div class="col-sm-10">
						<input name="name" class="form-control" id="input-name"
							placeholder="Name">
					</div>
				</div>
				<div class="form-group">
					<label for="input-mgmt-type" class="col-sm-2 control-label">Management
						type</label>
					<div class="col-sm-10">
						<div class="radio">
							<label> <input type="radio" name="mgmt-type"
								id="mgmt-type-option-locally" value="locally" checked>
								Locally
							</label>
						</div>
						<div class="radio">
							<label> <input type="radio" name="mgmt-type"
								id="mgmt-type-option-fedauthn" value="fedauthn">
								Federated authentication, local authorization
							</label>
						</div>
						<div class="radio">
							<label> <input type="radio" name="mgmt-type"
								id="mgmt-type-option-fedauthz" value="fedauthz">
								Federated authentication, federated authorization
							</label>
						</div>
					</div>
				</div>
				<div class="form-group">
					<label for="input-authn-endpoint" class="col-sm-2 control-label">IdP
						end-point</label>
					<div class="col-sm-10">
						<input name="authn-endpoint" class="form-control"
							id="input-authn-endpoint" placeholder="URL" disabled>
					</div>
				</div>
				<div class="form-group">
					<label for="input-authn-endpoint" class="col-sm-2 control-label">IdP
						public key</label>
					<div class="col-sm-10">
						<input name="idp-public-key" class="form-control"
							id="input-idp-public-key" placeholder="URL" disabled>
					</div>
				</div>
				<div class="form-group">
					<label for="input-attr-endpoint" class="col-sm-2 control-label">Attribute
						service end-point</label>
					<div class="col-sm-10">
						<input name="attr-endpoint" class="form-control"
							id="input-attr-endpoint" placeholder="URL" disabled>
					</div>
				</div>
				<div class="form-group">
					<label for="input-authz-endpoint" class="col-sm-2 control-label">Authorization
						end-point</label>
					<div class="col-sm-10">
						<input name="authz-endpoint" class="form-control"
							id="input-authz-endpoint" placeholder="URL" disabled>
					</div>
				</div>

				<div class="form-group">
					<label for="exampleInputFile" class="col-sm-2 control-label">Tenant
						logo</label>
					<div class="col-sm-10">
						<input name="file" type="file" id="input-logo">
					</div>
				</div>
				<div class="form-group">
					<label for="input-name" class="col-sm-2 control-label">Admin username</label>
					<div class="col-sm-10">
						<input name="loginName" class="form-control" id="input-name"
							placeholder="Username">
					</div>
				</div>
				<div class="form-group">
					<label for="input-name" class="col-sm-2 control-label">Admin password</label>
					<div class="col-sm-10">
						<input name="password" class="form-control" id="input-name"
							placeholder="Password">
					</div>
				</div>
				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-10">
						<button type="submit" class="btn btn-default">Create
							tenant</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>

<script>
	(function($) {
		$(function() {
			$('input:radio[name="mgmt-type"]').change(function() {
				if ($(this).val() == "locally") {
					$("#input-authn-endpoint").prop('disabled', true);
					$("#input-idp-public-key").prop('disabled', true);
					$("#input-attr-endpoint").prop('disabled', true);
					$("#input-authz-endpoint").prop('disabled', true);
				} else if ($(this).val() == "fedauthn") {
					$("#input-authn-endpoint").prop('disabled', false);
					$("#input-idp-public-key").prop('disabled', false);
					$("#input-attr-endpoint").prop('disabled', false);
					$("#input-authz-endpoint").prop('disabled', true);
				} else { // fedauthz
					$("#input-authn-endpoint").prop('disabled', false);
					$("#input-idp-public-key").prop('disabled', false);
					$("#input-attr-endpoint").prop('disabled', false);
					$("#input-authz-endpoint").prop('disabled', false);
				}
			});
		});
	})(jQuery);
</script>

<jsp:include page="../includes/footer.jsp" />
