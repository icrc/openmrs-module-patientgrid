package org.openmrs.module.patientgrid.cache;

import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.openmrs.module.patientgrid.PatientGridConstants.DEFAULT_DISK_CACHE_DIR_NAME;
import static org.openmrs.module.patientgrid.PatientGridConstants.GP_DISK_CACHE_DIR;
import static org.openmrs.module.patientgrid.PatientGridConstants.MODULE_ID;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ OpenmrsUtil.class, Context.class })
@PowerMockIgnore("jdk.internal.reflect.*")
public class DiskCacheTest {
	
	private static final String TEST_CACHE_PARENT_DIR = "test_report_cache";
	
	@Mock
	private AdministrationService mockAdminService;
	
	@Rule
	public ExpectedException ee = ExpectedException.none();
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(OpenmrsUtil.class);
		PowerMockito.mockStatic(Context.class);
		when(Context.getAdministrationService()).thenReturn(mockAdminService);
		Whitebox.setInternalState(DiskCache.getInstance(), File.class, (Object) null);
	}
	
	@Test
	public void getCacheDirectory_shouldReturnTheDefaultDirectory() {
		File testParentDir = new File("test");
		when(OpenmrsUtil.getDirectoryInApplicationDataDirectory(MODULE_ID)).thenReturn(testParentDir);
		
		File cacheDir = DiskCache.getInstance().getCacheDirectory();
		
		assertEquals(testParentDir, cacheDir.getParentFile());
		assertEquals(DEFAULT_DISK_CACHE_DIR_NAME, cacheDir.getName());
	}
	
	@Test
	public void getCacheDirectory_shouldReturnTheConfiguredDirectory() {
		final String testCacheDirName = "test";
		when(mockAdminService.getGlobalProperty(GP_DISK_CACHE_DIR)).thenReturn(testCacheDirName);
		File expected = new File(testCacheDirName);
		when(OpenmrsUtil.getDirectoryInApplicationDataDirectory(testCacheDirName)).thenReturn(expected);
		assertEquals(expected, DiskCache.getInstance().getCacheDirectory());
	}
	
	@Test
	public void getCacheDirectory_shouldCreateTheDirectoryIfItDoesNotExist() {
		final String testCacheDirName = "test";
		when(mockAdminService.getGlobalProperty(GP_DISK_CACHE_DIR)).thenReturn(testCacheDirName);
		File mockCacheDir = Mockito.mock(File.class);
		when(OpenmrsUtil.getDirectoryInApplicationDataDirectory(testCacheDirName)).thenReturn(mockCacheDir);
		when(mockCacheDir.mkdirs()).thenReturn(true);
		
		File cacheDir = DiskCache.getInstance().getCacheDirectory();
		
		assertEquals(mockCacheDir, cacheDir);
		Mockito.verify(mockCacheDir).mkdirs();
	}
	
	@Test
	public void getCacheDirectory_shouldNotCreateTheDirectoryIfItExists() {
		final String testCacheDirName = "test";
		when(mockAdminService.getGlobalProperty(GP_DISK_CACHE_DIR)).thenReturn(testCacheDirName);
		File mockCacheDir = Mockito.mock(File.class);
		when(OpenmrsUtil.getDirectoryInApplicationDataDirectory(testCacheDirName)).thenReturn(mockCacheDir);
		when(mockCacheDir.exists()).thenReturn(true);
		
		File cacheDir = DiskCache.getInstance().getCacheDirectory();
		
		assertEquals(mockCacheDir, cacheDir);
		Mockito.verify(mockCacheDir, never()).mkdirs();
	}
	
	@Test
	public void getCacheDirectory_shouldFailIfTheDirectoryCannotBeCreated() {
		final String testCacheDirName = "test";
		when(mockAdminService.getGlobalProperty(GP_DISK_CACHE_DIR)).thenReturn(testCacheDirName);
		File mockCacheDir = Mockito.mock(File.class);
		when(OpenmrsUtil.getDirectoryInApplicationDataDirectory(testCacheDirName)).thenReturn(mockCacheDir);
		ee.expect(APIException.class);
		ee.expectMessage(Matchers.equalTo("Failed to create grid report cache directory at " + mockCacheDir));
		
		DiskCache.getInstance().getCacheDirectory();
	}
	
	@Test
	public void deleteCacheFileOlderThan_shouldKeepNewFileandDeleteOldOne() throws IOException {
		String cacheDirName = "test";
		when(mockAdminService.getGlobalProperty(GP_DISK_CACHE_DIR)).thenReturn(cacheDirName);
		File cacheDir = new File(FileUtils.getTempDirectory(), getClass().getName() + "-" + System.currentTimeMillis());
		try {
			
			//setup
			assertFalse(cacheDir.exists());
			assertTrue(cacheDir.mkdir());
			when(OpenmrsUtil.getDirectoryInApplicationDataDirectory(cacheDirName)).thenReturn(cacheDir);
			File fileToKeep = File.createTempFile("new", ".xml", cacheDir);
			File fileToClean = File.createTempFile("old", ".xml", cacheDir);
			fileToClean.setLastModified(DateTime.now().minusHours(2).getMillis());
			
			//action
			DiskCache.getInstance().getCacheDirectory();
			DiskCache.getInstance().deleteCacheFileOlderThan(1);
			
			//assert
			assertTrue(fileToKeep.isFile());
			assertTrue(fileToKeep.exists());
			assertFalse(fileToClean.exists());
		}
		finally {
			if (cacheDir.isDirectory()) {
				FileUtils.deleteDirectory(cacheDir);
			}
		}
		assertFalse(cacheDir.exists());
		
	}
	
}
