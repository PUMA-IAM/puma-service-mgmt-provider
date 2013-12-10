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
										href="<c:url value="/organizations/${org.id}/attribute-families/${af.id}/delete"/>"><span
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
				action="<c:url value="//organizations/${org.id}/attribute-families/create-impl"/>">
				<div class="form-group">
					<label for="input-name" class="col-sm-2 control-label">Name</label>
					<div class="col-sm-10">
						<input name="name" class="form-control" id="input-name"
							placeholder="Attribute Family Name">
					</div>
				</div>
				<div class="form-group">
					<label for="input-name" class="col-sm-2 control-label">XACML identifier</label>
					<div class="col-sm-10">
						<input name="xacmlid" class="form-control" id="input-name"
							placeholder="">
					</div>
				</div>
				<div class="form-group">
						<label for="input-name" class="col-sm-2 control-label">Datatype</label>
						<div class="controls">
							<select name="datatype" class="form-control">
								<c:forEach items="${datatypes}" var="datatype">
									<option value="${datatype}">${datatype}</option>
								</c:forEach>
							</select>
						</div>
				</div>				
				<div class="form-group">
						<label for="input-name" class="col-sm-2 control-label">Multiplicity</label>
						<div class="controls">
							<select name="multiplicity" class="form-control">
								<c:forEach items="${multiplicityValues}" var="multiplicityValue">
									<option value="${multiplicityValue}">${multiplicityValue}</option>
								</c:forEach>
							</select>
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