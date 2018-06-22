/**
 * Waffle (https://github.com/Waffle/waffle)
 *
 * Copyright (c) 2010-2018 Application Security, Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors: Application Security, Inc.
 */
package waffle.servlet;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.util.AuthorizationHeader;
import waffle.util.CorsPreFlightCheck;

public class CorsAwareNegotiateSecurityFilter extends NegotiateSecurityFilter implements Filter {
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NegotiateSecurityFilter.class);

    /**
     * Instantiates a new negotiate security filter.
     */
    public CorsAwareNegotiateSecurityFilter() {
        CorsAwareNegotiateSecurityFilter.LOGGER
                .info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] loaded");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        CorsAwareNegotiateSecurityFilter.LOGGER
                .info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Loaded");

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        CorsAwareNegotiateSecurityFilter.LOGGER
                .info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Filtering");

        final HttpServletRequest sreq = (HttpServletRequest) request;
        final AuthorizationHeader authorizationHeader = new AuthorizationHeader(sreq);

        if (CorsPreFlightCheck.isPreFlight(sreq)) {
            CorsAwareNegotiateSecurityFilter.LOGGER
                    .info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Request is CorsPreFlight");
            chain.doFilter(request, response);
            return;
        }else if (authorizationHeader.isBearerAuthorizationHeader() ) {
            chain.doFilter(request, response);
            return;
        }
        else {
            CorsAwareNegotiateSecurityFilter.LOGGER
                    .info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Request is Not orsPreFlight");

            super.doFilter(request, response, chain);

            CorsAwareNegotiateSecurityFilter.LOGGER
                    .info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] Authentication Completed");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        CorsAwareNegotiateSecurityFilter.LOGGER
                .info("[waffle.servlet.CorsAwareNegotiateSecurityFilter] unloaded");

    }

}
