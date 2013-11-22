<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<jsp:include page="../includes/header.jsp" />

<div class="container">
	<div class="row">
		<div class="col-md-12">
			<h1 class="page-header">
				<c:out value="${org.name}" />
			</h1>
			<h3>Attribute families</h3>
			<c:choose>
				<c:when test="${empty org.attributeFamilies}">None</c:when>
				<c:otherwise>
					<table class="table table-hover">
						<thead>
							<tr>
								<th>Name</th>
								<th>Multiplicity</th>
								<th>Data type</th>
								<th></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="af" items="${org.attributeFamilies}"
								varStatus="status">
								<tr>
									<td><c:out value="${af.name}" /></td>
									<td><c:out value="${af.multiplicity}" /></td>
									<td><c:out value="${af.dataType}" /></td>
									<td><a class="btn btn-danger btn-sm"
										href="<c:url value="/attribute-families/${af.id}/delete"/>"><span
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
			<h1 class="page-header">Create Attribute Family</h1>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<form class="form-horizontal" role="form" method="post"
				action="<c:url value="/organizations/${org.id}/attribute-families/create-impl"/>">
				<div class="form-group">
					<label for="input-name" class="col-sm-2 control-label">Name</label>
					<div class="col-sm-10">
						<input name="name" class="form-control" id="input-name"
							placeholder="Name">
					</div>
				</div>
				<div class="form-group">
					<label for="input-mgmt-type" class="col-sm-2 control-label">Multiplicity</label>
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
					<label for="input-authn-endpoint" class="col-sm-2 control-label">Data type</label>
					<div class="col-sm-10">
						<input name="authn-endpoint" class="form-control"
							id="input-authn-endpoint" placeholder="URL" disabled>
					</div>
				</div>

				<div class="form-group">
					<div class="col-sm-offset-2 col-sm-10">
						<button type="submit" class="btn btn-default">Create
							attribute family</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>

<jsp:include page="../includes/footer.jsp" />