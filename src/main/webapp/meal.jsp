<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="formatter" scope="request" type="java.time.format.DateTimeFormatter"/>
<html lang="ru">
<head>
    <title>Edit Meal</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit Meal</h2>
<form method="post">
    <input name="id" value="${meal != null ? meal.id : -1 }" type="hidden"/>
    <p>DateTime: <input name="dateTime" value="${ meal != null ? formatter.format(meal.dateTime) : "" }"/></p>
    <p>Description: <input name="description" value="${ meal != null ? meal.description : "" }"/></p>
    <p>Calories: <input name="calories" value="${ meal != null ? meal.calories : "" }"/></p>
    <input type="submit">&nbsp;<a href="meals">назад</a>
</form>
</body>
</html>
