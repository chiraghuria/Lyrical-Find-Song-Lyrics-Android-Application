<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard for Music Lyrics Search</title>
</head>
<body>

<h2><%= "Number of song search requests" %></h2>
<h3><span><b> ${totalSearchCount} songs searched</b></span></h3>
<br/>
<h2><%= "Most searched song:" %></h2>
<table border="2">
    <thead>
    <tr BGCOLOR='#E4E5E7'>
        <th>Song Name</th>
        <th>Search Count</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${mostSearchedSong}" var="map">
        <tr>
            <td><c:out value="${map.key}"/></td>
            <td><c:out value="${map.value}"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<h2><%= "Most searched artist:" %></h2>
<table border="2">
    <thead>
    <tr BGCOLOR='#E4E5E7'>
        <th>Artist Name</th>
        <th>Search Count</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${mostSearchedArtist}" var="map">
        <tr>
            <td><c:out value="${map.key}"/></td>
            <td><c:out value="${map.value}"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<br/>

<br/>

<h2><%= "Request Logs" %></h2>
<table border="2">
    <thead>
    <tr BGCOLOR='#8BD4E7'>
        <th> Request Timestamp </th>
        <th> Request Method </th>
        <th> Request Protocol </th>
        <th> Request URL </th>
        <th> Request Secure </th>
        <th> Request Browser Name </th>
        <th> Request Machine IP </th>
        <th> Request Song Name </th>
        <th> Request Artist Name </th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${requestLogs}" var="map">
        <tr>
            <td><c:out value="${map['RequestTimestamp']}"/></td>
            <td><c:out value="${map['RequestMethod']}"/></td>
            <td><c:out value="${map['RequestProtocol']}"/></td>
            <td><c:out value="${map['RequestURL']}"/></td>
            <td><c:out value="${map['RequestSecure']}"/></td>
            <td><c:out value="${map['RequestBrowserName']}"/></td>
            <td><c:out value="${map['RequestMachineIP']}"/></td>
            <td><c:out value="${map['RequestSongName']}"/></td>
            <td><c:out value="${map['RequestArtistName']}"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<br />

<h2><%= "Response Logs" %></h2>
<table border="2">
    <thead>
    <tr BGCOLOR='#8BD4E7'>
        <th> Response Timestamp </th>
        <th> Response Thumbnail </th>
        <th> Response Lyrics </th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${responseLogs}" var="map">
        <tr>
            <td><c:out value="${map['ResponseTimestamp']}"/></td>
            <td><c:out value="${map['ResponseThumbnail']}"/></td>
            <td><c:out value="${map['ResponseLyrics']}"/></td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<br />

</body>
</html>