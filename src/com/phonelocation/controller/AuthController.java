package com.phonelocation.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.phonelocation.dao.LocationDao;
import com.phonelocation.dao.UserDao;
import com.phonelocation.model.Location;
import com.phonelocation.model.Token;
import com.phonelocation.model.Users;
import com.phonelocation.repository.TokenRepository;

@Controller
public class AuthController {

	public final static long DEADLINE_ONE_WEEK = 1000 * 60 * 60 * 24 * 7;
	public final static long DEADLINE_TEN_MINIES = 1000 * 60 * 10;

	@Autowired
	private TokenRepository tokenRepository;

	@Autowired
	private UserDao userDao;

	@Autowired
	private LocationDao locationDao;

	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public @ResponseBody Token auth(@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("name") String name, HttpServletRequest request,
			HttpServletResponse response) {

		System.out.println("username:" + username + " password:" + password
				+ " phonename:" + name);

		Users user = userDao.findUserByUsername(username, false);
		if (user == null || !user.getPassword().equals(password)) {
			try {
				System.out.println("hehe");
				response.sendError(404);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		String tokenid = "";
		Token token = new Token(name, tokenid, System.currentTimeMillis()
				+ DEADLINE_TEN_MINIES);

		Location iphone = locationDao.findLocationByPhoneid(name);
		if (iphone == null) {
			iphone = new Location();
			iphone.setPhoneid(name);
			locationDao.saveOrUpdate(iphone);
		}
		user.getPhones().add(iphone);
		userDao.saveOrUpdate(user);

		token = tokenRepository.insert(token);
		System.out.println("New Token:" + token.getOwner() + " <--> "
				+ token.getTokenid());
		return token;
	}

}
