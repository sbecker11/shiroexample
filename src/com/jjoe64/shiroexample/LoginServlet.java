package com.jjoe64.shiroexample;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.apache.shiro.web.util.WebUtils;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet
{

	/**
	 *
	 */
	private static final long serialVersionUID = -6856291352568121673L;

	public LoginServlet()
	{
		// init shiro - place this e.g. in the constructor
		Factory<SecurityManager> factory = new IniSecurityManagerFactory();
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setContentType("text/html");

		if (request.getParameter("logout") != null) {
			Subject currentUser = SecurityUtils.getSubject();
			if (currentUser.isAuthenticated()) {
				currentUser.logout();
				System.out.println("current user logged out");
			}
		}

		// draw JSP
		try {
			request.getRequestDispatcher("/includes/login.jsp")
					.include(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html");

		String username = request.getParameter("username");
		String password = request.getParameter("password");
		if (username == null || password == null) {
			request.setAttribute("message", "wrong parameters");
		} else {
			try {
				boolean justLoggedIn = tryLogin(username, password, false);
				if (justLoggedIn) {
					request.setAttribute("message", "Login successful");

					// redirect to denied request which was saved by 
					// PassThruAuthenticationFilter or to fallbackUrl
					String fallbackUrl = "/hello";
					WebUtils.redirectToSavedRequest(request, response, fallbackUrl);
					return;
				} else {
					request.setAttribute("message", "wrong email/pwd or an error...");
				}
			} catch (AuthorizationException e) {
				request.setAttribute("message", e.getMessage());
			}
		}

		// render JSP into response
		try {
			request.getRequestDispatcher("/includes/login.jsp")
					.include(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	// login
	public boolean tryLogin(String username, String password, Boolean rememberMe)
			throws AuthorizationException
	{
		// get the currently executing user:
		Subject currentUser = SecurityUtils.getSubject();

		if (!currentUser.isAuthenticated()) {
			// collect user principals and credentials in a gui specific manner
			// such as username/password html form, X509 certificate, OpenID,
			// etc.We'll use the username/password example here since it 
			// is the most common.
			UsernamePasswordToken token =
					new UsernamePasswordToken(username, password);

			// this is all you have to do to support 'remember me' 
			// (no config - built in!):
			token.setRememberMe(rememberMe);

			try {
				currentUser.login(token);
				System.out.println("User ["
					+ currentUser.getPrincipal().toString()
					+ "] logged in successfully.");

				// save current username in the session, 
				// so we have access to our User model
				currentUser.getSession().setAttribute("username", username);

				return true;
			} catch (UnknownAccountException uae) {
				throw new AuthorizationException("There is no user with username of "
					+ token.getPrincipal());
			} catch (IncorrectCredentialsException ice) {
				throw new AuthorizationException("Password for account "
					+ token.getPrincipal()
					+ " was incorrect!");
			} catch (LockedAccountException lae) {
				throw new AuthorizationException("The account for username "
					+ token.getPrincipal()
					+ " is locked.  "
					+ "Please contact your administrator to unlock it.");
			}
		} else {
			return true; // already logged in
		}
	}
}
