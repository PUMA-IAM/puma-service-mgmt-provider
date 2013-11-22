<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:include page="includes/header.jsp" />

<div class="jumbotron">
	<div class="container">
		<h1>PUMA service mgmt provider</h1>
	</div>
</div>

<div class="container">
	<div class="row">
		<div class="col-md-12">
			<h1 class="page-header">Provider management</h1>
		</div>
	</div>
	<div class="row">
		<div class="col-md-4">
			<a class="btn btn-default btn-lg" role="button" href="<c:url value="/organizations/${provider.id}"/>">Overview &raquo;</a>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<h1 class="page-header">Tenant management</h1>
		</div>
	</div>
	<div class="row">
		<div class="col-md-4">
			<a class="btn btn-default btn-lg" role="button" href="<c:url value="/tenants"/>">Tenants &raquo;</a>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12">
			<h1 class="page-header">Policy management</h1>
		</div>
	</div>
	<div class="row">
		<div class="col-md-4">
			<a class="btn btn-default btn-lg" role="button" href="<c:url value="/application-pdps"/>">Application PDPs &raquo;</a>
		</div>
		<div class="col-md-4">
			<a class="btn btn-default btn-lg" role="button" href="<c:url value="/central-puma-pdp"/>">Central PUMA PDP &raquo;</a>
		</div>
	</div>
</div>

<jsp:include page="includes/footer.jsp" />
