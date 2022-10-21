package org.openmrs.module.patientgrid.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.mapper.Mapper;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.serialization.SerializationException;
import org.openmrs.serialization.SimpleXStreamSerializer;

/**
 * Custom Xstream Serializer to persist only the minimum data form SimpleDataSet. SimpleDataSet must
 * contain only the data used by the front-end
 */
public class CustomXstreamSerializer extends SimpleXStreamSerializer {
	
	public CustomXstreamSerializer() throws SerializationException {
		init();
	}
	
	private void init() {
		//serialize only the field idToRowMap
		xstream.omitField(SimpleDataSet.class, "definition");
		xstream.omitField(SimpleDataSet.class, "sortCriteria");
		xstream.omitField(SimpleDataSet.class, "context");
		xstream.omitField(SimpleDataSet.class, "metaData");
		//secure xstream
		XStream.setupDefaultSecurity(xstream);
		xstream.allowTypesByWildcard(new String[] { "org.openmrs.**" });
		//do stuff on xstream
	}
}
