package org.openmrs.module.patientgrid.api.db.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.patientgrid.PatientGrid;
import org.openmrs.module.patientgrid.api.db.PatientGridDAO;

public class HibernatePatientGridDAO implements PatientGridDAO {
	
	private SessionFactory sessionFactory;
	
	/**
	 * Sets the sessionFactory
	 *
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
	
	/**
	 * @see PatientGridDAO#getPatientGrid(Integer)
	 */
	@Override
	public PatientGrid getPatientGrid(Integer patientGridId) {
		return (PatientGrid) getCurrentSession().get(PatientGrid.class, patientGridId);
	}
	
	/**
	 * @see PatientGridDAO#getPatientGridByUuid(String)
	 */
	@Override
	public PatientGrid getPatientGridByUuid(String uuid) {
		return (PatientGrid) getCurrentSession().createCriteria(PatientGrid.class).add(Restrictions.eq("uuid", uuid))
		        .uniqueResult();
	}
	
	/**
	 * @see PatientGridDAO#getPatientGrids(boolean)
	 */
	@Override
	public List<PatientGrid> getPatientGrids(boolean includeRetired) {
		Criteria criteria = getCurrentSession().createCriteria(PatientGrid.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		
		return criteria.list();
	}
	
	/**
	 * @see PatientGridDAO#savePatientGrid(PatientGrid)
	 */
	@Override
	public PatientGrid savePatientGrid(PatientGrid patientGrid) {
		getCurrentSession().save(patientGrid);
		return patientGrid;
	}
	
}
