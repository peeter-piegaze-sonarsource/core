package org.meveo.admin.action.notification;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.Enumeration;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.event.qualifier.InboundRequestReceived;
import org.meveo.model.audit.ChangeOriginEnum;
import org.meveo.model.notification.InboundRequest;
import org.meveo.service.audit.AuditOrigin;
import org.meveo.service.notification.InboundRequestService;
import org.slf4j.Logger;

/**
 * To call this servlet the url must be in this format: /inbound/&lt;provider.code&gt;
 * 
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
@WebServlet("/inbound/*")
@ServletSecurity(@HttpConstraint(rolesAllowed = "apiAccess"))
public class InboundServlet extends HttpServlet {

    private static final long serialVersionUID = 1551787937225264581L;

    @Inject
    InboundRequestService inboundRequestService;

    @Inject
    Logger log;

    @Inject
    @InboundRequestReceived
    protected Event<InboundRequest> eventProducer;

    @Inject
    private AuditOrigin auditOrigin;

    private void doService(HttpServletRequest req, HttpServletResponse res) {

        try {

            String path = req.getPathInfo();
            log.debug("received request for method {} , path={}", req.getMethod(), path);

            auditOrigin.setAuditOrigin(ChangeOriginEnum.INBOUND_REQUEST);
            auditOrigin.setAuditOriginName(path);

            InboundRequest inReq = new InboundRequest();
            inReq.setCode(req.getRemoteAddr() + "_" + req.getRemotePort() + "_" + req.getMethod() + "_" + System.nanoTime());

            inReq.setContentLength(req.getContentLength());
            inReq.setContentType(req.getContentType());

            if (req.getParameterNames() != null) {
                Enumeration<String> parameterNames = req.getParameterNames();
                while (parameterNames.hasMoreElements()) {
                    String parameterName = parameterNames.nextElement();
                    String[] paramValues = req.getParameterValues(parameterName);
                    String parameterValue = null;
                    String sep = "";
                    for (String paramValue : paramValues) {
                        parameterValue = sep + paramValue;
                        sep = "|";
                    }
                    inReq.getParameters().put(parameterName, parameterValue);
                }
            }
            inReq.setProtocol(req.getProtocol());
            inReq.setScheme(req.getScheme());
            inReq.setRemoteAddr(req.getRemoteAddr());
            inReq.setRemotePort(req.getRemotePort());
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader;
            try {
                reader = req.getReader();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            String body = buffer.toString();
            inReq.setBody(body);

            inReq.setMethod(req.getMethod());
            inReq.setAuthType(req.getAuthType());
            if (req.getCookies() != null) {
                for (Cookie cookie : req.getCookies()) {
                    inReq.getCoockies().put(cookie.getName(), cookie.getValue());
                }
            }
            if (req.getHeaderNames() != null) {
                Enumeration<String> headerNames = req.getHeaderNames();

                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    if (headerName != null && !headerName.toLowerCase().equals("authorization")) {
                        inReq.getHeaders().put(headerName, req.getHeader(headerName));
                    }
                }
            }
            inReq.setPathInfo(path);
            inReq.setRequestURI(req.getRequestURI());
            inboundRequestService.create(inReq);

            int status = HttpURLConnection.HTTP_OK;
            // process the notifications
            try {
                eventProducer.fire(inReq);
                if (inReq.getHeaders().get("fired").equals("false")) {
                    status = HttpURLConnection.HTTP_NOT_FOUND;
                }
            } catch (BusinessException be) {
                log.error("Failed when processing the notifications", be);
                status = HttpURLConnection.HTTP_INTERNAL_ERROR;
            }

            log.debug("triggered {} notification, resp body= {}", inReq.getNotificationHistories().size(), inReq.getResponseBody());
            // ONLY ScriptNotifications will produce notification history in
            // synchronous mode. Other type notifications will produce notification
            // history in asynchronous mode and thus
            // will not be related to inbound request.

            if (inReq.getResponseStatus() != null) {
                status = inReq.getResponseStatus();
            }
            res.setStatus(status);

            if (status != HttpURLConnection.HTTP_NOT_FOUND) {
                // produce the response
                res.setCharacterEncoding(inReq.getResponseEncoding() == null ? req.getCharacterEncoding() : inReq.getResponseEncoding());
                res.setContentType(inReq.getResponseContentType() == null ? inReq.getContentType() : inReq.getResponseContentType());
                for (String cookieName : inReq.getResponseCoockies().keySet()) {
                    res.addCookie(new Cookie(cookieName, inReq.getResponseCoockies().get(cookieName)));
                }

                for (String headerName : inReq.getResponseHeaders().keySet()) {
                    res.addHeader(headerName, inReq.getResponseHeaders().get(headerName));
                }

                if (inReq.getBytes() != null) {
                    IOUtils.copy(new ByteArrayInputStream(inReq.getBytes()), res.getOutputStream());
                    res.flushBuffer();
                } else if (inReq.getResponseBody() != null) {
                    try (PrintWriter out = res.getWriter()) {
                        out.print(inReq.getResponseBody());
                    } catch (IOException e) {
                        log.error("Failed to produce the response", e);
                        res.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    }
                }
            }

            inReq = inboundRequestService.update(inReq);



            log.debug("Inbound request finished with status {}", res.getStatus());

        } catch (BusinessException | IOException e) {
            log.error("Failed to process Inbound request ", e);
        }

    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doService(req, res);
    }

    public void doDelete(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doService(req, res);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doService(req, res);
    }

    public void doHead(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doService(req, res);
    }

    public void doOption(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doService(req, res);
    }

    public void doPut(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doService(req, res);
    }

    public void doTrace(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        doService(req, res);
    }

}
