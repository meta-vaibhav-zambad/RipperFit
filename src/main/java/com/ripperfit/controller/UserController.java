package com.ripperfit.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ripperfit.model.Employee;
import com.ripperfit.model.Login;
import com.ripperfit.model.Organization;
import com.ripperfit.service.OrganizationService;
import com.ripperfit.service.UserService;

/**
 * controller class to handle all user related views
 */
@RequestMapping(value = "/employee")
@RestController
public class UserController {

	private UserService userService;
	private OrganizationService organizationService;

	/**
	 * method to get UserServiceObject
	 * @return : UserService object
	 */
	public UserService getUserService() {
		return userService;
	}

	/**
	 * method to set UserService object
	 * @param UserService
	 */
	@Autowired(required = true)
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	/**
	 * Method to get OrganizationService object
	 * @return OrganizationService object
	 */
	public OrganizationService getOrganizationService() {
		return organizationService;
	}

	/**
	 * Method to set OrganizationService object
	 * @param organizationService : OrganizationService object
	 */
	@Autowired(required=true)
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	/**
	 * Method to get an employee by his/her email address
	 * @param email : email address of employee
	 * @return ResponseEntity with Employee object
	 */
	@RequestMapping(value = "/getEmployeeByEmail", method = RequestMethod.GET)
	public ResponseEntity<Employee> getEmployeeByEmail(@RequestParam String email) {

		Employee employee = this.userService.getEmployeeByEmail(email);
		if (employee != null) {
			return new ResponseEntity<Employee>(employee,HttpStatus.OK);
		} else {
			return new ResponseEntity<Employee>(HttpStatus.NO_CONTENT);
		}
	}

	/**
	 * method to get Employee By EmployeeId
	 * @param id : employee id
	 * @return : ResponseEntity with Employee object
	 */
	@RequestMapping(value = "/getEmployeeById", method = RequestMethod.GET)
	public ResponseEntity<Employee> getEmployeeById(@RequestParam String Id) {

		int id = Integer.parseInt(Id);
		Employee employee = this.userService.getEmployeeById(id);
		if (employee != null) {
			return new ResponseEntity<Employee>(employee,HttpStatus.OK);
		} else {
			return new ResponseEntity<Employee>(HttpStatus.NO_CONTENT);
		}
	}

	/**
	 * method to check that email and password are correct for login
	 * @param login : Login object with email and password
	 * @return : ResponseEntity with no object
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<Employee> login(@RequestBody Login login, HttpServletRequest request) {	

		HttpSession session = request.getSession();
		Employee employee = this.userService.login(login.getEmail(), login.getPassword());

		if (employee != null) {
			createSession(employee.getEmail(), session);
			return new ResponseEntity<Employee>(employee, HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<Employee>(HttpStatus.UNAUTHORIZED);
		}
	}
	
	/**
	 * Method to create session of logged in user
	 * @param email : email address of logged in user
	 * @param session : HttpSession object
	 * @return ResponseEntity with no object
	 */
	@RequestMapping(value = "/createSession", method = RequestMethod.GET)
	public ResponseEntity<Void> createSession(@RequestParam String email,HttpSession session) {	

		session.setAttribute("email", email);
		return new ResponseEntity<Void>(HttpStatus.OK);

	}

