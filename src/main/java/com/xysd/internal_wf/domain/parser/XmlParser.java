package com.xysd.internal_wf.domain.parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.xysd.internal_wf.domain.Action;
import com.xysd.internal_wf.domain.BeginNode;
import com.xysd.internal_wf.domain.DetermineNode;
import com.xysd.internal_wf.domain.EndNode;
import com.xysd.internal_wf.domain.ForkNode;
import com.xysd.internal_wf.domain.JoinNode;
import com.xysd.internal_wf.domain.MultipleNode;
import com.xysd.internal_wf.domain.MultipleStepNode;
import com.xysd.internal_wf.domain.ProcessConfig;
import com.xysd.internal_wf.domain.ProcessNode;
import com.xysd.internal_wf.domain.ProcessNodeTask;
import com.xysd.internal_wf.domain.Transition;

public class XmlParser extends DefaultHandler {

	private Log logger = LogFactory.getLog(this.getClass());

	private File configFile;
	
	private String content;

	private ProcessConfig processConfig;

	private ProcessNode processNode;

	private ProcessNodeTask taskConfig;

	private Transition transition;

	private Action action;

	private List<String> nodeNames = Arrays.asList("begin", "end",
			"processNode", "multipleNode", "multipleStepNode", "fork", "join",
			"subProcess");
	
	private List<Transition> transitions = new ArrayList<Transition>();
	
	private Map<String,ProcessNode> nodes = new LinkedHashMap<String,ProcessNode>();

	private ProcessNode buildProcessNode(String nodeName, String nodeId,
			Attributes attr) {
		ProcessNode n = null;
		if ("begin".equalsIgnoreCase(nodeName)) {
			n = new BeginNode(nodeId);
		} else if ("end".equalsIgnoreCase(nodeName)) {
			n = new EndNode(nodeId);
		} else if ("processNode".equalsIgnoreCase(nodeName)) {
			n = new ProcessNode(nodeId);
		} else if ("multipleNode".equalsIgnoreCase(nodeName)) {
			n = new MultipleNode(nodeId);
		} else if ("multipleStepNode".equalsIgnoreCase(nodeName)) {
			n = new MultipleStepNode(nodeId);

		} else if ("fork".equalsIgnoreCase(nodeName)) {
			n = new ForkNode(nodeId);
		} else if ("join".equalsIgnoreCase(nodeName)) {
			n = new JoinNode(nodeId);
			((JoinNode) n).setWaitInvoker(attr.getValue("waitInvoker"));

		}else if("determine".equalsIgnoreCase(nodeName)){
			n = new DetermineNode(nodeId);
		}
		else
			throw new RuntimeException("unkown processNode name:" + nodeName);

		return n;

	}

