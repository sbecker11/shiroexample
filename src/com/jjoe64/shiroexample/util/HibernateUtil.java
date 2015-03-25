package com.jjoe64.shiroexample.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtil
{

	// per http://stackoverflow.com/questions/8621906/is-buildsessionfactory-deprecated-in-hibernate-4
	private static SessionFactory sessionFactory = null;
	private static ServiceRegistry serviceRegistry = null;

	private static SessionFactory createSessionFactory()
	{
		Configuration configuration = new Configuration();
		configuration.configure();

		serviceRegistry = new ServiceRegistryBuilder().applySettings(
			configuration.getProperties()).buildServiceRegistry();

		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		return sessionFactory;
	}

	//	private static SessionFactory buildSessionFactory() {
	//		try {
	//			// Create the SessionFactory from hibernate.cfg.xml
	//			return new Configuration().configure().buildSessionFactory();
	//
	//		} catch (Throwable ex) {
	//			// Make sure you log the exception, as it might be swallowed
	//			System.err.println("Initial SessionFactory creation failed." + ex);
	//			throw new ExceptionInInitializerError(ex);
	//		}
	//	}

	public static SessionFactory getSessionFactory()
	{
		if (sessionFactory == null)
			createSessionFactory();
		return sessionFactory;
	}

	public static void shutdown()
	{
		// Close caches and connection pools
		getSessionFactory().close();
	}
}
