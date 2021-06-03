package nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.task.Script;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Created by Heerko Groefsema on 23-Apr-18.
 */

@XmlRootElement(name = "task")
public class Task {
	private String parent;
	private String actor;
	private String unitsoftime;
	private String guard;
	private String isTau = "false";
	private String subProcess;
	private Script script;

	public Task() {}

	@XmlAttribute(name = "parent")
	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	@XmlAttribute(name = "actor")
	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	@XmlAttribute(name = "unitsoftime")
	public String getUnitsoftime() {
		return unitsoftime;
	}

	public void setUnitsoftime(String unitsoftime) {
		this.unitsoftime = unitsoftime;
	}

	@XmlElement(name = "guard")
	public String getGuard() {
		return guard;
	}

	public void setGuard(String guard) {
		this.guard = guard;
	}

	@XmlElement(name = "script")
	public Script getScript() {
		return script;
	}

	public void setScript(Script script) {
		this.script = script;
	}

	@XmlAttribute(name = "isTau", required = true)
	public String getIsTau() {
		return isTau;
	}

	public void setIsTau(String isTau) {
		this.isTau = isTau;
	}
	
	@XmlAttribute(name = "subprocess")
	public String getSubProcess() {
		return subProcess;
	}
	
	public void setSubProcess(String subProcess) {
		this.subProcess = subProcess;
	}
}
