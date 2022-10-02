package org.openmrs.module.patientgrid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.api.SerializationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.SimpleDataSet;
import org.openmrs.serialization.SimpleXStreamSerializer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class PatientGridCacheTest {
	
	@Mock
	private DiskCache mockDiskCache;
	
	@Mock
	private SimpleDataSet mockDataSet;
	
	@Mock
	private SerializationService mockSerializationService;
	
	private PatientGridCache cache = new PatientGridCache();
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(Context.class);
		Whitebox.setInternalState(cache, DiskCache.class, mockDiskCache);
		when(Context.getSerializationService()).thenReturn(mockSerializationService);
	}
	
	@Test
	public void get_shouldReturnTheDeserializedDataSet() throws Exception {
		final String filename = "test_file";
		final String testReportData = "Test report data";
		when(mockSerializationService.deserialize(testReportData, SimpleDataSet.class, SimpleXStreamSerializer.class))
		        .thenReturn(mockDataSet);
		when(mockDiskCache.getFileContents(filename)).thenReturn(testReportData);
		
		assertEquals(mockDataSet, cache.get(filename, SimpleDataSet.class));
	}
	
	@Test
	public void get_shouldReturnNullIfNoCachedDataSetExists() {
		assertNull(cache.get("some-file", SimpleDataSet.class));
		Mockito.verifyZeroInteractions(mockSerializationService);
	}
	
	@Test
	public void getValueWrapper_shouldReturnNullIfNoCachedValueExists() {
		assertNull(cache.get("some-file"));
		Mockito.verifyZeroInteractions(mockSerializationService);
	}
	
	@Test
	public void getValueWrapper_shouldReturnTheWrappedValueOfTheDeserializedDataSet() throws Exception {
		final String filename = "test_file";
		final String testReportData = "Test report data";
		when(mockDiskCache.getFileContents(filename)).thenReturn(testReportData);
		when(mockSerializationService.deserialize(testReportData, SimpleDataSet.class, SimpleXStreamSerializer.class))
		        .thenReturn(mockDataSet);
		
		assertEquals(mockDataSet, cache.get(filename).get());
	}
	
	@Test
	public void put_shouldSerializeAndSaveTheSpecifiedDataSet() throws Exception {
		final String filename = "test_file";
		final String testReportData = "Test report data";
		when(mockSerializationService.serialize(mockDataSet, SimpleXStreamSerializer.class)).thenReturn(testReportData);
		
		cache.put(filename, mockDataSet);
		
		Mockito.verify(mockDiskCache).setFileContents(filename, testReportData);
	}
	
	@Test
	public void putIfAbsent_shouldReturnExistingValueAndNotSetTheNewValue() throws Exception {
		final String filename = "test_file";
		final String oldTestReportData = "Old Test report data";
		when(mockDiskCache.getFileContents(filename)).thenReturn(oldTestReportData);
		when(mockSerializationService.deserialize(oldTestReportData, SimpleDataSet.class, SimpleXStreamSerializer.class))
		        .thenReturn(mockDataSet);
		
		assertEquals(mockDataSet, cache.putIfAbsent(filename, mockDataSet).get());
		Mockito.verify(mockDiskCache, never()).setFileContents(eq(filename), anyString());
		
	}
	
	@Test
	public void putIfAbsent_shouldSerializeAndSaveTheSpecifiedDataSet() throws Exception {
		final String filename = "test_file";
		final String testReportData = "Test report data";
		when(mockSerializationService.serialize(mockDataSet, SimpleXStreamSerializer.class)).thenReturn(testReportData);
		
		assertNull(cache.putIfAbsent(filename, mockDataSet));
		
		Mockito.verify(mockDiskCache).setFileContents(filename, testReportData);
		
	}
	
}
