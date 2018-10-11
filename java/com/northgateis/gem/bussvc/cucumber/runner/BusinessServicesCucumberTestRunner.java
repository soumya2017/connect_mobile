package com.northgateis.gem.bussvc.cucumber.runner;

import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.IOUtils.readLines;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import com.northgateis.gem.framework.util.logger.GemLogger;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.junit.Assertions;
import cucumber.runtime.junit.FeatureRunner;
import cucumber.runtime.junit.JUnitReporter;
import cucumber.runtime.model.CucumberFeature;

/**
 * A custom cucumber class to execute testcase, This extends the feature file
 * loading functionality from cucumber and perform few these steps.
 * 
 * 1) If the feature file path is for a directory then traverses all the files in
 * that directory. 
 * 2) Copy create file with the same name in src/test/resources/temp directory 
 * 3) Find the line start with #BS-INCLUD 
 * 4) Extract the included file name and scenario name from that line. 
 * 5) Read that file and copy that scenario in the background of this file. 
 * 6) Do this in recursive and copy all the previous flow in background. 
 * 7) Pass the src/test/resources/temp as a new feature path and execute all files. 
 * 8) Delete the temp folder.
 * 
 * @author vikas.jain
 *
 */
public class BusinessServicesCucumberTestRunner extends ParentRunner<FeatureRunner> {

	private static final GemLogger staticLogger = GemLogger.getLogger(BusinessServicesCucumberTestRunner.class);
	
	private static final String SCENARIO = "Scenario:";
	private static final String SCENARIO_OUTLINE = "Scenario Outline:";
	private static final String INCLUDE_IDENTIFIER = "#BS-INCLUDE";
	private static final String TEMP_DIR = "../src/test/resources/temp";
	
	/**
	 * Allows us to hook into the cross-env config system to have seed data setup for different envs. 
	 */
	private static final String TEST_CONFIG_PROPERTIES = "../conf/gembusinessservicestest/gembusinessservices-functest-context.properties";
	
	private static final Map<String, String> envProperties = new HashMap<String, String>();
	
