package fr.glauncher;

import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.glauncher.game.Auth;
import fr.glauncher.game.PackInfos;
import fr.glauncher.ui.Launcher;
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
			pack = PackInfos.create("https://raw.githubusercontent.com/Daudeuf/GLauncher/master/modpack_info.json");
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final String  NAME  = "glauncher";
	private static final String  LABEL = "GLauncher";

	private ILogger  logger;
	private Path     launcherDir;
	private Launcher launcher;
	private Saver    saver;
	private Auth     auth;

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
