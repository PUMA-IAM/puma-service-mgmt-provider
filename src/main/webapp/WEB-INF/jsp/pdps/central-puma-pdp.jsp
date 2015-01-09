<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<jsp:include page="../includes/header.jsp" />

<div class="container">
	<div class="row">
		<div class="col-md-12">
			<h1>Overview</h1>
			<h3>Status</h3>
			<c:forEach var="pdp" items="${pdps}" varStatus="status">
				<p><c:out value="${pdp.langType}: ${pdp.status}" /></p>
			</c:forEach>
		</div>
	</div>

	<div class="row">
		<div class="col-md-12">
			<h1>Policy</h1>
		</div>
	</div>

	<div class="row">
		<div class="col-md-12">
			<h2>Current</h2>
				<h3>STAPL</h3>
				<pre class="prettyprint linenums"><c:out value="${central_policy_stapl}" /></pre>
				
				<h3>XACML</h3>
				<pre class="prettyprint linenums"><c:out value="${central_policy_xacml}" /></pre>
		</div>
	</div>

	<div class="row">
		<div class="col-md-12">
			<h2>Update</h2>
			<form role="form" method="post"
				action="<c:url value="/central-puma-pdp/policy/load"/>">
				<h3>STAPL</h3>
				<div class="form-group">
					<textarea class="form-control" style="width: 100%; height: 500px;"
						name="staplPolicy"><c:out value="${central_policy_stapl}" /></textarea>
				</div>
				<h3>XACML</h3>
				<div class="form-group">
					<textarea class="form-control" style="width: 100%; height: 500px;"
						name="xacmlPolicy"><c:out value="${central_policy_xacml}" /></textarea>
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
			<h3>STAPL</h3>
			<pre class="prettyprint linenums"><c:out value="${default_policy_stapl}" /></pre>
			<h3>XACML</h3>
			<pre class="prettyprint linenums"><c:out value="${default_policy_xacml}" /></pre>
			<a href="<c:url value="/central-puma-pdp/policy/load/default"/>" class="btn btn-default">Restore</a>
		</div>
	</div>
</div>

<jsp:include page="../includes/footer.jsp" />
