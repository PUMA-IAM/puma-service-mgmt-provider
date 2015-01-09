<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<jsp:include page="../includes/header.jsp" />

<div class="container">
	<div class="row">
		<div class="col-md-12">
			<h1 class="page-header">
				<c:out value="${tenant.name}" />
			</h1>
			<h3>Policy Language</h3>
			<p>
				<c:out value="${tenant.policyLanguage}" />
			</p>
			<h3>Management type</h3>
			<p>
				<c:out value="${tenant.managementType}" />
			</p>
			<h3>Super-tenant</h3>
			<p>
				<c:choose>
					<c:when test="${empty tenant.superTenant}">None</c:when>
					<c:otherwise>
						<a href="<c:url value="/tenants/${tenant.superTenant.id}"/>"><c:out value="${tenant.superTenant.name}" /></a>
					</c:otherwise>
				</c:choose>
			</p>
			<h3>Authentication End-point</h3>
			<p>
				<c:choose>
					<c:when test="${empty tenant.authnRequestEndpoint}">N/A</c:when>
					<c:otherwise>
						<c:out value="${tenant.authnRequestEndpoint}" />
					</c:otherwise>
				</c:choose>
			</p>
			<h3>IdP Public Key</h3>
			<p>
				<c:choose>
					<c:when test="${empty tenant.identityProviderPublicKey}">N/A</c:when>
					<c:otherwise>
						<c:out value="${tenant.identityProviderPublicKey}" />
					</c:otherwise>
				</c:choose>
			</p>
			<h3>Attribute Service Endpoint</h3>
			<p>
				<c:choose>
					<c:when test="${empty tenant.attrRequestEndpoint}">N/A</c:when>
					<c:otherwise>
						<c:out value="${tenant.attrRequestEndpoint}" />
					</c:otherwise>
				</c:choose>
			</p>
			<h3>Authorization End-point</h3>
			<p>
				<c:choose>
					<c:when test="${empty tenant.authzRequestEndpoint}">N/A</c:when>
					<c:otherwise>
						<c:out value="${tenant.authzRequestEndpoint}" />
					</c:otherwise>
				</c:choose>
			</p>
			<h3>Subtenants</h3>
			<c:choose>
				<c:when test="${empty tenant.subtenants}">None</c:when>
				<c:otherwise>
					<table class="table table-hover">
						<thead>
							<tr>
								<th>Name</th>
								<th></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="subtenant" items="${tenant.subtenants}"
								varStatus="status">
								<tr>
									<td><c:out value="${subtenant.name}" /></td>
									<td><a class="btn btn-primary btn-sm"
										href="<c:url value="/tenants/${subtenant.id}"/>"><span
											class="glyphicon glyphicon-chevron-right"></span> View
											details</a> <a class="btn btn-danger btn-sm"
										href="<c:url value="/tenants/${subtenant.id}/delete"/>"><span
											class="glyphicon glyphicon-chevron-right"></span> Delete</a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<div class="row">
		<div class="col-md-12">
			<h1 class="page-header">Create subtenant</h1>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<form class="form-horizontal" role="form" method="post"
				action="<c:url value="/tenants/${tenant.id}/subtenants/create-impl"/>">
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
						<c:choose>
							<c:when test="${empty managementValues}">
							</c:when>
							<c:otherwise>
								<c:forEach var="mgmtValue" items="${managementValues}" varStatus="status">
									<div class="radio">
										<label>
											<input type="radio" name="mgmt-type" id="mgmt-type-option-${mgmtValue}" value="${mgmtValue}">
												${mgmtValue.description}
										</label>
									</div>
								</c:forEach>
							</c:otherwise>
						</c:choose>
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
					<label for="input-userName" class="col-sm-2 control-label">Username</label>
					<div class="col-sm-10">
						<input name="userName" class="form-control"
							id="input-userName" placeholder="Name">
					</div>
				</div>
				<div class="form-group">
					<label for="input-password" class="col-sm-2 control-label">Password</label>
					<div class="col-sm-10">
						<input name="password" class="form-control"
							id="input-password" placeholder="Password">
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
				if ($(this).val() == "Locally") {
					$("#input-authn-endpoint").prop('disabled', true);
					$("#input-idp-public-key").prop('disabled', true);
					$("#input-attr-endpoint").prop('disabled', true);
					$("#input-authz-endpoint").prop('disabled', true);
					$("#input-userName").prop('disabled', false);
					$("#input-password").prop('disabled', false);
				} else if ($(this).val() == "FederatedAuthentication") {
					$("#input-authn-endpoint").prop('disabled', false);
					$("#input-idp-public-key").prop('disabled', false);
					$("#input-attr-endpoint").prop('disabled', false);
					$("#input-authz-endpoint").prop('disabled', true);
					$("#input-userName").prop('disabled', true);
					$("#input-password").prop('disabled', true);
				} else { // fedauthz
					$("#input-authn-endpoint").prop('disabled', false);
					$("#input-idp-public-key").prop('disabled', false);
					$("#input-attr-endpoint").prop('disabled', false);
					$("#input-authz-endpoint").prop('disabled', false);
					$("#input-userName").prop('disabled', true);
					$("#input-password").prop('disabled', true);
				}
			});
		});
	})(jQuery);
</script>

<jsp:include page="../includes/footer.jsp" />