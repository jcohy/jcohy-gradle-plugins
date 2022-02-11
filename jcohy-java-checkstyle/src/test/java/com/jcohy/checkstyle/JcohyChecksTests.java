package com.jcohy.checkstyle;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import com.puppycrawl.tools.checkstyle.AstTreeStringPrinter;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader.IgnoredModulesOptions;
import com.puppycrawl.tools.checkstyle.JavaParser;
import com.puppycrawl.tools.checkstyle.ModuleFactory;
import com.puppycrawl.tools.checkstyle.PackageObjectFactory;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.ThreadModeSettings;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.RootModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.xml.sax.InputSource;

/**
 * Copyright: Copyright (c) 2021
 * <a href="http://www.jcohy.com" target="_blank">jcohy.com</a>
 *
 * <p>
 * Description:
 *
 * @author jiac
 * @version 0.0.5.1 2021/6/21:17:15
 * @since 0.0.5.1
 */
//@ExtendWith(ParameterResolver.class)
@RunWith(Parameterized.class)
public class JcohyChecksTests {
    
    private static final boolean RUNNING_ON_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");
    
    private static final File SOURCES_DIR = new File("src/test/resources/source");
    
    private static final File CHECKS_DIR = new File("src/test/resources/check");
    
    private static final File CONFIGS_DIR = new File("src/test/resources/config");
    
    private static final File DEFAULT_CONFIG = new File(CONFIGS_DIR, "default-checkstyle-configuration.xml");
    
    private final Parameter parameter;
    
    public JcohyChecksTests(Parameter parameter) throws Exception {
        this.parameter = parameter;
    }
    
    //	@ParameterizedTest(name = "{0}")
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Parameter> files() throws IOException {
        return Arrays.stream(Objects.requireNonNull(SOURCES_DIR.list((dir, name) -> !name.startsWith(".")))).sorted().map(Parameter::new)
                .collect(Collectors.toList());
    }
    
    @Test
    public void processHasExpectedResults() throws Exception {
        Locale previousLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        Configuration configuration = loadConfiguration();
        RootModule rootModule = createRootModule(configuration);
        try {
            processAndCheckResults(rootModule);
        }
        finally {
            rootModule.destroy();
            Locale.setDefault(previousLocale);
        }
    }
    
    private Configuration loadConfiguration() throws Exception {
        try (InputStream inputStream = new FileInputStream(this.parameter.getConfigFile())) {
            Configuration configuration = ConfigurationLoader.loadConfiguration(new InputSource(inputStream),
                    new PropertiesExpander(new Properties()), IgnoredModulesOptions.EXECUTE,
                    ThreadModeSettings.SINGLE_THREAD_MODE_INSTANCE);
            return configuration;
        }
    }
    
    private RootModule createRootModule(Configuration configuration) throws CheckstyleException {
        ModuleFactory factory = new PackageObjectFactory(Checker.class.getPackage().getName(),
                getClass().getClassLoader());
        RootModule rootModule = (RootModule) factory.createModule(configuration.getName());
        rootModule.setModuleClassLoader(getClass().getClassLoader());
        rootModule.configure(configuration);
        return rootModule;
    }
    
    private void processAndCheckResults(RootModule rootModule) throws CheckstyleException {
        rootModule.addListener(this.parameter.getAssertionsListener());
        if (!RUNNING_ON_WINDOWS) {
            printDebugInfo(this.parameter.getSourceFile());
        }
        rootModule.process(Arrays.asList(this.parameter.getSourceFile()));
    }
    
    private void printDebugInfo(File file) throws CheckstyleException {
        try {
            System.out.println(AstTreeStringPrinter.printFileAst(file, JavaParser.Options.WITHOUT_COMMENTS));
        }
        catch (IOException ex) {
        }
    }
    
    private static class Parameter {
        
        private final String name;
        
        private final File sourceFile;
        
        private final AssertionsAuditListener assertionsListener;
        
        private final File configFile;
        
        Parameter(String sourceName) {
            this.name = sourceName.replace(".java", "");
            this.sourceFile = new File(SOURCES_DIR, sourceName);
            File configFile = new File(CONFIGS_DIR, this.name + ".xml");
            this.configFile = (configFile.exists() ? configFile : DEFAULT_CONFIG);
            this.assertionsListener = new AssertionsAuditListener(readChecks(this.name + ".txt"));
        }
        
        private List<String> readChecks(String name) {
            try {
                return Files.readAllLines(new File(CHECKS_DIR, name).toPath());
            }
            catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
        
        public File getSourceFile() {
            return this.sourceFile;
        }
        
        public File getConfigFile() {
            return this.configFile;
        }
        
        public AssertionsAuditListener getAssertionsListener() {
            return this.assertionsListener;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
    }
}
