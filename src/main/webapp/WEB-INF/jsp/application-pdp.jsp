<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix='fn' uri='http://java.sun.com/jsp/jstl/functions' %>

<jsp:include page="includes/header.jsp" />

<div class="container">
	<div class="row">
		<div class="col-md-12">
			<h1>Application PDP: <c:out value="${pdp.id}" /></h1>
			<h2>Status</h2>
			<p><c:out value="${pdp.status}" /></p>
			<h2>Policy</h2>
			<?prettify lang=xml linenums=true?>
			<pre class="prettyprint linenums"><c:out value="${pdp.policy}" /></pre>
		</div>
	</div>
</div>

<jsp:include page="includes/footer.jsp" />
