<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Storage Manager - Add new Storage</title> 
        <link rel="stylesheet" href="<c:url value="/style.css"/>" type="text/css">
    </head>
    <body>
        <h1>Add new Storage</h1>

        <c:if test="${not empty error}">
            <p class="error">
                <c:out escapeXml="false" value="${error}"/>
            </p>
        </c:if>

        <form action="<c:url value="/Index"/>" method="post">
            <table>                              
                <tr>
                    <th>Capacity:</th>
                    <td><input type="text" name="capacity" value="${storageForm.capacity}"/></td>
                </tr>
                <tr>
                    <th>Address:</th>
                    <td><input type="text" name="address" value="${storageForm.address}"/></td>
                </tr>
            </table>
            <p>
                <input type="Submit" value="Add Storage" name="submit"/>
                <input type="Submit" value="Cancel" name="cancel"/>
            </p>
        </form>
    </body>
</html>
