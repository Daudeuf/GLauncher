package fr.glauncher.game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class PackInfos
{
	private URI    url;
	private int    projectId;
	private int    fileId;
	private String version;

	private PackInfos()
	{
		super();
	}

	public static PackInfos create( String textUrl ) throws URISyntaxException, IOException
	{
		PackInfos    p            = new PackInfos();
		ObjectMapper objectMapper = new ObjectMapper();

		p.url = new URI(textUrl);

		JsonNode jsonNode = objectMapper.readTree(p.url.toURL());

		p.projectId = jsonNode.get("project_id").asInt();
		p.fileId    = jsonNode.get("file_id")   .asInt();
		p.version   = jsonNode.get("version")   .asText();

		return p;
	}

	public int    getProjectId() { return this.projectId; }
	public int    getFileId()    { return this.fileId;    }
	public String getVersion()   { return this.version;   }

	public void refresh() throws IOException
	{
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonNode = objectMapper.readTree(this.url.toURL());

		this.projectId = jsonNode.get("project_id").asInt();
		this.fileId    = jsonNode.get("file_id")   .asInt();
		this.version   = jsonNode.get("version")   .asText();
	}
}
