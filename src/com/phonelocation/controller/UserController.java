package com.phonelocation.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.code.kaptcha.Constants;
import com.phonelocation.dao.RolesDao;
import com.phonelocation.dao.UserDao;
import com.phonelocation.model.Roles;
import com.phonelocation.model.Users;

@Controller
public class UserController {

	@Autowired
	private UserDao userDao;

	@Autowired
	private RolesDao rolesDao;

	@RequestMapping(value = "/register.do", method = RequestMethod.GET)
	public String toRegister() {
		return "register";
	}

	@RequestMapping(value = "/register.do", method = RequestMethod.POST)
	public String register(String j_username, String j_password,
			String j_password_rep, String j_code, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		String code = (String) session
				.getAttribute(Constants.KAPTCHA_SESSION_KEY);
		if (!code.equals(j_code)) {
			System.out.println(code + " " + j_code + " ��֤�벻ƥ��");
			return "register";
		}
		if (j_username.equals("") || j_password.equals("")
				|| j_password_rep.equals("")) {
			System.out.println("����Ϊ��");
			return "register";
		}
		if (!j_password.equals(j_password_rep)) {
			System.out.println("�������벻ͬ");
			return "register";
		}
		Users user = userDao.findUserByUsername(j_username, true);
		if (user != null) {
			System.out.println("�û��Ѵ���" + j_username + " " + user.getUsername());
			return "register";
		}

		user = new Users(j_username, j_password, 1);
		user.setUsername(j_username);
		user.setPassword(j_password);
		user.setEnabled(1);
		user.getRoles().add(rolesDao.findRolesByRolename(Roles.ROLE_USER));

		userDao.saveOrUpdate(user);

		System.out.println("�½��û�");
		return "index";
	}
}
