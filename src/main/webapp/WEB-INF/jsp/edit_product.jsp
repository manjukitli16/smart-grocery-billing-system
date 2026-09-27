<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/fragments/header.jsp"><jsp:param name="title" value="Edit product"/><jsp:param name="active" value="inventory"/><jsp:param name="breadcrumb" value="Edit product"/></jsp:include>
<div class="page-heading"><div><p class="eyebrow">CATALOG</p><h1>Edit product</h1><p class="muted">Update the details for <c:out value="${product.name}"/>.</p></div></div>
<section class="panel form-panel"><form method="post" action="${pageContext.request.contextPath}/inventory/${product.id}/edit" class="product-form">
  <label>Product name<input name="name" required maxlength="120" value="<c:out value='${empty form.name ? product.name : form.name}'/>"></label>
  <label>Category<input name="category" maxlength="80" value="<c:out value='${empty form.category ? product.category : form.category}'/>" placeholder="e.g. Produce"></label>
  <div class="form-row"><label>Unit price<div class="input-prefix"><span>₹</span><input type="number" name="price" required min="0" step="0.01" value="<c:out value='${empty form.price ? product.price : form.price}'/>"></div></label><label>In stock<input type="number" name="stock" required min="0" step="1" value="<c:out value='${empty form.stock ? product.stock : form.stock}'/>"></label></div>
  <div class="form-actions"><a class="button button-quiet" href="${pageContext.request.contextPath}/inventory">Cancel</a><button class="button button-primary" type="submit">Save changes</button></div>
</form></section>
<jsp:include page="/WEB-INF/jsp/fragments/footer.jsp"/>