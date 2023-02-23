package org.openmrs.module.patientgrid.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.util.OpenmrsUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.when;
import static org.openmrs.module.patientgrid.PatientGridConstants.GP_MAX_CACHE_FILE_AGE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DiskCache.class, Context.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class CleanCacheTaskTest {

  @Test
  public void execute_shouldCalldeleteCacheFileOlderThanMethod() {
    //setup
    PowerMockito.mockStatic(Context.class);
    AdministrationService mockAdminService = PowerMockito.mock(AdministrationService.class);
    when(mockAdminService.getGlobalProperty(GP_MAX_CACHE_FILE_AGE,null)).thenReturn("32");
    when(Context.getAdministrationService()).thenReturn(mockAdminService);

    PowerMockito.mockStatic(DiskCache.class);
    DiskCache mockCache = PowerMockito.mock(DiskCache.class);
    when(DiskCache.getInstance()).thenReturn(mockCache);

    //action
    new CleanCacheTask().execute();

    //assert
    Mockito.verify(mockCache).deleteCacheFileOlderThan(32);




  }

}