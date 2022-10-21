package org.openmrs.module.patientgrid.xstream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.serialization.SerializationException;

import java.util.Date;

public class CustomXstreamSerializerTest {
	
	private CustomXstreamSerializer customXstreamSerializer;
	
	@Before
	public void setUp() throws Exception {
		customXstreamSerializer = new CustomXstreamSerializer();
	}
	
	private <T> T marshallUnMarshall(Object in, Class<T> type) {
		try {
			String serialize = customXstreamSerializer.serialize(in);
			return customXstreamSerializer.deserialize(serialize, type);
		}
		catch (SerializationException e) {
			Assert.fail(e.getMessage());
		}
		return null;
	}
	
	@Test
	public void serializeSimpleDataSet_shouldOnlyKeepTheFieldidToRowMap() {
		//setup
		SimpleDataSet dataset = getEmptyDataSet();
		dataset.addRow(new DataSetRow());
		
		//action
		SimpleDataSet afterSerialization = marshallUnMarshall(dataset, SimpleDataSet.class);
		
		//assertions
		Assert.assertNotNull(afterSerialization);
		Assert.assertNull(afterSerialization.getContext());
		Assert.assertNull(afterSerialization.getDefinition());
		Assert.assertNull(afterSerialization.getMetaData());
		Assert.assertNull(afterSerialization.getSortCriteria());
		Assert.assertEquals(1, afterSerialization.getRows().size());
	}
	
	private SimpleDataSet getEmptyDataSet() {
		SimpleDataSet dataset = new SimpleDataSet(new PatientDataSetDefinition(), Mockito.mock(EvaluationContext.class));
		dataset.setSortCriteria(new SortCriteria());
		return dataset;
	}
	
}
