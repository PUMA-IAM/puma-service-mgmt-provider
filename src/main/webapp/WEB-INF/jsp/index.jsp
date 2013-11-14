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
	<!-- Example row of columns -->
	<div class="row">
		<div class="col-md-4">
			<a class="btn btn-primary btn-lg" role="button" href="<c:url value="/application-pdps"/>">Application PDPs &raquo;</a>
		</div>
		<div class="col-md-4">
			<a class="btn btn-primary btn-lg" role="button" href="">Something &raquo;</a>
		</div>
		<div class="col-md-4">
			<a class="btn btn-primary btn-lg" role="button" href="">Something else &raquo;</a>
		</div>
	</div>
</div>

<jsp:include page="includes/footer.jsp" />
