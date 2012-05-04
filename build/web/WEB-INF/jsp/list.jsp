<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Storage Manager - Storage list</title>
        <link rel="stylesheet" href="<c:url value="/style.css"/>" type="text/css">
    </head>
    <body>
        <h1>Storage list</h1>
        
        <table>
            <tr><th>Id</th><th>Capacity</th><th>Address</th></tr>
        <c:forEach items="${storages}" var="storage" varStatus="loopStatus">
            <tr class="${loopStatus.index % 2 == 0 ? 'odd' : 'even'}"> 
                <td><c:out value="${storage.id}"/></td>
                <td><c:out value="${storage.capacity}"/></td>
                <td><c:out value="${storage.address}"/></td>                
            </tr>
        </c:forEach>
        </table>
        
        <p><a href="<c:url value="/Index"/>">Add Storage</a></p>
    </body>
</html>
