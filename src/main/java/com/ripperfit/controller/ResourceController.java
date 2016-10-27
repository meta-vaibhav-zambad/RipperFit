package com.ripperfit.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ripperfit.model.Organization;
import com.ripperfit.model.Resource;
import com.ripperfit.service.OrganizationService;
import com.ripperfit.service.ResourceService;
import com.ripperfit.util.AppException;

/**
 * Controller class for resource related tasks
 */
@RequestMapping(value = "/resource")
@RestController
public class ResourceController {

	private ResourceService resourceService;
	private OrganizationService organizationService;

	/**
	 * Method to get resourceService object
	 * @return resourceService object
	 */
	public ResourceService getResourceService() {
		return resourceService;
	}

	/**
	 * Method to set resourceService object
	 * @param resourceService object
	 */
	@Autowired(required=true)
	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
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
	 * @param OrganizationService object
	 */
	@Autowired(required=true)
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	/**
	 * Method to add a new resource
	 * @param resource : Resource object to be added
	 * @return ResponseEntity with no object
	 */
	@RequestMapping(value="/addResource",method = RequestMethod.POST)
	public ResponseEntity<Void> addResource(@RequestBody Resource resource){

		try{
			this.resourceService.addResource(resource);
			return new ResponseEntity<Void>(HttpStatus.CREATED);
		}catch(AppException resourceAlreadyExistsException){
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}catch(Exception ex){
			return new ResponseEntity<Void>(HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

	/**
	 * Method to get all resources available in an organization by organization ID
	 * @param organizationId : ID of organization
	 * @return ResponseEntity with list of Resource objects
	 */
	@RequestMapping(value = "/getResourcesByOrganizationId/{organizationId}", method = RequestMethod.GET)
	public ResponseEntity<List<Resource>> getResourcesByOrganizationId(@PathVariable("organizationId") int organizationId) {

		try{
			Organization organization = this.organizationService.getOrganizationById(organizationId);
			List<Resource> resourceList = this.resourceService.getAllResourcesInAnOrganization(organization);
			return new ResponseEntity<List<Resource>>(resourceList, HttpStatus.OK);
		}catch(AppException notExistsException){
			return new ResponseEntity<List<Resource>>(HttpStatus.NO_CONTENT);
		}catch(Exception ex){
			return new ResponseEntity<List<Resource>>(HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

	/**
	 * Method to get resource by its ID
	 * @param resourceId : ID of resource
	 * @return ResponseEntity with Resource object
	 */
	@RequestMapping(value = "/getResourceById/{resourceId}", method = RequestMethod.GET)
	public ResponseEntity<Resource> getResourceById(@PathVariable("resourceId") int resourceId) {

		try{
			Resource resource = this.resourceService.getResourceById(resourceId);
			return new ResponseEntity<Resource>(resource,HttpStatus.OK);
		}catch(AppException resourceNotExistsException){
			return new ResponseEntity<Resource>(HttpStatus.NO_CONTENT);
		}catch(Exception ex){
			return new ResponseEntity<Resource>(HttpStatus.SERVICE_UNAVAILABLE);
		}
	}


	/**
	 * Method to update a resource
	 * @param resource : Resource object to be updated
	 * @return ResponseEntity with no object
	 */
	@RequestMapping(value = "/updateResource", method = RequestMethod.PUT)
	public ResponseEntity<Void> update(@RequestBody Resource resource) {

		try{
			this.resourceService.updateResource(resource);
			return new ResponseEntity<Void>(HttpStatus.OK);
		}catch(AppException resourceNotExistsException){
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		}catch(Exception ex){
			return new ResponseEntity<Void>(HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
}