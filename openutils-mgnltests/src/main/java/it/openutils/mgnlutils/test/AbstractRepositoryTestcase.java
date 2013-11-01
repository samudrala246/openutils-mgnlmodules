/**
 *
 * Test utilities for Magnolia CMS (http://www.openmindlab.com/lab/products/mgnltests.html)
 * Copyright(C) 2009-2012, Openmind S.r.l. http://www.openmindonline.it
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.openutils.mgnlutils.test;

import info.magnolia.cms.core.Path;
import info.magnolia.cms.core.SystemProperty;
import info.magnolia.cms.security.AccessManager;
import info.magnolia.cms.security.MgnlGroupManager;
import info.magnolia.cms.security.MgnlRoleManager;
import info.magnolia.cms.security.PermissionUtil;
import info.magnolia.cms.security.Realm;
import info.magnolia.cms.security.SecuritySupport;
import info.magnolia.cms.security.SecuritySupportImpl;
import info.magnolia.cms.security.SystemUserManager;
import info.magnolia.cms.util.ClasspathResourcesUtil;
import info.magnolia.cms.util.ContentUtil;
import info.magnolia.content2bean.Content2BeanException;
import info.magnolia.content2bean.Content2BeanProcessor;
import info.magnolia.content2bean.Content2BeanUtil;
import info.magnolia.content2bean.TypeMapping;
import info.magnolia.content2bean.impl.Content2BeanProcessorImpl;
import info.magnolia.content2bean.impl.TypeMappingImpl;
import info.magnolia.context.MgnlContext;
import info.magnolia.context.SystemContext;
import info.magnolia.context.SystemRepositoryStrategy;
import info.magnolia.importexport.BootstrapUtil;
import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.init.properties.ClasspathPropertySource;
import info.magnolia.init.properties.InitPathsPropertySource;
import info.magnolia.init.properties.ModulePropertiesSource;
import info.magnolia.jcr.wrapper.DelegateSessionWrapper;
import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleManagementException;
import info.magnolia.module.ModuleManager;
import info.magnolia.module.ModuleManagerImpl;
import info.magnolia.module.ModuleRegistry;
import info.magnolia.module.ModuleRegistryImpl;
import info.magnolia.module.model.ModuleDefinition;
import info.magnolia.module.model.reader.BetwixtModuleDefinitionReader;
import info.magnolia.module.model.reader.DependencyCheckerImpl;
import info.magnolia.objectfactory.Components;
import info.magnolia.objectfactory.configuration.ComponentConfiguration;
import info.magnolia.objectfactory.configuration.ComponentProviderConfiguration;
import info.magnolia.objectfactory.configuration.ComponentProviderConfigurationBuilder;
import info.magnolia.objectfactory.configuration.ConfiguredComponentConfiguration;
import info.magnolia.objectfactory.configuration.ImplementationConfiguration;
import info.magnolia.objectfactory.configuration.InstanceConfiguration;
import info.magnolia.objectfactory.configuration.ProviderConfiguration;
import info.magnolia.repository.DefaultRepositoryManager;
import info.magnolia.repository.RepositoryManager;
import info.magnolia.test.ComponentsTestUtil;
import info.magnolia.test.FixedModuleDefinitionReader;
import info.magnolia.test.TestMagnoliaConfigurationProperties;
import info.magnolia.test.TestMagnoliaInitPaths;
import info.magnolia.test.mock.MockContext;
import info.magnolia.test.mock.MockWebContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.EventListenerIterator;
import javax.jcr.observation.ObservationManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.jndi.BindableRepository;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Generic version of Magnolia RepositoryTestcase, as found in magnolia-core test jar.
 * @author fgiust
 * @version $Id: TestNgRepositoryTestcase.java 2926 2010-08-29 15:26:31Z fgiust $
 */
@RepositoryTestConfiguration
public abstract class AbstractRepositoryTestcase
{

