<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions'%>

<jsp:include page="../includes/header.jsp" />

<div class="container">
	<div class="row">
		<div class="col-md-12">
			<h1>Overview</h1>
			<p>
				Number of connected application PDPs:
				<c:out value="${fn:length(pdps)}" />
			</p>
			<table class="table table-hover">
				<thead>
					<tr>
						<th>#</th>
						<th>Status</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="pdp" items="${pdps}" varStatus="status">
						<tr>
							<td><c:out value="${pdp.id}" /></td>
							<td><c:out value="${pdp.status}" /></td>
							<td><a class="btn btn-primary btn-sm"
								href="<c:url value="/application-pdps/${pdp.id}"/>"><span
									class="glyphicon glyphicon-chevron-right"></span> View details</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

	<div class="row">
		<div class="col-md-12">
			<h1>Application policy</h1>
		</div>
	</div>

	<div class="row">
		<div class="col-md-12">
			<h2>Current</h2>
			<pre class="prettyprint linenums"><c:out value="${application_policy.content}" /></pre>
		</div>
	</div>

	<div class="row">
		<div class="col-md-12">
			<h2>Update</h2>
			<form role="form" method="post"
				action="<c:url value="/application-pdps/policy/load"/>">
				<div class="form-group">
					<textarea class="form-control" style="width: 100%; height: 500px;"
						name="policy"><c:out value="${application_policy.content}" /></textarea>
				</div>
				<div class="form-group">
					<button type="submit" class="btn btn-default">Deploy
						policy</button>
				</div>
			</form>
		</div>
	</div>

	<div class="row">
		<div class="col-md-12">
			<h2>Restore default</h2>
			<pre class="prettyprint linenums"><c:out value="${default_policy}" /></pre>
			<a href="<c:url value="/application-pdps/policy/load/default"/>" class="btn btn-default">Restore</a>
		</div>
	</div>
</div>

<jsp:include page="../includes/footer.jsp" />
