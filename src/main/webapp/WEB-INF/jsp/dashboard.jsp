<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/WEB-INF/jsp/fragments/header.jsp"><jsp:param name="title" value="Overview"/><jsp:param name="active" value="overview"/><jsp:param name="breadcrumb" value="Overview"/></jsp:include>
<div class="page-heading"><div><p class="eyebrow">STORE OVERVIEW</p><h1>Good day, manager.</h1><p class="muted">Here is what is happening at your store today.</p></div><a class="button button-primary" href="${pageContext.request.contextPath}/billing"><span>＋</span> Start a sale</a></div>
<div class="metric-grid">
  <article class="metric"><div class="metric-top"><span>Sales today</span><span class="metric-icon green">↗</span></div><strong>₹<fmt:formatNumber value="${todayTotal}" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></strong><small>${todayCount} completed sale<c:if test="${todayCount != 1}">s</c:if></small></article>
  <article class="metric"><div class="metric-top"><span>Products</span><span class="metric-icon yellow">▤</span></div><strong>${productCount}</strong><small>Items in your catalog</small></article>
  <article class="metric"><div class="metric-top"><span>Needs attention</span><span class="metric-icon coral">!</span></div><strong>${lowStock.size()}</strong><small>Products at 5 units or fewer</small></article>
</div>
<div class="dashboard-grid">
  <section class="panel"><div class="section-heading"><div><h2>Recent sales</h2><p class="muted">Your latest completed transactions</p></div><a class="text-link" href="${pageContext.request.contextPath}/sales">View all <span>→</span></a></div>
    <c:choose><c:when test="${not empty recentBills}"><div class="table-wrap"><table><thead><tr><th>Receipt</th><th>Customer</th><th>Date</th><th class="align-right">Total</th></tr></thead><tbody><c:forEach items="${recentBills}" var="bill"><tr><td><a class="table-link" href="${pageContext.request.contextPath}/billing/${bill.id}">#${bill.id}</a></td><td><c:out value="${bill.customer}"/></td><td><c:out value="${bill.createdAt}"/></td><td class="align-right money">₹<fmt:formatNumber value="${bill.total}" minFractionDigits="2" maxFractionDigits="2" groupingUsed="true"/></td></tr></c:forEach></tbody></table></div></c:when><c:otherwise><div class="empty-state"><span class="empty-mark">↗</span><strong>No sales yet</strong><p>Your completed sales will appear here.</p><a class="text-link" href="${pageContext.request.contextPath}/billing">Create your first sale →</a></div></c:otherwise></c:choose>
  </section>
  <section class="panel stock-panel"><div class="section-heading"><div><h2>Stock watch</h2><p class="muted">Low inventory to review</p></div><a class="text-link" href="${pageContext.request.contextPath}/inventory">Inventory <span>→</span></a></div>
    <c:choose><c:when test="${not empty lowStock}"><ul class="stock-list"><c:forEach items="${lowStock}" var="product" begin="0" end="5"><li><div><strong><c:out value="${product.name}"/></strong><small><c:out value="${empty product.category ? 'Uncategorized' : product.category}"/></small></div><span class="stock-count ${product.stock == 0 ? 'out' : ''}">${product.stock} left</span></li></c:forEach></ul></c:when><c:otherwise><div class="empty-state compact"><span class="empty-mark">✓</span><strong>Stock looks good</strong><p>No products need attention.</p></div></c:otherwise></c:choose>
  </section>
</div>
<jsp:include page="/WEB-INF/jsp/fragments/footer.jsp"/>