    /**
     * Logger.
     */
    protected Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Setup and start the repository
     * @throws Exception any exception thrown during the test setup
     */
    public void setUp() throws Exception
    {
        // ignore mapping warnings
        org.apache.log4j.Logger.getLogger(RepositoryManager.class).setLevel(Level.ERROR);

        if (!this.getClass().isAnnotationPresent(RepositoryTestConfiguration.class))
        {
            throw new IllegalArgumentException("You should annotate your test with @RepositoryTestConfiguration");
        }

        // retrieve configuration from annotation
        RepositoryTestConfiguration repositoryTestConfiguration = this.getClass().getAnnotation(
            RepositoryTestConfiguration.class);

        String repositoryConfigFileName = repositoryTestConfiguration.repositoryConfig();

        String jackrabbitRepositoryConfigFileName = repositoryTestConfiguration.jackrabbitRepositoryConfig();
        String magnoliaProperties = repositoryTestConfiguration.magnoliaProperties();

        boolean autoStart = repositoryTestConfiguration.autostart();
        boolean quiet = repositoryTestConfiguration.quiet();
        boolean security = repositoryTestConfiguration.security();

        initDefaultImplementations();
        SystemProperty.getProperties().load(this.getClass().getResourceAsStream(magnoliaProperties));

        // MockUtil.initMockContext();

        final MockContext ctx = new MockWebContext()
        {

            @Override
            public AccessManager getAccessManager(String workspace)
            {
                return PermissionUtil.getAccessManager(workspace, getSubject());
            }
        };
        MgnlContext.setInstance(ctx);
        ComponentsTestUtil.setImplementation(SystemContext.class, MockContext.class);

        if (autoStart)
        {
            cleanUp();
            startRepository(repositoryConfigFileName, jackrabbitRepositoryConfigFileName, quiet);
        }

        if (repositoryTestConfiguration != null)
        {
            // bootstrap files list
            bootstrapFile(repositoryTestConfiguration.bootstrapFiles());

            // bootstrap files from directory
            if (repositoryTestConfiguration.bootstrapDirectory() != null
                && repositoryTestConfiguration.bootstrapDirectory().length > 0)
            {
                bootstrapDirectory(repositoryTestConfiguration.bootstrapDirectory());
            }

            for (ModuleConfiguration module : repositoryTestConfiguration.startModules())
            {
                startModule(module.name(), module.moduleclass());
            }

        }

        if (security)
        {
            final SecuritySupportImpl sec = new SecuritySupportImpl();
            sec.setGroupManager(new MgnlGroupManager());
            sec.setRoleManager(new MgnlRoleManager());
            SystemUserManager systemUserManager = new SystemUserManager();
            systemUserManager.setRealmName(Realm.REALM_SYSTEM.getName());
            sec.addUserManager(Realm.REALM_SYSTEM.getName(), systemUserManager);
            sec.setRoleManager(new MgnlRoleManager());
            sec.setGroupManager(new MgnlGroupManager());
            ComponentsTestUtil.setInstance(SecuritySupport.class, sec);
        }

    }

    /**
     * Shutdown the repository, optionally deleting the containing folder.
     * @throws Exception any exception thrown during the shutdown.
     */
    public void tearDown() throws Exception
    {
        RepositoryTestConfiguration repositoryTestConfiguration = this.getClass().getAnnotation(
            RepositoryTestConfiguration.class);

        // if module is set it is stopped on tearDown
        for (ModuleConfiguration module : repositoryTestConfiguration.startModules())
        {
            ((ModuleLifecycle) Components.getComponent(ModuleRegistry.class).getModuleInstance(module.name()))
                .stop(null);
        }

        if (repositoryTestConfiguration.autostart())
        {

            final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("info.magnolia");
            final Level originalLogLevel = logger.getLevel();
            if (repositoryTestConfiguration.quiet())
            {
                logger.setLevel(Level.WARN);
            }
            MgnlContext.release();

            SystemContext systemContext = Components.getComponent(SystemContext.class);

            RepositoryManager repositoryManager = Components.getComponent(RepositoryManager.class);

            Collection<String> workspaceNames = repositoryManager.getWorkspaceNames();

            // manually cleanup active sessions to avoid duplicate closures()
            for (String workspaceName : workspaceNames)
            {
                Session session = systemContext.getJCRSession(workspaceName);

                final ObservationManager observationManager = session.getWorkspace().getObservationManager();
                final EventListenerIterator listeners = observationManager.getRegisteredEventListeners();
                while (listeners.hasNext())
                {
                    observationManager.removeEventListener(listeners.nextEventListener());
                }

                BindableRepository br = ((BindableRepository) repositoryManager.getRepository(repositoryManager
                    .getWorkspaceMapping(workspaceName)
                    .getRepositoryName()));

                final Field repoField = BindableRepository.class.getDeclaredField("repository");
                repoField.setAccessible(true);
                Repository repository = (Repository) repoField.get(br);

                while (session instanceof DelegateSessionWrapper)
                {
                    session = ((DelegateSessionWrapper) session).getWrappedSession();
                }

                final Field asclass = RepositoryImpl.class.getDeclaredField("activeSessions");
                asclass.setAccessible(true);
                Map<Session, Session> activesessions = (Map<Session, Session>) asclass.get(repository);

                for (Session as : new HashSet<Session>(activesessions.keySet()))
                {
                    ((RepositoryImpl) repository).loggedOut((SessionImpl) as);
                    // as.logout();
                }

            }

            systemContext.release();

            repositoryManager.shutdown();
            if (true)
            {
                cleanUp();
            }
            logger.setLevel(originalLogLevel);
        }

        ComponentsTestUtil.clear();
        MgnlContext.setInstance(null);
    }

