<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${meal != null}">
    <jsp:useBean id="meal" scope="request" type="ru.javawebinar.topjava.model.Meal"/>
</c:if>
<html lang="ru">
<head>
    <title>${ meal != null ? "Edit Meal" : "Add Meal"}</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>${ meal != null ? "Edit Meal" : "Add Meal"}</h2>
<form method="post">
    <c:choose>
        <c:when test="${meal != null}">
            <input name="id" value="${meal.id}" type="hidden"/>
            <p>DateTime: <input name="dateTime" value="${ meal.dateTime }" type="datetime-local"/></p>
            <p>Description: <input name="description" value="${ meal.description }" type="text"/></p>
            <p>Calories: <input name="calories" value="${ meal.calories }" type="number"/></p>
            <input type="submit" value="save">
        </c:when>
        <c:otherwise>
            <p>DateTime: <input name="dateTime" type="datetime-local"/></p>
            <p>Description: <input name="description" type="text"/></p>
            <p>Calories: <input name="calories" type="number"/></p>
            <input type="submit" value="add">
        </c:otherwise>
    </c:choose>
</form>
</body>
</html>
