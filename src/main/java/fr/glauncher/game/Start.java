package fr.glauncher.game;

import fr.flowarg.openlauncherlib.NoFramework;
import fr.glauncher.Controller;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import org.jetbrains.annotations.NotNull;

public class Start
{
	public static void Start(Controller ctrl, @NotNull String name)
	{
		try
		{
			NoFramework noFramework = new NoFramework(
					ctrl.getLauncherDir(),
					ctrl.getAuth().getAuthInfos(),
					GameFolder.FLOW_UPDATER
			);

			String ramString = ctrl.getSaver().get("ram");

			int    ramValue  = Integer.parseInt(ramString == null ? "4096" : ramString);

			noFramework.getAdditionalVmArgs().add( String.format("-Xmx%sM", ramValue) );

			Process p = noFramework.launch(
					name,
					Controller.pack.getVersion().split("-")[1],
					NoFramework.ModLoader.FORGE
			);

			p.waitFor();

			ctrl.hide();
		}
		catch (Exception e)
		{
			ctrl.getLogger().printStackTrace(e);
		}
	}
}
