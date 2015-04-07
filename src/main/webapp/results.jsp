<%@ page contentType="text/yaml; charset=UTF-8" isELIgnored="false"
 %><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
 %>---
# Dibs Editor database export (YAML)
people:<c:forEach var="item" items="${selectorBean.people}">
   - ${item}</c:forEach>
forums:<c:forEach var="item" items="${selectorBean.forums}">
   - ${item.name}</c:forEach>
moderates:<c:forEach var="item" items="${reportBean.claimedList}">
   - ${item.name}:
     <c:forEach var="person" items="${item.dibsList}">- ${person.name}
     </c:forEach></c:forEach>
dibs:<c:forEach var="item" items="${reportBean.dibsModel}">
   - ${item.name}:
     <c:forEach var="dibs" items="${item.dibsList}">- ${dibs.person.name}: ${dibs.priority}
     </c:forEach></c:forEach>     