	public ProcessConfig parse() {
		try {

			SAXParserFactory spFactory = SAXParserFactory.newInstance();
			spFactory.setFeature("http://xml.org/sax/features/validation",
					false);
			spFactory.setValidating(false);
			SAXParser sParser = spFactory.newSAXParser();
			XMLReader xr = sParser.getXMLReader();
			xr.setErrorHandler(this);

			xr.setContentHandler(this);
			if(this.configFile!=null)
				xr.parse(new InputSource(new InputStreamReader(new FileInputStream(
					this.configFile), "UTF-8")));
			else if(this.content!=null){
				xr.parse(new InputSource(new InputStreamReader(new ByteArrayInputStream(this.content.getBytes("UTF-8")))));
			}
			else
				throw new RuntimeException("must specify a input source!");
			this.afterParse();
			return this.processConfig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	//解析完成后，给Transit加上target
	private void afterParse() {
		for(Transition trans:this.transitions){
			trans.setTarget(this.nodes.get(trans.getTargetNodeId()));
		}
	}

	public void startDocument() throws SAXException {

	}

	public void endDocument() throws SAXException {

	}

	private boolean isProcessNode(String nodeName) {
		for (String n : this.nodeNames) {
			if (n.equalsIgnoreCase(nodeName))
				return true;
		}
		return false;
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attr) throws SAXException {
		if ("process".equalsIgnoreCase(qName)) {
			buildProcessConfig(attr);
		}
		if (this.isProcessNode(qName)) {
			String nodeId = attr.getValue("id");
			this.processNode = this.buildProcessNode(qName, nodeId, attr);
			this.processNode.setEnterInvoker(attr.getValue("enterInvoker"));
			this.processNode.setLeaveInvoker(attr.getValue("leaveInvoker"));
			this.nodes.put(this.processNode.getProcessNodeId(), this.processNode);

		}
		if ("task".equalsIgnoreCase(qName)) {
			this.taskConfig = new ProcessNodeTask();
			this.taskConfig.setName(attr.getValue("name"));
			this.taskConfig.setGeneratorInvoker(attr
					.getValue("generatorInvoker"));
			this.taskConfig.setStepInvoker(attr.getValue("stepInvoker"));
			this.taskConfig.setTaskCreatedInvoker(attr.getValue("taskCreatedInvoker"));
			this.taskConfig.setTaskFinishedInvoker(attr.getValue("taskFinishedInvoker"));
			this.taskConfig.setLimitInvoker(attr.getValue("limitInvoker"));
			this.taskConfig.setExpireInvoker(attr.getValue("expireInvoker"));
		}
		if ("assignment".equalsIgnoreCase(qName)) {
			if (this.taskConfig == null)
				throw new RuntimeException(
						"assignment Element must inside a task Node!");
			this.taskConfig.setActorVar(attr.getValue("actorVar"));
			this.taskConfig.setActorTypeVar(attr.getValue("actorTypeVar"));
			this.taskConfig.setCreatorVar(attr.getValue("creatorVar"));
			this.taskConfig.setCreatorTypeVar(attr.getValue("creatorTypeVar"));
			this.taskConfig.setProjectTypeVar(attr.getValue("projectTypeVar"));
			this.taskConfig.setProjectIdVar(attr.getValue("projectIdVar"));
			

		}

		if ("transit".equalsIgnoreCase(qName)) {
			this.transition = new Transition();
			this.transition.setTransitName(attr.getValue("id"));
			this.transition.setSrc(this.processNode);
			this.transition.setTargetNodeId(attr.getValue("to"));
			this.transition.setProcessConfig(processConfig);
			this.transition.setConditionInvoker(attr
					.getValue("conditionInvoker"));
			this.transitions.add(this.transition);

		}

		if ("action".equalsIgnoreCase(qName)) {
			this.action = new Action(attr.getValue("invoker"));
		}

	}

	private void buildProcessConfig(Attributes attr) {
		String processName = attr.getValue("name");
		String taskClassName = attr.getValue("taskClassName");
		String processVariableClassNamee = attr
				.getValue("processVariableClassName");
		String processLockerClassNamee = attr
				.getValue("processLockerClassName");
		String processLogClassNamee = attr.getValue("processLogClassName");
		processConfig = new ProcessConfig(processName, taskClassName,
				processVariableClassNamee, processLockerClassNamee,
				processLogClassNamee);
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (this.isProcessNode(qName)) {
			if ("begin".equalsIgnoreCase(qName))
				this.processConfig.setBeginNode((BeginNode) this.processNode);
			else if ("end".equalsIgnoreCase(qName))
				this.processConfig.setEndNode((EndNode) this.processNode);
			else {
				processConfig.getProcedureNodes().put(
						this.processNode.getProcessNodeId(), this.processNode);
				this.processNode = null;
			}
		}
		if ("task".equalsIgnoreCase(qName)) {
			this.processNode.setTask(this.taskConfig);
			this.taskConfig = null;
		}
		if ("transit".equalsIgnoreCase(qName)) {
			this.processNode.getTransitions().put(
					this.transition.getTransitName(), this.transition);
			this.transition = null;

		}

		if ("action".equalsIgnoreCase(qName)) {
			this.transition.getActions().add(this.action);
			this.action = null;
		}

	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {

	}

	public void fatalError(SAXParseException exception) throws SAXException {
		String msg = ("FATAL: line " + exception.getLineNumber() + ": col:"
				+ exception.getColumnNumber() + " message:" + exception
				.getMessage());
		// System.out.println(msg);
		logger.error(msg);
		throw (exception);
	}

	public XmlParser(File configFile) {
		super();
		this.configFile = configFile;
	}

	public XmlParser(String content) {
		this.content = content;
	}

}
