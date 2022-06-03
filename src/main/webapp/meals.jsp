<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="meals" scope="request" type="java.util.List<ru.javawebinar.topjava.model.MealTo>"/>
<jsp:useBean id="formatter" scope="request" type="java.time.format.DateTimeFormatter"/>
<html lang="ru">
<head>
    <title>Meals</title>
    <style>
        td {
            border: black inset;
            padding: 5px;
        }
    </style>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<p>
    <a href="?action=create">add meal</a>
</p>
<table>
    <c:forEach var="meal" items="${meals}">
        <tr style="color: ${meal.excess ? 'red' : 'green'}">
            <td>${formatter.format(meal.dateTime)}</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
            <td><a href="?action=update&id=${meal.id}">update</a></td>
            <td><a href="?action=delete&id=${meal.id}">delete</a></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
