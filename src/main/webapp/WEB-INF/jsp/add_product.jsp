<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/jsp/fragments/header.jsp"><jsp:param name="title" value="Add product"/><jsp:param name="active" value="inventory"/><jsp:param name="breadcrumb" value="Add product"/></jsp:include>
<div class="page-heading"><div><p class="eyebrow">CATALOG</p><h1>Add a product</h1><p class="muted">Enter the product details to add it to your store.</p></div></div>
<section class="panel form-panel"><form method="post" action="${pageContext.request.contextPath}/inventory/add" class="product-form">
  <label>Product name<input name="name" required maxlength="120" value="<c:out value='${form.name}'/>" placeholder="e.g. Fuji apples"></label>
  <label>Category<input name="category" maxlength="80" value="<c:out value='${form.category}'/>" placeholder="e.g. Produce"></label>
  <div class="form-row"><label>Unit price<div class="input-prefix"><span>₹</span><input type="number" name="price" required min="0" step="0.01" value="<c:out value='${form.price}'/>" placeholder="0.00"></div></label><label>Opening stock<input type="number" name="stock" required min="0" step="1" value="<c:out value='${form.stock}'/>" placeholder="0"></label></div>
  <div class="form-actions"><a class="button button-quiet" href="${pageContext.request.contextPath}/inventory">Cancel</a><button class="button button-primary" type="submit">Save product</button></div>
</form></section>
<jsp:include page="/WEB-INF/jsp/fragments/footer.jsp"/>