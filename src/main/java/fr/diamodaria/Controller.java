package fr.diamodaria;

import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.diamodaria.game.Auth;
import fr.diamodaria.game.PackInfos;
import fr.diamodaria.ui.Launcher;
import fr.theshark34.openlauncherlib.util.Saver;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class Controller
{
	public static final PackInfos pack;

	static {
		try {
			pack = PackInfos.create("http://145.223.34.52/modpack_info.json");
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final String  NAME  = "diamodaria";
	private static final String  LABEL = "Diamodaria Launcher";

	private final ILogger  logger;
	private final Path     launcherDir;
	private final Launcher launcher;
	private final Saver    saver;
	private final Auth     auth;

	public Controller()
	{
		this.launcherDir = createGameDir(NAME, true);
		this.logger      = new Logger(String.format("[%s]", LABEL), this.launcherDir.resolve("launcher.log"));

		if (Files.notExists(this.launcherDir))
		{
			try
			{
				Files.createDirectory(this.launcherDir);
			}
			catch (IOException e)
			{
				this.logger.err("Unable to create launcher folder");
				this.logger.printStackTrace(e);
			}
		}

		this.saver = new Saver(this.launcherDir.resolve("config.properties"));
		this.saver.load();

		this.auth     = new Auth    ( this );
		this.launcher = new Launcher( this );

		this.launcher.checkOnline();
	}

	public ILogger getLogger()      { return this.logger;      }
	public Path    getLauncherDir() { return this.launcherDir; }
	public Saver   getSaver()       { return this.saver;       }
	public Auth    getAuth()        { return this.auth;        }

	public static Path createGameDir(String serverName, boolean inLinuxLocalShare)
	{
		final String os = Objects.requireNonNull(System.getProperty("os.name")).toLowerCase();

		if (os.contains("win"))
		{
			return Paths.get(System.getenv("APPDATA"), '.' + serverName);
		}
		else if (os.contains("mac"))
		{
			return Paths.get(
					System.getProperty("user.home"),
					"Library",
					"Application Support",
					serverName
			);
		}
		else
		{
			if (inLinuxLocalShare && os.contains("linux"))
			{
				return Paths.get(
						System.getProperty("user.home"),
						".local",
						"share",
						serverName
				);
			}
			else
			{
				return Paths.get(
						System.getProperty("user.home"),
						'.' + serverName
				);
			}
		}
	}

	public void switchLogin()
	{
		this.launcher.switchLogin();
	}
	public void hide() { this.launcher.setVisible( !this.launcher.isVisible() ); }
	public void refreshHeadImg() { this.launcher.refreshHeadImg(); }
}
