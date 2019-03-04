/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.server.FiltersSpecial;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**

 @author mlarr
 */
public class DispatcherToWebserviceNoApp implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        //ver que caso quieres que no vaya al dispatcher de javalite y acceda directamente al servlet (webservice apache cxf)
        if (1 == 2) {

            request.getRequestDispatcher(((HttpServletRequest) request).getServletPath()).forward(request, response);

        } else {

            chain.doFilter(request, response); // Just continue chain.

        }

    }

    @Override
    public void destroy() {
    }

}