	/**
	 * method to register employee
	 * @param employee : employee object
	 * @return : ResponseEntity with no object
	 */
	@RequestMapping(value = "/addEmployee", method = RequestMethod.POST)
	public ResponseEntity<Void> addEmployee(@RequestBody Employee employee, HttpServletRequest request) {

		HttpSession session = request.getSession();
		if(employee.getLastName() == null){
			employee.setLastName("");
		}
		int result = this.userService.registerEmployee(employee);
		if(result == 1) {
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		} else if(result == 2) {
			createSession(employee.getEmail(), session);
			return new ResponseEntity<Void>(HttpStatus.CREATED);
		} else {
			return new ResponseEntity<Void>(HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
	
	/**
	 * Method for register user through social login
	 * @param employee : Employee object to be logged
	 * @param session : HttpSession object
	 * @return : ResponseEntity with Login object
	 */
	@RequestMapping(value = "/socialLogin", method = RequestMethod.POST)
	public ResponseEntity<Login> addEmployeeThroughSocialLogin(@RequestBody Employee employee,HttpSession session) {

		Login login  = new Login();
		login.setEmail(employee.getEmail());
		employee.setPassword(passwordGenerator());
		login.setPassword(employee.getPassword());
		int result = this.userService.registerEmployee(employee);
		if(result == 1) {
			return new ResponseEntity<Login>(HttpStatus.CONFLICT);
		} else if(result == 2) {
			createSession(employee.getEmail(), session);
			return new ResponseEntity<Login>(login, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<Login>(HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
	
	/**
	 * Method to generate random password of 6 digits for social login and forget password services
	 * @return : String
	 */
	public String passwordGenerator() {
		final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder builder = new StringBuilder();
		int count = 6;
		while (count-- != 0) {
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}

	/**
	 * Method to update an employee
	 * @param employee : Employee object to be updated
	 * @return : ResponseEntity with no object
	 */
	@RequestMapping(value = "/updateEmployee", method = RequestMethod.PUT)
	public ResponseEntity<Void> update(@RequestBody Employee employee) {
		this.userService.updateUser(employee);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	/**
	 * Method for logout
	 * @param session : HttpSession object
	 * @param response : ServletResponse object
	 * @throws IOException
	 */
	@RequestMapping(value="/logout", method= RequestMethod.GET)
	public void logout(HttpSession session, final ServletResponse response) throws IOException{

		final HttpServletResponse httpResponse = (HttpServletResponse) response;
		session.invalidate();
		httpResponse.sendRedirect("/RipperFit/");
	}
	
	/**
	 * Method to get all employees of an organization by organization id
	 * @param organizationId : Id of organization
	 * @return : ResponseEntity with list of Employee objects
	 */
	@RequestMapping(value = "/getEmployeesByOrganizationId/{organizationId}", method = RequestMethod.GET)
	public ResponseEntity<List<Employee>> getEmployeesByOrganizationId(@PathVariable("organizationId") int organizationId) {

		Organization organization = this.organizationService.getOrganizationById(organizationId);
		List<Employee> list = this.userService.getAllEmployeesInAnOrganization(organization);
		if(list.isEmpty()) {
			return new ResponseEntity<List<Employee>>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<Employee>>(list, HttpStatus.OK);
		}
	}
	
	/**
	 * Method to get current logged in employee
	 * @param request : HttpServletRequest object
	 * @return : ResponseEntity with employee object
	 */
	@RequestMapping(value="/getCurrentEmployeeObject",method = RequestMethod.GET)
	public ResponseEntity<Employee> getCurrentEmployeeObject(HttpServletRequest request){

		HttpSession session = request.getSession();
		Employee employee = this.userService.getEmployeeByEmail((String) session.getAttribute("email"));
		return new ResponseEntity<Employee>(employee, HttpStatus.OK);
	}
	
	/**
	 * Method to change password
	 * @param oldPassword : previous password
	 * @param newPassword : updated password
	 * @param request : HttpServletRequest object
	 * @return ResponseEntity with Employee object
	 */	
	@RequestMapping(value="/changePassword",method=RequestMethod.POST)
	public ResponseEntity<Employee> changePassword(@RequestParam String oldPassword,@RequestParam String newPassword,HttpServletRequest request){

		HttpSession session = request.getSession();
		Employee employee = this.userService.getEmployeeByEmail((String) session.getAttribute("email"));
		if(oldPassword.equals(employee.getPassword())){

			employee.setPassword(newPassword);
			this.userService.updateUser(employee);
			return new ResponseEntity<Employee>(HttpStatus.OK);
		}
		return new ResponseEntity<Employee>(HttpStatus.NOT_ACCEPTABLE);
	}
	
	/**
	 * Method to get employees have to be approved
	 * @param request : HttpServletRequest object
	 * @return ResponseEntity with list of employee objects
	 */
	@RequestMapping(value="/getEmployeeApprove",method=RequestMethod.GET)
	public ResponseEntity<List<Employee>> viewAllRequestsForApprove(HttpServletRequest request) {	

		HttpSession session = request.getSession();
		String email = (String) session.getAttribute("email");
		Employee employee = this.userService.getEmployeeByEmail(email);
		List<Employee> list = this.userService.getEmployeeApprove(employee);
		if(list.isEmpty()) {
			return new ResponseEntity<List<Employee>>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<Employee>>(list, HttpStatus.OK);
		}
	}
	
	/**
	 * Method to get new password if user forgot
	 * @param email : email address of user
	 * @return ResponseEntity with new password map
	 */
	@RequestMapping(value="/forgetPassword",method=RequestMethod.PUT)
	public ResponseEntity<Map<String, String>> forgetPassword(@RequestBody String email){

		String pass= passwordGenerator();
		Employee employee = userService.getEmployeeByEmail(email);
		if(employee == null) {
			return new ResponseEntity<Map<String, String>>(HttpStatus.BAD_REQUEST);
		}
		employee.setPassword(pass);
		this.userService.updateUser(employee);

		Map<String, String> response = new HashMap<String, String>(); 
		response.put("pass", pass);
		return new ResponseEntity<Map<String, String>>(response, HttpStatus.OK);
	}
}