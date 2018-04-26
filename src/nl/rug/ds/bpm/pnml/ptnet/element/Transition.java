package nl.rug.ds.bpm.pnml.ptnet.element;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.ToolSpecific;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.Task;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.task.Script;

public class Transition extends Node {
	private String parent;
	private String actor;
	private String guard;
	private int unitsoftime;
	private boolean isTau;
	private Task task;
	private Script script;

	public Transition(String id) {
		super();
		xmlElement = new nl.rug.ds.bpm.pnml.ptnet.jaxb.node.transition.Transition(id);
		this.id = id;
	}

	public Transition(String id, String name) {
		super();
		xmlElement = new nl.rug.ds.bpm.pnml.ptnet.jaxb.node.transition.Transition(id, name);
		this.id = id;
		this.name = name;
	}

	public Transition(nl.rug.ds.bpm.pnml.ptnet.jaxb.node.transition.Transition xmlElement) {
		super(xmlElement);
		for (ToolSpecific toolSpecific: xmlElement.getToolSpecifics())
			if(toolSpecific.getTool().equals("nl.rug.ds.bpm.pnml.ptnet"))
				task = toolSpecific.getTask();
		if (task != null) {
			parent = task.getParent();
			actor = task.getActor();
			guard = task.getGuard();
			unitsoftime = Integer.parseInt(task.getUnitsoftime());
			isTau = Boolean.parseBoolean(task.getIsTau());
			script = task.getScript();
		}
	}

	public void setGuard(String guard) {
		this.guard = guard;
		if (task == null) {
			ToolSpecific toolSpecific = new ToolSpecific();
			task = new Task();
			toolSpecific.setTask(task);
			xmlElement.getToolSpecifics().add(toolSpecific);
		}
		task.setGuard(guard);
	}

	public String getGuard() {
		return guard;
	}

	public void setUnitsoftime(int unitsoftime) {
		this.unitsoftime = unitsoftime;
		if (task == null) {
			ToolSpecific toolSpecific = new ToolSpecific();
			task = new Task();
			toolSpecific.setTask(task);
			xmlElement.getToolSpecifics().add(toolSpecific);
		}
		task.setUnitsoftime("" + unitsoftime);
	}

	public int getUnitsoftime() {
		return unitsoftime;
	}

	public void setActor(String actor) {
		this.actor = actor;
		if (task == null) {
			ToolSpecific toolSpecific = new ToolSpecific();
			task = new Task();
			toolSpecific.setTask(task);
			xmlElement.getToolSpecifics().add(toolSpecific);
		}
		task.setActor(actor);
	}

	public String getActor() {
		return actor;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
		if (task == null) {
			ToolSpecific toolSpecific = new ToolSpecific();
			task = new Task();
			toolSpecific.setTask(task);
			xmlElement.getToolSpecifics().add(toolSpecific);
		}
		task.setParent(parent);
	}

	public boolean isTau() {
		return isTau;
	}

	public void setTau(boolean tau) {
		isTau = tau;
		if (task == null) {
			ToolSpecific toolSpecific = new ToolSpecific();
			task = new Task();
			toolSpecific.setTask(task);
			xmlElement.getToolSpecifics().add(toolSpecific);
		}
		task.setIsTau("" + tau);
	}

	public String getScript() {
		return script.getScript();
	}

	public void setScript(String script, String type) {
		this.script.setScript(script);
		this.script.setType(type);
		if (task == null) {
			ToolSpecific toolSpecific = new ToolSpecific();
			task = new Task();
			toolSpecific.setTask(task);
			xmlElement.getToolSpecifics().add(toolSpecific);
		}
		task.setScript(this.script);
	}

	public String getScriptType() {
		return script.getType();
	}

	public boolean isEnabled() {
		//TODO
	}
}
