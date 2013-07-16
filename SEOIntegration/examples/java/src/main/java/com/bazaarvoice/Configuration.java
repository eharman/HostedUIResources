package com.bazaarvoice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class Configuration {
    static HashMap<String, String > _baseConfiguration;
    HashMap<String, String> _instanceConfiguration;
    protected static final Log _log = LogFactory.getLog(Configuration.class);

    public static final String DEPLOYMENT_ZONE_ID = "deploymentZoneId";
    public static final String CLOUD_KEY = "cloudKey";
    public static final String LOAD_SEO_FILES_LOCALLY="loadSEOFilesLocally";
    public static final String LOCAL_SEO_FILE_ROOT = "localSEOFileRoot";
    public static final String CONNECT_TIMEOUT = "connectTimeout";
    public static final String SOCKET_TIMEOUT = "socketTimeout";
    public static final String INCLUDE_DISPLAY_INTEGRATION_CODE = "includeDisplayIntegrationCode";
    public static final String BOT_DETECTION = "botDetection";
    public static final String STAGING_S3_HOSTNAME = "stagingS3Hostname";
    public static final String PRODUCTION_S3_HOSTNAME = "productionS3Hostname";
    public static final String CRAWLER_AGENT_PATTERN = "crawlerAgentPattern";
    public static final String REVIEWS_INTEGRATION_SCRIPT = "reviewsIntegrationScript";
    public static final String QUESTIONS_INTEGRATION_SCRIPT = "questionsIntegrationScript";
    public static final String STORIES_INTEGRATION_SCRIPT = "storiesIntegrationScript";
    public static final String VERSION = "version";

    protected Configuration() {
        _instanceConfiguration = new HashMap<String, String>(_baseConfiguration);
    }

    static {
        Properties classProperties = new Properties();
        try {
            classProperties.load(BazaarvoiceDisplayHelper.class.getClassLoader().getResourceAsStream("bvconfig.properties"));
        } catch (IOException ex) {
            _log.error("Unable to find bvconfig.properties file in path.  Some required properties are not defined.");
            throw new RuntimeException(ex);
        }

        _baseConfiguration = new HashMap<String, String>();
        if (!classProperties.isEmpty()) {
            for (String key: classProperties.stringPropertyNames()) {
                _baseConfiguration.put(key, classProperties.getProperty(key));
            }
        }

        // Optionally merge in client defined properties if present
        Properties clientProperties = new Properties();
        InputStream propertyStream = null;
        try {
            propertyStream = new FileInputStream("bvclient.properties");
        } catch (FileNotFoundException ex) {
            propertyStream = BazaarvoiceDisplayHelper.class.getClassLoader().getResourceAsStream("bvclient.properties");

            if (propertyStream == null) {
                _log.info("Unable to find bvclient.properties file in path.  Please check to make sure the file is in your classpath or application root or define all client properties at runtime.");                throw new RuntimeException("Unable to find bvclient.properties file in path.  Please make sure the file in your classpath or application root.");
            }
        } finally {
            if (propertyStream != null) {
                try {
                    clientProperties.load(propertyStream);
                } catch (IOException ex) {
                    throw new RuntimeException("Unable to load bvclient.properties.  Some required properties are not defined.");
                }
            }
            for (String key: clientProperties.stringPropertyNames()) {
                _baseConfiguration.put(key, clientProperties.getProperty(key));
            }
        }
    }

    static public Configuration newInstance() {
        return new Configuration();
    }

    public Configuration setDeploymentZoneId(String deploymentZoneId) {
        return setStringValue(DEPLOYMENT_ZONE_ID, deploymentZoneId);
    }

    public Configuration setCloudKey(String cloudKey) {
        return setStringValue(CLOUD_KEY, cloudKey);
    }

    public Configuration setLoadSEOFilesLocally(boolean value) {
        return setStringValue(LOAD_SEO_FILES_LOCALLY, Boolean.toString(value));

    }
    public Configuration setLocalSEOFileRoot(String localSEOFileRoot) {
        return setStringValue(LOCAL_SEO_FILE_ROOT, localSEOFileRoot);
    }

    public Configuration setConnectTimeout(int timeout) {
        return setStringValue(CONNECT_TIMEOUT, Integer.toString(timeout));
    }
    public Configuration setSocketTimeout(int timeout) {
        return setStringValue(SOCKET_TIMEOUT, Integer.toString(timeout));
    }

    public Configuration setIncludeDisplayIntegrationCode(boolean includeDisplayIntegrationCode) {
        return setStringValue(INCLUDE_DISPLAY_INTEGRATION_CODE, Boolean.toString(includeDisplayIntegrationCode));
    }

    public Configuration setBotDetection(boolean botDetection) {
        return setStringValue(BOT_DETECTION, Boolean.toString(botDetection));
    }

    public String get(String key) {
        return _instanceConfiguration.get(key);
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    private Configuration setStringValue(String key, String value) {
        if(value == null || value.isEmpty()) {
            throw new IllegalArgumentException("The " + key + "property must not be empty.");
        }
        _instanceConfiguration.put(key, value);
        return this;
    }
}
