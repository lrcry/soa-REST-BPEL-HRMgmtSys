package au.com.ors.rest.resource;

import org.springframework.hateoas.ResourceSupport;

import au.com.ors.rest.bean.User;

/**
 * Resource support class for User<br/>
 * 
 * @author hansmong
 *
 */
public class UserResource extends ResourceSupport {
	public User user;
}
