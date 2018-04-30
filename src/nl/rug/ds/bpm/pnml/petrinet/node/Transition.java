package nl.rug.ds.bpm.pnml.petrinet.node;

import nl.rug.ds.bpm.pnml.jaxb.core.ToolSpecific;
import nl.rug.ds.bpm.pnml.jaxb.toolspecific.Task;
import nl.rug.ds.bpm.pnml.jaxb.toolspecific.task.Script;

public class Transition extends Node {
	private Task task;

	public Transition(String id) {
		xmlElement = new nl.rug.ds.bpm.pnml.jaxb.core.node.transition.Transition(id);
	}

	public Transition(String id, String name) {
		xmlElement = new nl.rug.ds.bpm.pnml.jaxb.core.node.transition.Transition(id, name);
	}

	public Transition(nl.rug.ds.bpm.pnml.jaxb.core.node.transition.Transition xmlElement) {
		super(xmlElement);
		for (ToolSpecific toolSpecific: xmlElement.getToolSpecifics())
			if(toolSpecific.getTool().equals("nl.rug.ds.bpm.pnml.petrinet"))
				task = toolSpecific.getTask();
	}

	public void setGuard(String guard) {
		checkTask();
		task.setGuard(guard);
	}

	public String getGuard() {
		String guard = "";
		
		try {
			guard = task.getGuard();
		} catch (NullPointerException e) {}
		
		return guard;
	}

	public void setUnitsoftime(int unitsoftime) {
		checkTask();
		task.setUnitsoftime("" + unitsoftime);
	}

	public int getUnitsoftime() {
		int unitsoftime = 1;
		
		try {
			unitsoftime = Integer.parseInt(task.getUnitsoftime());
		} catch (Exception e) {}
		
		return unitsoftime;
	}

	public void setActor(String actor) {
		checkTask();
		task.setActor(actor);
	}

	public String getActor() {
		String actor = "";
		
		try {
			actor = task.getActor();
		} catch (NullPointerException e) {}
		
		return actor;
	}

	public String getParent() {
		String parent = "";
		
		try {
			parent = task.getParent();
		} catch (NullPointerException e) {}
		
		return parent;
	}

	public void setParent(String parent) {
		checkTask();
		task.setParent(parent);
	}

	public boolean isTau() {
		boolean isTau = false;
		
		try {
			isTau = Boolean.parseBoolean(task.getIsTau());
		} catch (Exception e) {}
		
		return isTau;
	}

	public void setTau(boolean tau) {
		checkTask();
		task.setIsTau("" + tau);
	}

	public String getScript() {
		String script = "";
		
		try {
			script = task.getScript().getScript();
		} catch (NullPointerException e) {}
		
		return script;
	}
	
	public String getScriptType() {
		String scriptType = "";
		
		try {
			scriptType = task.getScript().getType();
		} catch (NullPointerException e) {}
		
		return scriptType;
	}

	public void setScript(String script, String type) {
		checkTask();
		task.setScript(new Script(script, type));
	}
	
	public String getSubProcess() {
		String sp = "";
		
		try {
			sp = task.getSubProcess();
		} catch (NullPointerException e) {}
		
		return sp;
	}
	
	public void setSubProcess(String subProcess) {
		checkTask();
		task.setSubProcess(subProcess);
	}

	private void checkTask() {
		if (task == null) {
			ToolSpecific toolSpecific = new ToolSpecific();
			task = new Task();
			toolSpecific.setTask(task);
			xmlElement.getToolSpecifics().add(toolSpecific);
		}
	}
}
