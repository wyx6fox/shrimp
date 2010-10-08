package com.xysd.internal_wf.operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.xysd.internal_wf.domain.ProcessConfig;
import com.xysd.internal_wf.domain.ProcessDefinition;
import com.xysd.internal_wf.domain.parser.XmlParser;

public class HashMapProcessDeployer implements ProcessDeployer {

	private Map<String, ProcessDefinition> definitions = new LinkedHashMap<String, ProcessDefinition>();

	public void deployProcesses(File[] files) {
		if (files != null) {
			for (File f : files) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(f));
					StringBuffer content = new StringBuffer();
					String line = null;
					while ((line = br.readLine()) != null) {
						content.append(line + "\n");

					}
					ProcessDefinition pd = new ProcessDefinition();
					pd.setId(UUID.randomUUID().toString());
					pd.setContent(content.toString());
					ProcessConfig config = new XmlParser(pd.getContent())
							.parse();
					pd.setName(config.getProcessName());
					definitions.put(pd.getId(), pd);
					br.close();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

			}
		}

	}

	public ProcessDefinition getLastProcessDefinition(String processName) {
		ProcessDefinition sameNameDefinition = null;
		for (ProcessDefinition def : this.definitions.values()) {
			if (processName.equals(def.getName()))
				sameNameDefinition = def;
		}
		return sameNameDefinition;
	}

	public ProcessDefinition findProcessDefinition(String processDefinitionId) {

		return this.definitions.get(processDefinitionId);
	}

}
