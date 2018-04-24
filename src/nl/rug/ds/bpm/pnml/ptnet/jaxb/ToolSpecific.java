package nl.rug.ds.bpm.pnml.ptnet.jaxb;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.Process;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.toolspecific.Task;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Heerko Groefsema on 20-Apr-18.
 */

@XmlRootElement(name = "toolspecific")
public class ToolSpecific {
	private String tool;
	private String version;
	private Process process;
	private Task task;
	private List<Object> other;

	public ToolSpecific() {
		other = new ArrayList<>();
		tool = "nl.rug.ds.bpm.pnml.ptnet";
		version = "1.0";
	}

	@XmlAttribute(name = "tool")
	public String getTool() {
		return tool;
	}

	public void setTool(String tool) {
		this.tool = tool;
	}

	@XmlAttribute(name = "version")
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@XmlElement(name = "process")
	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	@XmlElement(name = "task")
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@XmlAnyElement
	public List<Object> getOther() {
		return other;
	}

	public void setOther(List<Object> other) {
		this.other = other;
	}
}