    // info.magnolia.test.MgnlTestCase.initDefaultImplementations()
    private void initDefaultImplementations() throws IOException, ModuleManagementException
    {
        final List<ModuleDefinition> modules = getModuleDefinitionsForTests();
        final ModuleRegistry mr = new ModuleRegistryImpl();
        ModuleManagerImpl mm = new ModuleManagerImpl(
            null,
            new FixedModuleDefinitionReader(modules),
            mr,
            new DependencyCheckerImpl());
        mm.loadDefinitions();

        final TestMagnoliaConfigurationProperties configurationProperties = new TestMagnoliaConfigurationProperties(
            new ModulePropertiesSource(mr),
            new ClasspathPropertySource("/test-magnolia.properties"),
            new InitPathsPropertySource(new TestMagnoliaInitPaths()));
        SystemProperty.setMagnoliaConfigurationProperties(configurationProperties);

        ComponentsTestUtil.setInstance(ModuleManager.class, mm);
        ComponentsTestUtil.setInstance(ModuleRegistry.class, mr);
        ComponentsTestUtil.setInstance(MagnoliaConfigurationProperties.class, configurationProperties);

        ComponentsTestUtil.setImplementation(RepositoryManager.class, DefaultRepositoryManager.class);

        ComponentProviderConfigurationBuilder configurationBuilder = new ComponentProviderConfigurationBuilder();
        ComponentProviderConfiguration configuration = configurationBuilder.getComponentsFromModules(
            "system",
            mr.getModuleDefinitions());
        configuration.combine(configurationBuilder.getComponentsFromModules("main", mr.getModuleDefinitions()));

        // Content2BeanProcessorImpl uses dependency injection and since we don't have that with MockComponentProvider
        // we need to manually create this object and replace the component configuration read from core.xml
        final TypeMappingImpl typeMapping = new TypeMappingImpl();
        configuration.registerInstance(TypeMapping.class, typeMapping);
        configuration.registerInstance(Content2BeanProcessor.class, new Content2BeanProcessorImpl(typeMapping));

        for (Map.Entry<Class, ComponentConfiguration> entry : configuration.getComponents().entrySet())
        {
            ComponentConfiguration value = entry.getValue();
            if (value instanceof ImplementationConfiguration)
            {
                ImplementationConfiguration config = (ImplementationConfiguration) value;
                ComponentsTestUtil.setImplementation(config.getType(), config.getImplementation());
            }
            else if (value instanceof InstanceConfiguration)
            {
                InstanceConfiguration config = (InstanceConfiguration) value;
                ComponentsTestUtil.setInstance(config.getType(), config.getInstance());
            }
            else if (value instanceof ProviderConfiguration)
            {
                ProviderConfiguration config = (ProviderConfiguration) value;
                ComponentsTestUtil.setImplementation(config.getType(), config.getProviderClass());
            }
            else if (value instanceof ConfiguredComponentConfiguration)
            {
                ConfiguredComponentConfiguration config = (ConfiguredComponentConfiguration) value;
                ComponentsTestUtil.setConfigured(
                    config.getType(),
                    config.getWorkspace(),
                    config.getPath(),
                    config.isObserved());
            }
        }

        for (Map.Entry<Class< ? >, Class< ? >> entry : configuration.getTypeMapping().entrySet())
        {
            ComponentsTestUtil.setImplementation((Class) entry.getKey(), (Class) entry.getValue());
        }
    }

    // info.magnolia.test.MgnlTestCase.getModuleDefinitionsForTests()
    /**
     * Override this method to provide the appropriate list of modules your tests need.
     */
    protected List<ModuleDefinition> getModuleDefinitionsForTests() throws ModuleManagementException
    {
        final ModuleDefinition core = new BetwixtModuleDefinitionReader()
            .readFromResource("/META-INF/magnolia/core.xml");
        return Collections.singletonList(core);
    }

