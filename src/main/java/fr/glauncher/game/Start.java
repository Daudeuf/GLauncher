package fr.glauncher.game;

import fr.flowarg.openlauncherlib.NoFramework;
import fr.glauncher.Controller;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import org.jetbrains.annotations.NotNull;

public class Start
{
	private Start() {}

	public static void Start(Controller ctrl, @NotNull String name)
	{
		try
		{
			NoFramework noFramework = new NoFramework(
					ctrl.getLauncherDir(),
					ctrl.getAuth().getAuthInfos(),
					GameFolder.FLOW_UPDATER
			);

			noFramework.getAdditionalVmArgs().add(Controller.RAM);

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
