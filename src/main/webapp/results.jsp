<%@ page contentType="text/text; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
---
# Dibs Editor database export (YAML)
people:<c:forEach var="item" items="${selectorBean.people}">
   - ${item}</c:forEach>
forums:<c:forEach var="item" items="${selectorBean.forums}">
   - ${item}</c:forEach>
moderates:<c:forEach var="item" items="${selectorBean.moderates}">
   - ${item}</c:forEach>