    protected void modifyContextesToUseRealRepository()
    {
        // create a mock web context with same repository acquiring strategy as the system context
        MockContext systemContext = (MockContext) MgnlContext.getSystemContext();
        SystemRepositoryStrategy repositoryStrategy = Components.newInstance(SystemRepositoryStrategy.class);

        systemContext.setRepositoryStrategy(repositoryStrategy);
        MockContext ctx = (MockContext) MgnlContext.getInstance();
        ctx.setRepositoryStrategy(repositoryStrategy);
    }

    protected void startRepository(String repositoryConfigFileName, String jackrabbitRepositoryConfigFileName,
        boolean quiet) throws Exception
    {
        final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("info.magnolia");
        final Level originalLogLevel = logger.getLevel();
        if (quiet)
        {
            logger.setLevel(Level.WARN);
        }

        InputStream repositoryConfigFileStream = ClasspathResourcesUtil
            .getResource(repositoryConfigFileName)
            .openStream();

        extractConfigFile("magnolia.repositories.config", repositoryConfigFileStream, "target/repositories.xml");

        IOUtils.closeQuietly(repositoryConfigFileStream);

        InputStream jackrabbitRepositoryConfigFileStream = ClasspathResourcesUtil.getResource(
            jackrabbitRepositoryConfigFileName).openStream();

        extractConfigFile(
            "magnolia.repositories.jackrabbit.config",
            jackrabbitRepositoryConfigFileStream,
            "target/repo-conf/extracted.xml");

        IOUtils.closeQuietly(jackrabbitRepositoryConfigFileStream);

        Components.getComponent(RepositoryManager.class).init();

        modifyContextesToUseRealRepository();

        logger.setLevel(originalLogLevel);
    }

    private void extractConfigFile(String propertyName, InputStream configFileStream, String extractToPath)
        throws Exception
    {
        String targetFilename = Path.getAbsoluteFileSystemPath(extractToPath);
        File targetFile = new File(targetFilename);
        // extract resource to the filesystem (jackrabbit can't use a stream)
        new File(targetFile.getParent()).mkdirs();
        IOUtils.copy(configFileStream, new FileOutputStream(targetFile));
        SystemProperty.setProperty(propertyName, extractToPath);
    }

    private void cleanUp() throws IOException
    {
        FileUtils.deleteDirectory(new File(Components.getComponent(MagnoliaConfigurationProperties.class).getProperty(
            "magnolia.repositories.home")));
    }

    /**
     * Bootstrap one or more xml files.
     * @param resources one or more xml files (classpath path)
     * @throws RepositoryException thrown during import
     * @throws IOException if there are errors while loading the files
     */
    protected void bootstrapFile(String... resources) throws IOException, RepositoryException
    {
        BootstrapUtil.bootstrap(resources, ImportUUIDBehavior.IMPORT_UUID_COLLISION_THROW);
    }

    /**
     * Loads all the bootstrap files in a specified path under the classpath
     * @param paths classpath path
     * @throws RepositoryException thrown during import
     * @throws IOException if there are errors while loading the files
     */
    protected void bootstrapDirectory(final String... paths) throws IOException, RepositoryException
    {

        String[] resourcesToBootstrap = ClasspathResourcesUtil.findResources(new ClasspathResourcesUtil.Filter()
        {

            @Override
            public boolean accept(final String name)
            {
                for (String string : paths)
                {
                    if (StringUtils.contains(name, string) && StringUtils.endsWithIgnoreCase(name, ".xml"))
                    {
                        return true;
                    }
                }
                return false;
            }
        });

        BootstrapUtil.bootstrap(resourcesToBootstrap, ImportUUIDBehavior.IMPORT_UUID_COLLISION_THROW);
    }

    /**
     * Register and starts a module
     * @param modulename modulename
     * @param moduleClass ModuleClass
     * @throws Content2BeanException if the module configuration can't be successfully parsed.
     * @return module instance
     * @throws RepositoryException
     */
    protected ModuleLifecycle startModule(String modulename, Class< ? extends ModuleLifecycle> moduleClass)
        throws Content2BeanException, RepositoryException
    {
        // register and start module
        Node node = MgnlContext.getJCRSession("config").getNode("/modules/" + modulename + "/config");
        ModuleLifecycle module = (ModuleLifecycle) Content2BeanUtil.toBean(
            ContentUtil.asContent(node),
            true,
            moduleClass);
        module.start(null);
        Components.getComponent(ModuleRegistry.class).registerModuleInstance(modulename, module);

        return module;
    }
}
