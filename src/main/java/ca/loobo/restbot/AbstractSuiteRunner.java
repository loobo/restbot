package ca.loobo.restbot;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import ca.loobo.restbot.annotations.Host;
import ca.loobo.restbot.annotations.ResourceFiles;
import ca.loobo.restbot.exceptions.CaseNotExecutedException;
import ca.loobo.restbot.reader.ResourceReader;

public class AbstractSuiteRunner extends ParentRunner<Runner> implements RunnerScheduler{
	private static Logger logger = LoggerFactory.getLogger(SuiteRunner.class);
	private LinkedList<Resource> resources = new LinkedList<Resource>();
	private LinkedList<Runner> pendingQueue = new LinkedList<Runner>();
	private Context context = new Context();
	private RunNotifier notifier = new RunNotifier();
	private OrderedRunnerPreparer preparer;
	final private ResourceReader resourceReader;
	
	public AbstractSuiteRunner(Class<?> testClass, ResourceReader resReader) throws InitializationError {
		super(testClass);
		resourceReader = resReader;
		preparer = new OrderedRunnerPreparer(context);
		Host h = testClass.getAnnotation(Host.class);
		if (h != null) {
			context.setHost(h.host());
			context.setPort(h.port());
		}

		ResourceFiles r = testClass.getAnnotation(ResourceFiles.class);
		if (r == null || r.value().length==0) {
			//throw new InitializationError("no resource file is defined");
		}
		else {	
			for(String p : r.allows()) {
				this.context.addAllowFilter(p);
			}
			for(String f : r.value()) {
				this.addResource(f);
			}
			for(String f : r.propertyFiles()) {
				this.addProperty(f);
			}
		}

		prepare();
	}

	public void prepare() {
		this.setScheduler(this);
		preparer.prepare(this.context);
		logger.debug("{}: {}", Context.RESOURCE_FOLDER, context.getParam(Context.RESOURCE_FOLDER));
		logger.debug("{}: {}", Context.HOST, context.getHost());
		logger.debug("{}: {}", Context.PORT, context.getPort());
		
		for(Resource r : resources) {
			logger.debug("ResourceFile: {}", r.getFilename());
		}
	}
	
	public void start() {
		try {
			prepare();
			launch();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Context getContext() {
		return this.context;
	}
	
	public void launch() throws IOException {
		logger.debug("reading excel ...");

		for (Runner runner : this.preparer.getRunners()) {
			try {
				runner.run(notifier);
			}
			catch (CaseNotExecutedException e) {
				pendingQueue.add(runner);
			}
		}

		//just use a simple way to avoid dead loop in the worst case
		//not elegant way, but easy and enough for now
		int pendingCaseCounter = 0;
		while(pendingQueue.size()>0 && pendingCaseCounter<999) {
			Runner runner = pendingQueue.pop();
			try {
				pendingCaseCounter++;
				runner.run(null);
			}
			catch (CaseNotExecutedException e) {
				pendingQueue.add(runner);
			}				
		}

	}
	
	private void parseResource(Resource resFile) throws InitializationError {
		this.resourceReader.read(this.context, resFile);
	}
	
	public AbstractSuiteRunner addFileResource(String resPath) throws InitializationError {
		Resource res = new FileSystemResource(resPath);
		parseResource(res);
		this.resources.add(res);
		return this;
	}

	public AbstractSuiteRunner addResource(String resPath) throws InitializationError {
		Resource res = Configurator.instance().getApplicationContext().getResource(resPath);
		parseResource(res);
		this.resources.add(res);
		return this;
	}

	public AbstractSuiteRunner addProperty(String resPath) throws InitializationError {
		Resource res = Configurator.instance().getApplicationContext().getResource(resPath);
		this.resourceReader.readProperties(this.context, res);
		return this;
	}
	
	@Override
	protected List<Runner> getChildren() {
		return this.preparer.getRunners();
	}

	@Override
	protected Description describeChild(Runner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(Runner child, RunNotifier notifier) {
		child.run(notifier);
	}
    
	@Override
    public void schedule(Runnable childStatement) {
        childStatement.run();
    }

	@Override
    public void finished() {
		try {
			this.context.getResult().dump();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
