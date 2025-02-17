package fr.diamodaria.game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.flowarg.flowupdater.download.json.Mod;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class PackInfos
{
	private URI    url;
	private int    projectId;
	private int    fileId;
	private String version;
	private Mod[] mods;

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

		JsonNode jsonMods = jsonNode.get("mods");

		if (jsonMods != null && jsonMods.isArray()) {
			p.mods = new Mod[jsonMods.size()];

			for (int i = 0; i < jsonMods.size(); i++) {
				p.mods[i] = new Mod(
						jsonMods.get(i).get("name").asText(),
						jsonMods.get(i).get("downloadURL").asText(),
						jsonMods.get(i).get("sha1").asText(),
						jsonMods.get(i).get("size").asInt()
				);
			}
		}

		return p;
	}

	public int    getProjectId() { return this.projectId; }
	public int    getFileId()    { return this.fileId;    }
	public String getVersion()   { return this.version;   }
	public Mod[]  getMods()      { return this.mods;      }

	public void refresh() throws IOException
	{
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonNode = objectMapper.readTree(this.url.toURL());

		this.projectId = jsonNode.get("project_id").asInt();
		this.fileId    = jsonNode.get("file_id")   .asInt();
		this.version   = jsonNode.get("version")   .asText();

		JsonNode jsonMods = jsonNode.get("mods");

		if (jsonMods != null && jsonMods.isArray()) {
			this.mods = new Mod[jsonMods.size()];

			for (int i = 0; i < jsonMods.size(); i++) {
				this.mods[i] = new Mod(
						jsonMods.get(i).get("name").asText(),
						jsonMods.get(i).get("downloadURL").asText(),
						jsonMods.get(i).get("sha1").asText(),
						jsonMods.get(i).get("size").asInt()
				);
			}
		}
	}
}
