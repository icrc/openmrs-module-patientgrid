package org.openmrs.module.patientgrid.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.openmrs.User;

/**
 *
 */
public class CustomUserConverter implements Converter {
	
	@Override
	public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context) {
		User u = (User) obj;
		writer.addAttribute("uuid", u.getUuid());
		writer.addAttribute("id", ObjectUtils.toString(u.getId()));
	}
	
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext unmarshallingContext) {
		String uuid = reader.getAttribute("uuid");
		String id = reader.getAttribute("id");
		if (StringUtils.isNotBlank(uuid) && StringUtils.isNumeric(id)) {
			User u = new User();
			u.setUuid(uuid);
			try {
				u.setId(Integer.valueOf(id));
			}
			catch (NumberFormatException numberFormatException) {
				return null;
			}
			return u;
		}
		return null;
	}
	
	public Object getByUUID(String uuid) {
		User user = new User();
		user.setUuid(uuid);
		return user;
	}
	
	@Override
	public boolean canConvert(Class aClass) {
		return User.class.isAssignableFrom(aClass);
	}
}
