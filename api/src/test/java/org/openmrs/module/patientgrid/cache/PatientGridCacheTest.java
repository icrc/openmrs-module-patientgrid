package org.openmrs.module.patientgrid.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.api.context.Context;
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
@PrepareForTest(Context.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class PatientGridCacheTest {
	
	@Mock
	private DiskCache mockDiskCache;
	
	@Mock
	private SimpleDataSet mockDataSet;
	
	@Mock
	private CustomXstreamSerializer mockOpenmrsSerializer;
	
	private final PatientGridCache cache = new PatientGridCache();
	
	final String filename = "test_file";
	
	File file;
	
	@Before
	public void setup() throws Exception {
		PowerMockito.mockStatic(Context.class);
		Whitebox.setInternalState(cache, DiskCache.class, mockDiskCache);
		cache.setSerializer(mockOpenmrsSerializer);
		file = File.createTempFile("test", ".txt");
		when(mockDiskCache.getFile(filename)).thenReturn(file);
	}
	
	@After
	public void clean() throws Exception {
		file.delete();
	}
	
	@Test
	public void get_shouldReturnTheDeserializedDataSet() throws Exception {
		final String filename = "test_file";
		when(mockOpenmrsSerializer.fromXML(anyObject())).thenReturn(mockDataSet);
		
		assertEquals(mockDataSet, cache.get(filename, SimpleDataSet.class));
	}
	
	@Test
	public void get_shouldReturnNullIfNoCachedDataSetExists() {
		assertNull(cache.get("some-file", SimpleDataSet.class));
		Mockito.verifyZeroInteractions(mockOpenmrsSerializer);
	}
	
	@Test
	public void getValueWrapper_shouldReturnNullIfNoCachedValueExists() {
		assertNull(cache.get("some-file"));
		Mockito.verifyZeroInteractions(mockOpenmrsSerializer);
	}
	
	@Test
	public void getValueWrapper_shouldReturnTheWrappedValueOfTheDeserializedDataSet() throws Exception {
		when(mockOpenmrsSerializer.fromXML(file)).thenReturn(mockDataSet);
		
		assertEquals(mockDataSet, cache.get(filename).get());
	}
	
	@Test
	public void put_shouldSerializeAndSaveTheSpecifiedDataSet() throws Exception {
		
		cache.put(filename, mockDataSet);
		
		Mockito.verify(mockDiskCache).getFile(filename);
		Mockito.verify(mockOpenmrsSerializer).toXML(mockDataSet, file);
	}
	
	@Test
	public void putIfAbsent_shouldReturnExistingValueAndNotSetTheNewValue() throws Exception {
		final SimpleDataSet oldDataSet = mock(SimpleDataSet.class);
		when(mockOpenmrsSerializer.fromXML(file)).thenReturn(oldDataSet);
		
		assertSame(oldDataSet, cache.putIfAbsent(filename, mockDataSet).get());
		
		assertSame(oldDataSet, cache.get(filename).get());
		Mockito.verify(mockOpenmrsSerializer, never()).toXML(mockDataSet, file);
		
	}
	
	@Test
	public void putIfAbsent_shouldSerializeAndSaveTheSpecifiedDataSet() throws Exception {
		
		assertNull(cache.putIfAbsent(filename, mockDataSet));
		
		Mockito.verify(mockOpenmrsSerializer).toXML(mockDataSet, file);
		
	}
	
}
