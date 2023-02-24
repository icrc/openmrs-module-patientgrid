package org.openmrs.module.patientgrid.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.api.context.Context;
import org.openmrs.module.patientgrid.ExtendedDataSet;
import org.openmrs.module.patientgrid.PatientGridUtils;
import org.openmrs.module.patientgrid.xstream.CustomXstreamSerializer;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.serialization.OpenmrsSerializer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.File;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, PatientGridUtils.class })
@PowerMockIgnore("jdk.internal.reflect.*")
public class PatientGridCacheTest {
	
	@Mock
	private DiskCache mockDiskCache;
	
	private final String utcTimeZone = "UTC";
	
	@Mock
	private CustomXstreamSerializer mockOpenmrsSerializer;
	
	private final PatientGridCache cache = new PatientGridCache();
	
	final String filename = "test_file";
	
	File file;
	
	@Before
	public void setup() throws Exception {
		PowerMockito.mockStatic(Context.class);
		PowerMockito.mockStatic(PatientGridUtils.class);
		Whitebox.setInternalState(cache, DiskCache.class, mockDiskCache);
		cache.setSerializer(mockOpenmrsSerializer);
		file = File.createTempFile("test", ".txt");
		when(mockDiskCache.getFile(filename)).thenReturn(file);
		when(PatientGridUtils.getCurrentUserTimeZone()).thenReturn("utcTimeZone");
	}
	
	@After
	public void clean() throws Exception {
		file.delete();
	}
	
	@Test
	public void get_shouldReturnTheDeserializedDataSet() throws Exception {
		final String filename = "test_file";
		ExtendedDataSet dataSet = new ExtendedDataSet();
		when(mockOpenmrsSerializer.fromXML(anyObject())).thenReturn(dataSet);
		
		assertEquals(dataSet, cache.get(filename, ExtendedDataSet.class));
	}
	
	@Test
	public void get_shouldReturnNullIfVersionIsObsolete() throws Exception {
		//setup
		final String filename = "test_file";
		ExtendedDataSet dataSet = new ExtendedDataSet();
		dataSet.setXstreamVersion("");
		assertFalse(dataSet.isLastVersion());
		
		//action
		when(mockOpenmrsSerializer.fromXML(anyObject())).thenReturn(dataSet);
		
		//assert
		assertNull(cache.get(filename));
	}
	
	@Test
	public void get_shouldReturnNullIfDateRangeChanged() throws Exception {
		//setup
		final String filename = "test_file";
		ExtendedDataSet dataSet = new ExtendedDataSet();
		dataSet.setUsedDateRange("");
		dataSet.setPeriodOperand("{\"code\":\"LASTTHIRTYDAYS\"}");
		
		//action
		when(mockOpenmrsSerializer.fromXML(anyObject())).thenReturn(dataSet);
		
		//assert
		assertNull(cache.get(filename));
	}
	
	@Test
	public void get_shouldReturnCacheIfNotObsolete() throws Exception {
		//setup
		final String filename = "test_file";
		ExtendedDataSet dataSet = new ExtendedDataSet();
		dataSet.setUsedDateRange("2022-04-01_2022-12-31");
		final String oldTimeZone = System.getProperty("user.timezone");
		System.setProperty("user.timezone", utcTimeZone);
		dataSet.setPeriodOperand(
		    "{\"code\":\"customDaysInclusive\",\"fromDate\":\"2022-04-01 00:00:00\",\"toDate\":\"2022-12-31 00:00:00\"}");
		
		//action
		when(mockOpenmrsSerializer.fromXML(anyObject())).thenReturn(dataSet);
		
		//assert
		assertSame(dataSet, cache.get(filename).get());
		
		System.setProperty("user.timezone", oldTimeZone);
	}
	
	@Test
	public void get_shouldReturnNullIfNoCachedDataSetExists() {
		assertNull(cache.get("some-file", ExtendedDataSet.class));
		Mockito.verifyZeroInteractions(mockOpenmrsSerializer);
	}
	
	@Test
	public void getValueWrapper_shouldReturnNullIfNoCachedValueExists() {
		assertNull(cache.get("some-file"));
		Mockito.verifyZeroInteractions(mockOpenmrsSerializer);
	}
	
	@Test
	public void getValueWrapper_shouldReturnTheWrappedValueOfTheDeserializedDataSet() throws Exception {
		ExtendedDataSet dataSet = new ExtendedDataSet();
		when(mockOpenmrsSerializer.fromXML(file)).thenReturn(dataSet);
		
		assertSame(dataSet, cache.get(filename).get());
	}
	
	@Test
	public void put_shouldSerializeAndSaveTheSpecifiedDataSet() throws Exception {
		ExtendedDataSet dataSet = new ExtendedDataSet();
		cache.put(filename, dataSet);
		
		Mockito.verify(mockDiskCache).getFile(filename);
		Mockito.verify(mockOpenmrsSerializer).toXML(dataSet, file);
	}
	
	@Test
	public void putIfAbsent_shouldReturnExistingValueAndNotSetTheNewValue() throws Exception {
		final ExtendedDataSet oldDataSet = new ExtendedDataSet();
		ExtendedDataSet dataSet = new ExtendedDataSet();
		when(mockOpenmrsSerializer.fromXML(file)).thenReturn(oldDataSet);
		
		assertSame(oldDataSet, cache.putIfAbsent(filename, dataSet).get());
		
		assertSame(oldDataSet, cache.get(filename).get());
		Mockito.verify(mockOpenmrsSerializer, never()).toXML(dataSet, file);
		
	}
	
	@Test
	public void putIfAbsent_shouldSerializeAndSaveTheSpecifiedDataSet() throws Exception {
		ExtendedDataSet dataSet = new ExtendedDataSet();
		assertNull(cache.putIfAbsent(filename, dataSet));
		
		Mockito.verify(mockOpenmrsSerializer).toXML(dataSet, file);
		
	}
	
}
