package org.openmrs.module.patientgrid.xstream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.module.reporting.common.SortCriteria;
import org.openmrs.module.reporting.dataset.DataSetColumn;
import org.openmrs.module.reporting.dataset.DataSetRow;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.serialization.SerializationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomXstreamSerializerTest {
	
	private CustomXstreamSerializer customXstreamSerializer;
	
	String columnName = "column_1";
	
	String columnValue = "Full test with accent like é à and specific chars like *";
	
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
		SimpleDataSet dataset = createData();
		
		//action
		SimpleDataSet afterSerialization = marshallUnMarshall(dataset, SimpleDataSet.class);
		
		//assertions
		checkReadData(afterSerialization);
	}
	
	private SimpleDataSet createData() {
		SimpleDataSet dataset = getEmptyDataSet();
		DataSetRow row = new DataSetRow();
		DataSetColumn column = new DataSetColumn();
		
		column.setName(columnName);
		column.setDataType(String.class);
		Map<DataSetColumn, Object> values = new HashMap<>();
		
		values.put(column, columnValue);
		row.setColumnValues(values);
		dataset.addRow(row);
		return dataset;
	}
	
	private void checkReadData(SimpleDataSet afterSerialization) {
		Assert.assertNotNull(afterSerialization);
		Assert.assertNull(afterSerialization.getContext());
		Assert.assertNull(afterSerialization.getDefinition());
		Assert.assertNull(afterSerialization.getMetaData());
		Assert.assertNull(afterSerialization.getSortCriteria());
		Assert.assertEquals(1, afterSerialization.getRows().size());
		Map<DataSetColumn, Object> values = afterSerialization.getRows().get(0).getColumnValues();
		Assert.assertEquals(1, values.size());
		Object readColumnValue = values.get(new DataSetColumn(columnName, columnName, String.class));
		Assert.assertEquals(columnValue, readColumnValue);
	}
	
	@Test
	public void serializeSimpleDataSet_checkSaveToXmlFile() throws IOException {
		//setup
		SimpleDataSet dataset = createData();
		
		File target = File.createTempFile("test", ".xml");
		target.deleteOnExit();
		customXstreamSerializer.toXML(dataset, target);
		
		Assert.assertTrue(target.exists());
		//action
		SimpleDataSet afterSerialization = (SimpleDataSet) customXstreamSerializer.fromXML(target);
		//assertions
		checkReadData(afterSerialization);
		
		Assert.assertTrue(target.delete());
	}
	
	private SimpleDataSet getEmptyDataSet() {
		SimpleDataSet dataset = new SimpleDataSet(new PatientDataSetDefinition(), Mockito.mock(EvaluationContext.class));
		dataset.setSortCriteria(new SortCriteria());
		return dataset;
	}
	
}