	static {
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(new File(TEST_CONFIG_PROPERTIES)));
			for (Object key : props.keySet()) {
				if (key.toString().startsWith("bs.cucumber.")) {
					envProperties.put(key.toString(), props.get(key).toString());
				}
			}
		} catch (IOException e) {
			staticLogger.error("Failed to open file: " + TEST_CONFIG_PROPERTIES 
					+ ". Are you correctly running FTs in a sub-dir of the project?", e);
		}
	}
	
	protected final GemLogger logger = GemLogger.getLogger(this.getClass());
	private final JUnitReporter jUnitReporter;
	private final List<FeatureRunner> children = new ArrayList<FeatureRunner>();
	private final Runtime runtime;

	public BusinessServicesCucumberTestRunner(Class<?> clazz) throws InitializationError, IOException {
		super(clazz);
		logger.info("Started parsing gherkins and inclue background scenarios");
		final ClassLoader classLoader = clazz.getClassLoader();
		Assertions.assertNoCucumberAnnotatedMethods(clazz);

		final RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz);
		final RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
		final List<String> lisOfActualPath = runtimeOptions.getFeaturePaths();

		createTempDir();
		for (String featurePath : lisOfActualPath) {
			final File fileOrFolder = new File(featurePath);
			File[] listOfFiles;
			if (fileOrFolder.getName().contains(".svn")) {
				continue; //don't go there. Access denied...
			} else if (fileOrFolder.isDirectory()) {
				listOfFiles = fileOrFolder.listFiles();
			} else {
				listOfFiles = new File[] { fileOrFolder };//single file
				featurePath = substringBeforeLast(featurePath, "/");
			}
			for (File file : listOfFiles) {
				if (file.isDirectory()) {//e.g. .svn
					continue;
				}
				final File newFile = createTempFile(file.getName());
				final OutputStream out = new FileOutputStream(newFile, true);
				final BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
				final StringBuffer sb = new StringBuffer();
				
				@SuppressWarnings("unchecked")
				final List<String> lines = readLines(new FileInputStream(file));
				boolean comment = false;
				for (String line : lines) {
					if((line.contains(SCENARIO) || line.contains(SCENARIO_OUTLINE)) && line.trim().startsWith("#")) {
						comment =true;
					}
					if((line.contains(SCENARIO) || line.contains(SCENARIO_OUTLINE)) && !line.trim().startsWith("#")) {
						comment =false;
					}
					if (line.contains(INCLUDE_IDENTIFIER)) {
						sb.append("##############################");
						sb.append("\n");
						sb.append("#    Start of background     #");
						sb.append("\n");
						sb.append("##############################");
						sb.append("\n");
						includeBackGround(featurePath, sb, line, comment);
						sb.append("##############################");
						sb.append("\n");
						sb.append("#    End of background       #");
						sb.append("\n");
						sb.append("##############################");
						sb.append("\n");
					} else {
						sb.append(line);
						sb.append("\n");
					}
				}
				String finalGherkinsAfterProps = doEndPropertiesSubstitution(sb);
				if (logger.isDebugEnabled()) {
					logger.debug(finalGherkinsAfterProps);
				}
				bw.write(finalGherkinsAfterProps);
				bw.close();
				out.close();

			}
		}
		runtimeOptions.getFeaturePaths().clear();
		runtimeOptions.getFeaturePaths().add(TEMP_DIR);

		final ResourceLoader resourceLoader = new MultiLoader(classLoader);
		runtime = createRuntime(resourceLoader, classLoader, runtimeOptions);

		final List<CucumberFeature> cucumberFeatures = runtimeOptions.cucumberFeatures(resourceLoader);
		jUnitReporter = new JUnitReporter(runtimeOptions.reporter(classLoader), runtimeOptions.formatter(classLoader),
				runtimeOptions.isStrict());
		addChildren(cucumberFeatures);
	}

	private String doEndPropertiesSubstitution(StringBuffer sb) {
		String text = sb.toString();
		for (String propertyKey : envProperties.keySet()) {
			if (text.contains("${" + propertyKey + "}")) {
				String replaceMe = "\\$\\{" + propertyKey + "\\}";
				String replacedTo = envProperties.get(propertyKey);
				logger.debug("Replacing '" + replaceMe + "' with '" + replacedTo + "'.");
				text = text.replaceAll(replaceMe, "'" + replacedTo + "'");
			}
		}
		if (text.contains("${bs.cucumber.")) {
			throw new IllegalStateException("INVALID GHERKIN DUE TO MISSING ENVIRONMENT PROPERTIES USED IN THE SCENARIO!"
					+ " Cannot complete environment param subs for " + text);
		}
		return text;
	}

	/**
     * Create the Runtime. Can be overridden to customize the runtime or backend.
     *
     * @param resourceLoader used to load resources
     * @param classLoader    used to load classes
     * @param runtimeOptions configuration
     * @return a new runtime
     * @throws InitializationError if a JUnit error occurred
     * @throws IOException if a class or resource could not be loaded
     */
    protected Runtime createRuntime(ResourceLoader resourceLoader, ClassLoader classLoader,
                                    RuntimeOptions runtimeOptions) throws InitializationError, IOException {
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        return new Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
    }
	

	private void includeBackGround(String featurePath, StringBuffer sb, String line, boolean comment)
			throws FileNotFoundException, IOException {
		final String includeFileName = substring(line, line.indexOf("=") + 1, line.indexOf(",")).trim();
		final String scenarioName = substringAfterLast(line, "=").trim();
		final InputStream in = new FileInputStream(new File(featurePath + "/" + includeFileName));
		final List<String> linesOfIncludeFile = readLines(in);

		boolean append = false;
		for (String lineToInclude : linesOfIncludeFile) {
			 if (lineToInclude.contains(SCENARIO) && lineToInclude.contains(scenarioName)) {
				lineToInclude = lineToInclude.replace(SCENARIO, "#");
				append = true;
			}
			if ((lineToInclude.contains(SCENARIO) || lineToInclude.contains(SCENARIO_OUTLINE) || lineToInclude.trim().startsWith("@")) && !lineToInclude.contains(scenarioName)) {
				append = false;
			}
			if (append) {
				if (lineToInclude.contains(INCLUDE_IDENTIFIER)) {
					includeBackGround(featurePath, sb, lineToInclude,comment);
					lineToInclude = "";
				}
				if(comment){
					sb.append("#");
				}
				sb.append(lineToInclude);
				sb.append("\n");
			}
		}
		in.close();
	}

	@Override
	public List<FeatureRunner> getChildren() {
		return children;
	}

	@Override
	protected Description describeChild(FeatureRunner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(FeatureRunner child, RunNotifier notifier) {
		child.run(notifier);
	}

	@Override
	public void run(RunNotifier notifier) {
		try {
			super.run(notifier);
			jUnitReporter.done();
			jUnitReporter.close();
			runtime.printSummary();

		} finally {
			File tempDir = new File(TEMP_DIR);
			deleteDir(tempDir);
		}
	}
	
	private void createTempDir() {
		final File tempDir = new File(TEMP_DIR);
		if(tempDir.exists()){
			deleteDir(tempDir);
		}
		tempDir.mkdir();
	}
	
	private File createTempFile(String fileName) {
		final File newFile = new File(TEMP_DIR + "/" + fileName);
		return newFile;
	}

	private void deleteDir(File tempDir) {
		try {
			deleteDirectory(tempDir);
		} catch (Exception e) {
			logger.error("Directory does not exist", e);
		}
	}

	private void addChildren(List<CucumberFeature> cucumberFeatures) throws InitializationError {
		for (CucumberFeature cucumberFeature : cucumberFeatures) {
			children.add(new FeatureRunner(cucumberFeature, runtime, jUnitReporter));
		}
	}

}
