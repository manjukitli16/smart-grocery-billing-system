<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="en_IN" scope="request" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${param.title} · Fresh Mart</title>
  <link rel="stylesheet" href="${ctx}/css/app.css">
  <link rel="stylesheet" href="${ctx}/css/print.css" media="print">
</head>
<body>
  <aside class="sidebar">
    <a class="brand" href="${ctx}/"><span class="brand-mark">F</span><span>fresh<span class="brand-light">mart</span></span></a>
    <div class="sidebar-label">STORE</div>
    <nav class="nav-list" aria-label="Main navigation">
      <a class="nav-link ${param.active == 'overview' ? 'active' : ''}" href="${ctx}/">Overview</a>
      <a class="nav-link ${param.active == 'inventory' ? 'active' : ''}" href="${ctx}/inventory">Inventory</a>
      <a class="nav-link ${param.active == 'billing' ? 'active' : ''}" href="${ctx}/billing">New bill</a>
      <a class="nav-link ${param.active == 'sales' ? 'active' : ''}" href="${ctx}/sales">Bill history</a>
    </nav>
    <div class="sidebar-foot"><span class="status-dot"></span>Store is open</div>
  </aside>
  <main class="main-content">
    <header class="topbar"><span class="topbar-context">Fresh Mart <span>/</span> ${param.breadcrumb}</span><span class="user-chip">FM <span>Store manager</span></span></header>
    <section class="page-content">
      <c:if test="${not empty success}"><div class="flash success" role="status"><c:out value="${success}" /></div></c:if>
      <c:if test="${not empty error}"><div class="flash error" role="alert"><c:out value="${error}" /></div></c:if>