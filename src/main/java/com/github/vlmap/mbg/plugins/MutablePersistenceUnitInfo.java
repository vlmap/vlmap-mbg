package com.github.vlmap.mbg.plugins;


import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;


/**
 * Spring's base implementation of the JPA
 * {@link javax.persistence.spi.PersistenceUnitInfo} interface,
 * used to bootstrap an {@code EntityManagerFactory} in a container.
 *
 * <p>This implementation is largely a JavaBean, offering mutators
 * for all standard {@code PersistenceUnitInfo} properties.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Costin Leau
 * @since 2.0
 */
public class MutablePersistenceUnitInfo implements PersistenceUnitInfo {

    private String persistenceUnitName;


    private String persistenceProviderClassName;


    private PersistenceUnitTransactionType transactionType;


    private DataSource nonJtaDataSource;

    
    private DataSource jtaDataSource;

    private final List<String> mappingFileNames = new LinkedList<>();

    private List<URL> jarFileUrls = new LinkedList<>();

    
    private URL persistenceUnitRootUrl;

    private final List<String> managedClassNames = new LinkedList<>();

    private final List<String> managedPackages = new LinkedList<>();

    private boolean excludeUnlistedClasses = false;

    private SharedCacheMode sharedCacheMode = SharedCacheMode.UNSPECIFIED;

    private ValidationMode validationMode = ValidationMode.AUTO;

    private Properties properties = new Properties();

    private String persistenceXMLSchemaVersion = "2.0";



    @Override
    
    public String getPersistenceUnitName() {
        return this.persistenceUnitName;
    }



    @Override
    
    public String getPersistenceProviderClassName() {
        return this.persistenceProviderClassName;
    }

    @Override
    public PersistenceUnitTransactionType getTransactionType() {
        if (this.transactionType != null) {
            return this.transactionType;
        }
        else {
            return (this.jtaDataSource != null ?
                    PersistenceUnitTransactionType.JTA : PersistenceUnitTransactionType.RESOURCE_LOCAL);
        }
    }


    @Override
    
    public DataSource getJtaDataSource() {
        return this.jtaDataSource;
    }


    @Override
    
    public DataSource getNonJtaDataSource() {
        return this.nonJtaDataSource;
    }


    @Override
    public List<String> getMappingFileNames() {
        return this.mappingFileNames;
    }

    @Override
    public List<URL> getJarFileUrls() {
        return this.jarFileUrls;
    }



    @Override
    
    public URL getPersistenceUnitRootUrl() {
        return this.persistenceUnitRootUrl;
    }


    @Override
    public List<String> getManagedClassNames() {
        return this.managedClassNames;
    }


    @Override
    public boolean excludeUnlistedClasses() {
        return this.excludeUnlistedClasses;
    }

    @Override
    public SharedCacheMode getSharedCacheMode() {
        return this.sharedCacheMode;
    }


    @Override
    public ValidationMode getValidationMode() {
        return this.validationMode;
    }



    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public String getPersistenceXMLSchemaVersion() {
        return this.persistenceXMLSchemaVersion;
    }



    @Override
    
    public ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

    /**
     * This implementation throws an UnsupportedOperationException.
     */
    @Override
    public void addTransformer(ClassTransformer classTransformer) {
        throw new UnsupportedOperationException("addTransformer not supported");
    }

    /**
     * This implementation throws an UnsupportedOperationException.
     */
    @Override
    public ClassLoader getNewTempClassLoader() {
        throw new UnsupportedOperationException("getNewTempClassLoader not supported");
    }


    @Override
    public String toString() {
        return "PersistenceUnitInfo: name '" + this.persistenceUnitName +
                "', root URL [" + this.persistenceUnitRootUrl + "]";
    }

}
