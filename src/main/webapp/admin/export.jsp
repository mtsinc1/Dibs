<%@ page contentType="text/yaml; charset=UTF-8" isELIgnored="false"
 %><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
 %>---
# Dibs Editor database export (YAML)
people:<c:forEach var="item" items="${reportBean.people}">
   - ${item.name}</c:forEach>
forums:<c:forEach var="item" items="${reportBean.forums}">
   - ${item.name}</c:forEach>
moderates:<c:forEach var="item" items="${reportBean.moderatesList}">
   - ${item.name}:
     <c:forEach var="person" items="${item.dibsList}">- ${person.name}
     </c:forEach></c:forEach>
dibs:<c:forEach var="item" items="${reportBean.dibsModel}">
   - ${item.forumName}:
     <c:forEach var="dibs" items="${item.dibs}">- ${dibs.person.name}: ${dibs.priority}
     </c:forEach></c:forEach>     