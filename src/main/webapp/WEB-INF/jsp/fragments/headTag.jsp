<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><spring:message code="app.title"/></title>
    <!-- https://studyeasy.org/general/get-base-url-using-context-path-on-jsp-page/ -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>