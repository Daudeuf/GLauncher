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

			int ramValue = Integer.parseInt(ctrl.getSaver().get("ram"));

			noFramework.getAdditionalVmArgs().add( String.format("-Xmx%sM", ramValue) );

			ctrl.getLogger().info(String.format("-Xmx%sM", ramValue));

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
