<%
    request.getSession();
    String redirectURL;
    // For a case when no originator is set 
    if (session.getAttribute("originator")==null) {
        redirectURL = "index.jsp";
    }
    // For normal cases
    else {
        redirectURL = session.getAttribute("originator").toString();
    }
    response.sendRedirect(response.encodeRedirectURL(redirectURL));
%>